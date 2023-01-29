package com.example.commander.receiver;

import com.example.commander.CommandSerializer;
import com.example.commander.RawCommand;
import com.example.commander.domain.AsyncResult;
import com.example.commander.domain.JobId;
import com.example.commander.domain.PodId;
import com.example.commander.result.HandlerException;
import com.example.commander.result.RawResult;
import com.example.commander.sender.k8s.HostnameSupplier;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class CommandController {

    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);
    private final List<CommandListener> listeners;
    private final CommandSerializer commandSerializer;
    private final ExecutorService executorService;
    private final RestOperations client;

    public CommandController(List<CommandListener> listeners,
                             CommandSerializer commandSerializer,
                             ExecutorService executorService,
                             RestOperations client) {
        
        this.listeners = listeners;
        this.commandSerializer = commandSerializer;
        this.executorService = executorService;
        this.client = client;
    }

    @PostMapping("runAsync")
    ResponseEntity<Void> runAsync(@RequestBody byte[] cmd,
                                  @RequestHeader("X-JobId") String jobId,
                                  @RequestHeader("X-PodId") String podId,
                                  @RequestHeader("X-Callback-Pod-Fqdn") String target) {


        CompletableFuture.runAsync(() -> {
            RawCommand command = commandSerializer.deserialize(cmd, new TypeReference<>() {});

            var futures = listeners
                .stream()
                .filter(listener -> listener.supports(command.command()))
                .map(listener -> listener.onCommand(command.command())).toList();

            var results = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(__ -> futures.stream().map(CompletableFuture::join).filter(Objects::nonNull).toList())
                 .join();

            var as = commandSerializer.serialize(new AsyncResult(new JobId(jobId), new PodId(podId), results), new TypeReference<AsyncResult>() {
            });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            HttpEntity<byte[]> request = new HttpEntity<>(as, headers);

            try {

                ResponseEntity<byte[]> commandResult =
                        client.exchange("http://" + target + "/asyncCallback",
                                HttpMethod.POST,
                                request,
                                byte[].class);

            } catch (Exception e) {
                logger.error("Failed to callback",e);
            }


        }, executorService);

        return ResponseEntity.ok().build();
    }

    @PostMapping("run")
    ResponseEntity<byte[]> run(@RequestBody byte[] cmd) {

        try {

            RawCommand command = commandSerializer.deserialize(cmd, new TypeReference<>() {});


            List<RawResult> commandsResult = listeners.stream()
                    .filter(listener -> listener.supports(command.command()))
                    .map(listener -> Pair.of(listener, listener.onCommand(command.command())))
                    .map(envelope -> {

                        try {
                          return envelope.getValue().join();
                        } catch (Exception e){
                            return new HandlerException(envelope.getKey().id(), e);
                        }
                    })
                    .toList();

            return ok(commandSerializer.serialize(commandsResult, new TypeReference<List<RawResult>>() {}));
        } catch (Exception e){
            logger.error("", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

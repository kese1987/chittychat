package com.example.commander.sender;

import com.example.commander.*;
import com.example.commander.raw.result.PodException;
import com.example.commander.raw.result.PodResults;
import com.example.commander.raw.result.RawPodResults;
import com.example.commander.raw.result.RawResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;


public class DefaultSender implements Sender {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSender.class);

    private final K8s k8s;
    private final ExecutorService executorService;
    private final RestOperations client;
    private final CommandSerializer serde;


    public DefaultSender(K8s k8s,
                         ExecutorService executorService,
                         RestOperations client,
                         CommandSerializer serde) {

        this.k8s = k8s;
        this.executorService = executorService;
        this.client = client;

        this.serde = serde;
    }

    @Override
    public List<RawPodResults> run(byte[] command){

        byte[] serializedCommand = serde.serialize(new DefaultRawCommand(command, "signature".getBytes(StandardCharsets.UTF_8)));

        return k8s.pods().stream()
                .map(pod -> new CommandEnvelope(pod, doRun(serializedCommand, pod)))
                .map(this::maybeValue).toList();
    }

    private RawPodResults maybeValue(CommandEnvelope envelope)  {
        try {
            return envelope.future.get(60, TimeUnit.SECONDS);
        } catch (Exception e){
            return new PodException(envelope.pod, e);
        }
    }

    @Override
    public CompletableFuture<List<RawPodResults>> runAsync(byte[] command) {
        return null;
    }


    private CompletableFuture<RawPodResults> doRun(byte[] command, Pod pod) {
        return CompletableFuture.supplyAsync(() -> {

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                HttpEntity<byte[]> request = new HttpEntity<>(command, headers);

                try {

                    ResponseEntity<byte[]> commandResult =
                            client.exchange("http://" + pod.getAddress() + "/run",
                                    HttpMethod.POST,
                                    request,
                                    byte[].class);

                    if (commandResult.getStatusCode().is2xxSuccessful()){
                        var results = serde.<List<RawResult>>deserialize(commandResult.getBody());
                        return new PodResults(pod, results);
                    }
                    throw new CommandException(pod, commandResult.getStatusCode());
                } catch (HttpStatusCodeException e) {
                    throw new CommandException(pod, e);
                }

        }, executorService);
    }

    private record CommandEnvelope(Pod pod, CompletableFuture<RawPodResults> future){}

}

package com.example.commander.sender.command;

import com.example.commander.CommandException;
import com.example.commander.CommandSerializer;
import com.example.commander.Pod;
import com.example.commander.RawCommand;
import com.example.commander.result.PodException;
import com.example.commander.result.PodResults;
import com.example.commander.result.RawPodResults;
import com.example.commander.result.RawResult;
import com.example.commander.sender.k8s.K8s;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class DefaultSyncCommandManager implements SyncCommandManager{

    private final K8s kube;
    private final ExecutorService executorService;
    private final RestOperations client;
    private final CommandSerializer serde;


    public DefaultSyncCommandManager(K8s kube,
                                     ExecutorService executorService,
                                     RestOperations client,
                                     CommandSerializer serde) {
        this.kube = kube;
        this.executorService = executorService;
        this.client = client;
        this.serde = serde;
    }

    @Override
    public List<RawPodResults> run(byte[] command) {
        try {

            var runningCommands = runCommands(command);


            CompletableFuture
                    .allOf(runningCommands.stream().map(RunningCommand::future).toList().toArray(new CompletableFuture[0]))
                    .get(60, TimeUnit.SECONDS);

            return runningCommands.stream().map(this::maybeValue).toList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<RawPodResults> doRun(Pod pod, byte[] command) {
        return CompletableFuture.supplyAsync(() -> {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            HttpEntity<byte[]> request = new HttpEntity<>(command, headers);

            try {

                ResponseEntity<byte[]> commandResult =
                        client.exchange("http://" + pod.fqdn() + "/run",
                                HttpMethod.POST,
                                request,
                                byte[].class);

                if (commandResult.getStatusCode().is2xxSuccessful()){
                    var results = serde.deserialize(commandResult.getBody(), new TypeReference<List<RawResult>>() {});
                    return new PodResults(pod, results);
                }
                throw new CommandException(pod, commandResult.getStatusCode());
            } catch (HttpStatusCodeException e) {
                throw new CommandException(pod, e);
            }

        }, executorService);
    }

    private List<RunningCommand> runCommands(byte[] rawCommand) {
        return kube.pods().stream()
                .map(pod -> new RunningCommand(pod, this.doRun(pod, rawCommand))).toList();
    }



    private RawPodResults maybeValue(RunningCommand envelope)  {
        try {
            return envelope.future.get(60, TimeUnit.SECONDS);
        } catch (Exception e){
            return new PodException(envelope.pod, e);
        }
    }

    private record RunningCommand(Pod pod, CompletableFuture<RawPodResults> future){}
}

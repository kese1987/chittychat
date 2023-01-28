package com.example.commander.sender;

import com.example.commander.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestOperations;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.util.Collections.emptyList;


public class DefaultSender implements Sender {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSender.class);

    private final K8s k8s;
    private ExecutorService executorService;
    private RestOperations client;
    private ObjectMapper mapper;

    public DefaultSender(K8s k8s,
                         ExecutorService executorService,
                         RestOperations client,
                         ObjectMapper mapper) {

        this.k8s = k8s;
        this.executorService = executorService;
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public CompletableFuture<List<PodResult>> send(byte[] command){

        try {
            byte[] serializedCommand = trySerialize(new DefaultRawCommand(command, "signature".getBytes(StandardCharsets.UTF_8)));

            List<CompletableFuture<PodResult>> futurePodResults =
                    k8s.pods().stream().map(pod ->

                            CompletableFuture.supplyAsync(() -> {

                                try {

                                    HttpHeaders headers = new HttpHeaders();
                                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                                    HttpEntity<byte[]> request = new HttpEntity<>(serializedCommand, headers);

                                    ResponseEntity<byte[]> commandResult =
                                            client.exchange("http://" + pod.getAddress() + "/runCommand",
                                                    HttpMethod.POST,
                                                    request,
                                                    byte[].class);


                                    List<DefaultRawResult> results = emptyList();
                                    if (commandResult.hasBody()) {
                                        results = mapper.readValue(commandResult.getBody(), new TypeReference<List<DefaultRawResult>>() {
                                        });
                                    }

                                    if (commandResult.getStatusCode().is2xxSuccessful()) {

                                        return (PodResult) new DefaultPodResult(pod, results);
                                    } else {
                                        throw new CommandException(pod, results, commandResult.getStatusCode());
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }, executorService)
                    ).toList();

            return CompletableFuture.allOf(futurePodResults.toArray(new CompletableFuture[0]))
                    .thenApply(__ -> futurePodResults
                            .stream()
                            .map(CompletableFuture::join)
                            .filter(Objects::nonNull).toList());


        } catch (Exception e){
            CompletableFuture<List<PodResult>> failure = new CompletableFuture<>();
            failure.completeExceptionally(e);
            return failure;
        }


    }

    private byte[] trySerialize(RawCommand command) {
        try {
            return mapper.writeValueAsBytes(command);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

package com.example.commander.sender.command;

import com.example.commander.CommandException;
import com.example.commander.Pod;
import com.example.commander.domain.AsyncResult;
import com.example.commander.domain.JobId;
import com.example.commander.domain.PodId;
import com.example.commander.result.*;
import com.example.commander.sender.k8s.K8s;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DefaultAsyncCommandManager implements CallableAsyncCommandManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAsyncCommandManager.class);
    private static final  Map<JobId, Job> issuedJobs = Maps.newConcurrentMap();

    private final ExecutorService executorService;
    private final RestOperations client;
    private final AtomicSafeInitializer<ScheduledFuture<?>> reconcilingJob;
    private final K8s kube;

    public DefaultAsyncCommandManager(ExecutorService executorService,
                                      RestOperations client,
                                      ScheduledExecutorService scheduledExecService,
                                      K8s kube) {
        this.executorService = executorService;
        this.client = client;
        this.reconcilingJob = new AtomicSafeInitializer<>() {
            @Override
            protected ScheduledFuture<?> initialize() throws ConcurrentException {
                return scheduledExecService.scheduleAtFixedRate(DefaultAsyncCommandManager.this::updateIssuedJobs, 0, 10, TimeUnit.SECONDS);
            }
        };
        this.kube = kube;
    }

    private void updateIssuedJobs() {
        Instant now = Instant.now();

        issuedJobs.forEach((id, job) -> {
            long pendingJobs = job.jobs.entrySet().stream().filter(command -> {
                if (command.getValue() instanceof Pending p) {
                    return now.toEpochMilli() - p.issuedAt.toEpochMilli() < 300000;
                }
                return false;
            }).count();

            logger.debug("pending job " + pendingJobs);
            if (pendingJobs == 0) {
                logger.debug("job ready");
                var results = job.jobs.entrySet().stream().map(it -> {
                    if(it.getValue() instanceof Pending){
                        return (RawPodResults)new PodException(job.pods.get(it.getKey()), new RuntimeException("Max execution time expired!"));
                    } else if(it.getValue() instanceof Completed c) {
                        return new PodResults(job.pods.get(it.getKey()), c.result);
                    }

                    return null;
                }).filter(Objects::nonNull).toList();

                logger.debug("future resolved");
                job.future.complete(results);
                issuedJobs.remove(id);
            }
        });
    }

    @Override
    public CompletableFuture<List<RawPodResults>> runAsync( byte[] command) {

        var pods = kube.pods();

        startReconcilingJob();

        CompletableFuture<List<RawPodResults>> result = new CompletableFuture<>();

        String jobId = UUID.randomUUID().toString();
        var job =
                pods
                .stream()
                .map( pod -> Pair.of(new PodId(pod.hostname()), (CommandStatus)new Pending(Instant.now())))
                .collect(Collectors.toConcurrentMap(Pair::getKey, Pair::getValue));


        issuedJobs.put(new JobId(jobId), new Job(result, pods.stream().collect(Collectors.toMap(it -> new PodId(it.hostname()), it ->it)), job));

        pods.forEach(pod -> doRunAsync(pod, command, jobId));

        return result;
    }

    private void startReconcilingJob() {
        try {
            reconcilingJob.get();
        } catch (ConcurrentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void callback(AsyncResult result) {
        issuedJobs.computeIfPresent(result.jobId(), (jid, job) -> {
            job.jobs.computeIfPresent(result.podId(), (pid, status) -> {
                logger.debug("saved job result");
                return new Completed(result.results());
            });
            return job;
        });
    }

    private void doRunAsync(Pod pod, byte[] command, String jobId) {
        CompletableFuture.runAsync(() -> {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("X-JobId", jobId);
            headers.set("X-Callback-Pod-Fqdn", kube.runningPod().fqdn());
            headers.set("X-PodId", pod.hostname());
            HttpEntity<byte[]> request = new HttpEntity<>(command, headers);

            try {

                ResponseEntity<Void> commandResult =
                        client.exchange("http://" + pod.fqdn() + "/runAsync",
                                HttpMethod.POST,
                                request,
                                Void.class);



                if (commandResult.getStatusCode().is2xxSuccessful()){
                    return;
                }
                throw new CommandException(pod, commandResult.getStatusCode());
            } catch (HttpStatusCodeException e) {
                throw new CommandException(pod, e);
            }

        }, executorService);


    }

    private sealed interface CommandStatus permits Completed, Pending {}
    private record Completed(List<RawResult> result) implements CommandStatus {}
    private record Pending(Instant issuedAt)  implements CommandStatus {}



    private record Job(CompletableFuture<List<RawPodResults>> future, Map<PodId, Pod> pods, Map<PodId, CommandStatus> jobs){}
}

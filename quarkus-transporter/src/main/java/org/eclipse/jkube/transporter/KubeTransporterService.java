package org.eclipse.jkube.transporter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.runtime.ShutdownEvent;
import org.eclipse.jkube.kit.common.KitLogger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.jkube.kit.remotedev.RemoteDevelopmentConfig;
import org.eclipse.jkube.kit.remotedev.RemoteDevelopmentService;
import io.fabric8.kubernetes.client.KubernetesClient;

@ApplicationScoped
public class KubeTransporterService {

    @Inject
    KubernetesClient kubernetesClient;

    @Inject
    KitLogger logger;

    @Inject
    Config configService;

    RemoteDevelopmentService remoteDevelopmentService;

    void cleaningRemoteDevInstance(@Observes ShutdownEvent ev) {
        if (remoteDevelopmentService != null) {
            logger.info("The Kube Transporter is stopping...");
            remoteDevelopmentService.stop();
        }
    }

    public void start() {
        RemoteDevelopmentConfig config = configService.loadConfiguration();
        start(config, true);
    }

    public void start(RemoteDevelopmentConfig config, boolean sync) {
        if (remoteDevelopmentService != null) {
            remoteDevelopmentService.stop();
        }
        remoteDevelopmentService = new RemoteDevelopmentService(logger, kubernetesClient,
                config);

        try {
            CompletableFuture<Void> start = remoteDevelopmentService.start();
            if (sync) {
                start.get();
            }

        } catch (InterruptedException | ExecutionException e) {
            logger.error("An error occured while running the Kube transporter", e);
            Thread.currentThread().interrupt();
        }

    }

    public void stop() {
        if (remoteDevelopmentService != null) {
            remoteDevelopmentService.stop();
        }
    }
}
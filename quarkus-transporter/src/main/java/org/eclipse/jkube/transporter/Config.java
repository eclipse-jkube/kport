package org.eclipse.jkube.transporter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.jkube.kit.common.KitLogger;
import org.eclipse.jkube.kit.remotedev.RemoteDevelopmentConfig;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.fabric8.kubernetes.client.KubernetesClient;

@ApplicationScoped
public class Config {

    @Inject
    KubernetesClient kubeClient;

    @Inject
    KitLogger logger;

    public void persistToProject(RemoteDevelopmentConfig config, String projectRootFolder) {

        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        try {
            Path configFile = Paths.get(projectRootFolder).resolve(".transporter.yaml");
            logger.info("💾 Saving transporter config to file " + configFile);
            om.writeValue(configFile.toFile(),
                    config);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void persistToLocalKubeHome(RemoteDevelopmentConfig config) {
        String cluster = kubeClient.getConfiguration().getCurrentContext().getContext().getCluster();
        String namespace = kubeClient.getConfiguration().getCurrentContext().getContext().getNamespace();

        Map<String, RemoteDevelopmentConfig> namespaceConfigs = new HashMap<>();
        namespaceConfigs.put(namespace, config);

        Map<String, Map<String, RemoteDevelopmentConfig>> clusterConfigs = loadClusterBasedConfig();
        namespaceConfigs = clusterConfigs.get(cluster);
        if (namespaceConfigs == null) {
            namespaceConfigs = new HashMap<>();
            clusterConfigs.put(cluster, namespaceConfigs);
        }
        namespaceConfigs.put(namespace, config);

        String configFile = getConfigFileLocation();

        logger.info("💾 Saving transporter config to file " + configFile);

        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        try {
            om.writeValue(new File(configFile),
                    clusterConfigs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String getConfigFileLocation() {
        return kubeClient.getConfiguration().getFile().getParent() + File.separator
                + "kubectl-transporter.yaml";
    }

    public Map<String, Map<String, RemoteDevelopmentConfig>> loadClusterBasedConfig() {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        String configFile = getConfigFileLocation();

        logger.info("💾 Loading transporter config from file " + configFile);
        try {
            return om.readValue(new File(configFile),
                    new TypeReference<Map<String, Map<String, RemoteDevelopmentConfig>>>() {

                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public RemoteDevelopmentConfig loadConfiguration() {

        RemoteDevelopmentConfig config = loadFromLocalProject();
        if (config != null) {
            return config;
        }

        String cluster = kubeClient.getConfiguration().getCurrentContext().getContext().getCluster();
        String namespace = kubeClient.getConfiguration().getCurrentContext().getContext().getNamespace();

        return loadClusterBasedConfig().get(cluster).get(namespace);

    }

    private RemoteDevelopmentConfig loadFromLocalProject() {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        while (true) {
            Path configFile = currentDir.resolve(".transporter.yaml");
            logger.info("💾 Looking for transporter config from file " + configFile);
            if (Files.exists(configFile)) {
                logger.info("💾 Loading transporter config from file " + configFile);
                try {
                    ObjectMapper om = new ObjectMapper(new YAMLFactory());
                    return om.readValue(configFile.toFile(),
                            RemoteDevelopmentConfig.class);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (currentDir.equals(currentDir.getRoot())) {
                return null;
            }
            currentDir = currentDir.getParent();
        }
    }

}

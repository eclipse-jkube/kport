package io.quarkiverse.sshd.deployment.bouncycastle;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.jandex.IndexView;

import io.quarkiverse.sshd.runtime.BouncyCastleRecorder;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeReinitializedClassBuildItem;
import io.quarkus.security.deployment.BouncyCastleProviderBuildItem;

public class BouncyCastleSupportProcessor {

    @BuildStep
    void produceBouncyCastleProvider(BuildProducer<BouncyCastleProviderBuildItem> bouncyCastleProvider) {
        bouncyCastleProvider.produce(new BouncyCastleProviderBuildItem());
    }

    @BuildStep
    ReflectiveClassBuildItem registerForReflection(CombinedIndexBuildItem combinedIndex) {
        IndexView index = combinedIndex.getIndex();

        String[] dtos = index.getKnownClasses().stream()
                .map(ci -> ci.name().toString())
                .filter(n -> n.startsWith("org.bouncycastle.jcajce.provider.digest.") ||
                        n.startsWith("org.bouncycastle.jcajce.provider.symmetric.") ||
                        n.startsWith("org.bouncycastle.jcajce.provider.asymmetric.") ||
                        n.startsWith("org.bouncycastle.jcajce.provider.keystore."))
                .toArray(String[]::new);

        return ReflectiveClassBuildItem.builder(dtos).build();
    }

    @BuildStep
    IndexDependencyBuildItem registerBCDependencyForIndex() {
        return new IndexDependencyBuildItem("org.bouncycastle", "bcprov-jdk18on");
    }

    @BuildStep
    void secureRandomConfiguration(BuildProducer<RuntimeReinitializedClassBuildItem> reinitialized) {
        reinitialized.produce(new RuntimeReinitializedClassBuildItem("java.security.SecureRandom"));
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    public void registerBouncyCastleProvider(List<CipherTransformationBuildItem> cipherTransformations,
            BouncyCastleRecorder recorder,
            ShutdownContextBuildItem shutdownContextBuildItem) {
        List<String> allCipherTransformations = cipherTransformations.stream()
                .flatMap(c -> c.getCipherTransformations().stream()).collect(Collectors.toList());
        recorder.registerBouncyCastleProvider(allCipherTransformations, shutdownContextBuildItem);
    }
}

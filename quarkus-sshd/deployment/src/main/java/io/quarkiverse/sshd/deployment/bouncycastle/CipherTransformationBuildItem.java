package io.quarkiverse.sshd.deployment.bouncycastle;

import java.util.Collections;
import java.util.List;

import io.quarkus.builder.item.MultiBuildItem;

/**
 * A {@link MultiBuildItem} holding cipher transformations to be explicitly
 * registered as security services. Extensions should provide all cipher transformations
 * that are reachable at runtime. Those cipher transformations will be explicitly instantiated
 * at bootstrap so that graal can proceed with security services automatic registration.
 */
public final class CipherTransformationBuildItem extends MultiBuildItem {

    private final List<String> cipherTransformations;

    public CipherTransformationBuildItem(List<String> cipherTransformations) {
        this.cipherTransformations = cipherTransformations;
    }

    public List<String> getCipherTransformations() {
        return Collections.unmodifiableList(cipherTransformations);
    };

}
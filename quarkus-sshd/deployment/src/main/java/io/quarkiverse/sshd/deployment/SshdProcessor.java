package io.quarkiverse.sshd.deployment;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.Mac;

import org.apache.sshd.common.channel.ChannelListener;
import org.apache.sshd.common.forward.PortForwardingEventListener;
import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory;
import org.apache.sshd.common.session.SessionListener;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

class SshdProcessor {

    private static final String FEATURE = "sshd";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void registerForReflection(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        reflectiveClasses.produce(
                ReflectiveClassBuildItem.builder(KeyPairGenerator.class,
                        KeyAgreement.class,
                        KeyFactory.class,
                        Signature.class,
                        Mac.class).methods(true).build());
        reflectiveClasses.produce(
                ReflectiveClassBuildItem.builder(Nio2ServiceFactoryFactory.class).build());
    }

    @BuildStep
    NativeImageResourceBuildItem nativeImageResourceBuildItem() {
        return new NativeImageResourceBuildItem("META-INF/services/org.apache.sshd.common.io.IoServiceFactoryFactory");
    }

    @BuildStep
    void sessionProxy(BuildProducer<NativeImageProxyDefinitionBuildItem> proxiesProducer) {
        for (String s : Arrays.asList(
                SessionListener.class.getName(),
                ChannelListener.class.getName(),
                PortForwardingEventListener.class.getName())) {
            proxiesProducer.produce(new NativeImageProxyDefinitionBuildItem(s));
        }
    }

}

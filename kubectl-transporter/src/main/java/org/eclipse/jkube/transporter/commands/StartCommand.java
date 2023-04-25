package org.eclipse.jkube.transporter.commands;
import javax.inject.Inject;

import org.eclipse.jkube.transporter.KubeTransporterService;

import picocli.CommandLine;

@CommandLine.Command(name = "start", description = "Start transporter")
public class StartCommand implements Runnable {
    @Inject
    KubeTransporterService kubeTransporter;


    @Override
    public void run() {
        kubeTransporter.start();
    }
}

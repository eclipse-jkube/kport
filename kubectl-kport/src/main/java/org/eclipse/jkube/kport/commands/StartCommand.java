package org.eclipse.jkube.kport.commands;
import javax.inject.Inject;

import org.eclipse.jkube.kport.KportService;

import picocli.CommandLine;

@CommandLine.Command(name = "start", description = "Start kport")
public class StartCommand implements Runnable {
    @Inject
    KportService kubekport;


    @Override
    public void run() {
        kubekport.start();
    }
}

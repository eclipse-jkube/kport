package org.eclipse.jkube.transporter.commands;

import picocli.CommandLine;


import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.runtime.Quarkus;
@TopCommand
@CommandLine.Command(subcommands = {InitCommand.class, StartCommand.class})
public class StartTopCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("run init or start sub commands");

    }
}

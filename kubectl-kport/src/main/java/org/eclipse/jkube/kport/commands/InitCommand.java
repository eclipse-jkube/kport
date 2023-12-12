package org.eclipse.jkube.kport.commands;

import picocli.CommandLine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.eclipse.jkube.kit.remotedev.LocalService;
import org.eclipse.jkube.kit.remotedev.RemoteDevelopmentConfig;
import org.eclipse.jkube.kit.remotedev.RemoteService;
import org.eclipse.jkube.kport.Config;
import org.eclipse.jkube.kport.KportService;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.InputResult;
import de.codeshelf.consoleui.prompt.ListResult;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.KubernetesClient;

enum NextAction {

    ADD_LOCAL_SERVICE(
            "📥 Expose Local Service to the remote cluster",
            "📥 Remote traffic to @|green <remoteService>|@ forwarded to localhost:@|green <localAppPort>|@"),
    ADD_REMOTE_SERVICE(
            "📡 Expose a Remote Service locally",
            "📡 Local traffic to localhost:@|green <localPort>|@ forwarded to @|green <remoteService>|@:@|green <remoteServicePort>|@"),
    SAVE_LOCAL("🏁 Save in ~/.kube/kubectl-kport.yaml"),
    SAVE_PROJECT("🏁 Save in [currentDir]/.kport.yaml"),
    QUIT_WO_SAVING("🚫 Quit without saving");

    private String label;

    private String description;

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    private NextAction(String label, String description) {
        this.label = label;
        this.description = description;
    }

    NextAction(String label) {
        this.label = label;
    }
}

@CommandLine.Command(name = "init", description = "Initialize a default configuration")
public class InitCommand implements Runnable {
    @Inject
    KportService kubekport;

    @Inject
    KubernetesClient kubeclient;

    @Inject
    Config configService;

    @Override
    public void run() {
        printText("🌀 Configuring the Kube kport for\n"
                + "\t- cluster: @|green " + kubeclient.getConfiguration().getCurrentContext().getContext().getCluster()
                + "|@\n"
                + "\t- namespace: @|green "
                + kubeclient.getConfiguration().getCurrentContext().getContext().getNamespace() + "|@\n");

        if (kubeclient.services().list().getItems().isEmpty()) {
            text("🚫 No remote service detected.");
            return;
        }

        List<RemoteService> remoteServices = new ArrayList<>();
        List<LocalService> localServices = new ArrayList<>();

        boolean done = false;
        while (!done) {
            System.out.println("");
            NextAction nextAction = promptNextAction();
            System.out.println("");
            switch (nextAction) {
                case ADD_REMOTE_SERVICE:
                    remoteServices.add(promptExposeRemoteService());
                    break;
                case ADD_LOCAL_SERVICE:
                    localServices.add(promptNewLocalService());
                    break;
                case SAVE_PROJECT:
                    configService.persistToProject(
                            new RemoteDevelopmentConfig(0, remoteServices, localServices),
                            System.getProperty("user.dir"));
                    done = true;
                    break;
                case SAVE_LOCAL:
                    configService.persistToLocalKubeHome(
                            new RemoteDevelopmentConfig(0, remoteServices, localServices));
                    done = true;
                    break;
                case QUIT_WO_SAVING:
                    done = true;
                    break;
            }
        }

    }

    public static void printText(String txt) {
        System.out.println(text(txt));
    }

    public static String text(String txt) {
        return CommandLine.Help.Ansi.AUTO.text(txt).toString();
    }

    private NextAction promptNextAction() {

        try {
            ConsolePrompt prompt = new ConsolePrompt();
            PromptBuilder promptBuilder = prompt.getPromptBuilder();

            ListPromptBuilder nextActionChooserBuilder = promptBuilder.createListPrompt()
                    .name("nextAction")
                    .message(text("Choose an action:"));
            List.of(NextAction.values()).stream().forEach((nextAction) -> nextActionChooserBuilder.newItem()
                    .name(nextAction.name()).text(text(nextAction.getLabel())).add());
            nextActionChooserBuilder.addPrompt();
            HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());

            return NextAction.valueOf(((ListResult) result.get("nextAction")).getSelectedId());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    RemoteService promptExposeRemoteService() {
        try {

            printText(NextAction.ADD_REMOTE_SERVICE.getDescription());

            Stream<String> serviceList = kubeclient.services().list().getItems().stream()
                    .map((s) -> s.getMetadata().getName());

            String remoteService = promptList("Select @|green <remoteService>|@",
                    serviceList);

            List<ServicePort> ports = kubeclient.services().withName(remoteService).get().getSpec().getPorts();

            String remoteServicePort = promptList("Select @|green <remoteServicePort>|@", ports.stream()
                    .map((p) -> Integer.toString(p.getPort())));

            String localPort = promptWithSuggestion(
                    "Select @|green <localPort>|@", remoteServicePort);
            printText("🎉 Configured local traffic to localhost:@|green " + localPort + "|@ to be forwarded to @|green "
                    + remoteService + "|@:@|green "
                    + remoteServicePort + "|@");

            return new RemoteService(remoteService, Integer.valueOf(remoteServicePort), Integer.valueOf(localPort));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    LocalService promptNewLocalService() {

        try {

            Stream<String> serviceList = kubeclient.services().list().getItems().stream()
                    .map((s) -> s.getMetadata().getName());
            printText(NextAction.ADD_LOCAL_SERVICE.getDescription());
            String remoteService = promptList("Select @|green <remoteService>|@",
                    serviceList);

            Integer servicePort = kubeclient.services().withName(remoteService).get().getSpec().getPorts().get(0)
                    .getPort();
            String localAppPort = promptWithSuggestion(
                    "Select @|green <localAppPort>|@", Integer.toString(servicePort));

            printText("🎉 Configured remote traffic to @|green " + remoteService
                    + "|@ to be forwarded to localhost:@|green " + localAppPort + "|@");

            return new LocalService(remoteService, null, Integer.valueOf(localAppPort));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String promptWithSuggestion(String message, String defaultValue) throws IOException {
        ConsolePrompt prompt = new ConsolePrompt();
        PromptBuilder promptBuilder = prompt.getPromptBuilder();
        promptBuilder.createInputPrompt().name("name").message(text(message)).defaultValue(defaultValue)
                .addPrompt();
        HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
        return ((InputResult) result.get("name")).getInput();
    }

    private String promptList(String message, Stream<String> optionList) throws IOException {
        ConsolePrompt prompt = new ConsolePrompt();
        PromptBuilder promptBuilder = prompt.getPromptBuilder();

        ListPromptBuilder chooserBuilder = promptBuilder.createListPrompt()
                .name("idList")
                .message(text(message));
        optionList.forEach(
                (s) -> chooserBuilder.newItem().text(s).add());
        chooserBuilder.addPrompt();
        HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
        return ((ListResult) result.get("idList")).getSelectedId();
    }

}

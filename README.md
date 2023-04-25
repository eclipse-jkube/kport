# JKube-transporter
Have you heard about the Star-Trek transporter? It is a machine in the USS Enterprise spaceship that can teleport people to a remote planet.

This is somehow what you can achieve with JKube remote-dev commands: thanks to port-forwards and a proxy, the developer will feel like being able to teleport its Java app in dev mode into a remote Kubernetes cluster, replacing the existing remote app.

JKube-transporter is a **PoC** set of projects that leverages the JKube remote-dev feature and provides the same experience but for any kind of application to be deployed in Kubernetes (Java or not).

Behind the scene, these projects use Quarkus, the Supersonic Subatomic Java Framework.

- `quarkus-transporter`: quarkusified version of JKube remote-dev library
- `transporter-ide-server`: exposing in http/rest the remote-dev features to be used by non Java IDEs like VSCode.
- `kubectl-transporter`: a CLI tool to start the remote-dev, could be used as a kubectl plugin.

There is an additional tool: the [vscode-kube-transporter](https://github.com/sunix/vscode-kube-transporter) extension for VSCode that uses `transporter-ide-server`.

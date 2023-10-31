# JKube-kport

Have you ever wished for a Star-Trek-like teleportation device? The one on the USS Enterprise that can instantly transport people to a remote planet? Well, [JKube remote-dev](https://blog.marcnuri.com/eclipse-jkube-1-10#jkube-image-remote-dev) comes close to making that a reality for developers. By combining port-forwards and a proxy, developers can experience the sensation of teleporting their local Java applications into a remote Kubernetes cluster, seamlessly replacing the existing remote app.

JKube-kport, formerly known as JKube-transporter, is a Proof of Concept (PoC) project collection that builds upon the [JKube remote-dev](https://blog.marcnuri.com/eclipse-jkube-1-10#jkube-image-remote-dev) feature. It offers the same teleportation-like experience, but for any type of application you wish to deploy in Kubernetes, be it Java or not. All of this magic is powered by Quarkus, the Supersonic Subatomic Java Framework.

Here's a breakdown of the projects:

- `quarkus-kport`: A Quarkus-enabled version of the JKube remote-dev library.
- `kport-ide-server`: This component exposes the remote-dev features via HTTP/REST, making them accessible to non-Java IDEs like VSCode.
- [kubectl-kport](./kubectl-kport/README.md): A powerful CLI tool for initiating remote development. It can also be used as a kubectl plugin.

In addition to these, there's a nifty tool called the [vscode-kport](https://github.com/sunix/vscode-kport) extension designed specifically for VSCode. It interfaces with `kport-ide-server` to further enhance the development experience.

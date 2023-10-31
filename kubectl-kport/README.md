# kubectl-kport

`kubectl-kport` is a CLI tool designed to facilitate remote development. It can be used as a kubectl plugin. This tool redirects all traffic intended for a Kubernetes service to your local application. Additionally, it enables traffic redirection from your local application to services in the remote cluster.

## Installation
To get started, download the `kubectl-kport-YOUROS` binary for your operating system from [this link](https://github.com/jkubeio/kport/releases). Rename the binary to `kubectl-kport` and place it in a directory included in your system's PATH.

Once done, you should be able to run the command:
```
$ kubectl kport
```

## Initializing kubectl kport
To generate the `.kport.yaml` configuration file, use the interactive command:
```
$ kubectl kport init
```
This configuration file will be utilized by the subsequent `$ kubectl kport start` command.

## Starting kubectl kport
To initiate remote development mode, run:
```
$ kubectl kport start
```
This command will use the configuration stored in the `.kport.yaml` file in the current directory.

## Demo
You can follow the steps outlined in the [JKube remote-dev Northwind demo](https://github.com/redhat-developer-demos/northwind-traders#remote-dev-demo-walk-through). Make sure to prepare the demo as instructed.

In the `northwind` folder, use the command:
```
$ kubectl kport init
```
Provide the following details:
- A local service named `northwind` with the default port.
- The remote service `northwind-db` with the default ports.
- The remote service `rabbitmq` with the custom localPort `15672`.

Save the file in the current folder.

Continue with the demo, replacing `mvn oc:remote-dev` with `kubectl kport start`. This will ensure that traffic redirection is in effect.

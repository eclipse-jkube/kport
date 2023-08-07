# kubectl-kport
`kubectl-kport` is CLI tool to start the remote-dev. It could be used as a kubectl plugin.

## Installation
Download the `kubectl-kport-YOUROS` binary for your OS from https://github.com/jkubeio/kport/releases. Rename the binary `kubectl-kport` and add it in a directory of your PATH.

The command
```
$ kubectl kport
```
should then be available.

## kubectl kport init
```
$ kubectl kport init
```
is an interactive command that will help you to generate the `.kport.yaml` configuration file. The configuration will be used for the next command `$ kubectl kport start`.

## kubectl kport start
```
$ kubectl kport start
```
starts the remote-dev mode based on the configuration stored in the`.kport.yaml` file in the current folder.

## Demo
It is possible to reuse the [JKube remote-dev Northwind demo](https://github.com/redhat-developer-demos/northwind-traders#remote-dev-demo-walk-through).
Prepare the demo as indicated.
In the `northwind` folder, initialize the `.kport.yaml` file with
```
$ kubectl kport init
```
providing:
- a local service `northwind` with the default port
- the remote service `northwind-db` with the default ports
- the remote service `rabbitmq` with the custom localPort `15672`

and saving the file in the current folder.

Then continue the demo flow replacing `mvn oc:remote-dev` with `kubectl kport start`


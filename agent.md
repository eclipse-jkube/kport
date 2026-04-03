# Agent notes for kport

## Project overview

kport (JKube-kport) is a Proof-of-Concept collection that enables "teleporting" local apps into a remote Kubernetes cluster using JKube remote-dev, SSH tunnels, and port-forwarding.  
It is a multi-module Maven project built with **Quarkus** on Java 17.

### Modules
| Module | Purpose |
|---|---|
| `quarkus-kport` | Quarkus wrapper around the JKube remote-dev library; the core library |
| `kport-ide-server` | Exposes remote-dev features via HTTP/REST (for non-Java IDEs like VS Code) |
| `kubectl-kport` | CLI / kubectl plugin that drives remote development sessions |

## Key dependency: quarkus-sshd

`quarkus-kport` depends on `io.quarkiverse.sshd:quarkus-sshd`.

- Source: https://github.com/quarkiverse/quarkus-sshd
- **Current state**: the dependency is pinned to `999-SNAPSHOT` (the trunk / unreleased version).
- `999-SNAPSHOT` **is not published to Maven Central** — it must be built and installed from source before building kport.

### How to build quarkus-sshd from source
```bash
git clone https://github.com/quarkiverse/quarkus-sshd.git
cd quarkus-sshd
mvn install -DskipTests
```
This installs the artifact into the local Maven repository (`~/.m2`).  
**This must be done before running `mvn install` in kport.**

### When quarkus-sshd is released
Once a stable version is published to Maven Central, update **both** of the following:
1. `quarkus-kport/pom.xml` → change `<version>999-SNAPSHOT</version>` to the released version.
2. Remove the quarkus-sshd checkout + build steps from both GitHub Actions workflows.

## Quarkus platform version

Quarkus platform version is declared in `pom.xml`:
```xml
<quarkus.platform.version>3.33.1</quarkus.platform.version>
```
This version **must stay aligned with the Quarkus version used by the quarkus-sshd trunk**.  
Check the quarkus-sshd `pom.xml` (`<quarkus.version>`) before changing it.

## JKube kit version

`jkube-kit-remote-dev` version is declared in `pom.xml` `<dependencyManagement>`:
```xml
<artifactId>jkube-kit-remote-dev</artifactId>
<version>1.19.0</version>
```
Keep this in sync with the latest JKube release: https://github.com/eclipse-jkube/jkube/releases

## Jakarta EE migration (Quarkus 3.x)

This project uses **Quarkus 3.x** which requires `jakarta.*` imports (not `javax.*`).  
All CDI / JAX-RS annotations in the source files use `jakarta.*`:
- `jakarta.enterprise.context.ApplicationScoped`
- `jakarta.inject.Inject`
- `jakarta.ws.rs.*`

**Never revert to `javax.*` imports.**

## Building the project

```bash
# 1. Build and install quarkus-sshd snapshot (only needed once or when quarkus-sshd changes)
git clone https://github.com/quarkiverse/quarkus-sshd.git /tmp/quarkus-sshd
cd /tmp/quarkus-sshd && mvn install -DskipTests

# 2. Build kport (JVM mode, skip tests)
cd /path/to/kport
mvn clean install -DskipTests

# 3. Build kport (native mode)
mvn clean install -DskipTests -Dnative
```

## CI workflows (`.github/workflows/`)

| Workflow file | Trigger | Purpose |
|---|---|---|
| `pr-build-jvm.yml` | PR against `main` | Fast JVM-only Linux compilation check |
| `pr-build-native.yml` | PR against `main` | Native binary builds on Linux, macOS, Windows |
| `release-build-native.yml` | Release published | Native binaries attached to GitHub releases |
| `release.yml` | Release published | Maven release automation |

### Important: quarkus-sshd checkout in CI workflows

Because `quarkus-sshd 999-SNAPSHOT` is not published, **both PR workflows** check out and build it before building kport:

```yaml
- uses: actions/checkout@v3
  with:
    path: kport        # kport checked out into kport/ subdirectory

- uses: actions/checkout@v3
  with:
    repository: quarkiverse/quarkus-sshd
    path: quarkus-sshd

- name: Build quarkus-sshd (SNAPSHOT)
  working-directory: quarkus-sshd
  run: mvn install -DskipTests

- name: Build All (JVM)
  working-directory: kport
  run: mvn clean install -DskipTests
```

Because kport is checked out into a `kport/` subdirectory (not the workspace root), artifact upload paths must be prefixed with `kport/`, e.g.:
```yaml
path: kport/kubectl-kport/target/kubectl-kport
```

## Source file locations

| File | Module |
|---|---|
| `quarkus-kport/src/main/java/org/eclipse/jkube/kport/KportService.java` | quarkus-kport |
| `quarkus-kport/src/main/java/org/eclipse/jkube/kport/Config.java` | quarkus-kport |
| `quarkus-kport/src/main/java/org/eclipse/jkube/kport/LoggerServiceInjector.java` | quarkus-kport |
| `kport-ide-server/src/main/java/org/eclipse/jkube/kport/server/KportServer.java` | kport-ide-server |
| `kubectl-kport/src/main/java/org/eclipse/jkube/kport/commands/InitCommand.java` | kubectl-kport |
| `kubectl-kport/src/main/java/org/eclipse/jkube/kport/commands/StartCommand.java` | kubectl-kport |
| `kubectl-kport/src/main/java/org/eclipse/jkube/kport/commands/StartTopCommand.java` | kubectl-kport |

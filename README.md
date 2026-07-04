# spring-opcuaserver

An OPC UA server built with Spring Boot 4 (Java 21) and Eclipse Milo 0.6.x.

Milo 0.6.x still depends on `javax.xml.bind`, which was removed from the JDK, so the
`javax.xml.bind:jaxb-api` dependency is declared explicitly in `pom.xml`.

## Getting started

Generate the self-signed certificates used by the SSL bundle (they are gitignored):

```bash
sh server.sh
```

Build and run:

```bash
./mvnw clean spring-javaformat:apply package
./mvnw spring-boot:run
```

The OPC UA endpoints are exposed under the `/milo` path on TCP port `4048` by default.
The port can be changed with the `opcua.tcp-bind-port` property:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--opcua.tcp-bind-port=15048
```

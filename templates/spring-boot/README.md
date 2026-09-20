# Converted Program Starter

This is a buildable Java 21 / Spring Boot 4.1.1 starter, not a converted COBOL
application. It contains no business endpoints, configured database, or business DAO.

## Build and run

From this directory, with a full JDK 21 installed:

```powershell
.\mvnw.cmd verify
.\mvnw.cmd spring-boot:run
```

On macOS/Linux use `sh mvnw verify` and `sh mvnw spring-boot:run`. Maven Wrapper
downloads pinned Maven 3.9.11 and dependencies on first use. Set `JAVA_HOME` if
Java cannot be found on `PATH`. Stop the server with Ctrl+C.

The executable JAR is `target/converted-program-0.0.1-SNAPSHOT.jar`:

```sh
java -jar target/converted-program-0.0.1-SNAPSHOT.jar
```

Port 8080 is the default. There is no root or business endpoint until a conversion
is implemented, so HTTP 404 at `/` is expected. The context test verifies that the
starter assembles and starts with its dependencies; it does not verify COBOL logic.

## Adapt during a conversion

Copy this directory, including `.mvn/`, without `target/` or IDE files into the
selected session's `java/` directory. Update the artifact ID, name, package,
application class, application configuration, and test consistently. Follow the
workspace architecture guide and replace this README with the selected program's
contracts, configuration, example requests, tests, and known limitations.

The package documentation identifies controller, service, DAO, DTO, model, mapper,
and exception responsibilities. Add real classes as required by the analyzed scope;
do not implement placeholder business behavior merely to fill those packages.

## Wrapper provenance

The wrapper scripts are Apache Maven Wrapper 3.3.4 (only-script distribution).
Their license and notice files are retained under `.mvn/wrapper/`.

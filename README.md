# COBOL-to-Java Conversion Workspace

Use your IDE coding assistant to analyze one COBOL program and convert the scope
you choose into a Java 21 Spring Boot application. This repository provides a
shared workflow, session templates, and a buildable Java starter. Conversion is
performed by your assistant, not by an automatic parser or a separate LLM service.

All newly authored content is English. Original user-supplied sources are preserved
unchanged, even when their comments or literals use another language.

## Start a conversion

1. Put your program in `COBOL_PROGRAM/`, together with any available copybooks,
   called programs, schemas, and sample inputs. Identify which file is the entry point.
2. Open this workspace in your IDE and use an assistant with file and terminal access.
3. Start a new conversation with the prompt below. The assistant asks for scope,
   analyzes dependencies, and resolves ambiguities before generating business code.
4. Find the resulting analysis and standalone Java application under
   `conversions/<program-id>/<session-id>/`.

```text
Read AGENTS.md and docs/conversion-guide.md.
Start a new conversion session for COBOL_PROGRAM/SalaryReport.CBL.
Ask whether I want the full program, selected transaction types, or selected
PERFORMs / paragraphs / sections converted. Use English throughout.
Analyze the selected scope and its dependencies before generating Java code.
```

You can specify the scope in the initial prompt instead. For example:

```text
Read AGENTS.md and docs/conversion-guide.md.
Convert only 5000-WRITE-HEADERS in COBOL_PROGRAM/SalaryReport.CBL.
Identify its required state and dependencies, and ask about ambiguous behavior.
```

This second example is a request to analyze a partial scope, not evidence that the
paragraph is independently executable or that its source is valid.

## Assistant setup

| Assistant | Repository instructions |
| --- | --- |
| Codex | `AGENTS.md` |
| Claude | `CLAUDE.md`, which directs the assistant to the common guide |
| GitHub Copilot | `.github/copilot-instructions.md` |

Instruction discovery depends on the IDE integration and version. Enable repository
instructions where required and start a new session after adding them. If automatic
loading is unavailable, paste the starter prompt and explicitly attach the referenced
files. Instructions guide the assistant; they are not a deterministic conversion engine.

## Build the starter

Install a full JDK 21. Maven does not need to be installed separately. The first
build requires internet access to Maven Central. Set `JAVA_HOME` to your JDK if
Java is not already available on `PATH`.

Windows PowerShell:

```powershell
Set-Location templates/spring-boot
.\mvnw.cmd verify
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```sh
cd templates/spring-boot
sh mvnw verify
sh mvnw spring-boot:run
```

Stop the application with Ctrl+C. The starter runs on port 8080 and intentionally
has no business endpoints: an HTTP 404 at `/` is expected. Import its `pom.xml`
into IntelliJ to work with it as a Maven project; there is no root Maven aggregator.

## Workspace layout

| Location | Purpose |
| --- | --- |
| `COBOL_PROGRAM/` | Original input sources and their dependencies |
| `docs/conversion-guide.md` | Scope selection, analysis, generation, and delivery workflow |
| `docs/java-architecture.md` | Java conventions and layer responsibilities |
| `docs/research.md` | Research findings and access limitations |
| `docs/examples/salary-report-analysis.md` | Preliminary observations, not an approved conversion |
| `docs/workflow-checks.md` | Manual assistant workflow acceptance scenarios |
| `docs/verification.md` | Checks executed for the initial workspace delivery |
| `templates/session/` | Session, analysis, mapping, and verification templates |
| `templates/spring-boot/` | Java 21 / Spring Boot starter with Maven Wrapper |
| `conversions/` | Separate outputs for each conversion session |

The initial delivery does not convert SalaryReport. It also does not add a web UI,
database, external AI API dependency, or a general-purpose COBOL compiler.

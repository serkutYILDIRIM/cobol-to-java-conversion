# Initial workspace verification

Date: 2026-09-20.

## Executed checks

| Check | Result |
| --- | --- |
| Required assistant instructions, guides, templates, and wrapper files | Present |
| Original SalaryReport source hash | Unchanged |
| Newly authored content review | English; original supplied COBOL preserved |
| Maven Wrapper version command | Maven 3.9.11 running on Oracle JDK 21.0.10 |
| Maven distribution integrity | Download verified against published SHA-512; SHA-256 pinned in wrapper configuration |
| Starter compilation | Passed with Java release 21 |
| Application-context and embedded-server smoke test | 1 test, 0 failures, 0 errors, 0 skipped |
| Executable Spring Boot JAR packaging | Passed |

Command executed from `templates/spring-boot/`:

```powershell
.\mvnw.cmd -B -ntp verify
```

Result: **BUILD SUCCESS**, exit code 0, elapsed time 1 minute 45 seconds including
initial dependency downloads. The test started Spring Boot 4.1.1 and embedded
Tomcat on an automatically assigned port.

Build outputs, excluded from version control:

- `templates/spring-boot/target/converted-program-0.0.1-SNAPSHOT.jar`
- `templates/spring-boot/target/surefire-reports/`

The test run emitted a non-failing Mockito/Byte Buddy dynamic-agent warning on
JDK 21. It did not prevent the context test or packaging from completing.

Original source SHA-256:

```text
ED26ACE71907C3B362D9598B8B78B0D8DA9C4C9F4017EDE36D4D33C0AD6F15D4
```

## Verification limits

- No COBOL program was converted or executed in this initial delivery.
- No business endpoints or business-rule tests exist in the starter; those belong
  to a selected conversion session.
- Copilot, Claude, and Codex integrations were not each exercised in fresh sessions.
  `docs/workflow-checks.md` contains manual scenarios, not claimed test results.
- Windows was verified. The official Unix wrapper is included, but macOS/Linux
  execution was not tested in this environment.
- Reddit discussions remained inaccessible; no conclusions rely on their contents.

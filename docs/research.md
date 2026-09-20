# Research notes

Research date: 2026-09-20. These notes distinguish inspected evidence from blocked
sources and implementation choices.

## COBOL-AIRLINES

Sources inspected:

- https://github.com/kerestes/COBOL-AIRLINES
- https://github.com/kerestes/COBOL-AIRLINES/blob/main/COB-PROG/FLIGHT-DUPLICATE/FLIGHT-DUPLICATE-COB

The repository describes an airline internal system using COBOL, CICS, and DB2.
It is a business application, not a COBOL-to-Java converter. Its tree separates
CICS programs/maps, batch-style programs, and DB2 schemas/DCLGEN artifacts.

The inspected flight-duplication program includes table layouts, REDEFINES,
PERFORM loops, embedded SQL, and external SQL includes. This illustrates why
conversion requires data-layout and dependency analysis in addition to mapping
procedural statements. Our design separates business behavior from data access
and requests missing schemas/includes rather than inventing them. No airline
functionality or source implementation is copied into this workspace.

## Requested Reddit discussions

- https://www.reddit.com/r/cobol/comments/1dz9fzz/cobol_to_java_migration/
- https://www.reddit.com/r/java/comments/nkndif/lets_hear_your_cobol_to_java_migration_stories/

Both discussions returned HTTP 403. JSON, alternative-page/feed attempts, and a
public text-fetch alternative did not provide the discussions. No findings are
attributed to their comments. Business-equivalence checks in this workspace are
engineering choices, not claims about those inaccessible discussions.

## Platform and assistant instructions

- Spring Boot requirements: https://docs.spring.io/spring-boot/system-requirements.html
- Published parent: https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-parent/4.1.1/
- Codex instructions: https://developers.openai.com/codex/guides/agents-md/
- Claude instructions: https://code.claude.com/docs/en/memory
- Copilot instructions: https://docs.github.com/en/copilot/customizing-copilot/adding-repository-custom-instructions-for-github-copilot
- Maven Wrapper: https://maven.apache.org/wrapper/

Spring Boot 4.1.1 supports Java 21. Assistant documentation describes repository
instruction files, but discovery varies by integration/version. The common guide
and explicit starter prompt provide a fallback without provider-specific APIs.

Apache Maven Wrapper 3.3.4 scripts are distributed unchanged from Maven Central
with their embedded Apache license notices and accompanying license files. Maven
3.9.11 is pinned in the starter's wrapper configuration, with its distribution
checksum. No separately installed Maven is required.

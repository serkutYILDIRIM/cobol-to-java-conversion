# Java architecture

## Baseline

Use Java 21 and Spring Boot 4.1.1 with Maven Wrapper. Keep the application a single
Maven module unless the source requires a documented exception. Use Spring-managed
dependency versions. Add libraries only when the selected program needs them.

The starter contains the application entry point and package documentation, not
business endpoints or a sample DAO pretending to access real data. Generated
programs must implement the applicable layers below.

## Responsibilities

| Package | Responsibility | Dependency direction |
| --- | --- | --- |
| `controller` | REST endpoints, request validation, HTTP responses | Calls services; never DAOs directly |
| `service` | Business rules and use-case orchestration | Uses models and DAO contracts |
| `dao` | Actual file, database, or external data access | Does not depend on controllers or services |
| `dto` | Explicit request/response contracts, preferably records | No persistence behavior |
| `model` | Domain data and meaningful value types | No HTTP coupling |
| `mapper` | Nontrivial format and DTO/domain mappings | No business orchestration |
| `exception` | Domain failures and centralized HTTP translation | No business logic |

For file-based programs, DAOs own file access and expose domain records or a
well-defined streaming contract. Specify who closes streams. For SQL sources,
establish the real schema, transaction boundaries, and target database before
choosing JDBC or JPA. Do not introduce H2 merely to satisfy a DAO requirement.
If the selected behavior has no data access, document that a DAO is not applicable.

## Implementation rules

- Use constructor injection and final dependencies. Keep singleton Spring beans
  stateless; counters, buffers, flags, and totals belong to each operation.
- Use meaningful English names rather than numeric COBOL paragraph labels.
- Use `BigDecimal` for decimal monetary values. Construct from strings or unscaled
  integers; determine scale, rounding, range, and overflow from source semantics.
- Preserve fixed-width fields at I/O boundaries. Do not globally trim input or
  replace COBOL STRING delimiters with ordinary whitespace splitting.
- Use explicit encodings, resource cleanup, and defined file-failure behavior.
  Keep report formatting separate from business aggregation and file access.
- Inject `Clock` when business outputs use the current date or time.
- Define locale and time zone explicitly when formatting output; do not let the
  developer machine's locale change decimal separators, dates, or report text.
- Define Jakarta Validation messages in English. Use `@RestControllerAdvice` and
  `ProblemDetail` for consistent errors once business endpoints are added. Do not
  return internal stack traces or local filesystem details to API consumers.
- Translate STOP RUN to operation completion or an appropriate exception; never
  terminate the server JVM for a business operation.
- Keep every new comment, identifier, message, report heading, and document English.
  Record required translations as intentional source/output differences.

## Tests and delivery

Use JUnit through Spring Boot Test. Test services with focused inputs, DAOs with
real temporary files or a suitable database test environment, and HTTP contracts
with Spring's test support. Do not use mocks as evidence of actual file or SQL
behavior. Add failure, boundary, and state-isolation cases derived from the program.

Every generated application must build independently with its copied Maven Wrapper.
Document required external services, configuration, example requests, and the
limits of any COBOL compatibility evidence.

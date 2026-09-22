# Verification report

Status: passed; Java-tested and source-reviewed

## Environment and commands

- Java version: `21.0.10` (Oracle Java HotSpot 64-Bit Server VM)
- Maven/Wrapper versions: Maven Wrapper, Maven 3.9.11 distribution; Spring Boot 4.1.1
- Commands and exit codes: `java -version` (exit 0); `.\mvnw.cmd verify` (exit 0)
- Test results and report locations: 3 tests passed, 0 failures, 0 errors, 0 skipped. Reports are under `java/target/surefire-reports/`.

## Cases

| Scenario | Expected result and evidence source | Actual result | Status |
| --- | --- | --- | --- |
| Service returns the fixed-width source field | Exact 30-character value `HELLO FROM IBM COBOL` followed by ten spaces; source line 9 | Exact value and length verified | Pass |
| HTTP execution of `MAIN-LOGIC` | `GET /api/hello` returns `200 OK` and the fixed-width message in JSON; source lines 9 and 13, D1 | `200 OK` and exact JSON body verified | Pass |
| Repeated operation execution | Same result per invocation; source has no mutable state | Same value returned by consecutive service calls | Pass |

## COBOL comparison

- Runtime/compiler/dialect: unavailable
- Input fixtures and invocation: not run
- Compared outputs: none
- Allowed differences: none established
- Equivalence conclusion: COBOL execution equivalence is unverified. The Java application is source-reviewed and Java-tested only.

## Delivery checks

- [x] Selected behavior and required dependencies are implemented.
- [x] Excluded behavior is documented.
- [x] No fake success stubs remain.
- [x] Controller and service boundaries are respected; a DAO is not applicable.
- [x] Fixed-width behavior is tested.
- [x] Per-request state behavior is tested; no source error behavior exists to implement.
- [x] The standalone Java project passes Maven Wrapper verification.
- [x] All newly authored content is English.
- [x] Original COBOL inputs remain unchanged.
- [x] Remaining limitations and actual verification evidence are reported.

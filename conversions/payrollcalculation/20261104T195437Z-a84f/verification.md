# Verification report

Status: passed for the Java implementation; COBOL equivalence unverified

## Environment and commands

- Java version: Oracle JDK 21.0.10.
- Maven/Wrapper versions: Maven Wrapper 3.3.4; Maven 3.9.11.
- Command: `.\mvnw.cmd verify` from this Java project directory; exit code 0, `BUILD SUCCESS`.
- Test results: 5 tests, 0 failures, 0 errors, 0 skipped.
- Test report location: `java/target/surefire-reports/`.
- Packaging: executable JAR created at `java/target/payroll-calculation-0.0.1-SNAPSHOT.jar`.
- Non-failing warning: Mockito dynamically attached its Byte Buddy agent on JDK 21.

## Cases

| Scenario | Expected result and evidence source | Actual result | Status |
| --- | --- | --- | --- |
| Full report and API response | Source-derived report contract in `analysis.md`; GET returns JSON report text. | MockMvc returned HTTP 200 / JSON with the expected header, employee detail, summary, final separator, and LF line breaks. | Pass |
| Per-employee calculations | Source-derived sample values in `analysis.md`; rounding assumption D2. | All six amounts for each of the three employees matched the source-derived expectations. | Pass |
| Summary totals and highest-paid employee | Source-derived summary values in `analysis.md`; rounding assumption D2. | Gross 130350.00, deductions 35064.16, net 95285.84, average 31761.95, highest-paid Sample Employee A. | Pass |
| Repeated calls | Each invocation creates independent calculation state. | Repeated service reports were equal and had distinct employee lists. | Pass |
| Concurrent calls | Concurrent requests return independent, identical reports. | Eight concurrent MockMvc requests returned the same complete JSON response. | Pass |
| Unsupported route | No endpoint exists outside the documented API. | Unknown GET path returned HTTP 404. | Pass |

## COBOL comparison

- Runtime/compiler/dialect: No COBOL compiler is installed; GnuCOBOL is suggested by a source comment, but version and exact dialect are unverified.
- Input fixtures and invocation: Not run.
- Compared outputs: None.
- Allowed differences: JSON transport wrapper and LF line separators (D1/D3).
- Equivalence conclusion: Unverified; the implementation is source-reviewed and Java-tested only.

## Delivery checks

- [x] Selected behavior and required dependencies are implemented.
- [x] Excluded behavior is documented.
- [x] No fake success stubs remain.
- [x] Controller, service, and applicable DAO boundaries are respected; a DAO is not applicable.
- [x] Monetary and fixed-width behavior is tested where applicable.
- [x] Unsupported-route and per-request state behavior are tested; the source has no input-validation or external-I/O failure paths.
- [x] The standalone Java project passes Maven Wrapper verification.
- [x] All newly authored content is English.
- [x] Original COBOL inputs remain unchanged.
- [x] Remaining limitations and actual verification evidence are reported.

# Verification report

Status: passed for Java implementation; COBOL execution comparison unavailable

## Environment and commands

- Java: Oracle JDK `21.0.10` LTS
- Maven Wrapper: Apache Maven `3.9.11`
- Platform: Windows 10 amd64; platform encoding UTF-8; observed default locale
  `tr_TR`
- `java -version`: exit 0
- first `.\mvnw.cmd verify`: exit 1; all production code compiled and 14 of 15
  tests passed. One HTTP test expected an unquoted attachment filename while Spring
  emitted the standards-compliant quoted filename. The assertion was corrected.
- final `.\mvnw.cmd verify`: exit 0; 16 tests passed, no failures, errors, or skips
- `.\mvnw.cmd -version`: exit 0
- `git diff --check`: exit 0
- COBOL compiler/runtime: `cobc` was not discoverable on `PATH`
- Test reports: `java/target/surefire-reports/`
- Executable JAR: `java/target/salary-report-0.0.1-SNAPSHOT.jar`

## Cases

| Scenario | Expected result and evidence source | Actual result | Status |
| --- | --- | --- | --- |
| Source identity | SHA-256 from session creation remains unchanged | `ED26ACE71907C3B362D9598B8B78B0D8DA9C4C9F4017EDE36D4D33C0AD6F15D4` | Pass |
| Source encoding | Strict UTF-8 decode succeeds | Succeeded | Pass |
| Fixed record parsing | Map positions by Unicode character and parse `9(06)V99` as an implied decimal | Tested with ASCII, Turkish letters, and a supplementary Unicode character | Pass |
| Invalid input | Reject wrong widths, nonnumeric salaries, malformed UTF-8, and unsupported carriage returns | Focused mapper/DAO tests passed; HTTP malformed-record test returned 400 problem detail | Pass |
| Input lifecycle | Accept empty input, LF, CRLF, a final terminator, and no final terminator; translate read failure | DAO tests passed | Pass |
| Empty report | Emit one 80-character English message plus LF | Service test passed | Pass |
| Normal report | Emit fixed-width English headings, detail, implied-decimal salary, totals, and fixed date | Service test passed; every report record asserted as 80 characters | Pass |
| Name formatting | Stop at the first exact double-space and clear the destination for each employee | Long-then-short name test passed | Pass |
| Pagination | Preserve 36 details per page and emit standalone form-feed before page 2+ | 36/37-boundary test passed | Pass |
| Numeric overflow | Reject total beyond `PIC 9(09)V99` capacity and return HTTP 422 | Service and live HTTP tests passed | Pass |
| HTTP success | Return UTF-8 text with downloadable `EMP_REPORT.TXT` disposition | Live random-port HTTP test passed | Pass |
| State isolation | Repeated concurrent calls produce identical fresh reports | 20-call, eight-thread test passed | Pass |
| Application packaging | Build executable Spring Boot JAR | JAR built successfully | Pass |

## COBOL comparison

- Runtime/compiler/dialect: unavailable
- Input fixtures and invocation: no accepted business fixture was supplied; not run
- Compared outputs: none
- Allowed differences: accepted English translations, HTTP transport, strict
  validation and failures, standalone form-feed page separators, and clearing the
  employee-name destination on each record
- Equivalence conclusion: Java-tested and source-reviewed only. COBOL execution
  equivalence and byte-for-byte output equivalence remain unverified.

## Delivery checks

- [x] Selected behavior and required dependencies are implemented.
- [x] Excluded behavior is documented.
- [x] No fake success stubs remain.
- [x] Controller, service, and applicable DAO boundaries are respected.
- [x] Monetary and fixed-width behavior is tested where applicable.
- [x] Error and per-request state behavior is tested.
- [x] The standalone Java project passes Maven Wrapper verification.
- [x] All newly authored content is English.
- [x] Original COBOL inputs remain unchanged.
- [x] Remaining limitations and actual verification evidence are reported.

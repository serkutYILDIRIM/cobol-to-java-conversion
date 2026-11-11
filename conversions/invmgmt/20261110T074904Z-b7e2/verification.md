# INVMGMT verification

Status: passed Java verification; source-reviewed; COBOL execution equivalence
unverified.

## Environment and commands

Executed from this session's `java` directory on Windows:

- Oracle JDK 21.0.10, runtime `C:\Program Files\Java\jdk-21.0.10`.
- Maven Wrapper 3.3.4, Apache Maven 3.9.11, Spring Boot 4.1.1.
- Host default locale tr_TR, UTF-8. Numeric report formatting explicitly uses US
  symbols; an additional test changes the default locale to Germany.
- `.\mvnw.cmd --version`: exit 0.
- Initial `.\mvnw.cmd verify`: exit 1; 19 tests executed with three incorrect
  expected-format fixtures. Source checks showed 46 report lines, five label
  padding spaces, one title padding space, and the exact 14-character rejection
  prefix. Corrected test expectations and analysis wording, not business behavior.
- `.\mvnw.cmd '-Dtest=InventoryServiceTests,InventoryReportFormatterTests' test`:
  first rerun exit 1 (one remaining rejection-prefix fixture), second rerun exit 0,
  14 tests passed.
- Final `.\mvnw.cmd verify`: exit 0, BUILD SUCCESS, 19 tests, zero failures,
  zero errors, zero skipped; completed 2026-11-10T07:59:18Z in 17.516 seconds.
- `git diff --check`: exit 0 for tracked worktree content. New session files were
  not staged or committed. Existing unrelated payroll files were left untouched.

Reproducible output locations:

- [JUnit XML and text reports](java/target/surefire-reports/)
- [Final verification console output](java/target/verification-output.log)
- [Executable Spring Boot jar](java/target/inventory-management-0.0.1-SNAPSHOT.jar)

Build outputs are intentionally ignored by Git; the committed-form evidence is
this report and the executable test sources. Re-running verification regenerates
the JUnit reports and jar. The console log above was captured from the final run.

## Executed cases

| Test group / scenario | Source-derived expectation | Actual result |
| --- | --- | --- |
| EmbeddedInventoryTests (2) | All ten field layouts and twelve signed transactions match the original offsets, values, names, statuses, and order | Passed |
| Full batch | Quantities 40,85,18,50,55,42,15,7,8,80; 8 applied; 4 rejected | Passed |
| Rejection precedence | Missing product before inactive before invalid type before insufficient stock; rejected stock unchanged | Passed |
| Sale/reorder boundaries | Exact available stock may be sold; later sale rejects; equality with reorder flags LOW | Passed |
| Signed/zero movements | Do not invent positivity rules; preserve modeled unsigned receiving-field semantics | Passed |
| Stock receiving width | 99999 + 2 retains 00001 | Passed |
| First-match search | Duplicate-code fixture updates only first matching product | Passed |
| Inactive value / tax boundary | Inactive contributes value but not low count; .25 * .18 rounds .045 to .05 | Passed |
| Amount overflow | Preserve nine integer digits on item, net, and gross receiving fields | Passed |
| Counter/movement widths | Three-digit processed counter wraps; seven-digit received total wraps; six-digit edited report truncates | Passed |
| Repeated execution / immutable outputs | Each run starts from source constants; returned collections cannot be mutated | Passed |
| Concurrency | 32 calls through one service using eight workers produce identical independent results | Passed |
| InventoryServiceTests total | Eleven independent service tests | 11 passed |
| Fixed-width report | 46 lines, 78-character header/separator, 53-character title, 79-character details, exact padding and LF | Passed |
| Reject sign / numeric zero | Fixed negative-sign position; final zero digit retained; both action branches | Passed |
| Locale independence | US grouping and decimal separator even with German default | Passed |
| InventoryReportFormatterTests total | Three report tests | 3 passed |
| Real HTTP | Embedded Spring Boot server on random local port; POST serializes all products/transactions, summary and report; repeated requests reset state | Passed |
| HTTP failure contracts | Wrong method 405; unknown route 404 | Passed |
| Exception translation | Injected execution failure gives generic 500 ProblemDetail without private details; server-side error logged | Passed |
| InventoryHttpTests total | Two real-server tests and one isolated failure-mapping test | 3 passed |

Final batch values, independently calculated before assertions:

| Quantity | Expected and observed Java |
| --- | --- |
| Received / sold | 70 / 488 |
| Active low-stock products | 6 |
| Net inventory | 27676.07 |
| Rounded 18% tax | 4981.69 |
| Gross inventory | 32657.76 |

No DAO test is applicable: all source data is embedded working storage and the
application introduces no external data access.

## COBOL comparison and source integrity

- No `cobc` executable was found. No COBOL run or differential comparison occurred.
- Expected outputs are source-derived, not observed COBOL outputs.
- Source SHA-256 before and after:
  `E901B4C4955A60E17D02ED31BADB4FD06306A70175CCCE35CD99736A062242C3`.
- Original COBOL and other supplied programs were not edited.
- Numeric receiving-field corner behavior is explicitly assumed under D4 in
  [session.md](session.md). It is Java-tested but not confirmed against a specific
  COBOL runtime. The original batch does not exercise overflow or negative final stock.
- REST JSON, LF report transport, and operation return instead of STOP RUN are the
  documented intentional adaptations. No English translation was needed.

## Delivery checks

- [x] Full selected behavior and required dependencies implemented.
- [x] Scope, exclusions, decisions, analysis, and mapping recorded before generation.
- [x] No fake success stubs, persistence, or invented business rules.
- [x] Controller/service/formatter/model boundaries respected; DAO not applicable.
- [x] Monetary, fixed-width, boundary, failure, and state-isolation tests passed.
- [x] Independent project passes Maven Wrapper verify on JDK 21.
- [x] All new content is English.
- [x] Supplied COBOL source unchanged.
- [x] Actual checks and COBOL-equivalence limitation recorded.

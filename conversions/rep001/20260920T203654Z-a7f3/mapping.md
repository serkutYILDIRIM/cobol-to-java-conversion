# COBOL-to-Java mapping

| COBOL location / responsibility | Planned Java class / method / contract | Selected or supporting | Evidence / decision |
| --- | --- | --- | --- |
| `0000-MAIN-LOGIC` (109-113) | `SalaryReportService.generate(...)` orchestration | Selected | D-01 |
| `1000-INITIALIZE` (115-136) | DAO/resource setup plus per-operation state and injected clock | Supporting | Required initialization and priming read |
| `2000-PROCESS-DATA` (138-160) | report generation loop and aggregation | Selected | D-01 |
| `3000-TERMINATE` (162-177) | final totals/empty result and resource completion | Selected | D-01, D-06, D-07 |
| `4000-READ-EMP-FILE` (179-185) | streaming fixed-width employee DAO | Supporting | Required input/EOF dependency |
| `5000-WRITE-HEADERS` (187-201) | stateless report formatter with per-operation pagination state | Supporting | Required page/header dependency; D-02, D-08 |
| `EMP-RECORD` (24-32) | `EmployeeRecord` plus fixed-width mapper/parser | Supporting | Input contract; D-05, D-07 |
| Report layouts (34-106) | fixed-width report formatter | Supporting | Output contract; D-02, D-03, D-06 |
| COBOL file names (14, 18) | HTTP transport and stream DAO boundary | Supporting | D-04; server paths will not be exposed |
| STOP RUN/open errors (117-129) | typed exceptions and centralized HTTP problem responses | Supporting | D-07 |

## Excluded functionality

None within `REP001`. The supplied source has no transaction dispatch, external
calls, copybooks, SQL, CICS, or database behavior to convert.

## Intentional differences

- All generated identifiers, messages, headings, documentation, and error details
  will be English. Exact report translations await D-06.
- `STOP RUN` will become operation completion or a typed failure and will never
  terminate the Spring server JVM.
- HTTP replaces fixed process-level input/output filenames. The exact transport
  awaits D-04.
- Invalid page-advance syntax and the uncleared name buffer require explicit
  decisions D-02 and D-03; no correction has been assumed.

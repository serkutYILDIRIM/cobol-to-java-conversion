# COBOL-to-Java mapping

| COBOL location / responsibility | Java class / method / contract | Selected or supporting | Evidence / decision |
| --- | --- | --- | --- |
| `0000-MAIN-LOGIC` (109-113) | `SalaryReportService.generate(...)` orchestration | Selected | D-01 |
| `1000-INITIALIZE` (115-136) | `Utf8FixedWidthEmployeeRecordReader`, per-call state in `SalaryReportService`, and injected `Clock` | Supporting | Required initialization and priming-read equivalent |
| `2000-PROCESS-DATA` (138-160) | `SalaryReportService.generate` loop and aggregation | Selected | D-01 |
| `3000-TERMINATE` (162-177) | `SalaryReportFormatter` totals/empty output and Java resource completion | Selected | D-01, D-06, D-07 |
| `4000-READ-EMP-FILE` (179-185) | `EmployeeRecordReader` and `Utf8FixedWidthEmployeeRecordReader` | Supporting | Required input/EOF dependency |
| `5000-WRITE-HEADERS` (187-201) | `SalaryReportService` pagination and `SalaryReportFormatter` headings | Supporting | D-02, D-08 |
| `EMP-RECORD` (24-32) | `EmployeeRecordMapper` and `Employee` | Supporting | D-05, D-07 |
| Report layouts (34-106) | `SalaryReportFormatter` | Supporting | D-02, D-03, D-06 |
| COBOL file names (14, 18) | `SalaryReportController` raw-body/download contract | Supporting | D-04 |
| STOP RUN/open errors (117-129) | typed exceptions and `ApiExceptionHandler` problem responses | Supporting | D-07 |

## Excluded functionality

None within `REP001`. The supplied source has no transaction dispatch, external
calls, copybooks, SQL, CICS, or database behavior to convert.

## Intentional differences

- All generated identifiers, messages, headings, documentation, and error details
  are English using the translations accepted in D-06.
- `STOP RUN` becomes operation completion or a typed failure and never
  terminates the Spring server JVM.
- HTTP raw UTF-8 input and a downloadable text response replace fixed process-level
  input/output filenames (D-04 and D-05).
- A standalone form-feed line implements the invalid page-advance statement (D-02).
- Each name field is freshly space-filled, correcting prior-record suffix leakage
  from the uncleared COBOL destination (D-03).
- Numeric capacity overflow is explicit HTTP 422 instead of compiler-dependent
  behavior without `ON SIZE ERROR` (D-07).

# Program analysis

## Evidence and purpose

`COBOL_PROGRAM/SalaryReport.CBL` is a fixed-format program named `REP001` (lines
1-4). It opens a line-sequential employee input file and line-sequential report
output file (lines 11-20), processes every employee, and writes a dated,
paginated salary report with detail and total lines (lines 108-201).

The physical source is valid UTF-8 without a BOM and uses CRLF line endings. The
COBOL compiler and dialect are not identified. No COBOL compiler is currently
discoverable on `PATH`, so none of the behavior described here is an observed
COBOL execution result . 

## Data definitions

| Source field / record | PIC, length, scale | Initialization / conditions | Java representation |
| --- | --- | --- | --- |
| `EMP-RECORD` | Group, 80 characters | Populated by each line-sequential read | Strict UTF-8 boundary parser requiring exactly 80 Unicode characters (D-07) |
| `EMP-ID` | `X(05)`, 5 | Input positions 1-5 | Five-character `String`, with boundary padding preserved |
| `EMP-FIRST-NAME` | `X(15)`, 15 | Input positions 6-20 | Fixed-width `String`; two-space delimiter semantics retained |
| `EMP-LAST-NAME` | `X(15)`, 15 | Input positions 21-35 | Fixed-width `String`; two-space delimiter semantics retained |
| `EMP-DEPT` | `X(10)`, 10 | Input positions 36-45 | Fixed-width `String` |
| `EMP-SALARY` | `9(06)V99`, 8 digits, scale 2 | Input positions 46-53; unsigned display numeric | `BigDecimal` created from eight unscaled digits with scale 2 |
| Input filler | `X(27)`, 27 | Input positions 54-80 | Validated/preserved only at the input boundary; no business meaning |
| `REPORT-RECORD` | `X(80)`, 80 | Receives each report line | Exactly 80-character report line before line separator |
| File statuses | two `X(02)` fields | Set by COBOL file operations | Typed Java exceptions with 400/422/500 HTTP problem responses (D-07) |
| EOF switch | `X(01)`, initial `N`; level-88 value `Y` | Set after an at-end read | Local loop/end-of-stream state |
| Page count | `9(03)`, initial 0 | Incremented per header | Per-request integer, source capacity 999 |
| Line count/limit | `9(02)`, initial 99; limit 40 | Reset to 5 after headers | Per-request pagination state |
| Employee count | `9(05)`, initial 0 | Incremented for each processed record | Per-request integer, source capacity 99,999 |
| Total salary | `9(09)V99`, initial 0 | Sum of salaries | Per-request `BigDecimal`, source capacity 999,999,999.99 |
| Current date | `X(08)` split as YYYY/MM/DD | Accepted once during initialization | `LocalDate` obtained from an injected `Clock` |
| `DET-EMP-NAME` | `X(30)` | No explicit initialization between STRING operations | Fresh, space-filled 30-character value for each employee (D-03) |
| Edited salary | `$ZZZ,ZZ9.99` | Receives employee salary | Locale-independent COBOL-style formatter, 11 characters |
| Edited totals | `ZZ,ZZ9` and `$ZZZ,ZZZ,ZZ9.99` | Receive final count and salary | Locale-independent COBOL-style formatters |

No `COMP`, `COMP-3`, `REDEFINES`, or `OCCURS` fields occur. The sole level-88
condition is `END-OF-FILE` (lines 42-43).

## Selected control flow and dependency closure

Full-program execution begins at `0000-MAIN-LOGIC`: initialize, repeatedly process
the current record until EOF, terminate, then stop (lines 109-113).

`1000-INITIALIZE` opens the input and output files, stops after an open failure,
captures the date, and performs the priming read (lines 115-136). The priming read
is required so the processing loop does not process an undefined record for empty
input.

`2000-PROCESS-DATA` checks pagination before each detail, maps the current input
record to a detail line, writes it, increments the line and aggregate state, then
reads the next input record (lines 138-160). `4000-READ-EMP-FILE` sets EOF at the
end of input (lines 179-185). There is no `GO TO`, `THRU`, recursive call, or
transaction dispatch.

`5000-WRITE-HEADERS` increments the page, optionally attempts a page advance,
writes four header records, then sets the line count to 5 (lines 187-201). Since
the next page check is `line count > 40`, a steady-state page receives 36 details:
counts 5 through 40 are written, after which the count is 41. This is source-derived,
not runtime-observed.

`3000-TERMINATE` writes three total records when at least one employee was read or
an empty-input message otherwise, closes both files, and displays a completion
message (lines 162-177). The totals are not included in the pagination count and
can extend beyond the stated page limit.

All working state must be created per operation. No mutable report buffers,
counters, date, totals, or EOF flags may be shared between repeated or concurrent
HTTP requests.

## External dependencies and I/O

- Input logical name: `EMP_INPUT.DAT`, line sequential, nominal record layout 80
  characters (lines 14-16 and 24-32).
- Output logical name: `EMP_REPORT.TXT`, line sequential, receiving record exactly
  80 characters (lines 18-20 and 34-35).
- No `COPY`, `REPLACE`, `CALL`, SQL, CICS, database schema, sorting service, or
  external transaction manager is referenced.
- No copybook or business-data fixture is present in the repository.
- The Java DAO will own input stream creation and closure. Output ownership depends
  on D-04; the service will produce report content without exposing server paths.
- Encoding is not declared by COBOL and must be decided in D-05.



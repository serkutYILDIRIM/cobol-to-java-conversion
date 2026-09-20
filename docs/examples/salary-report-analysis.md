# SalaryReport: preliminary observations

Status: source inspection only. No conversion scope has been selected. This is
not an approved behavioral specification or a generated application.

Source: `COBOL_PROGRAM/SalaryReport.CBL`; PROGRAM-ID: `REP001`.

## Observed structure

The program reads line-sequential employee records and writes an 80-character
salary report with headings, detail rows, totals, and page numbering. There is
no SQL, CICS, COPY, CALL, or transaction-type dispatch in this supplied source.
Data access for a conversion would therefore use file DAOs, not a database.

| Paragraph | Observed responsibility |
| --- | --- |
| `0000-MAIN-LOGIC` | Initialize, process until EOF, terminate |
| `1000-INITIALIZE` | Open files, set report date, read first record |
| `2000-PROCESS-DATA` | Write details, accumulate totals, read next record |
| `3000-TERMINATE` | Write totals or empty-input message and close files |
| `4000-READ-EMP-FILE` | Read a record and set EOF state |
| `5000-WRITE-HEADERS` | Update page state and write headings |

The input fields have lengths 5, 15, 15, 10, 8, and 27, totaling 80. Salary uses
`PIC 9(06)V99`: eight digits with an implied two-place decimal, not a textual
decimal point. File encoding and runtime record-length behavior are not established.

## Questions for a future conversion session

- The pagination paragraph contains `MOVE ADVANCING PAGE TO REPORT-RECORD`, which
  appears invalid in standard COBOL. Determine the intended page-break output and
  source dialect before implementing it; do not silently invent form-feed behavior.
- `DET-EMP-NAME` is not cleared before successive STRING operations. A shorter
  name may leave trailing characters from the previous record. Ask whether to
  preserve observable behavior or clear the field in the Java version.
- STRING uses a delimiter of two spaces, not arbitrary whitespace. Do not silently
  replace it with `trim()` or a whitespace-splitting approximation.
- The first employee-count total layout has 81 characters while REPORT-RECORD has
  80. Establish truncation behavior and expected output widths.
- The line counter starts at 99, resets to 5 after headings, and checks `> 40`
  before a detail is written. The apparent steady-state page size is 36 detail
  rows, not 40; validate this expectation and the intended pagination semantics.
- Read/write error handling beyond the explicit OPEN checks is incomplete. Establish
  expected Java failure behavior and numeric overflow handling with the user.
- English translations of headings and messages are required in generated output.
  Record translated text and resolve changes affecting fixed-width layouts.

## Future test candidates

Empty input; one employee; implied-decimal parsing; salary totals; 36/37 detail
rows across page boundaries; longer followed by shorter names; names containing
the exact two-space delimiter; missing/unreadable input; unwritable output;
malformed records; numeric limits; fixed dates; independent repeated requests.

Expected outputs must be established during the selected conversion session.
No COBOL compiler run or output-equivalence validation has been performed here.

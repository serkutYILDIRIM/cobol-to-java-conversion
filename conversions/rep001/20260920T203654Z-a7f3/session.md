# Conversion session

Status: complete

## Identity

- Session ID: `20260920T203654Z-a7f3`
- Source path: `COBOL_PROGRAM/SalaryReport.CBL`
- PROGRAM-ID: `REP001`
- Source SHA-256: `ED26ACE71907C3B362D9598B8B78B0D8DA9C4C9F4017EDE36D4D33C0AD6F15D4`
- Dependency paths and hashes: none; the source contains no `COPY` dependencies
- Compiler/dialect and encoding: fixed-format COBOL is indicated by source layout; compiler and dialect are unresolved. The source is valid UTF-8 without a BOM and uses CRLF line endings.

## User-selected scope

- Mode: full
- Selected identifiers: all paragraphs in `REP001`
- User's scope statement: convert all COBOL code in `COBOL_PROGRAM/SalaryReport.CBL` as a full-program conversion
- Required supporting behavior: employee record parsing, file lifecycle, per-run state, date acquisition, report formatting, pagination, totals, EOF handling, and error translation
- Explicit exclusions: none within `REP001`; there is no transaction dispatch or external called program

## Decisions

| ID | Source evidence / question | User answer or established requirement | Status / effect |
| --- | --- | --- | --- |
| D-01 | Full program, transaction types, or selected paragraphs? | Full conversion of `SalaryReport.CBL`. | Resolved; all paragraphs are selected. |
| D-02 | Lines 191-193 contain invalid standard COBOL page-advance syntax. | Emit a standalone form-feed character before page 2+ headers. | Resolved; explicit text-report page separator. |
| D-03 | Lines 145-149 do not initialize `DET-EMP-NAME`. | Clear the 30-character name field for every employee. | Resolved; intentional correction prevents prior-record suffix leakage. |
| D-04 | How should the HTTP application receive employee data and return the report? | `POST /api/v1/salary-reports` accepts raw fixed-width data and returns `text/plain`. | Resolved; no server filesystem paths are exposed. |
| D-05 | What encoding should external fixed-width input and report output use? | UTF-8, with widths measured in Unicode characters. | Resolved; strict malformed UTF-8 rejection. |
| D-06 | Are the proposed fixed-width English translations accepted? | All recommendations accepted. | Resolved; use translations listed in `analysis.md`. |
| D-07 | What should happen for malformed input, overflow, and I/O failures? | Require exactly 80 characters per record; use HTTP 400 for malformed records, 422 for numeric overflow, and 500 for unexpected I/O failures. | Resolved; implement typed problem responses. |
| D-08 | Preserve the source-derived 36 detail rows per page or correct to 40? | Preserve 36. | Resolved; retain source counter behavior. |

## Progress

- Analysis: complete
- Contract definition: complete
- Implementation: complete
- Verification: passed with 16 Java tests; source hash and strict UTF-8 validity rechecked
- Remaining limitations: no accepted input/output fixture, identified compiler/dialect, or executable COBOL runtime is available, so COBOL execution equivalence is unverified

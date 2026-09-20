# Conversion session

Status: awaiting behavior decisions

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

## Decisions and open questions

| ID | Source evidence / question | User answer or established requirement | Status / effect |
| --- | --- | --- | --- |
| D-01 | Full program, transaction types, or selected paragraphs? | Full conversion of `SalaryReport.CBL`. | Resolved; all paragraphs are selected. |
| D-02 | Lines 191-193 contain `MOVE ADVANCING PAGE TO REPORT-RECORD`, which is not valid standard COBOL page-advance syntax. What bytes or lines should separate pages? | Awaiting user decision. | Open; blocks report pagination implementation. |
| D-03 | Lines 145-149 do not initialize `DET-EMP-NAME`; COBOL may retain suffix characters when a shorter name follows a longer one. Preserve that behavior or clear the field for every record? | Awaiting user decision. | Open; changes detail output. |
| D-04 | How should the HTTP application receive the employee data and return the report? | Awaiting user decision. | Open; blocks API and DAO contract. |
| D-05 | What encoding should be used for the external fixed-width employee data and report? | Awaiting user decision. | Open; character widths and Turkish employee data may differ by encoding. |
| D-06 | Workspace rules require English generated output. Are the proposed fixed-width English translations in `analysis.md` accepted? | Awaiting user decision. | Open; changes report text while retaining 80-character records. |
| D-07 | What should happen for malformed/short records, nonnumeric salaries, read/write failures, and counter or total overflow? | Awaiting user decision. | Open; blocks failure semantics. |
| D-08 | The counter resets to 5 and checks `> 40`, producing 36 detail rows per steady-state page. Is that intended, or should each page contain 40 details? | Awaiting user decision. | Open; changes pagination and report output. |

## Progress

- Analysis: in progress; source structure, state, dependencies, and semantic risks inventoried
- Contract definition: pending user decisions D-02 through D-08
- Implementation: not started, as required while behavior-changing questions remain open
- Verification: source hash and UTF-8 validity checked; Java/Cobol application tests not run
- Remaining limitations: no input fixture, expected report, compiler/dialect, or executable COBOL runtime is available

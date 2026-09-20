# REP001 Salary Report

This standalone Java 21 / Spring Boot 4.1.1 application is the full-program
conversion of `COBOL_PROGRAM/SalaryReport.CBL` (`PROGRAM-ID REP001`). It accepts
the original fixed-width employee layout and returns the formatted salary report.

## HTTP contract

`POST /api/v1/salary-reports`

- Request content type: `text/plain; charset=UTF-8`
- Request body: zero or more LF- or CRLF-separated employee records. Every record
  must contain exactly 80 Unicode characters.
- Response: `200 OK`, `text/plain; charset=UTF-8`, with attachment filename
  `EMP_REPORT.TXT`.
- Invalid UTF-8, record widths, or salaries: `400 Bad Request` problem detail.
- COBOL numeric capacity overflow: `422 Unprocessable Content` problem detail.
- Unexpected input I/O failure: `500 Internal Server Error` problem detail.

Each input record has this layout:

| Characters | Field | Rule |
| --- | --- | --- |
| 1-5 | Employee ID | Preserved fixed-width text |
| 6-20 | First name | Preserved; the first exact double-space ends report text |
| 21-35 | Last name | Preserved; the first exact double-space ends report text |
| 36-45 | Department | Preserved fixed-width text |
| 46-53 | Salary | Eight digits with an implied two-place decimal |
| 54-80 | Filler | Accepted and ignored |

The report uses English headings, 80-character report records, LF line endings,
36 detail records per page, and a standalone form-feed line before every page
after the first. Report dates use `Europe/Istanbul` by default. Change
`salary-report.zone-id` to another valid Java zone ID when required.

## Build and run

From this directory with JDK 21:

```powershell
.\mvnw.cmd verify
.\mvnw.cmd spring-boot:run
```

Submit a local input file from PowerShell:

```powershell
Invoke-WebRequest `
  -Method Post `
  -Uri http://localhost:8080/api/v1/salary-reports `
  -ContentType 'text/plain; charset=UTF-8' `
  -InFile EMP_INPUT.DAT `
  -OutFile EMP_REPORT.TXT
```

The executable JAR is `target/salary-report-0.0.1-SNAPSHOT.jar` after a successful
build. All operation state is request-local, so concurrent requests do not share
counters, totals, dates, or report buffers.

## Compatibility limits

No compatible COBOL compiler/runtime or accepted input/output fixture was available
for an execution comparison. The application is source-reviewed and Java-tested;
byte-for-byte COBOL equivalence is unverified. Accepted corrections and transport
adaptations are documented in the parent session files.

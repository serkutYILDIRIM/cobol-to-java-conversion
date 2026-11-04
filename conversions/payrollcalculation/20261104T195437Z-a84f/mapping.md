# COBOL-to-Java mapping

| COBOL location / responsibility | Java class / method / contract | Selected or supporting | Evidence / decision |
| --- | --- | --- | --- |
| `main-program` (lines 49-60) | `PayrollService.generateReport()` | Selected entry and orchestration | Full program scope; each invocation owns its state. |
| `prepare-sample-data` (lines 62-83) | `PayrollService` sample-data factory / immutable employee records | Supporting | Preserves the three source-defined inputs; D1. |
| `print-report-header` (lines 85-90) | `PayrollReportFormatter` header formatting | Supporting | Preserves English heading, TRY and sample-rate labels, and separators. |
| `calculate-salary` (lines 92-113) | `PayrollService` per-employee calculation | Selected business rules | Preserve `ROUNDED` assignments at two decimal places under D2. |
| `update-totals` (lines 115-122) | `PayrollService` aggregation and highest-net selection | Supporting | Preserve totals and first-wins tie behavior. |
| `print-employee-report` (lines 124-144) | `PayrollReportFormatter` employee formatting | Supporting | Preserve source labels, field padding, monetary picture, and line order; D3. |
| `print-summary-report` (lines 146-160) | `PayrollReportFormatter` summary formatting | Supporting | Preserve rounded average and highest-paid name; D2/D3. |
| Terminal `DISPLAY` operations (lines 85-90, 124-160) | `GET /api/payroll/report` and `PayrollReportResponse` | Transport adaptation | JSON DTO wraps the report text; no arbitrary input or filesystem access; D1. |
| `STOP RUN` (line 60) | Completion of the request handler | Runtime adaptation | Must not terminate the Spring Boot process. |

## Excluded functionality

None of the program's paragraphs or business behavior is excluded. No DAO is
created because the source has no file, database, or external-service access.

## Intentional differences

- The standalone terminal program is exposed as a read-only HTTP GET endpoint
  returning JSON with a `report` string. Its fixed sample employees remain
  source-defined; no request-based payroll calculation is added (D1).
- Report text is joined with LF line separators at the HTTP boundary (D3).
- Decimal calculations use Java `BigDecimal` and half-up rounding at explicit
  two-decimal `ROUNDED` destinations for the positive sample values. Exact
  compiler/runtime equivalence remains unverified (D2).
- No source output translation is needed because all displayed headings and
  labels are already English (D4).

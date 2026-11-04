# Program analysis

## Evidence and purpose

`COBOL_PROGRAM/PayrollCalculation.CBL` is a free-format COBOL program with
`PROGRAM-ID. PayrollCalculation` (lines 1-3). Its comments describe an educational
example with fixed sample data and illustrative deduction rates, not actual
payroll rules (lines 5-8). It has no caller-provided input. It initializes three
employees, calculates monthly pay, and displays employee and aggregate reports
(lines 17-21, 49-60).

The source is UTF-8 without a BOM. The comment suggests GnuCOBOL and `-free`
(lines 7-8), but no exact compiler dialect/version is established. The comment's
example input filename ends in `.txt`; this session's source is `.CBL`. No COBOL
compiler is available in the environment.

## Data definitions

| Source field / record | PIC, length, scale, encoding | Initialization / conditions | Proposed Java representation |
| --- | --- | --- | --- |
| `employee-count` | Level 78 constant, value 3 (line 17) | Fixed sample count | Constant and three immutable per-call employee records |
| `monthly-work-hours` | Level 78 constant, value 160 (line 18) | Fixed calculation divisor | Integer constant |
| `overtime-multiplier` | Level 78 constant, value 1.50 (line 19) | Fixed sample rate | `BigDecimal` constant |
| `insurance-rate`, `tax-rate` | Level 78 constants, 0.14 and 0.15 (lines 20-21) | Illustrative source rates | `BigDecimal` constants |
| `employee-id` | `PIC 9(4)`, four unsigned display digits (line 25) | Initialized to 1001, 1002, 1003 (lines 64, 71, 78) | Integer; four-digit display at report boundary |
| `employee-name` | `PIC X(30)`, 30 character positions (line 26) | Names moved into fixed-width fields (lines 65, 72, 79); report trims both ends (line 126) | String with report-boundary trim |
| `department` | `PIC X(20)`, 20 character positions (line 27) | Departments moved into fixed-width fields (lines 66, 73, 80); report trims both ends (line 127) | String with report-boundary trim |
| `base-salary` | `PIC 9(7)V99`, nine digits, scale 2 (line 28) | Sample values 45000, 38000, 32000 (lines 67, 74, 81) | `BigDecimal` scale 2 |
| `overtime-hours` | `PIC 9(3)`, three unsigned display digits (line 29) | Sample values 8, 4, 0 (lines 68, 75, 82) | Integer; three-digit display |
| `performance-score` | `PIC 9`, one unsigned digit (line 30) | Sample values 5, 4, 3 (lines 69, 76, 83) | Integer |
| `bonus-rate` | `PIC 9V99`, three digits, scale 2 (line 40) | Set by score branch for each employee (lines 93-97) | Per-calculation `BigDecimal` |
| `bonus-amount`, `overtime-pay` | `PIC 9(7)V99`, nine digits, scale 2 (lines 31-32) | Calculated per employee (lines 98-102) | `BigDecimal` scale 2 |
| `gross-salary`, `insurance-amount`, `tax-amount`, `net-salary` | `PIC 9(8)V99`, ten digits, scale 2 (lines 33-36) | Calculated per employee (lines 103-113) | `BigDecimal` scale 2 |
| `taxable-income` | `PIC 9(8)V99`, ten digits, scale 2 (line 41) | Set per employee (lines 108-109) | Local `BigDecimal` scale 2 |
| Totals and `average-net` | Four `PIC 9(10)V99` fields (lines 42-45) | Totals start at zero; average set in summary (lines 42-45, 115-122, 147) | Per-invocation `BigDecimal` scale 2 |
| `formatted-amount` | `PIC Z(9)9.99`, 13 positions with a decimal point and two fractional digits (line 46) | Reused for displayed monetary amounts (lines 130-157) | Locale-independent decimal formatter with leading zero suppression |
| `report-separator` | `PIC X(50)` (line 47) | Initialized to 50 hyphens; displayed as report divider (lines 85-90, 144, 160) | Constant string of 50 hyphens |

The employee table has three occurrences and is initialized before sample values
are assigned (lines 23-24, 62-83). There are no REDEFINES, OCCURS DEPENDING ON,
level-88 conditions, signed or computational numeric fields, or file records.

## Selected control flow and dependency closure

The complete entry point `main-program` runs `prepare-sample-data`,
`print-report-header`, loops employee indexes from 1 through `employee-count`,
and for each one calls `calculate-salary`, `update-totals`, and
`print-employee-report`; it then calls `print-summary-report` and `STOP RUN`
(lines 49-60).

Every paragraph is in scope. `prepare-sample-data` supplies all source-defined
inputs (lines 62-83). `calculate-salary` computes the selected employee's
payroll values (lines 92-113). `update-totals` accumulates gross, deductions,
and net and tracks the highest net pay (lines 115-122). The two print paragraphs
produce the employee and summary portions of the observable report (lines
124-160); the header paragraph emits the initial heading (lines 85-90).
`STOP RUN` becomes completion of an HTTP request, never termination of the
Spring application.

There are no transaction dispatches, PERFORM THRU ranges, GO TO statements,
fall-through control transfers, or external cleanup operations. Every result and
accumulator will be local to a report-generation invocation so simultaneous and
repeated requests cannot share working storage.

## External dependencies and I/O

The source has no `COPY`/`REPLACE`, `CALL`, SQL, CICS, external files, or
environment-provided data. Its only output is terminal `DISPLAY` (lines 85-90,
124-160). No DAO is applicable.

The Java adapter is `GET /api/payroll/report`, with no request body or query
parameters. It returns HTTP 200 and a JSON response DTO with one `report` string,
containing the complete report lines in source order. The report retains the
source's English labels, 50-hyphen separators, sample data, and numeric display
format. It uses LF between lines. No client input is validated because the
source accepts no client input; no arbitrary filesystem path is exposed.

## Business rules and semantic risks

- Performance 5 earns a 15% bonus, score 4 earns 10%, and all other scores earn
  zero bonus (lines 93-97).
- Overtime pay is base salary divided by 160 monthly work hours, times overtime
  hours and 1.50 (lines 100-102).
- Gross pay is base plus bonus plus overtime pay (lines 103-105).
- Insurance is 14% of gross; taxable income is gross less insurance; tax is 15%
  of taxable income; net is taxable income less tax (lines 106-113).
- `ROUNDED` appears on bonus, overtime pay, insurance, tax, and average-net
  assignments (lines 98-102, 106-107, 110-111, 147). Decimal destinations have
  scale 2. For the positive sample inputs the Java implementation will use
  half-up rounding to two places. Exact compiler intermediate precision,
  numeric overflow diagnostics, and any non-sample negative-input behavior are
  unverified because no COBOL runtime/version is available (decision D2).
- Totals are gross, insurance plus tax, and net; the average is total net divided
  by three and rounded to two decimal places. Highest-paid selection updates
  only for a strictly greater net salary, so a tie retains the first employee
  encountered (lines 115-122, 147-159).
- COBOL numeric DISPLAY fields have fixed widths (`PIC 9(4)`, `PIC 9(3)`, and
  `PIC 9`) and monetary display uses `PIC Z(9)9.99` (lines 25, 29-30, 46).
  Preserve zero padding for the integer display fields and suppress leading
  zeroes in monetary output. The amount picture has no grouping separator.
- The source output strings and headings are already English (lines 87-89,
  125-160); no wording or business-rule translation is needed.
- The response wraps the console report in JSON and uses LF line separators;
  these are documented transport adaptations (decision D3). No currency
  conversion is implied: the header says TRY (line 88).

## English output translations

| Source location | English replacement | Layout / compatibility effect | Decision reference |
| --- | --- | --- | --- |
| Lines 87-89, 125-160 | No text changes; source labels are already English. | None to report wording or line layout. | D4 |

## Java API and data-access contracts

| Use case | Method and path | Request / validation | Response / status / errors | DAO contract |
| --- | --- | --- | --- | --- |
| Generate the complete fixed-sample payroll report | `GET /api/payroll/report` | No body or parameters; no request validation. All sample inputs come from source-defined constants. | `200 OK`, JSON `PayrollReportResponse` containing a `report` string; errors use the application's standard problem response. | Not applicable: source performs no data access. |

The report DTO contains the complete report, not a partial or mocked success.
Every invocation constructs fresh calculation state. Monetary values use decimal
arithmetic; display formatting is fixed and locale-independent. There is no
external configuration or persistence contract.

## Verification design

Source-derived test expectations (not observed COBOL outputs):

| Scenario | Expected result and evidence source |
| --- | --- |
| Default report | Includes 3 employees and the complete source labels in source order; source lines 17, 49-60, 85-160. |
| Employee A | Bonus 6750.00, overtime 3375.00, gross 55125.00, insurance 7717.50, tax 7111.13, net 40296.37; source-derived from lines 64-69 and 92-113, applying D2. |
| Employee B | Bonus 3800.00, overtime 1425.00, gross 43225.00, insurance 6051.50, tax 5576.03, net 31597.47; source-derived from lines 71-76 and 92-113, applying D2. |
| Employee C | Bonus and overtime 0.00, gross 32000.00, insurance 4480.00, tax 4128.00, net 23392.00; source-derived from lines 78-83 and 92-113, applying D2. |
| Summary | Gross 130350.00, deductions 35064.16, net 95285.84, average net 31761.95; highest net employee is Sample Employee A; source-derived from lines 115-122 and 146-160, applying D2. |
| Repeated/concurrent calls | Each response is identical and has no cross-request state; architecture requirement derived from per-invocation COBOL working storage. |
| HTTP contract | GET succeeds with JSON response containing complete report text; source I/O adapted per D1/D3. |

No expected output in this analysis is claimed to have been observed from COBOL.

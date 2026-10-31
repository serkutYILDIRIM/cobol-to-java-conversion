# COBOL-to-Java mapping

| COBOL location / responsibility | Java class / method / contract | Selected or supporting | Evidence / decision |
| --- | --- | --- | --- |
| 0000-MAINLINE (L129-134) | `CustomerTransactionService.process` | Selected | D1 |
| 1000-INITIALIZE: open files, header (L136-157) | `AccountDao.open`, `Run.initialize`, `ReportFormatter.header` | Selected | Q1, Q6 |
| 1100-READ-NEXT-TRANS (L159-164) | `TransactionParser.parse`, read counter in `Run.processTransaction` | Selected | Q2 |
| 2000-PROCESS-TRANSACTIONS (L166-185) | `Run.processTransaction` | Selected | source |
| 2100-APPLY-TRANSACTION (L187-204) | `Run.apply` | Selected | Q4, Q6, Q7 |
| 2200-UPDATE-MASTER-AND-REPORT (L206-227) | `Run.updateMasterAndReport`, `AccountDao.Session.rewrite`, `ReportFormatter.detail` | Selected | Q4, Q5 |
| 2300 / 2400 error logging (L229-239) | `Run.logError`, `ReportFormatter.error` | Selected | source |
| 3000-TERMINATE (L241-251) | `Run.terminate` (DISPLAY lines to `consoleMessages`) | Selected | Q2 |
| TRANS-REC / TRANS-FILE | `Transaction`, `TransactionParser` (multipart upload) | Selected | Q2 |
| MASTER-REC / MASTER-FILE (VSAM) | `Account`, `FixedWidthAccountDao`, `PackedDecimal` | Selected | Q1, Q3 |
| REPORT-FILE / ERROR-FILE | `reportLines` / `errorLines` in the response | Selected | Q2 |
| `PIC ----,--9.99` | `ReportFormatter.editBalance` | Supporting | Q7 |
| Truncation of arithmetic results, 9(5) counters | `NumericRules` | Supporting | Q7 |
| `FUNCTION CURRENT-DATE(1:8)` | injected `Clock` + configured zone | Supporting | analysis |
| STOP RUN on open failure | `MasterFileException` -> HTTP 503 | Supporting | Q6 |

## Excluded functionality

None from the program. Interest computation is mentioned only in the header comment
and has no code, so nothing was implemented. `TR-DATE` is unused in the source.

## Intentional differences

Decisions below were made autonomously under the user's instruction to answer all
open questions and proceed; they are not COBOL-observed facts.

- Q1: master stored as a fixed-width binary file with packed-decimal balance (a stand-in for VSAM KSDS, no alternate indexes).
- Q2: HTTP multipart batch; report, errors, and DISPLAY output are returned in JSON instead of files/SYSOUT.
- Q3: 151-byte layout derived from the field definitions, not the declared 150.
- Q4: unknown transaction types preserved (balance unchanged, rewritten, reported with the previous report type, blank initially).
- Q5: 'SERVICE FE' truncation preserved.
- Q6: repeated overdraft fee preserved. STOP RUN/open failure becomes HTTP 503; no RETURN-CODE exists. Non-80-character or non-numeric records are rejected with HTTP 400 (source behavior undefined).
- Q7: silent truncation preserved (balances 9 integer digits, totals 11, edited fields 6, counters 5 digits).
- Deposit total DISPLAY is shown as 13 zero-padded unscaled digits without sign overpunch (real COBOL format unverified).
- Header date is `YYYYMMDD` plus two spaces, using the configured time zone (UTC by default).

# COBOL-to-Java mapping

All source references refer to
[InventoryManagement.CBL](../../../COBOL_PROGRAM/InventoryManagement.CBL).

| COBOL responsibility | Java counterpart | Scope / evidence |
| --- | --- | --- |
| 0000-MAIN, 185-190 | InventoryController.run -> InventoryService.run | Full execution, D1/D2 |
| Constants and tables, 26-96 | EmbeddedInventory, Product, Transaction | Exact literals and substring offsets, source data preserved |
| 1000-INITIALIZE, 195-196 | InventoryService.process local variables and fresh product list | New program state per call |
| 2000-PROCESS-TRANSACTIONS, 201-222 | InventoryService.process transaction loop | Source order and counter updates |
| 2100-FIND-PRODUCT, 224-234 | InventoryService first matching product index | First-match SEARCH |
| 2200-VALIDATE-TRANSACTION, 236-253 | InventoryService.rejectionReason | Missing/inactive/type/stock precedence |
| 2300-APPLY-TRANSACTION, 255-269 | InventoryService.process type switch | R, S, A; signed input; decimal receiving widths |
| 2900-REPORT-REJECTION, 271-277 | InventoryReportFormatter transaction log | Fixed signs, field widths and reason padding |
| 3000/3100 report, 282-319 | InventoryService product valuation; InventoryReportFormatter details | Includes inactive stock in value, excludes it from low count |
| 4000 summary, 324-352 | InventoryService summary; InventoryReportFormatter summary | HALF_UP tax, counts, gross and final action |
| Numeric pictures and DISPLAY, 130-178 | InventoryReportFormatter | US decimal/grouping, padding, LF adaptation |
| HTTP transport (new) | InventoryRunResponse and InventoryExceptionHandler | D2/D5; generic ProblemDetail failures with internal logging |

## Excluded functionality

None from INVMGMT. DAO is not applicable because the source has no external data
access. No persistence, arbitrary inputs, new stock rules, or unrelated programs.

## Intentional differences

- STOP RUN returns a response rather than terminating the Java server.
- Fresh state per request models a new invocation, not persistent inventory.
- Report lines are transported as JSON text with LF rather than native terminal
  DISPLAY line endings. Original widths and trailing spaces are retained.
- Structured JSON is additional output; source headings are already English.
- Decimal overflow behavior is source-derived under D4, not runtime-confirmed.

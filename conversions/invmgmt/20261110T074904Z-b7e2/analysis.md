# INVMGMT analysis

## Evidence and purpose

Source: [InventoryManagement.CBL](../../../COBOL_PROGRAM/InventoryManagement.CBL).
Line numbers below refer to that unchanged file. Fixed-format, ASCII-compatible
COBOL 85 is declared in lines 1-19. PROGRAM-ID is INVMGMT (line 11).
The program processes embedded stock movements and displays a report. There are
no arguments, interactive inputs, external files, COPY/REPLACE, CALL, SQL, CICS,
dates, file statuses, or EOF handling. No missing business dependency was found.
No compatible COBOL compiler is installed; all expectations are source-derived.

## Data definitions

| Source | Layout and initialization | Java representation |
| --- | --- | --- |
| Constants, 26-29 | 10 products, 12 transactions, implied-decimal V99 tax .18 | Immutable embedded data; BigDecimal("0.18") |
| Product literals, 35-55 | Ten X(43) records; each literal verified to be 43 ASCII characters | Exact strings parsed at original offsets |
| Product REDEFINES/OCCURS, 57-67 | X(5) code, X(20) name, 9(5) stock, 9(5) reorder, 9(5)V99 price, X(1) status | Immutable Product record; int quantities; BigDecimal scale 2 |
| Status level-88, 66-67 | A active; I inactive | String comparison; only I triggers inactivity |
| Transaction literals, 74-86 | X(12); original literals are 12 characters | Exact embedded strings |
| Transaction overlay, 88-96 | X(5) code, X(1) type, S9(5) SIGN LEADING SEPARATE (6 bytes) | Transaction record with int signed quantity |
| Type level-88, 92-94 | R receipt, S sale, A adjustment | Exact character values; unknown types rejected |
| Counters, 101-106 | Unsigned 3-digit counts and 7-digit received/sold; zero initialized | Per-run int values with receiving-field width semantics |
| Amounts, 108-112 | Unsigned 9(9)V99, zero initialized | BigDecimal scale 2, receiving width 9 integer digits |
| Flags/reason, 117-125 | Per-transaction flags; X(30) rejection reason | Local control flow and padded output reason |
| Report, 130-178 | Fixed widths, edited Z/9 numeric pictures, spaces | Separate InventoryReportFormatter |

No COMP, COMP-3, nested OCCURS, binary encoding, or external record encoding exists.
REDEFINES is represented by parsing exact substrings, not whitespace splitting.
Names retain their 20-character padding in domain and JSON output.

## Selected control flow and dependency closure

Full-program scope includes every paragraph:

1. 0000-MAIN (185-190) performs initialization, transactions, inventory, summary,
   then STOP RUN. Java returns one completed result, never exits the server.
2. 1000-INITIALIZE (195-196) zeroes counters/amounts. Each HTTP invocation also
   starts from original products, corresponding to a fresh program execution.
3. 2000-PROCESS-TRANSACTIONS (201-222) loops 1 through 12 in source order.
   Flags/reason reset each iteration.
4. 2100-FIND-PRODUCT (224-234) is a first-match linear SEARCH from index 1;
   missing product rejects without further validation.
5. 2200-VALIDATE-TRANSACTION (236-253) rejects in order: inactive product,
   unknown type, sale greater than available stock. No positivity rule exists.
6. 2300-APPLY-TRANSACTION (255-269) adds receipts and adjustments, subtracts
   sales, totals only receipts/sales, and displays the original signed input.
7. 2900-REPORT-REJECTION (271-277) formats rejected inputs without changing stock.
8. 3000-PRINT-INVENTORY-REPORT (282-295) prints all ten products in table order.
9. 3100-BUILD-DETAIL-LINE (297-319) rounds item values, adds net inventory,
   then chooses INACT before LOW (quantity <= reorder) before OK.
   Inactive stock contributes to value but never increments low-stock count.
10. 4000-PRINT-SUMMARY (324-352) rounds net * .18 to cents, adds gross,
    prints five counts and three amounts, then the low-stock action message.

PERFORMs return to their callers; STOP RUN prevents paragraph fall-through.
No GO TO, THRU, recursion, cleanup routine, or retained inter-run state applies.
Nothing in this program is excluded.

## Business rules and semantic risks

- Preserve validation precedence and even unusual signed transaction behavior;
  do not add positive-only receipt/sale or nonnegative-adjustment rules.
- Decimal ROUNDED uses HALF_UP; gross addition is exact at two decimals.
- Decimal receiving-field behavior is modeled as absolute value followed by
  low-order digit truncation for unsigned fields (stock 5, movement totals 7,
  amounts 9 integer digits). This is an explicit COBOL-85 compatibility assumption,
  not observed runtime evidence. The supplied batch does not overflow.
- CL-VALUE has only six digits despite seven-digit movement accumulators;
  report conversion retains the last six digits.
- Pictures: ZZZ,ZZ9 width 7; ZZ,ZZ9 width 6; ZZ,ZZ9.99 width 9;
  ZZZ,ZZZ,ZZ9.99 width 14. Reject picture -ZZ,ZZ9 width 7 uses a fixed
  leading minus position, blank for positive input. Zero keeps its final 9 digit.
- Preserve the exact X(14) 'REJECTED TRN: ' prefix and the 53-character
  title, 78-character header/separator, and 79-character detail layout. The
  separator is shorter than detail lines in the source; do not silently fix it.
- REST report uses LF line endings and retains trailing padding and final LF.
  Terminal-specific DISPLAY line endings are an intentional transport adaptation.
- All source messages are already English; no translation is necessary.

## Java API and data-access contracts

Decision D2 chooses the closest source contract: no client-supplied business data.

| Use case | Method/path | Request/validation | Response/errors | Data access |
| --- | --- | --- | --- | --- |
| Execute complete program | POST /api/inventory/runs | No body, query parameters, or configurable paths | 200 JSON InventoryRunResponse; unexpected failures yield generic 500 ProblemDetail and server-side error log | Not applicable: source uses embedded working storage, not files/DB |

Response: products (code, padded name, quantity, reorderLevel, unitPrice, status,
stockValue, flag); transactions (code, type, signed quantity, applied boolean,
rejectionReason, empty for applied); summary (processed, rejected, totalReceived,
totalSold, lowStockCount, netValue, taxAmount, grossValue); report (complete text).
No DAO, database, file-upload contract, persistence, or invented business inputs
are introduced. No Clock is needed because the program has no dates.
Records are immutable and all mutable work is local to a run.

## Verification design

Source-derived expected batch:

- Final quantities: 40, 85, 18, 50, 55, 42, 15, 7, 8, 80.
- Applied 8, rejected 4; received 70, sold 488; active low-stock count 6.
- Rejections in order: P1003 insufficient stock, P9999 not found, P1007
  invalid type, P1009 inactive.
- Net 27676.07; tax 4981.69; gross 32657.76 (independently summed from item values).

Tests will independently check fixed-width embedded field boundaries, all dispatch
branches and rejection precedence, equality at reorder/sale thresholds, signed
quantities, unsigned truncation, tax half-cent rounding, inactive value inclusion,
report columns/padding, both action messages, repeated and concurrent runs, and
real HTTP serialization. No DAO test is applicable without data access.

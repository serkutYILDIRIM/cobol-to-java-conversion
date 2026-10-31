# Program analysis

## Evidence and purpose

Source: `COBOL_PROGRAM/CUSTPROC.CBL`, fixed-format, IBM-ZOS Enterprise COBOL (L10-11, L154).
Purpose (L5-8): read transactions, validate against a VSAM master, apply deposits,
withdrawals, and fees, write a report and an error log. The header text mentions
"interest" but no interest computation exists in the code (observed fact).
Only one section (`0000-MAINLINE SECTION`, L129) exists; all paragraphs belong to it.
No COPY, CALL, SQL, or CICS. No sample data or JCL/DD definitions were supplied.

## Data definitions

| Source field / record | PIC, length, scale, encoding | Initialization / conditions | Proposed Java representation |
| --- | --- | --- | --- |
| TRANS-REC (L41-49) | 80 bytes: `TR-CUST-ID` X(10), `TR-TYPE` X(1), `TR-AMOUNT` 9(7)V99 (9 digit chars, unsigned display), `TR-DATE` X(8), filler X(52) | 88: D/W/F (L44-46). `TR-DATE` is never used. Amount is not validated as numeric | Record with `String customerId`, `char type`, `BigDecimal amount` (scale 2, >= 0), fixed-width parser |
| MASTER-REC (L53-61) | `MSTR-CUST-ID` X(10) key, `MSTR-NAME` X(30), `MSTR-CURR-BAL` S9(9)V99 COMP-3 (6 bytes), `MSTR-ACCT-STATUS` X(1), filler X(104). Sum = 151 vs `RECORD CONTAINS 150` | 88: 'A' active, 'F' frozen; other values fall to WHEN OTHER = treated as active | `CustomerAccount(id, name, BigDecimal balance scale 2, status)` |
| REPORT-REC (L66) | X(133) | Header col 1 = '1' (page-eject carriage control) | Fixed-width line formatter, 133 chars |
| ERROR-REC (L71) | X(80) | - | Fixed-width, 80 chars |
| WS-FILE-STATUSES (L75-79) | 4 x X(2) | no initial value | Mapped to DAO exceptions |
| EOF-TRANS-SW / MSTR-FOUND-SW (L82-86) | X(1), 'N' initially | 88 EOF-TRANS, MSTR-FOUND | local variables per run |
| WS-RECS-READ / PROC / ERR-COUNT (L89-92) | 9(5) COMP, 0 | binary, truncation behavior of >99999 unspecified in code | per-run counters (`int`/`long`), overflow rule Q7 |
| WS-TOTAL-DEPOSITS (L93) | S9(11)V99 COMP-3 | 0 | `BigDecimal` per run |
| WS-NEW-BALANCE (L97) | S9(9)V99 COMP-3 | 0 | `BigDecimal` |
| WS-OVERDRAFT-FEE (L98) | 9(3)V99 COMP-3 | 35.00 | constant `new BigDecimal("35.00")` |
| RPT-HEADER (L101-107) | 1+20+40+5+10+57 = 133 | `'1'`, `'BANK TRANSACTION RPT'`, `'DATE:'` | formatter |
| RPT-DETAIL (L110-120) | 2+10+5+30+5+10+5+11+5+11 = 94 chars, written into 133-byte record (space padded by WRITE FROM) | `RPT-TYPE`, ids have no VALUE | formatter |
| `RPT-OLD-BAL`/`RPT-NEW-BAL` PIC `----,--9.99` | 11 chars; floating minus; max 6 integer digits shown | - | explicit edited-numeric formatter with truncation per Q7 |
| ERR-DETAIL (L123-126) | 10 + ': ' + 68 = 80 | - | formatter |

## Selected control flow and dependency closure

Scope is the full program, so every paragraph is included.

1. `0000-MAINLINE` (L129-134): `1000-INITIALIZE`; loop `2000-PROCESS-TRANSACTIONS` until EOF; `3000-TERMINATE`; GOBACK.
2. `1000-INITIALIZE` (L136-157): open TRANS (failure: DISPLAY, RC=16, STOP RUN); open MASTER I-O (failure: DISPLAY, STOP RUN, RC not set, TRANS left open); open REPORT and ERROR outputs (status never checked); date `CURRENT-DATE(1:8)` (YYYYMMDD) into 10-char `RPT-HDR-DATE`; write header; prime-read.
3. `1100-READ-NEXT-TRANS` (L159-164): READ; AT END sets EOF; else counts a record read. Non-EOF I/O errors are not handled.
4. `2000-PROCESS-TRANSACTIONS` (L166-185): set found, move key, random READ master; INVALID KEY -> not found. Dispatch: not found -> 2300; frozen -> 2400; otherwise -> 2100. Then read next.
5. `2100-APPLY-TRANSACTION` (L187-204): D adds to balance and to total deposits; W subtracts, and if result < 0 displays 'OVERDRAFT FOR CUST: id' and subtracts 35.00; F subtracts amount (no overdraft check). Any other type: balance unchanged. Then 2200.
6. `2200-UPDATE-MASTER-AND-REPORT` (L206-227): format balances, REWRITE master (INVALID KEY only DISPLAYs; processing continues and the report line is still written), set type text, write detail, count processed.
7. `2300` / `2400` (L229-239): error line, error count.
8. `3000-TERMINATE` (L241-251): DISPLAY five summary lines, close all files.

Excluded: nothing. Unreferenced: `TR-DATE`, `WS-MSTR-STATUS` after open, `WS-REPT-STATUS`, `WS-ERR-STATUS`.

## External dependencies and I/O

| File | DD / assign | Org | Notes |
| --- | --- | --- | --- |
| TRANS-FILE | UT-S-TRANS | sequential, FB 80 | input; DAO streams records; closes in service/DAO try-with-resources |
| MASTER-FILE | DISK-MASTER | indexed (VSAM KSDS), random, I-O | key `MSTR-KEY`; read by key and rewritten. **Real data/schema not supplied** |
| REPORT-FILE | UT-S-REPORT | sequential, FB 133 | output |
| ERROR-FILE | UT-S-ERRORS | sequential, FB 80 | output |
| SYSOUT | DISPLAY | - | overdraft notices and 5 summary lines |

Missing artifacts: VSAM definition (key offset, alternate indexes, sharing), sample files, JCL, compiler options (TRUNC, NUMPROC, CODEPAGE), runtime for comparison.

## Business rules and semantic risks

- Frozen status check precedes transaction type; status other than 'A'/'F' behaves as active.
- Deposit/withdrawal/fee arithmetic has no ON SIZE ERROR: results exceeding 9 integer digits truncate silently (Q7).
- Withdrawal overdraft fee applied whenever the new balance is negative, even if it was negative before (Q6).
- Fees may drive balance negative without penalty.
- Unknown `TR-TYPE` is rewritten and reported with a stale or undefined `RPT-TYPE` (Q4).
- 'SERVICE FEE' truncated to 'SERVICE FE' (Q5).
- Edited balance shows at most 6 integer digits; larger values lose high-order digits (Q7).
- Header date: 8 digits YYYYMMDD in a 10-char field, right-padded with 2 spaces (no separators). Uses the system clock/time zone of the run; Java will inject `Clock` with a defined zone.
- Duplicate customers in one run see the updated balance because the master is rewritten each time.
- `WS-RECS-READ` etc. are 5-digit counters; no handling above 99999.
- MASTER-REC layout is 151 bytes vs. declared 150 (Q3).
- Open failure on master sets no return code; trans failure sets 16 (Q6).
- DISPLAY of the packed deposit total shows implied decimal without a decimal point, possibly with sign overpunch; exact console format unverified without a COBOL run.
- Header comment mentions interest, but none is computed; not implemented.

## English output translations

| Source location | English replacement | Layout / compatibility effect | Decision reference |
| --- | --- | --- | --- |
| All literals (L137-248) | Already English | None | - |

No translation is required. Q5 may change a literal.

## Java API and data-access contracts

Proposed, pending Q1 and Q2:

| Use case | Method and path | Request / validation | Response / status / errors | DAO contract |
| --- | --- | --- | --- | --- |
| Process transaction batch | `POST /api/v1/customer-transactions/batch` | Multipart file of 80-byte fixed-width records (or JSON list; Q2); each record validated for length 80 | 200 with report lines, error lines, counters, total deposits; 400 invalid record layout; 500/503 data-store failure via `ProblemDetail` | `AccountDao.findById`, `AccountDao.update` (Q1); trans input parsed from request stream, no arbitrary file paths |

Per-request state (counters, totals, EOF) is held in a per-run object, never in singleton fields.
STOP RUN on open failure becomes a domain exception, never a JVM exit.

## Verification design

| Case | Expected | Evidence |
| --- | --- | --- |
| Deposit active | balance + amount, total deposits updated, report line | source |
| Withdrawal within balance | balance - amount, no fee | source |
| Withdrawal causing negative | balance - amount - 35.00 | source |
| Withdrawal on already negative balance | extra 35.00 (Q6) | source |
| Fee transaction | balance - amount, no overdraft fee; type 'SERVICE FE' or per Q5 | source |
| Frozen / not found | 80-char error line, error count; master unchanged | source |
| Unknown type | per Q4 | pending |
| Empty transaction file | header only, zero counters | source |
| Duplicate customer sequence | second sees updated balance | source |
| Boundary: 9-digit balance, 6-digit edited display, 99999 counters | per Q7 | pending |
| Concurrent runs | independent counters/totals | source |
| Header date with fixed Clock | `YYYYMMDD` + 2 spaces | source |

Expected values are source-derived, not observed COBOL output.

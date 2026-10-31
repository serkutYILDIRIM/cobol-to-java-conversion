# Conversion session

Status: Java generated; Maven verification NOT run (user instruction)

## Identity

- Session ID: 20261031T193220Z-c5d1
- Source path: `COBOL_PROGRAM/CUSTPROC.CBL` (251 lines, unchanged)
- PROGRAM-ID: CUSTPROC
- Source SHA-256: CD6E88F2B65F852FBE358010215C8D4B28004B497914C7253C31FB494260F231
- Dependency paths and hashes: none (no COPY, CALL, SQL, or CICS found)
- Compiler/dialect and encoding: IBM Enterprise COBOL for z/OS assumed (SOURCE-COMPUTER IBM-ZOS, `FUNCTION CURRENT-DATE`); EBCDIC vs. ASCII of real data files unresolved; fixed-format source

## User-selected scope

- Mode: full
- Selected identifiers: all (0000-MAINLINE and every paragraph through 3000-TERMINATE)
- User's scope statement: "Full program" (answered in this session)
- Required supporting behavior: all paragraphs; file I/O for four files
- Explicit exclusions: none

## Decisions and open questions

| ID | Source evidence / question | User answer or established requirement | Status / effect |
| --- | --- | --- | --- |
| D1 | Scope selection | Full program | Resolved |
| Q1 | MASTER-FILE is VSAM KSDS (L22-26). What is the Java-side master store: fixed-width 150-byte record file, relational DB (which one/schema), or other? | Decided autonomously by assistant on user instruction (see mapping.md, Intentional differences) | Resolved |
| Q2 | Transport: HTTP endpoint accepting a multipart/structured batch of transactions and returning report + error log + totals, or a CLI/batch job on configured files? | Decided autonomously by assistant on user instruction (see mapping.md, Intentional differences) | Resolved |
| Q3 | MASTER-REC fields sum to 151 bytes (10+30+6+1+104) but `RECORD CONTAINS 150` (L52). Which is authoritative? | Decided autonomously by assistant on user instruction (see mapping.md, Intentional differences) | Resolved |
| Q4 | Unknown `TR-TYPE` (not D/W/F): source rewrites unchanged balance, reports it, reuses stale `RPT-TYPE` (L220-224). Preserve, or reject to error log? | Decided autonomously by assistant on user instruction (see mapping.md, Intentional differences) | Resolved |
| Q5 | `RPT-TYPE` is X(10) but 'SERVICE FEE' is 11 chars -> 'SERVICE FE' in source. Preserve truncation or widen/shorten (layout change)? | Decided autonomously by assistant on user instruction (see mapping.md, Intentional differences) | Resolved |
| Q6 | Overdraft fee applies on every withdrawal ending below 0, even if already overdrawn (L193-196). Preserve? Also open-failure of master sets no RETURN-CODE (L145-148). | Decided autonomously by assistant on user instruction (see mapping.md, Intentional differences) | Resolved |
| Q7 | Numeric overflow/truncation (balance beyond 9 integer digits, edited fields, 5-digit counters) is silent in source. Preserve truncation or fail? | Decided autonomously by assistant on user instruction (see mapping.md, Intentional differences) | Resolved |

## Progress

- Analysis: complete (see `analysis.md`)
- Contract definition: complete
- Implementation: complete (unbuilt)
- Verification: not run (tests written, not executed)
- Remaining limitations: no COBOL runtime or sample VSAM/transaction data supplied; equivalence cannot be claimed

# Program analysis

## Evidence and purpose

Source: `COBOL_PROGRAM/HELLO_FROM_IBM.CBL`.

- Lines 1-2 identify a COBOL program with `PROGRAM-ID. HELLO`.
- Lines 4-5 contain an `ENVIRONMENT DIVISION` and `CONFIGURATION SECTION`, with no environment entries.
- Lines 7-9 define one working-storage field.
- Lines 11-14 define the sole procedure paragraph, `MAIN-LOGIC`, which displays the initialized message and executes `STOP RUN`.

The observed purpose is to write a fixed text message to the COBOL display output and terminate. No conversion scope has been selected, so this is not yet a Java implementation contract.

## Data definitions

| Source field / record | PIC, length, scale, encoding | Initialization / conditions | Proposed Java representation |
| --- | --- | --- | --- |
| `OGRENCI-MESAJ` (line 8) | `PIC X(30)`; 30 alphanumeric character positions; encoding unresolved | `VALUE 'HELLO FROM IBM COBOL'` | Pending selected scope and output contract |

## Selected control flow and dependency closure

No scope is selected. The only available paragraph candidate is `MAIN-LOGIC` (lines 12-14). Its dependency closure comprises `OGRENCI-MESAJ` because line 13 displays that field. Execution terminates at `STOP RUN` (line 14). There are no `PERFORM`, `GO TO`, calls, loops, branches, or fall-through paths in the source.

## External dependencies and I/O

No `COPY`, `CALL`, SQL, CICS, file-control entries, file I/O, or transaction dispatch mechanism appears in the source. The only observed output is the `DISPLAY` statement on line 13. The destination and line-termination semantics remain compiler/runtime dependent.

## Business rules and semantic risks

The source initializes a 30-character alphanumeric field and displays it. The literal has 20 visible characters; COBOL storage padding and the exact `DISPLAY` behavior must be preserved or explicitly adapted when the Java output contract is defined. No calculations, dates, sorting, or error handling are present.

## English output translations

| Source location | English replacement | Layout / compatibility effect | Decision reference |
| --- | --- | --- | --- |
| Line 8 | No translation proposed: `HELLO FROM IBM COBOL` is already English. | Pending output contract. | Q1 |

## Java API and data-access contracts

Pending scope selection. The source has no transaction types and no external data access.

## Verification design

Pending scope and Java contract. A source-derived candidate case is that the selected execution path emits the initialized `OGRENCI-MESAJ` value and terminates; this is not an observed COBOL runtime result.

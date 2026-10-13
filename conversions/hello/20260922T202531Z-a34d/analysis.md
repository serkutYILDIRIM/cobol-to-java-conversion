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

The selected scope is the full program. Execution enters `MAIN-LOGIC` (line 12), displays `OGRENCI-MESAJ` (line 13), then terminates at `STOP RUN` (line 14). The dependency closure contains `OGRENCI-MESAJ` because it supplies the displayed value. There are no `PERFORM`, `GO TO`, calls, loops, branches, or fall-through paths in the source.

## External dependencies and I/O

No `COPY`, `CALL`, SQL, CICS, file-control entries, file I/O, or transaction dispatch mechanism appears in the source. The only observed output is the `DISPLAY` statement on line 13. The destination and line-termination semantics remain compiler/runtime dependent.

## Business rules and semantic risks

The source initializes a 30-character alphanumeric field and displays it. The literal has 20 visible characters; COBOL storage padding and the exact `DISPLAY` behavior must be preserved or explicitly adapted when the Java output contract is defined. No calculations, dates, sorting, or error handling are present.

## English output translations

| Source location | English replacement | Layout / compatibility effect | Decision reference |
| --- | --- | --- | --- |
| Line 8 | No translation proposed: `HELLO FROM IBM COBOL` is already English. | Pending output contract. | Q1 |

## Java API and data-access contracts

| Use case | Method and path | Request / validation | Response / status / errors | DAO contract |
| --- | --- | --- | --- | --- |
| Run the full `HELLO` program | `GET /api/hello` | No request body or parameters | `200 OK` with `{ "message": "HELLO FROM IBM COBOL          " }`; the string contains 30 characters and preserves the ten padding spaces implied by `PIC X(30)`. | Not applicable: the source has no external data access. |

The service is stateless. The source's `STOP RUN` is represented by completing the HTTP operation successfully; it must not terminate the server JVM.

## Verification design

- Verify the service returns exactly 30 characters: the 20-character literal followed by ten spaces. Evidence: source line 9 and `PIC X(30)`.
- Verify `GET /api/hello` returns `200 OK` and preserves the fixed-width value in its JSON response. Evidence: source lines 9 and 13, plus decision D1.
- Verify repeated requests return the same value. Evidence: no mutable state appears in the source.
- These are source-derived expectations, not observed COBOL runtime outputs.

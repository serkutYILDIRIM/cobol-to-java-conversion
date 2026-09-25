# Program analysis

## Evidence and purpose

Preliminary analysis; scope remains unselected. All line references below are to `COBOL_PROGRAM/CLIENT.CBL`. Lines 20-28 identify an IBM DB2 sample for querying and setting client connection settings. Lines 43-66 contain Identification, Data, and Procedure divisions with fixed-format-style columns. The source has ASCII-only bytes and CRLF endings; the original code page, compiler options, and DB2 runtime version are unknown. There is no declared input file or terminal read. The program calls DB2 APIs and writes console `DISPLAY` output (`:69-251`).

## Data definitions

| Source field / record | PIC, length, scale, encoding | Initialization / conditions | Proposed Java representation |
| --- | --- | --- | --- |
| `rc` (`:55`) | `PIC S9(9) COMP-5` | API return target, never inspected here | Native signed integer; exact ABI depends on DB2 definitions. |
| `errloc` (`:56`) | `PIC X(80)` | Set to query/set labels before `checkerr` (`:88-89,120-121,133-134,153-154`) | Error-operation label; COBOL move pads to width 80. |
| `listnumber` (`:59`) | `PIC S9(4) COMP-5 VALUE 4` | Passed by value to each DB2 API call | Four setting entries; ABI unresolved. |
| `default-settings` (`:61-64`) | Five occurrences, each with two `PIC S9(4) COMP-5 VALUE 0` fields | Only `default-value(1..4)` are saved and restored (`:93-97,137-141`); fifth element and `default-type` unused here | Four saved values in request-local state if entry flow is selected. |
| `SQLE-CONN-SETTING`, `SQLE-CONN-TYPE`, `SQLE-CONN-VALUE`, `SQLCA`, `SQL-*` constants | Unavailable copybooks (`:49-51`) | Used in both sections | Layout, values, and statuses unresolved. |

No `REDEFINES`, level-88 condition, decimal scale, or local `COMP-3` field appears in the entry source; missing copybooks may define them.

## Selected control flow and dependency closure

The entry section `client-pgm` (`:67-156`) sets four setting type codes (`:72-75`), queries with `sqlgqryc`, calls `checkerr`, and performs `print-query` (`:77-91`). It saves four returned values (`:93-97`), assigns four hard-coded replacement values (`:100-103`), calls `sqlgsetc` and `checkerr` (`:105-121`), queries and prints again (`:123-136`), then restores saved values with another `sqlgsetc` and `checkerr` (`:137-154`). `STOP RUN` (`:156`) prevents fall-through into the `print-query` section (`:158-251`). The latter is performed twice (`:91,136`) and ends with `EXIT`. No loop, `PERFORM THRU`, `GO TO`, transaction dispatcher, or file EOF logic appears.

If `client-pgm` is selected, `print-query` is required support because of both `PERFORM` statements, as are the DB2 query/set calls, state, and error handling. If only `print-query` is selected, its four setting values and comparison constants should be explicit inputs; query/set and restore are not required. Exact closure depends on scope and copybook definitions.

## External dependencies and I/O

`COPY "sqlenv.cbl"`, `"sql.cbl"`, and `"sqlca.cbl"` appear at `:49-51`; none was found in this workspace. Calls to `sqlgqryc`, `sqlgsetc`, and `checkerr` occur at `:82-89,114-121,127-134,147-154`; implementation/specification is unavailable. The DB2 calls pass `SQLE-CONN-SETTING` and `SQLCA` by reference and `listnumber` by value, returning `rc`; `checkerr` receives `SQLCA` and `errloc`. There are no inline SQL/CICS commands, `REPLACE`, file descriptors, or schemas in this source. DB2 client-setting state, connection ownership, concurrency, and error cleanup remain undefined without the dependencies.

## Business rules and semantic risks

The four requested replacement codes are `SQL-CONNECT-2`, `SQL-RULES-STD`, `SQL-DISCONNECT-COND`, and `SQL-SYNC-TWOPHASE` (`:100-103`). The code does not inspect `rc`; what happens on DB2 failure depends on `checkerr` (`:82-154`, D1). Restore runs only after the second query/print path, so an earlier abort might leave changed settings (D2). `print-query` uses independent `IF` checks (`:167-248`). Sentence-ending periods at `:193,210,217,221,235,242,247` end nearby `IF` statements; later explanatory or `TYPE` displays on `:194-197,211,218,222,236,243,248` are unconditional (D3). No calculations, dates, sorting, or fixed-width records appear. Console line text and spacing are observable output.

## English output translations

| Source location | English replacement | Layout / compatibility effect | Decision reference |
| --- | --- | --- | --- |
| `:69-248` | Existing displayed strings are English | No translation made; spelling/wording corrections would change observable output. | D3 if output is selected. |

## Java API and data-access contracts

| Use case | Method and path | Request / validation | Response / status / errors | DAO contract |
| --- | --- | --- | --- | --- |
| Pending selected scope | Pending | Pending | Pending | DB2 contract unresolved. |

If only `print-query` is selected, a request must supply four setting values, and a response must carry formatted text or structured descriptions. If `client-pgm` is selected, the API must define DB2 connection context, save/restore on failure, and status mapping. These choices remain open; no endpoint or data-access behavior is assumed.

## Verification design

After scope and constants are known, test matching and unmatched values and exact lines (source-derived), query/set failure behavior (dependency- and user-decision-derived), successful restore, and state isolation across repeated/concurrent requests. No COBOL output has been observed.

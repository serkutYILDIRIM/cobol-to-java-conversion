# Program analysis

## Evidence and purpose

Full-program scope selected. All line references below are to `COBOL_PROGRAM/CLIENT.CBL`. Lines 20-28 identify an IBM DB2 sample for querying and setting client connection settings. Lines 43-66 contain Identification, Data, and Procedure divisions with fixed-format-style columns. The source has ASCII-only bytes and CRLF endings; the original code page, compiler options, and DB2 runtime version are unknown. There is no declared input file or terminal read. The program calls DB2 APIs and writes console `DISPLAY` output (`:69-251`).

## Data definitions

| Source field / record | PIC, length, scale, encoding | Initialization / conditions | Proposed Java representation |
| --- | --- | --- | --- |
| `rc` (`:55`) | `PIC S9(9) COMP-5` | API return target, never inspected here | Native signed integer; exact ABI depends on DB2 definitions. |
| `errloc` (`:56`) | `PIC X(80)` | Set to query/set labels before `checkerr` (`:88-89,120-121,133-134,153-154`) | Error-operation label; COBOL move pads to width 80. |
| `listnumber` (`:59`) | `PIC S9(4) COMP-5 VALUE 4` | Passed by value to each DB2 API call | Four setting entries; ABI unresolved. |
| `default-settings` (`:61-64`) | Five occurrences, each with two `PIC S9(4) COMP-5 VALUE 0` fields | Only `default-value(1..4)` are saved and restored (`:93-97,137-141`); fifth element and `default-type` unused here | Four saved values in request-local state if entry flow is selected. |
| `SQLE-CONN-SETTING`, `SQLE-CONN-TYPE`, `SQLE-CONN-VALUE` | Unavailable `sqlenv.cbl` (`:49`); IBM Db2 11.5 docs show seven occurrences of two `PIC S9(4) COMP-5` fields | First four entries used here | Four type/value pairs; deployed copybook revision and numeric constants unverified. |
| `SQLCA`, `SQL-*` constants | Unavailable `sqlca.cbl` and `sql.cbl` (`:50-51`) | Used in both sections and `checkerr` | Error layout and constant values unresolved. |

No `REDEFINES`, level-88 condition, decimal scale, or local `COMP-3` field appears in the entry source; missing copybooks may define them.

## Selected control flow and dependency closure

The entry section `client-pgm` (`:67-156`) sets four setting type codes (`:72-75`), queries with `sqlgqryc`, calls `checkerr`, and performs `print-query` (`:77-91`). It saves four returned values (`:93-97`), assigns four hard-coded replacement values (`:100-103`), calls `sqlgsetc` and `checkerr` (`:105-121`), queries and prints again (`:123-136`), then restores saved values with another `sqlgsetc` and `checkerr` (`:137-154`). `STOP RUN` (`:156`) prevents fall-through into the `print-query` section (`:158-251`). The latter is performed twice (`:91,136`) and ends with `EXIT`. No loop, `PERFORM THRU`, `GO TO`, transaction dispatcher, or file EOF logic appears.

For the selected full program, both sections are required. The dependency closure includes four setting type codes, four values from each query, original-value capture, replacement codes, two set operations, `SQLCA` plus `checkerr` for every external call, and all `DISPLAY` output. `print-query` needs the queried values and copybook constants. External API and error semantics remain unresolved pending the copybooks, `checkerr`, and target Db2 context.

## External dependencies and I/O

`COPY "sqlenv.cbl"`, `"sql.cbl"`, and `"sqlca.cbl"` appear at `:49-51`; none was found in this workspace. Calls to `sqlgqryc`, `sqlgsetc`, and `checkerr` occur at `:82-89,114-121,127-134,147-154`. A public IBM `checkerr.cbl` sample is preserved under `references/`; it may differ from the actual linked version. Its `Checkerr` section returns immediately on SQLCODE 0, writes an error report for nonzero SQLCODE, continues after positive warnings, and stops the program after negative errors. It also calls `sqlgintp` and `sqlggstt` for Db2 error and SQLSTATE messages. The Db2 calls pass `SQLE-CONN-SETTING` and `SQLCA` by reference and `listnumber` by value, returning `rc`; `checkerr` receives `SQLCA` and `errloc`. There are no inline SQL/CICS commands, `REPLACE`, file descriptors, or schemas in the entry source. Db2 client-setting state, connection ownership, concurrency, and exact error text remain undefined without the remaining dependencies.

IBM Db2 11.5 documentation confirms that `sqlgqryc` takes the setting type/value array as input/output and `sqlgsetc` takes it as input; both accept 0-7 settings. The documented COBOL structure has seven items, each with `PIC S9(4) COMP-5` type and value fields. This supports the four-item call shape but does not establish the deployed runtime version or numeric constants. See `references/README.md` for documentation URLs.

## Business rules and semantic risks

The four requested replacement codes are `SQL-CONNECT-2`, `SQL-RULES-STD`, `SQL-DISCONNECT-COND`, and `SQL-SYNC-TWOPHASE` (`:100-103`). The code does not inspect `rc`; the public IBM `checkerr` reference indicates positive SQLCODE warnings continue and negative errors stop. Restore runs only after the second query/print path, so a negative error before that point can leave changed settings (D2). `print-query` uses independent `IF` checks (`:167-248`). Sentence-ending periods at `:193,210,217,221,235,242,247` end nearby `IF` statements; later explanatory or `TYPE` displays on `:194-197,211,218,222,236,243,248` are unconditional (D3). Preserve these source-visible lines. No calculations, dates, sorting, or fixed-width records appear. Console line text and spacing are observable output.

## English output translations

| Source location | English replacement | Layout / compatibility effect | Decision reference |
| --- | --- | --- | --- |
| `:69-248` | Existing displayed strings are English | No translation made; spelling/wording corrections would change observable output. | D3 if output is selected. |

## Java API and data-access contracts

| Use case | Method and path | Request / validation | Response / status / errors | DAO contract |
| --- | --- | --- | --- | --- |
| Full query/change/query/restore sequence | Pending target transport/path | Source accepts no direct input; target must identify Db2 connection/client context | Response should represent displayed report and failures; exact DTO/status pending Db2 behavior | Real query/set client-setting adapter; API and transaction context unresolved. |

The target API must define Db2 connection/client context, how settings are restored after failure, and error/status mapping. These choices materially affect a real implementation; no endpoint or data-access behavior is assumed. The `controller` would own request validation and HTTP response, a stateless `service` would orchestrate the sequence with per-request settings, a `dao` would call the real Db2 API, a formatter would reproduce selected display text, and `exception` handling would expose failures without leaking connection details. This follows `docs/java-architecture.md`; names and contracts remain provisional.

## Verification design

After copybook constants and decisions are known, test the four-call order, capture/restore of original values, matching and unmatched display values, exact lines including period-sensitive unconditional text (source-derived unless D3 changes it), query/set failure behavior (dependency- and user-decision-derived), and state isolation across repeated/concurrent requests. An integration test needs the agreed real Db2 interface. No COBOL output has been observed.

# COBOL-to-Java mapping

Full-program scope is selected. Java contracts remain pending Db2 dependencies and behavior decisions; no Java classes have been generated.

| COBOL location / responsibility | Java class / method / contract | Selected or supporting | Evidence / decision |
| --- | --- | --- | --- |
| `CLIENT.CBL:67-156` / query, change, query, restore | Service orchestration with per-request state | Selected | Calls and order in source. |
| `CLIENT.CBL:158-251` / display setting descriptions | Formatter for queried values | Selected | `PERFORM print-query` at `:91,136`. |
| `CLIENT.CBL:49-51,82-89,114-121,127-134,147-154` / copybooks and calls | Db2 DAO contract and error mapping | Selected; external definitions missing | Referenced dependencies. |
| `CLIENT.CBL:69-156` / console operation | Controller, DTO, and response mapping | Selected; HTTP contract pending | Java architecture and selected program output. |

## Excluded functionality

No behavior in `CLIENT.CBL` is excluded. Other COBOL programs in the workspace are outside this session.

## Intentional differences

None chosen. No Java classes have been generated; the Java contract remains pending.

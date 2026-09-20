# Conversion session

Status: awaiting scope

## Identity

- Session ID: <unique UTC timestamp and suffix>
- Source path: <entry program>
- PROGRAM-ID: <source identifier>
- Source SHA-256: <hash>
- Dependency paths and hashes: <list or none>
- Compiler/dialect and encoding: <known facts or unresolved>

## User-selected scope

- Mode: <full | transaction-types | paragraphs-or-sections>
- Selected identifiers: <exact source identifiers, or all for full>
- User's scope statement: <English summary>
- Required supporting behavior: <identified during analysis>
- Explicit exclusions: <unrelated functionality>

Do not generate business code while the scope is unselected. Do not carry scope
over from another session unless this session is explicitly being resumed.

## Decisions and open questions

| ID | Source evidence / question | User answer or established requirement | Status / effect |
| --- | --- | --- | --- |
| <ID> | <question> | <answer, never fabricated> | <open/resolved; effect> |

## Progress

- Analysis: <pending/in progress/complete>
- Contract definition: <pending/in progress/complete>
- Implementation: <not started/in progress/complete>
- Verification: <not run/partial/passed/failed>
- Remaining limitations: <list or none>

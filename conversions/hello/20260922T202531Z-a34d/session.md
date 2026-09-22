# Conversion session

Status: awaiting scope

## Identity

- Session ID: 20260922T202531Z-a34d
- Source path: `COBOL_PROGRAM/HELLO_FROM_IBM.CBL`
- PROGRAM-ID: `HELLO`
- Source SHA-256: `C8F5E8EF480846B0DC65B351A22BF510AB99F29EC3CEDCB9110B0B8EC52889E4`
- Dependency paths and hashes: none identified from source inspection
- Compiler/dialect and encoding: unresolved; source is fixed-format-style COBOL with standard divisions and statements

## User-selected scope

- Mode: pending
- Selected identifiers: pending
- User's scope statement: pending
- Required supporting behavior: preliminary analysis identifies `MAIN-LOGIC` and `OGRENCI-MESAJ` as the only execution path and state
- Explicit exclusions: pending scope selection

Do not generate business code while the scope is unselected.

## Decisions and open questions

| ID | Source evidence / question | User answer or established requirement | Status / effect |
| --- | --- | --- | --- |
| Q1 | The program has no transaction dispatch; the sole paragraph is `MAIN-LOGIC`. What scope should be converted? | Pending | No Java business code may be generated until resolved. |

## Progress

- Analysis: in progress; preliminary source evidence recorded
- Contract definition: pending
- Implementation: not started
- Verification: not run
- Remaining limitations: scope, compiler/dialect, and encoding remain unconfirmed

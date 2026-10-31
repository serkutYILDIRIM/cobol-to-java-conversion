# Verification report

Status: not run (by user instruction, no Maven build was executed)

## Environment and commands

- Java version: not checked
- Maven/Wrapper versions: not run
- Commands and exit codes: none. `.\mvnw.cmd verify` was started once and interrupted; its outcome is unknown and its `target/` output was deleted.
- Test results and report locations: none

## Cases

Tests are written in `java/src/test/java/com/example/custproc/`. They have **not been compiled or executed**.

| Scenario | Expected result and evidence source | Actual result | Status |
| --- | --- | --- | --- |
| Deposit updates balance, total, report line | source-derived | - | not run |
| Withdrawal within balance, no fee | source-derived | - | not run |
| Withdrawal below zero, 35.00 fee | source-derived | - | not run |
| Withdrawal on negative balance, fee again | source-derived (Q6) | - | not run |
| Fee overdraw, no overdraft fee, 'SERVICE FE' | source-derived (Q5) | - | not run |
| Frozen and missing accounts, 80-char errors | source-derived | - | not run |
| Unknown type, stale/blank report type | source-derived (Q4) | - | not run |
| Repeated customer sees updated balance | source-derived | - | not run |
| Empty input: header only, zero totals | source-derived | - | not run |
| 9-digit balance truncation | source-derived (Q7) | - | not run |
| Concurrent runs share no state | source-derived | - | not run |
| Missing/partial master file | design decision | - | not run |
| Packed decimal round trip, edited picture, counters wrap, parser | source-derived | - | not run |
| HTTP 200 and 400 contracts | design decision | - | not run |

## COBOL comparison

- Runtime/compiler/dialect: unavailable
- Input fixtures and invocation: none
- Compared outputs: none
- Allowed differences: see `mapping.md`
- Equivalence conclusion: unverified

## Delivery checks

- [x] Selected behavior and required dependencies are implemented (unbuilt).
- [x] Excluded behavior is documented.
- [x] No fake success stubs remain.
- [x] Controller, service, and DAO boundaries are respected.
- [ ] Monetary and fixed-width behavior is tested (tests written, not run).
- [ ] Error and per-request state behavior is tested (tests written, not run).
- [ ] The standalone Java project passes Maven Wrapper verification.
- [x] All newly authored content is English.
- [x] Original COBOL inputs remain unchanged.
- [x] Remaining limitations are reported.

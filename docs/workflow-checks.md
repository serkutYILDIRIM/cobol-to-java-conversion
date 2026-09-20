# Assistant workflow acceptance checks

Run these as manual assistant-session checks when validating an IDE integration.
Repository instructions cannot enforce model behavior deterministically. Do not
mark these scenarios passed merely because the instruction files exist.

| Scenario | Expected behavior |
| --- | --- |
| New program, no scope stated | Ask full vs transaction vs PERFORM scope before business generation |
| Full scope stated in initial prompt | Record it without asking the same question again |
| Partial scope without identifiers | Present source-derived candidates and request selection |
| SalaryReport transaction-type request | Explain that no transaction dispatch exists; request a valid alternative |
| Unknown paragraph | Explain that it was not found; do not fabricate or silently choose another |
| Selected paragraph with shared state | Document required inputs, initialization, support routines, and cleanup |
| Source defect or missing COPY/CALL | Ask a focused question; do not invent dependent behavior |
| New session for a previously converted program | Create separate outputs and establish scope anew |
| Explicit session resume | Load prior decisions and check whether source hashes changed |
| COBOL runtime unavailable | Report source-based expectations and unverified runtime equivalence |
| Non-English source messages | Generate English text, record differences, clarify layout implications |
| Workspace maintenance | Do not ask for a COBOL conversion scope unnecessarily |

For a delivered conversion, also verify service boundaries, real DAO behavior,
HTTP contracts, independent Maven build, and English-only newly authored content.

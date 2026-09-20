# Conversion guide

## 1. Establish the session and scope

Read the repository instructions. Communicate in English. Identify the requested
entry program; inspect filenames and PROGRAM-ID without assuming a conversion scope.
If the entry file is ambiguous, ask the user to select one.

Ask, unless already answered in this session:

> What would you like to convert: the full program, selected transaction types,
> or selected PERFORMs / paragraphs / sections?

Wait for this answer before generating business code. For partial conversion,
inspect the source and present real candidates with brief descriptions. Request
exact selections when missing. If there is no transaction dispatch mechanism,
explain that transaction-type selection does not apply and offer actual paragraphs
or full conversion. Never invent transaction codes.

Create a unique session directory as described in `conversions/README.md`. Copy
the session documentation templates and record the source path, PROGRAM-ID,
SHA-256, scope, explicit exclusions, and current status. New sessions start without
previous scope selections. For an explicit resume, read existing decisions and
compare source hashes; reanalyze affected behavior when sources have changed.

## 2. Analyze before implementing

Populate `analysis.md` from actual source evidence, including line references:

- Identify source format, compiler/dialect assumptions, encoding, divisions,
  sections, paragraphs, and program inputs/outputs.
- Describe file layouts, nested fields, PIC clauses, numeric signs and scales,
  COMP/COMP-3, REDEFINES, OCCURS, level-88 conditions, and initialization when present.
- Trace PERFORM (including THRU ranges and loops), fall-through, GO TO, sentence
  termination, STOP RUN/GOBACK, and transaction dispatch as applicable.
- Identify COPY/REPLACE, CALL, SQL, CICS, external files, and other dependencies.
  Request missing artifacts when needed; do not invent schemas or external behavior.
- Extract business rules, EOF and file-status handling, numeric truncation and
  overflow behavior, fixed-width padding, sorting, dates, and output formatting.

For partial scope, compute the required dependency closure: called routines, data
state, prerequisites, reads/writes, and cleanup. Explain why each support routine
is necessary. A selected paragraph is not automatically a standalone use case.
Expose required surrounding state as explicit inputs when appropriate and explain
the resulting contract. Exclude unrelated transaction entry points and behavior.

Record unresolved behavior and ask focused questions. Preserve established business
rules. Do not silently fix source defects or replace an unclear branch with a
plausible implementation. Continue independent analysis while answers are pending,
but do not implement the behavior that depends on those answers.

All new output text must be English. Translate source messages and headings in
generated artifacts, recording intentional differences. Ask about ambiguous terms
or translations that require changing fixed-width output layouts. Keep original
source files unchanged. Do not translate identifiers that must match external
contracts without agreeing and documenting the contract change.

## 3. Define the Java contract

Follow `docs/java-architecture.md`. In the analysis, define each selected use case's
HTTP method/path, request and response DTOs, validation, status codes, and data-access
contract. For batch input, document whether records arrive as a file or structured
request and how results are returned. Ask when these alternatives materially affect
the intended use; do not expose arbitrary filesystem paths as a default HTTP API.

List the Java responsibilities and their COBOL counterparts in `mapping.md`.
Establish test expectations before implementation, distinguishing executable COBOL
observations, user decisions, and source-derived expectations.

Only proceed to business implementation when scope is established, necessary
dependencies are available, and questions affecting that implementation are resolved.
Do not require redundant approval for decisions the user has already made.

## 4. Generate and verify

Copy the starter to the session's `java/` directory without `target/` or IDE files.
Set a meaningful artifact ID, application name, base package, and application class
for the program; update the test package and class consistently. Preserve the
wrapper files, including the hidden `.mvn/` directory. Implement selected behavior
and required support code. Remove irrelevant package markers and add a standalone
README with contracts, configuration, build/run commands, and example requests.

Implement meaningful business-rule, data-access, and controller tests. Fix the
clock in tests where dates affect outputs. Test boundary conditions and failure
behavior. Verify repeated or concurrent calls do not share COBOL working state.
Run `mvnw.cmd verify` on Windows or `sh mvnw verify` elsewhere using JDK 21.

If a compatible COBOL runtime and complete inputs are available, compare both
implementations using identical input fixtures. Record the compiler/dialect,
commands, outputs, and intentional differences. Otherwise label the result as
Java-tested and source-reviewed, with COBOL execution equivalence unverified.
Never present source-derived expected outputs as observed COBOL outputs.

## 5. Deliver honestly

Complete `verification.md`, the mapping, decisions, exclusions, and known limits.
Report the generated application's location, how to run it, implemented scope,
tests actually executed, unresolved issues, and intentional output differences.
Missing critical behavior is incomplete work, not a successful conversion. Do not
add placeholder implementations returning success for unsupported functionality.

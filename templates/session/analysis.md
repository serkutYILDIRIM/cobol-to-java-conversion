# Program analysis

## Evidence and purpose

<Describe source format, dialect, inputs, outputs, and purpose. Cite source paths
and line numbers. Distinguish observed source facts from assumptions.>

## Data definitions

| Source field / record | PIC, length, scale, encoding | Initialization / conditions | Proposed Java representation |
| --- | --- | --- | --- |
| <field> | <definition> | <state> | <type and constraints> |

## Selected control flow and dependency closure

<List selected entry points, actual execution order, loops, branches, fall-through,
shared state, initialization, and cleanup. Explain each included supporting routine
and why excluded routines are unnecessary.>

## External dependencies and I/O

<Inventory files, COPY, CALL, SQL, CICS, schemas, record layouts, statuses, and
missing artifacts. Specify stream/resource ownership and failure behavior.>

## Business rules and semantic risks

<Document calculations, rounding, overflow, truncation, delimiters, padding, dates,
EOF behavior, output formatting, and unresolved defects. Link session decision IDs.>

## English output translations

| Source location | English replacement | Layout / compatibility effect | Decision reference |
| --- | --- | --- | --- |
| <location> | <text> | <effect> | <reference> |

## Java API and data-access contracts

| Use case | Method and path | Request / validation | Response / status / errors | DAO contract |
| --- | --- | --- | --- | --- |
| <selected use case> | <HTTP contract> | <input> | <output> | <I/O> |

<Define concrete DTO fields, required state for partial scope, input/output transport,
and configuration required to implement this session. Resolve meaningful ambiguities
with the user. Do not expose unspecified arbitrary filesystem paths.>

## Verification design

<List normal, boundary, failure, and state-isolation cases with expected outcomes
and their evidence source: observed COBOL run, source inspection, or user decision.>

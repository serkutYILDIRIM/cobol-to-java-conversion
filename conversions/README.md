# Conversion outputs

Create one directory per session:

```text
conversions/<program-id>/<session-id>/
  session.md
  analysis.md
  mapping.md
  verification.md
  java/
```

Use a lowercase filesystem-safe program identifier and a UTC timestamp with a
short distinguishing suffix, such as `rep001/20260920T210000Z-a1b2/`. Do not reuse
an existing directory for a new session. Resume an existing directory only when
the user explicitly identifies that session.

Copy the documentation templates from `templates/session/` when starting a
session. Copy `templates/spring-boot/` into `java/` only after the scope and relevant
ambiguities are resolved. Exclude build outputs such as `target/` and local IDE files.
Do not copy input business data into outputs unless it is needed and appropriate
for the agreed test fixtures.

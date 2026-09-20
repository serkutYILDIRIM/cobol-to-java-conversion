# Workspace instructions

This workspace supports IDE-assisted COBOL-to-Java conversion. Read
`docs/conversion-guide.md` before starting or resuming a conversion, and
`docs/java-architecture.md` before changing generated Java applications.

- Use English for all new content and user communication.
- Preserve user-supplied COBOL and copybook files unchanged.
- Establish full-program, transaction-type, or PERFORM/paragraph/section scope
  for each new conversion session. Do not infer full conversion from a file upload.
- Reuse scope already stated in the current session; do not ask twice.
- Analyze dependencies before generating business code. Ask about missing
  information and behavior-changing corrections; never fabricate business rules.
- Keep session artifacts under `conversions/<program-id>/<session-id>/`.
- The starter is under `templates/spring-boot/`. It is not a converted program.
- Validate Java changes from the relevant Java project directory with
  `./mvnw verify` or, on Windows, `.\mvnw.cmd verify` using JDK 21.
- Record actual checks and limitations. Never claim COBOL equivalence without evidence.

Workspace maintenance and starter development do not require selecting a COBOL
conversion scope. The current initial delivery does not convert SalaryReport.

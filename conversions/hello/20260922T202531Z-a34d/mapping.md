# COBOL-to-Java mapping

| COBOL location / responsibility | Java class / method / contract | Selected or supporting | Evidence / decision |
| --- | --- | --- | --- |
| Lines 12-14, `MAIN-LOGIC` | `HelloController#getHello()` and `HelloService#getMessage()` | Selected | Full-program scope; source execution sequence |
| Line 9, `OGRENCI-MESAJ PIC X(30)` | `HelloService` fixed-width string and `HelloResponse.message` | Supporting | Source field definition; D1 |
| Line 13, `DISPLAY OGRENCI-MESAJ` | `GET /api/hello` JSON response | Selected | D1 |
| Line 14, `STOP RUN` | Normal successful HTTP response completion | Selected | Java architecture guidance |

## Excluded functionality

None. The full program is selected and contains no other procedures or transaction entry points.

## Intentional differences

`DISPLAY` output is adapted to a JSON HTTP response. The 30-character field value, including padding spaces, is retained. `STOP RUN` does not terminate the JVM; it completes the HTTP operation.

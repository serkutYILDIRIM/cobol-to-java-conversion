      *================================================================*
      * PROGRAM-ID   : INVMGMT                                         *
      * DESCRIPTION  : Sample inventory management program.            *
      *                Loads a product table, processes stock          *
      *                transactions, flags low stock items,            *
      *                calculates inventory value and prints a         *
      *                summary report.                                 *
      * AUTHOR       : SAMPLE                                          *
      * LANGUAGE     : COBOL 85                                        *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. INVMGMT.
       AUTHOR. SAMPLE.
       DATE-WRITTEN. 2026-11-09.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SOURCE-COMPUTER. IBM-PC.
       OBJECT-COMPUTER. IBM-PC.

       DATA DIVISION.
       WORKING-STORAGE SECTION.

      *----------------------------------------------------------------*
      * CONSTANTS                                                      *
      *----------------------------------------------------------------*
       01  WS-CONSTANTS.
           05  WS-MAX-PRODUCTS         PIC 9(02)   VALUE 10.
           05  WS-MAX-TRANSACTIONS     PIC 9(02)   VALUE 12.
           05  WS-TAX-RATE             PIC V99     VALUE .18.

      *----------------------------------------------------------------*
      * PRODUCT MASTER DATA                                            *
      *   CODE(5) NAME(20) QTY(5) REORDER(5) PRICE(7) STATUS(1)        *
      *----------------------------------------------------------------*
       01  WS-PRODUCT-DATA.
           05  FILLER  PIC X(43)  VALUE
               'P1001WIRELESS MOUSE      00120000500002490A'.
           05  FILLER  PIC X(43)  VALUE
               'P1002MECHANICAL KEYBOARD 00045000300008950A'.
           05  FILLER  PIC X(43)  VALUE
               'P100327 INCH MONITOR     00018000200024999A'.
           05  FILLER  PIC X(43)  VALUE
               'P1004USB-C HUB           00200000750003400A'.
           05  FILLER  PIC X(43)  VALUE
               'P1005LAPTOP STAND        00060000250004275A'.
           05  FILLER  PIC X(43)  VALUE
               'P1006NOISE CANCEL HEADSET00012000150015900A'.
           05  FILLER  PIC X(43)  VALUE
               'P1007HD WEBCAM           00035000200005990A'.
           05  FILLER  PIC X(43)  VALUE
               'P1008EXTERNAL SSD 1TB    00025000100011900A'.
           05  FILLER  PIC X(43)  VALUE
               'P1009DOCKING STATION     00008000100018900I'.
           05  FILLER  PIC X(43)  VALUE
               'P1010HDMI CABLE 2M       00300001000000750A'.

       01  WS-PRODUCT-TABLE REDEFINES WS-PRODUCT-DATA.
           05  WS-PRODUCT OCCURS 10 TIMES INDEXED BY PRD-IDX.
               10  PRD-CODE            PIC X(05).
               10  PRD-NAME            PIC X(20).
               10  PRD-QTY             PIC 9(05).
               10  PRD-REORDER-LEVEL   PIC 9(05).
               10  PRD-UNIT-PRICE      PIC 9(05)V99.
               10  PRD-STATUS          PIC X(01).
                   88  PRD-ACTIVE                  VALUE 'A'.
                   88  PRD-INACTIVE                VALUE 'I'.

      *----------------------------------------------------------------*
      * TRANSACTION TABLE                                              *
      *   TYPE: R = RECEIPT, S = SALE, A = ADJUSTMENT                  *
      *----------------------------------------------------------------*
       01  WS-TRANSACTION-DATA.
           05  FILLER  PIC X(12)  VALUE 'P1001S+00080'.
           05  FILLER  PIC X(12)  VALUE 'P1002R+00040'.
           05  FILLER  PIC X(12)  VALUE 'P1003S+00025'.
           05  FILLER  PIC X(12)  VALUE 'P1004S+00150'.
           05  FILLER  PIC X(12)  VALUE 'P1005A-00005'.
           05  FILLER  PIC X(12)  VALUE 'P9999R+00010'.
           05  FILLER  PIC X(12)  VALUE 'P1006R+00030'.
           05  FILLER  PIC X(12)  VALUE 'P1007X+00005'.
           05  FILLER  PIC X(12)  VALUE 'P1009S+00002'.
           05  FILLER  PIC X(12)  VALUE 'P1008S+00018'.
           05  FILLER  PIC X(12)  VALUE 'P1010S+00220'.
           05  FILLER  PIC X(12)  VALUE 'P1007S+00020'.

       01  WS-TRANSACTION-TABLE REDEFINES WS-TRANSACTION-DATA.
           05  WS-TRANSACTION OCCURS 12 TIMES INDEXED BY TRN-IDX.
               10  TRN-CODE            PIC X(05).
               10  TRN-TYPE            PIC X(01).
                   88  TRN-RECEIPT                 VALUE 'R'.
                   88  TRN-SALE                    VALUE 'S'.
                   88  TRN-ADJUSTMENT              VALUE 'A'.
               10  TRN-QTY             PIC S9(05)
                                       SIGN LEADING SEPARATE.

      *----------------------------------------------------------------*
      * COUNTERS AND ACCUMULATORS                                      *
      *----------------------------------------------------------------*
       01  WS-COUNTERS.
           05  WS-TRN-PROCESSED        PIC 9(03)   VALUE ZERO.
           05  WS-TRN-REJECTED         PIC 9(03)   VALUE ZERO.
           05  WS-LOW-STOCK-COUNT      PIC 9(03)   VALUE ZERO.
           05  WS-TOTAL-RECEIVED       PIC 9(07)   VALUE ZERO.
           05  WS-TOTAL-SOLD           PIC 9(07)   VALUE ZERO.

       01  WS-AMOUNTS.
           05  WS-ITEM-VALUE           PIC 9(09)V99 VALUE ZERO.
           05  WS-TOTAL-VALUE          PIC 9(09)V99 VALUE ZERO.
           05  WS-TAX-AMOUNT           PIC 9(09)V99 VALUE ZERO.
           05  WS-GRAND-TOTAL          PIC 9(09)V99 VALUE ZERO.

      *----------------------------------------------------------------*
      * FLAGS                                                          *
      *----------------------------------------------------------------*
       01  WS-FLAGS.
           05  WS-FOUND-FLAG           PIC X(01)   VALUE 'N'.
               88  PRODUCT-FOUND                   VALUE 'Y'.
               88  PRODUCT-NOT-FOUND               VALUE 'N'.
           05  WS-VALID-FLAG           PIC X(01)   VALUE 'Y'.
               88  TRANSACTION-VALID               VALUE 'Y'.
               88  TRANSACTION-INVALID             VALUE 'N'.

       01  WS-REJECT-REASON            PIC X(30)   VALUE SPACES.

      *----------------------------------------------------------------*
      * REPORT LINES                                                   *
      *----------------------------------------------------------------*
       01  WS-SEPARATOR                PIC X(78)   VALUE ALL '-'.

       01  WS-HEADER-1.
           05  FILLER                  PIC X(25)   VALUE SPACES.
           05  FILLER                  PIC X(28)
               VALUE 'INVENTORY MANAGEMENT REPORT'.

       01  WS-HEADER-2.
           05  FILLER                  PIC X(07)   VALUE 'CODE'.
           05  FILLER                  PIC X(22)
               VALUE 'PRODUCT NAME'.
           05  FILLER                  PIC X(09)   VALUE 'QTY'.
           05  FILLER                  PIC X(09)   VALUE 'REORDER'.
           05  FILLER                  PIC X(12)   VALUE 'UNIT PRICE'.
           05  FILLER                  PIC X(13)   VALUE 'STOCK VALUE'.
           05  FILLER                  PIC X(06)   VALUE 'FLAG'.

       01  WS-DETAIL-LINE.
           05  DL-CODE                 PIC X(05).
           05  FILLER                  PIC X(02)   VALUE SPACES.
           05  DL-NAME                 PIC X(20).
           05  FILLER                  PIC X(02)   VALUE SPACES.
           05  DL-QTY                  PIC ZZZ,ZZ9.
           05  FILLER                  PIC X(02)   VALUE SPACES.
           05  DL-REORDER              PIC ZZ,ZZ9.
           05  FILLER                  PIC X(03)   VALUE SPACES.
           05  DL-PRICE                PIC ZZ,ZZ9.99.
           05  FILLER                  PIC X(02)   VALUE SPACES.
           05  DL-VALUE                PIC ZZZ,ZZZ,ZZ9.99.
           05  FILLER                  PIC X(02)   VALUE SPACES.
           05  DL-FLAG                 PIC X(05).

       01  WS-REJECT-LINE.
           05  FILLER                  PIC X(14)
               VALUE 'REJECTED TRN: '.
           05  RL-CODE                 PIC X(05).
           05  FILLER                  PIC X(03)   VALUE ' / '.
           05  RL-TYPE                 PIC X(01).
           05  FILLER                  PIC X(03)   VALUE ' / '.
           05  RL-QTY                  PIC -ZZ,ZZ9.
           05  FILLER                  PIC X(03)   VALUE ' - '.
           05  RL-REASON               PIC X(30).

       01  WS-SUMMARY-LINE.
           05  SL-LABEL                PIC X(30).
           05  SL-VALUE                PIC ZZZ,ZZZ,ZZ9.99.

       01  WS-COUNT-LINE.
           05  CL-LABEL                PIC X(30).
           05  CL-VALUE                PIC ZZZ,ZZ9.

       PROCEDURE DIVISION.

      *================================================================*
      * MAIN CONTROL                                                   *
      *================================================================*
       0000-MAIN.
           PERFORM 1000-INITIALIZE
           PERFORM 2000-PROCESS-TRANSACTIONS
           PERFORM 3000-PRINT-INVENTORY-REPORT
           PERFORM 4000-PRINT-SUMMARY
           STOP RUN.

      *================================================================*
      * RESET COUNTERS AND ACCUMULATORS                                *
      *================================================================*
       1000-INITIALIZE.
           INITIALIZE WS-COUNTERS WS-AMOUNTS.

      *================================================================*
      * APPLY EACH TRANSACTION TO THE PRODUCT TABLE                    *
      *================================================================*
       2000-PROCESS-TRANSACTIONS.
           DISPLAY WS-SEPARATOR
           DISPLAY 'TRANSACTION PROCESSING LOG'
           DISPLAY WS-SEPARATOR
           PERFORM VARYING TRN-IDX FROM 1 BY 1
                   UNTIL TRN-IDX > WS-MAX-TRANSACTIONS
               SET TRANSACTION-VALID TO TRUE
               MOVE SPACES TO WS-REJECT-REASON
               PERFORM 2100-FIND-PRODUCT
               IF TRANSACTION-VALID
                   PERFORM 2200-VALIDATE-TRANSACTION
               END-IF
               IF TRANSACTION-VALID
                   PERFORM 2300-APPLY-TRANSACTION
                   ADD 1 TO WS-TRN-PROCESSED
               ELSE
                   PERFORM 2900-REPORT-REJECTION
                   ADD 1 TO WS-TRN-REJECTED
               END-IF
           END-PERFORM.

       2100-FIND-PRODUCT.
           SET PRODUCT-NOT-FOUND TO TRUE
           SET PRD-IDX TO 1
           SEARCH WS-PRODUCT
               AT END
                   SET TRANSACTION-INVALID TO TRUE
                   MOVE 'PRODUCT NOT FOUND' TO WS-REJECT-REASON
               WHEN PRD-CODE(PRD-IDX) = TRN-CODE(TRN-IDX)
                   SET PRODUCT-FOUND TO TRUE
           END-SEARCH.

       2200-VALIDATE-TRANSACTION.
           EVALUATE TRUE
               WHEN PRD-INACTIVE(PRD-IDX)
                   SET TRANSACTION-INVALID TO TRUE
                   MOVE 'PRODUCT IS INACTIVE' TO WS-REJECT-REASON
               WHEN NOT (TRN-RECEIPT(TRN-IDX) OR TRN-SALE(TRN-IDX)
                         OR TRN-ADJUSTMENT(TRN-IDX))
                   SET TRANSACTION-INVALID TO TRUE
                   MOVE 'INVALID TRANSACTION TYPE' TO WS-REJECT-REASON
               WHEN TRN-SALE(TRN-IDX)
                    AND TRN-QTY(TRN-IDX) > PRD-QTY(PRD-IDX)
                   SET TRANSACTION-INVALID TO TRUE
                   MOVE 'INSUFFICIENT STOCK' TO WS-REJECT-REASON
               WHEN OTHER
                   CONTINUE
           END-EVALUATE.

       2300-APPLY-TRANSACTION.
           EVALUATE TRUE
               WHEN TRN-RECEIPT(TRN-IDX)
                   ADD TRN-QTY(TRN-IDX) TO PRD-QTY(PRD-IDX)
                   ADD TRN-QTY(TRN-IDX) TO WS-TOTAL-RECEIVED
               WHEN TRN-SALE(TRN-IDX)
                   SUBTRACT TRN-QTY(TRN-IDX) FROM PRD-QTY(PRD-IDX)
                   ADD TRN-QTY(TRN-IDX) TO WS-TOTAL-SOLD
               WHEN TRN-ADJUSTMENT(TRN-IDX)
                   ADD TRN-QTY(TRN-IDX) TO PRD-QTY(PRD-IDX)
           END-EVALUATE
           DISPLAY 'APPLIED  TRN: ' TRN-CODE(TRN-IDX) ' / '
                   TRN-TYPE(TRN-IDX) ' / ' TRN-QTY(TRN-IDX).

       2900-REPORT-REJECTION.
           MOVE TRN-CODE(TRN-IDX)  TO RL-CODE
           MOVE TRN-TYPE(TRN-IDX)  TO RL-TYPE
           MOVE TRN-QTY(TRN-IDX)   TO RL-QTY
           MOVE WS-REJECT-REASON   TO RL-REASON
           DISPLAY WS-REJECT-LINE.

      *================================================================*
      * PRINT THE INVENTORY STATUS REPORT                              *
      *================================================================*
       3000-PRINT-INVENTORY-REPORT.
           DISPLAY SPACE
           DISPLAY WS-SEPARATOR
           DISPLAY WS-HEADER-1
           DISPLAY WS-SEPARATOR
           DISPLAY WS-HEADER-2
           DISPLAY WS-SEPARATOR
           PERFORM VARYING PRD-IDX FROM 1 BY 1
                   UNTIL PRD-IDX > WS-MAX-PRODUCTS
               PERFORM 3100-BUILD-DETAIL-LINE
               DISPLAY WS-DETAIL-LINE
           END-PERFORM
           DISPLAY WS-SEPARATOR.

       3100-BUILD-DETAIL-LINE.
           COMPUTE WS-ITEM-VALUE ROUNDED =
                   PRD-QTY(PRD-IDX) * PRD-UNIT-PRICE(PRD-IDX)
           ADD WS-ITEM-VALUE TO WS-TOTAL-VALUE
           MOVE PRD-CODE(PRD-IDX)          TO DL-CODE
           MOVE PRD-NAME(PRD-IDX)          TO DL-NAME
           MOVE PRD-QTY(PRD-IDX)           TO DL-QTY
           MOVE PRD-REORDER-LEVEL(PRD-IDX) TO DL-REORDER
           MOVE PRD-UNIT-PRICE(PRD-IDX)    TO DL-PRICE
           MOVE WS-ITEM-VALUE              TO DL-VALUE
           EVALUATE TRUE
               WHEN PRD-INACTIVE(PRD-IDX)
                   MOVE 'INACT' TO DL-FLAG
               WHEN PRD-QTY(PRD-IDX) <= PRD-REORDER-LEVEL(PRD-IDX)
                   MOVE 'LOW'   TO DL-FLAG
                   ADD 1 TO WS-LOW-STOCK-COUNT
               WHEN OTHER
                   MOVE 'OK'    TO DL-FLAG
           END-EVALUATE.

      *================================================================*
      * PRINT TOTALS AND STATISTICS                                    *
      *================================================================*
       4000-PRINT-SUMMARY.
           COMPUTE WS-TAX-AMOUNT ROUNDED = WS-TOTAL-VALUE * WS-TAX-RATE
           COMPUTE WS-GRAND-TOTAL = WS-TOTAL-VALUE + WS-TAX-AMOUNT

           DISPLAY SPACE
           DISPLAY 'SUMMARY'
           DISPLAY WS-SEPARATOR

           MOVE 'TRANSACTIONS PROCESSED  :' TO CL-LABEL
           MOVE WS-TRN-PROCESSED            TO CL-VALUE
           DISPLAY WS-COUNT-LINE
           MOVE 'TRANSACTIONS REJECTED   :' TO CL-LABEL
           MOVE WS-TRN-REJECTED             TO CL-VALUE
           DISPLAY WS-COUNT-LINE
           MOVE 'TOTAL UNITS RECEIVED    :' TO CL-LABEL
           MOVE WS-TOTAL-RECEIVED           TO CL-VALUE
           DISPLAY WS-COUNT-LINE
           MOVE 'TOTAL UNITS SOLD        :' TO CL-LABEL
           MOVE WS-TOTAL-SOLD               TO CL-VALUE
           DISPLAY WS-COUNT-LINE
           MOVE 'LOW STOCK PRODUCTS      :' TO CL-LABEL
           MOVE WS-LOW-STOCK-COUNT          TO CL-VALUE
           DISPLAY WS-COUNT-LINE

           DISPLAY WS-SEPARATOR
           MOVE 'INVENTORY VALUE (NET)   :' TO SL-LABEL
           MOVE WS-TOTAL-VALUE              TO SL-VALUE
           DISPLAY WS-SUMMARY-LINE
           MOVE 'TAX (18%)               :' TO SL-LABEL
           MOVE WS-TAX-AMOUNT               TO SL-VALUE
           DISPLAY WS-SUMMARY-LINE
           MOVE 'INVENTORY VALUE (GROSS) :' TO SL-LABEL
           MOVE WS-GRAND-TOTAL              TO SL-VALUE
           DISPLAY WS-SUMMARY-LINE
           DISPLAY WS-SEPARATOR

           IF WS-LOW-STOCK-COUNT > 0
               DISPLAY 'ACTION: PLEASE REVIEW LOW STOCK ITEMS.'
           ELSE
               DISPLAY 'ALL PRODUCTS ARE ABOVE REORDER LEVEL.'
           END-IF.

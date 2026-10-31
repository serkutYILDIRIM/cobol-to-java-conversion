       IDENTIFICATION DIVISION.
       PROGRAM-ID.    CUSTPROC.
       AUTHOR.        IBM-EXAMPLE.
       DATE-WRITTEN.  2026-10-31.
      *================================================================*
      * PURPOSE: READS TRANSACTION DATA, VALIDATES AND MERGES WITH     *
      *          VSAM MASTER FILE DATA, COMPUTES INTEREST/FEES, AND     *
      *          GENERATES BOTH A REPORT AND AN ERROR LOG FILE.        *
      *================================================================*
       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SOURCE-COMPUTER. IBM-ZOS.
       OBJECT-COMPUTER. IBM-ZOS.

       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT TRANS-FILE ASSIGN TO UT-S-TRANS
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE  IS SEQUENTIAL
               FILE STATUS  IS WS-TRANS-STATUS.

           SELECT MASTER-FILE ASSIGN TO DISK-MASTER
               ORGANIZATION IS INDEXED
               ACCESS MODE  IS RANDOM
               RECORD KEY   IS MSTR-KEY
               FILE STATUS  IS WS-MSTR-STATUS.

           SELECT REPORT-FILE ASSIGN TO UT-S-REPORT
               ORGANIZATION IS SEQUENTIAL
               FILE STATUS  IS WS-REPT-STATUS.

           SELECT ERROR-FILE ASSIGN TO UT-S-ERRORS
               ORGANIZATION IS SEQUENTIAL
               FILE STATUS  IS WS-ERR-STATUS.

       DATA DIVISION.
       FILE SECTION.
       FD  TRANS-FILE
           RECORDING MODE IS F
           RECORD CONTAINS 80 CHARACTERS.
       01  TRANS-REC.
           05  TR-CUST-ID        PIC X(10).
           05  TR-TYPE           PIC X(01).
               88  TR-DEPOSIT    VALUE 'D'.
               88  TR-WITHDRAWAL VALUE 'W'.
               88  TR-FEE        VALUE 'F'.
           05  TR-AMOUNT         PIC 9(07)V99.
           05  TR-DATE           PIC X(08).
           05  FILLER            PIC X(52).

       FD  MASTER-FILE
           RECORD CONTAINS 150 CHARACTERS.
       01  MASTER-REC.
           05  MSTR-KEY.
               10  MSTR-CUST-ID  PIC X(10).
           05  MSTR-NAME         PIC X(30).
           05  MSTR-CURR-BAL     PIC S9(09)V99 COMP-3.
           05  MSTR-ACCT-STATUS  PIC X(01).
               88  MSTR-ACTIVE   VALUE 'A'.
               88  MSTR-FROZEN   VALUE 'F'.
           05  FILLER            PIC X(104).

       FD  REPORT-FILE
           RECORDING MODE IS F
           RECORD CONTAINS 133 CHARACTERS.
       01  REPORT-REC            PIC X(133).

       FD  ERROR-FILE
           RECORDING MODE IS F
           RECORD CONTAINS 80 CHARACTERS.
       01  ERROR-REC             PIC X(80).

       WORKING-STORAGE SECTION.
      *--- FILE STATUS VARIABLES ---
       01  WS-FILE-STATUSES.
           05  WS-TRANS-STATUS   PIC X(02).
           05  WS-MSTR-STATUS    PIC X(02).
           05  WS-REPT-STATUS    PIC X(02).
           05  WS-ERR-STATUS     PIC X(02).

      *--- SWITCHES ---
       01  WS-SWITCHES.
           05  EOF-TRANS-SW      PIC X(01) VALUE 'N'.
               88  EOF-TRANS     VALUE 'Y'.
           05  MSTR-FOUND-SW     PIC X(01) VALUE 'N'.
               88  MSTR-FOUND    VALUE 'Y'.

      *--- COUNTERS & TOTALS ---
       01  WS-COUNTERS.
           05  WS-RECS-READ      PIC 9(05) COMP VALUE ZERO.
           05  WS-RECS-PROC      PIC 9(05) COMP VALUE ZERO.
           05  WS-ERR-COUNT      PIC 9(05) COMP VALUE ZERO.
           05  WS-TOTAL-DEPOSITS PIC S9(11)V99 COMP-3 VALUE ZERO.

      *--- WORKING COMP-3 FIELDS FOR MATH ---
       01  WS-CALCULATIONS.
           05  WS-NEW-BALANCE    PIC S9(09)V99 COMP-3 VALUE ZERO.
           05  WS-OVERDRAFT-FEE  PIC 9(03)V99  COMP-3 VALUE 35.00.

      *--- REPORT HEADER LINE ---
       01  RPT-HEADER.
           05  FILLER            PIC X(01) VALUE '1'.
           05  FILLER            PIC X(20) VALUE 'BANK TRANSACTION RPT'.
           05  FILLER            PIC X(40) VALUE SPACES.
           05  FILLER            PIC X(05) VALUE 'DATE:'.
           05  RPT-HDR-DATE      PIC X(10).
           05  FILLER            PIC X(57) VALUE SPACES.

      *--- REPORT DETAIL LINE ---
       01  RPT-DETAIL.
           05  FILLER            PIC X(02) VALUE SPACES.
           05  RPT-CUST-ID       PIC X(10).
           05  FILLER            PIC X(05) VALUE SPACES.
           05  RPT-NAME          PIC X(30).
           05  FILLER            PIC X(05) VALUE SPACES.
           05  RPT-TYPE          PIC X(10).
           05  FILLER            PIC X(05) VALUE SPACES.
           05  RPT-OLD-BAL      PIC ----,--9.99.
           05  FILLER            PIC X(05) VALUE SPACES.
           05  RPT-NEW-BAL      PIC ----,--9.99.

      *--- ERROR RECORD FORMAT ---
       01  ERR-DETAIL.
           05  ERR-CUST-ID       PIC X(10).
           05  FILLER            PIC X(02) VALUE ': '.
           05  ERR-MSG           PIC X(68).

       PROCEDURE DIVISION.
       0000-MAINLINE SECTION.
           PERFORM 1000-INITIALIZE
           PERFORM 2000-PROCESS-TRANSACTIONS 
               UNTIL EOF-TRANS
           PERFORM 3000-TERMINATE
           GOBACK.

       1000-INITIALIZE.
           OPEN INPUT  TRANS-FILE
           IF WS-TRANS-STATUS NOT = '00'
               DISPLAY 'ERROR OPENING TRANS FILE: ' WS-TRANS-STATUS
               MOVE 16 TO RETURN-CODE
               STOP RUN
           END-IF

           OPEN I-O    MASTER-FILE
           IF WS-MSTR-STATUS NOT = '00'
               DISPLAY 'ERROR OPENING VSAM MASTER: ' WS-MSTR-STATUS
               STOP RUN
           END-IF

           OPEN OUTPUT REPORT-FILE
                       ERROR-FILE

      *    -- Use Enterprise COBOL Intrinsic function for Current Date --
           MOVE FUNCTION CURRENT-DATE(1:8) TO RPT-HDR-DATE
           WRITE REPORT-REC FROM RPT-HEADER

           PERFORM 1100-READ-NEXT-TRANS.

       1100-READ-NEXT-TRANS.
           READ TRANS-FILE
               AT END MOVE 'Y' TO EOF-TRANS-SW
               NOT AT END
                   ADD 1 TO WS-RECS-READ
           END-READ.

       2000-PROCESS-TRANSACTIONS.
           MOVE 'Y' TO MSTR-FOUND-SW
           MOVE TR-CUST-ID TO MSTR-CUST-ID
           
      *    -- Random read on VSAM file --
           READ MASTER-FILE
               INVALID KEY 
                   MOVE 'N' TO MSTR-FOUND-SW
           END-READ

           EVALUATE TRUE
               WHEN NOT MSTR-FOUND
                   PERFORM 2300-LOG-ERROR-NOT-FOUND
               WHEN MSTR-FROZEN
                   PERFORM 2400-LOG-ERROR-FROZEN
               WHEN OTHER
                   PERFORM 2100-APPLY-TRANSACTION
           END-EVALUATE

           PERFORM 1100-READ-NEXT-TRANS.

       2100-APPLY-TRANSACTION.
           MOVE MSTR-CURR-BAL TO WS-NEW-BALANCE

           EVALUATE TRUE
               WHEN TR-DEPOSIT
                   ADD TR-AMOUNT TO WS-NEW-BALANCE
                   ADD TR-AMOUNT TO WS-TOTAL-DEPOSITS
               WHEN TR-WITHDRAWAL
                   SUBTRACT TR-AMOUNT FROM WS-NEW-BALANCE
                   IF WS-NEW-BALANCE < 0
                       DISPLAY 'OVERDRAFT FOR CUST: ' MSTR-CUST-ID
                       SUBTRACT WS-OVERDRAFT-FEE FROM WS-NEW-BALANCE
                   END-IF
               WHEN TR-FEE
                   SUBTRACT TR-AMOUNT FROM WS-NEW-BALANCE
           END-EVALUATE

           PERFORM 2200-UPDATE-MASTER-AND-REPORT.

       2200-UPDATE-MASTER-AND-REPORT.
           MOVE WS-NEW-BALANCE TO RPT-NEW-BAL
           MOVE MSTR-CURR-BAL  TO RPT-OLD-BAL
           MOVE WS-NEW-BALANCE TO MSTR-CURR-BAL

      *    -- Rewrite updated VSAM record --
           REWRITE MASTER-REC
               INVALID KEY
                   DISPLAY 'CRITICAL REWRITE ERROR ON ID: ' MSTR-CUST-ID
           END-REWRITE

           MOVE MSTR-CUST-ID TO RPT-CUST-ID
           MOVE MSTR-NAME    TO RPT-NAME
           
           EVALUATE TRUE
               WHEN TR-DEPOSIT    MOVE 'DEPOSIT'    TO RPT-TYPE
               WHEN TR-WITHDRAWAL MOVE 'WITHDRAWAL' TO RPT-TYPE
               WHEN TR-FEE        MOVE 'SERVICE FEE' TO RPT-TYPE
           END-EVALUATE

           WRITE REPORT-REC FROM RPT-DETAIL
           ADD 1 TO WS-RECS-PROC.

       2300-LOG-ERROR-NOT-FOUND.
           MOVE TR-CUST-ID TO ERR-CUST-ID
           MOVE 'ACCOUNT NUMBER NOT FOUND IN VSAM MASTER FILE' TO ERR-MSG
           WRITE ERROR-REC FROM ERR-DETAIL
           ADD 1 TO WS-ERR-COUNT.

       2400-LOG-ERROR-FROZEN.
           MOVE TR-CUST-ID TO ERR-CUST-ID
           MOVE 'TRANSACTION REJECTED: ACCOUNT STATUS IS FROZEN' TO ERR-MSG
           WRITE ERROR-REC FROM ERR-DETAIL
           ADD 1 TO WS-ERR-COUNT.

       3000-TERMINATE.
           DISPLAY '--- PROCESSING COMPLETE ---'
           DISPLAY 'TOTAL RECORDS READ    : ' WS-RECS-READ
           DISPLAY 'TOTAL RECS PROCESSED  : ' WS-RECS-PROC
           DISPLAY 'TOTAL ERRORS LOGGED   : ' WS-ERR-COUNT
           DISPLAY 'TOTAL VALUE DEPOSITED : ' WS-TOTAL-DEPOSITS
           
           CLOSE TRANS-FILE
                 MASTER-FILE
                 REPORT-FILE
                 ERROR-FILE.
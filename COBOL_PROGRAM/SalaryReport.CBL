       IDENTIFICATION DIVISION.
       PROGRAM-ID. REP001.
       AUTHOR.     IBM-SAMPLE-PROG.
       DATE-WRITTEN. 2026-09-20.
      *================================================================*
      * BU PROGRAM BIR CALISAN DOSYASINI (INPUT) OKUR VE MAAS DETAYLI  *
      * BIR YAZICI RAPORU (OUTPUT) URETIR. Toplam satirlar ve sayfa     *
      * numarasi hesaplamalarini icerir.                               *
      *================================================================*

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT EMP-FILE ASSIGN TO "EMP_INPUT.DAT"
               ORGANIZATION IS LINE SEQUENTIAL
               FILE STATUS IS WS-EMP-STATUS.

           SELECT REPORT-FILE ASSIGN TO "EMP_REPORT.TXT"
               ORGANIZATION IS LINE SEQUENTIAL
               FILE STATUS IS WS-REP-STATUS.

       DATA DIVISION.
       FILE SECTION.
       FD  EMP-FILE.
       01  EMP-RECORD.
           05  EMP-ID             PIC X(05).
           05  EMP-NAME.
               10 EMP-FIRST-NAME PIC X(15).
               10 EMP-LAST-NAME  PIC X(15).
           05  EMP-DEPT           PIC X(10).
           05  EMP-SALARY         PIC 9(06)V99.
           05  FILLER             PIC X(27).

       FD  REPORT-FILE.
       01  REPORT-RECORD          PIC X(80).

       WORKING-STORAGE SECTION.
      *--- DOSYA DURUM KONTROLLERİ ---
       01  WS-FILE-STATUSES.
           05  WS-EMP-STATUS      PIC X(02).
           05  WS-REP-STATUS      PIC X(02).
           05  WS-EOF-SWITCH      PIC X(01) VALUE "N".
               88 END-OF-FILE               VALUE "Y".

      *--- HESAPLAMA ALANLARI ---
       01  WS-COUNTERS-AND-TOTALS.
           05  WS-PAGE-COUNT      PIC 9(03) VALUE ZERO.
           05  WS-LINE-COUNT      PIC 9(02) VALUE 99.
           05  WS-LINE-LIMIT      PIC 9(02) VALUE 40.
           05  WS-EMP-COUNT       PIC 9(05) VALUE ZERO.
           05  WS-TOTAL-SALARY    PIC 9(09)V99 VALUE ZERO.

       01  WS-CURRENT-DATE.
           05  WS-YEAR            PIC X(04).
           05  WS-MONTH           PIC X(02).
           05  WS-DAY             PIC X(02).

      *--- RAPOR SATIR TASARIMLARI ---
       01  HEADING-1.
           05  FILLER             PIC X(06) VALUE "TARIH:".
           05  HD-DATE.
               10 HD-DAY          PIC X(02).
               10 FILLER          PIC X(01) VALUE "/".
               10 HD-MONTH        PIC X(02).
               10 FILLER          PIC X(01) VALUE "/".
               10 HD-YEAR         PIC X(04).
           05  FILLER             PIC X(15) VALUE SPACES.
           05  FILLER             PIC X(24) VALUE "CALISAN MAAS RAPORU".
           05  FILLER             PIC X(15) VALUE SPACES.
           05  FILLER             PIC X(06) VALUE "SAYFA:".
           05  HD-PAGE-NUM        PIC ZZZ9.

       01  HEADING-2.
           05  FILLER             PIC X(80) VALUE ALL "=".

       01  HEADING-3.
           05  FILLER             PIC X(07) VALUE "ID     ".
           05  FILLER             PIC X(32) VALUE "AD SOYAD                        ".
           05  FILLER             PIC X(12) VALUE "DEPARTMAN   ".
           05  FILLER             PIC X(15) VALUE "BRUT MAAS      ".

       01  HEADING-4.
           05  FILLER             PIC X(80) VALUE ALL "-".

       01  DETAIL-LINE.
           05  DET-EMP-ID         PIC X(05).
           05  FILLER             PIC X(02) VALUE SPACES.
           05  DET-EMP-NAME       PIC X(30).
           05  FILLER             PIC X(02) VALUE SPACES.
           05  DET-DEPT           PIC X(10).
           05  FILLER             PIC X(02) VALUE SPACES.
           05  DET-SALARY         PIC $ZZZ,ZZ9.99.
           05  FILLER             PIC X(17) VALUE SPACES.

       01  TOTAL-LINE-1.
           05  FILLER             PIC X(80) VALUE ALL "-".

       01  TOTAL-LINE-2.
           05  FILLER             PIC X(20) VALUE "TOPLAM CALISAN     :".
           05  TOT-EMP-COUNT      PIC ZZ,ZZ9.
           05  FILLER             PIC X(55) VALUE SPACES.

       01  TOTAL-LINE-3.
           05  FILLER             PIC X(20) VALUE "TOPLAM ODENEN MAAS :".
           05  TOT-SALARY         PIC $ZZZ,ZZZ,ZZ9.99.
           05  FILLER             PIC X(45) VALUE SPACES.

       PROCEDURE DIVISION.
       0000-MAIN-LOGIC.
           PERFORM 1000-INITIALIZE
           PERFORM 2000-PROCESS-DATA UNTIL END-OF-FILE
           PERFORM 3000-TERMINATE
           STOP RUN.

       1000-INITIALIZE.
           OPEN INPUT EMP-FILE
           IF WS-EMP-STATUS NOT = "00"
               DISPLAY "HATA: GIRIS DOSYASI ACILAMADI! STATUS: " 
                       WS-EMP-STATUS
               STOP RUN
           END-IF.

           OPEN OUTPUT REPORT-FILE
           IF WS-REP-STATUS NOT = "00"
               DISPLAY "HATA: RAPOR DOSYASI ACILAMADI! STATUS: " 
                       WS-REP-STATUS
               CLOSE EMP-FILE
               STOP RUN
           END-IF.

           ACCEPT WS-CURRENT-DATE FROM DATE YYYYMMDD
           MOVE WS-DAY   TO HD-DAY
           MOVE WS-MONTH TO HD-MONTH
           MOVE WS-YEAR  TO HD-YEAR

           PERFORM 4000-READ-EMP-FILE.

       2000-PROCESS-DATA.
           IF WS-LINE-COUNT > WS-LINE-LIMIT
               PERFORM 5000-WRITE-HEADERS
           END-IF

           MOVE EMP-ID TO DET-EMP-ID
           
           STRING EMP-FIRST-NAME DELIMITED BY "  "
                  " "            DELIMITED BY SIZE
                  EMP-LAST-NAME  DELIMITED BY "  "
                  INTO DET-EMP-NAME
           END-STRING
           
           MOVE EMP-DEPT   TO DET-DEPT
           MOVE EMP-SALARY TO DET-SALARY

           WRITE REPORT-RECORD FROM DETAIL-LINE
           ADD 1 TO WS-LINE-COUNT
           
           ADD 1          TO WS-EMP-COUNT
           ADD EMP-SALARY TO WS-TOTAL-SALARY

           PERFORM 4000-READ-EMP-FILE.

       3000-TERMINATE.
           IF WS-EMP-COUNT > 0
               WRITE REPORT-RECORD FROM TOTAL-LINE-1
               MOVE WS-EMP-COUNT    TO TOT-EMP-COUNT
               WRITE REPORT-RECORD FROM TOTAL-LINE-2
               MOVE WS-TOTAL-SALARY TO TOT-SALARY
               WRITE REPORT-RECORD FROM TOTAL-LINE-3
           ELSE
               MOVE "DOSYADA ISLENECEK KAYIT BULUNAMADI." 
                   TO REPORT-RECORD
               WRITE REPORT-RECORD
           END-IF.

           CLOSE EMP-FILE
           CLOSE REPORT-FILE
           DISPLAY "PROSES BASARIYLA TAMAMLANDI. RAPOR URETILDI.".

       4000-READ-EMP-FILE.
           READ EMP-FILE
               AT END
                   MOVE "Y" TO WS-EOF-SWITCH
               NOT AT END
                   NEXT SENTENCE
           END-READ.

       5000-WRITE-HEADERS.
           ADD 1 TO WS-PAGE-COUNT
           MOVE WS-PAGE-COUNT TO HD-PAGE-NUM
           
           IF WS-PAGE-COUNT > 1
               MOVE ADVANCING PAGE TO REPORT-RECORD
               WRITE REPORT-RECORD
           END-IF

           WRITE REPORT-RECORD FROM HEADING-1
           WRITE REPORT-RECORD FROM HEADING-2
           WRITE REPORT-RECORD FROM HEADING-3
           WRITE REPORT-RECORD FROM HEADING-4
           
           MOVE 5 TO WS-LINE-COUNT.

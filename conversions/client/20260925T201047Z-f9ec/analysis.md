# Program analizi

## Kanıt ve amaç

Tam program kapsamı seçildi. Aşağıdaki tüm satır referansları 'COBOL_PROGRAM/CLIENT.CBL'ye aittir. 20-28. satırlar, istemci bağlantı ayarlarının sorgulanması ve ayarlanması için bir IBM DB2 örneğini tanımlar. 43-66. satırlar, sabit formatlı sütunlara sahip Tanımlama, Veri ve Prosedür bölümlerini içerir. Kaynak yalnızca ASCII baytlarına ve CRLF sonlarına sahiptir; özgün kod sayfası, derleyici seçenekleri ve DB2 çalıştırma zamanı sürümü bilinmiyor. Bildirilen bir giriş dosyası veya terminal okuması yok. Program DB2 API'lerini çağırır ve konsol `DISPLAY` çıktısını (`:69-251`) yazar.

## Veri tanımları

| Kaynak alanı / kaydı | PIC, uzunluk, ölçek, kodlama | Başlatma / koşullar | Önerilen Java gösterimi |
| --- | --- | --- | --- |
| 'rc' (':55') | 'PIC S9(9) COMP-5' | API dönüş hedefi, burada hiçbir zaman denetlenmedi | Yerel imzalı tamsayı; tam ABI, DB2 tanımlarına bağlıdır. |
| 'hata' (':56') | 'RESİM X(80)' | "checkerr" öncesindeki etiketleri sorgulamak/ayarlamak için ayarla (`:88-89,120-121,133-134,153-154`) | Hata-işlem etiketi; COBOL pedleri 80 genişliğe taşıyın. |
| 'liste numarası' (`:59`) | 'PIC S9(4) COMP-5 DEĞER 4' | Her DB2 API çağrısına değere göre aktarıldı | Dört ayar girişi; ABI çözülmedi. |
| 'varsayılan ayarlar' (`:61-64`) | Her biri iki 'PIC S9(4) COMP-5 VALUE 0' alanına sahip beş oluşum | Yalnızca "varsayılan değer(1..4)" kaydedilir ve geri yüklenir (`:93-97,137-141`); beşinci öğe ve 'varsayılan tür' burada kullanılmadı | Giriş akışı seçilirse yerel istek durumunda kaydedilen dört değer. |
| 'SQLE-BAĞLANTI AYARI', 'SQLE-BAĞLANTI TİPİ', 'SQLE-BAĞLANTI-DEĞERİ' | Kullanılamayan 'sqlenv.cbl' (':49'); IBM Db2 11.5 belgeleri, iki "PIC S9(4) COMP-5" alanının yedi örneğini gösteriyor | Burada kullanılan ilk dört giriş | Dört tip/değer çifti; dağıtılan kopya kitabı revizyonu ve sayısal sabitler doğrulanmadı. |
| 'SQLCA', 'SQL-*' sabitleri | Kullanılamayan 'sqlca.cbl' ve 'sql.cbl' (`:50-51`) | Hem bölümlerde hem de `checkerr'de kullanılır | Hata düzeni ve sabit değerler çözülmedi. |

Giriş kaynağında hiçbir "REDEFINES", seviye-88 koşulu, ondalık ölçek veya yerel "COMP-3" alanı görünmüyor; eksik kopya defterleri bunları tanımlayabilir.


## Dış bağımlılıklar ve G/Ç

`COPY "sqlenv.cbl"`, `"sql.cbl"` ve `"sqlca.cbl"`, `:49-51`de görünür; bu çalışma alanında hiçbiri bulunamadı. "sqlgqryc", "sqlgsetc" ve "checkerr" çağrıları ":82-89,114-121,127-134,147-154" konumunda gerçekleşir. Genel bir IBM "checkerr.cbl" örneği "referanslar/" altında korunur; gerçek bağla
ntılı sürümden farklı olabilir. 'Checkerr' bölümü hemen SQLCODE 0'a döner, sıfırdan farklı SQLCODE için hata

IBM Db2 11.5 belgeleri, "sqlgqryc"in ayar türü/değer dizisini giriş/çıkış olarak aldığını ve "sqlgsetc"nin bunu giriş olarak aldığını doğrular; her ikisi de 0-7 ayarlarını kabul eder. Belgelenen COBOL yapısı, her biri 'PIC S9(4) COMP-5' türü ve değer alanlarına sahip yedi öğeden oluşur. Bu, dört öğeli çağrı şeklini destekler ancak dağıtılan çalışma zamanı sürümünü veya sayısal sabitleri oluşturmaz. Dokümantasyon URL'leri için 'referanslar/README.md'ye bakın.
raporu yazar, olumlu uyarılardan sonra devam eder ve olumsuz hatalardan sonra programı durdurur. Ayrıca Db2 hatası ve SQLSTATE mesajları için `sqlgintp` ve `sqlggstt`yi çağırır. Db2 çağrıları referansa göre 'SQLE-CONN-SETTING' ve 'SQLCA'yı, değere göre 'listnumber'ı geçirir ve 'rc'yi döndürür; 'checkerr', 'SQLCA' ve 'errloc'u alır. Giriş kaynağında satır içi SQL/CICS komutları, "REPLACE", dosya tanımlayıcıları veya şemalar yok. Db2 istemci ayarı durumu, bağlantı sahipliği, eşzamanlılık ve tam hata metni, kalan bağımlılıklar olmadan tanımsız kalır.

## İş kuralları ve anlamsal riskler

İstenen dört değiştirme kodu şunlardır: "SQL-CONNECT-2", "SQL-RULES-STD", "SQL-DISCONNECT-COND" ve "SQL-SYNC-TWOPHASE" (":100-103"). Kod 'rc'yi denetlemez; genel IBM `checkerr' referansı, olumlu SQLCODE uyarılarının devam ettiğini ve olumsuz hataların durduğunu gösterir. Yalnızca çalıştırmaları geri yükleme




## Seçilen kontrol akışı ve bağımlılık kapatma

'Client-pgm' (':67-156') giriş bölümü dört ayar türü kodunu (':72-75') ayarlar, 'sqlgqryc' ile sorgular, 'checkerr'ı çağırır ve 'yazdırma sorgusu' (':77-91') gerçekleştirir. Döndürülen dört değeri kaydeder (`:93-97`), dört sabit kodlanmış değiştirme değeri atar (`:100-103`), `sqlgsetc` ve `checkerr` (`:105-121`) çağrılarını yapar, tekrar sorgular ve yazdırır (`:123-136`), ardından kaydedilen değerleri başka bir `sqlgsetc` ve `checkerr` (`:137-154`) ile geri yükler. 'ÇALIŞTIRMAYI DURDUR' (':156'), 'yazdırma sorgusu' bölümüne (':158-251') geçmeyi önler. İkincisi iki kez yapılır (`:91,136`) ve 'EXIT' ile biter. Döngü yok, 'PERFORM THRU', 'GO TO', işlem dağıtıcısı veya dosya EOF mantığı görünmüyor.

Seçilen programın tamamı için her iki bölüm de gereklidir. Bağımlılık kapatma, dört ayar türü kodunu, her sorgudan dört değeri, orijinal değer yakalamayı, değiştirme kodlarını, iki ayar işlemini, her harici çağrı için 'SQLCA' artı 'checkerr'ı ve tüm 'DISPLAY' çıkışını içerir. 'print-query' sorgulanan değerlere ve kopya defteri sabitlerine ihtiyaç duyar. Harici API ve hata semantiği, kopya defterleri, "checkerr" ve hedef Db2 bağlamına kadar çözümlenmeden kalır.



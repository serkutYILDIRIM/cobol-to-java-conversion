# Program analizi

## Kanıt ve amaç

`COBOL_PROGRAM/SalaryReport.CBL`, `REP001` adını taşıyan, sabit biçimli bir programdır (satır 1-4). Program, satır sıralı (line-sequential) bir çalışan giriş dosyası ile yine satır sıralı bir rapor çıktı dosyası açar (satır 11-20); her bir çalışana ilişkin verileri işler ve tarih bilgisi içeren, sayfalandırılmış; detay satırları ve toplam satırlarından oluşan bir maaş raporu oluşturur (satır 108-201).

Kaynak dosya, BOM (Byte Order Mark) içermeyen, geçerli UTF-8 kodlamasına sahiptir ve satır sonlarında CRLF karakterlerini kullanır. Hangi COBOL derleyicisinin veya lehçesinin kullanıldığı belirtilmemiştir. Mevcut `PATH` üzerinde herhangi bir COBOL derleyicisi tespit edilemediğinden, burada açıklanan davranışların hiçbiri fiili bir COBOL yürütme sonucuna dayanmamaktadır.

## Veri tanımları

| Kaynak alan / kayıt | PIC, uzunluk, ölçek | Başlatma / koşullar | Java karşılığı |
| --- | --- | --- | --- |
| `EMP-RECORD` | Grup, 80 karakter | Her bir satır sıralı okuma işleminde doldurulur | Tam olarak 80 Unicode karakteri gerektiren katı UTF-8 sınır ayrıştırıcısı (D-07) |
| `EMP-ID` | `X(05)`, 5 | Giriş verisinde 1-5. konumlar | Beş karakterlik `String`; sınır dolgu karakterleri korunur |
| `EMP-FIRST-NAME` | `X(15)`, 15 | Giriş verisinde 6-20. konumlar | Sabit genişlikli `String`; iki boşluklu ayırıcı semantiği korunur |
| `EMP-LAST-NAME` | `X(15)`, 15 | Giriş verisinde 21-35. konumlar | Sabit genişlikli `String`; iki boşluklu ayırıcı semantiği korunur |
| `EMP-DEPT` | `X(10)`, 10 | Giriş verisinde 36-45. konumlar | Sabit genişlikli `String` |
| `EMP-SALARY` | `9(06)V99`, 8 basamak, ölçek 2 | Giriş verisinde 46-53. konumlar; işaretsiz, sayısal görüntüleme biçimi (display numeric) | Ölçek değeri 2 olan, sekiz basamaklı (ölçeksiz) sayıdan oluşturulmuş `BigDecimal` |
| Giriş dolgu alanı | `X(27)`, 27 | 54-80 arası giriş konumları | Yalnızca giriş sınırında doğrulanır/korunur; iş mantığı açısından bir anlamı yoktur |
| `REPORT-RECORD` | `X(80)`, 80 | Her bir rapor satırını alır | Satır ayırıcıdan önceki tam 80 karakterlik rapor satırı |
| Dosya durumları | İki adet `X(02)` alanı | COBOL dosya işlemleri tarafından ayarlanır | 400/422/500 HTTP hata yanıtlarıyla eşleşen, türü belirlenmiş Java istisnaları (D-07) |
| EOF (Dosya sonu) anahtarı | `X(01)`, başlangıç ​​değeri `N`; 88. seviye değeri `Y` | "At-end" (dosya sonu) okuma işleminden sonra ayarlanır | Yerel döngü/akış sonu durumu |
| Sayfa sayısı | `9(03)`, başlangıç ​​değeri 0 | Her başlıkta artırılır | İstek başına tam sayı; kaynak kapasitesi 999 |
| Satır sayısı/sınırı | `9(02)`, başlangıç ​​değeri 99; sınır 40 | Başlıklardan sonra 5 değerine sıfırlanır | İstek başına sayfalama durumu |
| Çalışan sayısı | `9(05)`, başlangıç ​​değeri 0 | İşlenen her kayıt için artırılır | İstek başına tam sayı; kaynak kapasitesi 99.999 |
| Toplam maaş | `9(09)V99`, başlangıç ​​değeri 0 | Maaşların toplamı | İstek başına `BigDecimal`; kaynak kapasitesi 999.999.999,99 |
| Mevcut tarih | YYYY/MM/DD formatında `X(08)` | Başlatma sırasında bir kez alınır | Enjekte edilen bir `Clock` nesnesinden elde edilen `LocalDate` |
| `DET-EMP-NAME` | `X(30)` | `STRING` işlemleri arasında ayrıca başlatılmaz | Her çalışan için boşluklarla doldurulmuş, yeni 30 karakterlik değer (D-03) |
| Biçimlendirilmiş maaş | `$ZZZ,ZZ9.99` | Çalışan maaşını alır | Yerel ayardan bağımsız, COBOL tarzı biçimlendirici (11 karakter) |
| Düzenlenmiş toplamlar | `ZZ,ZZ9` ve `$ZZZ,ZZZ,ZZ9.99` | Nihai sayı ve maaş değerlerini alır | Yerel ayardan bağımsız, COBOL tarzı biçimlendiriciler |

`COMP`, `COMP-3`, `REDEFINES` veya `OCCURS` alanları bulunmamaktadır. Tek `level-88` koşulu `END-OF-FILE` ifadesidir (42-43. satırlar).

## Seçilmiş kontrol akışı ve bağımlılık yapısı

Programın tamamının yürütülmesi `0000-MAIN-LOGIC` ile başlar: başlatma işlemleri yapılır, dosya sonuna (EOF) ulaşılana kadar mevcut kayıtlar işlenir, sonlandırma işlemleri gerçekleştirilir ve ardından program durdurulur (109-113. satırlar).

`1000-INITIALIZE` yordamı giriş ve çıkış dosyalarını açar; açılış başarısız olursa durur, tarihi alır ve ilk okuma işlemini (priming read) gerçekleştirir (115-136. satırlar). İlk okuma işlemi gereklidir; aksi takdirde, boş bir giriş dosyasında işleme döngüsü tanımlanmamış bir kaydı işlemeye çalışabilir.

`2000-PROCESS-DATA` yordamı, her bir detay kaydından önce sayfa düzenini kontrol eder, mevcut giriş kaydını bir detay satırına eşler, bu satırı yazar, satır sayacını ve toplam değerleri günceller, ardından bir sonraki giriş kaydını okur (138-160. satırlar). `4000-READ-EMP-FILE` yordamı, giriş dosyasının sonunda `EOF` durumunu ayarlar (179-185. satırlar). Programda `GO TO`, `THRU`, özyinelemeli (recursive) çağrı veya işlem dağıtımı (transaction dispatch) bulunmamaktadır.

`5000-WRITE-HEADERS` yordamı sayfa numarasını artırır, isteğe bağlı olarak sayfa ilerletme işlemi yapar, dört başlık kaydı yazar ve ardından satır sayacını 5 olarak ayarlar (187-201. satırlar). Sayfa düzeni kontrolü `satır sayısı > 40` koşuluna dayandığından, standart bir sayfaya 36 detay kaydı yazılır; 5'ten 40'a kadar olan satırlar yazıldığında sayaç 41 değerine ulaşır. Bu bilgi çalışma zamanındaki gözlemlerden değil, kaynak kodun kendisinden elde edilmiştir. `3000-TERMINATE` yordamı, en az bir çalışan kaydı okunduğunda toplam üç kayıt (veya okunan kayıt yoksa buna dair bir mesaj) yazar, her iki dosyayı da kapatır ve bir tamamlama mesajı görüntüler (satır 162-177). Bu toplamlar sayfa numaralandırma hesabına dahil edilmez ve belirtilen sayfa sınırını aşabilir.

Tüm çalışma durumu (working state), her bir işlem için ayrı ayrı oluşturulmalıdır. Değiştirilebilir rapor arabellekleri, sayaçlar, tarih bilgileri, toplamlar veya dosya sonu (EOF) bayrakları; tekrarlanan ya da eş zamanlı HTTP istekleri arasında paylaşılamaz.

## Dış bağımlılıklar ve G/Ç (I/O)

- Giriş mantıksal adı: `EMP_INPUT.DAT`; satır sıralı (line sequential) yapıda, nominal kayıt düzeni 80 karakter uzunluğundadır (satır 14-16 ve 24-32).
- Çıkış mantıksal adı: `EMP_REPORT.TXT`; satır sıralı (line sequential) yapıda, tam olarak 80 karakter uzunluğunda kayıtlar alır (satır 18-20 ve 34-42).


# Program analizi

## Kanıtlar ve amaç

Programın tamamı kapsama dahil edilmiştir. Aşağıdaki tüm satır referansları `COBOL_PROGRAM/CLIENT.CBL` dosyasına yöneliktir. 20-28. satırlar, istemci bağlantı ayarlarını sorgulamaya ve yapılandırmaya yönelik bir IBM DB2 örneğini tanımlar. 43-66. satırlar; sabit formatlı sütun yapısına sahip Identification (Tanımlama), Data (Veri) ve Procedure (Yordam) bölümlerini içerir. Kaynak kod yalnızca ASCII baytları ve CRLF satır sonu karakterleri içerir; orijinal kod sayfası, derleyici seçenekleri ve DB2 çalışma zamanı sürümü bilinmemektedir. Bildirilmiş bir girdi dosyası veya terminalden okuma işlemi yoktur. Program DB2 API'lerini çağırır ve konsola `DISPLAY` çıktısı yazar (`:69-251`).

## Veri tanımları

| Kaynak alan / kayıt | PIC, uzunluk, ölçek, kodlama | Başlatma / koşullar | Önerilen Java gösterimi |
| --- | --- | --- | --- |
| `rc` (`:55`) | `PIC S9(9) COMP-5` | API dönüş hedefi; burada incelenmiyor | Yerel işaretli tam sayı (native signed integer); kesin ABI, DB2 tanımlarına bağlıdır. |
| `errloc` (`:56`) | `PIC X(80)` | `checkerr` öncesinde sorgu/ayar etiketlerine atanır (`:88-89,120-121,133-134,153-154`) | Hata işlemi etiketi; COBOL "move" işlemi 80 karakter genişliğine tamamlar (pad). |
| `listnumber` (`:59`) | `PIC S9(4) COMP-5 VALUE 4` | Her DB2 API çağrısına değer olarak iletilir | Dört ayar girdisi; ABI belirlenmemiş. |
| `default-settings` (`:61-64`) | Beş tekrar (occurrence); her biri iki adet `PIC S9(4) COMP-5 VALUE 0` alanı içerir | Yalnızca `default-value(1..4)` kaydedilir ve geri yüklenir (`:93-97,137-141`); beşinci eleman ve `default-type` burada kullanılmaz | Giriş akışı seçilirse, istek yerel durumunda (request-local state) dört kayıtlı değer. | | `SQLE-CONN-SETTING`, `SQLE-CONN-TYPE`, `SQLE-CONN-VALUE` | `sqlenv.cbl` (`:49`) mevcut değil; IBM Db2 11.5 belgeleri, iki adet `PIC S9(4) COMP-5` alanının yedi kez geçtiğini gösteriyor | Burada ilk dört girdi kullanılıyor | Dört tür/değer çifti; dağıtılan copybook revizyonu ve sayısal sabitler doğrulanmadı. |
| `SQLCA`, `SQL-*` sabitleri | `sqlca.cbl` ve `sql.cbl` (`:50-51`) mevcut değil | Her iki bölümde ve `checkerr` içinde kullanılıyor | Hata düzeni ve sabit değerler çözümlenmedi. |

Girdi kaynağında `REDEFINES`, level-88 koşulu, ondalık ölçek veya yerel `COMP-3` alanı görünmüyor; eksik copybook'lar bunları tanımlıyor olabilir.

## Seçili kontrol akışı ve bağımlılık kapanışı

`client-pgm` girdi bölümü (`:67-156`), dört ayar türü kodu belirler (`:72-75`), `sqlgqryc` ile sorgulama yapar, `checkerr`'ı çağırır ve `print-query` işlemini gerçekleştirir (`:77-91`). Dönen dört değeri kaydeder (`:93-97`), kod içine gömülü (hard-coded) dört yedek değer atar (`:100-103`), `sqlgsetc` ve `checkerr`'ı çağırır (`:105-121`), yeniden sorgulama ve yazdırma yapar (`:123-136`) ve ardından başka bir `sqlgsetc` ve `checkerr` çağrısıyla kaydedilen değerleri geri yükler (`:137-154`). `STOP RUN` (`:156`) komutu, akışın `print-query` bölümüne (`:158-251`) geçmesini engeller. Bu son bölüm iki kez çalıştırılır (`:91, 136`) ve `EXIT` ile sona erer. Döngü, `PERFORM THRU`, `GO TO`, işlem dağıtıcısı veya dosya sonu (EOF) mantığı görülmemektedir.

Seçilen tam program için her iki bölüm de gereklidir. Bağımlılık kapsamı; dört ayar türü kodu, her sorgudan alınan dört değer, orijinal değer yakalama, değiştirme kodları, iki küme işlemi, her harici çağrı için `SQLCA` ve `checkerr` ile tüm `DISPLAY` çıktılarını içerir. `print-query` işlevi, sorgulanan değerlere ve copybook sabitlerine ihtiyaç duyar. Harici API ve hata işleme mantığı; ilgili copybook'lar, `checkerr` ve hedef Db2 bağlamı netleşene kadar çözümsüz kalmaktadır.

## Harici bağımlılıklar ve G/Ç

`:49-51` satırlarında `COPY "sqlenv.cbl"`, `"sql.cbl"` ve `"sqlca.cbl"` ifadeleri yer almaktadır; ancak bu çalışma alanında bunlardan hiçbiri bulunamamıştır. `sqlgqryc`, `sqlgsetc` ve `checkerr` çağrıları `:82-89, 114-121, 127-134, 147-154` satırlarında gerçekleşmektedir. `references/` dizininde herkese açık bir IBM `checkerr.cbl` örneği muhafaza edilmektedir; ancak bu örnek, gerçekte bağlanan sürümden farklı olabilir. Bu örneğin `Checkerr` bölümü; SQLCODE 0 olduğunda hemen döner, sıfır olmayan SQLCODE değerlerinde bir hata raporu yazar, pozitif uyarıların ardından işleme devam eder ve negatif hata durumlarında programı durdurur. Ayrıca Db2 hatası ve SQLSTATE mesajları için `sqlgintp` ve `sqlggstt` yordamlarını çağırır. Db2 çağrıları `SQLE-CONN-SETTING` ve `SQLCA`yı referansla, `listnumber`ı ise değerle aktararak `rc` değerini döndürür; `checkerr` ise `SQLCA` ve `errloc` parametrelerini alır. Kaynak kodda satır içi SQL/CICS komutları, `REPLACE` ifadeleri, dosya tanımlayıcıları veya şemalar bulunmamaktadır. Kalan bağımlılıklar olmadan; Db2 istemci ayar durumu, bağlantı sahipliği, eşzamanlılık ve kesin hata metni tanımlanmamış olarak kalmaktadır.

IBM Db2 11.5 belgeleri, `sqlgqryc` yordamının ayar türü/değer dizisini giriş/çıkış olarak, `sqlgsetc` yordamının ise giriş olarak aldığını ve her ikisinin de 0 ila 7 arasında ayarı kabul ettiğini doğrulamaktadır. Belgelenen COBOL yapısı, her biri `PIC S9(4) COMP-5` tipinde ve değer alanlarına sahip yedi öğeden oluşur. Bu yapı, dört öğeli çağrı biçimini destekler ancak dağıtılan çalışma zamanı sürümünü veya sayısal sabitleri belirlemez. Belge bağlantıları için `references/README.md` dosyasına bakın.

## İş kuralları ve anlamsal riskler

İstenen dört yedek kod şunlardır: `SQL-CONNECT-2`, `SQL-RULES-STD`, `SQL-DISCONNECT-COND` ve `SQL-SYNC-TWOPHASE` (`:100-103`). Kod `rc` değerini incelemez; genel IBM `checkerr` referansı, pozitif SQLCODE uyarılarının süreceğini, negatif hataların ise durdurucu nitelikte olduğunu belirtir. Geri yükleme işlemi yalnızca...

# COBOL'dan Java'ya eşleme

| COBOL konumu / sorumluluğu | Java sınıfı / metodu / sözleşmesi | Seçilen veya destekleyici | Kanıt / karar |
| --- | --- | --- | --- |
| 12-14. satırlar, `MAIN-LOGIC` | `HelloController#getHello()` ve `HelloService#getMessage()` | Seçilen | Tüm program kapsamı; kaynak yürütme sırası |
| 9. satır, `OGRENCI-MESAJ PIC X(30)` | `HelloService` sabit genişlikli metin alanı ve `HelloResponse.message` | Destekleyici | Kaynak alan tanımı; D1 |
| 13. satır, `DISPLAY OGRENCI-MESAJ` | `GET /api/hello` JSON yanıtı | Seçilen | D1 |
| 14. satır, `STOP RUN` | Başarılı HTTP yanıtının tamamlanması | Seçilen | Java mimarisi kılavuzu |

## Hariç tutulan işlevler

Yok. Programın tamamı seçilmiştir; başka herhangi bir yordam veya işlem giriş noktası içermemektedir.

## Bilinçli farklılıklar

`DISPLAY` çıktısı, JSON formatında bir HTTP yanıtına uyarlanmıştır. Dolgu boşlukları dahil olmak üzere 30 karakterlik alan değeri korunmuştur. `STOP RUN` komutu JVM'i sonlandırmaz; bunun yerine HTTP işlemini tamamlar.

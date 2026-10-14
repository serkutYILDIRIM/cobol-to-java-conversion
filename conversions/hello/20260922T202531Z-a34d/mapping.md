# COBOL'dan Java'ya eşleme

| COBOL konumu / sorumluluğu | Java sınıfı / metodu / sözleşmesi | Seçilen veya destekleyici | Kanıt / karar |
| --- | --- | --- | --- |
| 12-14. satırlar, `MAIN-LOGIC` | `HelloController#getHello()` ve `HelloService#getMessage()` | Seçilen | Programın tamamını kapsayan kapsam; kaynak yürütme sırası |
| 9. satır, `OGRENCI-MESAJ PIC X(30)` | `HelloService` sabit genişlikli metin (string) ve `HelloResponse.message` | Destekleyici | Kaynak alan tanımı; D1 |
| 13. satır, `DISPLAY OGRENCI-MESAJ` | `GET /api/hello` JSON yanıtı | Seçilen | D1 |
| 14. satır, `STOP RUN` | Normal ve başarılı HTTP yanıtı tamamlanması | Seçilen | Java mimarisi kılavuzu |

## Hariç tutulan işlevsellik

Yok. Programın tamamı seçilmiştir ve başka yordamlar veya işlem giriş noktaları içermemektedir.

## Bilinçli farklılıklar

`DISPLAY` çıktısı, bir JSON HTTP yanıtına uyarlanmıştır. Dolgu boşlukları da dahil olmak üzere 30 karakterlik alan değeri korunmuştur. `STOP RUN` komutu JVM'i sonlandırmaz; bunun yerine HTTP işlemini tamamlar.

# Program analizi

## Kanıtlar ve amaç

<Kaynak biçimini, lehçeyi, girdileri, çıktıları ve amacı tanımlayın. Kaynak yollarını ve satır numaralarını belirtin. Kaynaktan elde edilen gözlemlenebilir olguları, varsayımlardan ayırın.

## Seçilen kontrol akışı ve bağımlılık kapsamı

<Seçilen giriş noktalarını, fiili yürütme sırasını, döngüleri, dallanmaları, ardışık yürütmeleri (fall-through), paylaşılan durumu, başlatma ve temizleme işlemlerini listeleyin. Dahil edilen her bir destekleyici yordamı açıklayın ve dahil edilmeyenlerin neden gereksiz olduğunu belirtin.>

## İş kuralları ve anlamsal riskler

<Hesaplamaları, yuvarlama işlemlerini, taşmaları (overflow), kırpmaları (truncation), sınırlayıcıları, dolgu (padding) işlemlerini, tarihleri, dosya sonu (EOF) davranışlarını, çıktı biçimlendirmesini ve çözülmemiş hataları belgeleyin. İlgili karar kimliklerini (decision ID) referans olarak ekleyin.>

## Dış bağımlılıklar ve G/Ç (I/O)

<Dosyaları, COPY, CALL, SQL, CICS öğelerini, şemaları ve kayıt düzenlerini, durumları ve eksik bileşenleri envanterleyin. Akış/kaynak sahipliğini ve hata durumundaki davranışları belirtin.>


## Java API ve veri erişim sözleşmeleri

| Kullanım senaryosu | Metot ve yol | İstek / doğrulama | Yanıt / durum / hatalar | DAO sözleşmesi |
| --- | --- | --- | --- | --- |
| <seçilen kullanım senaryosu> | <HTTP sözleşmesi> | <girdi> | <çıktı> | <G/Ç> |
kısmi kapsam için gereken durumu, girdi/çıktı aktarımını ve bu oturumu uygulamak için gereken yapılandırmayı tanımlayın. Anlamlı belirsizlikleri kullanıcıyla görüşerek çözümleyin. Belirtilmemiş veya rastgele dosya sistemi yollarını açıkta bırakmayın.>
<Somut DTO alanlarını, 


## İngilizce çıktı çevirileri

| Kaynak konumu | İngilizce karşılığı | Düzen / uyumluluk üzerindeki etkisi | Karar referansı |
| --- | --- | --- | --- |
| <konum> | <metin> | <etki> | <referans> |


## Doğrulama tasarımı

<Normal durumları, sınır değerlerini ve hata senaryolarını ve durum yalıtımı (state-isolation) gerektiren durumları; beklenen sonuçları ve bunların kanıt kaynaklarıyla (gözlemlenen COBOL çalışması, kaynak incelemesi veya kullanıcı kararı) birlikte listeleyin.>

## Veri tanımları

| Kaynak alanı / kaydı | PIC, uzunluk, ölçek, kodlama | Başlatma / koşullar | Önerilen Java gösterimi |
| --- | --- | --- | --- |
| <alan> | <tanım> | <durum> | <tür ve kısıtlamalar> |

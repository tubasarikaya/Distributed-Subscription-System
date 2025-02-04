# Dağıtık Abonelik Sistemi (Distributed Subscriber Service)

22060344 Elif Sude ÇETİNKAYA
22060385 Zeynep Ravza DURSUN
22060374 Tuba SARIKAYA

### plotter.py

### admin.rb

- [x] serverlar ile socket üzerinden iletişim kurar.
- [x] serverlara 5 saniyede bir kapasite sorgusu gönderir.
- [x] serverlara birbirlerine bağlanmak için strt emri gönderir.
- [x] strt emrine karşılık gelen mesaj isteklerini alır.
- [x] kapasite sorgusuna karşılık gelen kapasite değerlerini alır.

### ServerX.java

- [x] admin.rbden gelen kapasite sorgusunu alır.
- [x] gelen kapasite sorgusuna karşılık kapasite değerleri gönderir.
- [x] admin.rbden gelen strt emrini alır.
- [x] gelen strt emrine karşılık yep veya nope mesajları gönderir.
- [x] istemcilerle socketler üzerinden haberleşir.
- [x] istemcilerden subcriber nesnesi alır.
- [x] gelen subscriber nesnelerine göre gerekli işlemleri (abone olma, abonelikten çıkma, aboneyi çevrimiçi / çevrimdışı yapma) yapar.

Sunum Videosu Linki:
https://drive.google.com/file/d/1c_x8DlbgNzi5RSm2wrm_Z2JCiVdy_NjV/view?usp=sharing

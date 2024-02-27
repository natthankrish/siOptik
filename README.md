
# siOptik

## Clone Project
- Install android studio [disini](https://developer.android.com/studio?_gl=1*1d1in5i*_up*MQ..gclid=CjwKCAiArfauBhApEiwAeoB7qD6qQ4QlOSEhVd7cRL1dCel4jpb3KhXPUWifxWPm5Xj4OxoCZLQOqBoC4TcQAvD_BwE&gclsrc=aw.ds) 

- Pada Android Studio, new project from Version Control

- Salin https di bawah ini ke kolom URL pada Android Studio
    ```
    https://gitlab.informatika.org/k02-04-ppl/ocr-android.git
    ```
- Kemudian klik tombol `Clone` dan tunggu proses build 

## Run Program
Ada 2 cara menjalankan progam, yaitu :

- Menggunakan emulator Android studio. Lakukan instalasi dan setup emulator dengan panduan [berikut](https://developer.android.com/codelabs/basic-android-kotlin-compose-emulator?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-1-pathway-2%3F_gl%3D1*i2239y*_up*MQ..%26gclid%3DCjwKCAiAibeuBhAAEiwAiXBoJF1A0nyXQVSxzMkWNUHR-rC_QS-LLzuOQuGAuza8YaTqGqAArIYD6BoC28YQAvD_BwE%26gclsrc%3Daw.ds%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-emulator&_gl=1*4db5j7*_up*MQ..&gclid=CjwKCAiAibeuBhAAEiwAiXBoJF1A0nyXQVSxzMkWNUHR-rC_QS-LLzuOQuGAuza8YaTqGqAArIYD6BoC28YQAvD_BwE&gclsrc=aw.ds#0)

- Menggunakan perangkat(smartphone) Android. Lakukan setup dengan panduan [berikut](https://developer.android.com/codelabs/basic-android-kotlin-compose-connect-device?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-1-pathway-2%3F_gl%3D1*1r7mpl*_up*MQ..%26gclid%3DCjwKCAiAibeuBhAAEiwAiXBoJF1A0nyXQVSxzMkWNUHR-rC_QS-LLzuOQuGAuza8YaTqGqAArIYD6BoC28YQAvD_BwE%26gclsrc%3Daw.ds%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-connect-device&_gl=1*1r7mpl*_up*MQ..&gclid=CjwKCAiAibeuBhAAEiwAiXBoJF1A0nyXQVSxzMkWNUHR-rC_QS-LLzuOQuGAuza8YaTqGqAArIYD6BoC28YQAvD_BwE&gclsrc=aw.ds#6)

## Edit Code Program
Setelah anda berhasil men-cloning progam, pada bagian kiri atas terdapat directory, disarankan Anda untuk mengganti dropdown dari `Project` ke `Android`. Hal ini untuk membuat directory terlihat sederhana pada Android Studio.

Adapun komponen penting pada proyek ini dalam mengedit code progam sebagai berikut:


- File `AndroidManifest.xml`, berisi code yang memuat tampilan aplikasi secara keseluruhan (bisa disebut `parent` dari semua file `.xml` pada code).

- Folder `kotlin com.example.sioptik` atau main project, berisi source code dalam bahasa `kotlin` untuk mengimplemtasikan fungsi-fungsi yang diperlukan pada tampilan aplikasi Android.

- Folder`res` (resourse), berupa kode untuk tampilan pada aplikasi yang ditulis dalam bahasa `.xml`. Folder `res` terdiri dari:

    - Folder `drawable` umumnya berisi gambar (bisa jenis gambar atau berupa file `.xml`). Gambar ini biasa dipakai untuk keperluan gambar folder `layout`.
    - Folder `layout` berisi file `.xml` yang membuat layout atau desain tampilan aplikasi Android.
    - Folder `mipmap` hampir mirip dengan `drawable`, namun ini berisi resolusi gambar.
    - Foder `values` berisi value-value yang disimpan seperti data string, color untuk desain tampilan dan sebagainya. `values` dapat dipanggil pada file `.xml` lainnya(biasanya folder `layout`) untuk ditampilkan pada tampilan atau keperluan mendesain tampilan pada aplikasi Android.

- `Gradle Scripts`, berisi file-file yang mem-build project, ada 2 file build yang biasa diedit untuk keperluan project, yaitu:

    - `build.gradle.kts (Project: siOptik)` berisi plugins yang memuat library. Android sendiri telah menyimpan semua library yang dibutuhkan pada `kotlin("android")`, namun banyaknya versinya membuat dapat terjadi error compatible.
    - `build.gradle.kts (Module: app)` berisi modul-modul dan dependencies yang digunakan (diimport).

    Setiap  perubahan version dan modul, lakukan `sync` jika diminta.


## Author
### Kelompok K02-04








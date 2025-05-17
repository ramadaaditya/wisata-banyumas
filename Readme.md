# 🏞️ Wisata Banyumas

**Wisata Banyumas** adalah aplikasi Android yang menyediakan informasi lengkap mengenai destinasi wisata yang ada di Kabupaten Banyumas, Jawa Tengah. Aplikasi ini ditujukan untuk mempermudah wisatawan dalam menemukan lokasi menarik, lengkap dengan deskripsi, gambar, dan petunjuk arah.

---

## ✨ Fitur Utama

- 🗺️ Menampilkan daftar destinasi wisata populer di Banyumas
- 🔍 Fitur pencarian berdasarkan nama destinasi
- 🖼️ Detail wisata lengkap dengan gambar dan deskripsi
- 📍 Lokasi destinasi terintegrasi dengan Google Maps
- ❤️ Menyimpan destinasi favorit
- 🌙 UI modern dan responsif menggunakan Jetpack Compose

---

## 🛠️ Teknologi yang Digunakan

- **Kotlin** - Bahasa pemrograman utama
- **Jetpack Compose** - Framework UI deklaratif dari Android
- **MVVM Architecture** - Arsitektur yang memisahkan tampilan, logika, dan data
- **ViewModel** - Menyimpan data antarmuka yang tahan terhadap perubahan konfigurasi
- **LiveData / StateFlow** - Manajemen data reaktif
- **Navigation Component** - Navigasi antar layar
- **Room Database** - Penyimpanan data lokal
- **Retrofit** - Library HTTP untuk konsumsi API
- **BuildConfig** - Menyimpan konfigurasi yang bersifat sensitif secara lokal

---

## ⚙️ Konfigurasi Manual (`local.properties`)

Aplikasi ini menggunakan konfigurasi sensitif (seperti API key) yang tidak disimpan langsung di dalam source code. Pengguna diharapkan untuk menambahkan file `local.properties` secara manual di root project.

### Langkah-langkah:

1. Buka (atau buat) file `local.properties` di root project.
2. Tambahkan konfigurasi berikut:

   ```properties
   GOOGLE_MAPS_BASE_URL=https://maps.googleapis.com/
   GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here

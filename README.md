# PixelFrost Launcher

Launcher Android bergaya Pixel dengan efek frosted glass, untuk Android 12 ke atas (target Android 16).

## Isi repo
- `app/`: modul aplikasi, AndroidManifest dengan role HOME, MainActivity placeholder
- `gradle/libs.versions.toml`: versi library terpusat
- `design-preview/home.html`: preview layar utama, buka langsung di browser
- `docs/ARCHITECTURE.md`: arsitektur, penyimpanan, dan batasan

## Cara menjalankan
1. Buka folder ini di Android Studio (Ladybug atau lebih baru).
2. Tunggu Gradle sync selesai.
3. Pilih Build > Build APK(s), atau Run ke HP.

Catatan: folder `gradle/wrapper/` belum ada. Android Studio akan membuatnya saat sync, atau jalankan `gradle wrapper` di terminal.

## Status
- [x] Blueprint arsitektur
- [x] Kerangka proyek Gradle dan manifest HOME
- [x] Preview layar utama (`design-preview/home.html`)
- [ ] Tahap 1: FrostContainer dengan backdrop blur (Haze)
- [ ] Tahap 2: grid dan app drawer
- [ ] Tahap 3: widget, folder, dan pencarian
- [ ] Tahap 4: gestur, backup, dan pengaturan

## Stack
- Kotlin, Jetpack Compose, Material 3
- Room (tata letak), DataStore (pengaturan visual)
- minSdk 31, targetSdk 36
- Haze untuk backdrop blur (ditambahkan di Tahap 1)

## Catatan
- Biometrik hanya mengunci peluncuran dari launcher ini, bukan seluruh sistem.
- Blur dibatasi anggarannya agar tetap ringan di chipset kelas menengah.

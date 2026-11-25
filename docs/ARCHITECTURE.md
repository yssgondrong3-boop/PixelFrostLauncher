# Arsitektur PixelFrost

## Modul
- `app/`: entry point, MainActivity, AndroidManifest (role HOME).
- `core/designsystem/`: FrostContainer, GrainOverlay, SpringModifiers, token warna Light Frost dan Dark Smoked Frost, MonetExtractor, Type.
- `core/system/`: AppMonitor, PrivateSpaceManager, LauncherWidgetHost, AppLockAuthenticator, LauncherGestureService.
- `core/data/`: Room (LauncherDatabase, WorkspaceDao, AppCacheDao), DataStore (ThemePreferences), BackupManager.
- `feature/home/`: grid multi-halaman, folder, dock, At a glance.
- `feature/drawer/`: daftar aplikasi, fast scroll A-Z, pencarian.
- `feature/settings/`: appearance dan backup.

## Penyimpanan
- Room: posisi item di grid, folder, cache daftar aplikasi.
- DataStore: radius blur, opasitas, ketebalan border, grain, grid, pemetaan gestur.

## Blur
- Efek kaca memakai backdrop blur (Haze). RenderEffect saja tidak cukup karena hanya memburamkan isi View itu sendiri.
- Anggaran blur: maksimal 2 sampai 3 layer aktif bersamaan agar ringan di chipset menengah.

## Batasan
- Biometrik hanya mengunci peluncuran dari launcher, bukan seluruh sistem.
- Private Space dan Work Profile hanya ditampilkan, tidak dikelola penuh.
- Double tap untuk kunci layar membutuhkan AccessibilityService.
- Live Updates bergantung pada aplikasi sumber notifikasi.

## SDK
- minSdk 31, compileSdk dan targetSdk 36.
- Compose BOM, Material 3, Room, DataStore, Coil, Hilt (pilih satu DI).

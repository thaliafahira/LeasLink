# LEASLINK

## 📌 Deskripsi Singkat
Aplikasi desktop berbasis Java dengan paradigma OOP yang digunakan untuk mengelola:
- Data kontrak
- Transaksi pelanggan
- Aging piutang (penagihan berdasarkan jatuh tempo)

Terdapat tiga jenis pengguna: Admin, Staff, dan Customer, dengan hak akses yang berbeda.

---

## ▶️ Cara Menjalankan Aplikasi

1. Buka IDE seperti IntelliJ atau VS Code
2. Buka folder `src/` dan jalankan class `Main.java`
3. Pastikan Java SDK sudah diinstall (versi minimal: Java 8)

---

## 🔧 Modul yang Diimplementasikan

| Modul           | Deskripsi                            | PIC           | Screenshot |
|----------------|--------------------------------------|----------------|------------|
| Login/Signup    | Autentikasi multi-role pengguna       | Thalia         | ![img](doc/login.png) |
| Kontrak         | Melihat & menambah data kontrak       | [Nama]         | ![img](doc/kontrak.png) |
| Transaksi       | Melihat riwayat transaksi pengguna    | [Nama]         | ![img](doc/transaksi.png) |
| Aging Piutang   | Laporan umur piutang per user         | [Nama]         | ![img](doc/aging.png) |

---

## 🗃️ Tabel Basis Data

| Tabel         | Atribut                                   |
|---------------|--------------------------------------------|
| users         | id, username, password, role              |
| kontrak       | id_kontrak, user_id, nama_proyek, tanggal |
| transaksi     | id_transaksi, user_id, jumlah, tanggal    |
| piutang       | id_piutang, user_id, jumlah, due_date     |

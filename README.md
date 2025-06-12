# LeasLink

**LeasLink** adalah aplikasi desktop yang dikembangkan untuk mendukung operasional PT Mitra dalam mengelola pembiayaan kendaraan bermotor. Aplikasi ini mencakup pencatatan kontrak pembiayaan, otomatisasi pencatatan pembayaran, pelaporan aging piutang secara real-time, dan kontrol akses berbasis peran.

---

## 🛠️ Cara Menjalankan Aplikasi

### 🔹 Metode 1: Menggunakan JAR Pre-built (Direkomendasikan)

1. **Clone Repository**

   ```bash
   git clone https://github.com/thaliafahira/LeasLink.git
   cd leaslink
   ```

2. **Jalankan Aplikasi**

   ```bash
   java -jar target/leaslink.jar
   ```

   🔁 *Jika belum ada file JAR, gunakan perintah berikut:*

   ```bash
   mvn exec:java -Dexec.mainClass="com.leaslink.Main"
   ```

### 🔹 Metode 2: Build dari Sumber Kode

1. **Cek Prasyarat**

   ```bash
   java -version   # Java 17+
   mvn -version    # Maven 3.6+
   ```

2. **Build Aplikasi**

   ```bash
   mvn clean compile
   mvn test
   mvn package
   ```

3. **Jalankan**

   ```bash
   java -jar target/leaslink.jar
   ```

### 🔹 Metode 3: Mode Pengembangan

1. **Setup IDE**

   * Import project sebagai Maven Project
   * Gunakan Java SDK 17+
   * Jalankan `Main.java`

2. **Via Command Line**

   ```bash
   mvn compile exec:java -Dexec.mainClass="com.leaslink.Main"
   ```

---

## 📁 Struktur Proyek

```
leaslink/
├── src/main/java/com/leaslink/
│   ├── controllers/    # Logika bisnis
│   ├── models/         # Model data
│   ├── utils/          # Kelas utilitas
│   └── views/          # Komponen antarmuka
├── tests/              # Unit test
├── doc/                # Dokumentasi dan screenshot
├── img/                # Aset gambar
├── pom.xml             # Konfigurasi Maven
└── README.md
```

---

## 🔧 Modul yang Diimplementasi

### 1. 🔐 Authentication Module

* **Deskripsi**: Autentikasi dengan kontrol akses berbasis peran.
* **Fitur**:

  * Registrasi pengguna & validasi email
  * Password terenkripsi
  * Hak akses: `admin`, `management`, `collector`, `customer`
* **Penanggung Jawab**: \Hakim
* **Tampilan**:
  ![Auth Screenshot](doc/auth_module.png)

---

### 2. 📄 Contract Module

* **Deskripsi**: Pengelolaan siklus kontrak pembiayaan kendaraan.
* **Fitur**:

  * Buat/edit kontrak
  * Alur approval multi-step
  * Tracking status kontrak
  * Pencarian & filter
* **Penanggung Jawab**: \Habibie
* **Tampilan**:
  ![Contract Screenshot](doc/contract_module.png)

---

### 3. 💰 Riwayat Transaksi Module

* **Deskripsi**: Mencatat & menampilkan histori pembayaran.
* **Fitur**:

  * Tracking riwayat cicilan per kontrak
  * Fitur catatan pembayaran
* **Penanggung Jawab**: \Alvin
* **Tampilan**:
  ![Transaction Screenshot](doc/riwayat_module.png)

---

### 4. 📊 Aging Reports Module ✅

* **Deskripsi**: Laporan aging piutang secara real-time.
* **Fitur**:

  * Perhitungan aging dinamis
  * Laporan berdasarkan tanggal jatuh tempo
* **Penanggung Jawab**: \Thalia
* **Tampilan**:
  ![Aging Report Screenshot](doc/aging_module.png)

---

## 🗃️ Struktur Tabel Basis Data

### `users`

| Kolom       | Tipe      | Keterangan                                     |
| ----------- | --------- | ---------------------------------------------- |
| id          | INTEGER   | PRIMARY KEY AUTOINCREMENT                      |
| full\_name  | TEXT      | Nama lengkap                                   |
| email       | TEXT      | UNIQUE, alamat email                           |
| phone       | TEXT      | Nomor telepon                                  |
| password    | TEXT      | Password (hash)                                |
| role        | TEXT      | 'admin', 'management', 'collector', 'customer' |
| created\_at | TIMESTAMP | Default `CURRENT_TIMESTAMP`                    |
| updated\_at | TIMESTAMP | Update otomatis via trigger                    |

🔁 *Trigger: `update_users_timestamp`*

---

### `motorcycles`

| Kolom            | Tipe      | Keterangan                           |
| ---------------- | --------- | ------------------------------------ |
| id               | INTEGER   | PRIMARY KEY AUTOINCREMENT            |
| brand            | TEXT      | Merek motor                          |
| model            | TEXT      | Model motor                          |
| year             | INTEGER   | Tahun keluaran                       |
| engine\_capacity | TEXT      | Kapasitas mesin                      |
| color            | TEXT      | Warna                                |
| chassis\_number  | TEXT      | UNIQUE                               |
| engine\_number   | TEXT      | UNIQUE                               |
| price            | DECIMAL   | Harga                                |
| status           | TEXT      | 'available', 'leased', 'maintenance' |
| created\_at      | TIMESTAMP |                                      |
| updated\_at      | TIMESTAMP |                                      |

---

### `leases`

| Kolom            | Tipe      | Keterangan                             |
| ---------------- | --------- | -------------------------------------- |
| id               | INTEGER   | PRIMARY KEY                            |
| customer\_id     | INTEGER   | FK ke `users(id)`                      |
| motorcycle\_id   | INTEGER   | FK ke `motorcycles(id)`                |
| lease\_amount    | DECIMAL   | Total pinjaman                         |
| monthly\_payment | DECIMAL   | Cicilan bulanan                        |
| lease\_duration  | INTEGER   | Lama kontrak (bulan)                   |
| start\_date      | DATE      | Tanggal mulai                          |
| end\_date        | DATE      | Tanggal jatuh tempo                    |
| status           | TEXT      | 'pending', 'active', 'completed', etc. |
| created\_by      | INTEGER   | FK pembuat (admin/staff)               |
| created\_at      | TIMESTAMP |                                        |
| updated\_at      | TIMESTAMP |                                        |

---

### `payments`

| Kolom           | Tipe      | Keterangan           |
| --------------- | --------- | -------------------- |
| id              | INTEGER   | PRIMARY KEY          |
| lease\_id       | INTEGER   | FK ke `leases(id)`   |
| payment\_date   | DATE      | Tanggal pembayaran   |
| amount          | DECIMAL   | Nominal yang dibayar |
| payment\_method | TEXT      | Metode pembayaran    |
| collector\_id   | INTEGER   | FK ke `users(id)`    |
| notes           | TEXT      | Catatan pembayaran   |
| created\_at     | TIMESTAMP |                      |

---

### `lease_audit_log`

| Kolom       | Tipe      | Keterangan                    |
| ----------- | --------- | ----------------------------- |
| id          | INTEGER   | PRIMARY KEY                   |
| lease\_id   | INTEGER   | FK ke `leases(id)`            |
| action      | TEXT      | Jenis aksi (e.g., "approved") |
| user\_id    | INTEGER   | FK ke `users(id)`             |
| notes       | TEXT      | Catatan aksi                  |
| created\_at | TIMESTAMP |                               |

---

### `financing_contract`

| Kolom          | Tipe      | Keterangan           |
| -------------- | --------- | -------------------- |
| contract\_id   | TEXT      | PRIMARY KEY          |
| debtor\_nik    | TEXT      | NIK debitur          |
| loan\_amount   | DECIMAL   | Jumlah pinjaman      |
| interest\_rate | DECIMAL   | Suku bunga (%)       |
| term           | INTEGER   | Lama kontrak (bulan) |
| start\_date    | DATE      | Tanggal mulai        |
| due\_date      | DATE      | Jatuh tempo          |
| status         | TEXT      | Status kontrak       |
| created\_at    | TIMESTAMP |                      |
| updated\_at    | TIMESTAMP |                      |

---

## 👥 Akun Pengguna Default

| Role       | Email                    | Password (default) |
| ---------- | ------------------------ | ------------------ |
| Admin      | `admin@leaslink.com`     | `admin`            |
| Management | `manager@leaslink.com`   | `management`       |
| Collector  | `collector@leaslink.com` | `collector`        |
| Customer   | `customer@leaslink.com`  | `customer`         |

---

## 💻 Spesifikasi Sistem

### Minimum

* **OS**: Windows 10 / macOS 10.14+ / Ubuntu 18.04+
* **Java**: OpenJDK 17+
* **RAM**: 4 GB
* **Storage**: 500 MB
* **Resolusi Layar**: 1024x768

### Rekomendasi

* **OS**: Windows 11 / macOS 12+
* **Java**: OpenJDK 21
* **RAM**: 8 GB+
* **Storage**: 1 GB
* **Resolusi Layar**: 1920x1080

---


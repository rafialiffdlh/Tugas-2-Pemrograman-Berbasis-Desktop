import java.util.Scanner;
public class Main {

    static final String MAKANAN = "Makanan";
    static final String MINUMAN = "Minuman";
    static final int BIAYA_PELAYANAN = 20000;
    static final int PERSEN_PAJAK = 10;
    static final int PERSEN_DISKON = 10;
    static final int BATAS_DISKON = 100000;      // diskon jika total > Rp 100.000
    static final int BATAS_BELI1GRATIS1 = 50000; // promo jika total > Rp 50.000

    static Scanner pemindai = new Scanner(System.in);
    static Menu[] daftarMenu = new Menu[50];
    static int jumlahMenu = 0;

    // PROGRAM UTAMA 
    public static void main(String[] args) {
        isiMenuAwal();
        boolean berjalan = true;
        while (berjalan) {
            System.out.println("\n===== MENU UTAMA =====");
            System.out.println("1. Pelanggan (Pesan Menu)");
            System.out.println("2. Pemilik Restoran (Kelola Menu)");
            System.out.println("0. Keluar");
            int pilihan = bacaAngka("Pilih: ");
            switch (pilihan) {
                case 1: menuPelanggan(); break;
                case 2: menuPemilik(); break;
                case 0:
                    System.out.println("Terima kasih. Sampai jumpa!");
                    berjalan = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid, silakan ulangi.");
            }
        }
    }

    // Data awal: minimal 4 menu untuk setiap kategori
    static void isiMenuAwal() {
        tambahKeArray(new Menu("Nasi Goreng", 25000, MAKANAN));
        tambahKeArray(new Menu("Mie Ayam", 20000, MAKANAN));
        tambahKeArray(new Menu("Sate Ayam", 30000, MAKANAN));
        tambahKeArray(new Menu("Soto Betawi", 28000, MAKANAN));
        tambahKeArray(new Menu("Es Teh Manis", 6000, MINUMAN));
        tambahKeArray(new Menu("Jus Alpukat", 15000, MINUMAN));
        tambahKeArray(new Menu("Kopi Susu", 18000, MINUMAN));
        tambahKeArray(new Menu("Air Mineral", 5000, MINUMAN));
    }

    static void tambahKeArray(Menu menuBaru) {
        if (jumlahMenu == daftarMenu.length) { // perbesar array jika penuh
            Menu[] lebihBesar = new Menu[daftarMenu.length * 2];
            for (int i = 0; i < daftarMenu.length; i++) lebihBesar[i] = daftarMenu[i];
            daftarMenu = lebihBesar;
        }
        daftarMenu[jumlahMenu] = menuBaru;
        jumlahMenu++;
    }

    // TAMPILAN MENU 
    static void tampilkanDaftarMenu() {
        String[] kategori = {MAKANAN, MINUMAN};
        System.out.println("\n========== DAFTAR MENU ==========");
        for (String k : kategori) {
            System.out.println("--- " + k.toUpperCase() + " ---");
            boolean adaItem = false;
            for (int i = 0; i < jumlahMenu; i++) {
                if (daftarMenu[i].getKategori().equals(k)) {
                    System.out.printf("%2d. %-18s %s%n", (i + 1),
                            daftarMenu[i].getNama(), rupiah(daftarMenu[i].getHarga()));
                    adaItem = true;
                }
            }
            if (!adaItem) System.out.println("   (belum ada menu)");
        }
    }

    // MENU PELANGGAN 
    static void menuPelanggan() {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n===== MENU PELANGGAN =====");
            System.out.println("1. Lihat Daftar Menu");
            System.out.println("2. Buat Pesanan");
            System.out.println("0. Kembali");
            int pilihan = bacaAngka("Pilih: ");
            if (pilihan == 1) tampilkanDaftarMenu();
            else if (pilihan == 2) prosesPemesanan();
            else if (pilihan == 0) kembali = true;
            else System.out.println("Pilihan tidak valid, silakan ulangi.");
        }
    }

    static void prosesPemesanan() {
        Menu[] pesananMenu = new Menu[10];
        int[] pesananJumlah = new int[10];
        int jumlahPesanan = 0;

        tampilkanDaftarMenu();
        while (true) {
            System.out.print("\nMasukkan nomor menu (ketik 'selesai' untuk mengakhiri): ");
            String masukan = pemindai.nextLine().trim();

            if (masukan.equalsIgnoreCase("selesai")) {
                if (jumlahPesanan == 0) {
                    System.out.println("Anda belum memesan apa pun.");
                    continue;
                }
                break;
            }

            int nomor = ubahKeAngka(masukan);
            if (nomor < 1 || nomor > jumlahMenu) { // di luar pilihan -> minta input lagi
                System.out.println("Input tidak valid, silakan pilih nomor menu yang tersedia.");
                continue;
            }

            int jumlah = 0;
            while (jumlah < 1) {
                jumlah = bacaAngka("Jumlah " + daftarMenu[nomor - 1].getNama() + ": ");
                if (jumlah < 1) System.out.println("Jumlah harus berupa angka lebih dari 0.");
            }

            Menu dipilih = daftarMenu[nomor - 1];
            boolean sudahAda = false;
            for (int i = 0; i < jumlahPesanan; i++) { // gabungkan jika menu sama
                if (pesananMenu[i] == dipilih) {
                    pesananJumlah[i] += jumlah;
                    sudahAda = true;
                    break;
                }
            }
            if (!sudahAda) {
                if (jumlahPesanan == pesananMenu.length) { 
                    Menu[] m = new Menu[jumlahPesanan * 2];
                    int[] j = new int[jumlahPesanan * 2];
                    for (int i = 0; i < jumlahPesanan; i++) { m[i] = pesananMenu[i]; j[i] = pesananJumlah[i]; }
                    pesananMenu = m;
                    pesananJumlah = j;
                }
                pesananMenu[jumlahPesanan] = dipilih;
                pesananJumlah[jumlahPesanan] = jumlah;
                jumlahPesanan++;
            }
            System.out.println("Ditambahkan: " + dipilih.getNama() + " x" + jumlah);
        }
        cetakStruk(pesananMenu, pesananJumlah, jumlahPesanan);
    }

    // HITUNG & CETAK STRUK
    static long hitungSubtotal(Menu[] pesananMenu, int[] pesananJumlah, int jumlahPesanan) {
        long total = 0;
        for (int i = 0; i < jumlahPesanan; i++) {
            total += (long) pesananMenu[i].getHarga() * pesananJumlah[i];
        }
        return total;
    }

    // Mengembalikan indeks minuman yang dipilih untuk promo beli 1 gratis 1 (-1 jika tidak ada)
    static int pilihMinumanPromo(Menu[] pesananMenu, int jumlahPesanan) {
        int[] indeksMinuman = new int[jumlahPesanan];
        int banyak = 0;
        for (int i = 0; i < jumlahPesanan; i++) {
            if (pesananMenu[i].getKategori().equals(MINUMAN)) {
                indeksMinuman[banyak] = i;
                banyak++;
            }
        }
        if (banyak == 0) return -1;
        if (banyak == 1) return indeksMinuman[0];

        System.out.println("\nSelamat! Anda mendapat promo BELI 1 GRATIS 1 untuk salah satu minuman.");
        for (int i = 0; i < banyak; i++) {
            System.out.println((i + 1) + ". " + pesananMenu[indeksMinuman[i]].getNama());
        }
        while (true) {
            int pilihan = bacaAngka("Pilih minuman untuk promo: ");
            if (pilihan >= 1 && pilihan <= banyak) return indeksMinuman[pilihan - 1];
            System.out.println("Pilihan tidak valid, silakan ulangi.");
        }
    }

    static void cetakStruk(Menu[] pesananMenu, int[] pesananJumlah, int jumlahPesanan) {
        long subtotal = hitungSubtotal(pesananMenu, pesananJumlah, jumlahPesanan);

        // Struktur keputusan: diskon 10% jika subtotal > Rp 100.000
        long diskon = 0;
        if (subtotal > BATAS_DISKON) {
            diskon = subtotal * PERSEN_DISKON / 100;
        }

        // Struktur keputusan: beli 1 gratis 1 jika subtotal > Rp 50.000
        int indeksPromo = -1;
        if (subtotal > BATAS_BELI1GRATIS1) {
            indeksPromo = pilihMinumanPromo(pesananMenu, jumlahPesanan);
        }

        long setelahDiskon = subtotal - diskon;
        long pajak = setelahDiskon * PERSEN_PAJAK / 100;
        long totalBayar = setelahDiskon + pajak + BIAYA_PELAYANAN;

        System.out.println("\n===== STRUK PESANAN =========");
        System.out.printf("%-18s %5s %12s %14s%n", "Item", "Jml", "Harga", "Total");
        System.out.println("-------------------------------------------------------");
        for (int i = 0; i < jumlahPesanan; i++) {
            long totalItem = (long) pesananMenu[i].getHarga() * pesananJumlah[i];
            System.out.printf("%-18s %5d %12s %14s%n", pesananMenu[i].getNama(), pesananJumlah[i],
                    rupiah(pesananMenu[i].getHarga()), rupiah(totalItem));
            if (i == indeksPromo) {
                System.out.printf("%-18s %5d %12s %14s%n", "  (Gratis) " + "B1G1", pesananJumlah[i],
                        rupiah(0), rupiah(0));
            }
        }
        System.out.println("------------------------");
        System.out.printf("%-38s %14s%n", "Total Biaya Pesanan", rupiah(subtotal));
        if (diskon > 0) {
            System.out.printf("%-38s %14s%n", "Diskon " + PERSEN_DISKON + "% (> Rp 100.000)", "-" + rupiah(diskon));
            System.out.printf("%-38s %14s%n", "Harga Setelah Diskon", rupiah(setelahDiskon));
        }
        if (indeksPromo >= 0) {
            System.out.printf("%-38s %14s%n", "Promo: Beli 1 Gratis 1",
                    pesananMenu[indeksPromo].getNama());
        }
        System.out.printf("%-38s %14s%n", "Pajak " + PERSEN_PAJAK + "%", rupiah(pajak));
        System.out.printf("%-38s %14s%n", "Biaya Pelayanan", rupiah(BIAYA_PELAYANAN));
        System.out.println("========================");
        System.out.printf("%-38s %14s%n", "TOTAL BAYAR", rupiah(totalBayar));
        System.out.println("========================");
        System.out.println("Terima kasih atas kunjungan Anda!");
    }

    // MENU PEMILIK
    static void menuPemilik() {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n===== PENGELOLAAN MENU (PEMILIK) =====");
            System.out.println("1. Tambah Menu Baru");
            System.out.println("2. Ubah Harga Menu");
            System.out.println("3. Hapus Menu");
            System.out.println("0. Kembali");
            int pilihan = bacaAngka("Pilih: ");
            switch (pilihan) {
                case 1: tambahMenuBaru(); break;
                case 2: ubahHargaMenu(); break;
                case 3: hapusMenu(); break;
                case 0: kembali = true; break;
                default: System.out.println("Pilihan tidak valid, silakan ulangi.");
            }
        }
    }

    // Dapat menambahkan beberapa menu sekaligus (perulangan)
    static void tambahMenuBaru() {
        String lagi = "ya";
        while (lagi.equalsIgnoreCase("ya")) {
            System.out.print("Nama menu: ");
            String nama = pemindai.nextLine().trim();
            if (nama.isEmpty()) {
                System.out.println("Nama tidak boleh kosong.");
                continue;
            }
            int harga = 0;
            while (harga < 1) {
                harga = bacaAngka("Harga (Rp): ");
                if (harga < 1) System.out.println("Harga harus berupa angka lebih dari 0.");
            }
            int kat = 0;
            while (kat != 1 && kat != 2) {
                kat = bacaAngka("Kategori (1. Makanan, 2. Minuman): ");
                if (kat != 1 && kat != 2) System.out.println("Pilihan tidak valid.");
            }
            tambahKeArray(new Menu(nama, harga, kat == 1 ? MAKANAN : MINUMAN));
            System.out.println("Menu '" + nama + "' berhasil ditambahkan.");

            lagi = "";
            while (!lagi.equalsIgnoreCase("ya") && !lagi.equalsIgnoreCase("tidak")) {
                System.out.print("Tambah menu lagi? (Ya/Tidak): ");
                lagi = pemindai.nextLine().trim();
            }
        }
    }


    static void ubahHargaMenu() {
        tampilkanDaftarMenu();
        int nomor = pilihNomorMenu("Masukkan nomor menu yang diubah (0 = batal): ");
        if (nomor == 0) return;
        Menu m = daftarMenu[nomor - 1];

        int hargaBaru = 0;
        while (hargaBaru < 1) {
            hargaBaru = bacaAngka("Harga baru untuk " + m.getNama() + " (Rp): ");
            if (hargaBaru < 1) System.out.println("Harga harus berupa angka lebih dari 0.");
        }
        System.out.println("Ubah harga " + m.getNama() + " dari " + rupiah(m.getHarga())
                + " menjadi " + rupiah(hargaBaru) + "?");
        if (konfirmasiYa()) {
            m.setHarga(hargaBaru);
            System.out.println("Harga berhasil diubah.");
        } else {
            System.out.println("Perubahan dibatalkan.");
        }
    }

    static void hapusMenu() {
        tampilkanDaftarMenu();
        int nomor = pilihNomorMenu("Masukkan nomor menu yang dihapus (0 = batal): ");
        if (nomor == 0) return;
        System.out.println("Hapus menu '" + daftarMenu[nomor - 1].getNama() + "'?");
        if (konfirmasiYa()) {
            for (int i = nomor - 1; i < jumlahMenu - 1; i++) { 
                daftarMenu[i] = daftarMenu[i + 1];
            }
            jumlahMenu--;
            daftarMenu[jumlahMenu] = null;
            System.out.println("Menu berhasil dihapus.");
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }

    //  meminta input sampai nomor valid (0 = batal)
    static int pilihNomorMenu(String teks) {
        while (true) {
            int nomor = bacaAngka(teks);
            if (nomor == 0 || (nomor >= 1 && nomor <= jumlahMenu)) return nomor;
            System.out.println("Nomor tidak valid, silakan ulangi.");
        }
    }

    // Konfirmasi: 'Ya' = lanjut, 'Tidak' = batal; selain itu minta input lagi
    static boolean konfirmasiYa() {
        while (true) {
            System.out.print("Yakin? (Ya/Tidak): ");
            String jawab = pemindai.nextLine().trim();
            if (jawab.equalsIgnoreCase("ya")) return true;
            if (jawab.equalsIgnoreCase("tidak")) return false;
            System.out.println("Input tidak valid, ketik 'Ya' atau 'Tidak'.");
        }
    }

    static int ubahKeAngka(String teks) {
        try {
            return Integer.parseInt(teks);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static int bacaAngka(String teks) {
        System.out.print(teks);
        return ubahKeAngka(pemindai.nextLine().trim());
    }

    static String rupiah(long nilai) {
        return "Rp " + String.format("%,d", nilai).replace(',', '.');
    }
}
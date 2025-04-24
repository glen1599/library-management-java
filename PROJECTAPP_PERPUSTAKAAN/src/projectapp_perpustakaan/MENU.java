package projectapp_perpustakaan;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 * Jendela utama aplikasi: sidebar navigasi, header, dan area konten.
 *
 * Seluruh tampilan dibangun manual (tanpa GUI Builder) agar mudah diatur,
 * sedangkan form CRUD tetap memakai layout NetBeans dan dipoles lewat Tema.
 */
public class MENU extends JFrame {

    private static final DateTimeFormatter FORMAT_JAM = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMAT_TANGGAL
            = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("id", "ID"));

    private final Map<String, JComponent> halaman = new LinkedHashMap<>();
    private final Map<String, NavItem> menu = new LinkedHashMap<>();

    private final JPanel isi = new JPanel(new BorderLayout());
    private final JLabel judul = new JLabel();
    private final JLabel subjudul = new JLabel();
    private final JLabel jam = new JLabel();
    private final JLabel tanggal = new JLabel();

    public MENU() {
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 660));
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setContentPane(bangun());
        buka("dashboard");
        mulaiJam();
    }

    public static void main(String[] args) {
        Tema.terapkan();
        java.awt.EventQueue.invokeLater(() -> new MENU().setVisible(true));
    }

    private JComponent bangun() {
        JPanel akar = new JPanel(new BorderLayout());
        akar.setBackground(Tema.LATAR);
        akar.setBorder(BorderFactory.createLineBorder(new Color(0x1E, 0x29, 0x3B)));
        akar.add(sidebar(), BorderLayout.WEST);
        akar.add(utama(), BorderLayout.CENTER);
        return akar;
    }

    // ------------------------------------------------------------------
    // Sidebar
    // ------------------------------------------------------------------
    private JComponent sidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Tema.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(236, 0));
        sidebar.setBorder(new EmptyBorder(22, 16, 18, 16));

        JPanel atas = new JPanel();
        atas.setOpaque(false);
        atas.setLayout(new BoxLayout(atas, BoxLayout.Y_AXIS));
        atas.add(merek());
        atas.add(Box.createVerticalStrut(28));

        JLabel bagian = new JLabel("MENU UTAMA");
        bagian.setForeground(new Color(0x55, 0x63, 0x78));
        bagian.setFont(Tema.FONT.deriveFont(Font.BOLD, 10f));
        bagian.setBorder(new EmptyBorder(0, 12, 8, 0));
        bagian.setAlignmentX(Component.LEFT_ALIGNMENT);
        atas.add(bagian);

        atas.add(menuItem("dashboard", "Dashboard", Ikon::dashboard));
        atas.add(menuItem("anggota", "Anggota", Ikon::anggota));
        atas.add(menuItem("buku", "Buku", Ikon::buku));
        atas.add(menuItem("peminjaman", "Peminjaman", Ikon::pinjam));
        atas.add(menuItem("pengembalian", "Pengembalian", Ikon::kembali));

        sidebar.add(atas, BorderLayout.NORTH);
        sidebar.add(kakiSidebar(), BorderLayout.SOUTH);
        return sidebar;
    }

    private JComponent merek() {
        JPanel merek = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        merek.setOpaque(false);
        merek.setAlignmentX(Component.LEFT_ALIGNMENT);
        merek.add(new Lencana());

        JPanel teks = new JPanel();
        teks.setOpaque(false);
        teks.setLayout(new BoxLayout(teks, BoxLayout.Y_AXIS));
        JLabel nama = new JLabel("Pustaka");
        nama.setForeground(Color.WHITE);
        nama.setFont(Tema.FONT.deriveFont(Font.BOLD, 17f));
        JLabel ket = new JLabel("Sistem Perpustakaan");
        ket.setForeground(Tema.SIDEBAR_TEKS);
        ket.setFont(Tema.FONT.deriveFont(10.5f));
        teks.add(nama);
        teks.add(Box.createVerticalStrut(1));
        teks.add(ket);
        merek.add(teks);
        return merek;
    }

    private NavItem menuItem(String kunci, String teks, Function<Color, Icon> ikon) {
        NavItem item = new NavItem(teks, ikon);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        item.addActionListener(e -> buka(kunci));
        menu.put(kunci, item);
        return item;
    }

    private JComponent kakiSidebar() {
        JPanel kaki = new JPanel();
        kaki.setOpaque(false);
        kaki.setLayout(new BoxLayout(kaki, BoxLayout.Y_AXIS));

        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        status.setOpaque(false);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titik = new JLabel("\u25CF");
        titik.setForeground(Tema.AKSEN);
        titik.setFont(Tema.FONT.deriveFont(8f));
        JLabel db = new JLabel("MySQL · db_perpustakaan");
        db.setForeground(new Color(0x64, 0x74, 0x8B));
        db.setFont(Tema.FONT.deriveFont(10.5f));
        status.add(titik);
        status.add(db);

        JLabel versi = new JLabel("Pustaka v2.0");
        versi.setForeground(new Color(0x44, 0x52, 0x66));
        versi.setFont(Tema.FONT.deriveFont(10f));
        versi.setBorder(new EmptyBorder(6, 16, 0, 0));
        versi.setAlignmentX(Component.LEFT_ALIGNMENT);

        kaki.add(status);
        kaki.add(versi);
        return kaki;
    }

    // ------------------------------------------------------------------
    // Header dan area konten
    // ------------------------------------------------------------------
    private JComponent utama() {
        JPanel utama = new JPanel(new BorderLayout());
        utama.setBackground(Tema.LATAR);
        utama.add(header(), BorderLayout.NORTH);

        JPanel badan = new JPanel(new BorderLayout());
        badan.setBackground(Tema.LATAR);
        badan.setBorder(new EmptyBorder(20, 24, 24, 24));

        Kartu kartu = new Kartu();
        kartu.setLayout(new BorderLayout());
        kartu.setBorder(new EmptyBorder(20, 22, 20, 22));
        isi.setOpaque(false);
        kartu.add(isi, BorderLayout.CENTER);

        badan.add(kartu, BorderLayout.CENTER);
        utama.add(badan, BorderLayout.CENTER);
        return utama;
    }

    private JComponent header() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.KARTU);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.GARIS),
                new EmptyBorder(15, 24, 15, 16)));

        JPanel kiri = new JPanel();
        kiri.setOpaque(false);
        kiri.setLayout(new BoxLayout(kiri, BoxLayout.Y_AXIS));
        judul.setFont(Tema.FONT.deriveFont(Font.BOLD, 21f));
        judul.setForeground(Tema.TEKS);
        subjudul.setFont(Tema.FONT.deriveFont(11.5f));
        subjudul.setForeground(Tema.TEKS_LEMBUT);
        kiri.add(judul);
        kiri.add(Box.createVerticalStrut(2));
        kiri.add(subjudul);

        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        kanan.setOpaque(false);

        JPanel waktu = new JPanel();
        waktu.setOpaque(false);
        waktu.setLayout(new BoxLayout(waktu, BoxLayout.Y_AXIS));
        jam.setFont(Tema.FONT.deriveFont(Font.BOLD, 15f));
        jam.setForeground(Tema.TEKS);
        jam.setAlignmentX(Component.RIGHT_ALIGNMENT);
        tanggal.setFont(Tema.FONT.deriveFont(10.5f));
        tanggal.setForeground(Tema.TEKS_LEMBUT);
        tanggal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        waktu.add(jam);
        waktu.add(tanggal);

        kanan.add(waktu);
        kanan.add(Box.createHorizontalStrut(4));
        kanan.add(pemisah());
        kanan.add(tombolJendela(Ikon.kecilkan(new Color(0x64, 0x74, 0x8B)), false));
        kanan.add(tombolJendela(Ikon.tutup(new Color(0x64, 0x74, 0x8B)), true));

        header.add(kiri, BorderLayout.WEST);
        header.add(kanan, BorderLayout.EAST);
        pasangGeser(header);
        return header;
    }

    private JComponent pemisah() {
        JSeparator garis = new JSeparator(SwingConstants.VERTICAL);
        garis.setPreferredSize(new Dimension(1, 30));
        garis.setForeground(Tema.GARIS);
        return garis;
    }

    private JButton tombolJendela(Icon ikon, boolean tutup) {
        JButton tombol = new JButton(ikon);
        tombol.setPreferredSize(new Dimension(32, 32));
        tombol.setBorder(new EmptyBorder(0, 0, 0, 0));
        tombol.setContentAreaFilled(false);
        tombol.setFocusPainted(false);
        tombol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tombol.putClientProperty("JButton.buttonType", "toolBar");
        if (tutup) {
            tombol.addActionListener(e -> System.exit(0));
        } else {
            tombol.addActionListener(e -> setState(JFrame.ICONIFIED));
        }
        return tombol;
    }

    private void pasangGeser(JComponent pegangan) {
        MouseAdapter geser = new MouseAdapter() {
            private Point awal;

            @Override
            public void mousePressed(MouseEvent e) {
                awal = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (awal != null) {
                    Point posisi = e.getLocationOnScreen();
                    setLocation(posisi.x - awal.x, posisi.y - awal.y);
                }
            }
        };
        pegangan.addMouseListener(geser);
        pegangan.addMouseMotionListener(geser);
        for (Component anak : pegangan.getComponents()) {
            if (anak instanceof JComponent && !(anak instanceof JButton)) {
                anak.addMouseListener(geser);
                anak.addMouseMotionListener(geser);
            }
        }
    }

    // ------------------------------------------------------------------
    // Navigasi halaman
    // ------------------------------------------------------------------
    private void buka(String kunci) {
        JComponent komponen = halaman.get(kunci);
        if (komponen == null) {
            komponen = buatHalaman(kunci);
            halaman.put(kunci, komponen);
        }
        isi.removeAll();
        isi.add(komponen, BorderLayout.CENTER);
        isi.revalidate();
        isi.repaint();

        for (Map.Entry<String, NavItem> item : menu.entrySet()) {
            item.getValue().setAktif(item.getKey().equals(kunci));
        }
        aturJudul(kunci);
    }

    private void aturJudul(String kunci) {
        switch (kunci) {
            case "anggota":
                judul.setText("Anggota");
                subjudul.setText("Kelola data anggota perpustakaan");
                break;
            case "buku":
                judul.setText("Buku");
                subjudul.setText("Kelola koleksi dan stok buku");
                break;
            case "peminjaman":
                judul.setText("Peminjaman");
                subjudul.setText("Catat transaksi peminjaman dan cetak nota");
                break;
            case "pengembalian":
                judul.setText("Pengembalian");
                subjudul.setText("Proses pengembalian, denda, dan nota");
                break;
            default:
                judul.setText("Dashboard");
                subjudul.setText("Ringkasan perpustakaan hari ini");
                break;
        }
    }

    private JComponent buatHalaman(String kunci) {
        switch (kunci) {
            case "anggota":
                return bungkusForm(new ANGGOTA(),
                        "Klik TAMBAH untuk anggota baru, atau ketik pada kolom CARI untuk mencari data.");
            case "buku":
                return bungkusForm(new BUKU(),
                        "Stok berkurang otomatis saat buku dipinjam dan kembali saat buku dikembalikan.");
            case "peminjaman":
                return bungkusForm(new PEMINJAMAN(),
                        "Isi jumlah pinjam untuk menghitung sisa stok. Nota dapat dicetak setelah transaksi disimpan.");
            case "pengembalian":
                return bungkusForm(new KEMBALIKAN_BUKU(),
                        "Pilih transaksi dari daftar pinjaman; keterlambatan dan denda dihitung otomatis.");
            default:
                return dashboard();
        }
    }

    /** Menempelkan form NetBeans tanpa title bar agar terlihat seperti halaman. */
    private JComponent bungkusForm(JInternalFrame form, String petunjuk) {
        form.setClosable(false);
        form.setMaximizable(false);
        form.setIconifiable(false);
        form.setBorder(new EmptyBorder(0, 0, 0, 0));
        ((BasicInternalFrameUI) form.getUI()).setNorthPane(null);
        form.setVisible(true);
        Tema.poles(form);

        JLabel bantuan = new JLabel(petunjuk);
        bantuan.setFont(Tema.FONT.deriveFont(11f));
        bantuan.setForeground(Tema.TEKS_LEMBUT);
        bantuan.setBorder(new EmptyBorder(12, 2, 0, 0));

        JPanel wadah = new JPanel(new BorderLayout());
        wadah.setOpaque(false);
        wadah.add(form, BorderLayout.CENTER);
        wadah.add(bantuan, BorderLayout.SOUTH);
        return wadah;
    }

    // ------------------------------------------------------------------
    // Dashboard
    // ------------------------------------------------------------------
    private JComponent dashboard() {
        JPanel dasbor = new JPanel(new BorderLayout(0, 18));
        dasbor.setOpaque(false);
        dasbor.add(barisStatistik(), BorderLayout.NORTH);

        JPanel bawah = new JPanel(new BorderLayout(18, 0));
        bawah.setOpaque(false);
        bawah.add(kartuAktivitas(), BorderLayout.CENTER);
        bawah.add(kartuPanduan(), BorderLayout.EAST);
        dasbor.add(bawah, BorderLayout.CENTER);
        dasbor.add(barisAksi(), BorderLayout.SOUTH);
        return dasbor;
    }

    private JComponent barisStatistik() {
        JPanel baris = new JPanel(new GridLayout(1, 4, 14, 0));
        baris.setOpaque(false);
        baris.add(kartuAngka(Ikon.anggota(Tema.AKSEN_GELAP), new Color(0xD1, 0xFA, 0xE5),
                hitung("select count(*) from tb_anggota"), "Anggota"));
        baris.add(kartuAngka(Ikon.buku(new Color(0x25, 0x63, 0xEB)), new Color(0xDB, 0xEA, 0xFE),
                hitung("select count(*) from tb_buku"), "Judul buku"));
        baris.add(kartuAngka(Ikon.pinjam(new Color(0x7C, 0x3A, 0xED)), new Color(0xED, 0xE9, 0xFE),
                hitung("select count(*) from tb_peminjaman"), "Peminjaman"));
        baris.add(kartuAngka(Ikon.kembali(new Color(0xD9, 0x77, 0x06)), new Color(0xFE, 0xF3, 0xC7),
                hitung("select count(*) from tb_kembalikanbuku"), "Pengembalian"));
        return baris;
    }

    private JComponent kartuAngka(Icon ikon, Color latarIkon, int nilai, String label) {
        Kartu kartu = new Kartu();
        kartu.setLayout(new BorderLayout(12, 0));
        kartu.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel chip = new JPanel(new BorderLayout());
        chip.setBackground(latarIkon);
        chip.setPreferredSize(new Dimension(38, 38));
        chip.setBorder(new EmptyBorder(9, 9, 9, 9));
        chip.add(new JLabel(ikon));

        JPanel teks = new JPanel();
        teks.setOpaque(false);
        teks.setLayout(new BoxLayout(teks, BoxLayout.Y_AXIS));
        JLabel angka = new JLabel(nilai < 0 ? "—" : String.valueOf(nilai));
        angka.setFont(Tema.FONT.deriveFont(Font.BOLD, 24f));
        angka.setForeground(Tema.TEKS);
        JLabel ket = new JLabel(label);
        ket.setFont(Tema.FONT.deriveFont(11f));
        ket.setForeground(Tema.TEKS_LEMBUT);
        teks.add(angka);
        teks.add(ket);

        kartu.add(chip, BorderLayout.WEST);
        kartu.add(teks, BorderLayout.CENTER);
        return kartu;
    }

    /** Tiga aksi yang paling sering dipakai, langsung menuju halaman terkait. */
    private JComponent barisAksi() {
        JPanel baris = new JPanel(new GridLayout(1, 3, 14, 0));
        baris.setOpaque(false);
        baris.setPreferredSize(new Dimension(0, 74));
        baris.add(tombolAksi("Anggota baru", "Daftarkan anggota perpustakaan",
                new Color(0x05, 0x96, 0x69), Ikon.anggota(new Color(0x05, 0x96, 0x69)), "anggota"));
        baris.add(tombolAksi("Tambah buku", "Masukkan koleksi dan stok baru",
                new Color(0x25, 0x63, 0xEB), Ikon.buku(new Color(0x25, 0x63, 0xEB)), "buku"));
        baris.add(tombolAksi("Catat peminjaman", "Buat transaksi dan cetak nota",
                new Color(0x7C, 0x3A, 0xED), Ikon.pinjam(new Color(0x7C, 0x3A, 0xED)), "peminjaman"));
        return baris;
    }

    private JComponent tombolAksi(String judul, String keterangan, Color warna, Icon ikon, String halaman) {
        JButton tombol = new JButton("<html><b>" + judul + "</b><br>"
                + "<span style='color:#64748B'>" + keterangan + "</span></html>", ikon);
        tombol.setHorizontalAlignment(SwingConstants.LEFT);
        tombol.setIconTextGap(12);
        tombol.setFont(Tema.FONT.deriveFont(Font.BOLD, 12f));
        tombol.setBackground(Tema.KARTU);
        tombol.setForeground(Tema.TEKS);
        tombol.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.GARIS),
                new EmptyBorder(10, 16, 10, 16)));
        tombol.putClientProperty("JButton.buttonType", "roundRect");
        tombol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tombol.setFocusPainted(false);
        tombol.addActionListener(e -> buka(halaman));
        return tombol;
    }

    private JComponent kartuAktivitas() {
        Kartu kartu = new Kartu();
        kartu.setLayout(new BorderLayout());
        kartu.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel kepala = new JPanel(new BorderLayout());
        kepala.setOpaque(false);
        JLabel judulKartu = new JLabel("Aktivitas terakhir");
        judulKartu.setFont(Tema.FONT.deriveFont(Font.BOLD, 14f));
        judulKartu.setForeground(Tema.TEKS);
        JLabel ket = new JLabel("Peminjaman terbaru");
        ket.setFont(Tema.FONT.deriveFont(11f));
        ket.setForeground(Tema.TEKS_LEMBUT);
        kepala.add(judulKartu, BorderLayout.NORTH);
        kepala.add(ket, BorderLayout.SOUTH);

        JPanel daftar = new JPanel();
        daftar.setOpaque(false);
        daftar.setLayout(new BoxLayout(daftar, BoxLayout.Y_AXIS));

        List<String[]> transaksi = ambil("select kodetransaksi, kodeanggota, judul, tanggalpinjam"
                + " from tb_peminjaman order by kodetransaksi desc limit 5");
        if (transaksi.isEmpty()) {
            JLabel kosong = new JLabel("Belum ada transaksi peminjaman.");
            kosong.setFont(Tema.FONT.deriveFont(12f));
            kosong.setForeground(Tema.TEKS_LEMBUT);
            kosong.setBorder(new EmptyBorder(24, 0, 0, 0));
            daftar.add(kosong);
        } else {
            for (String[] baris : transaksi) {
                daftar.add(barisAktivitas(baris));
            }
        }

        JPanel isiKartu = new JPanel();
        isiKartu.setOpaque(false);
        isiKartu.setLayout(new BoxLayout(isiKartu, BoxLayout.Y_AXIS));
        daftar.setAlignmentX(Component.LEFT_ALIGNMENT);
        isiKartu.add(daftar);
        isiKartu.add(Box.createVerticalStrut(14));
        isiKartu.add(kotakStok());
        isiKartu.add(Box.createVerticalGlue());

        kartu.add(kepala, BorderLayout.NORTH);
        kartu.add(isiKartu, BorderLayout.CENTER);
        return kartu;
    }

    /** Ringkasan stok terendah supaya petugas cepat tahu buku yang perlu ditambah. */
    private JComponent kotakStok() {
        JPanel kotak = new JPanel();
        kotak.setOpaque(false);
        kotak.setLayout(new BoxLayout(kotak, BoxLayout.Y_AXIS));
        kotak.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel judulStok = new JLabel("Stok menipis");
        judulStok.setFont(Tema.FONT.deriveFont(Font.BOLD, 13f));
        judulStok.setForeground(Tema.TEKS);
        judulStok.setAlignmentX(Component.LEFT_ALIGNMENT);
        kotak.add(judulStok);
        kotak.add(Box.createVerticalStrut(6));

        List<String[]> buku = ambil("select judul, stok from tb_buku order by cast(stok as unsigned) asc limit 3");
        if (buku.isEmpty()) {
            JLabel kosong = new JLabel("Belum ada data buku.");
            kosong.setFont(Tema.FONT.deriveFont(11.5f));
            kosong.setForeground(Tema.TEKS_LEMBUT);
            kotak.add(kosong);
            return kotak;
        }
        for (String[] baris : buku) {
            JPanel item = new JPanel(new BorderLayout());
            item.setOpaque(false);
            item.setBorder(new EmptyBorder(6, 0, 6, 0));
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            item.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel nama = new JLabel(baris[0]);
            nama.setFont(Tema.FONT.deriveFont(12f));
            nama.setForeground(Tema.TEKS);

            int stok = 0;
            try {
                stok = Integer.parseInt(baris[1]);
            } catch (NumberFormatException e) {
                // biarkan 0
            }
            JLabel jumlah = new JLabel(stok + " tersisa");
            jumlah.setFont(Tema.FONT.deriveFont(Font.BOLD, 11f));
            jumlah.setForeground(stok <= 5 ? Tema.MERAH : Tema.TEKS_LEMBUT);

            item.add(nama, BorderLayout.WEST);
            item.add(jumlah, BorderLayout.EAST);
            kotak.add(item);
        }
        return kotak;
    }

    private JComponent barisAktivitas(String[] baris) {
        JPanel item = new JPanel(new BorderLayout(12, 0));
        item.setOpaque(false);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF1, 0xF5, 0xF9)),
                new EmptyBorder(10, 0, 10, 0)));

        JLabel kode = new JLabel(baris[0]);
        kode.setFont(Tema.FONT.deriveFont(Font.BOLD, 12f));
        kode.setForeground(Tema.AKSEN_GELAP);
        kode.setPreferredSize(new Dimension(58, 18));

        JLabel keterangan = new JLabel(baris[1] + " meminjam " + baris[2]);
        keterangan.setFont(Tema.FONT.deriveFont(12f));
        keterangan.setForeground(Tema.TEKS);

        JLabel waktu = new JLabel(baris[3]);
        waktu.setFont(Tema.FONT.deriveFont(11f));
        waktu.setForeground(Tema.TEKS_LEMBUT);

        item.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 44));
        item.add(kode, BorderLayout.WEST);
        item.add(keterangan, BorderLayout.CENTER);
        item.add(waktu, BorderLayout.EAST);
        return item;
    }

    private JComponent kartuPanduan() {
        Kartu kartu = new Kartu();
        kartu.setLayout(new BorderLayout());
        kartu.setPreferredSize(new Dimension(286, 0));
        kartu.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel judulKartu = new JLabel("Panduan cepat");
        judulKartu.setFont(Tema.FONT.deriveFont(Font.BOLD, 14f));
        judulKartu.setForeground(Tema.TEKS);

        JPanel langkah = new JPanel();
        langkah.setOpaque(false);
        langkah.setLayout(new BoxLayout(langkah, BoxLayout.Y_AXIS));
        langkah.setBorder(new EmptyBorder(10, 0, 0, 0));
        langkah.add(langkah("1", "Daftarkan anggota baru pada menu Anggota."));
        langkah.add(langkah("2", "Tambahkan buku beserta stok pada menu Buku."));
        langkah.add(langkah("3", "Catat peminjaman, lalu cetak nota."));
        langkah.add(langkah("4", "Proses pengembalian dan denda otomatis."));

        kartu.add(judulKartu, BorderLayout.NORTH);
        kartu.add(langkah, BorderLayout.CENTER);
        return kartu;
    }

    private JComponent langkah(String nomor, String teks) {
        JPanel baris = new JPanel(new BorderLayout(10, 0));
        baris.setOpaque(false);
        baris.setBorder(new EmptyBorder(6, 0, 6, 0));

        JLabel angka = new JLabel(nomor, SwingConstants.CENTER);
        angka.setFont(Tema.FONT.deriveFont(Font.BOLD, 11f));
        angka.setForeground(Tema.AKSEN_GELAP);
        angka.setBackground(Tema.AKSEN_LEMBUT);
        angka.setOpaque(true);
        angka.setPreferredSize(new Dimension(22, 22));
        angka.setMinimumSize(new Dimension(22, 22));
        angka.setMaximumSize(new Dimension(22, 22));

        JLabel isi = new JLabel("<html><body style='width:168px'>" + teks + "</body></html>");
        isi.setFont(Tema.FONT.deriveFont(11.5f));
        isi.setForeground(Tema.TEKS_LEMBUT);
        baris.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 46));

        baris.add(angka, BorderLayout.WEST);
        baris.add(isi, BorderLayout.CENTER);
        return baris;
    }

    private int hitung(String sql) {
        List<String[]> hasil = ambil(sql);
        if (hasil.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(hasil.get(0)[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private List<String[]> ambil(String sql) {
        try {
            return Koneksi.query(sql);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    // ------------------------------------------------------------------
    // Jam dan komponen kustom
    // ------------------------------------------------------------------
    private void mulaiJam() {
        perbaruiWaktu();
        new Timer(1000, e -> perbaruiWaktu()).start();
    }

    private void perbaruiWaktu() {
        jam.setText(LocalTime.now().format(FORMAT_JAM));
        tanggal.setText(LocalDate.now().format(FORMAT_TANGGAL));
    }

    /** Panel putih dengan sudut membulat. */
    private static final class Kartu extends JPanel {

        Kartu() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x0F, 0x17, 0x2A, 10));
            g2.fillRoundRect(1, 2, getWidth() - 2, getHeight() - 3, 16, 16);
            g2.setColor(Tema.KARTU);
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 3, 16, 16);
            g2.setColor(Tema.GARIS);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 3, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Logo kotak hijau dengan ikon buku putih. */
    private static final class Lencana extends JComponent {

        Lencana() {
            setPreferredSize(new Dimension(38, 38));
            setMaximumSize(new Dimension(38, 38));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Tema.AKSEN);
            g2.fillRoundRect(0, 0, 38, 38, 12, 12);
            Icon ikon = Ikon.buku(Color.WHITE);
            ikon.paintIcon(this, g2, 9, 9);
            g2.dispose();
        }
    }

    /** Item navigasi sidebar dengan state hover dan aktif. */
    private static final class NavItem extends JButton {

        private final Function<Color, Icon> ikon;
        private boolean aktif;

        NavItem(String teks, Function<Color, Icon> ikon) {
            super(teks, ikon.apply(Tema.SIDEBAR_TEKS));
            this.ikon = ikon;
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(12);
            setForeground(Tema.SIDEBAR_TEKS);
            setFont(Tema.FONT.deriveFont(Font.BOLD, 12.5f));
            setBorder(new EmptyBorder(9, 12, 9, 12));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setAktif(boolean aktif) {
            this.aktif = aktif;
            setForeground(aktif ? Color.WHITE : Tema.SIDEBAR_TEKS);
            setIcon(ikon.apply(aktif ? Tema.AKSEN : Tema.SIDEBAR_TEKS));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (aktif) {
                g2.setColor(Tema.SIDEBAR_AKTIF);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Tema.AKSEN);
                g2.fillRoundRect(0, getHeight() / 2 - 9, 3, 18, 3, 3);
            } else if (getModel().isRollover()) {
                g2.setColor(Tema.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

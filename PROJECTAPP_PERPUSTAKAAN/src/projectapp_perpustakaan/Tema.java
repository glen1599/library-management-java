package projectapp_perpustakaan;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;

/**
 * Sistem desain aplikasi: palet warna, tipografi, dan gaya komponen.
 *
 * Semua pengaturan visual dikumpulkan di sini supaya tampilan konsisten dan
 * mudah diubah, tanpa menyentuh kode GUI Builder di setiap form.
 */
public final class Tema {

    public static final Color SIDEBAR = new Color(0x0B, 0x12, 0x20);
    public static final Color SIDEBAR_HOVER = new Color(0x16, 0x21, 0x3A);
    public static final Color SIDEBAR_AKTIF = new Color(0x18, 0x26, 0x42);
    public static final Color SIDEBAR_TEKS = new Color(0x94, 0xA3, 0xB8);

    public static final Color AKSEN = new Color(0x10, 0xB9, 0x81);
    public static final Color AKSEN_GELAP = new Color(0x05, 0x96, 0x69);
    public static final Color AKSEN_LEMBUT = new Color(0xD1, 0xFA, 0xE5);

    public static final Color LATAR = new Color(0xF1, 0xF5, 0xF9);
    public static final Color KARTU = Color.WHITE;
    public static final Color TEKS = new Color(0x0F, 0x17, 0x2A);
    public static final Color TEKS_LEMBUT = new Color(0x64, 0x74, 0x8B);
    public static final Color GARIS = new Color(0xE2, 0xE8, 0xF0);
    public static final Color MERAH = new Color(0xDC, 0x26, 0x26);
    public static final Color MERAH_LEMBUT = new Color(0xFE, 0xE2, 0xE2);

    public static final Font FONT = pilihFont();

    private Tema() {
    }

    /** Memasang tema dasar (FlatLaf) dan default komponen. Panggil sebelum membuat window. */
    public static void terapkan() {
        FlatLightLaf.setup();

        UIManager.put("defaultFont", FONT);
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("CheckBox.arc", 6);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Component.borderColor", GARIS);
        UIManager.put("Panel.background", KARTU);
        UIManager.put("Label.foreground", TEKS);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.trackArc", 999);

        UIManager.put("Table.background", KARTU);
        UIManager.put("Table.foreground", TEKS);
        UIManager.put("Table.rowHeight", 30);
        UIManager.put("Table.showHorizontalLines", false);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.alternateRowColor", new Color(0xF8, 0xFA, 0xFC));
        UIManager.put("Table.selectionBackground", AKSEN_LEMBUT);
        UIManager.put("Table.selectionForeground", TEKS);
        UIManager.put("TableHeader.background", new Color(0xF8, 0xFA, 0xFC));
        UIManager.put("TableHeader.foreground", TEKS_LEMBUT);
        UIManager.put("TableHeader.separatorColor", GARIS);
        UIManager.put("TableHeader.bottomSeparatorColor", GARIS);

        FlatLaf.updateUI();
    }

    /** Merapikan seluruh komponen di dalam form yang dibuat GUI Builder. */
    public static void poles(Container induk) {
        for (Component komponen : induk.getComponents()) {
            if (komponen instanceof JTable) {
                gayaTabel((JTable) komponen);
            } else if (komponen instanceof JLabel) {
                JLabel label = (JLabel) komponen;
                label.setFont(FONT.deriveFont(label.getFont().isBold() ? Font.BOLD : Font.PLAIN, 12f));
                label.setForeground(TEKS);
            } else if (komponen instanceof AbstractButton) {
                AbstractButton tombol = (AbstractButton) komponen;
                tombol.setIcon(null);
                tombol.setFont(FONT.deriveFont(Font.BOLD, 12f));
                tombol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                tombol.setFocusPainted(false);
            } else if (komponen instanceof JTextField) {
                komponen.setFont(FONT.deriveFont(13f));
            } else if (komponen instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) komponen;
                scroll.setBorder(BorderFactory.createLineBorder(GARIS));
                scroll.getViewport().setBackground(KARTU);
            } else if (komponen instanceof JPanel) {
                komponen.setBackground(KARTU);
            }
            if (komponen instanceof Container) {
                poles((Container) komponen);
            }
        }
    }

    public static void gayaTabel(JTable tabel) {
        tabel.setRowHeight(30);
        tabel.setShowGrid(false);
        tabel.setIntercellSpacing(new Dimension(0, 0));
        tabel.setFillsViewportHeight(true);
        tabel.setFont(FONT.deriveFont(12.5f));
        tabel.setSelectionBackground(AKSEN_LEMBUT);
        tabel.setSelectionForeground(TEKS);

        JTableHeader header = tabel.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setFont(FONT.deriveFont(Font.BOLD, 11.5f));
        header.setBackground(new Color(0xF8, 0xFA, 0xFC));
        header.setForeground(TEKS_LEMBUT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GARIS));
    }

    /** Tombol aksi utama: hijau, teks putih. */
    public static void utama(AbstractButton tombol) {
        tombol.setBackground(AKSEN);
        tombol.setForeground(Color.WHITE);
        tombol.setBorder(new EmptyBorder(7, 16, 7, 16));
        tombol.putClientProperty("JButton.buttonType", "roundRect");
    }

    /** Tombol aksi sekunder: putih dengan garis tipis. */
    public static void netral(AbstractButton tombol) {
        tombol.setBackground(KARTU);
        tombol.setForeground(TEKS);
        tombol.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GARIS),
                new EmptyBorder(7, 14, 7, 14)));
        tombol.putClientProperty("JButton.buttonType", "roundRect");
    }

    /** Tombol hapus: merah lembut. */
    public static void bahaya(AbstractButton tombol) {
        tombol.setBackground(MERAH_LEMBUT);
        tombol.setForeground(MERAH);
        tombol.setBorder(new EmptyBorder(7, 14, 7, 14));
        tombol.putClientProperty("JButton.buttonType", "roundRect");
    }

    private static Font pilihFont() {
        Set<String> tersedia = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String nama : new String[]{"Inter", "SF Pro Text", "Segoe UI", "Helvetica Neue", "Arial"}) {
            if (tersedia.contains(nama)) {
                return new Font(nama, Font.PLAIN, 13);
            }
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    }
}

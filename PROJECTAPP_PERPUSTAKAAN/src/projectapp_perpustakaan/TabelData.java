package projectapp_perpustakaan;

import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Mengisi JTable dari hasil query database.
 */
final class TabelData {

    private TabelData() {
    }

    static void isi(JTable tabel, String[] header, String sql, Object... params) {
        DefaultTableModel data = new DefaultTableModel(null, header) {
            @Override
            public boolean isCellEditable(int baris, int kolom) {
                return false;
            }
        };
        try {
            List<String[]> hasil = Koneksi.query(sql, params);
            for (String[] kolom : hasil) {
                data.addRow(kolom);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat data : " + e.getMessage());
        }
        tabel.setModel(data);
    }
}

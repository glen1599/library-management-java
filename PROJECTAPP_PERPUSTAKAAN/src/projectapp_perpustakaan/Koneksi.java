package projectapp_perpustakaan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Satu-satunya tempat konfigurasi dan akses database.
 *
 * Ubah nilai default di bawah, atau timpa tanpa menyentuh kode lewat JVM
 * property, misalnya di NetBeans: Run > Set Project Configuration > Customize
 * > VM Options:
 *
 *   -Ddb.url=jdbc:mysql://localhost:3306/db_perpustakaan -Ddb.user=root -Ddb.password=rahasia
 *
 * Driver MySQL (Connector/J) cukup diletakkan di folder lib/ dan didaftarkan
 * otomatis oleh JDBC 4, jadi tidak perlu Class.forName().
 */
public final class Koneksi {

    private static final String URL = System.getProperty(
            "db.url", "jdbc:mysql://localhost:3306/db_perpustakaan"
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta");
    private static final String USER = System.getProperty("db.user", "root");
    private static final String PASSWORD = System.getProperty("db.password", "");

    private static Connection connection;

    private Koneksi() {
    }

    /** Koneksi tunggal yang dipakai ulang agar aplikasi tidak membuka koneksi baru terus-menerus. */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    /** Menjalankan SELECT dengan parameter aman, hasilnya satu array String per baris. */
    public static List<String[]> query(String sql, Object... params) throws SQLException {
        List<String[]> hasil = new ArrayList<>();
        try (PreparedStatement ps = prepare(sql, params);
                ResultSet rs = ps.executeQuery()) {
            int jumlahKolom = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                String[] baris = new String[jumlahKolom];
                for (int i = 0; i < jumlahKolom; i++) {
                    baris[i] = rs.getString(i + 1);
                }
                hasil.add(baris);
            }
        }
        return hasil;
    }

    /** Menjalankan INSERT/UPDATE/DELETE dan mengembalikan jumlah baris yang terpengaruh. */
    public static int update(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = prepare(sql, params)) {
            return ps.executeUpdate();
        }
    }

    private static PreparedStatement prepare(String sql, Object... params) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps;
    }
}

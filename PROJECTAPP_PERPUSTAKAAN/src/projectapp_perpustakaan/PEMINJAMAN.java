package projectapp_perpustakaan;

import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Form transaksi peminjaman buku beserta cetak nota JasperReports.
 */
public class PEMINJAMAN extends javax.swing.JInternalFrame {

    private static final String[] HEADER_ANGGOTA = {"KodeAnggota", "Nama", "Npm", "ProgramStudi", "NoHp", "Alamat"};
    private static final String[] HEADER_BUKU = {"KodeBuku", "Judul", "Pengarang", "TahunTerbit", "Stok", "JenisBuku"};
    private static final String[] HEADER_TEMPAT_PINJAM = {"KA", "KB", "Judul", "Pengarang", "Terbit", "Jenis", "Pinjam", "Kembali", "Jumlah"};
    private static final String[] HEADER_TRANSAKSI = {"KT", "KA", "KB", "Judul", "Pengarang", "Terbit", "Jenis", "Pinjam", "Kembali", "Tersedia", "Jumlah", "SisaBuku"};

    public PEMINJAMAN() {
        initComponents();
        siapIsi(false);
        tombolNormal();
        Tema.poles(this);
        Tema.utama(bttambah);
        Tema.utama(btsimpan);
        Tema.bahaya(bthapus);
        Tema.netral(btcarianggota);
        Tema.netral(btcaribuku);
        Tema.netral(bttabeltransaksi);
        Tema.netral(bttabelpinjam);
    }

    /** Membuat kode transaksi berikutnya, misalnya KT014. */
    private void kodetransaksi() {
        try {
            String sql = "select right(max(kodetransaksi),2)+1 from tb_peminjaman";
            List<String[]> hasil = Koneksi.query(sql);
            String no = hasil.isEmpty() ? null : hasil.get(0)[0];
            if (no == null) {
                no = "1";
            }
            while (no.length() < 3) {
                no = "0" + no;
            }
            txkodetransaksi.setText("KT" + no);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal membuat kode transaksi : " + e.getMessage());
        }
    }

    private void siapIsi(boolean a) {
        txkodetransaksi.setEnabled(a);
        txkodeanggota.setEnabled(a);
        txkodebuku.setEnabled(a);
        txjudul.setEnabled(a);
        txpengarang.setEnabled(a);
        txtahunterbit.setEnabled(a);
        txjenisbuku.setEnabled(a);
        txtanggalpinjam.setEnabled(a);
        txtanggalkembali.setEnabled(a);
        txtersedia.setEnabled(a);
        txjumlahpinjam.setEnabled(a);
        txsisabuku.setEnabled(a);
    }

    private void tombolNormal() {
        bttambah.setEnabled(true);
        btsimpan.setEnabled(false);
        bthapus.setEnabled(false);
        btcarianggota.setEnabled(false);
        btcaribuku.setEnabled(false);
        bttabeltransaksi.setEnabled(true);
        bttabelpinjam.setEnabled(true);
    }

    private void bersih() {
        txkodetransaksi.setText("");
        txkodeanggota.setText("");
        txkodebuku.setText("");
        txjudul.setText("");
        txpengarang.setText("");
        txtahunterbit.setText("");
        txjenisbuku.setText("");
        txtersedia.setText("");
        txjumlahpinjam.setText("");
        txsisabuku.setText("");
    }

    /** Menyimpan keranjang pinjam sementara; trigger database mengurangi stok buku. */
    private void simpan_tempatpinjam() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String tanggalpinjam = date.format(txtanggalpinjam.getDate());
        String tanggalkembali = date.format(txtanggalkembali.getDate());
        try {
            Koneksi.update("insert into tb_tempatpinjam values(?,?,?,?,?,?,?,?,?)",
                    txkodeanggota.getText(), txkodebuku.getText(), txjudul.getText(),
                    txpengarang.getText(), txtahunterbit.getText(), txjenisbuku.getText(),
                    tanggalpinjam, tanggalkembali, txjumlahpinjam.getText());
            JOptionPane.showMessageDialog(null, "SIMPAN DATA PINJAM BERHASIL");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data pinjam : " + e.getMessage());
        }
    }

    private void simpan_transaksi() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String tanggalpinjam = date.format(txtanggalpinjam.getDate());
        String tanggalkembali = date.format(txtanggalkembali.getDate());
        try {
            Koneksi.update("insert into tb_peminjaman values(?,?,?,?,?,?,?,?,?,?,?,?)",
                    txkodetransaksi.getText(), txkodeanggota.getText(), txkodebuku.getText(),
                    txjudul.getText(), txpengarang.getText(), txtahunterbit.getText(),
                    txjenisbuku.getText(), tanggalpinjam, tanggalkembali, txtersedia.getText(),
                    txjumlahpinjam.getText(), txsisabuku.getText());
            JOptionPane.showMessageDialog(null, "SIMPAN DATA TRANSAKSI BERHASIL");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan transaksi : " + e.getMessage());
        }
        tampiltabeltransaksi();
    }

    private void hapustransaksi() {
        try {
            Koneksi.update("delete from tb_peminjaman where kodetransaksi=?", txkodetransaksi.getText());
            JOptionPane.showMessageDialog(null, "DATA TRANSAKSI TELAH DIHAPUS");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus transaksi : " + e.getMessage());
        }
        tampiltabeltransaksi();
        hapustempatpinjam();
    }

    private void hapustempatpinjam() {
        try {
            Koneksi.update("delete from tb_tempatpinjam where kodeanggota=?", txkodeanggota.getText());
            JOptionPane.showMessageDialog(null, "DATA PINJAM TELAH DIHAPUS");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data pinjam : " + e.getMessage());
        }
        tampiltabeltransaksi();
    }

    public void tampildaftaranggota() {
        TabelData.isi(tabelanggota, HEADER_ANGGOTA, "select*from tb_anggota");
    }

    public void tampildaftarbuku() {
        TabelData.isi(tabelbuku, HEADER_BUKU, "select*from tb_buku");
    }

    public void tampiltabeltempatpinjam() {
        TabelData.isi(tabeltempatpinjam, HEADER_TEMPAT_PINJAM, "select*from tb_tempatpinjam");
    }

    public void tampiltabeltransaksi() {
        TabelData.isi(tabeltransaksi, HEADER_TRANSAKSI, "select*from tb_peminjaman");
    }

    /** Menghitung sisa stok setelah jumlah pinjam diisi. */
    public void hitungstok() {
        try {
            int tersedia = Integer.parseInt(txtersedia.getText());
            int jumlahpinjam = Integer.parseInt(txjumlahpinjam.getText());

            if (jumlahpinjam > tersedia) {
                JOptionPane.showMessageDialog(null, "Stok buku tidak mencukupi", "", JOptionPane.INFORMATION_MESSAGE);
            } else {
                txsisabuku.setText(Integer.toString(tersedia - jumlahpinjam));
            }
        } catch (NumberFormatException e) {
            txsisabuku.setText("");
        }
    }

    public void nota() {
        try {
            InputStream report = getClass().getResourceAsStream("/REPORT/transaksipeminjaman.jasper");
            HashMap param = new HashMap();
            param.put("ptrans", txkodetransaksi.getText());
            JasperPrint print = JasperFillManager.fillReport(report, param, Koneksi.getConnection());
            JasperViewer.viewReport(print, false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data tidak dapat dicetak!", "Cetak Data", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jdtempatpinjam = new javax.swing.JDialog();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabeltempatpinjam = new javax.swing.JTable();
        txpencariantempatpinjam = new javax.swing.JTextField();
        jdtabeltransaksi = new javax.swing.JDialog();
        jInternalFrame2 = new javax.swing.JInternalFrame();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabeltransaksi = new javax.swing.JTable();
        txpencariantransaksi = new javax.swing.JTextField();
        jdtabelbuku = new javax.swing.JDialog();
        jInternalFrame3 = new javax.swing.JInternalFrame();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabelbuku = new javax.swing.JTable();
        txpencarianbuku = new javax.swing.JTextField();
        jdtabelanggota = new javax.swing.JDialog();
        jInternalFrame4 = new javax.swing.JInternalFrame();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabelanggota = new javax.swing.JTable();
        txpencariananggota = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txkodetransaksi = new javax.swing.JTextField();
        txkodeanggota = new javax.swing.JTextField();
        txkodebuku = new javax.swing.JTextField();
        txpengarang = new javax.swing.JTextField();
        txjudul = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtahunterbit = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txjenisbuku = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtersedia = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txjumlahpinjam = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txsisabuku = new javax.swing.JTextField();
        txtanggalpinjam = new com.toedter.calendar.JDateChooser();
        txtanggalkembali = new com.toedter.calendar.JDateChooser();
        btcarianggota = new javax.swing.JButton();
        btcaribuku = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        bttambah = new javax.swing.JButton();
        btsimpan = new javax.swing.JButton();
        bthapus = new javax.swing.JButton();
        bttabeltransaksi = new javax.swing.JButton();
        bttabelpinjam = new javax.swing.JButton();

        jdtempatpinjam.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jdtempatpinjam.setBackground(new java.awt.Color(0, 0, 51));
        jdtempatpinjam.setMinimumSize(new java.awt.Dimension(1074, 430));
        jdtempatpinjam.setModal(true);
        jdtempatpinjam.setResizable(false);

        jInternalFrame1.setTitle("TABEL TEMPAT PINJAM");
        jInternalFrame1.setPreferredSize(new java.awt.Dimension(694, 430));
        jInternalFrame1.setVisible(true);
        jInternalFrame1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jInternalFrame1MouseClicked(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(51, 51, 51));

        tabeltempatpinjam.setAutoCreateRowSorter(true);
        tabeltempatpinjam.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabeltempatpinjam.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabeltempatpinjam.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabeltempatpinjamMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tabeltempatpinjam);

        txpencariantempatpinjam.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        txpencariantempatpinjam.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txpencariantempatpinjam.setText("KETIK KODE ANGGOTA ATAU KODE BUKU UNTUK MELAKUKAN PENCARIAN");
        txpencariantempatpinjam.setPreferredSize(new java.awt.Dimension(87, 30));
        txpencariantempatpinjam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txpencariantempatpinjamKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txpencariantempatpinjam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1040, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txpencariantempatpinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jdtempatpinjamLayout = new javax.swing.GroupLayout(jdtempatpinjam.getContentPane());
        jdtempatpinjam.getContentPane().setLayout(jdtempatpinjamLayout);
        jdtempatpinjamLayout.setHorizontalGroup(
            jdtempatpinjamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jdtempatpinjamLayout.createSequentialGroup()
                .addComponent(jInternalFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, 1074, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jdtempatpinjamLayout.setVerticalGroup(
            jdtempatpinjamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jInternalFrame1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jdtabeltransaksi.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jdtabeltransaksi.setBackground(new java.awt.Color(0, 0, 51));
        jdtabeltransaksi.setMinimumSize(new java.awt.Dimension(1072, 430));
        jdtabeltransaksi.setModal(true);
        jdtabeltransaksi.setResizable(false);

        jInternalFrame2.setTitle("TABEL TRANSAKSI");
        jInternalFrame2.setPreferredSize(new java.awt.Dimension(694, 430));
        jInternalFrame2.setVisible(true);
        jInternalFrame2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jInternalFrame2MouseClicked(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(51, 51, 51));

        tabeltransaksi.setAutoCreateRowSorter(true);
        tabeltransaksi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabeltransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabeltransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabeltransaksiMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tabeltransaksi);

        txpencariantransaksi.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        txpencariantransaksi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txpencariantransaksi.setText("KETIK KODE TRANSAKSI UNTUK MELAKUKAN PENCARIAN");
        txpencariantransaksi.setPreferredSize(new java.awt.Dimension(87, 30));
        txpencariantransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txpencariantransaksiActionPerformed(evt);
            }
        });
        txpencariantransaksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txpencariantransaksiKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txpencariantransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1038, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txpencariantransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jInternalFrame2Layout = new javax.swing.GroupLayout(jInternalFrame2.getContentPane());
        jInternalFrame2.getContentPane().setLayout(jInternalFrame2Layout);
        jInternalFrame2Layout.setHorizontalGroup(
            jInternalFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jInternalFrame2Layout.setVerticalGroup(
            jInternalFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jdtabeltransaksiLayout = new javax.swing.GroupLayout(jdtabeltransaksi.getContentPane());
        jdtabeltransaksi.getContentPane().setLayout(jdtabeltransaksiLayout);
        jdtabeltransaksiLayout.setHorizontalGroup(
            jdtabeltransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jdtabeltransaksiLayout.createSequentialGroup()
                .addComponent(jInternalFrame2, javax.swing.GroupLayout.PREFERRED_SIZE, 1072, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jdtabeltransaksiLayout.setVerticalGroup(
            jdtabeltransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jInternalFrame2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jdtabelbuku.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jdtabelbuku.setBackground(new java.awt.Color(0, 0, 51));
        jdtabelbuku.setMinimumSize(new java.awt.Dimension(694, 430));
        jdtabelbuku.setModal(true);
        jdtabelbuku.setResizable(false);

        jInternalFrame3.setTitle("TABEL BUKU");
        jInternalFrame3.setPreferredSize(new java.awt.Dimension(694, 430));
        jInternalFrame3.setVisible(true);
        jInternalFrame3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jInternalFrame3MouseClicked(evt);
            }
        });

        jPanel7.setBackground(new java.awt.Color(51, 51, 51));

        tabelbuku.setAutoCreateRowSorter(true);
        tabelbuku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabelbuku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabelbuku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelbukuMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tabelbuku);

        txpencarianbuku.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        txpencarianbuku.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txpencarianbuku.setText("KETIK KODE BUKU UNTUK MELAKUKAN PENCARIAN");
        txpencarianbuku.setPreferredSize(new java.awt.Dimension(87, 30));
        txpencarianbuku.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txpencarianbukuKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txpencarianbuku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txpencarianbuku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jInternalFrame3Layout = new javax.swing.GroupLayout(jInternalFrame3.getContentPane());
        jInternalFrame3.getContentPane().setLayout(jInternalFrame3Layout);
        jInternalFrame3Layout.setHorizontalGroup(
            jInternalFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jInternalFrame3Layout.setVerticalGroup(
            jInternalFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jdtabelbukuLayout = new javax.swing.GroupLayout(jdtabelbuku.getContentPane());
        jdtabelbuku.getContentPane().setLayout(jdtabelbukuLayout);
        jdtabelbukuLayout.setHorizontalGroup(
            jdtabelbukuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jInternalFrame3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jdtabelbukuLayout.setVerticalGroup(
            jdtabelbukuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jInternalFrame3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jdtabelanggota.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jdtabelanggota.setBackground(new java.awt.Color(0, 0, 51));
        jdtabelanggota.setMinimumSize(new java.awt.Dimension(694, 430));
        jdtabelanggota.setModal(true);
        jdtabelanggota.setResizable(false);

        jInternalFrame4.setTitle("TABEL ANGGOTA");
        jInternalFrame4.setPreferredSize(new java.awt.Dimension(694, 430));
        jInternalFrame4.setVisible(true);
        jInternalFrame4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jInternalFrame4MouseClicked(evt);
            }
        });

        jPanel8.setBackground(new java.awt.Color(51, 51, 51));

        tabelanggota.setAutoCreateRowSorter(true);
        tabelanggota.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabelanggota.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabelanggota.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelanggotaMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tabelanggota);

        txpencariananggota.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        txpencariananggota.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txpencariananggota.setText("KETIK KODE ANGGOTA UNTUK MELAKUKAN PENCARIAN");
        txpencariananggota.setPreferredSize(new java.awt.Dimension(87, 30));
        txpencariananggota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txpencariananggotaKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txpencariananggota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txpencariananggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jInternalFrame4Layout = new javax.swing.GroupLayout(jInternalFrame4.getContentPane());
        jInternalFrame4.getContentPane().setLayout(jInternalFrame4Layout);
        jInternalFrame4Layout.setHorizontalGroup(
            jInternalFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jInternalFrame4Layout.setVerticalGroup(
            jInternalFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jdtabelanggotaLayout = new javax.swing.GroupLayout(jdtabelanggota.getContentPane());
        jdtabelanggota.getContentPane().setLayout(jdtabelanggotaLayout);
        jdtabelanggotaLayout.setHorizontalGroup(
            jdtabelanggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jInternalFrame4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jdtabelanggotaLayout.setVerticalGroup(
            jdtabelanggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jInternalFrame4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("PEMINJAMAN BUKU");
        setToolTipText("");

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel2.setToolTipText("");

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("KODE TRANSAKSI");

        jLabel2.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("KODE ANGGOTA");

        jLabel3.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("KODE BUKU");

        jLabel4.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("PENGARANG");

        jLabel5.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("JUDUL");

        txkodetransaksi.setEditable(false);
        txkodetransaksi.setBackground(new java.awt.Color(204, 204, 204));
        txkodetransaksi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txkodetransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txkodetransaksiActionPerformed(evt);
            }
        });

        txkodeanggota.setEditable(false);
        txkodeanggota.setBackground(new java.awt.Color(204, 204, 204));
        txkodeanggota.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txkodeanggota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txkodeanggotaActionPerformed(evt);
            }
        });

        txkodebuku.setBackground(new java.awt.Color(204, 204, 204));
        txkodebuku.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txkodebuku.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                txkodebukuComponentAdded(evt);
            }
        });
        txkodebuku.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                txkodebukuAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        txkodebuku.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txkodebukuInputMethodTextChanged(evt);
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                txkodebukuCaretPositionChanged(evt);
            }
        });
        txkodebuku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txkodebukuActionPerformed(evt);
            }
        });

        txpengarang.setEditable(false);
        txpengarang.setBackground(new java.awt.Color(204, 204, 204));
        txpengarang.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txpengarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txpengarangActionPerformed(evt);
            }
        });

        txjudul.setEditable(false);
        txjudul.setBackground(new java.awt.Color(204, 204, 204));
        txjudul.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txjudul.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txjudulActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("TAHUN TERBIT");

        txtahunterbit.setEditable(false);
        txtahunterbit.setBackground(new java.awt.Color(204, 204, 204));
        txtahunterbit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtahunterbit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtahunterbitActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("JENIS BUKU");

        txjenisbuku.setEditable(false);
        txjenisbuku.setBackground(new java.awt.Color(204, 204, 204));
        txjenisbuku.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txjenisbuku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txjenisbukuActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("TANGGAL PINJAM");

        jLabel9.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("TANGGAL KEMBALI");

        jLabel10.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("TERSEDIA");

        txtersedia.setEditable(false);
        txtersedia.setBackground(new java.awt.Color(204, 204, 204));
        txtersedia.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtersedia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtersediaActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("JUMLAH PINJAM");

        txjumlahpinjam.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txjumlahpinjam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txjumlahpinjamActionPerformed(evt);
            }
        });
        txjumlahpinjam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txjumlahpinjamKeyPressed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("SISA BUKU");

        txsisabuku.setEditable(false);
        txsisabuku.setBackground(new java.awt.Color(204, 204, 204));
        txsisabuku.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txsisabuku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txsisabukuActionPerformed(evt);
            }
        });

        btcarianggota.setBackground(new java.awt.Color(0, 0, 0));
        btcarianggota.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        btcarianggota.setForeground(new java.awt.Color(255, 255, 255));
        btcarianggota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICON/icon (45).png"))); // NOI18N
        btcarianggota.setText("ANGGOTA");
        btcarianggota.setPreferredSize(new java.awt.Dimension(87, 40));
        btcarianggota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcarianggotaActionPerformed(evt);
            }
        });

        btcaribuku.setBackground(new java.awt.Color(0, 0, 0));
        btcaribuku.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        btcaribuku.setForeground(new java.awt.Color(255, 255, 255));
        btcaribuku.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICON/icon (45).png"))); // NOI18N
        btcaribuku.setText("BUKU");
        btcaribuku.setPreferredSize(new java.awt.Dimension(87, 40));
        btcaribuku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcaribukuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txpengarang)
                        .addComponent(txjudul)
                        .addComponent(txtahunterbit)
                        .addComponent(txkodetransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txkodeanggota, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                            .addComponent(txkodebuku))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btcaribuku, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btcarianggota, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel10)
                            .addComponent(jLabel12))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(txtersedia, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txsisabuku, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(28, 28, 28)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txjenisbuku, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtanggalpinjam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtanggalkembali, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txjumlahpinjam, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(85, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txkodetransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel6)
                    .addComponent(txjenisbuku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtanggalpinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtanggalkembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtersedia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txjumlahpinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txsisabuku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btcarianggota, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txkodeanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(jLabel8)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btcaribuku, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(txkodebuku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txjudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txpengarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtahunterbit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));

        bttambah.setBackground(new java.awt.Color(0, 0, 0));
        bttambah.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        bttambah.setForeground(new java.awt.Color(255, 255, 255));
        bttambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICON/icon (24).png"))); // NOI18N
        bttambah.setText("TAMBAH");
        bttambah.setPreferredSize(new java.awt.Dimension(87, 40));
        bttambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttambahActionPerformed(evt);
            }
        });

        btsimpan.setBackground(new java.awt.Color(0, 0, 0));
        btsimpan.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        btsimpan.setForeground(new java.awt.Color(255, 255, 255));
        btsimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICON/icon (21).png"))); // NOI18N
        btsimpan.setText("SIMPAN");
        btsimpan.setPreferredSize(new java.awt.Dimension(87, 40));
        btsimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsimpanActionPerformed(evt);
            }
        });

        bthapus.setBackground(new java.awt.Color(0, 0, 0));
        bthapus.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        bthapus.setForeground(new java.awt.Color(255, 255, 255));
        bthapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICON/icon (26).png"))); // NOI18N
        bthapus.setText("HAPUS");
        bthapus.setPreferredSize(new java.awt.Dimension(87, 40));
        bthapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthapusActionPerformed(evt);
            }
        });

        bttabeltransaksi.setBackground(new java.awt.Color(0, 0, 0));
        bttabeltransaksi.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        bttabeltransaksi.setForeground(new java.awt.Color(255, 255, 255));
        bttabeltransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICON/icon (121).png"))); // NOI18N
        bttabeltransaksi.setText("TB TRANSAKSI");
        bttabeltransaksi.setPreferredSize(new java.awt.Dimension(87, 40));
        bttabeltransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttabeltransaksiActionPerformed(evt);
            }
        });

        bttabelpinjam.setBackground(new java.awt.Color(0, 0, 0));
        bttabelpinjam.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        bttabelpinjam.setForeground(new java.awt.Color(255, 255, 255));
        bttabelpinjam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICON/icon (121).png"))); // NOI18N
        bttabelpinjam.setText("TB PINJAM");
        bttabelpinjam.setPreferredSize(new java.awt.Dimension(87, 40));
        bttabelpinjam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttabelpinjamActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(bttabeltransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bttabelpinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bttambah, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btsimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bthapus, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bttambah, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btsimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bthapus, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bttabeltransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bttabelpinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setBounds(0, 0, 865, 360);
    }// </editor-fold>//GEN-END:initComponents

    private void txjudulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txjudulActionPerformed
    }//GEN-LAST:event_txjudulActionPerformed

    private void txpengarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txpengarangActionPerformed
    }//GEN-LAST:event_txpengarangActionPerformed

    private void txkodebukuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txkodebukuActionPerformed

    }//GEN-LAST:event_txkodebukuActionPerformed

    private void txkodeanggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txkodeanggotaActionPerformed
    }//GEN-LAST:event_txkodeanggotaActionPerformed

    private void txkodetransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txkodetransaksiActionPerformed
    }//GEN-LAST:event_txkodetransaksiActionPerformed

    private void btcarianggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcarianggotaActionPerformed
        jdtabelanggota.setLocationRelativeTo(null);
        tampildaftaranggota();
        jdtabelanggota.setVisible(true);

    }//GEN-LAST:event_btcarianggotaActionPerformed

    private void bttambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttambahActionPerformed
        if (bttambah.getText().equalsIgnoreCase("TAMBAH")) {
            bttambah.setText("BATAL");
            bersih();
            siapIsi(true);
            kodetransaksi();

            txkodetransaksi.setEnabled(true);
            txkodeanggota.setEnabled(true);
            bttambah.setEnabled(true);
            btsimpan.setEnabled(true);
            bthapus.setEnabled(false);
            btcarianggota.setEnabled(true);
            btcaribuku.setEnabled(true);
            bttabeltransaksi.setEnabled(false);
            bttabelpinjam.setEnabled(false);
        } else {
            bttambah.setText("TAMBAH");
            bersih();
            siapIsi(false);
            tombolNormal();
            tampiltabeltransaksi();
        }
    }//GEN-LAST:event_bttambahActionPerformed
    private void btsimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsimpanActionPerformed
        if (txkodetransaksi.getText().isEmpty()
                || txkodeanggota.getText().isEmpty()
                || txkodebuku.getText().isEmpty()
                || txpengarang.getText().isEmpty()
                || txjudul.getText().isEmpty()
                || txtahunterbit.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "LENGKAPI INPUTAN DATA!!!", "", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        hitungstok();
        if (bttambah.getText().equalsIgnoreCase("BATAL")) {
            simpan_tempatpinjam();
            simpan_transaksi();

            int pesan = JOptionPane.showConfirmDialog(null, "Print Out Nota?", "Print",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (pesan == JOptionPane.YES_OPTION) {
                nota();
            }
            bersih();
        }
    }//GEN-LAST:event_btsimpanActionPerformed
    private void bthapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthapusActionPerformed
        int pesan = JOptionPane.showConfirmDialog(null, "YAKIN DATA AKAN DIHAPUS ?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (pesan == JOptionPane.YES_OPTION) {
            hapustransaksi();
            bersih();
            siapIsi(false);
            tombolNormal();
        }
    }//GEN-LAST:event_bthapusActionPerformed
    private void txjenisbukuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txjenisbukuActionPerformed
    }//GEN-LAST:event_txjenisbukuActionPerformed

    private void txtersediaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtersediaActionPerformed
    }//GEN-LAST:event_txtersediaActionPerformed

    private void txjumlahpinjamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txjumlahpinjamActionPerformed
        hitungstok();
    }//GEN-LAST:event_txjumlahpinjamActionPerformed

    private void txsisabukuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txsisabukuActionPerformed
    }//GEN-LAST:event_txsisabukuActionPerformed

    private void txtahunterbitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtahunterbitActionPerformed
    }//GEN-LAST:event_txtahunterbitActionPerformed

    private void btcaribukuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcaribukuActionPerformed
        jdtabelbuku.setLocationRelativeTo(null);
        tampildaftarbuku();
        jdtabelbuku.setVisible(true);
    }//GEN-LAST:event_btcaribukuActionPerformed

    private void bttabeltransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttabeltransaksiActionPerformed
        jdtabeltransaksi.setLocationRelativeTo(null);
        tampiltabeltransaksi();
        jdtabeltransaksi.setVisible(true);
    }//GEN-LAST:event_bttabeltransaksiActionPerformed

    private void bttabelpinjamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttabelpinjamActionPerformed
        jdtempatpinjam.setLocationRelativeTo(null);
        tampiltabeltempatpinjam();
        jdtempatpinjam.setVisible(true);
    }//GEN-LAST:event_bttabelpinjamActionPerformed

    private void tabeltempatpinjamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabeltempatpinjamMouseClicked
        int baris = tabeltempatpinjam.getSelectedRow();
        if (baris < 0) {
            return;
        }
        txkodeanggota.setText(tabeltempatpinjam.getModel().getValueAt(baris, 0).toString());
        txkodebuku.setText(tabeltempatpinjam.getModel().getValueAt(baris, 1).toString());
        txjudul.setText(tabeltempatpinjam.getModel().getValueAt(baris, 2).toString());
        txpengarang.setText(tabeltempatpinjam.getModel().getValueAt(baris, 3).toString());
        txtahunterbit.setText(tabeltempatpinjam.getModel().getValueAt(baris, 4).toString());
        txjenisbuku.setText(tabeltempatpinjam.getModel().getValueAt(baris, 5).toString());
        txjumlahpinjam.setText(tabeltempatpinjam.getModel().getValueAt(baris, 8).toString());
        bthapus.setEnabled(true);
        jdtempatpinjam.dispose();
    }//GEN-LAST:event_tabeltempatpinjamMouseClicked

    private void txpencariantempatpinjamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txpencariantempatpinjamKeyPressed
        String cari = "%" + txpencariantempatpinjam.getText() + "%";
        TabelData.isi(tabeltempatpinjam, HEADER_TEMPAT_PINJAM,
                "select * from tb_tempatpinjam where kodeanggota like ? or kodebuku like ?", cari, cari);
    }//GEN-LAST:event_txpencariantempatpinjamKeyPressed

    private void jInternalFrame1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jInternalFrame1MouseClicked
    }//GEN-LAST:event_jInternalFrame1MouseClicked

    private void tabeltransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabeltransaksiMouseClicked
        int baris = tabeltransaksi.getSelectedRow();
        if (baris < 0) {
            return;
        }
        txkodetransaksi.setText(tabeltransaksi.getModel().getValueAt(baris, 0).toString());
        txkodeanggota.setText(tabeltransaksi.getModel().getValueAt(baris, 1).toString());
        txkodebuku.setText(tabeltransaksi.getModel().getValueAt(baris, 2).toString());
        txjudul.setText(tabeltransaksi.getModel().getValueAt(baris, 3).toString());
        txpengarang.setText(tabeltransaksi.getModel().getValueAt(baris, 4).toString());
        txtahunterbit.setText(tabeltransaksi.getModel().getValueAt(baris, 5).toString());
        txjenisbuku.setText(tabeltransaksi.getModel().getValueAt(baris, 6).toString());
        txtersedia.setText(tabeltransaksi.getModel().getValueAt(baris, 9).toString());
        txjumlahpinjam.setText(tabeltransaksi.getModel().getValueAt(baris, 10).toString());
        txsisabuku.setText(tabeltransaksi.getModel().getValueAt(baris, 11).toString());
        bthapus.setEnabled(true);
        jdtabeltransaksi.dispose();
        nota();
    }//GEN-LAST:event_tabeltransaksiMouseClicked

    private void txpencariantransaksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txpencariantransaksiKeyPressed
        String cari = "%" + txpencariantransaksi.getText() + "%";
        TabelData.isi(tabeltransaksi, HEADER_TRANSAKSI,
                "select * from tb_peminjaman where kodetransaksi like ? or kodeanggota like ?", cari, cari);
    }//GEN-LAST:event_txpencariantransaksiKeyPressed

    private void jInternalFrame2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jInternalFrame2MouseClicked
    }//GEN-LAST:event_jInternalFrame2MouseClicked

    private void tabelbukuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelbukuMouseClicked
        int baris = tabelbuku.getSelectedRow();
        if (baris < 0) {
            return;
        }
        txkodebuku.setText(tabelbuku.getModel().getValueAt(baris, 0).toString());
        txjudul.setText(tabelbuku.getModel().getValueAt(baris, 1).toString());
        txpengarang.setText(tabelbuku.getModel().getValueAt(baris, 2).toString());
        txtahunterbit.setText(tabelbuku.getModel().getValueAt(baris, 3).toString());
        txtersedia.setText(tabelbuku.getModel().getValueAt(baris, 4).toString());
        txjenisbuku.setText(tabelbuku.getModel().getValueAt(baris, 5).toString());
        bthapus.setEnabled(true);
        jdtabelbuku.dispose();
    }//GEN-LAST:event_tabelbukuMouseClicked

    private void txpencarianbukuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txpencarianbukuKeyPressed
        String cari = "%" + txpencarianbuku.getText() + "%";
        TabelData.isi(tabelbuku, HEADER_BUKU,
                "select * from tb_buku where kodebuku like ? or judul like ?", cari, cari);
    }//GEN-LAST:event_txpencarianbukuKeyPressed

    private void jInternalFrame3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jInternalFrame3MouseClicked
    }//GEN-LAST:event_jInternalFrame3MouseClicked

    private void tabelanggotaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelanggotaMouseClicked
        int baris = tabelanggota.getSelectedRow();
        if (baris < 0) {
            return;
        }
        txkodeanggota.setText(tabelanggota.getModel().getValueAt(baris, 0).toString());
        bthapus.setEnabled(true);
        jdtabelanggota.dispose();
    }//GEN-LAST:event_tabelanggotaMouseClicked

    private void txpencariananggotaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txpencariananggotaKeyPressed
        String cari = "%" + txpencariananggota.getText() + "%";
        TabelData.isi(tabelanggota, HEADER_ANGGOTA,
                "select * from tb_anggota where kodeanggota like ? or nama like ?", cari, cari);
    }//GEN-LAST:event_txpencariananggotaKeyPressed

    private void jInternalFrame4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jInternalFrame4MouseClicked
    }//GEN-LAST:event_jInternalFrame4MouseClicked

    private void txkodebukuComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_txkodebukuComponentAdded
    }//GEN-LAST:event_txkodebukuComponentAdded

    private void txkodebukuCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txkodebukuCaretPositionChanged
    }//GEN-LAST:event_txkodebukuCaretPositionChanged

    private void txkodebukuAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_txkodebukuAncestorAdded

    }//GEN-LAST:event_txkodebukuAncestorAdded

    private void txkodebukuInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txkodebukuInputMethodTextChanged

    }//GEN-LAST:event_txkodebukuInputMethodTextChanged

    private void txpencariantransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txpencariantransaksiActionPerformed
    }//GEN-LAST:event_txpencariantransaksiActionPerformed

    private void txjumlahpinjamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txjumlahpinjamKeyPressed
    }//GEN-LAST:event_txjumlahpinjamKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcarianggota;
    private javax.swing.JButton btcaribuku;
    private javax.swing.JButton bthapus;
    private javax.swing.JButton btsimpan;
    private javax.swing.JButton bttabelpinjam;
    private javax.swing.JButton bttabeltransaksi;
    private javax.swing.JButton bttambah;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JInternalFrame jInternalFrame2;
    private javax.swing.JInternalFrame jInternalFrame3;
    private javax.swing.JInternalFrame jInternalFrame4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JDialog jdtabelanggota;
    private javax.swing.JDialog jdtabelbuku;
    private javax.swing.JDialog jdtabeltransaksi;
    private javax.swing.JDialog jdtempatpinjam;
    private javax.swing.JTable tabelanggota;
    private javax.swing.JTable tabelbuku;
    private javax.swing.JTable tabeltempatpinjam;
    private javax.swing.JTable tabeltransaksi;
    private javax.swing.JTextField txjenisbuku;
    private javax.swing.JTextField txjudul;
    private javax.swing.JTextField txjumlahpinjam;
    private javax.swing.JTextField txkodeanggota;
    private javax.swing.JTextField txkodebuku;
    private javax.swing.JTextField txkodetransaksi;
    private javax.swing.JTextField txpencariananggota;
    private javax.swing.JTextField txpencarianbuku;
    private javax.swing.JTextField txpencariantempatpinjam;
    private javax.swing.JTextField txpencariantransaksi;
    private javax.swing.JTextField txpengarang;
    private javax.swing.JTextField txsisabuku;
    private javax.swing.JTextField txtahunterbit;
    private com.toedter.calendar.JDateChooser txtanggalkembali;
    private com.toedter.calendar.JDateChooser txtanggalpinjam;
    private javax.swing.JTextField txtersedia;
    // End of variables declaration//GEN-END:variables
}

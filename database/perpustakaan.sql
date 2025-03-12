-- =====================================================================
-- Aplikasi Perpustakaan - Skema Database
-- MySQL 5.7+ / MariaDB 10.3+  (dipakai JDBC dari aplikasi Java Swing)
--
-- Import:
--   mysql -u root -p < database/perpustakaan.sql
-- atau buka file ini lewat phpMyAdmin > Import.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `db_perpustakaan`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `db_perpustakaan`;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `tb_anggota`;
DROP TABLE IF EXISTS `tb_buku`;
DROP TABLE IF EXISTS `tb_peminjaman`;
DROP TABLE IF EXISTS `tb_kembalikanbuku`;
DROP TABLE IF EXISTS `tb_tempatpinjam`;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- Data anggota perpustakaan
-- ---------------------------------------------------------------------
CREATE TABLE `tb_anggota` (
  `kodeanggota` varchar(50) NOT NULL,
  `nama` varchar(50) NOT NULL,
  `npm` varchar(50) NOT NULL,
  `programstudi` varchar(50) NOT NULL,
  `nomerhp` varchar(50) NOT NULL,
  `alamat` varchar(200) NOT NULL,
  PRIMARY KEY (`kodeanggota`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Data buku
-- ---------------------------------------------------------------------
CREATE TABLE `tb_buku` (
  `kodebuku` varchar(50) NOT NULL,
  `judul` varchar(50) NOT NULL,
  `pengarang` varchar(50) NOT NULL,
  `tahunterbit` varchar(50) NOT NULL,
  `stok` varchar(50) NOT NULL,
  `jenisbuku` varchar(50) NOT NULL,
  PRIMARY KEY (`kodebuku`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Keranjang pinjam sementara: satu baris per anggota.
-- Trigger `kurangistokbuku` mengurangi stok tb_buku saat baris ditambah.
-- ---------------------------------------------------------------------
CREATE TABLE `tb_tempatpinjam` (
  `kodeanggota` varchar(50) NOT NULL,
  `kodebuku` varchar(50) NOT NULL,
  `judul` varchar(50) NOT NULL,
  `pengarang` varchar(50) NOT NULL,
  `tahunterbit` varchar(50) NOT NULL,
  `jenisbuku` varchar(50) NOT NULL,
  `tanggalpinjam` date NOT NULL,
  `tanggalkembali` date NOT NULL,
  `jumlahpinjam` varchar(50) NOT NULL,
  PRIMARY KEY (`kodeanggota`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Riwayat transaksi peminjaman
-- ---------------------------------------------------------------------
CREATE TABLE `tb_peminjaman` (
  `kodetransaksi` varchar(20) NOT NULL,
  `kodeanggota` varchar(20) NOT NULL,
  `kodebuku` varchar(20) NOT NULL,
  `judul` varchar(50) NOT NULL,
  `pengarang` varchar(20) NOT NULL,
  `tahunterbit` varchar(20) NOT NULL,
  `jenisbuku` varchar(20) NOT NULL,
  `tanggalpinjam` date NOT NULL,
  `tanggalkembali` date NOT NULL,
  `tersedia` varchar(20) NOT NULL,
  `jumlahpinjam` varchar(20) NOT NULL,
  `sisabuku` varchar(20) NOT NULL,
  PRIMARY KEY (`kodetransaksi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Riwayat transaksi pengembalian
-- Trigger `updatestokbuku` mengembalikan stok tb_buku saat baris ditambah.
-- ---------------------------------------------------------------------
CREATE TABLE `tb_kembalikanbuku` (
  `kodetransaksi` varchar(50) NOT NULL,
  `kodeanggota` varchar(50) NOT NULL,
  `kodebuku` varchar(50) NOT NULL,
  `judul` varchar(50) NOT NULL,
  `pengarang` varchar(50) NOT NULL,
  `tahunterbit` varchar(50) NOT NULL,
  `jenis` varchar(50) NOT NULL,
  `tanggalpinjam` date NOT NULL,
  `tanggalkembali` date NOT NULL,
  `jumlahpinjam` varchar(50) NOT NULL,
  `keterlambatan` varchar(50) NOT NULL,
  `denda` varchar(50) NOT NULL,
  PRIMARY KEY (`kodetransaksi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELIMITER $$
CREATE TRIGGER `kurangistokbuku` AFTER INSERT ON `tb_tempatpinjam`
FOR EACH ROW
BEGIN
  UPDATE tb_buku SET stok = stok - NEW.jumlahpinjam WHERE kodebuku = NEW.kodebuku;
END$$

CREATE TRIGGER `updatestokbuku` AFTER INSERT ON `tb_kembalikanbuku`
FOR EACH ROW
BEGIN
  UPDATE tb_buku SET stok = stok + NEW.jumlahpinjam WHERE kodebuku = NEW.kodebuku;
END$$
DELIMITER ;

-- ---------------------------------------------------------------------
-- Data contoh
-- ---------------------------------------------------------------------
INSERT INTO `tb_anggota` (`kodeanggota`, `nama`, `npm`, `programstudi`, `nomerhp`, `alamat`) VALUES
('KA001', 'Bank Miun', '201443501111', 'Informatika', '085811112222', 'Kp. Miun'),
('KA002', 'Bank Mian', '201443507777', 'Informatika', '085770000000', 'Kp. Rambutan Rt.01/07 Jakarta Timur'),
('KA003', 'Melissa', '20211234', 'Informatika', '081233332222', 'Jl. Bahagian No.19');

INSERT INTO `tb_buku` (`kodebuku`, `judul`, `pengarang`, `tahunterbit`, `stok`, `jenisbuku`) VALUES
('KB001', 'Java Design UI', 'Bank David', '2019', '24', 'Java Design'),
('KB002', 'Cara Makan Nasi', 'Abang Toyib', '2021', '13', 'Bertahan Hidup');

INSERT INTO `tb_peminjaman` (`kodetransaksi`, `kodeanggota`, `kodebuku`, `judul`, `pengarang`, `tahunterbit`, `jenisbuku`, `tanggalpinjam`, `tanggalkembali`, `tersedia`, `jumlahpinjam`, `sisabuku`) VALUES
('KT001', 'KA003', 'KB002', 'Cara Makan Nasi', 'Abang toyib', '2021', 'Bertahan Hidup', '2021-03-24', '2021-03-25', '13', '3', '10'),
('KT002', 'KA003', 'KB001', 'Java Design UI', 'Bank David', '2019', 'Java Design', '2021-03-01', '2021-03-02', '24', '4', '20'),
('KT003', 'KA003', 'KB002', 'Cara Makan Nasi', 'Abang toyib', '2021', 'Bertahan Hidup', '2021-03-01', '2021-03-02', '13', '3', '10');

INSERT INTO `tb_kembalikanbuku` (`kodetransaksi`, `kodeanggota`, `kodebuku`, `judul`, `pengarang`, `tahunterbit`, `jenis`, `tanggalpinjam`, `tanggalkembali`, `jumlahpinjam`, `keterlambatan`, `denda`) VALUES
('KT001', 'KA003', 'KB002', 'Cara Makan Nasi', 'Abang toyib', '2021', 'Bertahan Hidup', '2021-03-24', '2021-03-25', '3', '0', '0'),
('KT002', 'KA003', 'KB001', 'Java Design UI', 'Bank David', '2019', 'Java Design', '2021-03-01', '2021-03-02', '4', '22', '110000');

-- Trigger di atas sudah menambah stok dari data pengembalian contoh,
-- jadi stok diset ulang agar konsisten dengan riwayat tb_peminjaman.
UPDATE `tb_buku` SET `stok` = '24' WHERE `kodebuku` = 'KB001';
UPDATE `tb_buku` SET `stok` = '13' WHERE `kodebuku` = 'KB002';

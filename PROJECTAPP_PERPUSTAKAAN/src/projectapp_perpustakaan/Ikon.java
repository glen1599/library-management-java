package projectapp_perpustakaan;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;

/**
 * Ikon garis (line icons) yang digambar langsung dengan Java2D.
 *
 * Dibuat vektor agar tetap tajam di semua skala dan warnanya bisa mengikuti
 * tema tanpa perlu file gambar.
 */
public final class Ikon {

    private Ikon() {
    }

    public static Icon dashboard(Color warna) {
        return new Garis(warna) {
            @Override
            void bentuk(Graphics2D g) {
                g.draw(new RoundRectangle2D.Double(2.5, 2.5, 6, 6, 2, 2));
                g.draw(new RoundRectangle2D.Double(11.5, 2.5, 6, 6, 2, 2));
                g.draw(new RoundRectangle2D.Double(2.5, 11.5, 6, 6, 2, 2));
                g.draw(new RoundRectangle2D.Double(11.5, 11.5, 6, 6, 2, 2));
            }
        };
    }

    public static Icon anggota(Color warna) {
        return new Garis(warna) {
            @Override
            void bentuk(Graphics2D g) {
                g.draw(new Ellipse2D.Double(7, 2.5, 6, 6));
                g.draw(new Arc2D.Double(3, 11, 14, 12, 0, 180, Arc2D.OPEN));
            }
        };
    }

    public static Icon buku(Color warna) {
        return new Garis(warna) {
            @Override
            void bentuk(Graphics2D g) {
                Path2D buku = new Path2D.Double();
                buku.moveTo(10, 5);
                buku.curveTo(8, 3.4, 5, 2.9, 2.5, 3.4);
                buku.lineTo(2.5, 15.8);
                buku.curveTo(5, 15.3, 8, 15.8, 10, 17.2);
                buku.curveTo(12, 15.8, 15, 15.3, 17.5, 15.8);
                buku.lineTo(17.5, 3.4);
                buku.curveTo(15, 2.9, 12, 3.4, 10, 5);
                buku.closePath();
                g.draw(buku);
                g.draw(new java.awt.geom.Line2D.Double(10, 5, 10, 17.2));
            }
        };
    }

    public static Icon pinjam(Color warna) {
        return new Garis(warna) {
            @Override
            void bentuk(Graphics2D g) {
                g.draw(new java.awt.geom.Line2D.Double(3, 6.5, 16, 6.5));
                g.draw(poli(12.5, 3.5, 15.8, 6.5, 12.5, 9.5));
                g.draw(new java.awt.geom.Line2D.Double(17, 13.5, 4, 13.5));
                g.draw(poli(7.5, 10.5, 4.2, 13.5, 7.5, 16.5));
            }
        };
    }

    public static Icon kembali(Color warna) {
        return new Garis(warna) {
            @Override
            void bentuk(Graphics2D g) {
                Path2D panah = new Path2D.Double();
                panah.moveTo(4.5, 8);
                panah.lineTo(12, 8);
                panah.curveTo(15.5, 8, 15.5, 16, 12, 16);
                panah.lineTo(7, 16);
                g.draw(panah);
                g.draw(poli(7.5, 5, 4.5, 8, 7.5, 11));
            }
        };
    }

    public static Icon plus(Color warna) {
        return new Garis(warna) {
            @Override
            void bentuk(Graphics2D g) {
                g.draw(new java.awt.geom.Line2D.Double(10, 4, 10, 16));
                g.draw(new java.awt.geom.Line2D.Double(4, 10, 16, 10));
            }
        };
    }

    public static Icon tutup(Color warna) {
        return new Garis(warna) {
            @Override
            void bentuk(Graphics2D g) {
                g.draw(new java.awt.geom.Line2D.Double(5, 5, 15, 15));
                g.draw(new java.awt.geom.Line2D.Double(15, 5, 5, 15));
            }
        };
    }

    public static Icon kecilkan(Color warna) {
        return new Garis(warna) {
            @Override
            void bentuk(Graphics2D g) {
                g.draw(new java.awt.geom.Line2D.Double(5, 10, 15, 10));
            }
        };
    }

    private static Path2D poli(double... titik) {
        Path2D jalur = new Path2D.Double();
        jalur.moveTo(titik[0], titik[1]);
        for (int i = 2; i < titik.length; i += 2) {
            jalur.lineTo(titik[i], titik[i + 1]);
        }
        return jalur;
    }

    private abstract static class Garis implements Icon {

        private final Color warna;

        Garis(Color warna) {
            this.warna = warna;
        }

        abstract void bentuk(Graphics2D g);

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(warna);
            g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            bentuk(g2);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 20;
        }
    }
}

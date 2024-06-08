import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

class RenderTarget extends JPanel implements ActionListener {
    private Timer timer;
    private RHI rhi;
    Model testmodel;
    public boolean interpmode = false;

    public RenderTarget(RHI rhi) {

        this.rhi = rhi;
        timer = new Timer(30, this);
        timer.start();

    }

    @Override
    protected void paintComponent(Graphics g) {
        ProcessStat.TickStart();
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        rhi.ProjectAll();
        ProcessStat.TrackStart("描画キューから描画");
        for (Face face : rhi.faces) {
            g2d.setColor(face.shade1);
            int[][] Points = face.Break();
            if (this.interpmode) {
                Color c1c = new Color(face.shade0.getRed(), face.shade0.getGreen(), face.shade0.getBlue(), 0);
                Color c2c = new Color(face.shade1.getRed(), face.shade1.getGreen(), face.shade1.getBlue(), 0);
                Color c3c = new Color(face.shade2.getRed(), face.shade2.getGreen(), face.shade2.getBlue(), 0);
                GradientPaint gradient0 = new GradientPaint((float) face.p0.x, (float) face.p0.y, face.shade0,
                        (float) face.p1.x, (float) face.p1.y, c2c);
                GradientPaint gradient1 = new GradientPaint((float) face.p1.x, (float) face.p1.y, face.shade1,
                        (float) face.p2.x, (float) face.p2.y, c3c);
                GradientPaint gradient2 = new GradientPaint((float) face.p2.x, (float) face.p2.y, face.shade2,
                        (float) face.p0.x, (float) face.p0.y, c1c);

                g2d.setPaint(gradient0);
                g2d.fillPolygon(Points[0], Points[1], Points.length);
                g2d.setPaint(gradient1);
                g2d.fillPolygon(Points[0], Points[1], Points.length);
                g2d.setPaint(gradient2);
            } else {
                g2d.setColor(face.shaded);
            }
            g2d.fillPolygon(Points[0], Points[1], Points.length);
            g2d.setColor(Color.BLACK);
            // g2d.drawPolygon(Points[0], Points[1], Points.length);
        }
        ProcessStat.TrackEnd("描画キューから描画");
        // gradationtest(g2d, 100, 100, 100);
        ProcessStat.TickEnd();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        rhi.models.get(0).modelMatrix.rotate(0, 0.05, 0.01);
        // rhi.models.get(0).modelMatrix.matrix4[2][3] = 6 * Math.sin((double)
        // System.currentTimeMillis() / 1000.0);
        // rhi.models.get(0).modelMatrix
        // .SetPosition(new Point3D(6 * Math.sin((double) System.currentTimeMillis() /
        // 1000.0), 0, 0));
        repaint();
        rhi.ClearFaces();
    }

    public void DrawGlow(Graphics g, int x, int y, int radius) { // RadialGradientPaintの作成
        /*
         * // 円の中心と半径を設定
         * int x = getWidth() / 2;
         * int y = getHeight() / 2;
         * int radius = 100;
         */
        Graphics2D g2d = (Graphics2D) g;
        // グラデーションの設定
        float[] fractions = { 0.0f, 1.0f }; // グラデーションの位置
        Color[] colors = { Color.RED, new Color(0, 0, 0, 0) }; // グラデーションの色
        RadialGradientPaint paint = new RadialGradientPaint(x, y, radius, fractions, colors);

        // グラデーションを描画
        g2d.setPaint(paint);
        g2d.fill(new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius));
    }
}
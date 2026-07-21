package cardengine.application.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;

/**
 * Hintergrund-Panel, das den gruenen Spieltisch (Filz) zeichnet.
 *
 * <p>Reiner Layout-Container mit gemaltem Radial-Verlauf, damit die Kartenkomponenten
 * wie auf einem Kartentisch liegen. Als Layout-Panel weiterhin voll nutzbar.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class FeltPanel extends JPanel {

    private static final Color CENTER = new Color(0x2E7D45);
    private static final Color EDGE = new Color(0x1B5E32);

    public FeltPanel() {
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        float radius = Math.max(w, h) * 0.75f;
        RadialGradientPaint paint = new RadialGradientPaint(
                w / 2f, h / 2f, radius,
                new float[]{0f, 1f},
                new Color[]{CENTER, EDGE});
        g2.setPaint(paint);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }
}

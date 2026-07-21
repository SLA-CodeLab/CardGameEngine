package cardengine.application.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Swing-Ansicht des Nachziehstapels in der Tischmitte.
 *
 * <p>Zeichnet den verbleibenden Zugstapel als kleinen, verdeckten Kartenstapel
 * (mehrere versetzte Rueckseiten via {@link CardRenderer}) samt Restanzahl. Rein
 * darstellend; die Anzahl kommt ueber {@link #setDeckSize(int)}.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class DeckPanel extends JPanel {

    private static final int CARD_W = 62;
    private static final int CARD_H = 88;

    private int deckSize;

    public DeckPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(160, CARD_H + 46));
    }

    /**
     * Setzt die anzuzeigende Restkartenzahl und zeichnet neu.
     *
     * @param deckSize verbleibende Karten im Nachziehstapel
     */
    public void setDeckSize(int deckSize) {
        this.deckSize = deckSize;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cx = (getWidth() - CARD_W) / 2;
        int top = 6;

        if (deckSize <= 0) {
            // Leerer Stapelplatz.
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawRoundRect(cx, top, CARD_W, CARD_H, 10, 10);
        } else {
            // Ein paar versetzte Rueckseiten fuer die Stapeltiefe.
            int layers = Math.min(4, deckSize);
            for (int i = layers - 1; i >= 0; i--) {
                CardRenderer.paintCard(g2, null, cx + i * 2, top + i * 2, CARD_W, CARD_H);
            }
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String label = "Nachziehstapel: " + deckSize;
        int lw = g2.getFontMetrics().stringWidth(label);
        g2.drawString(label, (getWidth() - lw) / 2, top + CARD_H + 24);

        g2.dispose();
    }
}

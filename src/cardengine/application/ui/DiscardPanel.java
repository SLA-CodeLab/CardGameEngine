package cardengine.application.ui;

import cardengine.framework.core.Card;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Swing-Ansicht des Ablagestapels in der Tischmitte (fuer Mau-Mau). Zeigt die
 * oberste, offen liegende Karte samt Anzahl. Rein darstellend; die Daten kommen
 * ueber {@link #setTop(Card, int)}. Bei leerem Stapel wird ein leerer Platz
 * gezeichnet – so bleibt das Panel auch fuer Spiele ohne Ablage (Minigame)
 * unauffaellig.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class DiscardPanel extends JPanel {

    private static final int CARD_W = 62;
    private static final int CARD_H = 88;

    private Card topCard;
    private int count;

    public DiscardPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(160, CARD_H + 46));
    }

    /**
     * Setzt die oberste Ablagekarte und die Stapelgroesse und zeichnet neu.
     *
     * @param topCard oberste (offene) Karte oder {@code null} bei leerem Stapel
     * @param count   Anzahl Karten auf dem Ablagestapel
     */
    public void setTop(Card topCard, int count) {
        this.topCard = topCard;
        this.count = count;
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

        if (topCard == null) {
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawRoundRect(cx, top, CARD_W, CARD_H, 10, 10);
        } else {
            // Angedeutete Stapeltiefe: ein paar Kartenraender unter der obersten Karte.
            int layers = Math.min(3, Math.max(1, count));
            for (int i = layers - 1; i >= 1; i--) {
                CardRenderer.paintCard(g2, null, cx - i * 2, top + i * 2, CARD_W, CARD_H);
            }
            CardRenderer.paintCard(g2, topCard, cx, top, CARD_W, CARD_H);
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String label = "Ablagestapel: " + count;
        int lw = g2.getFontMetrics().stringWidth(label);
        g2.drawString(label, (getWidth() - lw) / 2, top + CARD_H + 24);

        g2.dispose();
    }
}

package cardengine.application.ui;

import cardengine.framework.core.Card;
import cardengine.framework.core.Player;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/**
 * Swing-Ansicht fuer die aufgefaechterte Hand eines Spielers.
 *
 * <p>Reine View-Komponente ohne Spiellogik: zeichnet Namen und Kartenanzahl sowie die
 * Karten als echte, leicht ueberlappende Blaetter (via {@link CardRenderer}). Der aktive
 * Spieler bekommt eine hervorgehobene, goldene Sitzflaeche. Aktualisiert wird die Ansicht
 * ausschliesslich ueber {@link #update(Player, boolean)}.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class PlayerPanel extends JPanel {

    private static final int CARD_W = 62;
    private static final int CARD_H = 88;
    private static final int HEADER_H = 26;
    private static final int PAD = 10;

    private static final Color SEAT_ACTIVE = new Color(255, 215, 0, 60);
    private static final Color SEAT_ACTIVE_BORDER = new Color(0xE0C067);
    private static final Color TEXT = Color.WHITE;

    private Player player;
    private boolean active;

    public PlayerPanel(Player player) {
        this.player = player;
        setOpaque(false);
        setPreferredSize(new Dimension(340, HEADER_H + CARD_H + 2 * PAD));
    }

    /**
     * Aktualisiert Spielerzustand und Hervorhebung und zeichnet neu.
     *
     * @param player   aktueller Spielerzustand
     * @param isActive true, wenn dieser Spieler gerade am Zug ist
     */
    public void update(Player player, boolean isActive) {
        this.player = player;
        this.active = isActive;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Hervorgehobene Sitzflaeche fuer den aktiven Spieler.
        if (active) {
            g2.setColor(SEAT_ACTIVE);
            g2.fillRoundRect(2, 2, w - 4, h - 4, 18, 18);
            g2.setColor(SEAT_ACTIVE_BORDER);
            g2.drawRoundRect(2, 2, w - 5, h - 5, 18, 18);
        }

        // Kopfzeile: Name + Kartenanzahl.
        List<Card> cards = player.getHand().getCards();
        g2.setColor(TEXT);
        g2.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 14));
        String header = (active ? "▶ " : "") + player.getName() + "  ·  " + cards.size() + " Karten";
        g2.drawString(header, PAD, PAD + 14);

        // Karten leicht ueberlappend auffaechern.
        int count = cards.size();
        int top = HEADER_H + PAD;
        if (count > 0) {
            int available = w - 2 * PAD - CARD_W;
            int step = count == 1 ? 0 : Math.min(CARD_W + 6, available / (count - 1));
            int totalWidth = CARD_W + step * (count - 1);
            int startX = (w - totalWidth) / 2;
            for (int i = 0; i < count; i++) {
                CardRenderer.paintCard(g2, cards.get(i), startX + i * step, top, CARD_W, CARD_H);
            }
        }
        g2.dispose();
    }
}

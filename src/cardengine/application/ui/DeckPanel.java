package cardengine.application.ui;

import cardengine.framework.core.Card;
import cardengine.framework.core.Suit;

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
 * darstellend; die Daten kommen ueber {@link #setDeckSize(int)} bzw.
 * {@link #setDeck(int, Card, Suit)}.</p>
 *
 * <p><b>Ergaenzung (Claude, Opus 4.8):</b> Fuer Durak liegt die Trumpfkarte offen und
 * quer <em>unter</em> dem Nachziehstapel und ragt seitlich heraus – vorher war sie
 * ueberhaupt nicht sichtbar, obwohl {@code DurakDeck.shuffle()} sie extra aufdeckt.
 * Sie wird als letzte Karte gezogen, bleibt also bis zum Schluss liegen.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class DeckPanel extends JPanel {

    private static final int CARD_W = 62;
    private static final int CARD_H = 88;
    /**
     * Wie weit die quer liegende Trumpfkarte unter dem Nachziehstapel steckt. Der Rest
     * schaut rechts heraus, damit man Rang und Farbe des Trumpfs jederzeit ablesen kann.
     */
    private static final int TRUMP_INSET = 20;
    /** Breite, die Stapel + herausragende Trumpfkarte zusammen belegen. */
    private static final int TRUMP_FOOTPRINT = TRUMP_INSET + CARD_H;

    private int deckSize;
    private Card trumpCard;
    private Suit trumpSuit;

    public DeckPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(200, CARD_H + 64));
    }

    /**
     * Setzt die anzuzeigende Restkartenzahl und zeichnet neu (Spiele ohne Trumpf).
     *
     * @param deckSize verbleibende Karten im Nachziehstapel
     */
    public void setDeckSize(int deckSize) {
        setDeck(deckSize, null, null);
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Setzt Restkartenzahl und die offen liegende Trumpfkarte.</p>
     *
     * @param deckSize  verbleibende Karten im Nachziehstapel
     * @param trumpCard quer liegende Trumpfkarte, oder {@code null} (Deck leer / kein Trumpf)
     * @param trumpSuit Trumpffarbe; bleibt auch dann gueltig, wenn die Karte schon gezogen wurde
     */
    public void setDeck(int deckSize, Card trumpCard, Suit trumpSuit) {
        this.deckSize = deckSize;
        this.trumpCard = trumpCard;
        this.trumpSuit = trumpSuit;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        boolean hasTrump = trumpSuit != null;
        // Ohne Trumpf bleibt der Stapel mittig; mit Trumpf rueckt er nach links,
        // damit die quer liegende Karte rechts herausschaut.
        int stackX = hasTrump
                ? (getWidth() - TRUMP_FOOTPRINT) / 2
                : (getWidth() - CARD_W) / 2;
        // Karte + zwei Textzeilen mittig im verfuegbaren Platz.
        int top = Math.max(6, (getHeight() - (CARD_H + 52)) / 2);

        // Die Trumpfkarte liegt UNTER dem Stapel -> zuerst zeichnen. Sie steckt nur mit
        // TRUMP_INSET unter dem Stapel, der Rest bleibt sichtbar.
        if (hasTrump && trumpCard != null) {
            int cx = stackX + TRUMP_INSET + CARD_H / 2;
            int cy = top + CARD_H / 2;
            // Hier wird das CardVisibility-Feld wirklich ausgewertet: DurakDeck.shuffle()
            // deckt genau diese eine Karte per flip() auf, alle anderen bleiben HIDDEN.
            CardRenderer.paintCardRotated(g2, trumpCard, CardRenderer.isFaceUp(trumpCard),
                    cx, cy, CARD_W, CARD_H);
        }

        if (deckSize <= 0) {
            // Leerer Stapelplatz.
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawRoundRect(stackX, top, CARD_W, CARD_H, 10, 10);
        } else {
            // Ein paar versetzte Rueckseiten fuer die Stapeltiefe.
            int layers = Math.min(4, deckSize);
            for (int i = layers - 1; i >= 0; i--) {
                CardRenderer.paintCard(g2, null, false, stackX + i * 2, top + i * 2, CARD_W, CARD_H);
            }
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String label = "Nachziehstapel: " + deckSize;
        int lw = g2.getFontMetrics().stringWidth(label);
        g2.drawString(label, (getWidth() - lw) / 2, top + CARD_H + 22);

        if (hasTrump) {
            // Trumpffarbe zusaetzlich als Text - bleibt lesbar, wenn die Karte gezogen wurde.
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            String trumpLabel = "Trumpf: " + CardRenderer.symbolOf(trumpSuit);
            int tw = g2.getFontMetrics().stringWidth(trumpLabel);
            int tx = (getWidth() - tw) / 2;
            int ty = top + CARD_H + 40;
            g2.setColor(Color.WHITE);
            g2.drawString("Trumpf: ", tx, ty);
            int prefix = g2.getFontMetrics().stringWidth("Trumpf: ");
            // Symbol in Kartenfarbe, damit Herz/Karo rot erscheinen.
            g2.setColor(suitOnFelt(trumpSuit));
            g2.drawString(CardRenderer.symbolOf(trumpSuit), tx + prefix, ty);
        }

        g2.dispose();
    }

    /**
     * Schwarze Symbole waeren auf dem gruenen Filz kaum lesbar, deshalb wird Pik/Kreuz
     * hier hell statt schwarz gezeichnet.
     */
    private Color suitOnFelt(Suit suit) {
        Color c = CardRenderer.colorOf(suit);
        return c.equals(new Color(0x212121)) ? Color.WHITE : new Color(0xFF6B6B);
    }
}

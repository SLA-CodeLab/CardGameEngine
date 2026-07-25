package cardengine.application.ui;

import cardengine.framework.core.Card;
import cardengine.framework.core.Player;
import cardengine.framework.core.Suit;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Swing-Ansicht fuer die aufgefaecherte Hand eines Spielers.
 *
 * <p>Reine View-Komponente ohne Spiellogik: zeichnet Namen, Rolle und Kartenanzahl sowie
 * die Karten als echte, leicht ueberlappende Blaetter (via {@link CardRenderer}). Der
 * aktive Spieler bekommt eine hervorgehobene, goldene Sitzflaeche.</p>
 *
 * <p><b>Ergaenzungen (Claude, Opus 4.8):</b></p>
 * <ul>
 *   <li><b>Perspektive:</b> Nur die eigene Hand liegt offen; Mitspieler/Bots werden
 *       verdeckt gezeichnet ({@link #setFaceUp(boolean)}). Vorher konnte man die Karten
 *       der Bots mitlesen.</li>
 *   <li><b>Sortierung:</b> Die Hand wird nach Farbe und Rang sortiert angezeigt, Truempfe
 *       stehen geschlossen am rechten Ende ({@link #setTrumpSuit(Suit)}).</li>
 *   <li><b>Highlighting:</b> Karten, die gerade wirklich gelegt werden duerfen, leuchten
 *       gruen; der Rest wird abgedunkelt ({@link #setPlayableCards(Set)}).</li>
 *   <li><b>Sitzplatz:</b> Seitliche Plaetze faechern senkrecht auf, damit die Spieler im
 *       Kreis um den Tisch sitzen koennen ({@link Seat}).</li>
 * </ul>
 *
 * @author Claude (Opus 4.8)
 */
public class PlayerPanel extends JPanel {

    /** Sitzposition am Tisch – bestimmt, wie die Hand aufgefaechert wird. */
    public enum Seat {
        BOTTOM, TOP, LEFT, RIGHT;

        boolean isVertical() {
            return this == LEFT || this == RIGHT;
        }
    }

    private static final int CARD_W = 62;
    private static final int CARD_H = 88;
    private static final int HEADER_H = 26;
    private static final int PAD = 10;

    private static final Color SEAT_ACTIVE = new Color(255, 215, 0, 60);
    private static final Color SEAT_ACTIVE_BORDER = new Color(0xE0C067);
    private static final Color TEXT = Color.WHITE;
    private static final Color ROLE_TEXT = new Color(0xC8E6C9);

    private Player player;
    private boolean active;
    private Seat seat = Seat.BOTTOM;

    /** true = eigene Hand (offen), false = fremde Hand (verdeckt). */
    private boolean faceUp = true;

    /** Trumpffarbe fuer die Sortierung; {@code null} bei Spielen ohne Trumpf. */
    private Suit trumpSuit;

    /** Karten, die jetzt gelegt werden duerfen; {@code null} = kein Highlighting. */
    private Set<Card> playableCards;

    /** Rolle im aktuellen Zug (z.&nbsp;B. "Angreifer"), oder {@code null}. */
    private String roleLabel;

    /**
     * Anzeige-Reihenfolge der Hand. Wird bei jedem {@link #update(Player, boolean)} neu
     * gebaut und sowohl zum Zeichnen als auch fuer die Klickzuordnung benutzt, damit
     * beides garantiert dieselbe Reihenfolge sieht.
     */
    private List<Card> displayCards = new ArrayList<>();

    /** Wird mit der angeklickten Karte aufgerufen – aber nur, wenn dieser Spieler am Zug ist. */
    private Consumer<Card> cardClickListener;

    public PlayerPanel(Player player) {
        this.player = player;
        setOpaque(false);
        applyPreferredSize();

        // GENERIERT von Claude (Opus 4.8): Klick auf eine Handkarte des aktiven
        // Spielers an den Controller melden (Grundlage fuer "Karte spielen").
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!active || cardClickListener == null || !faceUp) {
                    return;
                }
                int idx = cardIndexAt(e.getX(), e.getY());
                if (idx >= 0) {
                    cardClickListener.accept(displayCards.get(idx));
                }
            }
        });
    }

    /**
     * GENERIERT von Claude (Opus 4.8).
     *
     * <p>Registriert den Handler, der beim Klick auf eine Karte des aktiven Spielers
     * mit genau dieser Karte aufgerufen wird.</p>
     *
     * @param listener Empfaenger der angeklickten Karte
     */
    public void setCardClickListener(Consumer<Card> listener) {
        this.cardClickListener = listener;
    }

    /** Legt den Sitzplatz fest (steuert Auffaecherung und bevorzugte Groesse). */
    public void setSeat(Seat seat) {
        this.seat = seat;
        applyPreferredSize();
        revalidate();
        repaint();
    }

    /** true = Hand offen zeigen (eigener Spieler), false = verdeckt (Mitspieler/Bot). */
    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
        repaint();
    }

    /** Trumpffarbe fuer die Sortierung der Hand; {@code null} bei Spielen ohne Trumpf. */
    public void setTrumpSuit(Suit trumpSuit) {
        this.trumpSuit = trumpSuit;
        repaint();
    }

    /**
     * Setzt die Karten, die gerade regelkonform gelegt werden koennen.
     *
     * @param playableCards legbare Karten, oder {@code null} fuer "kein Highlighting"
     */
    public void setPlayableCards(Set<Card> playableCards) {
        this.playableCards = playableCards;
        repaint();
    }

    /** Rolle im aktuellen Zug, z.&nbsp;B. "Angreifer" / "Verteidiger" / "Zuleger". */
    public void setRoleLabel(String roleLabel) {
        this.roleLabel = roleLabel;
        repaint();
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
        this.displayCards = sortedHand();
        repaint();
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Sortiert eine <em>Kopie</em> der Hand fuer die Anzeige: erst die normalen Farben
     * (nach Farbe, dann Rang), danach die Truempfe. Das Modell bleibt unangetastet – die
     * Reihenfolge in {@code Hand} ist Spiellogik und geht die View nichts an.</p>
     */
    private List<Card> sortedHand() {
        List<Card> out = new ArrayList<>(player.getHand().getCards());
        out.sort(Comparator
                .comparingInt((Card c) -> isTrump(c) ? 1 : 0)
                .thenComparingInt(c -> c.getSuit().ordinal())
                .thenComparingInt(c -> c.getRank().ordinal()));
        return out;
    }

    private boolean isTrump(Card card) {
        return trumpSuit != null && card.getSuit() == trumpSuit;
    }

    /** Seitliche Plaetze brauchen eine zweite Kopfzeile fuer die Rolle. */
    private int headerHeight() {
        return seat.isVertical() ? HEADER_H + 16 : HEADER_H;
    }

    private void applyPreferredSize() {
        if (seat.isVertical()) {
            setPreferredSize(new Dimension(CARD_W + 2 * PAD + 40, headerHeight() + CARD_H + 2 * PAD + 110));
        } else {
            setPreferredSize(new Dimension(360, headerHeight() + CARD_H + 2 * PAD));
        }
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

        // Kopfzeile: Name + Kartenanzahl (+ Rolle).
        Font nameFont = new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 14);
        g2.setColor(TEXT);
        g2.setFont(nameFont);
        String header = (active ? "▶ " : "") + player.getName() + "  ·  " + displayCards.size();
        g2.drawString(header, PAD, PAD + 14);

        if (roleLabel != null && !roleLabel.isEmpty()) {
            g2.setColor(ROLE_TEXT);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            int nameWidth = g2.getFontMetrics(nameFont).stringWidth(header);
            // Seitliche Plaetze sind schmal -> Rolle in eine zweite Zeile, sonst wird sie
            // abgeschnitten ("gre..." statt "greift an").
            if (seat.isVertical() || PAD + nameWidth + 8 + g2.getFontMetrics().stringWidth(roleLabel) > getWidth()) {
                g2.drawString(roleLabel, PAD, PAD + 28);
            } else {
                g2.drawString(roleLabel, PAD + nameWidth + 8, PAD + 14);
            }
        }

        paintCards(g2, w, h);
        g2.dispose();
    }

    private void paintCards(Graphics2D g2, int w, int h) {
        int count = displayCards.size();
        if (count == 0) {
            return;
        }
        int step = step(count, w, h);
        int startX = startX(count, w, step);
        int startY = startY(count, h, step);

        for (int i = 0; i < count; i++) {
            Card card = displayCards.get(i);
            int x = seat.isVertical() ? startX : startX + i * step;
            int y = seat.isVertical() ? startY + i * step : startY;

            CardRenderer.paintCard(g2, card, faceUp, x, y, CARD_W, CARD_H);

            // Highlighting nur fuer die eigene, offene Hand und nur wenn man am Zug ist.
            if (faceUp && active && playableCards != null) {
                if (playableCards.contains(card)) {
                    CardRenderer.paintPlayableGlow(g2, x, y, CARD_W, CARD_H);
                } else {
                    CardRenderer.paintDimmed(g2, x, y, CARD_W, CARD_H);
                }
            }
        }
    }

    /** Ueberlappungsschritt zwischen zwei Karten – abhaengig von Sitz und Platz. */
    private int step(int count, int w, int h) {
        if (count <= 1) {
            return 0;
        }
        if (seat.isVertical()) {
            int available = h - headerHeight() - 2 * PAD - CARD_H;
            return Math.max(10, Math.min(CARD_H / 3, available / (count - 1)));
        }
        int available = w - 2 * PAD - CARD_W;
        return Math.max(10, Math.min(CARD_W + 6, available / (count - 1)));
    }

    private int startX(int count, int w, int step) {
        if (seat.isVertical()) {
            return (w - CARD_W) / 2;
        }
        return (w - (CARD_W + step * (count - 1))) / 2;
    }

    private int startY(int count, int h, int step) {
        if (!seat.isVertical()) {
            return headerHeight() + PAD;
        }
        int totalH = CARD_H + step * (count - 1);
        return Math.max(headerHeight() + PAD, (h - totalH) / 2 + headerHeight() / 2);
    }

    /**
     * GENERIERT von Claude (Opus 4.8).
     *
     * <p>Bildet einen Mausklick auf einen Index der <em>Anzeigereihenfolge</em> ab.
     * Verwendet exakt dieselbe Geometrie wie {@link #paintCards(Graphics2D, int, int)}.
     * Da sich die Karten ueberlappen, gewinnt die zuletzt gezeichnete (oberste) Karte –
     * deshalb wird von hinten nach vorne geprueft.</p>
     *
     * @return Index in {@link #displayCards} oder {@code -1}
     */
    private int cardIndexAt(int px, int py) {
        int count = displayCards.size();
        if (count == 0) {
            return -1;
        }
        int w = getWidth();
        int h = getHeight();
        int step = step(count, w, h);
        int startX = startX(count, w, step);
        int startY = startY(count, h, step);

        for (int i = count - 1; i >= 0; i--) {
            int x = seat.isVertical() ? startX : startX + i * step;
            int y = seat.isVertical() ? startY + i * step : startY;
            if (px >= x && px <= x + CARD_W && py >= y && py <= y + CARD_H) {
                return i;
            }
        }
        return -1;
    }
}

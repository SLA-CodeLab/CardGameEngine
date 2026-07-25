package cardengine.application.ui;

import cardengine.framework.core.Card;
import cardengine.framework.core.CardVisibility;
import cardengine.framework.core.Rank;
import cardengine.framework.core.Suit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Zeichenhilfe (Java2D) fuer einzelne Kartenblaetter.
 *
 * <p>Rendert eine {@link Card} als klassisches Blatt: weisser, abgerundeter Rahmen,
 * Rang und Farbsymbol in den Ecken sowie ein grosses Symbol in der Mitte – Herz/Karo
 * rot, Pik/Kreuz schwarz. {@code null} wird als verdeckte Rueckseite gezeichnet.
 * Die {@link Card} wird ueber {@link Suit}/{@link Rank} in Symbole uebersetzt, sodass
 * keine Karten-ID mehr noetig ist (z.&nbsp;B. ♠4).</p>
 *
 * @author Claude (Opus 4.8)
 */
public final class CardRenderer {

    private static final Color FACE = Color.WHITE;
    private static final Color BORDER = new Color(0x9E9E9E);
    private static final Color RED = new Color(0xC62828);
    private static final Color BLACK = new Color(0x212121);
    private static final Color BACK_FILL = new Color(0x6B1414);
    private static final Color BACK_TRIM = new Color(0xE0C067);

    /** Rahmen fuer eine Karte, die der Spieler jetzt legen darf. */
    private static final Color PLAYABLE = new Color(0x66FF88);
    /** Rahmen fuer die offene, noch zu schlagende Angriffskarte. */
    private static final Color OPEN_ATTACK = new Color(0xFF6B6B);
    /** Schleier ueber Karten, die gerade nicht gelegt werden duerfen. */
    private static final Color DIM = new Color(0, 0, 0, 105);

    private CardRenderer() {
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Wertet das bislang ungenutzte {@link CardVisibility} aus: Nur eine als
     * {@code VISIBLE} markierte Karte wird offen gezeichnet. Damit steuert das
     * Framework-Feld tatsaechlich die Darstellung, statt nur mitgeschrieben zu werden.</p>
     *
     * @param card zu pruefende Karte
     * @return true, wenn die Karte offen liegt
     */
    public static boolean isFaceUp(Card card) {
        return card != null && card.getVisibility() == CardVisibility.VISIBLE;
    }

    /**
     * Zeichnet eine Karte in den angegebenen Bereich – wahlweise offen oder verdeckt.
     * Ueber {@code faceUp} bestimmt der Aufrufer die Sicht-Perspektive (fremde Hand =
     * verdeckt), ohne dafuer {@code null} uebergeben zu muessen.
     *
     * @param g      Zeichenkontext
     * @param card   Karte, oder {@code null} fuer eine verdeckte Rueckseite
     * @param faceUp true zeichnet die Vorderseite, false die Rueckseite
     * @param x      linke Kante
     * @param y      obere Kante
     * @param w      Breite
     * @param h      Hoehe
     */
    public static void paintCard(Graphics2D g, Card card, boolean faceUp, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int arc = Math.max(8, w / 6);
        if (card == null || !faceUp) {
            paintBack(g2, x, y, w, h, arc);
        } else {
            paintFace(g2, card.getSuit(), card.getRank(), x, y, w, h, arc);
        }
        g2.dispose();
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Zeichnet eine Karte um 90 Grad gedreht – gebraucht fuer die Trumpfkarte, die in
     * Durak quer unter dem Nachziehstapel liegt.</p>
     *
     * @param cx Mittelpunkt x der gedrehten Karte
     * @param cy Mittelpunkt y der gedrehten Karte
     * @param w  Breite der Karte vor der Drehung
     * @param h  Hoehe der Karte vor der Drehung
     */
    public static void paintCardRotated(Graphics2D g, Card card, boolean faceUp, int cx, int cy, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.rotate(Math.PI / 2, cx, cy);
        paintCard(g2, card, faceUp, cx - w / 2, cy - h / 2, w, h);
        g2.dispose();
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Legt einen gruenen Leuchtrahmen um eine Karte, die der Spieler jetzt legen darf.
     * Welche Karten das sind, entscheidet nicht die View, sondern die aktuelle
     * {@code Phase} ueber {@code isValid} – der Controller reicht das Ergebnis nur durch.</p>
     */
    public static void paintPlayableGlow(Graphics2D g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = Math.max(8, w / 6);
        g2.setColor(new Color(PLAYABLE.getRed(), PLAYABLE.getGreen(), PLAYABLE.getBlue(), 70));
        g2.setStroke(new BasicStroke(6f));
        g2.drawRoundRect(x - 2, y - 2, w + 3, h + 3, arc, arc);
        g2.setColor(PLAYABLE);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(x - 1, y - 1, w + 1, h + 1, arc, arc);
        g2.dispose();
    }

    /** Roter Rahmen fuer die offene Angriffskarte, die noch geschlagen werden muss. */
    public static void paintOpenAttackMarker(Graphics2D g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = Math.max(8, w / 6);
        g2.setColor(OPEN_ATTACK);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(x - 2, y - 2, w + 3, h + 3, arc, arc);
        g2.dispose();
    }

    /** Dunkler Schleier fuer Karten, die gerade nicht gelegt werden duerfen. */
    public static void paintDimmed(Graphics2D g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = Math.max(8, w / 6);
        g2.setColor(DIM);
        g2.fillRoundRect(x, y, w, h, arc, arc);
        g2.dispose();
    }

    private static void paintFace(Graphics2D g, Suit suit, Rank rank, int x, int y, int w, int h, int arc) {
        drawCardBase(g, x, y, w, h, arc);

        String rankText = rankLabel(rank);
        String suitText = suitSymbol(suit);
        g.setColor(suitColor(suit));

        // Ecke oben links.
        Font cornerFont = new Font("SansSerif", Font.BOLD, Math.round(h * 0.20f));
        drawCorner(g, rankText, suitText, cornerFont, x, y, w, h, false);
        // Ecke unten rechts (um 180° gedreht gespiegelt).
        drawCorner(g, rankText, suitText, cornerFont, x, y, w, h, true);

        // Grosses Symbol in der Mitte.
        Font centerFont = new Font("SansSerif", Font.BOLD, Math.round(h * 0.42f));
        g.setFont(centerFont);
        FontMetrics fm = g.getFontMetrics();
        int sx = x + (w - fm.stringWidth(suitText)) / 2;
        int sy = y + (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(suitText, sx, sy);
    }

    private static void drawCorner(Graphics2D g, String rankText, String suitText, Font font,
                                   int x, int y, int w, int h, boolean mirrored) {
        Graphics2D g2 = (Graphics2D) g.create();
        if (mirrored) {
            g2.rotate(Math.PI, x + w / 2.0, y + h / 2.0);
        }
        g2.setFont(font);
        int pad = Math.max(4, w / 12);
        int line = font.getSize();
        g2.drawString(rankText, x + pad, y + pad + line);
        g2.drawString(suitText, x + pad, y + pad + 2 * line);
        g2.dispose();
    }

    private static void drawCardBase(Graphics2D g, int x, int y, int w, int h, int arc) {
        g.setColor(FACE);
        g.fillRoundRect(x, y, w, h, arc, arc);
        g.setColor(BORDER);
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
    }

    private static void paintBack(Graphics2D g, int x, int y, int w, int h, int arc) {
        g.setColor(BACK_FILL);
        g.fillRoundRect(x, y, w, h, arc, arc);
        g.setColor(BACK_TRIM);
        g.setStroke(new BasicStroke(2f));
        int inset = Math.max(4, w / 10);
        g.drawRoundRect(x + inset, y + inset, w - 2 * inset - 1, h - 2 * inset - 1, arc / 2, arc / 2);
        // Kleine Raute in der Mitte als Muster.
        int cx = x + w / 2;
        int cy = y + h / 2;
        int d = Math.min(w, h) / 6;
        int[] px = {cx, cx + d, cx, cx - d};
        int[] py = {cy - d, cy, cy + d, cy};
        g.drawPolygon(px, py, 4);
    }

    /** @return kompakte Bezeichnung wie {@code ♠4} (Farbe + Rang). */
    public static String shortLabel(Card card) {
        return card != null ? suitSymbol(card.getSuit()) + rankLabel(card.getRank()) : "??";
    }

    /** @return das Symbol einer Farbe, z.&nbsp;B. fuer die Trumpfanzeige. */
    public static String symbolOf(Suit suit) {
        return suit != null ? suitSymbol(suit) : "?";
    }

    /** @return die Anzeigefarbe einer Kartenfarbe (Herz/Karo rot, sonst schwarz). */
    public static Color colorOf(Suit suit) {
        return suit != null ? suitColor(suit) : BLACK;
    }

    private static String rankLabel(Rank rank) {
        return switch (rank) {
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
            case TEN -> "10";
            case JACK -> "J";
            case QUEEN -> "Q";
            case KING -> "K";
            case ACE -> "A";
        };
    }

    private static String suitSymbol(Suit suit) {
        return switch (suit) {
            case HEARTS -> "♥";   // ♥
            case DIAMONDS -> "♦"; // ♦
            case CLUBS -> "♣";    // ♣
            case SPADES -> "♠";   // ♠
        };
    }

    private static Color suitColor(Suit suit) {
        return (suit == Suit.HEARTS || suit == Suit.DIAMONDS) ? RED : BLACK;
    }
}

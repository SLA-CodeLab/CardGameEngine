package cardengine.showcase.durak.ui;

import cardengine.application.ui.CardRenderer;
import cardengine.application.ui.TablePanel;
import cardengine.framework.core.Card;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * ERGAENZUNG von Claude (Opus 4.8).
 *
 * <p>Durak-Tisch: zeigt jedes Angriffs-/Verteidigungspaar <b>einzeln und nebeneinander</b>.
 * Vorher landete alles auf einem einzigen Ablagestapel, auf dem nur die oberste Karte zu
 * sehen war – man konnte weder erkennen, was schon geschlagen wurde, noch welchen Rang
 * man zulegen darf.</p>
 *
 * <p>Die Verteidigungskarte liegt leicht versetzt <em>ueber</em> ihrer Angriffskarte, so
 * wie man sie auch auf einem echten Tisch quer darueber legt. Die noch offene (also noch
 * nicht geschlagene) Angriffskarte bekommt einen roten Rahmen.</p>
 *
 * <p><b>Zuordnung der Paare:</b> {@code Table} ist im Framework nur eine flache Liste.
 * Ein Paar wird deshalb ueber die Position abgeleitet – gerader Index = Angriff,
 * ungerader Index = zugehoerige Verteidigung. Das ist genau dieselbe Annahme, die auch
 * {@code AttackPhase.allDefended()} trifft ("es gibt immer nur eine offene Angriffskarte").</p>
 *
 * <p>//todo (Framework/Durak, ausserhalb application): {@code Table} kennt keine Paare.
 * Sauberer waere eine Struktur wie {@code List<Attack{card, beatenBy}>} bzw. das im
 * Klassendiagramm vorgesehene {@code tableAttack}/{@code tableDefense}. Erst damit
 * liessen sich mehrere gleichzeitig offene Angriffskarten (echtes Durak mit Zulegern)
 * darstellen. Bis dahin gilt die Paritaets-Annahme.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class DurakTablePanel extends TablePanel {

    /** Versatz der Verteidigungskarte gegenueber ihrer Angriffskarte. */
    private static final int DEF_DX = 20;
    private static final int DEF_DY = 26;
    private static final int GAP = 16;
    /** Platz oberhalb der Karten fuer die Kopfzeile. */
    private static final int HEADER_SPACE = 24;

    private final List<Card> cards = new ArrayList<>();

    public DurakTablePanel() {
        setPreferredSize(new Dimension(520, CARD_H + DEF_DY + HEADER_SPACE + 26));
    }

    @Override
    public void setCards(List<Card> cards) {
        this.cards.clear();
        if (cards != null) {
            this.cards.addAll(cards);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Kartenblock mittig im verfuegbaren Platz, damit der Tisch nicht am oberen
        // Rand klebt, wenn das Fenster hoch ist.
        int blockH = CARD_H + DEF_DY;
        int top = Math.max(HEADER_SPACE, (getHeight() - blockH) / 2);

        g2.setColor(new Color(255, 255, 255, 190));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));

        if (cards.isEmpty()) {
            String hint = "Tisch ist leer – der Angreifer legt vor.";
            int hw = g2.getFontMetrics().stringWidth(hint);
            g2.drawString(hint, (getWidth() - hw) / 2, top + CARD_H / 2);
            g2.dispose();
            return;
        }

        // Anzahl Paare: jede Angriffskarte (gerader Index) eroeffnet ein Paar.
        int pairs = (cards.size() + 1) / 2;
        int pairW = CARD_W + DEF_DX;
        int step = pairW + GAP;
        int available = getWidth() - 2 * GAP;
        if (pairs > 1 && pairs * pairW + (pairs - 1) * GAP > available) {
            // Bei vielen Paaren enger zusammenruecken, statt aus dem Panel zu laufen.
            step = Math.max(CARD_W / 2 + 6, (available - pairW) / (pairs - 1));
        }
        int totalW = pairW + step * (pairs - 1);
        int startX = Math.max(GAP, (getWidth() - totalW) / 2);

        for (int i = 0; i < pairs; i++) {
            int x = startX + i * step;
            Card attack = cards.get(2 * i);
            Card defense = (2 * i + 1) < cards.size() ? cards.get(2 * i + 1) : null;

            // Karten auf dem Tisch liegen per Definition offen (siehe Glossar: "Table
            // bezeichnet alle fuer die Spieler sichtbaren Karten").
            CardRenderer.paintCard(g2, attack, true, x, top, CARD_W, CARD_H);
            if (defense != null) {
                CardRenderer.paintCard(g2, defense, true, x + DEF_DX, top + DEF_DY, CARD_W, CARD_H);
            } else {
                // Noch nicht geschlagen -> hervorheben.
                CardRenderer.paintOpenAttackMarker(g2, x, top, CARD_W, CARD_H);
            }
        }

        // Kopfzeile: was liegt an, was ist noch zu schlagen.
        boolean allBeaten = cards.size() % 2 == 0;
        String header = allBeaten
                ? (pairs == 1 ? "Angriff geschlagen" : "Alle " + pairs + " Angriffe geschlagen")
                : "Zu schlagen: " + CardRenderer.shortLabel(cards.get(cards.size() - 1));
        g2.setColor(allBeaten ? new Color(0xC8E6C9) : new Color(0xFF9E9E));
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        int hw = g2.getFontMetrics().stringWidth(header);
        g2.drawString(header, (getWidth() - hw) / 2, top - 8);

        g2.dispose();
    }
}

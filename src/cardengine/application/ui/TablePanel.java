package cardengine.application.ui;

import cardengine.framework.core.Card;

import javax.swing.JPanel;
import java.util.List;

/**
 * ERGAENZUNG von Claude (Opus 4.8).
 *
 * <p>Gemeinsame Oberklasse fuer die Darstellung des Tisches. Jedes Spiel legt Karten
 * anders in die Mitte, deshalb bekommt die {@link GameView} die passende Ansicht
 * hineingereicht statt sie fest zu verdrahten:</p>
 * <ul>
 *   <li>{@link DiscardPanel} – ein Ablagestapel mit oberster Karte (Mau-Mau).</li>
 *   <li>{@link DurakTablePanel} – Angriffs-/Verteidigungspaare nebeneinander (Durak).</li>
 * </ul>
 *
 * <p>Die Ansicht bekommt nur die Kartenliste des {@code Table} und kennt keine Regeln.</p>
 *
 * @author Claude (Opus 4.8)
 */
public abstract class TablePanel extends JPanel {

    protected static final int CARD_W = 62;
    protected static final int CARD_H = 88;

    protected TablePanel() {
        setOpaque(false);
    }

    /**
     * Uebernimmt den aktuellen Tischinhalt und zeichnet neu.
     *
     * @param cards Karten auf dem Tisch, in Ablagereihenfolge
     */
    public abstract void setCards(List<Card> cards);
}

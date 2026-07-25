package cardengine.showcase.maumau.strategy.effect;

import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.core.Suit;
import cardengine.framework.strategy.Effect;
import cardengine.showcase.maumau.state.PlayPhase;

import java.util.List;

/**
 * GENERIERT von Claude (Fable 5).
 *
 * <p>Karteneffekt (Strategy-Pattern) des <b>Buben</b>: der Spieler, der ihn legt,
 * bestimmt die Farbe, auf die als naechstes reagiert werden muss. Der Wunsch wird
 * in der {@link PlayPhase} hinterlegt, die ihn bei der Zugpruefung beruecksichtigt.</p>
 *
 * <p><b>Woher kommt die gewaehlte Farbe?</b> Ein {@code Effect} kennt nur das
 * {@link Game} und kann selbst keinen Dialog oeffnen (und soll es als Modell-Code
 * auch nicht). Deshalb zwei Wege:</p>
 * <ul>
 *   <li><b>Mensch:</b> Der Controller fragt die Farbe vor dem Einreichen des
 *       Commands per Dialog ab und setzt sie ueber {@link #setChosenSuit(Suit)}.</li>
 *   <li><b>Bot (Fallback):</b> Wurde keine Farbe gesetzt, waehlt der Effekt die
 *       Farbe, von der der Spieler nach dem Ablegen die meisten Karten haelt –
 *       eine sinnvolle Heuristik, mit der die Bots ohne eigenen Zusatzcode
 *       auskommen.</li>
 * </ul>
 *
 * <p>Nach dem Anwenden wird die gesetzte Farbe zurueckgesetzt: der Wunsch gilt
 * pro Ausspielen, nicht dauerhaft fuer die Karte.</p>
 *
 * @author Claude (Fable 5)
 */
public class ChooseSuitEffect implements Effect {

    /** Vom Controller (Mensch) vorab gewaehlte Farbe; {@code null} = Heuristik nutzen. */
    private Suit chosenSuit;

    /**
     * Legt die gewuenschte Farbe fuer das naechste Ausspielen dieses Buben fest.
     *
     * @param suit Wunschfarbe oder {@code null}, um die Heuristik zu verwenden
     */
    public void setChosenSuit(Suit suit) {
        this.chosenSuit = suit;
    }

    @Override
    public void apply(Game game) {
        if (!(game.getCurrentPhase() instanceof PlayPhase phase)) {
            return;
        }

        // Beim Anwenden liegt der gerade gelegte Bube bereits oben auf dem Ablagestapel.
        List<Card> pile = game.getTable().getCards();
        if (pile.isEmpty()) {
            return;
        }
        Card jack = pile.get(pile.size() - 1);

        Suit wish = (chosenSuit != null)
                ? chosenSuit
                : mostFrequentSuit(game.getActivePlayer(), jack);
        phase.setSuitWish(wish, jack);

        chosenSuit = null; // verbraucht – beim naechsten Ausspielen wird neu gewaehlt
    }

    /**
     * Heuristik fuer Bots: die Farbe, von der der Spieler die meisten Handkarten
     * haelt. War der Bube die letzte Karte, ist die Wahl egal (das Spiel ist mit
     * leerer Hand gewonnen) – dann zaehlt einfach die Farbe des Buben.
     */
    private Suit mostFrequentSuit(Player player, Card jack) {
        if (player == null || player.getHand().isEmpty()) {
            return jack.getSuit();
        }
        Suit best = jack.getSuit();
        int bestCount = -1;
        for (Suit suit : Suit.values()) {
            int count = 0;
            for (Card card : player.getHand().getCards()) {
                if (card.getSuit() == suit) {
                    count++;
                }
            }
            if (count > bestCount) {
                bestCount = count;
                best = suit;
            }
        }
        return best;
    }
}

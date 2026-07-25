package cardengine.showcase.maumau.state;

import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.core.Suit;
import cardengine.framework.state.Phase;
import cardengine.showcase.maumau.command.DrawCardCommand;
import cardengine.showcase.maumau.command.PlayCardCommand;

import java.util.List;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Einzige Phase des Mau-Mau (State-Pattern). Mau-Mau kennt keinen echten
 * Phasenwechsel wie Durak: Es gibt nur "der Aktive ist am Zug". Deshalb bleibt
 * diese Phase aktiv und {@link #next(Game)} schaltet lediglich reihum den naechsten
 * Spieler weiter.</p>
 *
 * <p>Die gesamte Regellogik steckt in {@link #isValid(Game, Command)}:</p>
 * <ul>
 *   <li>Ein {@link PlayCardCommand} ist gueltig, wenn er vom aktiven Spieler kommt,
 *       die Karte wirklich auf dessen Hand liegt und sie zur obersten Ablagekarte
 *       passt (gleiche Farbe oder gleicher Rang).</li>
 *   <li>Ein {@link DrawCardCommand} ist gueltig, wenn er vom aktiven Spieler kommt
 *       und der Nachziehstapel nicht leer ist.</li>
 * </ul>
 *
 * <p>Die Karteneffekte (7 zieht zwei, 8 aussetzen, Bube waehlt Farbe) stecken
 * <b>nicht</b> in dieser Phase, sondern als {@code Effect} in den {@code EffectCard}s
 * des {@code MauMauDeck}; angewendet werden sie beim Ausspielen im
 * {@code PlayCardCommand}. Nur der <b>Farbwunsch des Buben</b> beruehrt die Phase,
 * weil er die Ablegeregel aendert: Ist ein Wunsch aktiv, zaehlt statt der obersten
 * Karte allein die gewuenschte Farbe (ERGAENZUNG von Claude, Fable 5).</p>
 *
 * <p>Der Wunsch gilt dabei genau so lange, wie der zugehoerige Bube oben auf dem
 * Ablagestapel liegt. Das wird bei jeder Pruefung aus dem Spielzustand abgeleitet
 * statt irgendwo aufgeraeumt – dadurch erlischt der Wunsch von selbst, sobald eine
 * Karte darauf gelegt wird, und auch ein Undo des Buben macht ihn automatisch
 * wirkungslos.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class PlayPhase implements Phase {

    /** Nach einem Buben gewuenschte Farbe; {@code null} = kein Wunsch gesetzt. */
    private Suit wishedSuit;

    /** Der Bube, zu dem der Wunsch gehoert – der Wunsch gilt nur, solange er oben liegt. */
    private Card wishCard;

    /**
     * ERGAENZUNG von Claude (Fable 5).
     *
     * <p>Hinterlegt einen Farbwunsch (wird vom {@code ChooseSuitEffect} des Buben
     * aufgerufen). Die Phase eignet sich als Ablageort, weil dieselbe Instanz das
     * ganze Spiel ueber aktiv bleibt ({@link #next(Game)} liefert {@code this}).</p>
     *
     * @param suit gewuenschte Farbe
     * @param jack der soeben gelegte Bube (oberste Karte des Ablagestapels)
     */
    public void setSuitWish(Suit suit, Card jack) {
        this.wishedSuit = suit;
        this.wishCard = jack;
    }

    /**
     * ERGAENZUNG von Claude (Fable 5).
     *
     * @param game aktuelles Spiel
     * @return die aktuell geltende Wunschfarbe oder {@code null}, wenn kein Wunsch
     *         (mehr) aktiv ist, weil der zugehoerige Bube nicht mehr oben liegt
     */
    public Suit getActiveSuitWish(Game game) {
        if (wishedSuit == null || wishCard == null) {
            return null;
        }
        return topOfDiscard(game) == wishCard ? wishedSuit : null;
    }

    @Override
    public boolean isValid(Game game, Command cmd) {
        Player active = game.getActivePlayer();
        if (active == null || cmd == null) {
            return false;
        }

        if (cmd instanceof PlayCardCommand play) {
            if (play.getPlayer() != active) {
                return false;
            }
            Card card = play.getCard();
            if (!active.getHand().getCards().contains(card)) {
                return false;
            }
            // Aktiver Farbwunsch (Bube): dann zaehlt nur die gewuenschte Farbe.
            Suit wish = getActiveSuitWish(game);
            if (wish != null) {
                return card.getSuit() == wish;
            }
            return matches(card, topOfDiscard(game));
        }

        if (cmd instanceof DrawCardCommand draw) {
            if (draw.getPlayer() != active) {
                return false;
            }
            return game.getDeck() != null && !game.getDeck().isEmpty();
        }

        return false;
    }

    @Override
    public Phase next(Game game) {
        List<Player> players = game.getPlayers();
        if (players.isEmpty()) {
            return this;
        }
        int idx = players.indexOf(game.getActivePlayer());
        Player nextPlayer = players.get((idx + 1) % players.size());
        game.setActivePlayer(nextPlayer);
        return this;
    }

    /**
     * Mau-Mau-Ablegeregel: eine Karte passt, wenn sie dieselbe Farbe oder denselben
     * Rang wie die oberste Ablagekarte hat. Ist der Ablagestapel leer, ist alles
     * erlaubt (kommt im normalen Spielverlauf nicht vor, da eine Startkarte liegt).
     */
    private boolean matches(Card card, Card top) {
        if (top == null) {
            return true;
        }
        return card.getSuit() == top.getSuit() || card.getRank() == top.getRank();
    }

    /** @return oberste Karte des Ablagestapels (Tisch) oder {@code null}, wenn leer. */
    private Card topOfDiscard(Game game) {
        List<Card> pile = game.getTable().getCards();
        return pile.isEmpty() ? null : pile.get(pile.size() - 1);
    }
}

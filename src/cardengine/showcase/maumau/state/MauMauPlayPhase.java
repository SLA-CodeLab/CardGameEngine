package cardengine.showcase.maumau.state;

import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.state.Phase;
import cardengine.showcase.maumau.command.MauMauDrawCommand;
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
 *   <li>Ein {@link MauMauDrawCommand} ist gueltig, wenn er vom aktiven Spieler kommt
 *       und der Nachziehstapel nicht leer ist.</li>
 * </ul>
 *
 * <p>Karteneffekte (7 zieht zwei, 8 aussetzen, Bube waehlt Farbe ...) sind hier
 * <b>absichtlich nicht</b> umgesetzt – sie sind ueber das {@code Effect}/{@code EffectCard}
 * des Frameworks vorgesehen und werden separat ergaenzt.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MauMauPlayPhase implements Phase {

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
            return matches(card, topOfDiscard(game));
        }

        if (cmd instanceof MauMauDrawCommand draw) {
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

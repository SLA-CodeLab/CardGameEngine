package cardengine.showcase.minigame;

import cardengine.framework.command.Command;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.state.Phase;

import java.util.List;

/**
 * Einzige Phase des Minigames: der aktive Spieler zieht genau eine Karte.
 *
 * <p>An das neue {@link Phase}-Interface angepasst. Die Phase erzeugt keinen Command
 * mehr selbst und fuehrt auch nichts aus – das uebernimmt zentral
 * {@code Game.submitCommand(...)}. Sie stellt nur zwei Dinge bereit:</p>
 * <ul>
 *   <li>{@link #isValid(Game, Command)} – ist dieser Zug jetzt erlaubt?</li>
 *   <li>{@link #next(Game)} – Zustandsuebergang: naechster Spieler ist am Zug.</li>
 * </ul>
 *
 * <p>Da das Minigame nur eine Phase kennt, bleibt diese Phase aktiv; {@code next(...)}
 * schaltet lediglich den aktiven Spieler weiter. Ueber das Spielende entscheidet die
 * Engine per {@code checkWinCondition()}.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MiniDrawPhase implements Phase {

    public MiniDrawPhase() {
    }

    /**
     * Prueft, ob gezogen werden darf: es muss ein Zieh-Command sein und das Deck
     * darf nicht leer sein.
     *
     * @param game aktuelles Spiel
     * @param cmd  eingereichter Command
     * @return true, wenn der Zug zulaessig ist
     */
    @Override
    public boolean isValid(Game game, Command cmd) {
        return cmd instanceof MiniDrawCommand
                && game.getDeck() != null
                && !game.getDeck().isEmpty();
    }

    /**
     * Schaltet den aktiven Spieler zyklisch auf den naechsten Spieler weiter.
     *
     * @param game aktuelles Spiel
     */
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
}

package cardengine.application.bot;

import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.state.Phase;
import cardengine.showcase.maumau.command.DrawCardCommand;
import cardengine.showcase.maumau.command.PlayCardCommand;

import java.util.ArrayList;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Einfacher Mau-Mau-Bot (Strategy-Pattern). Heuristik: die erste spielbare
 * Handkarte legen; ist keine spielbar, eine Karte nachziehen.</p>
 *
 * <p>UMSTELLUNG von Claude (Fable 5): Ob eine Karte spielbar ist, entscheidet der
 * Bot nicht mehr selbst (vorher stand die Farbe/Rang-Regel hier doppelt), sondern
 * er baut Kandidaten-Commands und fragt {@code Phase.isValid} – dasselbe Muster,
 * mit dem der Controller die Handkarten hervorhebt. Damit beruecksichtigt der Bot
 * automatisch auch den Farbwunsch nach einem Buben; mit der alten Doppel-Regel
 * haette er dort ungueltige Zuege eingereicht und das Spiel waere stehengeblieben
 * (abgelehnte Zuege loesen keinen neuen Bot-Zug aus). Den eigenen Farbwunsch beim
 * Buben trifft der Bot ueber den Fallback im {@code ChooseSuitEffect}.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MauMauBot implements BotStrategy {

    @Override
    public Command decideMove(Game game, Player me) {
        Phase phase = game.getCurrentPhase();
        if (phase != null) {
            for (Card card : new ArrayList<>(me.getHand().getCards())) {
                Command play = new PlayCardCommand(me, card, game.getTable(), game);
                if (phase.isValid(game, play)) {
                    return play;
                }
            }
        }
        if (game.getDeck() != null && !game.getDeck().isEmpty()) {
            return new DrawCardCommand(me, game.getDeck());
        }
        return null;
    }
}

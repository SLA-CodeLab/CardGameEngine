package cardengine.showcase.maumau.strategy.effect;

import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.strategy.Effect;

/**
 * Mau-Mau-Effekt 8: der naechste Spieler wird uebersprungen.
 * @author Stanislav
 */

public class SkipEffect implements Effect {

    @Override
    public void apply(Game game) {
        //wir nehmen den nächsten Spieler
        Player skipped = game.getNextPlayer(game.getActivePlayer());
        // und wir setzen ihn als aktiver Spiler
        if (skipped != null) {
            game.setActivePlayer(skipped);
        }
    }
}

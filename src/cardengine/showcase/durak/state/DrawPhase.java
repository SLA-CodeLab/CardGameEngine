package cardengine.showcase.durak.state;

import cardengine.framework.command.Command;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.state.Phase;
import cardengine.showcase.durak.command.DrawCardCommand;
import cardengine.showcase.durak.DurakGameSetup;


public class DrawPhase implements Phase {

    /**
     * Prüft ob der Spieler Karten ziehen darf.
     * @return true wenn das Deck noch Karten hat
     * @author Lukas
     */
    @Override
    public boolean isValid(Game game, Command cmd) {
        if (cmd instanceof DrawCardCommand draw) {
            Player player = draw.getPlayer();
            if (player != null && game.getDeck() != null && !game.getDeck().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Es wird nur in die nächste Phase übergegangen wenn kein Spieler mehr eine Karte ziehen muss und kann
     * @return DrawPhase wenn Karte gezogen werden muss oder AttackPhase wenn es weiter gehen kann
     * @author Lukas
     */
    @Override
    public Phase next(Game game, Command cmd) {
        if (playerNeedCards(game)) return this;
        return DurakTurn.startAttack(game, game.getActivePlayer());
    }

    /**
     * Prüft Handkartenlimit und schaut das jeder Spieler möglichst 6 Karten auf der Hand hat
     * todo hier muss noch die Reihenfolge der Prioritäten beim ziehen beachtet werden
     * @param game
     * @return
     * @author Lukas
     */
    private boolean playerNeedCards(Game game) {
        if (game.getDeck().isEmpty()) return false;
        for (Player p : game.getPlayers()) {
            if (p.getHand().size() < DurakGameSetup.getHandSize()) return true;
        }
        return false;
    }
}

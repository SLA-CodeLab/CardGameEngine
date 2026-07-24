package cardengine.showcase.durak.state;

import cardengine.framework.command.Command;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.state.Phase;
import cardengine.showcase.durak.command.DrawCardCommand;

public class DrawPhase implements Phase {

    /**
     * todo Schreibe morgen Doku
     *      * @return true wenn regelkonform
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
     * todo Schreibe morgen Doku
     * @return true, wenn legen Regelkonform ist
     * @author Lukas
     */
    //todo hier wird noch nicht die richtige Reihenfolge zum Ziehen bestimmt also Angreifer -> Zuleger -> Verteidiger
    @Override
    public Phase next(Game game) {
        if (playerNeedCards(game)) return this;
        return new AttackPhase(game.getNextPlayer(game.getActivePlayer()));
    }

    /**
     * todo Schreibe morgen Doku
     * @param game
     * @return
     * @author Lukas
     */
    private boolean playerNeedCards(Game game) {
        if (game.getDeck() != null && !game.getDeck().isEmpty()) {
            for (Player p : game.getPlayers()) {
                if (p.getHand().size() < 6) {
                    return true;
                }
            }
        }
        return false;
    }
}

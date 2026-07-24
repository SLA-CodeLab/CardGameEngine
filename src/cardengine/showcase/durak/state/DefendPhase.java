package cardengine.showcase.durak.state;

import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.state.Phase;
import cardengine.showcase.durak.command.AttackCardCommand;
import cardengine.showcase.durak.command.DefendCardCommand;
import cardengine.showcase.durak.command.TakeCardCommand;
import cardengine.showcase.durak.command.ThrowInCardCommand;

/**
 * Implementiert die Verteidigungsphase des Spiels also wo der Verteidiger die ungeschlagenen Karten auf dem Tisch schlagen muss
 * @author Lukas
 */
public class DefendPhase implements Phase {
    private Player verteidiger;
    public DefendPhase(Player verteidiger) {
        this.verteidiger = verteidiger;
    }

    @Override
    public boolean isValid(Game game, Command cmd) {
        if (verteidiger == null || cmd == null) {
            return false;
        }

        if (cmd instanceof DefendCardCommand defend) {
            if (defend.getPlayer() != verteidiger) {
                return false;
            }
            Card card = defend.getCard();
            if (!verteidiger.getHand().getCards().contains(card)) {
                return false;
            }
            return defendable(card, game);
        }

        if (cmd instanceof TakeCardCommand take) {
            return take.getPlayer() == verteidiger;
        }
        return false;
    }

    private boolean defendable(Card card, Game game) {
        //todo
        return false;
    }

    @Override
    public Phase next(Game game) {
        if (game.getTable().isEmpty()) {
            return new DrawPhase();
        }
        else if ((game.getTable().size() % 2) == 0) {
            return new AttackPhase(verteidiger);
        }
        else  {
            return new DefendPhase(verteidiger);
        }
    }
}

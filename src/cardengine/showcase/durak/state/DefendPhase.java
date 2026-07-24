package cardengine.showcase.durak.state;

import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.core.Suit;
import cardengine.framework.state.Phase;
import cardengine.showcase.durak.command.DefendCardCommand;
import cardengine.showcase.durak.command.TakeCardCommand;
import cardengine.showcase.durak.factory.DurakDeck;

import java.util.List;

public class DefendPhase implements Phase {
    private Player verteidiger;
    public DefendPhase(Player verteidiger) {
        this.verteidiger = verteidiger;
    }

    /**
     * todo Schreibe morgen Doku
     * @return true wenn regelkonform
     * @author Lukas
     */
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

    /**
     * todo Schreibe morgen Doku
     * @param schlagkarte
     * @param game
     * @return
     * @author Lukas
     */
    private boolean defendable(Card schlagkarte, Game game) {
        List<Card> table = game.getTable().getCards();
        if (table.isEmpty() || table.size() % 2 == 0) return false;

        Suit trumpf = ((DurakDeck) game.getDeck()).getTrumpSuit(); //todo code smell?
        Card offeneAngriffskarte = table.get(table.size() - 1);
        return schlaegt(schlagkarte, offeneAngriffskarte, trumpf);
    }

    /**
     * todo Schreibe morgen Doku
     * @param schlag
     * @param angriff
     * @param trumpf
     * @return
     * @author Lukas
     */
    private boolean schlaegt(Card schlag, Card angriff, Suit trumpf) {
        if (schlag.getSuit() == angriff.getSuit()) {
            return schlag.getRank().ordinal() > angriff.getRank().ordinal();
        }
        return schlag.getSuit() == trumpf && angriff.getSuit() != trumpf;
    }

    /**
     * todo Schreibe morgen Doku
     * @return true, wenn legen Regelkonform ist
     * @author Lukas
     */

    //todo hier muss es eigentlich zwei Fälle geben also das der Verteifiger Erfolgreich alles geschlagen hat dann nächster Angreifer wird oder
    // nicht erfolgreich geschlagen hat und dann der Zuleger nächster Angreifer wird
    @Override
    public Phase next(Game game) {
        if (game.getTable().isEmpty()) {
            game.setActivePlayer(game.getNextPlayer(verteidiger));
            if (game.getDeck().isEmpty()) {
                return new AttackPhase(game.getNextPlayer(game.getActivePlayer()));
            } else {
                return new DrawPhase();
            }
        }
        else if (allDefended(game)) {
            return new AttackPhase(verteidiger);
        }
        else  {
            return new DefendPhase(verteidiger);
        }
    }

    private boolean allDefended(Game game) {
        return (game.getTable().size() % 2) == 0;
    }
}

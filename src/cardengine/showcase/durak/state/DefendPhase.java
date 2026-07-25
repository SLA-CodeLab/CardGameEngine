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
     * guckt ob der verteidiger auch gerade am zug ist und ob dieser die betreffende Handkarte auch im Deck hat falls ja,
     * dann geht er in defendable funkition rein
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
     * guckt sich die karte an mit der geschlagen werden soll, holt sich dann die letzte karte die gelegt wurde also die, die zu verteidigen ist
     * und ruft darauf dann schlaegt auf
     * @param schlagkarte karte an mit der geschlagen werden soll
     * @return true, wenn schlagbar
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
     * Vergleicht Karten ob die Karte basierend auf Suit und Rank und unter beachtung Trumpf schlagen kann
     * @param schlag Karte die zum schlagen verwendet werden soll
     * @param angriff Karte mit der angegriffen wurde und die verteidigt werden muss
     * @param trumpf Suit des Trumpfs für dieses Spiel
     * @return true, wenn 'schlag' auf 'angriff' passt
     * @author Lukas
     */
    private boolean schlaegt(Card schlag, Card angriff, Suit trumpf) {
        if (schlag.getSuit() == angriff.getSuit()) {
            return schlag.getRank().ordinal() > angriff.getRank().ordinal();
        }
        return schlag.getSuit() == trumpf && angriff.getSuit() != trumpf;
    }

    /**
     * @return die nächste Phase
     * @author Lukas
     */
    @Override
    public Phase next(Game game) {
        if (game.getTable().isEmpty()) {
            Player candidate = game.getNextPlayer(verteidiger);
            game.setActivePlayer(candidate);
            if (DurakTurn.needsRefill(game)) {
                return new DrawPhase();
            }
            return DurakTurn.startAttack(game, candidate);
        }
        else if (allDefended(game)) {
            // todo ZULEGER: Hier wird fest der urspruengliche Angreifer wieder aktiv.
            //  Sobald die AttackPhase reihum weiterschaltet, muss stattdessen deren
            //  aktueller Leger uebernommen werden. Siehe todo in AttackPhase.
            game.setActivePlayer(DurakTurn.prevInGame(game, verteidiger));
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

package cardengine.showcase.durak.state;

import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.state.Phase;
import cardengine.showcase.durak.command.AttackCardCommand;
import cardengine.showcase.durak.command.ThrowInCardCommand;

import java.util.List;

/**
 * Implementiert die Angriffsphase des Spiels also das der aktive Spieler und der Zuleger sofern dran Karten auf den Tisch legen dürfen
 *
 * @author Lukas
 */
public class AttackPhase implements Phase {
    //Eigentlich wollte ich das unbedingt vermeiden weil das bisschen gegen das State Pattern geht wenn Phase Objektvariablen hat
    // aber Durak ist echt besonders damit das der activePlayer die ganze Zeit um den Verteidiger rumwechselt. Da finde ich einfach keine schöne Lösung
    private Player verteidiger;
    public AttackPhase(Player verteidiger) {
        this.verteidiger = verteidiger;
    }

    @Override
    public boolean isValid(Game game, Command cmd) {
        Player angreifer = game.getPreviousPlayer(verteidiger);
        if (angreifer == null || cmd == null) {
            return false;
        }

        if (cmd instanceof AttackCardCommand attack) {
            if (attack.getPlayer() != angreifer) {
                return false;
            }
            Card card = attack.getCard();
            if (!angreifer.getHand().getCards().contains(card)) {
                return false;
            }
            return darfLegen(card, game);
        }

        Player zuleger = game.getNextPlayer(verteidiger);
        if (cmd instanceof ThrowInCardCommand throwIn) {
            if (throwIn.getPlayer() != zuleger) {
                return false;
            }
            Card card = throwIn.getCard();
            if (!zuleger.getHand().getCards().contains(card)) {
                return false;
            }
            return darfLegen(card, game);
        }
        return false;
    }

    private boolean darfLegen(Card card, Game game) {
        List<Card> tableCards = game.getTable().getCards();
        if (tableCards.isEmpty()) {
            return true;
        }

        for  (Card c : tableCards) {
            if (c.getRank() == card.getRank()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Phase next(Game game) {
        game.setActivePlayer(verteidiger);
        return new DefendPhase(verteidiger);
    }
}

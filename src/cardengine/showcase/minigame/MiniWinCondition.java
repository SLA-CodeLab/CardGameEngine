package cardengine.showcase.minigame;

import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.strategy.WinCondition;

/**
 * GENERIERT ZUM TESTEN DES FRAMEWORKS
 */
public class MiniWinCondition implements WinCondition {

    @Override
    public boolean isGameOver(Game game) {
        // Spiel endet, wenn ein Spieler 3 Karten hat ODER das Deck leer ist
        if (game.getDeck() != null && game.getDeck().isEmpty()) {
            return true;
        }
        
        for (Player p : game.getPlayers()) {
            if (p.getHand().getCards().size() >= 3) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Player getWinner(Game game) {
        // Der Gewinner ist der mit den meisten Karten
        Player winner = null;
        int maxCards = -1;
        
        for (Player p : game.getPlayers()) {
            int count = p.getHand().getCards().size();
            if (count > maxCards) {
                maxCards = count;
                winner = p;
            } else if (count == maxCards) {
                // Bei Gleichstand (z.B. Deck leer) gibt es keinen eindeutigen Gewinner (Unentschieden)
                winner = null; 
            }
        }
        return winner;
    }
}

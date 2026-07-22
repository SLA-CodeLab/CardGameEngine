package cardengine.showcase.durak.strategy;

import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.strategy.WinCondition;

public class DurakWinCondition implements WinCondition {
    /**
     * @author Stanislav
     */
    @Override
    public boolean isGameOver(Game game) {

        // wenn es noch Karten im Deck gibt, dann ist das Game nicht vorbei
        if (!game.getDeck().isEmpty()){
            return false;
        }

        // zaehlen, wie viele Spieler noch Karten haben
        int playersWithCards = 0;
        for (Player player : game.getPlayers()) {
            if (!player.getHand().isEmpty()){
                playersWithCards++;
            }
        }

        //Falls nur eine Person Karten hat, dann ist die Person Durak
        return playersWithCards <= 1;
    }


    /**
     * @author Stanislav
     */

    @Override
    public Player getWinner(Game game) {

        Player winner = null;
        int playerCoundWithEmptyHands = 0;

        for (Player player : game.getPlayers()) {
            if (player.getHand().isEmpty()){
                winner = player;
                playerCoundWithEmptyHands++;
            }
        }
        //Wenn es eine unentschieden auftaucht
        if (playerCoundWithEmptyHands == game.getPlayers().size()){
            return null;
        }

        return winner;
    }
}

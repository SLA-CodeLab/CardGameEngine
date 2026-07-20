package cardengine.showcase.minigame;

import cardengine.framework.state.Phase;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;

/**
 * GENERIERT ZUM TESTEN DES FRAMEWORKS
 */
public class MiniDrawPhase implements Phase {

    public MiniDrawPhase() {
    }

    @Override
    public void aktionDurchfuehren(Game game) {
        Player activePlayer = game.getActivePlayer();
        System.out.println("\n[Phase] " + activePlayer.getName() + " ist am Zug.");
        
        // Simuliere, dass der Spieler einen Command ausführt
        MiniDrawCommand drawCommand = new MiniDrawCommand(activePlayer, game.getDeck());
        
        // Command über die Engine ausführen (landet in der History!)
        game.executeCommand(drawCommand);
        
        // Phase beenden -> TurnLoop in Game.java wechselt danach zum nächsten Spieler
        game.changePhase(null); 
    }
}

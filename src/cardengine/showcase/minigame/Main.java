package cardengine.showcase.minigame;

import cardengine.framework.core.Game;
import cardengine.framework.core.Player;

/**
 * GENERIERT ZUM TESTEN DES FRAMEWORKS
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("--- ENGINE START ---");
        Game game = new Game();
        
        System.out.println("[Main] Spieler werden hinzugefügt...");
        game.addPlayer(new Player(1, "Alice"));
        game.addPlayer(new Player(2, "Bob"));
        
        System.out.println("[Main] initGame wird aufgerufen...");
        game.initGame(new MiniFactory(), new MiniWinCondition(), new MiniSetup());
        
        System.out.println("--- ENGINE BEENDET ---");
        Player winner = game.getWinner();
        if (winner != null) {
            System.out.println("Gewinner: " + winner.getName() + " mit " + winner.getHand().getCards().size() + " Karten!");
        } else {
            System.out.println("Das Spiel endete unentschieden!");
        }
    }
}

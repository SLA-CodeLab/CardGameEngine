package cardengine.application;

import cardengine.application.controller.GameController;
import cardengine.application.ui.GameView;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.showcase.minigame.MiniFactory;
import cardengine.showcase.minigame.MiniSetup;
import cardengine.showcase.minigame.MiniWinCondition;

import javax.swing.SwingUtilities;

/**
 * Einstiegspunkt der Swing-Anwendung.
 *
 * <p>Baut das Model (das {@link Game} mit dem Minigame-Showcase), die
 * {@link GameView} und den {@link GameController} auf dem Event-Dispatch-Thread
 * zusammen und startet das Spiel. Ab hier ist der Ablauf rein ereignisgetrieben:
 * Buttons -> {@code submitCommand} -> {@code GameListener} -> Neuzeichnen.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.addPlayer(new Player(1, "Alice"));
            game.addPlayer(new Player(2, "Bob"));

            GameView view = new GameView(game.getPlayers());
            new GameController(game, view);

            // Model vorbereiten (verteilt Startkarten), dann anzeigen und starten.
            game.initGame(new MiniFactory(), new MiniWinCondition(), new MiniSetup());
            view.setVisible(true);
            game.start(); // feuert das erste onStateChanged -> initiales Rendern
        });
    }
}

package cardengine.showcase.minigame;

import cardengine.application.controller.MinigameController;
import cardengine.application.ui.GameView;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;

import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.List;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Einstiegspunkt fuer den <b>Minigame</b>-Showcase (Framework-Test). Baut Model,
 * View und Controller auf dem Event-Dispatch-Thread zusammen und startet das Spiel.
 * Jeder Showcase hat einen eigenen {@code Main} in seinem eigenen Package, damit sich
 * die Spiele unabhaengig voneinander starten lassen.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MinigameMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            List<Player> players = Arrays.asList(
                    new Player(1, "Alice"),
                    new Player(2, "Bob"),
                    new Player(3, "Carol"),
                    new Player(4, "Dave"));
            players.forEach(game::addPlayer);

            GameView view = new GameView(game.getPlayers(), "Minigame");
            new MinigameController(game, view);

            game.initGame(new MiniFactory(), new MiniWinCondition(), new MiniSetup());
            view.setVisible(true);
            game.start();
        });
    }
}

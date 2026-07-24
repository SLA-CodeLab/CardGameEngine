package cardengine.showcase.durak;

import cardengine.application.bot.BotStrategy;
import cardengine.application.bot.DurakBot;
import cardengine.application.controller.DurakController;
import cardengine.application.ui.GameView;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.showcase.durak.factory.DurakDeckFactory;
import cardengine.showcase.durak.strategy.DurakWinCondition;

import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Claude (Opus 4.8)
 */
public class DurakMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            List<Player> players = Arrays.asList(
                    new Player(1, "Alice"),
                    new Player(2, "Bob"),
                    new Player(3, "Carol"),
                    new Player(4, "Dave"));
            players.forEach(game::addPlayer);

            Map<Player, BotStrategy> bots = new LinkedHashMap<>();
            bots.put(players.get(1), new DurakBot());
            bots.put(players.get(2), new DurakBot());
            bots.put(players.get(3), new DurakBot());

            GameView view = new GameView(game.getPlayers(), "Durak");
            new DurakController(game, view, bots);

            game.initGame(new DurakDeckFactory(), new DurakWinCondition(), new DurakGameSetup());
            view.setVisible(true);
            game.start();
        });
    }
}

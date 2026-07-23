package cardengine.showcase.maumau;

import cardengine.application.controller.MauMauController;
import cardengine.application.ui.GameView;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.application.bot.BotStrategy;
import cardengine.showcase.maumau.bot.MauMauBot;
import cardengine.showcase.maumau.factory.MauMauDeckFactory;
import cardengine.showcase.maumau.strategy.MauMauWinCondition;

import javax.swing.SwingUtilities;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Einstiegspunkt fuer den <b>Mau-Mau</b>-Showcase. Ein menschlicher Spieler ("Du")
 * tritt gegen zwei Bots an. Model, View und Controller werden auf dem
 * Event-Dispatch-Thread zusammengebaut; danach laeuft alles ereignisgetrieben:
 * Kartenklick/Button -&gt; {@code submitCommand} -&gt; {@code GameListener} -&gt;
 * Neuzeichnen, und Bot-Zuege spielen sich ueber den {@code BotDriver} von selbst ab.</p>
 *
 * <p>Spieler- und Bot-Zahl lassen sich hier frei aendern: Wer in der {@code bots}-Map
 * steht, wird vom Computer gesteuert; wer fehlt, ist menschlich.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MauMauMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            Player du = new Player(1, "Du");
            Player botBob = new Player(2, "Bot Bob");
            Player botCarol = new Player(3, "Bot Carol");
            game.addPlayer(du);
            game.addPlayer(botBob);
            game.addPlayer(botCarol);

            // Nur die Bots bekommen eine Strategie; "Du" bleibt menschlich.
            Map<Player, BotStrategy> bots = new LinkedHashMap<>();
            bots.put(botBob, new MauMauBot());
            bots.put(botCarol, new MauMauBot());

            GameView view = new GameView(game.getPlayers(), "Mau-Mau");
            view.setDrawButtonText("Karte ziehen");
            new MauMauController(game, view, bots);

            game.initGame(new MauMauDeckFactory(), new MauMauWinCondition(), new MauMauSetup());
            view.setVisible(true);
            game.start();
        });
    }
}

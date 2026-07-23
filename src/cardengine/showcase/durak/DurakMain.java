package cardengine.showcase.durak;

import cardengine.application.controller.DurakController;
import cardengine.application.ui.GameView;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.showcase.durak.factory.DurakDeckFactory;
import cardengine.showcase.durak.strategy.DurakWinCondition;

import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.List;

/**
 * GENERIERT von Claude (Opus 4.8) – <b>Geruest / Platzhalter</b>.
 *
 * <p>Einstiegspunkt fuer den <b>Durak</b>-Showcase. Das Fenster laesst sich bereits
 * oeffnen, das Spiel ist aber noch nicht spielbar: {@code DurakGameSetup} verteilt noch
 * keine Karten und liefert noch keine Startphase, und die Phasen (AttackPhase /
 * DefendPhase / DrawPhase) fehlen. Sobald ihr diese implementiert habt, wird derselbe
 * Tisch lebendig – ggf. hier noch die Spielerzahl anpassen und im {@link DurakController}
 * die Aktionen anbinden.</p>
 *
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

            GameView view = new GameView(game.getPlayers(), "Durak");
            new DurakController(game, view);

            game.initGame(new DurakDeckFactory(), new DurakWinCondition(), new DurakGameSetup());
            view.setVisible(true);
            game.start();
        });
    }
}

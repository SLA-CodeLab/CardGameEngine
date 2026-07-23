package cardengine.application.bot;

import cardengine.framework.command.Command;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;

import javax.swing.Timer;
import java.util.Map;
import java.util.function.Consumer;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Spiel-unabhaengige Hilfe, die Bot-Spieler automatisch ziehen laesst. Nach jeder
 * Zustandsaenderung prueft der Controller ueber {@link #onState()}, ob der jetzt aktive
 * Spieler ein Bot ist; wenn ja, wird nach einer kurzen Pause (damit man die Zuege sieht)
 * genau ein Bot-Zug ausgefuehrt. Da jeder Bot-Zug wieder eine Zustandsaenderung ausloest,
 * spielen sich mehrere Bots hintereinander von selbst ab, bis wieder ein Mensch am Zug
 * ist oder das Spiel vorbei ist.</p>
 *
 * <p>Der Driver kennt keine spielspezifischen Commands: <em>was</em> gezogen wird,
 * entscheidet die {@link BotStrategy}; <em>wie</em> der Command eingereicht (und
 * geloggt) wird, uebernimmt der per Konstruktor uebergebene Consumer. Dadurch ist der
 * Driver fuer Mau-Mau und spaeter Durak gleichermassen nutzbar.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class BotDriver {

    private final Game game;
    private final Map<Player, BotStrategy> bots;
    private final int delayMs;
    private final Consumer<Command> moveConsumer;

    /** Verhindert, dass parallel mehrere Bot-Zuege eingeplant werden. */
    private boolean scheduled;

    /**
     * @param game         das Spiel
     * @param bots         Zuordnung Bot-Spieler -&gt; Strategie (menschliche Spieler fehlen hier)
     * @param delayMs      Pause vor jedem Bot-Zug in Millisekunden
     * @param moveConsumer erhaelt den vom Bot gewaehlten Command (soll ihn einreichen/loggen)
     */
    public BotDriver(Game game, Map<Player, BotStrategy> bots, int delayMs, Consumer<Command> moveConsumer) {
        this.game = game;
        this.bots = bots;
        this.delayMs = delayMs;
        this.moveConsumer = moveConsumer;
    }

    /** @return true, wenn dieser Spieler von einem Bot gesteuert wird. */
    public boolean isBot(Player player) {
        return bots.containsKey(player);
    }

    /**
     * Plant – falls der aktive Spieler ein Bot ist – dessen naechsten Zug ein.
     * Aus {@code onStateChanged} des Controllers aufzurufen.
     */
    public void onState() {
        if (scheduled || game.getCurrentPhase() == null) {
            return;
        }
        if (!isBot(game.getActivePlayer())) {
            return;
        }
        scheduled = true;
        Timer timer = new Timer(delayMs, e -> fire());
        timer.setRepeats(false);
        timer.start();
    }

    private void fire() {
        scheduled = false;
        if (game.getCurrentPhase() == null) {
            return;
        }
        Player active = game.getActivePlayer();
        BotStrategy strategy = bots.get(active);
        if (strategy == null) {
            return;
        }
        Command cmd = strategy.decideMove(game, active);
        if (cmd != null) {
            moveConsumer.accept(cmd);
        }
    }
}

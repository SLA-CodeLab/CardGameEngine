package cardengine.application.controller;

import cardengine.application.bot.BotDriver;
import cardengine.application.bot.BotStrategy;
import cardengine.application.ui.CardRenderer;
import cardengine.application.ui.GameView;
import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.observer.GameListener;
import cardengine.showcase.maumau.command.MauMauDrawCommand;
import cardengine.showcase.maumau.command.PlayCardCommand;

import java.util.Map;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Controller (MVC) fuer den Mau-Mau-Showcase. Uebersetzt UI-Ereignisse in
 * Framework-Commands:</p>
 * <ul>
 *   <li>Klick auf eine eigene Handkarte -&gt; {@link PlayCardCommand} (Karte legen).</li>
 *   <li>„Karte ziehen"-Button -&gt; {@link MauMauDrawCommand}.</li>
 *   <li>„Rückgängig"-Button -&gt; {@code Game.undoLastAction()}.</li>
 * </ul>
 *
 * <p>Ob ein Zug erlaubt ist, entscheidet nicht der Controller, sondern die
 * {@code MauMauPlayPhase} ueber {@code isValid}. Der Controller reicht nur ein und
 * stellt anschliessend ueber die {@link GameListener}-Callbacks das Ergebnis dar.
 * Bot-Spieler werden ueber den {@link BotDriver} automatisch gezogen.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MauMauController implements GameListener {

    private static final int BOT_DELAY_MS = 800;

    private final Game game;
    private final GameView view;
    private final BotDriver botDriver;

    /**
     * @param game vorbereitetes Spiel
     * @param view zugehoerige Ansicht
     * @param bots Zuordnung Bot-Spieler -&gt; Strategie; menschliche Spieler stehen hier nicht drin
     */
    public MauMauController(Game game, GameView view, Map<Player, BotStrategy> bots) {
        this.game = game;
        this.view = view;
        this.botDriver = new BotDriver(game, bots, BOT_DELAY_MS, this::submitBotMove);

        game.addGameListener(this);
        view.setCardClickAction(this::onPlayCard);
        view.setDrawAction(e -> onDraw());
        view.setUndoAction(e -> onUndo());
    }

    /** Versucht, die angeklickte Karte des aktiven Spielers abzulegen. */
    private void onPlayCard(Card card) {
        Player active = game.getActivePlayer();
        if (botDriver.isBot(active)) {
            return; // Bots spielen nur ueber den BotDriver, nicht per Klick.
        }
        int pileBefore = game.getTable().size();

        game.submitCommand(new PlayCardCommand(active, card, game.getTable()));

        // Nur bei tatsaechlich gelegter Karte loggen (sonst hat isValid abgelehnt).
        if (game.getTable().size() > pileBefore) {
            view.log(active.getName() + " legt " + CardRenderer.shortLabel(card));
        }
    }

    /** Der aktive Spieler zieht eine Karte vom Nachziehstapel. */
    private void onDraw() {
        Player active = game.getActivePlayer();
        if (botDriver.isBot(active)) {
            return; // Bots ziehen ueber den BotDriver.
        }
        int before = active.getHand().size();

        game.submitCommand(new MauMauDrawCommand(active, game.getDeck()));

        if (active.getHand().size() > before) {
            view.log(active.getName() + " zieht eine Karte.");
        }
    }

    /**
     * Reicht einen vom {@link BotDriver} gewaehlten Zug ein und schreibt eine
     * passende Logzeile. Der aktive Spieler ist hier immer der ziehende Bot.
     */
    private void submitBotMove(Command cmd) {
        Player active = game.getActivePlayer();
        if (cmd instanceof PlayCardCommand play) {
            view.log(active.getName() + " legt " + CardRenderer.shortLabel(play.getCard()));
        } else if (cmd instanceof MauMauDrawCommand) {
            view.log(active.getName() + " zieht eine Karte.");
        }
        game.submitCommand(cmd);
    }

    /** Nimmt den letzten Zug zurueck (nur die Kartenbewegung; siehe Framework-Undo). */
    private void onUndo() {
        if (game.canUndo()) {
            view.log("Rückgängig.");
            game.undoLastAction();
        }
    }

    @Override
    public void onStateChanged(Game game) {
        view.render(game);
        botDriver.onState(); // ist als Naechstes ein Bot dran? Dann zieht er selbst.
    }

    @Override
    public void onGameOver(Player winner) {
        view.showGameOver(winner);
    }

    @Override
    public void onInvalidMove(Command cmd) {
        view.log("Ungültiger Zug – Karte passt nicht (Farbe/Zahl) oder du bist nicht dran.");
    }
}

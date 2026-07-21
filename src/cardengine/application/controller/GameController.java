package cardengine.application.controller;

import cardengine.application.ui.CardRenderer;
import cardengine.application.ui.GameView;
import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.observer.GameListener;
import cardengine.showcase.minigame.MiniDrawCommand;

import java.util.List;

/**
 * Controller (MVC) zwischen {@link Game} (Model) und {@link GameView} (View).
 *
 * <p>Bindet die beiden ueber genau zwei Beruehrungspunkte des Frameworks:
 * Aktionen gehen ueber {@code Game.submitCommand(...)} / {@code Game.undoLastAction()}
 * hinein, Aktualisierungen kommen ueber das {@link GameListener}-Callback heraus.
 * Der Controller baut den showcase-spezifischen {@link MiniDrawCommand} und haelt
 * die View sonst frei von jeder Spiellogik.</p>
 *
 * <p>Alle drei {@link GameListener}-Callbacks werden bedient: {@code onStateChanged}
 * zeichnet neu, {@code onGameOver} zeigt den Gewinner, {@code onInvalidMove} meldet
 * einen abgelehnten Zug.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class GameController implements GameListener {

    private final Game game;
    private final GameView view;

    /**
     * Verdrahtet Model und View und registriert die Button-Handler.
     *
     * @param game vorbereitetes Spiel (vor {@code start()})
     * @param view zugehoerige Ansicht
     */
    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;

        game.addGameListener(this);
        view.setDrawAction(e -> onDraw());
        view.setUndoAction(e -> onUndo());
    }

    /** Baut den Zieh-Command fuer den aktiven Spieler und reicht ihn ein. */
    private void onDraw() {
        Player active = game.getActivePlayer();
        List<Card> hand = active.getHand().getCards();
        int before = hand.size();

        Command cmd = new MiniDrawCommand(game, active, game.getDeck());
        game.submitCommand(cmd);

        // Wenn tatsaechlich gezogen wurde, die neue Karte ins Log schreiben.
        if (hand.size() > before) {
            view.log(active.getName() + " zieht " + CardRenderer.shortLabel(hand.get(hand.size() - 1)));
        }
    }

    /** Nimmt den letzten Zug zurueck. */
    private void onUndo() {
        if (game.canUndo()) {
            view.log("Rückgängig.");
            game.undoLastAction();
        }
    }

    @Override
    public void onStateChanged(Game game) {
        view.render(game);
    }

    @Override
    public void onGameOver(Player winner) {
        // Wird von Game.submitCommand(...) beim Spielende gefeuert.
        view.showGameOver(winner);
    }

    @Override
    public void onInvalidMove(Command cmd) {
        view.log("Ungültiger Zug – nicht möglich.");
    }
}

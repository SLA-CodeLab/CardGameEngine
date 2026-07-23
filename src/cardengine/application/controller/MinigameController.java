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
 * Controller (MVC) fuer den Minigame-Showcase zwischen {@link Game} und {@link GameView}.
 *
 * <p>Bindet beide ueber genau zwei Beruehrungspunkte des Frameworks: Aktionen gehen
 * ueber {@code Game.submitCommand(...)} / {@code Game.undoLastAction()} hinein,
 * Aktualisierungen kommen ueber das {@link GameListener}-Callback heraus. Der
 * Controller baut den showcase-spezifischen {@link MiniDrawCommand} und haelt die
 * View frei von Spiellogik.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MinigameController implements GameListener {

    private final Game game;
    private final GameView view;

    public MinigameController(Game game, GameView view) {
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
        view.showGameOver(winner);
    }

    @Override
    public void onInvalidMove(Command cmd) {
        view.log("Ungültiger Zug – nicht möglich.");
    }
}

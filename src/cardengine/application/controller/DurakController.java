package cardengine.application.controller;

import cardengine.application.ui.GameView;
import cardengine.framework.command.Command;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.observer.GameListener;

/**
 * GENERIERT von Claude (Opus 4.8) – <b>Geruest / Platzhalter</b>.
 *
 * <p>Verbindet {@link Game} und {@link GameView} fuer den Durak-Showcase bereits
 * ueber die {@link GameListener}-Callbacks (Neuzeichnen, Spielende) und den
 * „Rückgängig"-Button. Damit laesst sich {@code DurakMain} schon starten und der
 * Tisch anzeigen.</p>
 *
 * <p><b>Noch offen (bewusst euch ueberlassen):</b> Sobald die Durak-Phasen
 * (AttackPhase / DefendPhase / DrawPhase) und ein vollstaendiges
 * {@code DurakGameSetup} stehen, hier die UI-Aktionen anbinden – abhaengig von der
 * aktuellen Phase einen {@code AttackCardCommand} / {@code DefendCardCommand} /
 * {@code TakeCardCommand} bzw. {@code DrawCardCommand} bauen und ueber
 * {@code game.submitCommand(...)} einreichen (Vorbild:
 * {@code cardengine.application.controller.MauMauController}). Bots gehen genauso wie bei
 * Mau-Mau ueber {@code cardengine.application.bot.BotDriver} plus eine
 * {@code DurakBot}-Strategie.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class DurakController implements GameListener {

    private final Game game;
    private final GameView view;

    public DurakController(Game game, GameView view) {
        this.game = game;
        this.view = view;

        game.addGameListener(this);
        view.setUndoAction(e -> onUndo());

        // TODO(Durak): view.setCardClickAction(this::onCardClicked) und
        // view.setDrawAction(...) anbinden, sobald die Phasen existieren.
    }

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
        view.log("Ungültiger Zug.");
    }
}

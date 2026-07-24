package cardengine.application.controller;

import cardengine.application.bot.BotDriver;
import cardengine.application.bot.BotStrategy;
import cardengine.application.ui.GameView;
import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.observer.GameListener;
import cardengine.showcase.durak.command.AttackCardCommand;
import cardengine.showcase.durak.command.DefendCardCommand;
import cardengine.showcase.durak.command.DrawCardCommand;
import cardengine.showcase.durak.command.EndAttackCommand;
import cardengine.showcase.durak.command.TakeCardCommand;
import cardengine.showcase.durak.state.AttackPhase;
import cardengine.showcase.durak.state.DefendPhase;
import cardengine.showcase.durak.state.DrawPhase;

import java.util.Collections;
import java.util.Map;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Controller (MVC) fuer den Durak-Showcase. Uebersetzt UI-Ereignisse in
 * Framework-Commands – <em>welcher</em> Command, haengt an der aktuellen Phase:</p>
 * <ul>
 *   <li>Kartenklick in der {@link AttackPhase} -&gt; {@link AttackCardCommand},
 *       in der {@link DefendPhase} -&gt; {@link DefendCardCommand}.</li>
 *   <li>Aktions-Button in der AttackPhase -&gt; {@link EndAttackCommand} („Passen"),
 *       in der DefendPhase -&gt; {@link TakeCardCommand} („Aufnehmen").</li>
 *   <li>Die {@link DrawPhase} fuellt der Controller automatisch auf 6 auf –
 *       Nachziehen ist in Durak keine Entscheidung.</li>
 * </ul>
 *
 * <p>Ob ein Zug erlaubt ist, entscheidet nicht der Controller, sondern die jeweilige
 * {@code Phase} ueber {@code isValid}. Der Controller reicht nur ein und stellt ueber die
 * {@link GameListener}-Callbacks das Ergebnis dar. Bot-Spieler laufen ueber den
 * {@link BotDriver} und reichen exakt dieselben Commands ein.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class DurakController implements GameListener {

    private static final int BOT_DELAY_MS = 800;
    private static final int HAND_TARGET = 6;

    private final Game game;
    private final GameView view;
    private final BotDriver botDriver;

    /** Verhindert erneutes Anstossen, waehrend {@link #refillHands()} selbst Draws einreicht. */
    private boolean refilling;

    /** Alle Spieler menschlich. */
    public DurakController(Game game, GameView view) {
        this(game, view, Collections.emptyMap());
    }

    /**
     * @param game vorbereitetes Spiel
     * @param view zugehoerige Ansicht
     * @param bots Zuordnung Bot-Spieler -&gt; Strategie; menschliche Spieler stehen hier nicht drin
     */
    public DurakController(Game game, GameView view, Map<Player, BotStrategy> bots) {
        this.game = game;
        this.view = view;
        this.botDriver = new BotDriver(game, bots, BOT_DELAY_MS, this::submitBotMove);

        game.addGameListener(this);
        view.setCardClickAction(this::onCardClicked);
        view.setDrawAction(e -> onActionButton());
        view.setUndoAction(e -> onUndo());
    }

    /** Kartenklick des aktiven (menschlichen) Spielers: je nach Phase angreifen oder verteidigen. */
    private void onCardClicked(Card card) {
        Player active = game.getActivePlayer();
        if (active == null || botDriver.isBot(active)) {
            return; // Bots spielen nur ueber den BotDriver.
        }
        if (game.getCurrentPhase() instanceof AttackPhase) {
            submitHuman(new AttackCardCommand(active, game.getTable(), card),
                    active.getName() + " legt " + card);
        } else if (game.getCurrentPhase() instanceof DefendPhase) {
            submitHuman(new DefendCardCommand(active, game.getTable(), card),
                    active.getName() + " schlaegt mit " + card);
        }
    }

    /** Aktions-Button: in der AttackPhase „Passen", in der DefendPhase „Aufnehmen". */
    private void onActionButton() {
        Player active = game.getActivePlayer();
        if (active == null || botDriver.isBot(active)) {
            return;
        }
        if (game.getCurrentPhase() instanceof AttackPhase) {
            submitHuman(new EndAttackCommand(active, game.getTable()),
                    active.getName() + " passt.");
        } else if (game.getCurrentPhase() instanceof DefendPhase) {
            submitHuman(new TakeCardCommand(active, game.getTable()),
                    active.getName() + " nimmt die Karten auf.");
        }
    }

    /** Reicht einen menschlichen Zug ein und loggt ihn nur, wenn die Phase ihn akzeptiert hat. */
    private void submitHuman(Command cmd, String logLine) {
        int before = game.getTable().size();
        Object phaseBefore = game.getCurrentPhase();
        game.submitCommand(cmd);
        boolean accepted = game.getTable().size() != before || game.getCurrentPhase() != phaseBefore;
        if (accepted) {
            view.log(logLine);
        }
    }

    private void submitBotMove(Command cmd) {
        game.submitCommand(cmd);
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
        updateActionButton();
        if (refilling) {
            return; // Re-Entrancy aus refillHands(): nicht erneut anstossen.
        }
        if (game.getCurrentPhase() instanceof DrawPhase) {
            refillHands();
            return;
        }
        botDriver.onState();
    }

    /**
     * Nachziehen ist in Durak deterministisch: In der DrawPhase fuellt der Controller alle
     * Spieler auf 6 auf und laesst die Engine danach in die naechste AttackPhase wechseln.
     * Bots werden waehrend des Nachziehens bewusst nicht angestossen.
     */
    private void refillHands() {
        refilling = true;
        try {
            while (game.getCurrentPhase() instanceof DrawPhase) {
                Player needy = firstNeedy();
                if (needy == null) {
                    break;
                }
                game.submitCommand(new DrawCardCommand(needy, game.getDeck()));
            }
        } finally {
            refilling = false;
        }
        view.render(game);
        updateActionButton();
        botDriver.onState();
    }

    /** @return erster Spieler mit weniger als sechs Karten, oder {@code null} (Deck leer / alle voll). */
    private Player firstNeedy() {
        if (game.getDeck() == null || game.getDeck().isEmpty()) {
            return null;
        }
        for (Player p : game.getPlayers()) {
            if (p.getHand().size() < HAND_TARGET) {
                return p;
            }
        }
        return null;
    }

    /** Beschriftet und (de)aktiviert den Aktions-Button passend zur aktuellen Phase. */
    private void updateActionButton() {
        if (game.getCurrentPhase() instanceof AttackPhase) {
            view.setDrawButtonText("Passen");
            view.setActionEnabled(true);
        } else if (game.getCurrentPhase() instanceof DefendPhase) {
            view.setDrawButtonText("Aufnehmen");
            view.setActionEnabled(true);
        } else {
            view.setActionEnabled(false);
        }
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

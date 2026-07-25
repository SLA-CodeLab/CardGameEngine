package cardengine.application.controller;

import cardengine.application.bot.BotDriver;
import cardengine.application.bot.BotStrategy;
import cardengine.application.ui.CardRenderer;
import cardengine.application.ui.GameView;
import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.core.Suit;
import cardengine.framework.observer.GameListener;
import cardengine.framework.state.Phase;
import cardengine.showcase.durak.command.AttackCardCommand;
import cardengine.showcase.durak.command.DefendCardCommand;
import cardengine.showcase.durak.command.DrawCardCommand;
import cardengine.showcase.durak.command.EndAttackCommand;
import cardengine.showcase.durak.command.TakeCardCommand;
import cardengine.showcase.durak.command.ThrowInCardCommand;
import cardengine.showcase.durak.factory.DurakDeck;
import cardengine.showcase.durak.state.AttackPhase;
import cardengine.showcase.durak.state.DefendPhase;
import cardengine.showcase.durak.state.DrawPhase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Controller (MVC) fuer den Durak-Showcase. Uebersetzt UI-Ereignisse in
 * Framework-Commands und stellt ueber die {@link GameListener}-Callbacks das Ergebnis dar.</p>
 *
 * <p><b>Leitidee (Claude, Opus 4.8):</b> Der Controller kennt die Durak-Regeln <em>nicht</em>
 * und raet auch nicht, welcher Command gerade passt. Er baut Kandidaten und fragt die
 * aktuelle {@link Phase} ueber {@code isValid}, welcher davon erlaubt ist. Daraus ergibt
 * sich alles Weitere von selbst:</p>
 * <ul>
 *   <li><b>Kartenklick:</b> Angriff, Zulegen oder Verteidigen – je nachdem, was die Phase
 *       gerade akzeptiert.</li>
 *   <li><b>Highlighting:</b> Genau die Handkarten leuchten, fuer die es einen gueltigen
 *       Command gibt. Keine zweite Regelimplementierung in der UI.</li>
 *   <li><b>Aktions-Button:</b> „Passen"/„Aufnehmen" ist nur anklickbar, wenn der
 *       entsprechende Command gueltig waere.</li>
 * </ul>
 *
 * <p>Die {@link DrawPhase} fuellt der Controller automatisch auf sechs Karten auf –
 * Nachziehen ist in Durak keine Spielerentscheidung. Bot-Spieler laufen ueber den
 * {@link BotDriver} und reichen exakt dieselben Commands ein.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class DurakController implements GameListener {

    private static final int BOT_DELAY_MS = 2000;
    private static final int HAND_TARGET = 6;

    private final Game game;
    private final GameView view;
    private final BotDriver botDriver;

    /** Spieler an diesem Rechner – nur seine Hand liegt offen und nur er klickt. */
    private final Player localPlayer;

    /** Verhindert erneutes Anstossen, waehrend {@link #refillHands()} selbst Draws einreicht. */
    private boolean refilling;

    /** Wird von {@link #onInvalidMove(Command)} gesetzt; ersetzt das fruehere Raten am Tischstand. */
    private boolean lastMoveRejected;

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
        this.localPlayer = firstHuman(game, bots);

        game.addGameListener(this);
        view.setLocalPlayer(localPlayer);
        view.setCardClickAction(this::onCardClicked);
        view.setDrawAction(e -> onActionButton());
        view.setUndoAction(e -> onUndo());
    }

    /** @return erster Spieler ohne Bot-Strategie, oder {@code null} (reines Bot-Spiel). */
    private static Player firstHuman(Game game, Map<Player, BotStrategy> bots) {
        for (Player p : game.getPlayers()) {
            if (!bots.containsKey(p)) {
                return p;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ Eingaben

    /**
     * Kartenklick des aktiven (menschlichen) Spielers. Welcher Command daraus wird,
     * entscheidet die Phase – so funktioniert derselbe Klick fuer Angreifer, Zuleger
     * und Verteidiger.
     */
    private void onCardClicked(Card card) {
        Player active = game.getActivePlayer();
        if (active == null || active != localPlayer || botDriver.isBot(active)) {
            return; // Bots spielen nur ueber den BotDriver.
        }
        Command cmd = cardCommandFor(active, card);
        if (cmd == null) {
            view.log(CardRenderer.shortLabel(card) + " kannst du gerade nicht legen.");
            return;
        }
        submitHuman(cmd, active.getName() + ": " + describe(cmd, card));
    }

    /** Aktions-Button: in der AttackPhase „Passen", in der DefendPhase „Aufnehmen". */
    private void onActionButton() {
        Player active = game.getActivePlayer();
        if (active == null || active != localPlayer || botDriver.isBot(active)) {
            return;
        }
        Phase phase = game.getCurrentPhase();
        if (phase instanceof AttackPhase) {
            submitHuman(new EndAttackCommand(active, game.getTable()),
                    active.getName() + " passt – der Tisch wird abgeräumt.");
        } else if (phase instanceof DefendPhase) {
            submitHuman(new TakeCardCommand(active, game.getTable()),
                    active.getName() + " nimmt die Karten auf.");
        }
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Sucht den Command, den die aktuelle Phase fuer diese Karte akzeptiert:
     * angreifen, zulegen oder verteidigen. Gibt {@code null} zurueck, wenn die Karte
     * gerade nicht gelegt werden darf.</p>
     *
     * <p>{@code isValid} ist eine reine Pruefung ohne Seiteneffekte – die Commands werden
     * hier nur gebaut, nicht ausgefuehrt.</p>
     */
    private Command cardCommandFor(Player player, Card card) {
        Phase phase = game.getCurrentPhase();
        if (phase == null) {
            return null;
        }
        Command attack = new AttackCardCommand(player, game.getTable(), card);
        if (phase.isValid(game, attack)) {
            return attack;
        }
        //todo (Durak, ausserhalb application): ThrowInCardCommand(Player, Card, Table) hat eine
        // andere Parameterreihenfolge als AttackCardCommand(Player, Table, Card) - leicht zu
        // verwechseln, sollte vereinheitlicht werden.
        Command throwIn = new ThrowInCardCommand(player, card, game.getTable());
        if (phase.isValid(game, throwIn)) {
            return throwIn;
        }
        Command defend = new DefendCardCommand(player, game.getTable(), card);
        if (phase.isValid(game, defend)) {
            return defend;
        }
        return null;
    }

    private String describe(Command cmd, Card card) {
        String label = CardRenderer.shortLabel(card);
        if (cmd instanceof DefendCardCommand) {
            return "schlägt mit " + label;
        }
        if (cmd instanceof ThrowInCardCommand) {
            return "legt " + label + " zu";
        }
        return "greift mit " + label + " an";
    }

    /** Reicht einen menschlichen Zug ein und loggt ihn nur, wenn die Phase ihn akzeptiert hat. */
    private void submitHuman(Command cmd, String logLine) {
        lastMoveRejected = false;
        game.submitCommand(cmd);
        if (!lastMoveRejected) {
            view.log(logLine);
        }
    }

    private void submitBotMove(Command cmd) {
        Player active = game.getActivePlayer();
        String name = active != null ? active.getName() : "Bot";
        lastMoveRejected = false;
        game.submitCommand(cmd);
        if (!lastMoveRejected) {
            view.log(name + ": " + botLogLine(cmd));
        }
    }

    private String botLogLine(Command cmd) {
        if (cmd instanceof AttackCardCommand attack) {
            return "greift mit " + CardRenderer.shortLabel(attack.getCard()) + " an";
        }
        if (cmd instanceof ThrowInCardCommand throwIn) {
            return "legt " + CardRenderer.shortLabel(throwIn.getCard()) + " zu";
        }
        if (cmd instanceof DefendCardCommand defend) {
            return "schlägt mit " + CardRenderer.shortLabel(defend.getCard());
        }
        if (cmd instanceof TakeCardCommand) {
            return "nimmt die Karten auf";
        }
        if (cmd instanceof EndAttackCommand) {
            return "passt";
        }
        return "zieht";
    }

    private void onUndo() {
        //todo (Framework, ausserhalb application): Game.undoLastAction() macht nur den Command
        // rueckgaengig, nicht den Phasenwechsel - currentPhase und activePlayer bleiben stehen.
        // Fuer ein sauberes Undo muesste die Engine auch die Phase zuruecksetzen (z.B. indem
        // CommandHistory die Phase mitspeichert).
        if (game.canUndo()) {
            view.log("Rückgängig.");
            game.undoLastAction();
        }
    }

    // ------------------------------------------------------------------ Darstellung

    @Override
    public void onStateChanged(Game game) {
        renderAll();
        if (refilling) {
            return; // Re-Entrancy aus refillHands(): nicht erneut anstossen.
        }
        if (game.getCurrentPhase() instanceof DrawPhase) {
            refillHands();
            return;
        }
        botDriver.onState();
    }

    /** Schiebt Trumpf, Rollen, Highlighting und Buttonzustand in die View. */
    private void renderAll() {
        pushTrump();
        view.render(game);
        updateRoles();
        updateHighlighting();
        updateActionButton();
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Reicht die Trumpfkarte an die View durch. Sie ist die unterste Karte des Decks
     * und wird als letzte gezogen – {@code DurakDeck.shuffle()} deckt sie beim Mischen auf.</p>
     *
     * <p>//todo (Durak, ausserhalb application): {@code DurakDeck} sollte ein
     * {@code getTrumpCard()} anbieten. Bis dahin holt der Controller die unterste Karte
     * ueber {@code getCards().get(0)} – dieselbe Stelle, die {@code revealTrumpCard()}
     * aufdeckt.</p>
     */
    private void pushTrump() {
        if (!(game.getDeck() instanceof DurakDeck durakDeck)) {
            return;
        }
        Suit trumpSuit = durakDeck.getTrumpSuit();
        List<Card> remaining = durakDeck.getCards();
        Card trumpCard = remaining.isEmpty() ? null : remaining.get(0);
        view.setTrump(trumpCard, trumpSuit);
    }

    /**
     * Beschriftet den aktiven Sitz mit seiner Rolle.
     *
     * <p>//todo (Durak, ausserhalb application): AttackPhase/DefendPhase kennen den
     * Verteidiger, bieten aber keinen Getter. Mit einem {@code getVerteidiger()} koennte
     * die UI auch die uebrigen Rollen (Verteidiger, Zuleger) dauerhaft anzeigen.</p>
     */
    private void updateRoles() {
        view.clearRoleLabels();
        Player active = game.getActivePlayer();
        Phase phase = game.getCurrentPhase();
        if (active == null || phase == null) {
            return;
        }
        if (phase instanceof AttackPhase) {
            view.setRoleLabel(active, "greift an");
            view.setStatus(active.getName() + " greift an");
        } else if (phase instanceof DefendPhase) {
            view.setRoleLabel(active, "verteidigt");
            view.setStatus(active.getName() + " verteidigt");
        } else if (phase instanceof DrawPhase) {
            view.setStatus("Nachziehen …");
        }
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Fragt fuer jede Handkarte des eigenen Spielers die Phase, ob es dafuer einen
     * gueltigen Command gibt, und laesst die View genau diese Karten hervorheben.</p>
     *
     * <p>//todo (Framework, ausserhalb application): {@code Hand.getPlayableCards()} war
     * genau dafuer gedacht, wird aber nirgends aufgerufen und gibt einfach die ganze Hand
     * zurueck – die Hand kann das auch gar nicht wissen, weil es von der Phase abhaengt.
     * Diese Methode hier ersetzt sie; {@code Hand.getPlayableCards()} kann entfallen.</p>
     */
    private void updateHighlighting() {
        Player active = game.getActivePlayer();
        if (localPlayer == null || active != localPlayer || game.getCurrentPhase() == null) {
            view.setPlayableCards(Collections.emptySet());
            return;
        }
        Set<Card> playable = new LinkedHashSet<>();
        for (Card card : new ArrayList<>(localPlayer.getHand().getCards())) {
            if (cardCommandFor(localPlayer, card) != null) {
                playable.add(card);
            }
        }
        view.setPlayableCards(playable);
    }

    /**
     * Beschriftet den Aktions-Button und schaltet ihn nur frei, wenn der zugehoerige
     * Command tatsaechlich gueltig waere (also z.&nbsp;B. „Passen" erst, wenn alles
     * geschlagen ist).
     */
    private void updateActionButton() {
        Phase phase = game.getCurrentPhase();
        Player active = game.getActivePlayer();
        boolean mine = localPlayer != null && active == localPlayer;

        if (phase instanceof AttackPhase) {
            view.setDrawButtonText("Passen");
            view.setActionEnabled(mine && phase.isValid(game, new EndAttackCommand(active, game.getTable())));
        } else if (phase instanceof DefendPhase) {
            view.setDrawButtonText("Aufnehmen");
            view.setActionEnabled(mine && phase.isValid(game, new TakeCardCommand(active, game.getTable())));
        } else {
            view.setActionEnabled(false);
        }
    }

    /**
     * Nachziehen ist in Durak deterministisch: In der DrawPhase fuellt der Controller alle
     * Spieler auf sechs auf und laesst die Engine danach in die naechste AttackPhase wechseln.
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
        renderAll();
        botDriver.onState();
    }

    /**
     * @return erster Spieler mit weniger als sechs Karten, oder {@code null} (Deck leer / alle voll).
     *
     * <p>//todo (Durak, ausserhalb application): Die Ziehreihenfolge ist in Durak
     * festgelegt (Angreifer, dann Zuleger, zuletzt der Verteidiger). Hier wird noch in
     * Sitzreihenfolge aufgefuellt – siehe auch den todo in {@code DrawPhase}.</p>
     */
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

    // ------------------------------------------------------------------ Spielende

    @Override
    public void onGameOver(Player winner) {
        renderAll();
        view.showGameOver(durakResult());
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>In Durak gibt es keinen Gewinner, sondern einen Verlierer: den „Durak", der als
     * Einziger noch Karten haelt. Der Text wird deshalb hier gebildet.</p>
     *
     * <p>//todo (Durak, ausserhalb application): {@code DurakWinCondition.getWinner()}
     * liefert einen Spieler mit leerer Hand. Fuer Durak waere ein {@code getLoser()}
     * bzw. eine umbenannte Semantik ehrlicher.</p>
     */
    private String durakResult() {
        List<Player> withCards = new ArrayList<>();
        for (Player p : game.getPlayers()) {
            if (!p.getHand().isEmpty()) {
                withCards.add(p);
            }
        }
        if (withCards.size() == 1) {
            return "Durak (Verlierer): " + withCards.get(0).getName() + "!";
        }
        if (withCards.isEmpty()) {
            return "Unentschieden – niemand bleibt auf Karten sitzen!";
        }
        return "Spiel beendet.";
    }

    @Override
    public void onInvalidMove(Command cmd) {
        lastMoveRejected = true;
        view.log("Ungültiger Zug.");
    }
}

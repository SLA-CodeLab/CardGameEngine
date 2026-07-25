package cardengine.application.controller;

import cardengine.application.bot.BotDriver;
import cardengine.application.bot.BotStrategy;
import cardengine.application.ui.CardRenderer;
import cardengine.application.ui.GameView;
import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.EffectCard;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.core.Suit;
import cardengine.framework.observer.GameListener;
import cardengine.framework.state.Phase;
import cardengine.showcase.maumau.command.DrawCardCommand;
import cardengine.showcase.maumau.command.PlayCardCommand;
import cardengine.showcase.maumau.state.PlayPhase;
import cardengine.showcase.maumau.strategy.effect.ChooseSuitEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Controller (MVC) fuer den Mau-Mau-Showcase. Uebersetzt UI-Ereignisse in
 * Framework-Commands:</p>
 * <ul>
 *   <li>Klick auf eine eigene Handkarte -&gt; {@link PlayCardCommand} (Karte legen).</li>
 *   <li>„Karte ziehen"-Button -&gt; {@link DrawCardCommand}.</li>
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

    private static final int BOT_DELAY_MS = 2000;

    private final Game game;
    private final GameView view;
    private final BotDriver botDriver;

    /** Spieler an diesem Rechner – nur seine Hand liegt offen (Claude, Opus 4.8). */
    private final Player localPlayer;

    /**
     * @param game vorbereitetes Spiel
     * @param view zugehoerige Ansicht
     * @param bots Zuordnung Bot-Spieler -&gt; Strategie; menschliche Spieler stehen hier nicht drin
     */
    public MauMauController(Game game, GameView view, Map<Player, BotStrategy> bots) {
        this.game = game;
        this.view = view;
        this.botDriver = new BotDriver(game, bots, BOT_DELAY_MS, this::submitBotMove);
        this.localPlayer = firstHuman(game, bots);

        game.addGameListener(this);
        view.setLocalPlayer(localPlayer);
        view.setCardClickAction(this::onPlayCard);
        view.setDrawAction(e -> onDraw());
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

    /** Versucht, die angeklickte Karte des aktiven Spielers abzulegen. */
    private void onPlayCard(Card card) {
        Player active = game.getActivePlayer();
        if (botDriver.isBot(active)) {
            return; // Bots spielen nur ueber den BotDriver, nicht per Klick.
        }

        PlayCardCommand cmd = new PlayCardCommand(active, card, game.getTable(), game);

        // ERGAENZUNG (Claude, Fable 5): Bube -> Farbwunsch vorher abfragen und am
        // Effekt hinterlegen. Gefragt wird nur, wenn der Zug ueberhaupt gueltig
        // waere – sonst erschiene der Dialog vor einer ohnehin folgenden Ablehnung.
        Phase phase = game.getCurrentPhase();
        if (phase != null && phase.isValid(game, cmd)
                && card instanceof EffectCard effectCard
                && effectCard.getAction() instanceof ChooseSuitEffect wishEffect) {
            Suit suit = view.askSuitWish();
            if (suit == null) {
                return; // Dialog abgebrochen -> Karte doch nicht legen
            }
            wishEffect.setChosenSuit(suit);
        }

        Player nextBefore = game.getNextPlayer(active); // fuer die Effekt-Logzeile
        int pileBefore = game.getTable().size();

        game.submitCommand(cmd);

        // Nur bei tatsaechlich gelegter Karte loggen (sonst hat isValid abgelehnt).
        if (game.getTable().size() > pileBefore) {
            view.log(active.getName() + " legt " + CardRenderer.shortLabel(card));
            logEffect(active, card, nextBefore);
        }
    }

    /** Der aktive Spieler zieht eine Karte vom Nachziehstapel. */
    private void onDraw() {
        Player active = game.getActivePlayer();
        if (botDriver.isBot(active)) {
            return; // Bots ziehen ueber den BotDriver.
        }
        int before = active.getHand().size();

        game.submitCommand(new DrawCardCommand(active, game.getDeck()));

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
        Player nextBefore = game.getNextPlayer(active);
        if (cmd instanceof PlayCardCommand play) {
            view.log(active.getName() + " legt " + CardRenderer.shortLabel(play.getCard()));
            game.submitCommand(cmd);
            logEffect(active, play.getCard(), nextBefore);
        } else {
            if (cmd instanceof DrawCardCommand) {
                view.log(active.getName() + " zieht eine Karte.");
            }
            game.submitCommand(cmd);
        }
    }

    /**
     * ERGAENZUNG von Claude (Fable 5).
     *
     * <p>Schreibt nach einer erfolgreich gelegten Effektkarte, was passiert ist.
     * Die Effekte selbst kennen die View nicht (Modell-Code) – die Uebersetzung in
     * Logzeilen ist Aufgabe des Controllers. Aufzurufen <em>nach</em>
     * {@code submitCommand}, damit z.&nbsp;B. der Farbwunsch schon gesetzt ist.</p>
     *
     * @param who        Spieler, der die Karte gelegt hat
     * @param card       die gelegte Karte
     * @param nextBefore der naechste Spieler zum Zeitpunkt des Legens (Betroffener von 7/8)
     */
    private void logEffect(Player who, Card card, Player nextBefore) {
        if (!(card instanceof EffectCard)) {
            return;
        }
        switch (card.getRank()) {
            case SEVEN -> {
                if (nextBefore != null) {
                    view.log(nextBefore.getName() + " zieht zwei Strafkarten.");
                }
            }
            case EIGHT -> {
                if (nextBefore != null) {
                    view.log(nextBefore.getName() + " wird übersprungen.");
                }
            }
            case JACK -> {
                if (game.getCurrentPhase() instanceof PlayPhase phase) {
                    Suit wish = phase.getActiveSuitWish(game);
                    if (wish != null) {
                        view.log(who.getName() + " wünscht sich " + CardRenderer.symbolOf(wish) + ".");
                    }
                }
            }
            default -> { /* keine weitere Ausgabe */ }
        }
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
        view.setStatus(buildWishStatus()); // null = Standardtext "X ist am Zug"
        view.render(game);
        updateHighlighting();
        updateActionButton();
        botDriver.onState(); // ist als Naechstes ein Bot dran? Dann zieht er selbst.
    }

    /**
     * ERGAENZUNG von Claude (Fable 5).
     *
     * @return Statuszeile mit aktiver Wunschfarbe, oder {@code null}, wenn kein
     *         Farbwunsch gilt (dann zeigt die View ihren Standardtext)
     */
    private String buildWishStatus() {
        if (game.getCurrentPhase() instanceof PlayPhase phase) {
            Suit wish = phase.getActiveSuitWish(game);
            Player active = game.getActivePlayer();
            if (wish != null && active != null) {
                return active.getName() + " ist am Zug – gewünschte Farbe: " + CardRenderer.symbolOf(wish);
            }
        }
        return null;
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Hebt die Handkarten hervor, die gerade wirklich passen. Die Regel dafuer steht
     * nicht hier, sondern in {@code PlayPhase.isValid} – der Controller baut nur
     * Kandidaten-Commands und fragt die Phase.</p>
     */
    private void updateHighlighting() {
        Phase phase = game.getCurrentPhase();
        Player active = game.getActivePlayer();
        if (phase == null || localPlayer == null || active != localPlayer) {
            view.setPlayableCards(Collections.emptySet());
            return;
        }
        Set<Card> playable = new LinkedHashSet<>();
        for (Card card : new ArrayList<>(localPlayer.getHand().getCards())) {
            if (phase.isValid(game, new PlayCardCommand(localPlayer, card, game.getTable(), game))) {
                playable.add(card);
            }
        }
        view.setPlayableCards(playable);
    }

    /**
     * Seit der View-Umstellung entscheidet der Controller ueber den Aktions-Button:
     * ziehen kann nur der Mensch, wenn er dran ist und der Stapel noch Karten hat.
     */
    private void updateActionButton() {
        boolean running = game.getCurrentPhase() != null;
        boolean mine = localPlayer != null && game.getActivePlayer() == localPlayer;
        boolean deckLeft = game.getDeck() != null && !game.getDeck().isEmpty();
        view.setActionEnabled(running && mine && deckLeft);
    }

    @Override
    public void onGameOver(Player winner) {
        view.showGameOver(winner);
    }

    @Override
    public void onInvalidMove(Command cmd) {
        view.log("Ungültiger Zug – Karte passt nicht (Farbe/Zahl bzw. gewünschte Farbe) oder du bist nicht dran.");
    }
}

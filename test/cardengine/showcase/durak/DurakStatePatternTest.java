package cardengine.showcase.durak;

import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.observer.GameListener;
import cardengine.showcase.durak.command.AttackCardCommand;
import cardengine.showcase.durak.command.DefendCardCommand;
import cardengine.showcase.durak.factory.DurakDeckFactory;
import cardengine.showcase.durak.strategy.DurakWinCondition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GENERIERT von Claude (Opus 4.8) als Test-First-Spezifikation fuer das
 * Durak-State-Pattern – ausgelegt auf beliebige Spielerzahlen (2, 3, 4).
 *
 * <p><b>Dieser Test ist absichtlich zunaechst "rot".</b> Er beschreibt das
 * gewuenschte Verhalten der noch zu bauenden Zustandsmaschine (AttackPhase,
 * DefendPhase, DrawPhase) und der noch zu vervollstaendigenden
 * {@link DurakGameSetup}. Jede fehlschlagende Testmethode ist eine konkrete
 * Aufgabe: implementiere so lange, bis sie gruen wird.</p>
 *
 * <p><b>Bezug zur UI:</b> Dieser Test erzeugt keine GUI, sondern spielt "headless"
 * ueber {@code game.submitCommand(...)}. Weil die Startlogik hier bewusst fuer 2,
 * 3 und 4 Spieler geprueft wird, ist sichergestellt, dass dieselbe Engine auch ein
 * 4-Spieler-Spiel im Fenster tragen kann: Es genuegt dann, in {@code Main} vier
 * Spieler hinzuzufuegen – {@code GameView} ordnet mehr als zwei Spieler bereits
 * automatisch als Reihe an.</p>
 *
 * <p>Damit der Test schon <i>vor</i> dem Anlegen der Phasenklassen kompiliert,
 * werden die Phasen nicht per {@code import} referenziert, sondern nur ueber ihren
 * einfachen Klassennamen geprueft
 * ({@code game.getCurrentPhase().getClass().getSimpleName()}).</p>
 *
 * @author Claude (Opus 4.8)
 */
public class DurakStatePatternTest {

    /** Erwartete Startkartenanzahl pro Spieler (siehe DurakGameSetup.HAND_SIZE). */
    private static final int HAND_SIZE = 6;

    private Game game;
    private List<Player> players;
    private RecordingListener listener;

    /**
     * GENERIERT von Claude (Opus 4.8).
     *
     * <p>Baut ein frisches Durak mit {@code playerCount} Spielern auf: Deck ueber
     * die Factory, Siegbedingung, Setup (verteilt Haende + liefert Startphase),
     * dann {@code start()}. Danach steht das Spiel im Startzustand und wartet auf
     * eingereichte Commands – genau wie nach dem Oeffnen der GUI.</p>
     *
     * <p>Diese Methode ist bewusst spielerzahl-agnostisch geschrieben: dieselbe
     * Aufbaulogik funktioniert fuer 2, 3 und 4 Spieler. Genau das muss auch deine
     * {@link DurakGameSetup} erfuellen (ueber {@code game.getPlayers()} iterieren,
     * keine feste "2" annehmen).</p>
     *
     * @param playerCount Anzahl der Spieler am Tisch
     */
    private void startGame(int playerCount) {
        game = new Game();
        players = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            Player p = new Player(i, "Spieler " + i);
            players.add(p);
            game.addPlayer(p);
        }

        listener = new RecordingListener();
        game.addGameListener(listener);

        game.initGame(new DurakDeckFactory(), new DurakWinCondition(), new DurakGameSetup());
        game.start();
    }

    /**
     * GENERIERT von Claude (Opus 4.8).
     *
     * <p><b>Aufgabe:</b> {@link DurakGameSetup#distributeInitialHands(Game)} und
     * {@link DurakGameSetup#getStartPhase()} implementieren.</p>
     *
     * <p>Fuer 2, 3 und 4 Spieler muss nach dem Start jeder Spieler
     * {@value #HAND_SIZE} Karten haben, ein aktiver Spieler gesetzt sein und die
     * Startphase die AttackPhase sein.</p>
     *
     * @param playerCount Spielerzahl (2, 3 oder 4)
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void startVerteiltHaendeUndBeginntInAttackPhase(int playerCount) {
        startGame(playerCount);

        for (Player p : players) {
            assertEquals(HAND_SIZE, p.getHand().size(),
                    p.getName() + " muss " + HAND_SIZE + " Startkarten haben");
        }
        assertNotNull(game.getActivePlayer(), "Es muss ein aktiver Spieler (Angreifer) gesetzt sein");

        assertNotNull(game.getCurrentPhase(), "Nach start() darf die Phase nicht null sein");
        assertEquals("AttackPhase", currentPhaseName(), "Startphase muss die AttackPhase sein");
    }

    /**
     * GENERIERT von Claude (Opus 4.8).
     *
     * <p><b>Aufgabe:</b> AttackPhase mit {@code isValid} (erlaubt einen
     * AttackCardCommand des aktiven Spielers) und {@code next} (Karte liegt auf dem
     * Tisch, Verteidiger wird aktiv, Rueckgabe DefendPhase) bauen.</p>
     *
     * <p>Fuer jede Spielerzahl gilt: Nach dem Angriff ist die Karte weg aus der
     * Hand, liegt auf dem Tisch, ein anderer Spieler ist am Zug, Phase ist
     * DefendPhase.</p>
     *
     * @param playerCount Spielerzahl (2, 3 oder 4)
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void angriffLegtKarteUndWechseltInDefendPhase(int playerCount) {
        startGame(playerCount);

        Player attacker = game.getActivePlayer();
        Card angriff = attacker.getHand().getCards().get(0);

        game.submitCommand(new AttackCardCommand(attacker, game.getTable(), angriff));

        assertFalse(attacker.getHand().getCards().contains(angriff), "Karte muss die Hand verlassen haben");
        assertTrue(game.getTable().getCards().contains(angriff), "Karte muss auf dem Tisch liegen");
        assertNotSame(attacker, game.getActivePlayer(), "Jetzt ist der Verteidiger am Zug");
        assertEquals("DefendPhase", currentPhaseName(), "Nach dem Angriff folgt die DefendPhase");
    }

    /**
     * GENERIERT von Claude (Opus 4.8).
     *
     * <p><b>Aufgabe (der 4-Spieler-relevante Teil):</b> Der Verteidiger ist der
     * Spieler <i>links</i> vom Angreifer, also der naechste Sitz im Uhrzeigersinn:
     * {@code players[(angreiferIndex + 1) % spielerzahl]}. In {@code AttackPhase.next}
     * darf also nicht "der andere Spieler" hart verdrahtet werden, sondern es muss
     * modulo der Spielerzahl weitergeschaltet werden (vgl.
     * {@link cardengine.showcase.minigame.MiniDrawPhase}).</p>
     *
     * <p>Dieser Test faellt bei einer 2-Spieler-Verdrahtung nicht auf, bei 4
     * Spielern aber sofort – deshalb ist er der Waechter gegen eine nur zufaellig
     * "fuer zwei" funktionierende Implementierung.</p>
     */
    @Test
    void bei4SpielernVerteidigtDerNaechsteSitz() {
        startGame(4);

        Player attacker = game.getActivePlayer();
        int attackerIndex = players.indexOf(attacker);
        Player erwarteterVerteidiger = players.get((attackerIndex + 1) % players.size());

        Card angriff = attacker.getHand().getCards().get(0);
        game.submitCommand(new AttackCardCommand(attacker, game.getTable(), angriff));

        assertSame(erwarteterVerteidiger, game.getActivePlayer(),
                "Verteidiger muss der naechste Sitz nach dem Angreifer sein");
    }

    /**
     * GENERIERT von Claude (Opus 4.8).
     *
     * <p><b>Aufgabe:</b> Die {@code isValid}-Waechter der Phasen. In der AttackPhase
     * darf kein DefendCardCommand angenommen werden.</p>
     *
     * <p>Ein unpassender Command muss abgelehnt werden: Phase bleibt AttackPhase und
     * der Listener bekommt genau ein {@code onInvalidMove}. So testest du, dass die
     * Regeln wirklich in {@code isValid} sitzen und nicht im Command.</p>
     */
    @Test
    void ungueltigerCommandWirdAbgelehntUndPhaseBleibt() {
        startGame(4);

        Player attacker = game.getActivePlayer();
        Card irgendeine = attacker.getHand().getCards().get(0);

        // Falscher Command-Typ fuer die AttackPhase -> muss abprallen.
        game.submitCommand(new DefendCardCommand(attacker, game.getTable(), irgendeine));

        assertEquals("AttackPhase", currentPhaseName(), "Nach ungueltigem Zug bleibt die Phase unveraendert");
        assertEquals(1, listener.invalidMoves, "Es muss genau ein onInvalidMove gemeldet werden");
        assertTrue(game.getTable().getCards().isEmpty(), "Bei einem ungueltigen Zug darf keine Karte bewegt werden");
    }

    /**
     * GENERIERT von Claude (Opus 4.8).
     *
     * <p>Kleiner Helfer: einfacher Klassenname der aktuellen Phase. Vermeidet einen
     * {@code import} der (noch nicht existierenden) Phasenklassen, damit der Test
     * bereits kompiliert.</p>
     */
    private String currentPhaseName() {
        return game.getCurrentPhase() == null ? "null" : game.getCurrentPhase().getClass().getSimpleName();
    }

    /**
     * GENERIERT von Claude (Opus 4.8).
     *
     * <p>Minimaler {@link GameListener}, der nur mitzaehlt, was die Engine meldet.
     * Zeigt zugleich, wie die GUI angebunden ist: dort aktualisiert
     * {@code onStateChanged} die Ansicht, hier merken wir uns nur die Aufrufe.</p>
     */
    private static final class RecordingListener implements GameListener {
        int stateChanges;
        int invalidMoves;
        Player winner;
        boolean gameOver;

        @Override
        public void onStateChanged(Game game) {
            stateChanges++;
        }

        @Override
        public void onGameOver(Player winner) {
            this.gameOver = true;
            this.winner = winner;
        }

        @Override
        public void onInvalidMove(Command cmd) {
            invalidMoves++;
        }
    }
}

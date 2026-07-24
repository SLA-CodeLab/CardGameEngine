package cardengine.showcase.durak.state;

import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.state.Phase;

/**
 * Ey sorry wollte echt nichts innerhalb von Durak generieren aber ich musste das alles einfach noch fertig bekommen beovr ich weg bin // von Lukas
 *
 * FIX / ERGAENZUNG von Claude (Opus 4.8).
 *
 * <p>Rollen-Rotation fuer Durak an einer zentralen Stelle: wer greift als Naechstes an,
 * wer verteidigt. Der Kern des Problems in den urspruenglichen Phasen war, dass der
 * Angreifer ueber {@code getPreviousPlayer(verteidiger)} (Nachbar-Annahme) abgeleitet
 * wurde und bereits ausgeschiedene Spieler weiter eingeteilt wurden. Beides fuehrte im
 * Endspiel zu festgefahrenen Phasen.</p>
 *
 * <p>Diese Klasse ueberspringt Spieler, die schon raus sind (keine Karten mehr und Deck
 * leer), und bestimmt Angreifer/Verteidiger konsistent aus dem aktiven Spieler heraus.</p>
 *
 * @author Claude (Opus 4.8)
 */
final class DurakTurn {

    private DurakTurn() {
    }

    /** Ein Spieler ist raus, wenn er keine Karten mehr hat und nicht mehr nachziehen kann. */
    static boolean isOut(Game game, Player p) {
        return p != null && p.getHand().isEmpty() && game.getDeck().isEmpty();
    }

    /** Ab {@code from} (inklusive) der erste noch mitspielende Spieler. */
    static Player firstInGame(Game game, Player from) {
        Player p = from;
        for (int i = 0; i < game.getPlayers().size(); i++) {
            if (!isOut(game, p)) return p;
            p = game.getNextPlayer(p);
        }
        return from;
    }

    /** Nach {@code from} (exklusive) der naechste noch mitspielende Spieler. */
    static Player nextInGame(Game game, Player from) {
        Player p = game.getNextPlayer(from);
        for (int i = 0; i < game.getPlayers().size(); i++) {
            if (!isOut(game, p)) return p;
            p = game.getNextPlayer(p);
        }
        return game.getNextPlayer(from);
    }

    /** Vor {@code from} (exklusive) der vorherige noch mitspielende Spieler (= der Angreifer). */
    static Player prevInGame(Game game, Player from) {
        Player p = game.getPreviousPlayer(from);
        for (int i = 0; i < game.getPlayers().size(); i++) {
            if (!isOut(game, p)) return p;
            p = game.getPreviousPlayer(p);
        }
        return game.getPreviousPlayer(from);
    }

    /** Muss ueberhaupt jemand nachziehen? Nur wenn das Deck nicht leer ist und jemand unter 6 Karten hat. */
    static boolean needsRefill(Game game) {
        if (game.getDeck() == null || game.getDeck().isEmpty()) return false;
        for (Player p : game.getPlayers()) {
            if (p.getHand().size() < 6) return true;
        }
        return false;
    }

    /**
     * Startet die naechste Angriffsphase: {@code candidate} (oder der naechste noch
     * spielende Spieler danach) wird Angreifer, der naechste noch spielende Sitz danach
     * wird Verteidiger. Setzt den aktiven Spieler auf den Angreifer.
     */
    static Phase startAttack(Game game, Player candidate) {
        Player attacker = firstInGame(game, candidate);
        game.setActivePlayer(attacker);
        Player defender = nextInGame(game, attacker);
        return new AttackPhase(defender);
    }
}

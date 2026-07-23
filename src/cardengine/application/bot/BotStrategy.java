package cardengine.application.bot;

import cardengine.framework.command.Command;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Strategie eines computergesteuerten Spielers (Strategy-Pattern). Ein Bot
 * entscheidet auf Basis des aktuellen Spielzustands, welchen {@link Command} der
 * angegebene Spieler als Naechstes einreichen soll.</p>
 *
 * <p>Wichtig: Der Bot benutzt exakt denselben Weg wie ein Mensch – er baut einen
 * Command, der anschliessend ueber {@code Game.submitCommand(...)} von der Phase
 * geprueft wird. Der Bot muss die Regeln also nicht kennen; er darf einen gueltigen
 * Zug vorschlagen, die endgueltige Kontrolle bleibt bei {@code Phase.isValid}.</p>
 *
 * @author Claude (Opus 4.8)
 */
public interface BotStrategy {

    /**
     * @param game aktuelles Spiel
     * @param me   Spieler, fuer den entschieden wird (ist gerade am Zug)
     * @return der einzureichende Command, oder {@code null}, wenn kein Zug moeglich ist
     */
    Command decideMove(Game game, Player me);
}

package cardengine.showcase.maumau.strategy;

import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.strategy.WinCondition;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Siegbedingung (Strategy-Pattern) fuer Mau-Mau: Wer zuerst alle Karten
 * abgelegt hat, gewinnt.</p>
 *
 * <p>Als Absicherung gegen ein Verklemmen endet das Spiel zusaetzlich, wenn der
 * Nachziehstapel leer ist (dann gewinnt, wer die wenigsten Karten haelt).
 * Das in echten Regeln uebliche Neumischen des Ablagestapels ist bewusst
 * <b>nicht</b> umgesetzt – das ist fuer den Showcase nicht noetig.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MauMauWinCondition implements WinCondition {

    @Override
    public boolean isGameOver(Game game) {
        // Jemand hat ausgespielt?
        for (Player player : game.getPlayers()) {
            if (player.getHand().isEmpty()) {
                return true;
            }
        }
        // Notbremse: kein Nachziehen mehr moeglich.
        return game.getDeck() != null && game.getDeck().isEmpty();
    }

    @Override
    public Player getWinner(Game game) {
        // Vorrang: der Spieler mit leerer Hand.
        for (Player player : game.getPlayers()) {
            if (player.getHand().isEmpty()) {
                return player;
            }
        }

        // Sonst (Deck leer): der Spieler mit den wenigsten Karten; bei Gleichstand
        // gibt es keinen eindeutigen Gewinner (Unentschieden -> null).
        Player best = null;
        int fewest = Integer.MAX_VALUE;
        boolean tie = false;
        for (Player player : game.getPlayers()) {
            int count = player.getHand().size();
            if (count < fewest) {
                fewest = count;
                best = player;
                tie = false;
            } else if (count == fewest) {
                tie = true;
            }
        }
        return tie ? null : best;
    }
}

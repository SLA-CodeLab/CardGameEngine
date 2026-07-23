package cardengine.showcase.maumau.bot;

import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.application.bot.BotStrategy;
import cardengine.showcase.maumau.command.MauMauDrawCommand;
import cardengine.showcase.maumau.command.PlayCardCommand;

import java.util.List;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Einfacher Mau-Mau-Bot (Strategy-Pattern). Heuristik: die erste passende
 * Handkarte legen (gleiche Farbe oder gleicher Rang wie die oberste Ablagekarte);
 * ist keine spielbar, eine Karte nachziehen.</p>
 *
 * <p>Bewusst schlicht gehalten – der Bot demonstriert, dass ein computergesteuerter
 * Spieler ueber genau dieselben Commands laeuft wie ein Mensch. Klugere Strategien
 * (z.&nbsp;B. Farbe mit den meisten Karten bevorzugen) liessen sich hier leicht
 * ergaenzen.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MauMauBot implements BotStrategy {

    @Override
    public Command decideMove(Game game, Player me) {
        Card top = topOfDiscard(game);
        for (Card card : me.getHand().getCards()) {
            if (top == null || card.getSuit() == top.getSuit() || card.getRank() == top.getRank()) {
                return new PlayCardCommand(me, card, game.getTable());
            }
        }
        if (game.getDeck() != null && !game.getDeck().isEmpty()) {
            return new MauMauDrawCommand(me, game.getDeck());
        }
        return null;
    }

    private Card topOfDiscard(Game game) {
        List<Card> pile = game.getTable().getCards();
        return pile.isEmpty() ? null : pile.get(pile.size() - 1);
    }
}

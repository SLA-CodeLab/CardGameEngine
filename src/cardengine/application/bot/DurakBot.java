package cardengine.application.bot;

import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.core.Suit;
import cardengine.showcase.durak.command.AttackCardCommand;
import cardengine.showcase.durak.command.DefendCardCommand;
import cardengine.showcase.durak.command.EndAttackCommand;
import cardengine.showcase.durak.command.TakeCardCommand;
import cardengine.showcase.durak.factory.DurakDeck;
import cardengine.showcase.durak.state.AttackPhase;
import cardengine.showcase.durak.state.DefendPhase;

import java.util.List;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Einfacher Durak-Bot (Strategy-Pattern). Genau wie {@code MauMauBot} laeuft er ueber
 * dieselben Commands wie ein Mensch – er schlaegt nur einen Zug vor, die endgueltige
 * Kontrolle bleibt bei {@code Phase.isValid}. Heuristik:</p>
 * <ul>
 *   <li><b>AttackPhase</b> (Bot ist Angreifer): erste legal legbare Karte angreifen,
 *       sonst {@code EndAttackCommand} (passen).</li>
 *   <li><b>DefendPhase</b> (Bot ist Verteidiger): erste Karte, die die offene
 *       Angriffskarte schlaegt, sonst {@code TakeCardCommand} (aufnehmen).</li>
 *   <li><b>DrawPhase</b>: nichts – das Nachziehen steuert der Controller.</li>
 * </ul>
 *
 * @author Claude (Opus 4.8)
 */
public class DurakBot implements BotStrategy {

    @Override
    public Command decideMove(Game game, Player me) {
        if (game.getCurrentPhase() instanceof AttackPhase) {
            Card attack = firstLegalAttack(game, me);
            if (attack != null) {
                return new AttackCardCommand(me, game.getTable(), attack);
            }
            return new EndAttackCommand(me, game.getTable()); // nichts (mehr) zu legen -> passen
        }
        if (game.getCurrentPhase() instanceof DefendPhase) {
            Card beat = firstBeatingCard(game, me);
            if (beat != null) {
                return new DefendCardCommand(me, game.getTable(), beat);
            }
            return new TakeCardCommand(me, game.getTable()); // nicht schlagbar -> aufnehmen
        }
        return null; // DrawPhase steuert der Controller
    }

    /** Erste Handkarte, die gelegt werden darf: bei leerem Tisch beliebig, sonst rang-passend. */
    private Card firstLegalAttack(Game game, Player me) {
        List<Card> table = game.getTable().getCards();
        for (Card card : me.getHand().getCards()) {
            if (table.isEmpty() || matchesRankOnTable(card, table)) {
                return card;
            }
        }
        return null;
    }

    private boolean matchesRankOnTable(Card card, List<Card> table) {
        for (Card c : table) {
            if (c.getRank() == card.getRank()) {
                return true;
            }
        }
        return false;
    }

    /** Erste Handkarte, die die offene Angriffskarte schlaegt (gleiche Farbe hoeher oder Trumpf). */
    private Card firstBeatingCard(Game game, Player me) {
        List<Card> table = game.getTable().getCards();
        if (table.isEmpty()) {
            return null;
        }
        Card open = table.get(table.size() - 1);
        Suit trump = ((DurakDeck) game.getDeck()).getTrumpSuit();
        for (Card card : me.getHand().getCards()) {
            if (beats(card, open, trump)) {
                return card;
            }
        }
        return null;
    }

    private boolean beats(Card schlag, Card angriff, Suit trump) {
        if (schlag.getSuit() == angriff.getSuit()) {
            return schlag.getRank().ordinal() > angriff.getRank().ordinal();
        }
        return schlag.getSuit() == trump && angriff.getSuit() != trump;
    }
}

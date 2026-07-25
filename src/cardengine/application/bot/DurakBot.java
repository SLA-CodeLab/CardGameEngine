package cardengine.application.bot;

import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.core.Suit;
import cardengine.framework.state.Phase;
import cardengine.showcase.durak.command.AttackCardCommand;
import cardengine.showcase.durak.command.DefendCardCommand;
import cardengine.showcase.durak.command.EndAttackCommand;
import cardengine.showcase.durak.command.TakeCardCommand;
import cardengine.showcase.durak.command.ThrowInCardCommand;
import cardengine.showcase.durak.factory.DurakDeck;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Einfacher Durak-Bot (Strategy-Pattern). Er laeuft ueber dieselben Commands wie ein
 * Mensch und schlaegt nur einen Zug vor – die endgueltige Kontrolle bleibt bei
 * {@code Phase.isValid}.</p>
 *
 * <p><b>Ueberarbeitet (Claude, Opus 4.8):</b> Der Bot hatte die Regeln vorher
 * <em>nachgebaut</em> (eigene {@code beats()}-Methode, eigener Rangvergleich) – doppelte
 * Logik, die stillschweigend von {@code DefendPhase} abweichen kann. Jetzt baut er
 * Kandidaten-Commands und fragt die Phase, welcher davon erlaubt ist. Zwei Vorteile:</p>
 * <ul>
 *   <li>Keine zweite Regelimplementierung mehr.</li>
 *   <li>Er beherrscht automatisch auch das <b>Zulegen</b> ({@link ThrowInCardCommand}),
 *       sobald die Phasen den Zuleger aktiv schalten.</li>
 * </ul>
 *
 * <p>Heuristik: moeglichst billig spielen – zuerst niedrige Nicht-Trumpf-Karten, Truempfe
 * zuletzt. Kann er nichts legen, passt er bzw. nimmt auf.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class DurakBot implements BotStrategy {

    @Override
    public Command decideMove(Game game, Player me) {
        Phase phase = game.getCurrentPhase();
        if (phase == null || me == null) {
            return null;
        }

        // 1. Eine Karte legen - die billigste, die die Phase akzeptiert.
        for (Card card : cheapestFirst(game, me)) {
            Command cmd = firstValidFor(game, phase, me, card);
            if (cmd != null) {
                return cmd;
            }
        }

        // 2. Sonst: passen (Angriff beenden) oder aufnehmen.
        Command end = new EndAttackCommand(me, game.getTable());
        if (phase.isValid(game, end)) {
            return end;
        }
        Command take = new TakeCardCommand(me, game.getTable());
        if (phase.isValid(game, take)) {
            return take;
        }
        return null;
    }

    /**
     * Baut die moeglichen Commands fuer eine Karte und liefert den ersten, den die Phase
     * erlaubt: angreifen, zulegen oder verteidigen.
     *
     * <p>{@code isValid} prueft nur – die Commands werden hier gebaut, nicht ausgefuehrt.</p>
     */
    private Command firstValidFor(Game game, Phase phase, Player me, Card card) {
        Command attack = new AttackCardCommand(me, game.getTable(), card);
        if (phase.isValid(game, attack)) {
            return attack;
        }
        Command throwIn = new ThrowInCardCommand(me, card, game.getTable());
        if (phase.isValid(game, throwIn)) {
            return throwIn;
        }
        Command defend = new DefendCardCommand(me, game.getTable(), card);
        if (phase.isValid(game, defend)) {
            return defend;
        }
        return null;
    }

    /**
     * Handkarten nach "Wert" sortiert: erst Nicht-Truempfe aufsteigend, dann Truempfe.
     * So gibt der Bot seine starken Karten zuletzt her.
     */
    private List<Card> cheapestFirst(Game game, Player me) {
        Suit trump = trumpOf(game);
        List<Card> cards = new ArrayList<>(me.getHand().getCards());
        cards.sort(Comparator
                .comparingInt((Card c) -> (trump != null && c.getSuit() == trump) ? 1 : 0)
                .thenComparingInt(c -> c.getRank().ordinal()));
        return cards;
    }

    /** @return Trumpffarbe, oder {@code null}, wenn das Deck keine kennt. */
    private Suit trumpOf(Game game) {
        return (game.getDeck() instanceof DurakDeck durakDeck) ? durakDeck.getTrumpSuit() : null;
    }
}

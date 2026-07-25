package cardengine.showcase.maumau.factory;

import cardengine.framework.core.Card;
import cardengine.framework.core.EffectCard;
import cardengine.framework.core.Rank;
import cardengine.framework.core.StandardDeck;
import cardengine.framework.core.Suit;
import cardengine.showcase.maumau.strategy.effect.ChooseSuitEffect;
import cardengine.showcase.maumau.strategy.effect.DrawTwoEffect;
import cardengine.showcase.maumau.strategy.effect.SkipEffect;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Deck fuer den Mau-Mau-Showcase: das klassische 32er-Blatt (Sieben bis Ass in
 * allen vier Farben). Analog zu {@code DurakDeck} wird das Deck bereits im
 * Konstruktor mit Karten gefuellt; das Mischen uebernimmt spaeter
 * {@link StandardDeck#shuffle()} ueber {@code Game.initGame(...)}.</p>
 *
 * <p>ERGAENZUNG von Claude (Fable 5): Die Sonderkarten des Mau-Mau werden hier
 * als {@link EffectCard} mit ihrem jeweiligen {@code Effect} erzeugt – das Deck
 * ist damit die einzige Stelle, die festlegt, <em>welcher Rang welchen Effekt</em>
 * hat: 7 = zwei ziehen, 8 = aussetzen, Bube = Farbwunsch. Jede Karte bekommt ihre
 * eigene Effekt-Instanz (wichtig fuer den Buben, der sich die gewaehlte Farbe
 * kurzzeitig merkt).</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MauMauDeck extends StandardDeck {

    /** Kleinster Rang im Mau-Mau-Blatt (7). Alles ab hier kommt ins Deck. */
    private static final int MIN_RANK_ORDINAL = Rank.SEVEN.ordinal();

    public MauMauDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                if (rank.ordinal() >= MIN_RANK_ORDINAL) {
                    addCard(createCard(suit, rank));
                }
            }
        }
    }

    /**
     *
     * Effect Card mit passendem Effekt, fuer alle anderen Raenge eine normale Card
     * @author: Stanislav
     */
    private Card createCard(Suit suit, Rank rank) {
        return switch (rank) {
            case SEVEN -> new EffectCard(suit, rank, new DrawTwoEffect());
            case EIGHT -> new EffectCard(suit, rank, new SkipEffect());
            case JACK -> new EffectCard(suit, rank, new ChooseSuitEffect());
            default -> new Card(suit, rank);
        };
    }
}

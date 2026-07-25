package cardengine.showcase.maumau.command;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.EffectCard;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.core.Table;

/**
 * GENERIERT von Claude (Opus 4.8).
 *
 * <p>Command (Command-Pattern), das eine Handkarte auf den Ablagestapel legt.
 * Wie im Framework vorgesehen prueft dieser Command <b>keine</b> Regeln – ob die
 * Karte ueberhaupt passt (Farbe/Zahl), entscheidet {@code MauMauPlayPhase.isValid}.
 * Hier wird nur die Karte bewegt und fuer das Undo gemerkt.</p>
 *
 * <p><b>Karteneffekte</b> (ERGAENZUNG von Claude, Fable 5): Ist die gelegte Karte
 * eine {@link EffectCard} (7, 8, Bube), wird ihr {@code Effect} direkt nach der
 * erfolgreichen Kartenbewegung angewendet. Das Ausspielen ist der natuerliche
 * Ausloeser des Effekts – genau dafuer traegt die Karte ihn mit sich.</p>
 *
 * <p>Beim Undo wird – wie beim {@code DrawCardCommand} bewusst entschieden – nur
 * die Kartenbewegung zurueckgenommen, nicht der Effekt: gezogene Strafkarten
 * koennen nicht zurueck ins Deck, ein Aussetzen laesst sich nachtraeglich nicht
 * ungeschehen machen. Einzig der Farbwunsch des Buben erlischt beim Undo von
 * selbst, weil er nur gilt, solange der Bube oben liegt (siehe {@code PlayPhase}).</p>
 *
 * @author Claude (Opus 4.8)
 */
public class PlayCardCommand extends AbstractCommand {

    private final Game game;
    private final Table discardPile;
    private final Card card;
    private boolean moved;

    /**
     * @param player      Spieler, der die Karte legt
     * @param card        die zu legende Handkarte
     * @param discardPile Ablagestapel (der Tisch des Spiels)
     * @param game        das Spiel – wird zum Anwenden von Karteneffekten benoetigt
     */
    public PlayCardCommand(Player player, Card card, Table discardPile, Game game) {
        super(player);
        this.card = card;
        this.discardPile = discardPile;
        this.game = game;
    }

    @Override
    public void execute() {
        // card.flip() deckt die Karte fuer den Ablagestapel auf und liefert sie zurueck.
        moved = getPlayer().getHand().transferCard(card.flip(), discardPile);

        // Effektkarte? Dann wirkt sie jetzt, wo sie oben auf dem Stapel liegt.
        if (moved && card instanceof EffectCard effectCard && effectCard.getAction() != null) {
            effectCard.getAction().apply(game);
        }
    }

    @Override
    public void undo() {
        if (!moved) {
            return;
        }
        discardPile.transferCard(card.flip(), getPlayer().getHand());
        moved = false;
    }

    /** @return die von diesem Command betroffene Karte (fuer die Regelpruefung in der Phase). */
    public Card getCard() {
        return card;
    }
}

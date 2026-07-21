package cardengine.showcase.minigame;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.CardCollection;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.factory.Deck;

/**
 * Command des Minigames: der aktive Spieler zieht eine Karte vom Deck.
 *
 * <p>An das GUI-taugliche Framework angepasst: der Command wird vom Controller/der
 * GUI gebaut und ueber {@code Game.submitCommand(...)} eingereicht. Ausgefuehrt und
 * historisiert wird er zentral von der Engine ({@code commandHistory}); der Zugwechsel
 * selbst liegt in {@link MiniDrawPhase#next(Game)}.</p>
 *
 * <p>Der Command merkt sich beim Ausfuehren den ziehenden Spieler und stellt ihn beim
 * {@link #undo()} wieder als aktiven Spieler her. Da {@code Game.undoLastAction()} nur
 * den Command zuruecknimmt (nicht die Phasen-Transition), bleibt so nach einem Undo der
 * aktive Spieler konsistent. (Siehe Framework-Notiz: Undo deckt Turn-/Phasenwechsel
 * nicht selbst ab.)</p>
 *
 * @author Claude (Opus 4.8)
 */
public class MiniDrawCommand extends AbstractCommand {

    private final Game game;
    private final Deck deck;
    private Card drawnCard;
    private Player previousActivePlayer;

    /**
     * @param game   Referenz auf das Spiel (fuer die Undo-Wiederherstellung)
     * @param player Spieler, der die Karte zieht
     * @param deck   Deck, von dem gezogen wird
     */
    public MiniDrawCommand(Game game, Player player, Deck deck) {
        super(player);
        this.game = game;
        this.deck = deck;
    }

    /**
     * Zieht die oberste Karte auf die Hand des Spielers.
     */
    @Override
    public void execute() {
        if (deck == null) {
            return;
        }
        this.drawnCard = deck.drawCard();
        if (this.drawnCard == null) {
            return;
        }
        getPlayer().getHand().addCard(this.drawnCard);
        // Vor der Phasen-Transition merken, wer gezogen hat (fuer Undo).
        this.previousActivePlayer = game.getActivePlayer();
    }

    /**
     * Macht den Zug rueckgaengig: Karte zurueck aufs Deck, ziehender Spieler wieder aktiv.
     */
    @Override
    public void undo() {
        if (this.drawnCard == null) {
            return;
        }
        getPlayer().getHand().removeCard(this.drawnCard);
        // Deck-Interface bietet kein addCard, daher ueber die CardCollection-Basis.
        ((CardCollection) deck).addCard(this.drawnCard);

        if (this.previousActivePlayer != null) {
            game.setActivePlayer(this.previousActivePlayer);
        }
        this.drawnCard = null;
    }
}

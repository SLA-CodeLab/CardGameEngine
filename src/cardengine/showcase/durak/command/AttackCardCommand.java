package cardengine.showcase.durak.command;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.Player;
import cardengine.framework.core.Table;

/**
 * Command um mit einer Karte einen Spieler zu attackieren bzw dazuzulegen
 *
 * @author Lukas
 */
public class AttackCardCommand extends AbstractCommand {
    private Table table;
    private Card card;
    private boolean moved;

    public AttackCardCommand(Player player, Table table, Card card) {
        super(player);
        this.table = table;
        this.card = card;
    }

    @Override
    public void execute() {
        moved = getPlayer().getHand().transferCard(card.flip(), table);
    }

    @Override
    public void undo() {
        if(!moved) {
            return;
        }
        table.transferCard(card.flip(), getPlayer().getHand());
        moved = false;
    }

    public Card getCard() {
        return card;
    }
}

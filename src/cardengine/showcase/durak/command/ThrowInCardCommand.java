package cardengine.showcase.durak.command;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.Player;
import cardengine.framework.core.Table;

/**
 * Macht genau das gleiche wie AttackCard also ist vielleicht redundant je nachdem wie Phase genau aussehen wird
 * habe ich mich erstmal an Klassendiagramm gehalten
 *
 * @author Lukas
 */
public class ThrowInCardCommand extends AbstractCommand {
    private Table table;
    private Card card;
    private boolean moved;

    public ThrowInCardCommand(Player player, Card card, Table table) {
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
}

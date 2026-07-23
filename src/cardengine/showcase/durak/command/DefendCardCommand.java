package cardengine.showcase.durak.command;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.Player;
import cardengine.framework.core.Table;

/**
 * Command um mit einer Karte eine angreifende Karte zu schlagen
 * Wichtig ist zu wissen das hier nicht Regel geprüft wird das muss Phase isValid machen
 * also hier nur Karte bewegen.
 *
 * @author Lukas
 */
public class DefendCardCommand extends AbstractCommand {
    private Table table;
    private Card card;
    private boolean moved;

    public DefendCardCommand(Player player, Table table, Card card) {
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
        if (!moved) {
            return;
        }
        table.transferCard(card.flip(), getPlayer().getHand());
        moved = false;
    }
}

package cardengine.showcase.durak.command;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.CardCollection;
import cardengine.framework.core.Player;
import cardengine.framework.core.Table;

import java.util.ArrayList;

/**
 * Ist nicht im Klassendiagramm enthalten bisher aber ist zwingend notwenig um den Angreifer passen zu lassen und um die Karten vom Tisch wegzuschmeißen bzw undo auch discard speichern
 * @author Lukas
 */
public class EndAttackCommand extends AbstractCommand {
    private Table table;
    private ArrayList<Card> beaten;

    public EndAttackCommand(Player player, Table table, CardCollection discard) {
        super(player);
        this.table = table;
    }

    @Override public void execute() {
        beaten = new ArrayList<>(table.getCards());
        for (Card card : beaten) table.removeCard(card);
    }
    @Override public void undo() {
        for (Card card : beaten) table.addCard(card);
    }
}

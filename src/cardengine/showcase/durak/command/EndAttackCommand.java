package cardengine.showcase.durak.command;

import cardengine.framework.command.AbstractCommand;
import cardengine.framework.core.Card;
import cardengine.framework.core.Player;
import cardengine.framework.core.Table;

import java.util.ArrayList;

/**
 * Ist nicht im Klassendiagramm enthalten bisher aber ist zwingend notwenig, um den Angreifer passen zu lassen und um die Karten vom Tisch wegzuschmeißen, bzw undo auch discard speichern
 * @author Lukas
 */
public class EndAttackCommand extends AbstractCommand {
    private Table table;
    private ArrayList<Card> beaten;

    public EndAttackCommand(Player player, Table table) {
        super(player);
        this.table = table;
    }

    // todo ZULEGER (analysiert von Claude, Opus 4.8): execute() raeumt den Tisch sofort ab,
    //  d.h. "ich passe" und "Bito / Angriff endgueltig vorbei" sind hier derselbe Command.
    //  Solange das so ist, kann es keinen Zuleger geben: sobald der Angreifer passt, ist der
    //  Tisch leer und niemand kann mehr nachlegen. Noetig waere eine Trennung, z.B. ein
    //  PassCommand (reicht nur weiter, Tisch bleibt liegen) und das Abraeumen erst dann,
    //  wenn alle reihum gepasst haben. Siehe todo in AttackPhase.
    @Override public void execute() {
        beaten = new ArrayList<>(table.getCards());
        for (Card card : beaten) table.removeCard(card);
    }
    @Override public void undo() {
        for (Card card : beaten) table.addCard(card);
    }
}

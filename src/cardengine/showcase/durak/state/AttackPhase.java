package cardengine.showcase.durak.state;

import cardengine.framework.command.Command;
import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.state.Phase;
import cardengine.showcase.durak.command.AttackCardCommand;
import cardengine.showcase.durak.command.EndAttackCommand;
import cardengine.showcase.durak.command.ThrowInCardCommand;

import java.util.List;

public class AttackPhase implements Phase {
    //Eigentlich wollte ich das unbedingt vermeiden weil das bisschen gegen das State Pattern geht wenn Phase Objektvariablen hat
    // aber Durak ist echt besonders damit das der activePlayer die ganze Zeit um den Verteidiger rumwechselt. Da finde ich einfach keine schöne Lösung
    // todo eventuell muss man hier architektur ändern aber würde es erstmal so lassen
    private Player verteidiger;
    public AttackPhase(Player verteidiger) {
        this.verteidiger = verteidiger;
    }

    /**
     * Prüft ob ein Spieler mit einer Karte angreifen kann. Es wird AttackCardCommand bei Angreifer und ThrowInCardCommand bei Zuleger aufgerufen
     * @param game Hauptgameobjekt um Spielerreihenfolge zu bekommen und Karten auf Tisch zu legen bzw zu sehen was offen da liegt
     * @param cmd Das Command das ausgeführt werden soll. Es wird mit Instanceof geguckt welche Aktion ausgeführt wird
     * @return true wenn regelkonform
     * @author Lukas
     */
    @Override
    public boolean isValid(Game game, Command cmd) {
        Player angreifer = game.getActivePlayer();
        if (angreifer == null || cmd == null) {
            return false;
        }

        if (cmd instanceof AttackCardCommand attack) {
            if (attack.getPlayer() != angreifer) {
                return false;
            }
            Card card = attack.getCard();
            if (!angreifer.getHand().getCards().contains(card)) {
                return false;
            }
            return darfLegen(card, game);
        }

        Player zuleger = DurakTurn.nextInGame(game, verteidiger);
        if (cmd instanceof ThrowInCardCommand throwIn) {
            if (throwIn.getPlayer() != zuleger) {
                return false;
            }
            Card card = throwIn.getCard();
            if (!zuleger.getHand().getCards().contains(card)) {
                return false;
            }
            return darfLegen(card, game);
        }

        if(cmd instanceof EndAttackCommand end) {
            if  (end.getPlayer() != angreifer && end.getPlayer() != zuleger) {
                return false;
            }

            return allDefended(game) && !game.getTable().isEmpty();
        }
        return false;
    }

    /**
     * Hilfmethode
     * @param card Karte die gelegt werden soll
     * @param game Gameobjekt für auf dem Tisch liegende Karten iterieren
     * @return true, wenn legen Regelkonform ist
     * @author Lukas
     */
    private boolean darfLegen(Card card, Game game) {
        List<Card> tableCards = game.getTable().getCards();
        if (tableCards.isEmpty()) {
            return true;
        }

        for  (Card c : tableCards) {
            if (c.getRank() == card.getRank()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Bestimmt nächste Phase des Spiels. Wenn alle Karten verteidigt sind geht er in die DrawPhase wenn nicht dann wird der aktive Spieler
     * weitergegeben und der Verteidiger muss die neuen Karten verteidigen
     * @return DrawPhase wenn Table leer oder DefendPhase wenn neue Karten da
     * @author Lukas
     */
    @Override
    public Phase next(Game game) {
        if (allDefended(game)) {
            game.setActivePlayer(verteidiger);
            if (DurakTurn.needsRefill(game)) {
                return new DrawPhase();
            }
            return DurakTurn.startAttack(game, verteidiger);
        }
        game.setActivePlayer(verteidiger);
        return new DefendPhase(verteidiger);
    }


    /**
     * Diese Hilfsfunktion berechnet ob alle Karten verteidigt sind oder nicht.
     *
     * Damit das funktioniert habe ich nur geschaut ob die Anzahl Karten gerade ist oder nicht.
     * Es kann also nicht mehere offene Angriffskarten geben. Es gibt Regeln in Durak wie es mehrere offene Karten gibt aber das ist
     * gefühlt unmöglich vernünftig mit unserem Code ohne krasse Änderungen in der Architektur abzubilden
     *
     * ANNAHME FÜR DIE ZUKUNFT: ES KANN IMMER NUR EINE ANGREIFENDE KARTE GEBEN; ERST SOBALD VERTEIDIGT KANN EINE NEUE GELEGT WERDEN
     *
     * @param game Hauptgameobjekt damit er sich die Anzahl Karten auf Tisch holen kann
     * @return true wenn alle Karten defended sind (eigentlich nur ob gerade Anzahl Karten auf dem Tisch liegen)
     *
     * @author Lukas
     */
    private boolean allDefended(Game game) {
        return (game.getTable().size() % 2) == 0;
    }
}

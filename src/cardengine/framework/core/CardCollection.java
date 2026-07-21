package cardengine.framework.core;

import java.util.ArrayList;
import java.util.List;

public abstract class CardCollection {
    protected List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        if (card != null) {
            cards.add(card);
        }
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    /**
     * Verschiebt eine konkrete Karte aus dieser Collection in eine andere.
     *
     * @author Lukas
     */
    public boolean transferCard(Card card, CardCollection target) {
        if (card == null || target == null) return false;
        if (!cards.remove(card)) return false;
        target.addCard(card);
        return true;
    }
}

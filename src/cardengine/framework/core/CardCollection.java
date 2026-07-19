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

    public Card drawCard() {
        return null;
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean transferCard(Card card, CardCollection targetCollection) {
        return true;
    }
}

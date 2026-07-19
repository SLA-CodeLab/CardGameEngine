package cardengine.framework.core;

public abstract class Card {
    private int id;
    private CardVisibility visibility = CardVisibility.HIDDEN;

    public Card(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public CardVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(CardVisibility visibility) {
        this.visibility = visibility;
    }

    public void flip() {
        if (this.visibility == CardVisibility.VISIBLE) {
            this.visibility = CardVisibility.HIDDEN;
        } else {
            this.visibility = CardVisibility.VISIBLE;
        }
    }

    @Override
    public abstract String toString();
}

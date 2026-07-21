package cardengine.framework.core;

public abstract class Card {
    private CardVisibility visibility = CardVisibility.HIDDEN;

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



}

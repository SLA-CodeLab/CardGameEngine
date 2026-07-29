package cardengine.framework.core;

public class Player {
    private String name;
    private final CardCollection hand = new CardCollection();

    public Player(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public CardCollection getHand() {
        return hand;
    }
}

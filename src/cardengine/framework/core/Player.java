package cardengine.framework.core;

public class Player {
  //todo wofür ID das wird nirgendwo verwendet Code Smell
    private String name;
    private CardCollection hand = new CardCollection();

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

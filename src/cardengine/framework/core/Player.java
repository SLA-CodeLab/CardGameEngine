package cardengine.framework.core;

public class Player {
    private int id; //todo wofür ID das wird nirgendwo verwendet Code Smell
    private String name;
    private Hand hand = new Hand();

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }
}

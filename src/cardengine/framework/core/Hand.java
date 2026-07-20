package cardengine.framework.core;

import java.util.ArrayList;
import java.util.List;

public class Hand extends CardCollection {
    
    public List<Card> getPlayableCards() {
        // die Mehode muss dann in phase mirgieren, da es erst in der bestimmten Phase klar wird
        return new ArrayList<>(cards);
    }
}

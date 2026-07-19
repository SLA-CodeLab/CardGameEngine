package cardengine.framework.core;

import java.util.ArrayList;
import java.util.List;

public class Hand extends CardCollection {
    
    public List<Card> getPlayableCards() {
        // Stub
        return new ArrayList<>(cards);
    }
}

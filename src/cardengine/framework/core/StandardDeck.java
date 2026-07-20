package cardengine.framework.core;

import cardengine.framework.factory.Deck;
import java.util.Collections;

public abstract class StandardDeck extends CardCollection implements Deck {
    
    @Override
    public void shuffle() {
        Collections.shuffle(cards);
    }

    @Override
    public void resetDeck() {
        //eher nicht gebracuht, weil man dann einfach neue deck erstellt. Zu viel Für nichts
    }
}

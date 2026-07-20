package cardengine.framework.core;

public class Table extends CardCollection {

    //ist mir nicht anders eingefallen, als diese Lösung
    public Card getCardById(int id) {
        for(Card card: cards){
            if (card.getId() == (id)){
                return card;
            }
        }
        return null;
    }
}

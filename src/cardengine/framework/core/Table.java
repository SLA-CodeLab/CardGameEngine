package cardengine.framework.core;

public class Table extends CardCollection {

    //ist mir nicht anders eingefallen, als diese Lösung
    //Hab das mal auskommentiert weil wir cardID nicht mehr haben und das Fehler produziert hat. Warum braucht Table eigentlich eine Funktion getCardById?
//    public Card getCardById(int id) {
//        for(Card card: cards){
//            if (card.getId() == (id)){
//                return card;
//            }
//        }
//        return null;
//    }
}

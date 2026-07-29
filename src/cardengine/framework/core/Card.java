package cardengine.framework.core;

public class Card {
    private final Suit suit;
    private final Rank rank;
    private CardVisibility visibility;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        visibility = CardVisibility.HIDDEN;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public CardVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(CardVisibility visibility) {
        this.visibility = visibility;
    }

    /**
     * @author Akim
     *
     * Ich habe die Methode so angepasst das es direkt Card zurück gibt das ist angenehmer damit die Commands zu programmieren
     * Das sollte auch alte Usages nicht kaputt machen da ignorierter Returnwert nicht so deep ist
     * @author Lukas
     * @return Gibt die geflippte Version der Karte wieder
     *
     */
    public Card flip() {
        if (this.visibility == CardVisibility.HIDDEN) {
            setVisible();
        } else {
            setHidden();
        }
        return this;
    }
    private void setVisible(){
        setVisibility(CardVisibility.VISIBLE);
    }
    private void setHidden(){
        setVisibility(CardVisibility.HIDDEN);
    }

    public String toString() {
        return rank + " of " + suit;
    }
}

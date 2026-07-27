package cardengine.framework.core;

public class Card {
    private Suit suit;
    private Rank rank;
    private CardVisibility visibility = CardVisibility.HIDDEN;

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
     *todo ich habe diese Funktion viel benutzt aber ist irgendwie unschön nicht hundert prozent sicher zu sein ob jetzt sichtbar oder nicht
     * vielleicht wäre es besser eine setVisible und setHidden zu machen
     */
    public Card flip() {
        if (this.visibility == CardVisibility.HIDDEN) {
            setVisible();
        } else {
            setHidden();
        }
        return this;
    }
    public Card setVisible(){
        setVisibility(CardVisibility.VISIBLE);
        return this;
    }
    public Card setHidden(){
        setVisibility(CardVisibility.HIDDEN);
        return this;
    }

    public String toString() {
        return rank + " of " + suit;
    }
}

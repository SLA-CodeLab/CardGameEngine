package cardengine.showcase.durak;

import cardengine.framework.core.*;
import cardengine.framework.factory.Deck;
import cardengine.framework.state.Phase;
import cardengine.showcase.durak.factory.DurakDeck;
import cardengine.showcase.durak.state.AttackPhase;

public class DurakGameSetup implements GameSetup {
    private static final int HAND_SIZE = 6;
    private static final int MAX_PLAYERS = 6;

    /**
     * Iteriert über alle Spieler und verteilt jedem 6 Handkarten
     *
     * @author Lukas
     */
    @Override
    public void distributeInitialHands(Game game) {
        Deck deck = game.getDeck();
        if (deck == null) return;
        for (Player p : game.getPlayers()) {
            for (int i = 0; i < HAND_SIZE; i++) {
                Card c = deck.drawCard();
                if (c != null) p.getHand().addCard(c);
            }
        }
    }

    /**
     * @author Stanislav
     */
    //Hilfsmethode zum Wissen, wer startet
    @Override
    public void assignFirstPlayer(Game game) {
        Suit trumpSuit = ((DurakDeck) game.getDeck()).getTrumpSuit(); // von Lukas hinzugefügt //todo code smell?
        Player lowestTrumpOwner = null;
        Rank lowestTrumpRank = null;

        // alle Spieler scannen
        for (Player player : game.getPlayers()){

            //eine Hand des Spielers erhalten refactored HAND = CardCollection
            CardCollection hand = player.getHand();

            // alle Cards scannen
            for (Card card : hand.getCards()) {
                //nach trumpf suchen
                if (card.getSuit() == trumpSuit) {

                    // Hilfsmittel: GEMINI 3.1 PRO Ansatz Start
                    if (lowestTrumpOwner == null || card.getRank().ordinal() < lowestTrumpRank.ordinal()) {
                    // Hilfsmittel: GEMINI 3.1 PRO Ansatz Ende

                        lowestTrumpRank = card.getRank();
                        lowestTrumpOwner = player;
                    }
                }
            }
        }

        // Falls niemand einen Trumpf besityt, dann startet einfach der erste Spieler in der Liste
        Player startingPlayer;

        if (lowestTrumpOwner != null) {
            startingPlayer = lowestTrumpOwner;
        } else {
            startingPlayer = game.getPlayers().get(0);
        }
        //zuweisen
        game.setActivePlayer(startingPlayer);
    }

    /**
     * Die Startphase wird dann im Framework aus GameSetup geholt. Startphase bei Durak ist Angriffsphase.
     * Für die Phasen wird immer der Verteidiger gemerkt damit zwischen Angreifer und Zuleger gewechselt werden kann.
     * @param game liefert Startspieler über activePlayer
     * @author Lukas
     */
    @Override
    public Phase getStartPhase(Game game) {
        Player verteidiger = game.getNextPlayer(game.getActivePlayer());
        return new AttackPhase(verteidiger);
    }
    public void validateNumberOfPlayers(Game game) {
        if (game.getPlayers().size() > MAX_PLAYERS) {
            throw new IllegalArgumentException("Too many players");
        }
        if (game.getPlayers().size() < 2) {
            throw new IllegalArgumentException("Not enough players");
        }
    }
}

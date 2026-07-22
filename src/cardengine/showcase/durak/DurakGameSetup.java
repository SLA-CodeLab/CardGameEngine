package cardengine.showcase.durak;

import cardengine.framework.core.*;
import cardengine.framework.factory.Deck;
import cardengine.framework.state.Phase;
import cardengine.showcase.durak.factory.DurakDeck;

public class DurakGameSetup implements GameSetup {
    private static final int HAND_SIZE = 6;

    /**
     * @author Stanislav
     */
    @Override
    public void distributeInitialHands(Game game) {
        //todo
    }

    /**
     * @author Stanislav
     */
    //Hilfsmethode zum Wissen, wer startet
    private void assignFirstPlayer(Game game, Suit trumpSuit) {
        Player lowestTrumpOwner = null;
        Rank lowestTrumpRank = null;

        // alle Spieler scannen
        for (Player player : game.getPlayers()){

            //eine Hand des Spielers erhalten
            Hand hand = player.getHand();

            // alle Cards scannen
            for (Card card : hand.getCards()) {
                SimpleCard simpleCard = (SimpleCard) card;
                //nach trumpf suchen
                if (simpleCard.getSuit() == trumpSuit) {

                    // Hilfsmittel: GEMINI 3.1 PRO Ansatz Start
                    if (lowestTrumpOwner == null || simpleCard.getRank().ordinal() < lowestTrumpRank.ordinal()) {
                    // Hilfsmittel: GEMINI 3.1 PRO Ansatz Ende

                        lowestTrumpRank = simpleCard.getRank();
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
     * @author Stanislav
     */
    @Override
    public Phase getStartPhase() {
        return null;
    }
}

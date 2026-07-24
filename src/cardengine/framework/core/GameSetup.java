package cardengine.framework.core;

import cardengine.framework.state.Phase;

public interface GameSetup {
    void distributeInitialHands(Game game);
    void assignFirstPlayer(Game game);
    Phase getStartPhase(Game game);
    }

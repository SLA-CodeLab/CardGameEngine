package cardengine.framework.core;

import cardengine.framework.state.Phase;

public interface GameSetup {
        void distributeInitialHands(Game game);
        Phase getStartPhase();
    }

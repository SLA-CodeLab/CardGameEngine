package cardengine.showcase.minigame;

import cardengine.framework.command.Command;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.observer.GameListener;

/**
 * Konsolen-Runner des Minigames.
 *
 * <p>Demonstriert den neuen, GUI-tauglichen Ablauf des Frameworks headless:
 * {@code initGame()} -> {@code start()} -> wiederholtes {@code submitCommand(...)}.
 * Der Zustand wird ueber einen {@link GameListener} ausgegeben, nicht mehr durch
 * {@code System.out} in Command/Phase. Damit ist dieselbe Engine, die auch die
 * Swing-GUI treibt, ohne Fenster testbar.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("--- ENGINE START ---");
        Game game = new Game();

        game.addPlayer(new Player(1, "Alice"));
        game.addPlayer(new Player(2, "Bob"));

        // Zustandsausgabe rein ueber den Observer.
        game.addGameListener(new GameListener() {
            @Override
            public void onStateChanged(Game g) {
                StringBuilder sb = new StringBuilder("[Zustand] ");
                for (Player p : g.getPlayers()) {
                    sb.append(p.getName()).append("=").append(p.getHand().getCards().size()).append("  ");
                }
                sb.append("| Deck=").append(g.getDeck() != null ? g.getDeck().getDeckSize() : 0);
                if (g.getCurrentPhase() != null) {
                    sb.append(" | am Zug: ").append(g.getActivePlayer().getName());
                }
                System.out.println(sb);
            }

            @Override
            public void onGameOver(Player winner) {
                System.out.println("[GameOver] Gewinner: "
                        + (winner != null ? winner.getName() : "unentschieden"));
            }

            @Override
            public void onInvalidMove(Command cmd) {
                System.out.println("[Ungültig] Zug nicht möglich.");
            }
        });

        game.initGame(new MiniFactory(), new MiniWinCondition(), new MiniSetup());
        game.start();

        // "Auto-Play": solange eine Phase aktiv ist, zieht der aktive Spieler.
        int safety = 0;
        while (game.getCurrentPhase() != null && safety++ < 100) {
            Player active = game.getActivePlayer();
            game.submitCommand(new MiniDrawCommand(game, active, game.getDeck()));
        }

        System.out.println("--- ENGINE BEENDET ---");
        Player winner = game.getWinner();
        if (winner != null) {
            System.out.println("Gewinner: " + winner.getName()
                    + " mit " + winner.getHand().getCards().size() + " Karten!");
        } else {
            System.out.println("Das Spiel endete unentschieden!");
        }
    }
}

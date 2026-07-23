package cardengine.application.ui;

import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Haupt-Fenster (View) der Swing-GUI im Kartenspiel-Look (Durak-Stil).
 *
 * <p>Zeigt einen gruenen Spieltisch ({@link FeltPanel}) mit aufgefaecherten Haenden
 * ({@link PlayerPanel}) rund um den Nachziehstapel ({@link DeckPanel}), darueber eine
 * Titel-/Statusleiste und darunter die Bedienelemente samt Log. Die View kennt keine
 * Spielregeln: sie stellt den {@link Game}-Zustand dar ({@link #render(Game)}) und meldet
 * Klicks ueber {@link #setDrawAction(ActionListener)} / {@link #setUndoAction(ActionListener)}
 * an den Controller.</p>
 *
 * @author Claude (Opus 4.8)
 */
public class GameView extends JFrame {

    private static final Color BAR_BG = new Color(0x14321F);
    private static final Color ACCENT = new Color(0xE0C067);

    private final JLabel statusLabel = new JLabel();
    private final JButton drawButton = new JButton("Karte ziehen");
    private final JButton undoButton = new JButton("Rückgängig");
    private final JTextArea logArea = new JTextArea(6, 30);

    private final List<PlayerPanel> playerPanels = new ArrayList<>();
    private final DeckPanel deckPanel = new DeckPanel();
    private final DiscardPanel discardPanel = new DiscardPanel();
    private final List<Player> players;
    private final String gameTitle;

    /**
     * @param players   Spieler, fuer die je ein {@link PlayerPanel} angelegt wird
     * @param gameTitle Name des Spiels (Fenstertitel und Kopfzeile), z.&nbsp;B. "Mau-Mau"
     */
    public GameView(List<Player> players, String gameTitle) {
        super("CardGameEngine – " + gameTitle);
        this.players = players;
        this.gameTitle = gameTitle;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildTitleBar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildControls(), BorderLayout.SOUTH);

        log("Willkommen! Klicke 'Karte ziehen', um zu ziehen.");
        setMinimumSize(new Dimension(560, 640));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BAR_BG);
        bar.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        JLabel title = new JLabel("♠ ♥ " + gameTitle + " ♦ ♣");
        title.setForeground(ACCENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        bar.add(title, BorderLayout.WEST);

        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bar.add(statusLabel, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildTable() {
        FeltPanel felt = new FeltPanel();
        felt.setLayout(new BorderLayout());
        felt.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Player p : players) {
            playerPanels.add(new PlayerPanel(p));
        }

        // Mittelbereich mit Nachziehstapel (links) und Ablagestapel (rechts).
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        center.setOpaque(false);
        center.add(deckPanel);
        center.add(discardPanel);

        if (players.size() == 2) {
            // Klassische Sitzordnung: Gegner oben, eigener Spieler unten.
            felt.add(playerPanels.get(1), BorderLayout.NORTH);
            felt.add(center, BorderLayout.CENTER);
            felt.add(playerPanels.get(0), BorderLayout.SOUTH);
        } else {
            felt.add(center, BorderLayout.NORTH);
            JPanel row = new JPanel(new GridLayout(1, players.size(), 8, 8));
            row.setOpaque(false);
            for (PlayerPanel pp : playerPanels) {
                row.add(pp);
            }
            felt.add(row, BorderLayout.SOUTH);
        }
        return felt;
    }

    private JPanel buildControls() {
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(BAR_BG);
        south.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        buttons.setOpaque(false);
        styleButton(drawButton, new Color(0x2E7D45));
        styleButton(undoButton, new Color(0x5A5A5A));
        undoButton.setEnabled(false);
        buttons.add(drawButton);
        buttons.add(undoButton);
        south.add(buttons, BorderLayout.NORTH);

        logArea.setEditable(false);
        logArea.setBackground(new Color(0x0F2417));
        logArea.setForeground(new Color(0xC8E6C9));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        south.add(new JScrollPane(logArea), BorderLayout.CENTER);
        return south;
    }

    private void styleButton(JButton b, Color bg) {
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setOpaque(true);
    }

    /** Registriert den Handler fuer den „Karte ziehen"-Button. */
    public void setDrawAction(ActionListener listener) {
        for (ActionListener al : drawButton.getActionListeners()) {
            drawButton.removeActionListener(al);
        }
        drawButton.addActionListener(listener);
    }

    /** Registriert den Handler fuer den „Rückgängig"-Button. */
    public void setUndoAction(ActionListener listener) {
        for (ActionListener al : undoButton.getActionListeners()) {
            undoButton.removeActionListener(al);
        }
        undoButton.addActionListener(listener);
    }

    /**
     * GENERIERT von Claude (Opus 4.8).
     *
     * <p>Registriert den Handler, der beim Klick auf eine Handkarte des aktiven
     * Spielers mit genau dieser Karte aufgerufen wird (fuer "Karte spielen", z.&nbsp;B.
     * in Mau-Mau). Spiele ohne Kartenauswahl (Minigame) setzen diesen Handler
     * einfach nicht.</p>
     *
     * @param listener Empfaenger der angeklickten Karte
     */
    public void setCardClickAction(Consumer<Card> listener) {
        for (PlayerPanel pp : playerPanels) {
            pp.setCardClickListener(listener);
        }
    }

    /**
     * GENERIERT von Claude (Opus 4.8).
     *
     * <p>Beschriftet den Aktions-Button um (Standard: „Karte ziehen"), damit die
     * gleiche View fuer verschiedene Spiele passende Begriffe zeigen kann.</p>
     *
     * @param text neue Button-Beschriftung
     */
    public void setDrawButtonText(String text) {
        drawButton.setText(text);
    }

    /**
     * Zeichnet den kompletten Spielzustand neu.
     *
     * @param game aktuelles Spiel
     */
    public void render(Game game) {
        boolean running = game.getCurrentPhase() != null;
        Player active = game.getActivePlayer();

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            playerPanels.get(i).update(p, running && p == active);
        }

        int deckSize = game.getDeck() != null ? game.getDeck().getDeckSize() : 0;
        deckPanel.setDeckSize(deckSize);

        // Ablagestapel (Tisch): oberste Karte + Anzahl.
        List<Card> pile = game.getTable().getCards();
        Card topDiscard = pile.isEmpty() ? null : pile.get(pile.size() - 1);
        discardPanel.setTop(topDiscard, pile.size());

        if (running) {
            statusLabel.setText(active.getName() + " ist am Zug");
        }

        drawButton.setEnabled(running && deckSize > 0);
        undoButton.setEnabled(running && game.canUndo());
    }

    /**
     * Beendet die Darstellung und meldet den Gewinner.
     *
     * @param winner Gewinner oder {@code null} bei Unentschieden
     */
    public void showGameOver(Player winner) {
        drawButton.setEnabled(false);
        undoButton.setEnabled(false);

        String msg = (winner != null)
                ? "Gewinner: " + winner.getName() + " mit "
                  + winner.getHand().getCards().size() + " Karten!"
                : "Unentschieden!";
        statusLabel.setText("Spiel vorbei – " + msg);
        log(msg);
        // Nach dem finalen Neuzeichnen anzeigen, damit der Tisch zuerst aktuell ist.
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, msg, "Spielende", JOptionPane.INFORMATION_MESSAGE));
    }

    /**
     * Haengt eine Zeile an das Log-Fenster.
     *
     * @param line auszugebende Zeile
     */
    public void log(String line) {
        logArea.append(line + System.lineSeparator());
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}

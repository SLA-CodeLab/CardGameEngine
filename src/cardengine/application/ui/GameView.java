package cardengine.application.ui;

import cardengine.framework.core.Card;
import cardengine.framework.core.Game;
import cardengine.framework.core.Player;
import cardengine.framework.core.Suit;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Haupt-Fenster (View) der Swing-GUI im Kartenspiel-Look (Durak-Stil).
 *
 * <p>Zeigt einen gruenen Spieltisch ({@link FeltPanel}) mit den Spielern rund um die
 * Tischmitte, darueber eine Titel-/Statusleiste und darunter die Bedienelemente samt Log.
 * Die View kennt keine Spielregeln: sie stellt den {@link Game}-Zustand dar
 * ({@link #render(Game)}) und meldet Klicks an den Controller.</p>
 *
 * <p><b>Ergaenzungen (Claude, Opus 4.8):</b></p>
 * <ul>
 *   <li><b>Sitzordnung im Kreis:</b> Die Spieler sitzen im Uhrzeigersinn um den Tisch
 *       (unten/links/oben/rechts) statt nebeneinander in einer Reihe. Der eigene Spieler
 *       sitzt immer unten ({@link #setLocalPlayer(Player)}).</li>
 *   <li><b>Austauschbare Tischmitte:</b> Welche {@link TablePanel}-Ansicht in der Mitte
 *       liegt, gibt das jeweilige Spiel vor – Ablagestapel bei Mau-Mau, Angriffs-/
 *       Verteidigungspaare bei Durak.</li>
 *   <li><b>Klare Zustaendigkeit:</b> {@link #render(Game)} zeichnet nur noch den Zustand.
 *       Ob der Aktionsknopf anklickbar ist, entscheidet der Controller ueber
 *       {@link #setActionEnabled(boolean)} – vorher hat die View das mit
 *       "Deck nicht leer" ueberschrieben und der Controller hat direkt danach korrigiert.</li>
 * </ul>
 *
 * @author Claude (Opus 4.8)
 */
public class GameView extends JFrame {

    private static final Color BAR_BG = new Color(0x14321F);
    private static final Color ACCENT = new Color(0xE0C067);

    private final JLabel statusLabel = new JLabel();
    private final JButton actionButton = new JButton("Karte ziehen");
    private final JButton undoButton = new JButton("Rückgängig");
    private final JTextArea logArea = new JTextArea(6, 30);

    private final List<Player> players;
    private final String gameTitle;

    /** Spieler -> Sitzflaeche. LinkedHashMap, damit die Sitzreihenfolge stabil bleibt. */
    private final Map<Player, PlayerPanel> playerPanels = new LinkedHashMap<>();

    private final DeckPanel deckPanel = new DeckPanel();
    private final TablePanel tablePanel;

    /** Sitzflaechen-Container der vier Himmelsrichtungen. */
    private final JPanel seatNorth = transparentRow();
    private final JPanel seatSouth = transparentRow();
    private final JPanel seatWest = transparentRow();
    private final JPanel seatEast = transparentRow();

    /** Spieler, dessen Hand offen liegt und der unten sitzt. */
    private Player localPlayer;

    /** Trumpfinformationen fuer Deckanzeige und Handsortierung (Durak). */
    private Card trumpCard;
    private Suit trumpSuit;

    /** Vom Controller gesetzter Statustext; {@code null} = Standardtext. */
    private String statusOverride;

    /**
     * @param players   Spieler, fuer die je ein {@link PlayerPanel} angelegt wird
     * @param gameTitle Name des Spiels (Fenstertitel und Kopfzeile), z.&nbsp;B. "Mau-Mau"
     */
    public GameView(List<Player> players, String gameTitle) {
        this(players, gameTitle, new DiscardPanel());
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * @param players    Spieler, fuer die je ein {@link PlayerPanel} angelegt wird
     * @param gameTitle  Name des Spiels
     * @param tablePanel Ansicht der Tischmitte (Ablagestapel oder Durak-Paare)
     */
    public GameView(List<Player> players, String gameTitle, TablePanel tablePanel) {
        super("CardGameEngine – " + gameTitle);
        this.players = players;
        this.gameTitle = gameTitle;
        this.tablePanel = tablePanel;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        for (Player p : players) {
            playerPanels.put(p, new PlayerPanel(p));
        }
        if (!players.isEmpty()) {
            localPlayer = players.get(0);
        }

        add(buildTitleBar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildControls(), BorderLayout.SOUTH);
        rebuildSeats();

        log("Willkommen bei " + gameTitle + "!");
        setMinimumSize(new Dimension(900, 720));
        pack();
        setLocationRelativeTo(null);
    }

    private static JPanel transparentRow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        p.setOpaque(false);
        return p;
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
        felt.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // Mitte: Nachziehstapel (mit Trumpf) links, Spielflaeche rechts daneben.
        JPanel center = new JPanel(new BorderLayout(18, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        center.add(deckPanel, BorderLayout.WEST);
        center.add(tablePanel, BorderLayout.CENTER);

        felt.add(seatNorth, BorderLayout.NORTH);
        felt.add(seatWest, BorderLayout.WEST);
        felt.add(center, BorderLayout.CENTER);
        felt.add(seatEast, BorderLayout.EAST);
        felt.add(seatSouth, BorderLayout.SOUTH);
        return felt;
    }

    private JPanel buildControls() {
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(BAR_BG);
        south.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        buttons.setOpaque(false);
        styleButton(actionButton, new Color(0x2E7D45));
        styleButton(undoButton, new Color(0x5A5A5A));
        setButtonEnabled(undoButton, false);
        buttons.add(actionButton);
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
        b.putClientProperty("activeBg", bg);
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Ein {@code setOpaque(true)}-Button behaelt seine kraeftige Hintergrundfarbe auch
     * im deaktivierten Zustand und sieht dadurch klickbar aus. Deshalb wird die Farbe hier
     * mitgeschaltet.</p>
     */
    private void setButtonEnabled(JButton b, boolean enabled) {
        b.setEnabled(enabled);
        Color active = (Color) b.getClientProperty("activeBg");
        b.setBackground(enabled ? active : new Color(0x3A4A40));
        b.setForeground(enabled ? Color.WHITE : new Color(0x8A9A90));
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Setzt den eigenen Spieler: Seine Hand liegt offen, alle anderen werden verdeckt
     * gezeichnet, und er sitzt unten am Tisch. Die uebrigen Spieler werden von dort aus im
     * Uhrzeigersinn verteilt (unten &rarr; links &rarr; oben &rarr; rechts).</p>
     *
     * @param localPlayer eigener Spieler; {@code null} deckt alle Haende auf (Debug/Zuschauer)
     */
    public void setLocalPlayer(Player localPlayer) {
        this.localPlayer = localPlayer;
        rebuildSeats();
    }

    /**
     * Verteilt die Spielerflaechen im Kreis um den Tisch. Der eigene Spieler sitzt unten,
     * die anderen folgen im Uhrzeigersinn. Bei mehr als vier Spielern reihen sich die
     * ueberzaehligen oben ein.
     */
    private void rebuildSeats() {
        seatNorth.removeAll();
        seatSouth.removeAll();
        seatWest.removeAll();
        seatEast.removeAll();

        int n = players.size();
        if (n == 0) {
            return;
        }
        int localIndex = Math.max(0, players.indexOf(localPlayer));
        PlayerPanel.Seat[] ring = ringFor(n);

        // Mehr Spieler als Sitzplaetze: alle Gegner oben nebeneinander.
        if (ring == null) {
            seatNorth.setLayout(new GridLayout(1, n - 1, 8, 8));
        } else {
            seatNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));
        }

        for (int i = 0; i < n; i++) {
            Player p = players.get(i);
            PlayerPanel panel = playerPanels.get(p);
            int offset = (i - localIndex + n) % n;

            PlayerPanel.Seat seat;
            if (ring != null) {
                seat = ring[offset];
            } else {
                seat = offset == 0 ? PlayerPanel.Seat.BOTTOM : PlayerPanel.Seat.TOP;
            }
            panel.setSeat(seat);
            // Nur die eigene Hand liegt offen. localPlayer == null -> alles offen (Debug).
            panel.setFaceUp(localPlayer == null || p == localPlayer);
            panel.setTrumpSuit(trumpSuit);
            containerFor(seat).add(panel);
        }

        seatNorth.revalidate();
        seatSouth.revalidate();
        seatWest.revalidate();
        seatEast.revalidate();
        repaint();
    }

    /** @return Sitzreihenfolge im Uhrzeigersinn ab "unten", oder {@code null} bei &gt;4 Spielern. */
    private PlayerPanel.Seat[] ringFor(int playerCount) {
        switch (playerCount) {
            case 1:
                return new PlayerPanel.Seat[]{PlayerPanel.Seat.BOTTOM};
            case 2:
                return new PlayerPanel.Seat[]{PlayerPanel.Seat.BOTTOM, PlayerPanel.Seat.TOP};
            case 3:
                return new PlayerPanel.Seat[]{
                        PlayerPanel.Seat.BOTTOM, PlayerPanel.Seat.LEFT, PlayerPanel.Seat.TOP};
            case 4:
                return new PlayerPanel.Seat[]{
                        PlayerPanel.Seat.BOTTOM, PlayerPanel.Seat.LEFT,
                        PlayerPanel.Seat.TOP, PlayerPanel.Seat.RIGHT};
            default:
                return null;
        }
    }

    private JPanel containerFor(PlayerPanel.Seat seat) {
        switch (seat) {
            case TOP:
                return seatNorth;
            case LEFT:
                return seatWest;
            case RIGHT:
                return seatEast;
            case BOTTOM:
            default:
                return seatSouth;
        }
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Setzt die Trumpfinformation: Die Karte liegt quer unter dem Nachziehstapel, die
     * Farbe steuert zusaetzlich die Sortierung der Handkarten.</p>
     *
     * @param trumpCard offen liegende Trumpfkarte, oder {@code null} (schon gezogen)
     * @param trumpSuit Trumpffarbe, oder {@code null} bei Spielen ohne Trumpf
     */
    public void setTrump(Card trumpCard, Suit trumpSuit) {
        this.trumpCard = trumpCard;
        this.trumpSuit = trumpSuit;
        for (PlayerPanel panel : playerPanels.values()) {
            panel.setTrumpSuit(trumpSuit);
        }
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Markiert die Karten, die der eigene Spieler jetzt legen darf. Welche das sind,
     * entscheidet die {@code Phase} ueber {@code isValid} – die View bekommt nur das
     * Ergebnis und hebt es hervor.</p>
     *
     * @param playable legbare Karten, oder {@code null} fuer "kein Highlighting"
     */
    public void setPlayableCards(Set<Card> playable) {
        for (Map.Entry<Player, PlayerPanel> e : playerPanels.entrySet()) {
            e.getValue().setPlayableCards(e.getKey() == localPlayer ? playable : null);
        }
    }

    /** Beschriftet einen Sitz mit seiner Rolle im aktuellen Zug (z.&nbsp;B. "Angreifer"). */
    public void setRoleLabel(Player player, String role) {
        PlayerPanel panel = playerPanels.get(player);
        if (panel != null) {
            panel.setRoleLabel(role);
        }
    }

    /** Entfernt alle Rollenbeschriftungen. */
    public void clearRoleLabels() {
        for (PlayerPanel panel : playerPanels.values()) {
            panel.setRoleLabel(null);
        }
    }

    /** Registriert den Handler fuer den Aktions-Button. */
    public void setDrawAction(ActionListener listener) {
        for (ActionListener al : actionButton.getActionListeners()) {
            actionButton.removeActionListener(al);
        }
        actionButton.addActionListener(listener);
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
     * Spielers mit genau dieser Karte aufgerufen wird.</p>
     *
     * @param listener Empfaenger der angeklickten Karte
     */
    public void setCardClickAction(Consumer<Card> listener) {
        for (PlayerPanel pp : playerPanels.values()) {
            pp.setCardClickListener(listener);
        }
    }

    /** Beschriftet den Aktions-Button um (Standard: „Karte ziehen"). */
    public void setDrawButtonText(String text) {
        actionButton.setText(text);
    }

    /**
     * Aktiviert bzw. deaktiviert den Aktions-Button. Seit der Umstellung entscheidet das
     * ausschliesslich der Controller – die View ueberschreibt es nicht mehr.
     *
     * @param enabled true, wenn der Aktions-Button anklickbar sein soll
     */
    public void setActionEnabled(boolean enabled) {
        setButtonEnabled(actionButton, enabled);
    }

    /** Setzt einen eigenen Statustext (z.&nbsp;B. mit Phase/Rolle); {@code null} = Standard. */
    public void setStatus(String status) {
        this.statusOverride = status;
        statusLabel.setText(status != null ? status : statusLabel.getText());
    }

    /**
     * Zeichnet den kompletten Spielzustand neu.
     *
     * @param game aktuelles Spiel
     */
    public void render(Game game) {
        boolean running = game.getCurrentPhase() != null;
        Player active = game.getActivePlayer();

        for (Map.Entry<Player, PlayerPanel> e : playerPanels.entrySet()) {
            Player p = e.getKey();
            e.getValue().update(p, running && p == active);
        }

        int deckSize = game.getDeck() != null ? game.getDeck().getDeckSize() : 0;
        deckPanel.setDeck(deckSize, trumpCard, trumpSuit);
        tablePanel.setCards(game.getTable().getCards());

        if (statusOverride != null) {
            statusLabel.setText(statusOverride);
        } else if (running && active != null) {
            statusLabel.setText(active.getName() + " ist am Zug");
        }

        // Undo ist eine generische Faehigkeit des Frameworks -> darf die View selbst pruefen.
        setButtonEnabled(undoButton, running && game.canUndo());
    }

    /**
     * Beendet die Darstellung und meldet den Gewinner.
     *
     * @param winner Gewinner oder {@code null} bei Unentschieden
     */
    public void showGameOver(Player winner) {
        String msg = (winner != null)
                ? "Gewinner: " + winner.getName() + "!"
                : "Unentschieden!";
        showGameOver(msg);
    }

    /**
     * ERGAENZUNG von Claude (Opus 4.8).
     *
     * <p>Variante mit fertigem Text – Durak meldet den <em>Verlierer</em> ("Durak"), nicht
     * einen Gewinner, deshalb bildet der Controller den Satz selbst.</p>
     *
     * @param message anzuzeigender Schlusstext
     */
    public void showGameOver(String message) {
        setButtonEnabled(actionButton, false);
        setButtonEnabled(undoButton, false);

        statusLabel.setText("Spiel vorbei");
        log(message);
        // Nach dem finalen Neuzeichnen anzeigen, damit der Tisch zuerst aktuell ist.
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, "Spielende", JOptionPane.INFORMATION_MESSAGE));
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

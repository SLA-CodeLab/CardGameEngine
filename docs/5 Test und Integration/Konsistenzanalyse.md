# Konsistenzanalyse CardGameEngine

> Stand: 2026-07-21. Momentaufnahme des Codes nach dem Event-/GUI-Umbau
> (commit „Game.java auf EventListener angepasst"). Dient als Grundlage für
> die zweite Iteration (Grobdesign 2 / Feindesign 2).

## Gesamtbefund

Das Framework ist im Kern funktionsfähig – der `minigame`-Showcase läuft sauber
durch Engine → Command → Phase → Observer → GUI. Die Inkonsistenzen entstehen
durch drei überlagerte „Zeitschichten":

1. **Schicht A (ältester Entwurf):** Schleifen-`GameLoop`, `aktionDurchfuehren()`,
   `getCardById` – orientiert am Aktivitätsdiagramm.
2. **Schicht B (Pattern-Ausbau):** geplante Durak/Poker/MauMau-Strukturen
   (`Effect`, `Table.getCard`, `transferCard`, leere dummy-Packages).
3. **Schicht C (Event/GUI-Umbau, jüngster commit):** `submitCommand`/`start`/
   `GameListener` – ersetzt Schicht A.

Reste von A und B wurden nie aufgeräumt und erzeugen fast alle „verwaisten
Funktionen".

Autoren (git shortlog): Lukas (18), Stanislav (11), Akim (6).

## 1. Verwaiste / ungenutzte Funktionen

| Element | Ort | Herkunft / Problem |
|---|---|---|
| `Game.executeCommand(Command)` | Game.java | Aus altem UML-Entwurf; durch `submitCommand()` ersetzt. Umgeht Validierung, Phasenwechsel, Win-Check, Observer. Toter Doppelpfad. |
| `EffectCard` + `Effect` | core/EffectCard.java, strategy/Effect.java | Angefangenes Strategy-Pattern für Karteneffekte (MauMau). Nirgends instanziiert. Halbfertig. (Wird evtl. für Durak-Showcase noch gebraucht → vorerst behalten.) |
| `EffectCard(int id, …)` | EffectCard.java | `id`-Parameter wurde weggeworfen – Relikt der entfernten Card-ID. **(entfernt am 2026-07-21)** |
| `Hand.getPlayableCards()` | core/Hand.java | Stub; Kommentar: „muss dann in phase migrieren". Nie aufgerufen. |
| `CardCollection.transferCard(...)` | core/CardCollection.java | Für Durak-Züge (Hand→Tisch). Nie genutzt. |
| `Card.getVisibility/setVisibility/flip` + Enum `CardVisibility` | core/Card.java | Sichtbarkeits-Mechanismus nirgends verwendet. |
| `DeckFactory.getDeckSize()` | factory/DeckFactory.java | Nie aufgerufen; Konzeptdopplung mit `Deck.getDeckSize()`. |
| `Deck.resetDeck()`, `CommandHistory.clearHistory()` | StandardDeck.java, CommandHistory.java | Plausible API, aktuell ungenutzt. |

**Auskommentierte Leiche (entfernt 2026-07-21):** `Table.getCardById(int)` – hing an der
entfernten Card-ID. `Table` ist dadurch eine leere Hülle, obwohl UML
`Table.getCard(Suit,Rank): Card` vorsieht.

## 2. Halb-fertige / vermischte Pattern-Ansätze

- **State-Pattern – zwei konkurrierende Designs:** alt `Phase.aktionDurchfuehren(Game)`
  (UML + auskommentierter `gameLoop()` in Game.java) vs. neu
  `Phase.isValid(Game,Command)` + `next(Game)` (Code). Abweichung zur Folien-Lehrform
  (Kleuker F. 309): dort gibt die zustandsverändernde Methode den Folgezustand zurück;
  `next(Game)` gibt `void` zurück und ruft `changePhase()` nicht auf → bei Multi-Phasen
  (echtes Durak) unvollständig. **Am wenigsten fertiger Teil.**
- **Command-Pattern:** sauber und folienkonform (F. 313/314).
- **Strategy:** `WinCondition` fertig ✅; `Effect` ist die leere zweite Hälfte.
- **Factory Method:** sauber (F. 388f.); nur doppelte `getDeckSize()`.
- **Observer:** `GameListener` + `Game.notify*` = Textbuch-Observer (F. 277–282);
  in keinem UML-Diagramm dokumentiert (jüngstes, 5. Pattern).
- **Leere Platzhalter-Packages:** `showcase/durak/{command,model,state,strategy}` und
  `showcase/maumau/{…}` – nur leere `dummy*`-Klassen (zudem lowercase-Namen). Nie gefüllt.
  Einziger real laufender Showcase ist `minigame` (flach, ohne MVC – bewusst als Test).

## 3. Doku ↔ Code Inkonsistenzen

- `GameRules` (in Projektstruktur.md als Kern-Interface gelistet) existiert nicht;
  Aufgaben aufgeteilt auf `WinCondition` + `Phase` + `GameSetup`.
- `Deck.java` laut Doku in `core/`, real in `framework/factory/`.
- `framework/observer/` und `core/GameSetup.java` fehlen in Projektstruktur.md.
- `+Übersicht.md`/Aktivitätsdiagramm beschreiben noch die `gameLoop`-Welt (Schicht A).

## 4. Kleinere Code-Smells

- ~~Zwei `Main`-Klassen~~ – Konsolen-`Main` entfernt 2026-07-21; nur noch Swing-`application/Main`.
- `application`-Schicht ist hart an `minigame` gekoppelt (importiert MiniFactory/… direkt).
- `Game.start()` ruft `players.get(0)` ohne Leer-Prüfung → Crash bei 0 Spielern.
- Begriffs-Dopplung `CardCollection.size()` vs. `Deck.getDeckSize()`.
- Namensabweichung UML↔Code: `EffectCard.getEffect()` vs. `getAction()`; UML `excecuteCommand` (Tippfehler).
- ~~`System.out` in `MiniSetup`~~ – entfernt 2026-07-21, konsistent mit `GameListener`.

## Priorisierte Empfehlung

**Sofort (toter Code):** `Game.executeCommand()`, auskommentierter `gameLoop()`-Block.
(Card-ID-Reste + Konsolen-`Main` + `System.out` bereits erledigt 2026-07-21.)

**Entscheiden (Pattern zu Ende bringen ODER entfernen):** `EffectCard`/`Effect`,
`CardVisibility`/`flip`, `transferCard`, `Hand.getPlayableCards`, `resetDeck`,
`clearHistory`, `DeckFactory.getDeckSize`, leere durak/maumau-Packages,
`Phase.next()` → `changePhase()` (echtes State-Pattern).

**Für neue UML / Bericht:** Observer + `GameSetup` nachtragen; `GameRules`, `gameLoop`,
`aktionDurchfuehren` als „ersetzt durch Event-Modell" markieren.

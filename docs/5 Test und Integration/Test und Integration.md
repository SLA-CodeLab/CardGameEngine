Framework-Fixes 1–7: Dokumentation

Stand: 2026-07-29. Zusammenfassung aller bisherigen Fixes am cardengine-Framework. //todo-Kommentare im Code.

Ausgangslage - welche Probleme es gab

Vor diesen Fixes hatte das Framework mehrere zusammenhängende Schwachstellen, die aus drei überlagerten Entwicklungsphasen stammten (siehe Konsistenzanalyse):

Tote/verwaiste Daten: Player trug eine id, die im gesamten Code nie gelesen wurde — ein Relikt aus einem älteren Entwurf.
Leere Klassen mit doppelter Bedeutung: Table war eine leere Hülle ohne echte Logik und wurde gleichzeitig als Durak-Tisch und als MauMau-Ablagestapel benutzt — zwei verschiedene Spielkonzepte in einer Klasse. Hand war ebenfalls praktisch leer (getPlayableCards() war ein nie aufgerufener Stub).
Zyklische Paketabhängigkeit (A1): application und showcase kannten sich gegenseitig. DurakController, DurakBot und DurakTablePanel lagen in application/, obwohl sie reines Durak-Wissen enthielten — das widerspricht dem Grundprinzip "Framework ← Showcase ← Application" (Inversion of Control).
Fehlende Eingabevalidierung: Es gab keine Prüfung, ob eine sinnvolle Anzahl Spieler im Spiel ist. Bei 0 Spielern konnte Game.start() abstürzen; eine Obergrenze fehlte komplett.
Duplizierte Konstante: Die Zahl 6 (maximale Handkartenanzahl in Durak) stand an vier verschiedenen Stellen im Code (DrawPhase, DurakTurn, DurakController, dazu implizit auch im DurakGameSetup).
Kaputtes Undo bei mehreren Spielern: CommandHistory speicherte nur den ausgeführten Command, nicht aber Phase und activePlayer. Ein Undo machte also nur die Karte rückgängig, ließ Spielphase und aktiven Spieler aber unverändert — bei 3 Spielern führte das zu sichtbar falschen Spielzuständen nach einem Undo.
State-Pattern kannte den Command nicht (A4): Phase.next(Game) musste erraten, was gerade passiert war, statt es zu wissen. Das führte zu Hacks wie table.size() % 2 == 0 als Ersatz für "ist alles verteidigt?" — verstreut über mehrere Klassen.


Was gefixt wurde

Fix 1 — Player-ID entfernt. Die ungenutzte id wurde aus Player entfernt. Spieler werden jetzt eindeutig über ihren Namen identifiziert. Damit verschwindet totes Feld samt allem Code, der es unnötig mitschleppen musste.

Fix 2 — Table und Hand entfernt, CardCollection konkret gemacht. Statt zwei leerer Spezialklassen gibt es jetzt nur noch die konkrete CardCollection. Sowohl der Durak-Tisch als auch MauMau-Ablagestapel und die Hand eines Spielers sind jetzt einfach Instanzen von CardCollection — kein künstliches Doppelkonzept mehr.

Fix 3 — Paketzyklus A1 aufgelöst. DurakController liegt jetzt in showcase.durak.controller, DurakBot in showcase.durak.bot, DurakTablePanel in showcase.durak.ui — genauso für MauMau (showcase.maumau.{controller,bot,ui}). Die Abhängigkeitsrichtung ist jetzt einseitig: showcase darf application/framework kennen, umgekehrt nicht. Damit ist die im Projekt geforderte Inversion of Control ("Framework ← Showcase ← Application") tatsächlich im Code umgesetzt.

Fix 4 — Validierung der Spieleranzahl. Das GameSetup-Interface hat jetzt eine Methode, die prüft, ob zu wenige oder zu viele Spieler im Spiel sind, und bei Verstoß eine IllegalArgumentException wirft. Der Aufruf erfolgt zentral in Game.initGame(), also bevor überhaupt gespielt werden kann.

Fix 5 — HAND_SIZE an einer Stelle. Die Konstante für die Ziel-Handkartenzahl liegt jetzt nur noch in DurakGameSetup. DurakTurn und DrawPhase lesen sie von dort; DurakController ebenfalls 

Fix 6 — Undo repariert (HistoryEntry). Eine neue Klasse HistoryEntry speichert zu jedem Command zusätzlich einen Schnappschuss von Phase und activePlayer vor der Ausführung. CommandHistory verwaltet jetzt Stack<HistoryEntry> statt Stack<Command>. Beim Undo werden Karte, Phase und aktiver Spieler gemeinsam zurückgesetzt — vorher blieben Phase/Spieler stehen, während nur die Karte zurückgelegt wurde, was bei 3 Spielern zu inkonsistenten Zuständen führte.

Fix 7 — Phase.next(Game, Command) (A4). Die Schnittstelle wurde um den ausgeführten Command erweitert:


public interface Phase {
    boolean isValid(Game game, Command cmd);
    Phase   next(Game game, Command executed);
}

AttackPhase und DefendPhase prüfen jetzt direkt (cmd instanceof TakeCardCommand, cmd instanceof EndAttackCommand), statt aus der Kartenparität auf dem Tisch zu raten. Das entspricht der Lehrform aus der Vorlesung (Folie 309): die zustandsverändernde Methode bekommt die Eingabe, die den Übergang ausgelöst hat, und entscheidet direkt danach.



Was noch offen ist:

Zuleger funktioniert weiterhin nicht. AttackPhase.isValid() akzeptiert zwar theoretisch einen ThrowInCardCommand vom Zuleger, aber der Zuleger wird nie activePlayer — weder GUI noch Bot können diesen Command in der Praxis auslösen. Das ist eine größere, eigenständige Baustelle (mehrere offene Angriffskarten, reihum weiterschaltender Leger, "Passen" von "Tisch abräumen" trennen).

Nachzieh-Logik liegt noch teilweise im Controller. DurakController enthält weiterhin refillHands(), das refilling-Flag zur Selbstschutz-Absicherung und firstNeedy(). Die Regel "auf HAND_SIZE auffüllen" ist damit noch nicht vollständig in DrawPhase verankert, sondern wird aktiv vom Controller gesteuert — genau das Prinzip, das mit Fix 7 gerade erst für Phase.next() durchgesetzt wurde, fehlt hier noch als letzter Schritt.

Mit der Undo-Funktion werden die Karten wieder in den Deck nicht zurückgelegt.
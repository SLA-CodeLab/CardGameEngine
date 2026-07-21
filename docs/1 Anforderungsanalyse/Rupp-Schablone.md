
Game Init
1. Nach dem das System gestartet wurde soll das System ein Deck anlegen
2. Nach dem Anlegen des Decks soll das System dieses mischen
3. Nach dem das Deck gemischt wurde soll das System die Hand austeilen
4. Sobald alle Player eine Hand haben soll das System die Reihenfolge der Player bestimmen.
Game Loop
5. In jeder Round vor dem Turn des ersten Players soll das System die Möglichkeit haben den Table zu aktualisieren
6. Nach dem der Table und Hand vorbereitet sind soll das System dem aktiven Player die Möglichkeit bieten seinen Turn durchzuführen
Turn Loop
7. Wenn ein Turn begonnen wird soll das System dem Player die Möglichkeit bieten die Phasen zu durchlaufen
Phase Loop
8. Wenn eine Phase begonnen wird soll das System dem Player die Möglichkeit bieten Actions durchzuführen
Action Loop
9. Wenn eine Action durchgeführt wird soll das System prüfen ob die Action gültig ist
10. Ist die Action ungültig so soll das System dem Player die Möglichkeit bieten eine andere Action durchzuführen
11. Ist die Action gültig so soll das System prüfen ob eine weitere Aktion möglich ist
12. Ist eine weitere Aktion möglich so soll das System dem Player die Möglichkeit bieten eine weitere Action durchzuführen
13. Kann der Player eine weitere Aktion durchführen soll das System dem Player die Möglichkeit bieten das Abzulehnen und in die nächste Phase zu gehen
14. Ist die Action gültig so soll das System dem User (menschlicher Player) die Möglichkeit bieten ein Undo (Action rückgängig) zu machen
15. Versucht der User ein Undo zu machen prüft das System ob ein Undo möglich ist
16. Ist ein Undo möglich so soll das System dem User die Möglichkeit bieten eine neue Action durchzuführen
17. Ist ein Undo nicht möglich so soll das System dem User das anzeigen und die Möglichkeit bieten eine weitere Aktion durchzuführen
18. Am Ende einer Phase soll das System prüfen ob eine Win Condition erfüllt ist.
19. Ist eine Win Condition erfüllt soll das System den Winner anzeigen
20. Nach dem Prüfen einer Win Condition soll das System die nächste Phase einleiten
21. Ist keine nächste Phase einleitbar (letzte Phase) soll das System den Turn des Players beenden
22. Nach beenden des Turns soll das System prüfen ob ein nächster Player seinen Turn beginnen darf
23. Darf kein nächster Player seinen Turn beginnen so soll das System die nächste Round starten
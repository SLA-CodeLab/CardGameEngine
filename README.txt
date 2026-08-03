ZUSAMMENFASSUNG
---
Ziel der Vorliegenden Hausarbeit für das Modul OOAD ist die Entwicklung eines objektorientierten Frameworks für 
rundenbasierte Kartenspiele in Java. Zentral war die Beobachtung die Kartenspiele wie Mau 
Mau oder Durak zwar unterschiedliche Regeln aber zentrale, strukturelle Gemeinsamkeiten 
aufweisen wie bspw. Kartenverwaltung in Händen und Decks sowie das Aufteilen der 
Spielzüge in Phasen. Ziel war dementsprechend den wiederverwendbaren Kern als Framework 
umzusetzen und einen Showcase eines komplizierteren Kartenspiels das dieses Verwendet zu 
entwickeln. 
Die Entwicklung erfolgte eng an den den in der Vorlesung vorgestellten Vorgehen [Kleu]. In 
der Anforderungsanalyse wurden zunächst ein Glossar sowie Stakeholder und Ziele festgelegt, 
daraus wurden dann Use Cases mit zughöriger Dokumentation abgeleitet sowie die 
Anforderungen nach der Rupp-Schablone formuliert und anschließend die Aktivitätsdiagramm.  
Fortgefahren wurde mit dem Grobdesign im Zuge welcher ein Aktivitätsdiagramm sowie 
Klassendiagramme für das State Pattern für die Spielphasen, das Command Pattern für 
Spielzüge und deren Rücknahme, das Factory Method Pattern für die Deckerzeugung sowie das 
Strategy Pattern für Siegbedingungen und Karteneffekte. Das Observer Pattern kam in einer 
zweiten Iteration hinzu, nachdem die ursprünglich vorgesehene Konsolenschleife durch eine 
ereignisgesteuerte Oberfläche ersetzt wurde.  
Die Implementierung erfolgte in zwei Ebenen zuerst wurde das Framework entwickelt, welches 
zuerst aus den KIassendiagrammen abgeleitet wurde und dann im nächsten Schritt mit der 
notwendigen Logik bestückt wurde. Als Showcases entstanden Durak, Mau-Mau und ein 
minimales Testspiel zur Absicherung des Zusammenspiels aller Muster. 
Weiter mit der Validierung erfolgte über ein Tracing sämtlicher Anforderungen sowie einer 
Konsistenzanalyse zwischen UML und Quelltext in welchem Zuge die zweite Iteration der 
UML-Dateien erstellt wurden. Der überwiegende Teil der Anforderungen wurde erfüllt. 


DATEIEN
---
Verzeichnis 			| 	Inhalt 
/Hausarbeit 			|	Diese Hausarbeit im Originalformat und als PDF
/Quellcode/CardGameEngine/docs	|	UML-Diagramme und weitere Doku zu dem Projekt 
/Quellcode/CardGameEngine/src 	| 	Der Entwickelte Quelltext zu dem Projekt
../cardengine/framework		|	Das Entwickelte Framework
../cardengine/showcase/Durak	|	Der vollständige Showcase zu Durak selbst implementiert

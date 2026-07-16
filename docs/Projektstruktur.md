```csharp
CardGameProject/
├── src/
│   └── cardengine/
│       ├── framework/                       <-- ENGINE 
│       │   ├── core/
│       │   ├── state/                        <-- State Pattern
│       │   ├── command/                      <-- Command Pattern    
│       │   ├── strategy/                     <-- Strategy Pattern
│       │   └── factory/                      <-- Factory Pattern
│       │                    
│       ├── showcase/ 
│		│		├── durak/
│       │   │   ├── model/
│       │   │   ├── state/
│       │   │   ├── command/
│       │   │   └── strategy/
│       │   │
│       │   ├── maumau/
│       │   └── poker/
│       │
│       └── application   
│			    	├── ui/
│           ├── controller/
│           └── Main.java/ 
│               
│
└── docs/uml/
```

```csharp
CardGameProject/
├── src/
│   └── cardengine/
│       ├── framework/                       <-- ENGINE (generisch)
│       │   ├── core/
│       │   │   ├── Card.java                (Interface: suit, rank)
│       │   │   ├── Deck.java
│       │   │   ├── Player.java
│       │   │   └── GameRules.java            (Interface, s.u.)
│       │   │
│       │   ├── state/                        <-- State Pattern
│       │   │   ├── GameState.java            (Interface)
│       │   │   ├── TurnManager.java          (Context)
│       │   │   ├── TurnStartState.java
│       │   │   ├── AwaitingActionState.java
│       │   │   └── TurnEndState.java
│       │   │
│       │   ├── command/                      <-- Command Pattern
│       │   │   ├── GameCommand.java          (Interface)
│       │   │   └── CommandHistory.java
│       │   │
│       │   ├── strategy/                     <-- Strategy Pattern
│       │   │   ├── CardEffect.java           (Interface)
│       │   │   └── WinCondition.java         (Interface)
│       │   │
│       │   └── factory/                      <-- Factory Method
│       │       └── DeckFactory.java          (abstract, createDeck())
│       │
│       └── showcase/ //NUR BEISPIEL in der REALITÄT MVC ANWENDEN
│           ├── durak/
│           │   ├── DurakCard.java           (implements Card)
│           │   ├── DurakDeckFactory.java    (extends DeckFactory — 36 Karten, 6–Ass, legt Trumpf fest)
│           │   ├── DurakRules.java          (implements GameRules)
│           │   ├── command/
│           │   │   ├── AttackCommand.java   (Angriffskarte auslegen)
│           │   │   ├── ThrowInCommand.java  (weitere Karte gleichen Rangs zulegen, "подкинуть")
│           │   ├── DefendCommand.java   (mit höherer Karte / Trumpf schlagen)
│           │   │   └── TakeCommand.java     (Verteidiger nimmt alle Karten auf — kein undo sinnvoll)
│           │   ├── DurakWinCondition.java   (implements WinCondition — letzter Spieler mit Karten verliert)
│           │   └── Main.java
│           │
│           ├── maumau/
│           │   ├── MauMauCard.java           (implements Card)
│           │   ├── MauMauDeckFactory.java    (extends DeckFactory)
│           │   ├── MauMauRules.java          (implements GameRules)
│           │   ├── command/
│           │   │   ├── PlayCardCommand.java
│           │   │   └── DrawCardCommand.java
│           │   ├── effects/                  <-- Strategy-Konkretisierungen
│           │   │   ├── SkipTurnEffect.java   (implements CardEffect)
│           │   │   ├── DrawTwoEffect.java    (implements CardEffect)
│           │   │   └── ChooseSuitEffect.java (implements CardEffect)
│           │   ├── MauMauWinCondition.java   (implements WinCondition)
│           │   └── Main.java
│           │
│           └── poker/
│               ├── PokerCard.java            (implements Card)
│               ├── PokerDeckFactory.java     (extends DeckFactory)
│               ├── PokerRules.java           (implements GameRules)
│               ├── command/
│               │   ├── BetCommand.java
│               │   ├── CallCommand.java
│               │   ├── RaiseCommand.java
│               │   ├── CheckCommand.java
│               │   └── FoldCommand.java
│               ├── HandEvaluator.java        <-- kein Pattern, reine Hilfsklasse
│               ├── PokerWinCondition.java    (implements WinCondition)
│               └── Main.java
│
├── docs/uml/
└── bin/
```
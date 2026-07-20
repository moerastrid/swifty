# Swingy — Project Plan

## Source of truth

- **Subject PDF**: `swingy.en.subject.pdf` in the root of this repo — this is the ULTIMATE source of truth for requirements.
- **Flowchart**: `Mazie.drawio` in the root — student's own design, used to derive the GameView interface. Open with draw.io desktop or diagrams.net.
- **This file**: current plan + wishlist.

---

## Student context & agent interaction rules

**School**: Codam (42 network), Amsterdam.

**AI policy at Codam**: AI tools are allowed as a learning aid — asking questions, exploring concepts, comparing approaches. What is explicitly forbidden is copy-pasting AI-generated code you don't understand into your project. The student must be able to understand and explain every single line they submit. AI is a tutor/peer, not a substitute developer.

**Consequence for agents**: Do NOT just write code and hand it over. For every non-trivial decision or implementation:

- The student wants to write their own code. Don't write code for them, just give examples/suggestions.
- Explain **what** you are suggesting and **why** (the design reasoning, not just syntax)
- When there are multiple valid approaches, explain them all: benefits and downsides. The student decides which one to implement.
- If the student asks "why" about anything, always answer — never skip the explanation
- Prefer teaching the pattern once and then helping apply it, over repeating full implementations
- If something is a Java best practice or convention (e.g. records, interface default visibility, static factory methods), name it so the student can look it up
- Student has ADHD and needs small, managable tasks. Also be kind please. We love you.

The goal is that after this project the student can sit in front of a peer evaluator and defend every line of code in the codebase, because the student wrote everything themselves.

---

## Current status

Mandatory subject requirements: complete. Bonus requirements: complete. Remaining work is polish, UX and optional features.

Build & run:

```bash
mvn clean package
java -jar target/swingy.jar console
java -jar target/swingy.jar gui
```

---

## Todo

### General / robustness

- [ ] Decide what happens after a player wins. Quit entirely? Back to the main/new-game menu? Offer to immediately start a fresh map with the same hero?
- [ ] Write player-facing instructions / how-to-play text.
- [ ] *(idea, not decided)* Consider `java.util.logging` instead of scattered `System.err.println(...)` calls.

### GitHub / submission

- [ ] Consider publishing builds under GitHub Releases.

### Persistence

- [ ] Allow deleting a hero from the load-game / select-hero screen.

### High scores (new idea, not designed yet)

- [ ] On win *or* loss, record something about the hero as a persistent "high score" entry (new SQLite table, same repository pattern as `HeroRepository`). Open questions: what counts as "the score" (level? xp? turns survived?), where it's shown, whether it's global or per hero type.

### Builder pattern (deliberate learning goal, not just a feature)

- [ ] Apply the Builder pattern somewhere and be able to explain it. Best candidate: `Hero`'s DB-loading constructor (8 positional params). Worth deciding honestly whether Builder is the textbook-correct fit here (all params required, no optionals) or a deliberate retrofit to practice the pattern — either is fine, but be able to say which for peer-eval defense.

### Swing GUI

- [ ] Arrow-key navigation for movement (N/E/S/W), and consider extending keyboard control to other screens.
- [ ] Kanteen screen artwork.

### Open questions / other

- [ ] Reconcile Hibernate Validator's rules with what `GameController`/the repository actually check — any duplication or gaps?
- [ ] Re-verify MVC separation once more against the subject's "only a good and clear implementation will be accepted" — a full, deliberate pass.
- [ ] Re-verify "best practices suited for this problem" — no concrete checklist for this yet.

---


## Mandatory requirements checklist (from subject) — re-verified 2026-07-09

> This checklist **is** the subject's mandatory part (V.1 Gameplay, V.2 Features, V.3 Validation). Re-checked line-by-line against the subject PDF and the current code (`GameController`, `GameEngine`, `GameMap`, `Hero`, `pom.xml`) — not just carried over from before.

- [x] MVC architecture (Model/View/Controller clearly separated)
- [x] Launch with `java -jar mazie.jar console`
- [x] Launch with `java -jar mazie.jar gui`
- [x] Build with `mvn clean package` → produces runnable jar
- [x] No external libraries except Hibernate Validator + SQLite JDBC (confirmed — `pom.xml` has exactly these two `<dependency>` entries)
- [x] Hero persistence (load on start, save on exit)
- [x] Create hero flow
- [x] Select existing hero flow
- [x] Show hero stats (name, class, level, xp, attack, defence, hp)
- [x] Square map, size = (level-1)\*5+10-(level%2) — `GameMap` builds this as `(level-1)*5+10-(level%2) + 2`. The `+2` is intentional, not a deviation: the subject's border cell is itself part of the map (a hero wins once *standing next to* the border, not when standing on it), so the map is grown by one ring on each side to keep the win-condition logic elsewhere unchanged. Confirmed with the student.
- [x] Move in 4 directions (N/E/S/W)
- [x] Win by reaching border
- [x] Monsters randomly spread on map
- [x] Fight or run choice when meeting monster
- [x] Run = 50% chance, fail = must fight
- [x] Battle simulation using hero + monster stats
- [x] Lose battle = game over
- [x] Win battle = XP gain, possible artifact drop
- [x] Level up formula: level\*1000 + (level-1)^2\* 450
- [x] 3 artifact types: Weapon (+attack), Armor (+defence), Helm (+hp)
- [x] Annotation-based validation (Hibernate Validator) on user input
- [x] Validation failure highlighted to user

## Bonus checklist

- [x] Hero persistence in a relational database instead of a text file
- [x] Switch between console/GUI at runtime without closing.

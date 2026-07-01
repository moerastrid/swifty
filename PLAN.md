# Swingy — Project Plan & Handoff

## Source of truth

- **Subject PDF**: `swingy.en.subject.pdf` in the root of this repo — this is the ULTIMATE source of truth for requirements.
- **Flowchart**: `Mazie.drawio` in the root — student's own design, used to derive the GameView interface. Open with draw.io desktop or diagrams.net.
- **This file**: current plan, to be updated as work progresses.

---

## Student context & agent interaction rules

**School**: Codam (42 network), Amsterdam.

**AI policy at Codam**: AI tools are allowed as a learning aid — asking questions, exploring concepts, comparing approaches. What is explicitly forbidden is copy-pasting AI-generated code you don't understand into your project. The student must be able to understand and explain every single line they submit. AI is a tutor/peer, not a substitute developer.

**Consequence for agents**: Do NOT just write code and hand it over. For every non-trivial decision or implementation:

- The student wants to write their own code. Don't write code for them, just give examples/suggestions.
- Explain **what** you are suggesting and **why** (the design reasoning, not just syntax)
- When there are multiple valid approaches, explain them all: benefits and downsides. The students decides which one to implement.
- If the student asks "why" about anything, always answer — never skip the explanation
- Prefer teaching the pattern once and then helping apply it, over repeating full implementations
- If something is a Java best practice or convention (e.g. records, interface default visibility, static factory methods), name it so the student can look it up
- Student has ADHD and needs small, managable tasks. Also be kind please. We love you.

The goal is that after this project the student can sit in front of a peer evaluator and defend every line of code in the codebase, because the student wrote everything themselves.

---

## Project structure

```text
swingy/
  mazie/          ← ACTIVE project (work here)
  old/            ← old attempt with SQL, let a subagent explore for reusage if nessecary.
  oldold/         ← old attempt with Swing, let a subagent explore for reusage if nessecary. 
  swingy.en.subject.pdf 
  Mazie.drawio
```

Build & run commands (from `mazie/`):

```bash
mvn clean package
java -jar target/mazie.jar console
java -jar target/mazie.jar gui
```

---

## Current status per fase

### ✅ Fase 1 — Model & Validatie (DONE)

- `Hero.java`: `@NotBlank`, `@Size`, `@NotNull`, `@Min` annotaties; constructor uses `HeroType` stats; `gainXp()` returns `boolean` for level-up
- `HeroType.java`: enum with `baseAttack`, `baseDefence`, `baseHp` per type (FROG/HARE/BEAR)
- `Monster.java`: static final name arrays, final Random, off-by-one fixed
- `Artifact.java`: converted to `record`; static factory methods `weapon()`, `armour()`, `helmet()`; names as static final arrays inside Artifact (deliberate choice)
- `ArtifactType.java`: empty enum — names live in Artifact (deliberate)
- `FightResult.java`: `record FightResult(boolean win, boolean levelup, Artifact drop)`
- `GameEngine.java`: `fight()` returns `FightResult`, no System.out.println, `dropArtifact()` implemented
- `pom.xml`: Hibernate Validator + SQLite present, jar config correct

### ✅ Fase 2 — GameView interface (DONE)

- `GameView.java`: 14 methods covering full game flow (welcome, create/select hero, ask direction, fight/run, level-up, artifact drop, end game)
- `showFightSummary(String, int)`: takes a pre-formatted string + xp gained — kept simple for now, may revisit during controller implementation when concrete needs become clear
- `askKeepArtifact(Artifact, Hero)`: hero is passed so view can show current artifacts for context

### 🔄 Fase 3 — Persistence (IN PROGRESS)

Decision: **SQLite database** (bonus) instead of text file — subject allows this as a replacement.

Done:

- `HeroRepository.java` interface in `mazie.repository` with 4 methods: `loadAllHeroes()`, `save(Hero)`, `update(Hero)`, `delete(Hero)`.
- `SQLiteHeroRepository.java` skeleton: connection setup, `createTables()` fully implemented.
  - Two tables: `hero` + `artifact` with CHECK constraints and FOREIGN KEY
  - `PRAGMA foreign_keys = ON` enabled
  - try-with-resources on Statement
  - `ArtifactType.valueOf(string)` is the pattern for reconstructing enums from DB strings

Still to do:

- `save(Hero hero)`: INSERT into `hero`, then INSERT each non-null artifact into `artifact`. Use `PreparedStatement`. After INSERT, read back the generated id with `getGeneratedKeys()` and set it on the hero via `hero.setId()`.
- `update(Hero hero)`: UPDATE `hero` row + DELETE old artifacts for this hero + INSERT current artifacts fresh. Simpler than trying to diff.
- `delete(Hero hero)`: DELETE from `artifact WHERE hero_id = ?`, then DELETE from `hero WHERE id = ?`. Artifacts first (FK constraint).
- `loadAllHeroes()`: Two-query approach — `SELECT * FROM hero`, build Hero objects, then per hero `SELECT * FROM artifact WHERE hero_id = ?`. Use `hero.setArtifact(artifact)` to place artifacts (already handles routing to weapon/armour/helmet slot). Skip null-check — only call `setArtifact` if artifact row exists.
- Wire into `GameController`: constructor takes `HeroRepository`, `setup()` calls `loadAllHeroes()`, `start()` finally-block calls `update(hero)`, `save(hero)` after `createValidHero()`.
- Update `Main` to instantiate `new SQLiteHeroRepository()` and pass to controller.

### ✅ Fase 4 — TerminalView (DONE)

All 15 `GameView` methods implemented in `TerminalView`:

- `AnsiColor`: typesafe `enum`, `toString()` returns the ANSI escape code
- Helpers: `colorPrint(AnsiColor, String)`, `scanNextLine()` (strips + lowercases input; catches `NoSuchElementException` on EOF and throws `QuitException`)
- `choose(prompt, Map<String, T>)`: **generic** keuze-helper — replaced the duplicated y/n / f-r / n-e-s-w switches (Rule of Three resolved). Keys must be lowercase; recursion on invalid input.
- Input flow: `showError`, `showWelcome` (ASCII art from classpath), `askNewGame`, `createHero` (type via `choose`, then name), `selectHero`, `confirmHero`, `askDirection`, `askFightMonster`, `askKeepArtifact`
- Output flow: `showHeroStats`, `showStartGame`, `showRunSuccess`, `showEndGame`, `showFightSummary`, `showLevelUp`
- Presentation moved out of the model: `Hero.toString()` is now a short debug string; the player-facing stats layout lives in `showHeroStats` (MVC: view owns presentation — directly supports the subject's MVC requirement)

Known open items (TODOs in code):

- `selectHero` uses `hero.getId()` as a `choose` key — every hero has `id = 0` until persistence assigns real ids, so multiple heroes collide on `"0"`. Switch to the list index, or wait for Fase 3 to assign ids. The name-key still works in the meantime.

### ✅ Fase 5 — GameController (DONE)

- `setup()`: welcome → ask new/existing → create/select → show stats → confirm → retry on reject. Follows the `Mazie.drawio` GAME SETUP lane.
- `createValidHero()`: loops `view.createHero()` + `validator.validate(hero)`, shows the joined violation messages via `view.showError(...)` until valid. **Validation trigger lives in the controller; the view stays dumb** (subject V.3: annotation-based validation).
- `Validator` built once as a field via `ParameterMessageInterpolator` (no EL dependency needed — keeps the dependency tree minimal for the "no external libraries" rule).
- `QuitException` (EOF) caught in `start()`.
- `gameLoop(Hero hero)`: while-loop over `turn()`, checks `engine.win()` after each turn, calls `showEndGame(true)` on border reached.
- `turn()`: direction → move → empty step OR fight/run → FightResult → game-over / level-up / artifact drop. Follows `Mazie.drawio` GAME PLAY lane.
- `GameView.showEmptyStep()` added to interface + implemented in both `TerminalView` and `GuiView` stub.

Still to do (waiting on other phases):

- `setup()` still hardcodes `newGame = true` — gate it on "saved heroes exist" once Fase 3 lands.
- Wire `repository.save(...)` into the `finally` block in `start()` (needs Fase 3).
- Remove debug `System.out.println`s + `tryOutHeroes()` / `tryOutLogic()` before submission.
- Hero stats currently shown at END of loop iteration — consider moving to start of turn (before `askDirection`) so player sees state before deciding.

### 🔄 Fase 6 — SwingView (STUB CREATED)

- `GuiView.java` created in `mazie.view.gui`, implements `GameView`, all methods are `#todo` stubs.
- No logic implemented yet.

### ✅ Fase 7 — Main + args (DONE)

- `parse(String[] args)` implemented: returns `true` for `console`/`c`, `false` for `gui`/`g`, throws `ParseException` on unknown input.
- `main()` instantiates `TerminalView` or `GuiView` based on `parse()` result, builds `GameController`, calls `start()`.
- `ParseException` added as a new exception class.

---

## Next-session priorities (top of stack)

1. **Fase 3 — `save(Hero)`**: INSERT hero row + artifact rows. Use `PreparedStatement`. Read back generated id with `getGeneratedKeys()` and call `hero.setId()`. Then wire `save()` into controller after `createValidHero()`.
2. **Fase 3 — `update(Hero)` + `delete(Hero)`**: update is DELETE old artifacts + UPDATE hero row + INSERT fresh artifacts. Delete is artifacts first, then hero (FK order).
3. **Fase 3 — `loadAllHeroes()`**: two queries (heroes, then artifacts per hero). Use `hero.setArtifact()` to place artifacts. Wire into `setup()`.
4. **Fase 3 — wire controller**: constructor takes `HeroRepository`; `setup()` uses loaded list to gate `askNewGame`; `finally` calls `update(hero)`.
5. **Fase 6 — SwingView**: only after persistence is fully wired and end-to-end works.
6. **Submission hygiene**: remove debug printlns + `tryOutHeroes()`/`tryOutLogic()`; verify `>=` vs `>` in `gainXp()`.

---

## Fase 3 — Persistence plan

Package: `mazie.repository`. Chosen approach: **SQLite** (bonus — replaces text file requirement).

**Files:**

- `HeroRepository.java` — interface: `loadAllHeroes()`, `save(Hero)`, `update(Hero)`, `delete(Hero)` ✅
- `SQLiteHeroRepository.java` — implements interface, connection + createTables() done ✅

**Schema:**

```sql
hero(id, name, type, level, xp, attack, defence, hp)
artifact(id, name, type, value, hero_id → hero.id)
```

- `hero.type` CHECK: `'FROG'`, `'HARE'`, `'BEAR'`
- `artifact.type` CHECK: `'WEAPON'`, `'ARMOUR'`, `'HELMET'`
- `PRAGMA foreign_keys = ON` enabled in `createTables()`

**Key patterns to use:**

- `PreparedStatement` for all INSERT/UPDATE/DELETE (prevents SQL injection)
- `statement.getGeneratedKeys()` after INSERT to get the auto-generated id → `hero.setId(id)`
- `HeroType.valueOf(rs.getString("type"))` and `ArtifactType.valueOf(rs.getString("type"))` to reconstruct enums
- `hero.setArtifact(artifact)` already routes to the right slot (weapon/armour/helmet) — use this in `loadAllHeroes()`
- Do NOT call `setWeapon/setArmour/setHelmet` with null — only call `setArtifact` if an artifact row exists
- Do NOT run Hibernate Validator when loading from DB — validation is only for fresh user input

**update() strategy:** DELETE old artifacts for hero_id + UPDATE hero row + INSERT fresh artifacts. Simpler than diffing.

**delete() order:** DELETE from artifact first (FK), then DELETE from hero.

---

## Fase 6 — SwingView plan

New file: `mazie/src/main/java/mazie/view/swing/SwingView.java`, implements `GameView`.

**Layout idea (one option, not mandatory):**

- `JFrame` with `BorderLayout`
- A non-editable `JTextArea` (wrapped in `JScrollPane`) for messages and stats
- A panel with 4 `JButton`s (N/E/S/W) for direction — only enabled during `askDirection()`

**The hard part — blocking input from buttons:**

The controller runs on the main thread and calls `view.askDirection()` expecting a return value. But Swing button clicks happen on the EDT (Event Dispatch Thread) and don't return anything to the caller. You need a way for the EDT to *hand a value back* to the controller thread, and for the controller to *wait* until that happens.

Approaches to look up and choose between:

- `SynchronousQueue<Direction>` — EDT calls `put(dir)`, controller calls `take()`. Direct hand-off, no buffer.
- `BlockingQueue<Direction>` with capacity 1 — similar, slightly more forgiving
- `CompletableFuture<Direction>` — controller awaits, EDT completes it

For the modal dialogs (`askFightMonster`, `askKeepArtifact`, `confirmHero`, `createHero`), `JOptionPane.showXxxDialog(...)` already blocks the calling thread — no queue needed.

**EDT safety rule to look up:**

- All Swing UI updates must run on the EDT. Use `SwingUtilities.invokeLater(...)` from non-EDT threads.

**Important:** the controller does NOT need to know it's talking to Swing. It just calls `view.askDirection()` and gets a `Direction` back. The threading complexity is fully hidden inside `SwingView`.

---

## Design decisions made

| Decision | Rationale |
| --- | --- |
| No map displayed to user | Student's deliberate choice — adds mystery, subject doesn't require it |
| Artifact names in Artifact.java, not ArtifactType | Simpler, deadline-focused |
| No MonsterFactory class | Monster.easy/medium/hard() is already Static Factory Method pattern, extra class adds no value |
| `Hero.id` field present but 0 until DB assigns it | Needed by DB (PRIMARY KEY). Not used by game logic — only the repository reads/writes it. Private constructor + `setId()` used during load. |
| `gainXp()` returns boolean (levelup) | Cleaner than separate `levelUp()` call |
| EOF in terminal input throws `QuitException` | `Ctrl+D` closes stdin permanently; bubbling a quit signal avoids infinite retry loops and keeps exit handling centralized |
| SynchronousQueue for Swing direction input | Standard solution for blocking EDT-to-controller communication |
| JOptionPane for Swing dialogs | Blocks naturally, no queue needed |
| Static Factory Methods instead of Builder pattern | Subject *hints* at Builder pattern (Chapter III). Skipped because Hero only has `name + type` as user input (Builder shines for many optional fields), and Monster/Artifact already use Static Factory Methods which are idiomatic for our case. **Be ready to explain this trade-off at peer evaluation** — evaluator may ask where Builder was applied. |
| SQLite dependency in `pom.xml` | Used for the relational-DB bonus (replaces text file requirement). Subject explicitly allows a library for this bonus *"if explicitly justified and serving only that part"*. Justification: SQLite JDBC is the minimal driver needed to implement a relational DB; no ORM or abstraction layer used. |
| Validation trigger in controller, not view | `createValidHero()` validates; the view only collects input. Keeps the view dumb and makes the same validation reusable for the Swing view. |
| `ParameterMessageInterpolator` instead of `buildDefaultValidatorFactory()` | Avoids needing a Jakarta EL implementation (e.g. Expressly) on the classpath — one less dependency to justify against the "no external libraries" rule. Handles `{min}`/`{max}` placeholders, which is all we use. |
| `choose(prompt, Map<String,T>)` generic helper | A `Map` captures *what is valid* (keys) **and** *what it maps to* (values) in one structure, eliminating per-method switches. A `Set` would only validate and force a second switch. |
| Trim + lowercase all input in `scanNextLine()` | Single normalization point: `"  Y  "` works everywhere, and `@Size`/`@NotBlank` see meaningful characters (blocks names like `"        a"`). |
| `Hero.toString()` is debug-only; stats layout in `showHeroStats` | `toString()` convention = logging/debug. Player presentation belongs in the view (MVC) so each view (terminal/Swing) formats independently. |

---

## Mandatory requirements checklist (from subject)

> This checklist **is** the subject's mandatory part (V.1 Gameplay, V.2 Features, V.3 Validation) — treat it as the grading rubric. The turn order inside the game loop follows `Mazie.drawio`. Annotations: *engine ready* = model/`GameEngine` logic exists but is not yet wired into a playable `gameLoop`.

- [x] MVC architecture (Model/View/Controller clearly separated)
- [x] Launch with `java -jar mazie.jar console`
- [ ] Launch with `java -jar mazie.jar gui` — *SwingView stubs only (Fase 6)*
- [x] Build with `mvn clean package` → produces runnable jar
- [x] No external libraries except Hibernate Validator + SQLite (SQLite justified as bonus DB library)
- [ ] Hero persistence (load on start, save on exit) — *SQLite approach in progress (Fase 3)*
- [x] Create hero flow
- [ ] Select existing hero flow — *needs Fase 3 complete + selectHero id-key fix*
- [x] Show hero stats (name, class, level, xp, attack, defence, hp)
- [x] Square map, size = (level-1)*5+10-(level%2)
- [x] Move in 4 directions (N/E/S/W)
- [x] Win by reaching border
- [x] Monsters randomly spread on map
- [x] Fight or run choice when meeting monster
- [x] Run = 50% chance, fail = must fight
- [x] Battle simulation using hero + monster stats
- [x] Lose battle = game over
- [x] Win battle = XP gain, possible artifact drop
- [x] Level up formula: level*1000 + (level-1)^2 * 450 — *verify `>` vs `>=` boundary still open*
- [x] 3 artifact types: Weapon (+attack), Armor (+defence), Helm (+hp)
- [x] Annotation-based validation (Hibernate Validator) on user input
- [x] Validation failure highlighted to user

## Bonus checklist

- [ ] Hero persistence in relational database instead of text file — *in progress (Fase 3)*
- [ ] Switch between console/GUI at runtime without closing

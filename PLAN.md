# Swingy — Project Plan & Handoff

## Source of truth

- **Subject PDF**: `swingy.en.subject.pdf` in the root of this repo — this is the ULTIMATE source of truth for requirements.
- **Flowchart**: `Mazie.drawio` in the root — student's own design, used to derive the GameView interface. Open with draw.io desktop or diagrams.net.
- **This file**: current plan, to be updated as work progresses.

**Last full review: 2026-07-03** — re-read subject PDF, `Mazie.drawio`, and the actual current codebase (not just this file) to verify everything below is still accurate. See "Drawio review" section further down for details.

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

### ✅ Fase 3 — Persistence, repository layer (DONE)

Decision: **SQLite database** (bonus) instead of text file — subject allows this as a replacement, justified in the design-decisions table below.

Files (package `mazie.repository`):

- `HeroRepository.java` — interface: `Map<Integer, Hero> loadAllHeroes()`, `save(Hero)`, `update(Hero)`, `delete(Hero)`. Returns a `Map<Integer, Hero>` keyed by hero id, not a `List<Hero>` — see design decisions for why.
- `SQLiteHeroRepository.java` — fully implemented:
  - Connection setup with data-dir read/write check, `setAutoCommit(false)` set once in the constructor, `createTables()` (schema + `PRAGMA foreign_keys = ON`)
  - `save(Hero)`: INSERT hero row, read back generated id via `getGeneratedKeys()` → `hero.setId(...)`, then INSERT each artifact
  - `update(Hero)`: DELETE old artifacts + UPDATE hero row + INSERT fresh artifacts (full-replace, not diffing — deliberate, see design decisions)
  - `delete(Hero)`: DELETE artifacts first (FK order), then DELETE hero row
  - `loadAllHeroes()`: single `LEFT JOIN` query (hero + artifact, aliased columns to avoid name collisions), delegates row→object mapping to `SQLiteHeroMapper`
  - All writes wrapped in try/catch with `commitConnection()`/`rollbackConnection()` (manual transactions)
  - All SQL text and error messages extracted to `private static final String` constants
  - `update()`/`delete()` throw `RepositoryException` if `hero.getId() == 0` (never persisted yet)
- `SQLiteHeroMapper.java` — extracted row-mapping responsibility: `mapHeroes(ResultSet)` (static, dedups heroes via internal map, guards against heroes with no artifacts to avoid an NPE), `convertHeroType`/`convertArtifactType` (private static switch-expressions). Pulled out of `SQLiteHeroRepository` because this logic needs no `Connection` and changes for a different reason (domain model shape) than the rest of the repository — a genuine SRP-motivated extraction. A generic `Utils` class (holding *all* private methods, static, with `Connection` passed as a parameter) and a separate `HeroSchema` class (schema/DDL only) were both tried and explicitly rejected/reverted — see design decisions for why.

**This layer is solid, reviewed line-by-line, no known bugs.** What's NOT done: wiring these calls into `GameController` — that's the actual remaining work, tracked under Fase 5 below.

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

### 🔄 Fase 5 — GameController (repository still not wired in)

The gameplay logic matches the `Mazie.drawio` flow well. **The repository is not wired in at all yet** — that's the remaining work.

**What's implemented and conceptually working:**

- `setup()`: welcome → ask new/existing → create/select → show stats → confirm → retry on reject. Follows the `Mazie.drawio` GAME SETUP lane.
- `createValidHero()`: loops `view.createHero()` + `validator.validate(hero)`, shows the joined violation messages via `view.showError(...)` until valid. **Validation trigger lives in the controller; the view stays dumb** (subject V.3: annotation-based validation).
- `Validator` built once as a field via `ParameterMessageInterpolator` (no EL dependency needed — keeps the dependency tree minimal for the "no external libraries" rule).
- `QuitException` (EOF / Ctrl+D) caught in `start()`.
- `gameLoop(Hero hero)`: while-loop over `turn()`, checks `engine.win()` after each turn, calls `showEndGame(true)` on border reached.
- `turn()`: direction → move → empty step OR fight/run → FightResult → game-over / level-up / artifact drop. Follows `Mazie.drawio` GAME PLAY lane.
- `GameView.showEmptyStep()` added to interface + implemented in both `TerminalView` and `GuiView` stub.

**Still to do — this is now the priority:**

- `setup()` still hardcodes `newGame = true`, and calls `view.selectHero(Collections.emptyMap())` with a hardcoded empty map instead of real data. The `repository` field is stored in the constructor but **still never read anywhere in the class** (confirmed: compiler flags it as "never read"). Needs: call `repository.loadAllHeroes()` once, gate `askNewGame()` on whether that map is non-empty, pass the loaded `Map<Integer, Hero>` into `view.selectHero(...)` instead of the empty-map placeholder.
- Call `repository.save(hero)` right after `createValidHero()` returns (brand-new hero, never persisted).
- Call `repository.update(hero)` when the game ends. Recommended: put it in `start()`'s `finally` block (currently just `// #todo`) so it runs unconditionally — covers win, loss, *and* quit-via-`QuitException` in one place.
- Open design question (also written directly on the drawio itself — see review below): when a hero dies in battle, should `repository.delete(hero)` be called, or should the hero just stay in the DB at last-saved stats so the player can try again with a fresh hero? Not decided yet — resolves the `// #todo delete hero?` comment in `turn()`.
- Remove debug `System.out.println`s scattered through `setup()`, `gameLoop()`, `turn()` before submission.
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

1. **Wire `HeroRepository` into `GameController`** (the repository itself is done, see Fase 3):
   - `setup()`: call `repository.loadAllHeroes()`, gate `askNewGame()` on whether it's non-empty, pass the map into `selectHero(...)` instead of the current hardcoded `Collections.emptyMap()`.
   - Call `repository.save(hero)` right after a brand-new hero passes `createValidHero()`.
   - Call `repository.update(hero)` in `start()`'s `finally` block (covers win, loss, and quit in one place).
   - Decide + implement: does losing a battle call `repository.delete(hero)`, or just leave the hero at last-saved stats? (open question, also flagged on the drawio itself)
2. **Submission hygiene**: remove debug `System.out.println`s in `GameController` (`setup()`, `gameLoop()`, `turn()`); verify `>` vs `>=` boundary in `Hero.gainXp()`.
3. **Fase 6 — SwingView**: only after the above is done and console mode works fully end-to-end (create → play → persist → reload).
4. **Open from the drawio's own "MY CURRENT QUESTIONS" box**: how to handle Ctrl+C (SIGINT) mid-game — currently only EOF (Ctrl+D) is caught via `QuitException`; SIGINT isn't handled at all and will kill the JVM without persisting. Decide if a `Runtime.getRuntime().addShutdownHook(...)` is worth adding, or leave out of scope.

---

## Fase 3 — Persistence, final implementation reference

Package: `mazie.repository`. Chosen approach: **SQLite** (bonus — replaces text file requirement). Status: **done** (repository layer only — controller wiring is separate, see Fase 5).

**Files:**

- `HeroRepository.java` — interface: `Map<Integer, Hero> loadAllHeroes()`, `save(Hero)`, `update(Hero)`, `delete(Hero)` ✅
- `SQLiteHeroRepository.java` — connection/transaction lifecycle + CRUD orchestration ✅
- `SQLiteHeroMapper.java` — `ResultSet` → `Hero`/`Artifact` mapping, extracted for SRP reasons ✅

**Schema:**

```sql
hero(id, name, type, level, xp, attack, defence, hp)
artifact(id, name, type, value, hero_id → hero.id)
```

- `hero.type` CHECK: `'FROG'`, `'HARE'`, `'BEAR'`
- `artifact.type` CHECK: `'WEAPON'`, `'ARMOUR'`, `'HELMET'`
- `PRAGMA foreign_keys = ON` enabled in `createTables()`
- `connection.setAutoCommit(false)` set once in the constructor; every write method ends with `commitConnection()` on success or `rollbackConnection()` in the catch block

**Key patterns used (for reference / peer-evaluation defense):**

- `PreparedStatement` for all INSERT/UPDATE/DELETE (prevents SQL injection)
- `statement.getGeneratedKeys()` after the hero INSERT to get the auto-generated id → `hero.setId(id)` (artifacts don't need this — `Artifact` is an immutable `record` with no persisted identity, see design decisions)
- Manual switch-expressions in `SQLiteHeroMapper` reconstruct `HeroType`/`ArtifactType` enums from DB strings
- `hero.setArtifact(artifact)` routes to the right slot (weapon/armour/helmet) — used in the mapper, guarded by `artifactName != null` (a hero with no artifacts still produces one `LEFT JOIN` row with all-`NULL` artifact columns — skip it, otherwise `NullPointerException`)
- Do NOT run Hibernate Validator when loading from DB — validation is only for fresh user input

**`update()` strategy:** DELETE all old artifacts for `hero_id` + UPDATE hero row + INSERT current artifacts fresh (full-replace, not diffing) — deliberate, given a hero only ever has ≤3 artifacts and `Artifact` has no identity to diff against.

**`delete()` order:** DELETE from `artifact` first (FK constraint), then DELETE from `hero`.

**No known bugs in this layer.** The only remaining persistence-related work is calling these methods from `GameController` (Fase 5).

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
| `HeroRepository.loadAllHeroes()` returns `Map<Integer, Hero>`, not `List<Hero>` | Repository stays keyed by DB id (the natural, free identifier); `TerminalView.selectHero()` converts it internally to `Map<String, Hero>` for its own `choose()` helper. Each layer picks the key type it actually needs — the repository shouldn't need to know about view/UI concerns. |
| `SQLiteHeroMapper` extracted from `SQLiteHeroRepository`; a generic `Utils` class and a separate `HeroSchema` class were not | Row→object mapping has a genuinely different "reason to change" (domain model shape) than SQL/connection code, and needs no `Connection` — a clean SRP-motivated split. A grab-bag `Utils` class (considered for *all* private methods, static, with `Connection` passed as a parameter everywhere) and a separate `HeroSchema` class (schema/DDL only, tried then reverted) were both rejected: they either leak `connection` as a parameter through the whole class or add a whole extra file for 3 lines of one-time DDL — overhead without real benefit at this project's scale. |
| Manual transactions (`setAutoCommit(false)` once + explicit `commit()`/`rollback()` per write) | `save()`/`update()`/`delete()` each touch 2+ tables (hero + artifacts); without a transaction, a failure partway through could leave the DB in an inconsistent state (e.g. hero row saved, artifact insert failed). |

---

## Mandatory requirements checklist (from subject)

> This checklist **is** the subject's mandatory part (V.1 Gameplay, V.2 Features, V.3 Validation) — treat it as the grading rubric. The turn order inside the game loop follows `Mazie.drawio`. Annotations: *engine ready* = model/`GameEngine` logic exists but is not yet wired into a playable `gameLoop`.

- [x] MVC architecture (Model/View/Controller clearly separated)
- [x] Launch with `java -jar mazie.jar console`
- [ ] Launch with `java -jar mazie.jar gui` — *SwingView stubs only (Fase 6)*
- [x] Build with `mvn clean package` → produces runnable jar
- [x] No external libraries except Hibernate Validator + SQLite (SQLite justified as bonus DB library)
- [ ] Hero persistence (load on start, save on exit) — *repository layer done (Fase 3); `GameController` wiring still missing (Fase 5)*
- [x] Create hero flow
- [ ] Select existing hero flow — *repository + view side (`Map<Integer,Hero>`) done; blocked on the same `GameController` wiring*
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

- [ ] Hero persistence in relational database instead of text file — *repository layer done; not yet wired into `GameController`, so not playable/verifiable end-to-end (Fase 5)*
- [ ] Switch between console/GUI at runtime without closing

---

## Drawio review (2026-07-03)

Re-read `Mazie.drawio` end to end and compared it against the subject and the current code. **Conclusion: the diagram still matches the intended design well — no redraw needed.** Details:

- **GAME SETUP lane** (start game menu → welcome → new/existing decision → create hero / select existing hero → view hero stats → "ok?" confirm/retry loop) matches `GameController.setup()` 1:1, including the "Hero DB" node feeding into "select existing hero" and "view hero stats" — this is exactly the `repository.loadAllHeroes()` call that's still missing (see Fase 5).
- **GAME PLAY lane** (create map, sized/seeded by hero level → direction input → border-of-map win check → villain encounter → fight/run → battle simulation → win/lose → XP/level-up/artifact-drop) matches `GameEngine`/`GameController.turn()` well.
- The diagram shows a dashed "save hero with new xp" arrow back to the Hero DB right after the border-of-map win check, and a separate "save artifact" step after "keep?". The current implementation plan deliberately simplifies this to **one `repository.update(hero)` call** at the very end of the game (in `start()`'s `finally`), covering hero stats + all current artifacts at once, rather than saving incrementally at every event. This is a reasonable simplification (no mid-game crash-durability requirement in the subject) but is a deviation from the literal diagram — flagging it here so it's a conscious choice, not an oversight.
- The diagram has a "MY CURRENT QUESTIONS" box with two open questions, written by the student, still relevant:
  - *"What to do after 'stop'?"* — now understood: persist final state via `update()`, then the program just ends (`main()` returns). Not yet implemented (see Fase 5).
  - *"How to let user quit midway? ^C?"* — **still genuinely unresolved.** `QuitException` currently only catches EOF/Ctrl+D (via `NoSuchElementException` from `Scanner`). Ctrl+C sends SIGINT, which is not caught anywhere and will kill the JVM immediately without running any persistence/cleanup code. Needs a decision: add a `Runtime.getRuntime().addShutdownHook(...)`, or explicitly accept this as out of scope for the project.
- No structural changes to the diagram are needed — the two open questions above are implementation decisions, not drawing errors.

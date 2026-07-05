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

## Current status

Fases 1-5 and 7 (model/validation, `GameView` interface, SQLite persistence layer, `TerminalView`, `GameController` wiring, `Main`/args parsing) are **done**. Console mode (`java -jar target/mazie.jar console`) is fully playable end-to-end: create/select hero → persist → play → win/lose/quit → persist again.

Only **Fase 6 — SwingView** remains, and it's now well underway.

**GUI progress as of 2026-07-05 (2nd session, big jump):**

Files: `mazie/src/main/java/mazie/view/gui/` → `GuiView.java`, `GamePanel.java`, `WelcomePanel.java`, `NewOrLoadGamePanel.java`, `SelectHeroPanel.java`, `ConfirmPanel.java`, `EndPanel.java`, `DirectionPanel.java`, `ArtifactPanel.java`, `RunPanel.java`, `LevelUpPanel.java`, `HeroPanel.java`, `DirectionButton.java`, `YesNoButton.java`, `YesNoButtonPanel.java`, `ThemeColor.java`.

**Interface change:** `showHeroStats(Hero)` was **removed from `GameView`** — showing a hero's stats always happened together with a yes/no confirmation in practice (`confirmHero`), so the separate method was redundant. `ConfirmPanel` now covers that ground on its own (question label + `HeroPanel` + `YesNoButtonPanel`). If this comes up again: this was a deliberate interface simplification, not an oversight — don't re-add `showHeroStats` without the student asking.

**Architecture, settled (unchanged from before, still holding up well under more screens):**

- `GamePanel` is the single persistent container, with two independent, separately-managed slots:
  - `subPanel` (`BorderLayout.CENTER`) — the current "screen". Swapped via a private `setSubPanel(JPanel)` that removes only the previous `subPanel`, not the whole panel tree.
  - `log` (`BorderLayout.SOUTH`) — a single-line `JLabel` status message, swapped via `setLog(text)`/`clearLog()`. Now has two visual variants: `setLog(...)` (plain white text, routine status) and `setErrorLog(...)` (black text on yellow, opaque background — used for `showError`). This is how the earlier-discussed "log line vs `JOptionPane` for errors" question got resolved: errors stay in the same log slot but get a visually distinct style, keeping the whole UI in one consistent custom-panel look instead of mixing in native OS dialogs.
  - Every screen is its own `JPanel` subclass (SRP) — `GamePanel` itself contains zero building logic.
- Thread hand-off pattern (EDT ↔ controller thread), applied consistently across every interactive screen: `GuiView` creates a `CountDownLatch`/`BlockingQueue`, schedules the panel swap via `invokeLater`, then blocks (`.await()`/`.take()`) on the controller thread; button listeners on the EDT call `.countDown()`/`.put(...)`. `InterruptedException` always becomes `Thread.currentThread().interrupt()` + rethrow as `QuitException`.
- `HeroPanel` is the reusable "hero stats card" (name + stats grid + worn artifacts) — now composed inside `LevelUpPanel`, `ConfirmPanel`, and each row of `SelectHeroPanel`. Good payoff from extracting it early.
- `NewOrLoadGamePanel` (for `askNewGame`) and `SelectHeroPanel` (for `selectHero`) both settled on the same visual language as the rest: custom-styled `JPanel`s with `BlockingQueue` hand-off, not `JOptionPane` — confirms the student's consistency call from last session (custom panels throughout, no native dialogs anywhere so far).
- Considered and rejected along the way (don't re-suggest unless asked): `SwingWorker` for icon loading; a generic `SwingComponentFactory`/`PanelFactory`/Abstract Factory centralizing screen construction; `CardLayout`; `JOptionPane` for any of the interactive screens (student chose full visual consistency instead).

**Done (fully wired in `GuiView`, working):**

- ✅ `showWelcome()` → `WelcomePanel` + `CountDownLatch`
- ✅ `askNewGame()` → `NewOrLoadGamePanel` + `SynchronousQueue<Boolean>`
- ✅ `selectHero(heroes)` → `SelectHeroPanel` (scrollable list of `HeroPanel` rows + select buttons) + `SynchronousQueue<Hero>`
- ✅ `confirmHero(hero)` → `ConfirmPanel` (reuses `HeroPanel`) + `SynchronousQueue<Boolean>`
- ✅ `showStartGame()` → log message
- ✅ `askDirection()` → `DirectionPanel` + `LinkedBlockingQueue<Direction>`
- ✅ `showEmptyStep()` → log message
- ✅ `askFightMonster(monster)` → `RunPanel` + `SynchronousQueue<Boolean>`
- ✅ `showRunSuccess(monster, success)` → log message
- ✅ `showEndGame(win)` → `EndPanel` + `CountDownLatch` (also disposes the frame)
- ✅ `showLevelUp(hero)` → `LevelUpPanel` (reuses `HeroPanel`) + `CountDownLatch`
- ✅ `askKeepArtifact(artifact, hero)` → `ArtifactPanel` + `SynchronousQueue<Boolean>`
- ✅ `showError(error)` → log message, styled distinctly (`setErrorLog`)

**Still `#todo` stubs in `GuiView` — only two left:**

- ❌ `createHero()` — currently returns a hardcoded `new Hero("hare5", HeroType.HARE)`
- ❌ `showFightSummary(String, int)` — currently a no-op

### 🔄 Step-by-step plan for the rest of Fase 6

Almost there — just two methods and then an end-to-end test:

1. **`showFightSummary(fightSummary, xpGained)`** — do this first, it's the easy one: a log message, exactly the same pattern as `showRunSuccess`/`showEmptyStep`/`showStartGame` (`panel.setLog(...)` with the formatted text). No new panel needed.
2. **`createHero()`** — the last real screen to build: needs actual text/dropdown input (name + `HeroType`), unlike every other screen so far which was just buttons. A `JTextField` for the name + `JComboBox<HeroType>` (or 3 buttons, one per type — consistent with how `NewOrLoadGamePanel` used 2 plain buttons instead of a dropdown) stacked with `BoxLayout`, plus a submit button. Returns a `Hero` via a `BlockingQueue<Hero>`, same hand-off pattern as every other screen. Note: validation (`@Valid`/Hibernate Validator) happens in the controller, not the view — `createHero()` just needs to collect and return the raw input; if it's invalid, `showError(...)` fires and `createHero()` gets called again by the controller (confirm this loop still matches `GameController.createValidHero()`).
3. **End-to-end test**: `GameController` needs zero changes (confirmed — it only calls `view.xxx()`, doesn't know it's Swing). Run `java -jar target/mazie.jar gui` and manually walk through every scenario already verified for `TerminalView`: new game → create hero → confirm → play → empty steps → fight/run → level up → artifact drop → win/lose → relaunch → load game → select existing hero → confirm → play again.
4. **Once step 3 passes**: Fase 6 (and the whole mandatory subject checklist) is done. Revisit the "Still open / low priority" list below only after that.

### Still open / low priority, not blocking Fase 6

- **Submission hygiene** in `GameController`: remove debug `System.out.println`s in `setup()`, `gameLoop()`, `turn()` (and in `GameEngine.fightRound()`); verify `>` vs `>=` boundary in `Hero.gainXp()`.
- **Ctrl+C (SIGINT) mid-game**: currently only EOF (Ctrl+D) is caught via `QuitException`; SIGINT isn't handled at all and will kill the JVM without persisting. Decide if a `Runtime.getRuntime().addShutdownHook(...)` is worth adding, or leave out of scope. Note: `GuiView`'s blocking calls already rethrow `InterruptedException` as `QuitException`, so a shutdown hook that interrupts the controller thread would already propagate correctly through existing exception handling.
- **`MonsterFactory` refactor** (nice-to-have, see dedicated section below) — cosmetic gameplay-variance polish, not a requirement. Do not start before Fase 6 is fully done and tested.

---

## NICE-TO-HAVE — MonsterFactory refactor (not started, low priority)

**⚠️ Do not start this before Fase 6 (SwingView) is fully done and tested.** This is polish, not a requirement — the game already works and is balanced without it.

**Why this idea came up:** balance-testing revealed that FROG/HARE die in a very narrow, predictable level range (their fights resolve in few rounds → low variance → outcomes cluster tightly), while BEAR (many rounds per fight due to low attack) naturally has a much wider death-level spread from pure RNG variance. Giving each monster *species* (not just each tier) its own stat profile — e.g. a capybara is a tank (high defence/hp, lower attack), a mosquito is a glass cannon (low defence, decent attack) — would introduce natural per-encounter variance for fast fighters too, without bolting on a generic random multiplier.

**Two design options discussed:**

1. **Minimal**: keep `Monster` as one concrete class; `Monster.easy()/medium()/hard()` picks a random name **and** a matching stat-multiplier (e.g. a small per-species multiplier table). Small change, no new class hierarchy, less time.
2. **Full OOP refactor (student's preference)**: `Monster` becomes `abstract class Monster`; one subclass per animal (`Butterfly`, `Fish`, `Hamster` for easy; `Cat`, `Mosquito`, `Cow`, `Seal` for medium; `Tiger`, `Shark`, `Capybara` for hard), each overriding its own attack/defence/hp multipliers relative to the tier baseline. A new `MonsterFactory` class picks a random subclass per tier (`MonsterFactory.easy(heroLevel)` etc.), replacing the current `Monster.easy/medium/hard()` static factory methods. `GameEngine`'s calls to `Monster.easy(...)` etc. need to change to `MonsterFactory.easy(...)`.

**Suggested incremental approach if/when picked up:**

1. Add the abstract `Monster` class + just one subclass per tier as a proof of concept (e.g. only `Capybara` and `Mosquito`) to validate the design compiles and plays correctly.
2. Fill in the remaining subclasses.
3. Add `MonsterFactory`, update `GameEngine` call sites.

**Still to decide (student's call, not the agent's):** the exact attack/defence/hp multiplier per species — e.g. capybara = tank archetype, mosquito = glass cannon archetype. Suggested starting point discussed but not committed to numbers yet.

---



## Fase 3 — Persistence, final implementation reference

Package: `mazie.repository`. Chosen approach: **SQLite** (bonus — replaces text file requirement). Status: **done**, including controller wiring (Fase 5).

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

**No known bugs in this layer.**

---

## Design decisions made

| Decision | Rationale |
| --- | --- |
| No map displayed to user | Student's deliberate choice — adds mystery, subject doesn't require it |
| Artifact names in Artifact.java, not ArtifactType | Simpler, deadline-focused |
| No MonsterFactory class *(reconsidered 2026-07-04, see Nice-to-have section below)* | Originally: Monster.easy/medium/hard() is already Static Factory Method pattern, extra class adds no value. Reconsidered after balance testing revealed FROG/HARE have too little combat variance (short fights = low variance = death clusters tightly around one level). Per-species subclasses with different stat profiles (tank vs glass cannon) would add natural variance without a global random multiplier. Still a nice-to-have, not started. |
| `Hero.id` field present but 0 until DB assigns it | Needed by DB (PRIMARY KEY). Not used by game logic — only the repository reads/writes it. Private constructor + `setId()` used during load. |
| `gainXp()` returns boolean (levelup) | Cleaner than separate `levelUp()` call |
| EOF in terminal input throws `QuitException` | `Ctrl+D` closes stdin permanently; bubbling a quit signal avoids infinite retry loops and keeps exit handling centralized |
| LinkedBlockingQueue<Direction> for Swing direction input (not SynchronousQueue) | Same EDT↔controller hand-off pattern as `CountDownLatch` in `showWelcome()`, but needs to carry a value back. `CountDownLatch` was the simpler warm-up case; `BlockingQueue` generalizes it to pass a `Direction`. |
| JOptionPane for Swing dialogs | Blocks naturally, no queue needed |
| Static Factory Methods instead of Builder pattern | Subject *hints* at Builder pattern (Chapter III). Skipped because Hero only has `name + type` as user input (Builder shines for many optional fields), and Monster/Artifact already use Static Factory Methods which are idiomatic for our case. **Be ready to explain this trade-off at peer evaluation** — evaluator may ask where Builder was applied. |
| SQLite dependency in `pom.xml` | Used for the relational-DB bonus (replaces text file requirement). Subject explicitly allows a library for this bonus *"if explicitly justified and serving only that part"*. Justification: SQLite JDBC is the minimal driver needed to implement a relational DB; no ORM or abstraction layer used. |
| Validation trigger in controller, not view | `createValidHero()` validates; the view only collects input. Keeps the view dumb and makes the same validation reusable for the Swing view. |
| `ParameterMessageInterpolator` instead of `buildDefaultValidatorFactory()` | Avoids needing a Jakarta EL implementation (e.g. Expressly) on the classpath — one less dependency to justify against the "no external libraries" rule. Handles `{min}`/`{max}` placeholders, which is all we use. |
| `choose(prompt, Map<String,T>)` generic helper | A `Map` captures *what is valid* (keys) **and** *what it maps to* (values) in one structure, eliminating per-method switches. A `Set` would only validate and force a second switch. |
| Trim + lowercase all input in `scanNextLine()` | Single normalization point: `"  Y  "` works everywhere, and `@Size`/`@NotBlank` see meaningful characters (blocks names like `"        a"`). |
| `Hero.toString()` is debug-only; stats presentation lives in the view | `toString()` convention = logging/debug. Player presentation belongs in the view (MVC) so each view (terminal/Swing) formats independently. `GameView.showHeroStats(Hero)` was later removed entirely — `confirmHero(Hero)` always needed the same stats display anyway, so the separate method was redundant (`TerminalView` prints stats inline in `confirmHero`; `GuiView`'s `ConfirmPanel` composes `HeroPanel`). |
| `HeroRepository.loadAllHeroes()` returns `Map<Integer, Hero>`, not `List<Hero>` | Repository stays keyed by DB id (the natural, free identifier); `TerminalView.selectHero()` converts it internally to `Map<String, Hero>` for its own `choose()` helper. Each layer picks the key type it actually needs — the repository shouldn't need to know about view/UI concerns. |
| `SQLiteHeroMapper` extracted from `SQLiteHeroRepository`; a generic `Utils` class and a separate `HeroSchema` class were not | Row→object mapping has a genuinely different "reason to change" (domain model shape) than SQL/connection code, and needs no `Connection` — a clean SRP-motivated split. A grab-bag `Utils` class (considered for *all* private methods, static, with `Connection` passed as a parameter everywhere) and a separate `HeroSchema` class (schema/DDL only, tried then reverted) were both rejected: they either leak `connection` as a parameter through the whole class or add a whole extra file for 3 lines of one-time DDL — overhead without real benefit at this project's scale. |
| Manual transactions (`setAutoCommit(false)` once + explicit `commit()`/`rollback()` per write) | `save()`/`update()`/`delete()` each touch 2+ tables (hero + artifacts); without a transaction, a failure partway through could leave the DB in an inconsistent state (e.g. hero row saved, artifact insert failed). |

---

## Mandatory requirements checklist (from subject)

> This checklist **is** the subject's mandatory part (V.1 Gameplay, V.2 Features, V.3 Validation) — treat it as the grading rubric. The turn order inside the game loop follows `Mazie.drawio`.

- [x] MVC architecture (Model/View/Controller clearly separated)
- [x] Launch with `java -jar mazie.jar console`
- [ ] Launch with `java -jar mazie.jar gui` — *SwingView nearly done (Fase 6): only `createHero()` and `showFightSummary()` still stubbed, everything else wired and working, see step-by-step plan above*
- [x] Build with `mvn clean package` → produces runnable jar
- [x] No external libraries except Hibernate Validator + SQLite (SQLite justified as bonus DB library)
- [x] Hero persistence (load on start, save on exit) — *wired in Fase 5, per-path saves matching the drawio*
- [x] Create hero flow
- [x] Select existing hero flow
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
- [x] Level up formula: level*1000 + (level-1)^2* 450 — *verify `>` vs `>=` boundary still open*
- [x] 3 artifact types: Weapon (+attack), Armor (+defence), Helm (+hp)
- [x] Annotation-based validation (Hibernate Validator) on user input
- [x] Validation failure highlighted to user

## Bonus checklist

- [x] Hero persistence in relational database instead of text file — *fully wired into `GameController` (Fase 5), playable end-to-end*
- [ ] Switch between console/GUI at runtime without closing

---

## Drawio review (2026-07-03)

`Mazie.drawio` still matches the intended design well — no redraw needed. One deliberate deviation from the literal diagram, worth knowing before touching persistence code again: the diagram shows a dashed "save hero with new xp" arrow after the border-of-map win check, plus a *separate* "save artifact" step after "keep?". The implementation simplifies this to a single `repository.update(hero)` call per event (win, or end of a won fight) instead of saving incrementally at every sub-step — acceptable since the subject has no mid-game crash-durability requirement.

Remaining open question from the diagram's own "MY CURRENT QUESTIONS" box: how to handle Ctrl+C (SIGINT) mid-game — see next-session priority #7 above.

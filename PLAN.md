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

**Fase 6 — SwingView is DONE (2026-07-05).** Every `GameView` method is implemented in `GuiView`/`GamePanel`, project compiles clean, and the full manual end-to-end playthrough of `java -jar target/mazie.jar gui` has been verified by the student (new game → create hero → confirm → play → empty steps → fight/run → level up → artifact drop → win/lose → relaunch → load game → select existing hero → confirm → play again — all working). **The entire mandatory subject checklist is now done.** Remaining work on this project is optional polish only (see "Still open / low priority" below).

### GUI architecture reference (for peer-evaluation defense)

Files: `mazie/src/main/java/mazie/view/gui/` → `GuiView.java`, `GamePanel.java`, `WelcomePanel.java`, `NewOrLoadGamePanel.java`, `SelectHeroPanel.java`, `ConfirmPanel.java`, `NewHeroPanel.java`, `DirectionPanel.java`, `ArtifactPanel.java`, `RunPanel.java`, `LevelUpPanel.java`, `EndPanel.java`, `HeroPanel.java`, `DirectionButton.java`, `YesNoButton.java`, `YesNoButtonPanel.java`, `ThemeColor.java`.

**`GamePanel`** is the single persistent container, with two independent, separately-managed slots:

- `subPanel` (`BorderLayout.CENTER`) — the current "screen", one `JPanel` subclass per `GameView` method that needs a full screen. Swapped via a private `setSubPanel(JPanel)` that removes only the previous `subPanel`, not the whole panel tree. Every screen owns its own construction (SRP) — `GamePanel` itself contains zero building logic.
- `log` (`BorderLayout.SOUTH`) — a single-line `JLabel` status message for the passive `show*` methods, with two styles: `setLog(...)` (plain, routine status) and `setErrorLog(...)` (black-on-yellow, `showError`).

**Thread hand-off (EDT ↔ controller thread)**, identical pattern for every interactive method: `GuiView` creates a `CountDownLatch`/`BlockingQueue`, schedules the panel swap via `invokeLater`, then blocks (`.await()`/`.take()`) on the controller thread; button listeners on the EDT call `.countDown()`/`.put(...)`. `InterruptedException` always becomes `Thread.currentThread().interrupt()` + rethrow as `QuitException`. No `JOptionPane` used anywhere — every interactive screen is a custom `JPanel` for full visual consistency.

**Reused building blocks:** `HeroPanel` (stats card: name + stats grid + worn artifacts) is composed inside `LevelUpPanel`, `ConfirmPanel`, and each row of `SelectHeroPanel`. `YesNoButtonPanel` is composed inside `ArtifactPanel`, `RunPanel`, `ConfirmPanel`.

**Interface change:** `GameView.showHeroStats(Hero)` was removed entirely — always used together with a yes/no confirmation in practice, so folded into `confirmHero(Hero)`.

**Swing lessons learned (keep in mind if touched again):**

- `ButtonGroup` (mutual exclusion) + `JToggleButton` (persistent selected state) + `ItemListener`/`itemStateChanged` (fires for both the newly-selected **and** newly-deselected button) is the combo used in `NewHeroPanel` for the 3 `HeroType` buttons. A plain `ActionListener` does *not* work for this — it only fires for the button physically clicked, not the one a `ButtonGroup` silently deselects as a side effect.
- On Windows, the native/default Look & Feel largely **ignores `setBackground()`/`setForeground()` on `JButton`/`JToggleButton`** (native chrome paints over custom colors). `NewHeroPanel` ended up using `setForeground()` only for selection styling. If background-color styling is needed again: either set a pure-Java L&F (`UIManager.setLookAndFeel(Metal/Nimbus...)`, once at startup) or use a `Border` instead (reliably painted by Swing regardless of L&F — see `ArtifactPanel`/`RunPanel`'s `BorderFactory.createLineBorder(...)`).
- Considered and rejected (don't re-suggest unless asked): `SwingWorker` for icon loading; a centralized `PanelFactory`/Abstract Factory for screen construction; `CardLayout`.

### Still open / low priority, not blocking anything

- **Submission hygiene** in `GameController`: remove debug `System.out.println`s in `setup()`, `gameLoop()`, `turn()` (and in `GameEngine.fightRound()`); verify `>` vs `>=` boundary in `Hero.gainXp()`.
- **Ctrl+C (SIGINT) mid-game**: currently only EOF (Ctrl+D) is caught via `QuitException`; SIGINT isn't handled at all and will kill the JVM without persisting. Decide if a `Runtime.getRuntime().addShutdownHook(...)` is worth adding, or leave out of scope. `GuiView`'s blocking calls already rethrow `InterruptedException` as `QuitException`, so a shutdown hook that interrupts the controller thread would already propagate correctly.
- **`MonsterFactory` refactor** (nice-to-have, see dedicated section below) — cosmetic gameplay-variance polish, not a requirement.

---

## NICE-TO-HAVE — MonsterFactory refactor (not started, low priority)

**⚠️ Purely optional polish, not a subject requirement.** The game already works and is balanced without it.

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
- [x] Launch with `java -jar mazie.jar gui` — *Fase 6 done, full manual end-to-end playthrough verified 2026-07-05*
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

Remaining open question from the diagram's own "MY CURRENT QUESTIONS" box: how to handle Ctrl+C (SIGINT) mid-game — see "Still open / low priority" list above.

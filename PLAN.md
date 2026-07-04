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

**2026-07-04 session note:** `Monster.java`'s easy/medium/hard stat formulas were hand-tuned via a throwaway simulation tool (since removed from `Main.java` again) to get sensible death-level distribution (roughly: safe before level 5, gets dangerous around level 8, risky at level 10+). The student experimented with additional per-`HeroType` mechanics (an xp-gain multiplier, asymmetric base HP, combat variance) to fine-tune this further, then **deliberately reverted those experiments** in `HeroType.java`, `Hero.java`, and `Main.java` to keep writing the balance logic themselves — `Monster.java`'s tuned values are the only lasting change from that session. This is fine and expected; don't reintroduce the reverted mechanics without the student asking again.

**⚠️ IMPORTANT — read this before doing anything else:** the student spent an entire evening (2026-07-04) tuning gameplay balance instead of starting Fase 6 (SwingView), which is genuinely procrastination-via-a-fun-task avoiding the hard/new one (threading is a new concept, gameplay tuning is familiar territory). Gameplay balance is **not graded pass/fail**. SwingView **is a mandatory subject requirement** (`java -jar mazie.jar gui` must work) and is the **only remaining mandatory item not yet started**. **If the student starts talking about more gameplay/monster/balance tweaks again before Fase 6 is done, remind them clearly: finish the mandatory Swing requirement first.** Be kind about it — this is an ADHD/avoidance pattern, not laziness, and deadline stress is real — but be firm about the priority order.

Only **Fase 6 — SwingView** remains, split into sub-phases below.

**GUI progress as of 2026-07-05 — build on this, do NOT restart:**

Files: `mazie/src/main/java/mazie/view/gui/` → `GuiView.java`, `GamePanel.java`, `DirectionButton.java`, `ThemeColor.java`.

- ✅ **Fase 6.1 welcome screen** — `GuiView`/`GamePanel` set up (`JFrame`+`BorderLayout`, icon/logo loaded via `getClass().getResource(...)` — classpath, not file path, works in the jar). `GamePanel.setWelcomePanel(latch)` swaps content (`removeAll()`+`add()`+`revalidate()`+`repaint()`, all inside `GamePanel` itself).
- ✅ **`showWelcome()` fully working**: `CountDownLatch(1)` pattern — `GuiView` creates latch, schedules panel build via `invokeLater`, blocks on `latch.await()`; Start button's listener (EDT) calls `latch.countDown()`.
- ✅ **Fase 6.2 `askDirection()` fully working** — same hand-off pattern, upgraded to carry a value: `LinkedBlockingQueue<Direction>`. `GuiView.askDirection()` creates queue, schedules `panel.setDirectionPanel(queue)`, blocks on `queue.take()`. `DirectionButton extends JButton` (dumb: only knows its own `Direction` + look, exposes `getDirection()`); `GamePanel.setListener(button, queue)` wires the actual `queue.put(...)` listener — deliberate split so `DirectionButton` doesn't need to know about `BlockingQueue`.
- ✅ Both blocking methods handle `InterruptedException` the same way: `Thread.currentThread().interrupt()` + rethrow as `QuitException` (propagates up like the terminal's Ctrl+D handling — reuses existing exception, no new mechanism needed).
- ⚠️ **Known small cleanup, not urgent**: `GamePanel.setDirectionPanel(BlockingQueue queue)` param is a raw type, should be `BlockingQueue<Direction>`.
- ❌ **Not started**: persistent `JTextArea`/`JScrollPane` message log in `GamePanel` (still commented-out fields at top of the class) — needed for `showEmptyStep`, `showRunSuccess`, `showFightSummary`, `showLevelUp`, `showHeroStats`, `showError`, etc., all still `#todo` stubs in `GuiView`.
- ❌ **Fase 6.3 not started**: `askNewGame`, `createHero`, `selectHero`, `confirmHero`, `askFightMonster`, `askKeepArtifact` all still stub returns in `GuiView` — plan is `JOptionPane.showXxxDialog(...)` for each (blocks the calling thread on its own, no queue needed).
- Considered and rejected along the way (don't suggest again unless asked): `SwingWorker` for icon loading (not slow enough to justify it); a generic `SwingComponentFactory`/`ComponentFactory` holding the direction-button-building logic (mixed domain knowledge like `Direction`/`BlockingQueue` into what should be a purely visual factory — `DirectionButton` as its own class was the cleaner fix).

### 🔄 Fase 6.1 — remaining: game screen message log

Add non-editable `JTextArea` wrapped in `JScrollPane` to `GamePanel` for messages/stats (the fields are already stubbed out in comments at the top of the class). Needed before `GuiView`'s remaining `show*` methods can do anything real.

### 🔄 Fase 6.3 — Modal dialogs

For `askFightMonster`, `askKeepArtifact`, `confirmHero`, `createHero`, `askNewGame`, `selectHero`: use `JOptionPane.showXxxDialog(...)`, which already blocks the calling thread — no queue needed for these.

### 🔄 Fase 6.4 — Wire in + end-to-end test

- `GameController` needs zero changes (confirmed) — only calls `view.xxx()`, doesn't know it's Swing.
- `java -jar target/mazie.jar gui` should run the full game: create/select hero → persist → play → win/lose/quit → persist again.
- Manually test the same scenarios already verified for `TerminalView`.

---

## NICE-TO-HAVE — MonsterFactory refactor (not started, low priority)

**⚠️ If the student asks about this again: remind them to finish the "Next-session priorities" must-haves above first.** This is polish, not a requirement — the game already works and is balanced without it.

**Why this idea came up (2026-07-04):** balance-testing revealed that FROG/HARE die in a very narrow, predictable level range (their fights resolve in few rounds → low variance → outcomes cluster tightly), while BEAR (many rounds per fight due to low attack) naturally has a much wider death-level spread from pure RNG variance. Giving each monster *species* (not just each tier) its own stat profile — e.g. a capybara is a tank (high defence/hp, lower attack), a mosquito is a glass cannon (low defence, decent attack) — would introduce natural per-encounter variance for fast fighters too, without bolting on a generic random multiplier.

**Two design options discussed:**

1. **Minimal**: keep `Monster` as one concrete class; `Monster.easy()/medium()/hard()` picks a random name **and** a matching stat-multiplier (e.g. a small per-species multiplier table). Small change, no new class hierarchy, less time.
2. **Full OOP refactor (student's preference)**: `Monster` becomes `abstract class Monster`; one subclass per animal (`Butterfly`, `Fish`, `Hamster` for easy; `Cat`, `Mosquito`, `Cow`, `Seal` for medium; `Tiger`, `Shark`, `Capybara` for hard), each overriding its own attack/defence/hp multipliers relative to the tier baseline. A new `MonsterFactory` class picks a random subclass per tier (`MonsterFactory.easy(heroLevel)` etc.), replacing the current `Monster.easy/medium/hard()` static factory methods. `GameEngine`'s calls to `Monster.easy(...)` etc. need to change to `MonsterFactory.easy(...)`.

**Suggested incremental approach if/when picked up:**

1. Add the abstract `Monster` class + just one subclass per tier as a proof of concept (e.g. only `Capybara` and `Mosquito`) to validate the design compiles and plays correctly.
2. Fill in the remaining subclasses.
3. Add `MonsterFactory`, update `GameEngine` call sites.

**Still to decide (student's call, not the agent's):** the exact attack/defence/hp multiplier per species — e.g. capybara = tank archetype, mosquito = glass cannon archetype. Suggested starting point discussed but not committed to numbers yet.

---

## Next-session priorities (top of stack)

Welcome screen + `askDirection()` are done and working (see GUI progress note above) — the hardest conceptual part (EDT↔controller thread hand-off) is solved and understood. Remaining work is more mechanical from here.

1. **🚨 Fase 6.1 remainder — message log in `GamePanel`**: add `JTextArea`+`JScrollPane` (fields already stubbed in comments). Needed before any `show*` method in `GuiView` can do something real. START HERE.
2. **Fase 6.3 — modal dialogs**: `JOptionPane.showXxxDialog(...)` for `askNewGame`, `createHero`, `selectHero`, `confirmHero`, `askFightMonster`, `askKeepArtifact` — these block on their own, no queue/latch needed.
3. **Fase 6.4**: wire remaining `GuiView` stubs to the message log + dialogs, then manually test `java -jar target/mazie.jar gui` end-to-end (same scenarios as `TerminalView`).
4. **Small cleanup**: `GamePanel.setDirectionPanel` param is a raw `BlockingQueue`, should be `BlockingQueue<Direction>`.
5. **Submission hygiene** in `GameController`: remove debug `System.out.println`s in `setup()`, `gameLoop()`, `turn()` (and in `GameEngine.fightRound()`); verify `>` vs `>=` boundary in `Hero.gainXp()`.
6. **Manually test the full persistence loop end-to-end** (console mode): create → confirm → play → win/lose/quit → relaunch → confirm the hero shows up correctly (or is gone, if deleted on loss) in `selectHero`.
7. **Ctrl+C (SIGINT) mid-game**: currently only EOF (Ctrl+D) is caught via `QuitException`; SIGINT isn't handled at all and will kill the JVM without persisting. Decide if a `Runtime.getRuntime().addShutdownHook(...)` is worth adding, or leave out of scope. Note: `GuiView`'s blocking calls (`showWelcome`/`askDirection`) already rethrow `InterruptedException` as `QuitException`, so a shutdown hook that interrupts the controller thread would already propagate correctly through existing exception handling.
8. **⚠️ NICE-TO-HAVE, NOT URGENT, DO NOT START BEFORE ITEM 1-3 ARE DONE: `MonsterFactory` refactor** (see dedicated section below). If the student brings this up again, remind them: SwingView first, this is polish on top of an already-working, already-tuned balance, not a blocker.

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
| `Hero.toString()` is debug-only; stats layout in `showHeroStats` | `toString()` convention = logging/debug. Player presentation belongs in the view (MVC) so each view (terminal/Swing) formats independently. |
| `HeroRepository.loadAllHeroes()` returns `Map<Integer, Hero>`, not `List<Hero>` | Repository stays keyed by DB id (the natural, free identifier); `TerminalView.selectHero()` converts it internally to `Map<String, Hero>` for its own `choose()` helper. Each layer picks the key type it actually needs — the repository shouldn't need to know about view/UI concerns. |
| `SQLiteHeroMapper` extracted from `SQLiteHeroRepository`; a generic `Utils` class and a separate `HeroSchema` class were not | Row→object mapping has a genuinely different "reason to change" (domain model shape) than SQL/connection code, and needs no `Connection` — a clean SRP-motivated split. A grab-bag `Utils` class (considered for *all* private methods, static, with `Connection` passed as a parameter everywhere) and a separate `HeroSchema` class (schema/DDL only, tried then reverted) were both rejected: they either leak `connection` as a parameter through the whole class or add a whole extra file for 3 lines of one-time DDL — overhead without real benefit at this project's scale. |
| Manual transactions (`setAutoCommit(false)` once + explicit `commit()`/`rollback()` per write) | `save()`/`update()`/`delete()` each touch 2+ tables (hero + artifacts); without a transaction, a failure partway through could leave the DB in an inconsistent state (e.g. hero row saved, artifact insert failed). |

---

## Mandatory requirements checklist (from subject)

> This checklist **is** the subject's mandatory part (V.1 Gameplay, V.2 Features, V.3 Validation) — treat it as the grading rubric. The turn order inside the game loop follows `Mazie.drawio`.

- [x] MVC architecture (Model/View/Controller clearly separated)
- [x] Launch with `java -jar mazie.jar console`
- [ ] Launch with `java -jar mazie.jar gui` — *SwingView stubs only (Fase 6)*
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
- [x] Level up formula: level*1000 + (level-1)^2 * 450 — *verify `>` vs `>=` boundary still open*
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

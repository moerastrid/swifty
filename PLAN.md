# Swingy — Project Plan & Handoff

## Source of truth

- **Subject PDF**: `swingy.en.subject.pdf` in the root of this repo — this is the ULTIMATE source of truth for requirements.
- **Flowchart**: `Mazie.drawio` in the root — student's own design, used to derive the GameView interface. Open with draw.io desktop or diagrams.net.
- **This file**: current plan + wishlist, kept up to date by the agent as work progresses. This is the plan, not a changelog — finished work is summarized briefly, not narrated in detail. Detailed rationale for decisions that are done and settled belongs in memory (`/memories/repo/`) or git history, not here.

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

## Current status (re-verified 2026-07-06)

The entire **mandatory checklist is done and re-verified against the subject PDF + a fresh code read** (see checklist below — every item confirmed in code, not just assumed). Console and GUI modes are both fully playable end-to-end. Bonus: SQLite persistence is done; runtime console/GUI switching is not.

The project is functionally complete. All remaining work is the student's polish/quality wishlist below — nothing here is blocking or required for the mandatory grade.

---

## Wishlist — next up

Organized by topic. Items with an *(agent note: ...)* were checked against the current code on 2026-07-06 — read those before starting that item, they usually already point at the exact file/line and the trade-off to explain to the student.

### General / robustness

- [ ] **Handle Ctrl+C (SIGINT) gracefully.** Window-close (clicking the X) is already handled (see Architecture reference below); SIGINT is not — it still kills the JVM with no save. A `Runtime.getRuntime().addShutdownHook(...)` registered once in `Main.main()` is the idiomatic option, interrupting the controller thread — needs its own careful look since a shutdown hook runs on yet another thread.
- [ ] **Decide what happens after a player wins.** Quit entirely? Back to the main/new-game menu? Offer to immediately start a fresh map with the same hero?
- [ ] Write player-facing instructions / how-to-play text.
- [ ] *(idea, not decided)* Consider `java.util.logging` instead of scattered `System.err.println(...)` calls — it's part of the JDK itself (no external-library conflict), gives log levels/timestamps/pluggable destinations for free. Currently `System.err.println` is used consistently and is perfectly defensible for a project this size; only worth it if more extensive logging is wanted later, not just for the one or two existing diagnostic prints.

### GitHub / submission

- [ ] Write a README for GitHub.
- [ ] Consider publishing builds under GitHub Releases.

### Persistence

- [ ] Allow deleting a hero from the load-game / select-hero screen.

### High scores (new idea, not designed yet)

- [ ] On win *or* loss, record something about the hero as a persistent "high score" entry (new SQLite table, same repository pattern as `HeroRepository`). Open questions the student still needs to decide: what counts as "the score" (level reached? xp? map size / turns survived?), where it's shown (a leaderboard screen? on the end-game screen?), and whether it's one global list or split per hero type.

### Game engine

- [x] **`MonsterFactory` built (2026-07-07).** `mazie.model.monster.Monster` is now `abstract` (singleton `MonsterFactory` in the same package picks a random subclass per tier: `Library`/`IKEA`/`Park` easy, `Hairsalon`/`Kanteen`/`Office`/`Teams`/`Bus` medium, `Swimmingpool`/`TeamBuilding`/`Supermarket`/`Vim` hard — themed as "everyday annoyances" rather than animals). `GameMap` now owns monster generation via `factory.new{Easy,Medium,Hard}Monster(heroLevel)`; `GameEngine` only consumes `Monster`, doesn't create them anymore. Views already narrate the specific species name (`monster.getName()`) in `RunPanel`/`GamePanel`/`TerminalView` fight/run messages — nice touch, already wired up correctly.
  - [x] `finalMessage` constructor bug fixed (2026-07-07) — `Monster(...)` now correctly assigns the parameter instead of hardcoding `""`.
  - [x] `model/monster/OldMonster.java` deleted (2026-07-07) — dead code from the rename, gone.
  - [x] **Per-species stat variance done (2026-07-07).** Template Method pattern implemented: `Monster`'s constructor takes `(name, actions, finalMessage, heroLevel, baseAttack, baseDefence, baseHp, baseXpReward)` and calls 4 separate `calcAttack/calcDefence/calcHp/calcXpReward(base, lvl)` methods — each monster now passes its own distinct base numbers (tank vs. glass-cannon archetypes across all 12 monsters, see e.g. `Kanteen`/`Teams` as tanks vs. `IKEA`/`Hairsalon`/`TeamBuilding` as glass cannons). `Vim` still has its own deliberately-unbounded `5*lvl`-style base (the intended "unbeatable at high hero level" monster, exempt from the normal balance rules). `TeamBuilding` additionally has a fading `earlyBonus(lvl)` (flat bonus that decays to 0 by level 4) so there's a real, if modest, chance of an unusually rough encounter very early — needed since low levels have very few monster encounters (small maps), so pure "more variance across archetypes" alone couldn't produce any early deaths.
  - [x] **Balance-tuning session done (2026-07-07), stopped short of exact targets — accepted as "good enough".** Built `mazie.game.BalanceSimulation` (`java -jar mazie.jar simulate`) to simulate thousands of lifetimes reusing real `GameMap`/`MonsterFactory`/`GameEngine` logic, reporting: % dead by level 3, % dead by level 8, % still alive at level 12+, and % of heroes that reach level 3 after clearing every monster on map 1. Long iterative session (see repo memory for full blow-by-blow) surfaced 3 real structural bugs along the way, all now fixed: (1) monster growth was accidentally re-added to `xpReward` at one point causing a runaway mid-map leveling snowball — reverted; (2) all monster combat stats sharing one uncapped percentage-growth curve caused a hard death "cliff" around level 7-9 with 0% ever reaching level 12+ — fixed by capping stat growth at a `PEAK_LEVEL`/`HP_PEAK_LEVEL` (attack/defence freeze after level 3, hp after level 1) so growth doesn't compound forever; (3) `hp` never fully resets between maps (only a half-heal on level-up, by design — student wanted this kept), which is *why* growth needed capping at all. Final numbers are a reasonable, cliff-free curve (FROG/HARE/BEAR average death level ~8/~10/~11.5, nobody survives forever, nobody dies instantly) but do **not** hit the original strict per-hero-type percentage targets (8-12% dead by 3, 80-90% dead by 8, ≥5% alive at 12) exactly — student explicitly called a stop on further fine-tuning (cost/time trade-off) and asked to settle for "good enough" over precise numbers. Don't re-open this without being asked; if revisited, start from `BalanceSimulation`'s current output as the baseline, not from scratch.
- [ ] **Write a real fight-summary narration** instead of the current flat string (`GameController.turn()` → `view.showFightSummary("the fight ended", ...)`). Consider monster-specific attack flavor text, and reflecting the hero's equipped weapon in the narration.
- [ ] Decide: does winning actually reward the player with anything besides "you win"? What's the in-game motivation to reach the border? (see also the new "High scores" idea under Persistence above — might answer this.)
- [ ] *(idea recovered from `old/`, not adopted yet)* `old/src/main/java/mazie/model/{Quality.java,util/QualityDefiner.java}` had a small `BAD/NORMAL/GOOD` artifact-quality roll (weighted 1/3/1 via `random.nextInt(5)`) that could scale artifact value independently of the monster's own stats. Current `GameEngine.dropArtifact()` only scales value off monster attack+defence — no quality tier. Could combine nicely with the `MonsterFactory` variance idea above, but is a separate, smaller change if picked up alone.

### Builder pattern (new idea — deliberate learning goal, not just a feature)

- [ ] Student explicitly wants to apply the Builder pattern somewhere and be able to explain it — even as a pure refactor with no new functionality, the goal is understanding it, not adding features. Directly relevant to the existing "No Builder pattern used" defense note in the Architecture reference below (implementing this would replace/soften that trade-off explanation).
  - Best candidate to discuss: `Hero`'s DB-loading constructor (`Hero(int id, String name, HeroType type, int level, int xp, int attack, int defence, int hp)` — 8 positional params, see `Hero.java`) is a reasonable textbook case to start the Builder-vs-telescoping-constructors-vs-static-factory discussion.
  - Worth an honest discussion when picked up: Builder shines with many *optional* params, and this constructor's params are all required — so is Builder the textbook-correct fit here, or is it being deliberately retrofitted to practice the pattern? Both are legitimate reasons to do it, but the student should be able to articulate which one applies for peer-eval defense.

### Swing GUI

- [ ] **Keep the hero's stats visible at all times while playing.** *(agent note: today `HeroPanel` only appears inside `ConfirmPanel`, `LevelUpPanel`, and `SelectHeroPanel` rows — not during `DirectionPanel`/`ArtifactPanel`/`RunPanel`, i.e. not while actually walking/fighting.)*
- [ ] Arrow-key navigation for movement (N/E/S/W) — and consider extending keyboard control to other screens too.
- [ ] Give the fight summary its own dedicated screen, once the narrated version above is written.
- [ ] General look & feel pass — buttons currently use the default OS chrome and the whole UI reads as "very old-school Java". *(agent note: Windows' native L&F ignores `setBackground()`/`setForeground()` on `JButton`/`JToggleButton` — `UIManager.setLookAndFeel(Metal/Nimbus...)` once at startup is the fix if a non-native look is wanted; `Border`s paint reliably regardless of L&F and are already used in `ArtifactPanel`/`RunPanel`.)*
- [ ] Win/lose screen artwork.
- [ ] *(idea recovered from `oldold/`, not adopted yet)* `oldold/src/main/java/ajav/view/swing/SwingGui.java`'s `initFonts()` picked different named system fonts per OS (`os.name` contains `"win"`/`"linux"`/else → e.g. `"Cambria Math"` vs `"DejaVu Math TeX Gyre"` vs `"Serif"`) for header/input/body text, instead of relying on the L&F default font. No actual font *files* exist anywhere in `old/`/`oldold/` — this is a code pattern, not an asset to copy. Worth considering alongside the L&F pass above, though it doesn't solve the button-background-color problem (that needs `UIManager.setLookAndFeel` or `Border`s regardless).

### Visual assets (general)

- [ ] Icons/art for heroes and monsters — ASCII art for the terminal view, PNGs for the Swing view.

### Repo cleanup (`old/` + `oldold/`)

**Scanned 2026-07-06 — nothing essential is missing from the current `mazie/` project.** Both folders are earlier, superseded attempts kept around "just in case"; here's what's in them and why none of it needs porting before deleting them:

- `old/src/main/resources/{mazie-logo.png,mazie-icon.png}` — **already copied into `mazie/src/main/resources/`** and already used (`GuiView`'s frame icon, `WelcomePanel`/`NewOrLoadGamePanel`'s logo). Nothing to do.
- `old/model/HeroFactory.java` + its 5 `HeroType`s (`PENGUIN`/`FROG`/`BEAR`/`HARE`/`TURTLE`, singleton + string-switch factory) — superseded by the current 3-type `Hero(name, type)` design. Not a gap, a deliberate simplification.
- `old/model/artifact/{Weapon,Armor,Helm,Artifact}.java` (one subclass per artifact type) — superseded by the current single `Artifact` value type + `ArtifactType` enum (see "Notes worth remembering for peer-evaluation defense" above — this was a conscious simplification, not an oversight).
- `old/model/util/QualityDefiner.java` + `Quality.java` — the one genuinely reusable *idea*, already folded into the Game engine wishlist above (artifact quality tiers).
- `old/database/HeroDao.java` + `old/controller/{GameValidator,Options,Prompts}.java` — an earlier plain-JDBC DAO + terminal menu system, superseded by the current `SQLiteHeroRepository`/`SQLiteHeroMapper` (transactions, mapper split) and `TerminalView`. Current implementation is strictly more mature; nothing to recover.
- `oldold/` (`ajav.*` package) — the earliest prototype (`old_hero`, a separate console view, no persistence layer at all). The only reusable idea is the OS-aware font-picking pattern, already folded into the Swing GUI wishlist above.

**Proposed next step (not yet done — needs a go-ahead before touching it):** delete `old/` and `oldold/` entirely, and flatten `mazie/` up to the repo root (so `pom.xml`, `src/`, etc. live directly under `swingy/` instead of `swingy/mazie/`). This is a structural change that touches build commands, this plan file, and repo memory — before executing: confirm with the student, then update the "Project structure"/"Build & run commands" section above, `/memories/repo/swingy-progress.md`, and check for any other hard-coded `mazie/`-relative paths (e.g. IDE run configs, `.gitignore`) first.

### Open questions / other

- [ ] **Bonus: switch between console and GUI view at runtime, without restarting.** Not started, no design chosen yet — needs its own investigation (e.g. `GameController.setView()` already exists as a hook, but nothing calls it yet, and swapping the *active* view mid-blocking-call needs real thought).
- [ ] Reconcile Hibernate Validator's rules with what `GameController`/the repository actually check — any duplication or gaps? Needs investigation.
- [ ] Re-verify MVC separation once more against the subject's "only a good and clear implementation will be accepted" — a full, deliberate pass, not just a checklist tick.
- [ ] Re-verify "best practices suited for this problem" — no concrete checklist for this yet; research what's expected, then review class-by-class.

---

## Architecture reference (essential facts for whoever picks this up next)

### GUI (`mazie/view/gui/`)

Files: `GuiView.java`, `GamePanel.java`, `WelcomePanel.java`, `NewOrLoadGamePanel.java`, `SelectHeroPanel.java`, `ConfirmPanel.java`, `NewHeroPanel.java`, `DirectionPanel.java`, `ArtifactPanel.java`, `RunPanel.java`, `LevelUpPanel.java`, `EndPanel.java`, `HeroPanel.java`, `DirectionButton.java`, `YesNoButton.java`, `YesNoButtonPanel.java`, `ThemeColor.java`.

- **`GamePanel`** is the single persistent container with two independently-managed slots: `subPanel` (`BorderLayout.CENTER`, the current "screen" — one `JPanel` subclass per `GameView` method that needs a full screen, swapped via a private `setSubPanel(JPanel)` that removes only the previous `subPanel`) and `log` (`BorderLayout.SOUTH`, a one-line `JLabel` status message, with `setLog(...)` for routine status vs. black-on-yellow `setErrorLog(...)` for `showError`).
- **Thread hand-off (EDT ↔ controller thread)** — identical pattern for every interactive `GameView` method: `GuiView` creates a `CountDownLatch`/`SynchronousQueue`/`LinkedBlockingQueue`, schedules the panel swap via `invokeLater`, then blocks (`.await()`/`.take()`) on the controller thread; button listeners on the EDT call `.countDown()`/`.put(...)`. `InterruptedException` always becomes `controllerThread.interrupt()` + rethrow as `QuitException`. No `JOptionPane` anywhere — every interactive screen is a custom `JPanel`.
- **Window-close handling (added 2026-07-06):** `GuiView` captures `Thread.currentThread()` at construction time as `controllerThread` (valid because `new GuiView()` and the later `controller.start()` call both happen on the same thread — `Main.main()` runs everything sequentially on one thread, no separate thread is ever spun up for the controller). `initFrame()` sets `JFrame.DO_NOTHING_ON_CLOSE` and adds a `WindowAdapter` whose `windowClosing(...)` calls `controllerThread.interrupt()` (wakes whichever blocking call the controller thread is parked in, existing `InterruptedException`→`QuitException` machinery handles the rest, exactly like a terminal Ctrl+D quit) followed by `fr.dispose()`. Calling `dispose()` immediately (rather than waiting for the save to finish) is safe: it only tears down the window/native resources on the EDT and does not affect the JVM's overall liveness — the process still only exits once the controller thread itself finishes its save and `Main.main()` returns naturally, since that's a separate, still-running non-daemon thread. SIGINT (Ctrl+C) is still unhandled — this only covers clicking the window's X.
- **Reused building blocks:** `HeroPanel` (stats card) is composed inside `LevelUpPanel`, `ConfirmPanel`, and each row of `SelectHeroPanel`. `YesNoButtonPanel` is composed inside `ArtifactPanel`, `RunPanel`, `ConfirmPanel`.
- **Swing gotchas already learned:** `ButtonGroup` + `JToggleButton` + `ItemListener`/`itemStateChanged` (fires for both the newly-selected *and* newly-deselected button) is the combo `NewHeroPanel` uses for the 3 `HeroType` buttons — a plain `ActionListener` only fires for the button physically clicked. Windows' native L&F ignores `setBackground()`/`setForeground()` on buttons — use `UIManager.setLookAndFeel(...)` or a `Border` instead.
- **Thread-safety audit (done 2026-07-06):** every `GamePanel.setXxxPanel(...)`/`setLog(...)` call is only ever invoked from inside a `GuiView`-scheduled `invokeLater(...)` — since the EDT processes its event queue one event at a time, `subPanel`/`log` are never mutated concurrently (standard Swing single-thread confinement, not manual locking). Full pass over every panel constructor found exactly one live null-safety bug: `NewHeroPanel` could submit with `type == null` if the player never picked a `HeroType` (fixed with an inline error label + `type != null` guard). Every other panel's constructor only ever receives objects the controller already guarantees non-null before the panel is built.

### Exception handling (`mazie.exception`, `Main`, `GuiView`) — done 2026-07-06

- **Exception hierarchy**: `QuitException` (user-initiated quit, always unchecked `RuntimeException`), `ModelException` (domain-invariant violations, e.g. `Hero.setWeapon(...)` given the wrong artifact type), `FatalException` (startup/environment failures, e.g. DB connection), `ParseException` (bad CLI args), `RepositoryException` (DB read/write failures — always caught locally in `GameController`, never reaches `Main`).
- **`Main.main()`** wraps `run(args)` in one top-level catch mapping each exception type to a `sysexits.h`-style exit code (`EX_USAGE=64`, `EX_UNAVAILABLE=69`, `EX_SOFTWARE=70`, `QuitException→EX_SUCCESS=0`, generic `Throwable→EX_GENERAL=1`) — named constants, not magic numbers. A trailing `System.exit(EX_SUCCESS)` after the try/catch handles the normal (no-exception) completion path. All `System.exit(...)` calls are deliberately confined to `Main` only, never called from deeper in the code (including the EDT-crash handler below) — kept as a hard rule the student wanted, similar to C conventions.
- **EDT uncaught-exception safety net**: `Main.threadConfig()` (a small Spring-`@Configuration`-style setup method) registers `Thread.setDefaultUncaughtExceptionHandler(...)` once, capturing `Thread.currentThread()` (the main/controller thread) at that point. If an exception escapes Swing's EDT event-dispatch loop (the one thread-safety gap `Main`'s own try/catch can never cover, since exceptions don't propagate across threads), the handler logs it, sets `private static volatile boolean edtCrashed`, and calls `mainThread.interrupt()` to unblock whatever `GuiView` blocking call the controller thread is parked in — reusing the exact same `InterruptedException`→`QuitException` machinery as a normal quit/window-close, so a best-effort save still happens. `main()` checks `edtCrashed` *after* `run(args)` returns normally and exits with `EX_SOFTWARE` instead of `EX_SUCCESS` if it was set — deliberately not disguising a crash as a clean exit code 0.
  - `edtCrashed` is `volatile` — not strictly required here (the JLS guarantees a happens-before edge between `Thread.interrupt()` and any later code that detects the interruption, e.g. every `catch (InterruptedException e)` in `GuiView`, which chains through to the read in `main()`), but kept for documentation clarity and to not rely on that structural ordering staying intact forever.
  - Registered unconditionally (not just for GUI mode) — harmless in console mode since `Main`'s own top-level catch already catches everything before it could ever reach this global fallback there.

### Persistence (`mazie.repository`)

SQLite (bonus, replaces the text-file requirement). `HeroRepository` interface (`loadAllHeroes/save/update/delete`) → `SQLiteHeroRepository` (connection/transaction lifecycle + CRUD) → `SQLiteHeroMapper` (ResultSet → Hero/Artifact mapping, split out for SRP).

- Schema: `hero(id, name, type, level, xp, attack, defence, hp)`, `artifact(id, name, type, value, hero_id → hero.id)`. `hero.type` CHECK `'FROG'/'HARE'/'BEAR'`, `artifact.type` CHECK `'WEAPON'/'ARMOUR'/'HELMET'`. `PRAGMA foreign_keys = ON`.
- All numeric/text columns are `NOT NULL` with `CHECK` constraints against bad hand-edited data (`level >= 1`, `xp/attack/defence/value >= 0`, `name` length between 2\u201330) — deliberately **excluding** `hero.hp`, which gets `NOT NULL` but no `CHECK(hp >= 0)`: a hero's raw `hp` column can legitimately go negative mid-combat while a `HELMET` artifact keeps their *total* hp (`hp + helmet.value`) positive, and SQLite `CHECK` constraints can't reference another table's row to enforce that combined invariant without a `TRIGGER` — left as an app-layer invariant (`GameEngine`'s `getTotalHp() > 0` check) instead.
- Manual transactions: `setAutoCommit(false)` once in the constructor; every write method ends with commit on success / rollback in the catch block (needed since `save`/`update`/`delete` each touch 2+ tables).
- `update()` is full-replace, not diffing: delete all artifacts for `hero_id`, update the hero row, re-insert current artifacts — fine since a hero has ≤3 artifacts with no persisted identity to diff against.
- `delete()` order: `artifact` rows first (FK constraint), then `hero`.
- Do **not** run Hibernate Validator on heroes loaded from the DB — validation is for fresh user input only.
- All writes use `PreparedStatement` with `?` placeholders (SQL-injection safety) — see the JDBC/SQLite cheat-sheet in user memory for exact syntax if needed again.

### Notes worth remembering for peer-evaluation defense

- **No Builder pattern used**, even though the subject hints at it (Chapter III). Static Factory Methods were used instead (`Monster.easy/medium/hard()`, `Artifact.weapon/armour/helmet(...)`) — justified because `Hero` only takes `name + type` from the user (Builder shines with many optional fields), and Static Factory is already idiomatic for `Monster`/`Artifact`. Be ready to explain this trade-off if asked "where's your Builder?".
- **SQLite dependency justification**: allowed under the subject's bonus-library exception ("if explicitly justified and serving only that part") — it's the minimal JDBC driver needed for a relational DB, no ORM/abstraction layer on top.
- **`ParameterMessageInterpolator`** is used instead of `buildDefaultValidatorFactory()`'s default, to avoid needing a Jakarta EL implementation on the classpath as an extra dependency to justify.
- Validation is triggered in the controller (`GameController.createValidHero()`), not the view — keeps views dumb/reusable across terminal + Swing.

---

## Mandatory requirements checklist (from subject) — re-verified 2026-07-06

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
- [x] Square map, size = (level-1)\*5+10-(level%2) (confirmed in `GameMap` constructor, matches formula exactly)
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
- [ ] Switch between console/GUI at runtime without closing — see Open questions wishlist above

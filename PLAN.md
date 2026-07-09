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

## Current status (2026-07-09)

All mandatory requirements are implemented and match the subject (see checklist below). Console and GUI modes are both fully playable end-to-end. Bonus: SQLite persistence is done; runtime console/GUI switching is not started. Remaining work is the polish/quality wishlist below — nothing there is blocking or required for the mandatory grade.

---

## Wishlist — next up

Organized by topic.

### General / robustness

- [ ] **Handle Ctrl+C (SIGINT) gracefully.** Window-close (clicking the X) is already handled (see Architecture reference below); SIGINT is not — it still kills the JVM with no save. A `Runtime.getRuntime().addShutdownHook(...)` registered once in `Main.main()` is the idiomatic option, interrupting the controller thread — needs its own careful look since a shutdown hook runs on yet another thread.
- [ ] **Decide what happens after a player wins.** Quit entirely? Back to the main/new-game menu? Offer to immediately start a fresh map with the same hero?
- [ ] Write player-facing instructions / how-to-play text.
- [ ] *(idea, not decided)* Consider `java.util.logging` instead of scattered `System.err.println(...)` calls.

### GitHub / submission

- [ ] Write a README for GitHub.
- [ ] Consider publishing builds under GitHub Releases.

### Persistence

- [ ] Allow deleting a hero from the load-game / select-hero screen.

### High scores (new idea, not designed yet)

- [ ] On win *or* loss, record something about the hero as a persistent "high score" entry (new SQLite table, same repository pattern as `HeroRepository`). Open questions: what counts as "the score" (level? xp? turns survived?), where it's shown, whether it's global or per hero type.

### Game engine

- [ ] Confirm `Hero.gainXp(xp)` intentionally no longer resets `xp` on level-up (it used to subtract `xpNeed`; now `xp` keeps accumulating past the threshold) — check nothing downstream assumes the old per-level reset.
- [ ] Reconsider exception type: `SQLiteHeroMapper.convertHeroType(...)` now throws `IllegalArgumentException` (was `RepositoryException`, still is in `convertArtifactType`) — `Main`'s catch merges `ModelException | IllegalArgumentException`, mixing bad-user-input with corrupt-persisted-data handling. Probably should stay a `RepositoryException`.
- [ ] `GameController.turn()` — split into smaller methods (currently 6 if-statements, student's own `#todo`).
- [ ] **Write a real fight-summary narration** instead of the current flat string (`GameController.turn()` → `view.showFightSummary("the fight ended", ...)`). Consider monster-specific attack flavor text and reflecting the hero's equipped weapon.
- [ ] Decide: does winning reward the player with anything besides "you win"? (see High scores idea above — might answer this.)
- [ ] *(idea recovered from `old/`, not adopted)* `old/model/{Quality,util/QualityDefiner}.java` had a `BAD/NORMAL/GOOD` artifact-quality roll independent of monster stats — current `dropArtifact()` only scales off `monster.getXpReward()`. Could add a quality tier on top.

### Builder pattern (deliberate learning goal, not just a feature)

- [ ] Student wants to apply the Builder pattern somewhere and be able to explain it. Best candidate: `Hero`'s DB-loading constructor (8 positional params). Worth discussing honestly whether Builder is the textbook-correct fit here (all params required, no optionals) or a deliberate retrofit to practice the pattern — either is fine, but the student should be able to say which for peer-eval defense.

### Swing GUI

- [ ] **Keep the hero's stats visible at all times while playing.** Today `HeroPanel` only appears inside `ConfirmPanel`, `LevelUpPanel`, and `SelectHeroPanel` — not during `DirectionPanel`/`ArtifactPanel`/`RunPanel`.
- [ ] Arrow-key navigation for movement (N/E/S/W), and consider extending keyboard control to other screens.
- [ ] Give the fight summary its own dedicated screen, once the narrated version above is written.
- [ ] General look & feel pass — buttons use default OS chrome. *(Windows' native L&F ignores `setBackground()`/`setForeground()` on `JButton`/`JToggleButton` — `UIManager.setLookAndFeel(Metal/Nimbus...)` or a `Border` are the fixes.)*
- [ ] Win/lose screen artwork.
- [ ] *(idea recovered from `oldold/`, not adopted)* OS-aware named system fonts instead of the L&F default (no font files exist anywhere in `old/`/`oldold/`, just a code pattern).

### Visual assets

- [ ] **Wire up the hero/monster PNGs (added 2026-07-08) into the actual views.** `mazie/src/main/resources/` has `frog.png`/`mouse.png`/`weevil.png` + one PNG per monster species — none are loaded by any view yet (only `mazie-logo.png`/`mazie-icon.png` are). Swing: show in `HeroPanel`/fight panels. Terminal still needs separate ASCII-art versions.

### Repo cleanup (`old/` + `oldold/`)

Both folders are superseded prototypes — nothing essential is missing from `mazie/` (verified 2026-07-06; the only two reusable ideas, artifact quality tiers and OS-aware fonts, are already folded into the wishlist above). **Proposed, not yet done:** delete `old/` and `oldold/`, flatten `mazie/` up to the repo root — needs student go-ahead first (touches build commands, this file, and repo memory).

### Open questions / other

- [ ] **Bonus: switch between console and GUI view at runtime, without restarting.** `GameController.setView()` already exists as a hook but nothing calls it yet.
- [ ] Reconcile Hibernate Validator's rules with what `GameController`/the repository actually check — any duplication or gaps?
- [ ] Re-verify MVC separation once more against the subject's "only a good and clear implementation will be accepted" — a full, deliberate pass.
- [ ] Re-verify "best practices suited for this problem" — no concrete checklist for this yet.

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

- Schema: `hero(id, name, type, level, xp, attack, defence, hp)`, `artifact(id, name, type, value, hero_id → hero.id)`. `hero.type` CHECK is generated from `HeroType.values()` (`'FROG'/'MOUSE'/'WEEVIL'`), `artifact.type` CHECK likewise from `ArtifactType.values()` (`'WEAPON'/'ARMOUR'/'HELMET'`) — both in `SQLiteHeroRepository`, no hand-typed string literals. `PRAGMA foreign_keys = ON`.
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
- [ ] Switch between console/GUI at runtime without closing — see Open questions wishlist above

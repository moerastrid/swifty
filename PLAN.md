# Swingy — Project Plan & Handoff

## Source of truth

- **Subject PDF**: `swingy.en.subject.pdf` in the root of this repo — this is the ULTIMATE source of truth for requirements.
- **Flowchart**: `Mazie.drawio` in the root — student's own design, used to derive the GameView interface. Open with draw.io desktop or diagrams.net.
- **This file**: current plan, to be updated as work progresses.

**Last full review: 2026-07-04** — re-read subject PDF, `Mazie.drawio`, and the actual current codebase (not just this file) to verify everything below is still accurate. See "Drawio review" section further down for details.

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

**2026-07-04 GUI session progress:** the student made real, understood progress on Fase 6.1 — do NOT restart this from scratch next session, build on it. What exists now:

- `mazie/src/main/java/mazie/view/gui/GuiView.java`: `JFrame` set up (`initFrame()`), icon loaded from classpath (`getClass().getResource("/mazie-icon.png")`, NOT a file path — important, works inside the jar), `SwingUtilities.invokeLater(() -> frame.setVisible(true))` correctly placed as the *last* step after all content is built (student understands *why*: invokeLater tasks run in FIFO submission order, so visibility must be scheduled last).
- `mazie/src/main/java/mazie/view/gui/GamePanel.java`: `extends JPanel`, holds a `welcomePanel()` (logo `ImageIcon` from `/mazie-logo.png` + a "Start" `JButton`), swapped in via `setWelcomePanel(latch)` (`removeAll()` + `add()` + `revalidate()` + `repaint()` — student understands why all four calls are needed, incl. why `removeAll()` matters before swapping content, and why `revalidate/repaint` belongs in `GamePanel` itself rather than being the caller's (`GuiView`'s) responsibility).
- `ThemeColor.java` copied over from `/oldold` as planned (pure color constants, no dependencies).
- `showWelcome()` is **fully implemented and working**, and is a genuinely good solved example of the EDT↔controller hand-off pattern, just simpler than `askDirection()`: it uses a `CountDownLatch(1)` — `GuiView.showWelcome()` creates the latch, schedules `panel.setWelcomePanel(latch)` via `invokeLater`, then calls `latch.await()` on the controller thread; the "Start" button's `ActionListener` (running on the EDT) calls `latch.countDown()`. Student worked through *why* a plain `boolean` flag + busy-wait loop would be wrong here (busy-waiting wastes CPU; more importantly, no `happens-before` guarantee across threads without a proper synchronizer — directly connected back to the `SwingWorker` tutorial text about memory visibility). Also handled `InterruptedException` correctly with `Thread.currentThread().interrupt()` in the catch block, and understands *why* (nothing currently interrupts this thread in practice — it's defensive code, becomes relevant only if a future SIGINT/shutdown-hook feature is added, see next-session priority on Ctrl+C below).
- Student was also corrected on a couple of concrete Swing API mistakes worth remembering if they recur: `JOptionPane` is a modal-dialog component, not a general-purpose layout container (don't `.add()` buttons into one to build a toolbar); `JScrollPane` needs `setViewportView(...)`/constructor-arg, not `.add(...)`, to attach its content.
- Explicitly decided **against** `SwingWorker` for icon loading (loading a small local PNG is near-instant, not the "genuinely slow task" `SwingWorker` is for) — good applied YAGNI reasoning, worth remembering as a talking point for peer evaluation ("I considered X, here's why I didn't need it").

**Still open in Fase 6.1:** the actual game screen (persistent `JTextArea` + `JScrollPane` for messages, plus the N/E/S/W `JButton` panel) has not been built yet inside `GamePanel` — only the welcome screen exists so far. This is the next concrete step before touching `askDirection()` itself.

### 🔄 Fase 6.1 — Static layout (no threading yet)

Build the visual skeleton of `mazie/src/main/java/mazie/view/gui/GuiView.java` without solving the blocking-input problem yet. Hardcode/stub return values (e.g. `askDirection()` always returns `Direction.NORTH`) just to get the UI rendering and navigable.

- `JFrame` with `BorderLayout` ✅ done
- Welcome screen with logo + Start button, actually gating on the click via `CountDownLatch` ✅ done (see session progress note above — this ended up needing real (simple) threading, not just static layout, and that's fine/expected)
- Non-editable `JTextArea` (wrapped in `JScrollPane`) for messages/stats — **not started yet**, next step
- Panel with 4 `JButton`s (N/E/S/W), only enabled during `askDirection()` — **not started yet**, next step
- Reusable from `/oldold/src/main/java/ajav/view/swing/`: `ThemeColor.java` ✅ copied over. The font-per-OS logic in `SwingGui.initFonts()` — student deliberately skipped this for now (deprioritized, not forgotten), fine to pick up later or never if fonts look acceptable as-is.

### 🔄 Fase 6.2 — Blocking direction input (the hard part)

Solve the EDT ↔ controller thread hand-off for `askDirection()`. The controller runs on the main thread and calls `view.askDirection()` expecting a synchronous return value; Swing button clicks fire on the EDT and don't return anything to the caller.

**Good news for next session:** the student already solved a simpler version of exactly this problem in `showWelcome()` (see session progress note above, using `CountDownLatch`). `askDirection()` is the same pattern *plus* needing to pass back a value (`Direction`), which `CountDownLatch` can't carry — so a different tool is needed here. Frame it to the student as "you already did the hard conceptual part, now you need a tool that also carries a value" rather than a brand new problem.

Approaches to look up and choose between:

- `SynchronousQueue<Direction>` — EDT calls `put(dir)`, controller calls `take()`. Direct hand-off, no buffer.
- `BlockingQueue<Direction>` with capacity 1 — similar, slightly more forgiving.
- `CompletableFuture<Direction>` — controller awaits, EDT completes it.

**Note:** `/oldold`'s `SwingGui.getInput()` is a placeholder (`return "input";`) — it does NOT solve this problem, don't expect a shortcut there.

**EDT safety rule to look up:** all Swing UI updates must run on the EDT. Use `SwingUtilities.invokeLater(...)` from non-EDT threads.

### 🔄 Fase 6.3 — Modal dialogs

For `askFightMonster`, `askKeepArtifact`, `confirmHero`, `createHero`, `askNewGame`, `selectHero`: use `JOptionPane.showXxxDialog(...)`, which already blocks the calling thread — no queue needed for these.

### 🔄 Fase 6.4 — Wire in + end-to-end test

- Confirm `GameController` needs zero changes — it only calls `view.askDirection()` etc. and shouldn't know it's talking to Swing. The threading complexity from Fase 6.2 stays fully hidden inside `GuiView`.
- `java -jar target/mazie.jar gui` should run the full game: create/select hero → persist → play → win/lose/quit → persist again.
- Manually test the same scenarios already verified for `TerminalView`.

New file for all of this: `mazie/src/main/java/mazie/view/gui/GuiView.java` (stub already exists), implements `GameView`.

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

1. **🚨 Finish Fase 6.1 — build the actual game screen in `GamePanel`.** Welcome screen is done; still need the persistent `JTextArea`/`JScrollPane` for messages + the N/E/S/W `JButton` panel (still stubbed/no listeners yet). START HERE.
2. **Fase 6.2 — blocking direction input**: solve the EDT-to-controller hand-off for `askDirection()` (see Fase 6.2 below). The student already solved the simpler version of this exact problem in `showWelcome()` using `CountDownLatch` — remind them of that if they feel stuck, it's the same pattern plus needing to carry a `Direction` value back (`SynchronousQueue`/`BlockingQueue`/`CompletableFuture`). Budget real focus time for this, it's normal for it to take longer than expected.
3. **Fase 6.3 & 6.4**: modal dialogs (`JOptionPane` for `askNewGame`, `createHero`, `selectHero`, `confirmHero`, `askFightMonster`, `askKeepArtifact`), then wire in and test end-to-end in GUI mode.
4. **Submission hygiene** in `GameController`: remove debug `System.out.println`s in `setup()`, `gameLoop()`, `turn()` (and in `GameEngine.fightRound()`); verify `>` vs `>=` boundary in `Hero.gainXp()`.
5. **Manually test the full persistence loop end-to-end** (console mode): create → confirm → play → win/lose/quit → relaunch → confirm the hero shows up correctly (or is gone, if deleted on loss) in `selectHero`.
6. **Ctrl+C (SIGINT) mid-game**: currently only EOF (Ctrl+D) is caught via `QuitException`; SIGINT isn't handled at all and will kill the JVM without persisting. Decide if a `Runtime.getRuntime().addShutdownHook(...)` is worth adding, or leave out of scope.
7. **⚠️ NICE-TO-HAVE, NOT URGENT, DO NOT START BEFORE ITEM 1-3 ARE DONE: `MonsterFactory` refactor** (see dedicated section below). If the student brings this up again, remind them: SwingView first, this is polish on top of an already-working, already-tuned balance, not a blocker.

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

Remaining open question from the diagram's own "MY CURRENT QUESTIONS" box: how to handle Ctrl+C (SIGINT) mid-game — see next-session priority #4 above.

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

### ❌ Fase 3 — Persistence (NOT STARTED)

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

### 🔄 Fase 5 — GameController (IN PROGRESS)

Done:

- `setup()`: welcome → ask new/existing → create/select → show stats → confirm → retry on reject. Follows the `Mazie.drawio` GAME SETUP lane.
- `createValidHero()`: loops `view.createHero()` + `validator.validate(hero)`, shows the joined violation messages via `view.showError(...)` until valid. **Validation trigger lives in the controller; the view stays dumb** (subject V.3: annotation-based validation).
- `Validator` built once as a field via `ParameterMessageInterpolator` (no EL dependency needed — keeps the dependency tree minimal for the "no external libraries" rule).
- `QuitException` (EOF) caught in `start()`.

Still to do:

- `gameLoop()` is an empty `#todo` — the next big piece. Must follow the `Mazie.drawio` GAME PLAY lane (see Fase 5 plan below).
- Pass the chosen `hero` into `gameLoop(hero)` (now assigned in `start()` but never read).
- `setup()` still calls `askNewGame()` unconditionally — gate it on "saved heroes exist" once Fase 3 lands.
- Remove the leftover experimental `startGamePlay`, `tryOutHeroes`, `tryOutLogic` before submission.
- Wire `repository.save(...)` into the `finally` block (needs Fase 3).

### ❌ Fase 6 — SwingView (NOT STARTED)

### ❌ Fase 7 — Main + args (NOT STARTED)

---

## Next-session priorities (top of stack)

1. **Fase 5 — `gameLoop()`**: implement the GAME PLAY turn loop following `Mazie.drawio` (show stats → ask direction → move → monster? fight/run → win/lose/level-up/artifact → border = win). The engine methods (`move`, `win`, `runAway`, `fight` → `FightResult`) already exist; this is wiring + view calls. Pass `hero` into `gameLoop(hero)`.
2. **Fase 3 — Persistence**: `HeroRepository` + `TextFileHeroRepository` (subject V.2: heroes saved to a text file on exit, loaded on start). Then gate `askNewGame()` on a non-empty list and fix `selectHero`'s id-key.
3. **Fase 7 — `Main` + args**: parse `args[0]` for `console` / `gui`; `Main` is still a `"hoi :)"` stub that hardcodes `TerminalView`. Mandatory: `java -jar mazie.jar console|gui`.
4. **Fase 6 — `SwingView`**: only after the console flow is fully playable end-to-end.
5. **Verify level-up boundary**: `Hero.gainXp()` uses `this.xp > xpNeed`; the subject says level up when you *reach* the next-level XP — double-check whether `>=` is intended.
6. **Submission hygiene**: remove `startGamePlay`/`tryOut*` debug code; decide on the SQLite dependency (remove if the DB bonus is not done).

---

## Fase 3 — Persistence plan

New package: `mazie.persistence`

Files to create:

- `HeroRepository.java` — interface with two methods:
  - `loadAll()` returns `List<Hero>`
  - `saveAll(List<Hero>)` returns `void`
- `TextFileHeroRepository.java` — implements the interface, reads/writes `heroes.txt`

**Data contract — one hero per line, pipe-separated:**

```java
name|type|level|xp|attack|defence|hp|weapon_name|weapon_val|armour_name|armour_val|helmet_name|helmet_val
```

- Empty artifact slots: empty string for name, 0 for value
- File location: `heroes.txt` next to the jar (working directory)

**Key conventions to look up:**

- `try-with-resources` for file I/O
- `java.nio.file.Files` (read/write) vs old `FileReader` — prefer NIO
- `String.split("\\|", -1)` — the `-1` keeps trailing empty fields (important for empty artifact slots)

**Important rule:** when loading from file, do NOT run Hibernate Validator. Validation is only for fresh user input. Loading uses the private no-args constructor + setters so the type-stat override in the main constructor doesn't fire.

---

## Fase 4 — TerminalView plan

Implement all `GameView` methods in `mazie/src/main/java/mazie/view/terminal/TerminalView.java`.

**Per-method intent:**

- `createHero()`: ask type (via `choose`) + name via Scanner, construct a Hero, return it. **No validation here** — the view stays dumb; the controller's `createValidHero()` runs the Validator and re-asks on violations.
- `selectHero(List<Hero>)`: print numbered list, read int, return the chosen hero, loop on invalid input
- `confirmHero(Hero)`: print hero stats + ask `y/n`
- `askDirection()`: read `n/e/s/w`, loop until valid, return `Direction` enum
- `askFightMonster(Monster)`: show monster stats, read `f` or `r`
- `askKeepArtifact(Artifact, Hero)`: show artifact + current hero (so user has context), read `y/n`
- All `show*` methods: print to `System.out`, use `AnsiColor` enum constants for color

**Helper-method strategy (Rule of Three):**

- Already extracted: `colorPrint(AnsiColor, String)`, `scanNextLine()`
- Likely candidate after 2–3 more methods: `readChoice(prompt, validInputs)` for the y/n / f-or-r / n-e-s-w pattern. Don't extract pre-emptively — wait until the third occurrence to confirm the shape.

**Ctrl+C / Ctrl+D handling:**

- `Scanner.nextLine()` throws `NoSuchElementException` when stdin closes (Ctrl+D on Unix) — caught in `scanNextLine()`
- Implemented: `scanNextLine()` now throws `QuitException` on EOF instead of returning a fallback string/null. This avoids infinite re-prompt loops when stdin is closed.
- Next: catch `QuitException` at controller level (or top-level flow) to save heroes and exit cleanly.
- Ctrl+C terminates the JVM; nothing to do beyond letting the OS handle it

**Hibernate Validator usage (now in the controller, not the view):**

- Validation lives in `GameController.createValidHero()`, not in `createHero()` — the view only collects input.
- `Validator` is a controller field built via `ParameterMessageInterpolator` (no EL dependency).
- `validator.validate(hero)` returns a `Set<ConstraintViolation<Hero>>`; on non-empty, show the joined `getMessage()`s and re-ask; on empty, return the hero.

**Conventions to look up:**

- `Scanner.nextLine()` vs `Scanner.next()` — prefer `nextLine()` to avoid leftover newline issues
- Closing `Scanner` on `System.in` — don't, it closes `System.in` permanently. Keep one Scanner field for the view's lifetime.

---

## Fase 5 — GameController plan

Constructor takes a `GameView` and a `HeroRepository`. Both are interfaces — the controller doesn't know if it's terminal/Swing or text/SQL.

**Setup phase (before game loop):**

1. Load all saved heroes from the repository
2. Show welcome
3. If no saved heroes: go straight to create hero
4. If saved heroes exist: ask new-or-existing, then either create or select
5. Show stats + confirm — if rejected, loop back to step 3
6. Show start-game message, build the `GameEngine`

**Game loop (per turn) — follows `Mazie.drawio`:**

1. Show hero stats
2. Ask direction
3. Move via the engine — returns the monster on the new square, or `null`
4. If a monster is there:
   - Ask fight-or-run
   - If run: call the engine's run-away, show the result. If succeeded → next turn. If failed → fall through to fight.
   - Fight: get the `FightResult` from the engine
     - If lost → show game over, exit loop
     - If won → show battle result; if level-up show that; if artifact dropped ask keep, apply if yes
5. If hero reached the border → show win, exit loop

**After game loop:** save all heroes to the repository.

**Subject-required scaling (must be reflected in `GameEngine`, not in the controller):**

- XP gained on win must scale with the monster's strength (subject: *"Experience points, based on the villain power"*). Already in `Monster.xpReward`.
- Dropped artifact's value must scale with the monster's strength (subject: *"the quality of the artefact also varies depending on the villain's strength"*). Currently `Artifact.weapon(int value)` accepts a value — make sure the engine derives that value from the monster (e.g. `xpReward / N` or `monster.attack`), not from a constant.

**Open question to decide before implementing:**

- On death: remove the dead hero from the saved list, or keep it (level/xp preserved as a memorial)? Subject doesn't specify. Drawio also flags this with "delete hero from database?". Pick one and add it to "Design decisions".

**Conventions to look up:**

- Java labeled `break` / `continue` — useful for cleanly exiting nested loops here
- Why depending on interfaces (not concrete classes) in the constructor matters — Dependency Inversion Principle

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

## Fase 7 — Main plan

Responsibilities of `Main.main(String[] args)`:

1. Verify `args.length >= 1`, otherwise print usage to `System.err` and exit with a non-zero status
2. Based on `args[0]`, instantiate either `TerminalView` or `SwingView`. Unknown value → print error, exit
3. Instantiate `TextFileHeroRepository("heroes.txt")`
4. Instantiate the `GameController` with view + repository and call `start()`

**Conventions to look up:**

- Java 14+ `switch` expression with `->` arrows and `yield` — clean way to map a string to an object
- `System.exit(int)` vs throwing — for a CLI entry point, `exit` is fine

**Future-proofing for the runtime-switch bonus:** if you want to allow switching between console and GUI without restarting, Main should hand the chosen view to the controller, and the controller should accept a way to swap views mid-game. Don't build this now — just don't structure things in a way that makes it impossible.

---

## Design decisions made

| Decision | Rationale |
| --- | --- |
| No map displayed to user | Student's deliberate choice — adds mystery, subject doesn't require it |
| Artifact names in Artifact.java, not ArtifactType | Simpler, deadline-focused |
| No MonsterFactory class | Monster.easy/medium/hard() is already Static Factory Method pattern, extra class adds no value |
| Hero has no `id` field | id is DB concern only, not domain concept |
| `gainXp()` returns boolean (levelup) | Cleaner than separate `levelUp()` call |
| EOF in terminal input throws `QuitException` | `Ctrl+D` closes stdin permanently; bubbling a quit signal avoids infinite retry loops and keeps exit handling centralized |
| SynchronousQueue for Swing direction input | Standard solution for blocking EDT-to-controller communication |
| JOptionPane for Swing dialogs | Blocks naturally, no queue needed |
| Static Factory Methods instead of Builder pattern | Subject *hints* at Builder pattern (Chapter III). Skipped because Hero only has `name + type` as user input (Builder shines for many optional fields), and Monster/Artifact already use Static Factory Methods which are idiomatic for our case. **Be ready to explain this trade-off at peer evaluation** — evaluator may ask where Builder was applied. |
| SQLite dependency in `pom.xml` | Reserved for the relational-DB bonus. Subject explicitly allows libraries for bonus work *"if explicitly justified and serving only that part"*. **If the DB bonus is not done, remove SQLite from `pom.xml` before submission** to stay within the "no external libraries except javax.validation" mandatory rule. |
| Validation trigger in controller, not view | `createValidHero()` validates; the view only collects input. Keeps the view dumb and makes the same validation reusable for the Swing view. |
| `ParameterMessageInterpolator` instead of `buildDefaultValidatorFactory()` | Avoids needing a Jakarta EL implementation (e.g. Expressly) on the classpath — one less dependency to justify against the "no external libraries" rule. Handles `{min}`/`{max}` placeholders, which is all we use. |
| `choose(prompt, Map<String,T>)` generic helper | A `Map` captures *what is valid* (keys) **and** *what it maps to* (values) in one structure, eliminating per-method switches. A `Set` would only validate and force a second switch. |
| Trim + lowercase all input in `scanNextLine()` | Single normalization point: `"  Y  "` works everywhere, and `@Size`/`@NotBlank` see meaningful characters (blocks names like `"        a"`). |
| `Hero.toString()` is debug-only; stats layout in `showHeroStats` | `toString()` convention = logging/debug. Player presentation belongs in the view (MVC) so each view (terminal/Swing) formats independently. |

---

## Mandatory requirements checklist (from subject)

> This checklist **is** the subject's mandatory part (V.1 Gameplay, V.2 Features, V.3 Validation) — treat it as the grading rubric. The turn order inside the game loop follows `Mazie.drawio`. Annotations: *engine ready* = model/`GameEngine` logic exists but is not yet wired into a playable `gameLoop`.

- [ ] MVC architecture (Model/View/Controller clearly separated) — *Model + View done; Controller in progress (gameLoop pending)*
- [ ] Launch with `java -jar mazie.jar console` — *`Main` is still a stub, no arg parsing (Fase 7)*
- [ ] Launch with `java -jar mazie.jar gui` — *SwingView not started (Fase 6)*
- [x] Build with `mvn clean package` → produces runnable jar
- [ ] No external libraries except Hibernate Validator (jakarta.validation) — *SQLite still bundled (bonus); remove if DB bonus dropped*
- [ ] Hero persistence in text file (load on start, save on exit) — *Fase 3 not started*
- [x] Create hero flow
- [ ] Select existing hero flow — *view method done; needs persistence to supply the list + id-key fix*
- [x] Show hero stats (name, class, level, xp, attack, defence, hp)
- [x] Square map, size = (level-1)*5+10-(level%2) — *`GameMap`*
- [ ] Move in 4 directions (N/E/S/W) — *engine `move()` ready; gameLoop wiring pending*
- [ ] Win by reaching border — *engine `win()` ready; loop pending*
- [ ] Monsters randomly spread on map — *engine ready; loop pending*
- [ ] Fight or run choice when meeting monster — *view `askFightMonster` done; loop wiring pending*
- [ ] Run = 50% chance, fail = must fight — *engine `runAway()` ready; loop pending*
- [ ] Battle simulation using hero + monster stats — *engine `fight()` ready; loop pending*
- [ ] Lose battle = game over — *engine ready; loop pending*
- [ ] Win battle = XP gain, possible artifact drop — *engine `FightResult` ready; loop pending*
- [x] Level up formula: level*1000 + (level-1)^2 * 450 — *verify `>` vs `>=` boundary*
- [x] 3 artifact types: Weapon (+attack), Armor (+defence), Helm (+hp)
- [x] Annotation-based validation (Hibernate Validator) on user input — *in `createValidHero()`*
- [x] Validation failure highlighted to user — *`showError` in yellow with violation messages*

## Bonus checklist

- [ ] Hero persistence in relational database instead of text file
- [ ] Switch between console/GUI at runtime without closing

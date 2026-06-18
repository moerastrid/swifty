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

### 🔄 Fase 4 — TerminalView (IN PROGRESS)

Done:

- `AnsiColor`: converted to typesafe `enum` with `toString()` returning the ANSI escape code
- Helpers: `colorPrint(AnsiColor, String)`, `scanNextLine()` (catches `NoSuchElementException` for Ctrl+C/D)
- `showError`, `showWelcome` (loads `mazie-icon-ascii.txt` from classpath via `getResourceAsStream`)
- `askNewGame`: y/n/yes/no via switch expression, recursive retry on invalid
- `createHero`: type loop (b/f/h or full name), name loop, returns `new Hero(name, type)` — but **without Validator yet** and with a manual length check that should go away
- ASCII art copied from `oldold/src/main/resources/` to `mazie/src/main/resources/`

Open for next session — see priorities at bottom of this section.

Still to implement (skeletons present, return placeholder values):

- `selectHero`, `confirmHero`, `showHeroStats`, `showStartGame`, `askDirection`, `askFightMonster`, `showRunSuccess`, `showEndGame`, `showFightSummary`, `showLevelUp`, `askKeepArtifact`

### ❌ Fase 5 — GameController (NOT STARTED)

### ❌ Fase 6 — SwingView (NOT STARTED)

### ❌ Fase 7 — Main + args (NOT STARTED)

---

## Next-session priorities (top of stack)

1. **Make `scanNextLine()` return `""` instead of `null`** on Ctrl+D — one-line change in `TerminalView`, removes null-checks in every caller and lets empty input fall naturally into `default` cases. Currently `askNewGame()` would NPE on Ctrl+D because `answer.toLowerCase()` runs on a `null` result.
2. **Integrate Hibernate Validator into `createHero()`** — this is a **mandatory subject requirement** that is not yet wired up:
   - Add `private final Validator validator` field, initialized once in constructor via `Validation.buildDefaultValidatorFactory().getValidator()` (factory creation is expensive, do it once)
   - After `new Hero(name, type)`: call `validator.validate(hero)` → `Set<ConstraintViolation<Hero>>`
   - If non-empty: loop through violations, call `showError(v.getMessage())`, ask the user again
   - Remove the manual `length >= 2 && length <= 30` check on the name — `@Size` on `Hero.name` already covers it (avoid double validation)
   - Remove the manual empty-name check too — `@NotBlank` covers it (and `@NotBlank` rejects whitespace-only, which the current check does not)
3. **Decide loop style consistency** — `askNewGame` uses recursion, `createHero` uses while-loops. Pick one for the rest of the methods (and ideally normalize the existing two).

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

- `createHero()`: ask name via Scanner, ask type, construct a Hero, validate, loop on violations showing each error message **(skeleton done, Validator integration pending — see Next-session priorities)**
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
- Plan: have `scanNextLine()` return `""` on EOF so callers don't need null-checks; empty input naturally falls into the `default` case of any `switch` and triggers a re-prompt
- Ctrl+C terminates the JVM; nothing to do beyond letting the OS handle it

**Hibernate Validator usage in `createHero()`:**

- Build a `Validator` from `Validation.buildDefaultValidatorFactory()`
- Call `validator.validate(hero)` — returns a `Set<ConstraintViolation<Hero>>`
- If non-empty: show each violation's `getMessage()` and ask again
- If empty: return the hero

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
| SynchronousQueue for Swing direction input | Standard solution for blocking EDT-to-controller communication |
| JOptionPane for Swing dialogs | Blocks naturally, no queue needed |
| Static Factory Methods instead of Builder pattern | Subject *hints* at Builder pattern (Chapter III). Skipped because Hero only has `name + type` as user input (Builder shines for many optional fields), and Monster/Artifact already use Static Factory Methods which are idiomatic for our case. **Be ready to explain this trade-off at peer evaluation** — evaluator may ask where Builder was applied. |
| SQLite dependency in `pom.xml` | Reserved for the relational-DB bonus. Subject explicitly allows libraries for bonus work *"if explicitly justified and serving only that part"*. **If the DB bonus is not done, remove SQLite from `pom.xml` before submission** to stay within the "no external libraries except javax.validation" mandatory rule. |

---

## Mandatory requirements checklist (from subject)

- [ ] MVC architecture (Model/View/Controller clearly separated)
- [ ] Launch with `java -jar mazie.jar console`
- [ ] Launch with `java -jar mazie.jar gui`
- [ ] Build with `mvn clean package` → produces runnable jar
- [ ] No external libraries except Hibernate Validator (javax.validation)
- [ ] Hero persistence in text file (load on start, save on exit)
- [ ] Create hero flow
- [ ] Select existing hero flow
- [ ] Show hero stats (name, class, level, xp, attack, defence, hp)
- [ ] Square map, size = (level-1)*5+10-(level%2)
- [ ] Move in 4 directions (N/E/S/W)
- [ ] Win by reaching border
- [ ] Monsters randomly spread on map
- [ ] Fight or run choice when meeting monster
- [ ] Run = 50% chance, fail = must fight
- [ ] Battle simulation using hero + monster stats
- [ ] Lose battle = game over
- [ ] Win battle = XP gain, possible artifact drop
- [ ] Level up formula: level*1000 + (level-1)^2 * 450
- [ ] 3 artifact types: Weapon (+attack), Armor (+defence), Helm (+hp)
- [ ] Annotation-based validation (Hibernate Validator) on user input
- [ ] Validation failure highlighted to user

## Bonus checklist

- [ ] Hero persistence in relational database instead of text file
- [ ] Switch between console/GUI at runtime without closing

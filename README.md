# MAZIE - an a-maze-ing RPG

hello friend.

Are you lost?

What are you doing inside this project?

Are you willing to get lost?

## run Mazie

Who knows what you might find, when you enter the maze....

setup + run:

```bash
mvn clean package
java -jar target/swingy.jar console
java -jar target/swingy.jar gui
```

default mode : GUI.

## play Mazie

make a new hero or select an existing one.
hero name between 2 and 30 chars.

your hero can only carry one item of each type (helmet, armour, weapon).

you can always:

- switch views (switch button or type 'switch')
- quit the game (close window or type 'q' 'quit' or 'exit')

the game ends when your hero dies (you lose) or when you reach the border of the map.
when you win, your hero is safe and you can continue playing with your hero.
when you die, your hero gets deleted and is gone forever.

the size of the map and difficulty of the monsters is dependent on the level of the hero.
try to see how far you can get before your hero dies.

_also be careful to not get stuck in vim, you might not be able to escape._

## requirements

- java 21
- maven 3.9+

## what else

to reset the database, run:

```bash
rm data/swingy.db
```

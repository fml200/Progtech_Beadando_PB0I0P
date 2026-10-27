# Rush Hour – parancssoros játék

Asztali, parancssoros kétszemélyes **Rush Hour** (csúszóblokk) játék Java 21-ben, Maven projektként.

## A játék

* NxM-es tábla (alapértelmezés: 6x12), amelyen különböző hosszúságú (2, 3, ...) járművek állnak.
  Minden jármű tengelye rögzített: vagy vízszintesen, vagy függőlegesen csúszhat.
* A **játékos** céljárműve az **A**, a **gépé** a **B**. Mindkettőnek saját kijárata van a tábla szélén
  (a kijáratot a keret megszakítása jelzi: `>`, `<`, `^`, `v`).
* A játékos kezd. Egy lépésben **bármelyik** jármű (a sajátod, az ellenfélé vagy egy semleges akadály)
  elcsúsztatható a tengelye mentén **bármennyi egybefüggő üres mezővel**. Átugrani, átfedni, forgatni
  és a pályáról lecsúsztatni nem lehet.
* A gép egyszerű: véletlenszerűen választ egy járművet, amelynek van lehetséges lépése, és annak egy
  véletlen lépését hajtja végre.
* Az nyer, akinek a céljárműve **elérte a kijáratot**.

```
    a b c d e f g h i j k l
  +-------------------------+
1 | . . . . . D . . . . F . |
2 | . . . C . D . . . . F . |
3 | A A . C . . . E . . . . >      <- az A kijárata: jobbra
4 < . . . C . . . E . . B B |      <- a B kijárata: balra
5 | . H . . . G . E . . . . |
6 | . H . . . G . . J J . . |
  +-------------------------+
```

Az alapértelmezett pálya például 3 lépésben megoldható: `move C down 2`, `move E down 1`, `move A right 10`
(persze a gép közben ő is mozgat).

## Futtatás

Fordítás, Checkstyle, tesztek, JaCoCo (80%) ellenőrzés és a jar-ok elkészítése:

```bash
mvn clean install
```

Futtatás (a `target` mappából, a build után):

```bash
java -jar target/rushhour-1.0-SNAPSHOT-jar-with-dependencies.jar
java -jar target/rushhour-1.0-SNAPSHOT-jar-with-dependencies.jar palya.txt
```

IntelliJ IDEA-ban a Maven panelen (Lifecycle) a `clean`, majd az `install` duplakattintással indítható.
Ha saját futtatási konfigurációt készítesz, a goalok mezőjébe csak ennyi kerüljön: `clean install`.

Pályafájl nélkül a munkakönyvtárban lévő `level.txt` töltődik be; ha az sincs (vagy érvénytelen), a beégetett
alapértelmezett pálya indul. A napló a `logs/rushhour.log` fájlba kerül.
A JaCoCo riport: `target/site/jacoco/index.html`.

## Parancsok

| Parancs                    | Hatás                                                                         |
|----------------------------|-------------------------------------------------------------------------------|
| `move <jármű> <irány> <db>` | jármű csúsztatása, pl. `move C down 2`; irány: `up/down/left/right` vagy `fel/le/balra/jobbra` |
| `save <fájl>`              | az aktuális állás mentése szövegfájlba                                        |
| `load <fájl>`              | pálya vagy mentett állás betöltése (új játszma indul belőle)                  |
| `new`                      | új játék a kezdőpályáról                                                      |
| `print`                    | a tábla kiírása                                                               |
| `help`                     | a most használható parancsok listája                                          |
| `exit` / `quit`            | kilépés                                                                       |

A parancsok kis- és nagybetűre érzéketlenek. A játék végén (`FINISHED` fázis) a `move` és a `save` nem
használható, a `new`, `load`, `print`, `help`, `exit` igen.

## Pályafájl formátuma

```
# megjegyzés (a # kezdetű és az üres sorok kimaradnak)
6 12            sorok és oszlopok száma
A RIGHT         az A (játékos) céljármű kijárata: UP, DOWN, LEFT vagy RIGHT
B LEFT          a B (gép) céljármű kijárata
.....D....F.    a rács: '.' üres mező, A-Z a jármű azonosítója
...C.D....F.    (egy jármű mezői egy sorban vagy oszlopban, összefüggően állnak)
AA.C...E....
...C...E..BB
.H...G.E....
.H...G..JJ..
```

Hibás fájl esetén a `load` hibaüzenetet ír (pl. átfedő járművek, hiányzó A vagy B jármű, rossz sorhossz,
már megoldott pálya), indításkor pedig az alapértelmezett pálya indul.

## Felépítés

```
pom.xml                              Java 21; jar, assembly, jacoco, checkstyle plugin; JUnit 5, Mockito, Logback
config/checkstyle.xml                Checkstyle szabályok
level.txt                            az induláskor betöltött pálya
src/main/java/hu/nye/progtech/rushhour
  Main                               belépési pont (csak összerakja a részeket)
  Game                               a játék: start() beolvassa a nevet és a parancsokat
  model/                             Board, Vehicle, Position, Move, Direction, Orientation, Side, BoardBuilder
  exception/                         GameException <- InvalidMoveException, InvalidBoardException, BoardFileException
  service/                           GameSession, MachinePlayer, DefaultBoards, LevelProvider, InitialLevel
  persistence/                       BoardTextReader, BoardTextWriter, BoardFileRepository
  ui/                                BoardRenderer
  ui/command/                        Command, CommandRegistry, GameContext, GamePhase és a konkrét parancsok
src/test/java/...                    a fentiek tesztjei (JUnit 5 + Mockito, Given-When-Then szerkezetben)
```

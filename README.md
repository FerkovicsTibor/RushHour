# Rush Hour

Java 21 nyelven készített parancssoros Rush Hour
logikai játék, amelyben a játékos a számítógép ellen játszik.

## Felhasznált technológiák

- Java 21
- Maven
- SQLite
- JUnit 5
- Mockito
- JaCoCo
- Checkstyle
- SLF4J és Logback
- Git és GitHub

## Játékszabályok

A játék egy N x M méretű táblán zajlik.
A játékos céljárműve az A, a számítógépé a B.

Az A járműnek a tábla jobb oldalán,
a B járműnek a bal oldalán kell kijutnia.

A járművek csak a saját tengelyük mentén mozoghatnak.
Nem fedhetik át egymást, és nem ugorhatnak át
más járműveken.

A játékos és a számítógép felváltva lép.
A számítógép véletlenszerű szabályos lépést választ.

## Funkciók

- Új játék indítása
- Külső pályafájl betöltése
- Játékállás mentése megadott névvel
- Mentett játék folytatása
- Játékossorrend tárolása
- Játékosnevek és győzelmek tárolása
- Adatbázisalapú ranglista
- Események és hibák naplózása

## Fordítás

A projekt fordításához Java 21 és Maven szükséges.

```bash
mvn clean install
```

## Futtatás

```bash
java -jar target/rush-hour-1.0-SNAPSHOT-jar-with-dependencies.jar
```

A futtatáshoz Java 21 szükséges.

## Főmenü

1. Új játék
2. Pálya betöltése fájlból
3. Mentett játék folytatása
4. Ranglista
5. Kilépés

## Játékparancsok

| Parancs | Jelentés |
|---|---|
| A 2 | Az A jármű mozgatása két mezővel jobbra |
| B -2 | A B jármű mozgatása két mezővel balra |
| C 1 | A C jármű mozgatása pozitív irányba |
| save teszt | Játék mentése teszt néven |
| exit | Visszatérés a főmenübe |

A pozitív irány jobbra vagy lefelé,
a negatív irány balra vagy felfelé történő
mozgást jelent.

## Pályafájl formátuma

Példa egy 6 x 12 méretű pályára:

```text
6 12
A 3 2 2 H
B 4 10 2 H
C 1 5 3 V
D 3 8 3 V
E 1 9 2 H
F 4 3 3 V
G 6 6 3 H
H 1 1 2 V
```

Az első sor a tábla méretét tartalmazza.

A további sorok formátuma:

```text
azonosító sor oszlop hossz irány
```

A koordináták számozása 1-től kezdődik.

- H: vízszintes jármű
- V: függőleges jármű

## Adatbázis

A program SQLite adatbázist használ.
Az adatbázis automatikusan létrejön
rushhour.db néven.

Az adatbázis a játékosok nevét és
győzelmeik számát tárolja.

## Mentések és naplózás

A mentett játékállások a saves könyvtárba kerülnek.
Mentés a játékos körében lehetséges.

A program naplófájlja:

```text
logs/rushhour.log
```

## Tesztelés

Az egységtesztek futtatása:

```bash
mvn test
```

A projekt JUnit 5 és Mockito segítségével
teszteli a játék működését.

## Tesztlefedettség

A JaCoCo jelentés előállítása:

```bash
mvn clean verify
```

A jelentés böngészőben megnyitható:

```text
target/site/jacoco/index.html
```

A projekt legutóbb mért tesztlefedettsége:

- Utasításlefedettség: 97%
- Elágazáslefedettség: 92%
- Sorlefedettség: 95,8%

A Maven konfiguráció legalább 80%-os
utasítás- és sorlefedettséget követel meg.

## Kódminőség

A Java-forráskód ellenőrzése Checkstyle
segítségével történik.

```bash
mvn checkstyle:check
```

## A projekt célja

A projekt egyetemi programozási beadandó
keretében készült.

# Chef_Statistic_System_Console

Java 17+ / Maven alapú konzolos alkalmazás séfbérlések statisztikáihoz. Célév: **2024**.
Az alkalmazás kimenete magyar nyelvű.

## Adatforrás és a CSV helye

Az adatok a `../resources/chef_berlesek_2025.csv` fájlban vannak (130 bérlés). A fájl a későbbi asztali, Web API és frontend projektekkel közös, ezért az alkalmazásmappán kívül található. A fájlnévben 2025 szerepel (az eredeti előírás szerint), de a bérlések 2024-es adatok. Ez tudatos döntés.

A program a CSV-t automatikusan megkeresi: a munkakönyvtárból indulva felfelé haladva keresi a `resources/chef_berlesek_2025.csv` fájlt, így a workspace gyökeréből és ebből a mappából indítva is működik. Ettől eltérő helyen lévő fájl esetén az elérési út megadható az első parancssori argumentumként.

## Mappaszerkezet

    DualisKepzes_Feladatok/
      resources/chef_berlesek_2025.csv     közös adatfájl
      Chef_Statistic_System_Console/       ez a projekt (pom.xml, src/)

## Futtatás parancssorból

    cd Chef_Statistic_System_Console
    mvn test                 # egységtesztek
    mvn compile exec:java    # futtatás (hónapot kér, 1-12)
    mvn package && java -jar target/chef-statistic-system-console-1.0.0.jar

## IntelliJ IDEA

1. File > Open, válassza ki a `Chef_Statistic_System_Console` mappát (amelyben a `pom.xml` van), és bízzon meg a projektben. A Maven automatikusan importál.
2. A File > Project Structure > Project SDK legyen JDK 17 vagy újabb.
3. Nyissa meg a `src/main/java/hu/chefstat/Main.java` fájlt, és kattintson a `main` melletti zöld nyílra > Run. A hónapot a Run ablakban adja meg.
4. Ha a CSV nem található, a Run > Edit Configurations > Working directory mezőben állítsa be a workspace mappát vagy ezt a mappát, vagy adja meg a CSV útvonalát program argumentumként.

## Visual Studio Code

1. Telepítse a Microsoft "Extension Pack for Java" bővítménycsomagot (tartalmazza a Maven támogatást).
2. File > Open Folder, válassza ki a `Chef_Statistic_System_Console` mappát (vagy a szülő workspace mappát; az almappákban lévő Maven projektet is felismeri).
3. Várja meg a Java projekt importálásának végét, majd nyissa meg a `Main.java` fájlt, és kattintson a `main` felett megjelenő "Run" feliratra, vagy használja a Maven nézetet (Lifecycle > test / compile, Plugins > exec > exec:java).
4. A kérdésre a hónapot a Terminal panelen adja meg.

## Értelmezési döntések

- A bérlési napok száma a kezdőnapot is magában foglalja, így az azonos kezdő- és záródátumú bérlés 1 napos. A `TotalPrice` a teljes bérlés ára (napok száma × napi díj).
- A havi és az éves bevétel, valamint az átlagos időtartam csak a hónapba, illetve 2024-be eső bérlési napokat számolja. A CSV tartalmaz 2023/2024 és 2024/2025 határt átlépő bérlést is.
- A legdrágább bérlés a teljes `TotalPrice` alapján dönt, akkor is, ha a bérlés átlóg egy évhatáron.
- A konyhatípusonkénti darabszám és a legtöbbször bérelt séf az összes CSV-beli bérlést számolja. A különböző séfek száma a 2024-gyel átfedő bérlések `chefid` értékein alapul.
- A konyhatípusok nevei a CSV-ben szereplő formában (angolul) jelennek meg.
- A számok magyar formátumban jelennek meg (tizedesvessző, pl. 5,38 nap). A pénzösszegek egész értéknél tizedesek nélkül, euróban szerepelnek.
- A `chef_berlesek_2025.csv` UTF-8 kódolású, pontosvesszővel (`;`) tagolt; a fejléc: `uid;chefid;startdate;enddate;daily_rate;name;cuisine`.

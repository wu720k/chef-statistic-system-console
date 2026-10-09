package hu.chefstat;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final int YEAR = 2024;
    private static final Locale HU = Locale.forLanguageTag("hu-HU");

    public static void main(String[] args) {
        Locale.setDefault(HU);
        try {
            run(args);
        } catch (IOException | RuntimeException e) {
            System.err.println("Hiba: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void run(String[] args) throws IOException {
        Path csv = args.length > 0 ? Path.of(args[0]) : CsvLoader.locate();
        List<Berles> rentals = CsvLoader.load(csv);
        System.out.println("Betöltve " + rentals.size() + " bérlés innen: " + csv.toAbsolutePath().normalize());
        Statistics stats = new Statistics(rentals, YEAR);

        int month = askMonth(new Scanner(System.in));
        System.out.printf("%nA(z) %d. hónap bevétele: %s%n", month, eur(stats.monthlyRevenue(month)));
        System.out.printf("%nA teljes %d-es éves bevétel: %s%n", YEAR, eur(stats.yearlyRevenue()));

        stats.mostExpensiveRental().ifPresent(b -> {
            System.out.printf("%nA legdrágább bérlés %s séftől volt, teljes ár: %s%n",
                    b.getName(), eur(b.getTotalPrice()));
            System.out.println("  Konyhatípus:  " + b.getCuisine());
            System.out.println("  Kezdő dátum:  " + b.getStartDate());
            System.out.println("  Záró dátum:   " + b.getEndDate());
            System.out.println("  Napi díj:     " + eur(b.getDailyRate()));
            System.out.println("  Napok száma:  " + b.getDays());
        });

        System.out.printf("%nÖsszesen %d különböző séfet béreltek ki.%n", stats.distinctChefCount());
        stats.mostFrequentChef().ifPresent(e ->
                System.out.printf("%nA legtöbbször bérelt séf: %s (%d bérlés)%n", e.getKey(), e.getValue()));

        System.out.println("\nBérlések száma konyhatípusonként:\n");
        for (Map.Entry<String, Long> e : stats.rentalsByCuisine().entrySet()) {
            System.out.printf("%s: %d bérlés%n", e.getKey(), e.getValue());
        }
        System.out.printf("%nÁtlagos bérlési időtartam: %.2f nap%n", stats.averageDurationDays());
    }

    private static int askMonth(Scanner in) {
        while (true) {
            System.out.print("Adjon meg egy hónapot (1-12): ");
            if (!in.hasNextLine()) {
                throw new IllegalStateException("Nincs több bemenet, a hónap nem adható meg.");
            }
            String text = in.nextLine().trim();
            try {
                int m = Integer.parseInt(text);
                if (m >= 1 && m <= 12) {
                    return m;
                }
            } catch (NumberFormatException ignored) {
                // az alábbi hibaüzenetre esik vissza
            }
            System.out.println("Érvénytelen bemenet, kérem, 1 és 12 közötti egész számot adjon meg.");
        }
    }

    /** Whole amounts without decimals ("35665 euró"), otherwise with a decimal comma. */
    private static String eur(BigDecimal v) {
        BigDecimal s = v.stripTrailingZeros();
        return (s.scale() <= 0 ? s.toBigInteger().toString() : String.format(HU, "%.2f", v)) + " euró";
    }
}

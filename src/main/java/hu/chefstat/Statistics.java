package hu.chefstat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** All statistics over a list of rentals. */
public class Statistics {
    private final List<Berles> rentals;
    private final int year;

    public Statistics(List<Berles> rentals, int year) {
        this.rentals = rentals;
        this.year = year;
    }

    /** Revenue of the selected month: only the rental days inside that month are charged. */
    public BigDecimal monthlyRevenue(int month) {
        YearMonth ym = YearMonth.of(year, month);
        return revenueBetween(ym.atDay(1), ym.atEndOfMonth());
    }

    public BigDecimal yearlyRevenue() {
        return revenueBetween(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
    }

    private BigDecimal revenueBetween(LocalDate from, LocalDate to) {
        return rentals.stream().map(b -> b.getRevenueWithin(from, to))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Rental with the highest TotalPrice (full rental price; first one wins a tie). */
    public Optional<Berles> mostExpensiveRental() {
        return rentals.stream().max(Comparator.comparing(Berles::getTotalPrice));
    }

    /** Distinct chefid values among rentals that overlap the year. */
    public long distinctChefCount() {
        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = LocalDate.of(year, 12, 31);
        return rentals.stream().filter(b -> b.getDaysWithin(from, to) > 0)
                .map(Berles::getChefId).distinct().count();
    }

    /** Chef name with the most rentals (ties: alphabetically first), with its count. */
    public Optional<Map.Entry<String, Long>> mostFrequentChef() {
        return rentals.stream().collect(Collectors.groupingBy(Berles::getName, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.<String, Long>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())));
    }

    /** Cuisine to number of rentals, sorted by count (desc) then name. */
    public Map<String, Long> rentalsByCuisine() {
        return rentals.stream()
                .collect(Collectors.groupingBy(Berles::getCuisine, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    /** Average days per rental, counting only the days inside the year. */
    public double averageDurationDays() {
        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = LocalDate.of(year, 12, 31);
        return rentals.stream().mapToLong(b -> b.getDaysWithin(from, to)).filter(d -> d > 0)
                .average().orElse(0);
    }
}

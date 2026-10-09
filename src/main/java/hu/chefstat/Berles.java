package hu.chefstat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** One chef rental (one CSV row). */
public class Berles {
    private final int uid;
    private final int chefId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal dailyRate;
    private final String name;
    private final String cuisine;

    public Berles(int uid, int chefId, LocalDate startDate, LocalDate endDate,
                  BigDecimal dailyRate, String name, String cuisine) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("a záró dátum korábbi a kezdő dátumnál: " + startDate + " > " + endDate);
        }
        this.uid = uid;
        this.chefId = chefId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyRate = dailyRate;
        this.name = name;
        this.cuisine = cuisine;
    }

    public int getUid() { return uid; }
    public int getChefId() { return chefId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BigDecimal getDailyRate() { return dailyRate; }
    public String getName() { return name; }
    public String getCuisine() { return cuisine; }

    /** Rental days; the start day counts, so start == end is 1 day. */
    public long getDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /** Total price of the whole rental: days x daily rate. */
    public BigDecimal getTotalPrice() {
        return dailyRate.multiply(BigDecimal.valueOf(getDays()));
    }

    /** Number of rental days that fall within [from, to] (both inclusive). */
    public long getDaysWithin(LocalDate from, LocalDate to) {
        LocalDate s = startDate.isAfter(from) ? startDate : from;
        LocalDate e = endDate.isBefore(to) ? endDate : to;
        return e.isBefore(s) ? 0 : ChronoUnit.DAYS.between(s, e) + 1;
    }

    /** Revenue attributable to the days that fall within [from, to]. */
    public BigDecimal getRevenueWithin(LocalDate from, LocalDate to) {
        return dailyRate.multiply(BigDecimal.valueOf(getDaysWithin(from, to)));
    }

    @Override
    public String toString() {
        return "Berles{uid=" + uid + ", chef=" + name + " (#" + chefId + "), " + startDate + " - " + endDate
                + ", " + dailyRate + " EUR/day, " + cuisine + "}";
    }
}

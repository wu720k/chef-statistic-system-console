package hu.chefstat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class BerlesTest {
    private static Berles b(String s, String e, int rate) {
        return new Berles(1, 1, LocalDate.parse(s), LocalDate.parse(e), BigDecimal.valueOf(rate), "X", "Y");
    }

    @Test
    void sameDayIsOneDay() {
        assertEquals(new BigDecimal("100"), b("2024-05-05", "2024-05-05", 100).getTotalPrice());
    }

    @Test
    void monthSplitAcrossBoundary() {
        Statistics st = new Statistics(List.of(b("2024-01-30", "2024-02-02", 100)), 2024);
        assertEquals(new BigDecimal("200"), st.monthlyRevenue(1));
        assertEquals(new BigDecimal("200"), st.monthlyRevenue(2));
        assertEquals(new BigDecimal("400"), st.yearlyRevenue());
    }

    @Test
    void yearBoundaryClipped() {
        Statistics st = new Statistics(
                List.of(b("2023-12-30", "2024-01-02", 10), b("2024-12-31", "2025-01-02", 10)), 2024);
        assertEquals(new BigDecimal("30"), st.yearlyRevenue());
        assertEquals(1.5, st.averageDurationDays(), 1e-9);
    }
}

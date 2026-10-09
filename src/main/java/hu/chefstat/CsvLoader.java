package hu.chefstat;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Reads chef_berlesek_2025.csv and locates it in the shared resources folder. */
public final class CsvLoader {
    public static final String FILE_NAME = "chef_berlesek_2025.csv";
    private static final String HEADER = "uid;chefid;startdate;enddate;daily_rate;name;cuisine";

    private CsvLoader() { }

    /**
     * Finds resources/chef_berlesek_2025.csv by walking up from the working directory,
     * so it works from the workspace root, from Chef_Statistic_System_Console, or from target/.
     */
    public static Path locate() {
        Path dir = Path.of("").toAbsolutePath();
        while (dir != null) {
            Path candidate = dir.resolve("resources").resolve(FILE_NAME);
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
            dir = dir.getParent();
        }
        throw new IllegalStateException("A resources/" + FILE_NAME
                + " fájl nem található a munkakönyvtárban és annak szülőmappáiban. "
                + "Adja meg a CSV elérési útját az első argumentumként.");
    }

    public static List<Berles> load(Path file) throws IOException {
        if (!Files.isRegularFile(file)) {
            throw new IOException("A CSV fájl nem található: " + file.toAbsolutePath());
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !lines.get(0).replace("﻿", "").trim().equalsIgnoreCase(HEADER)) {
            throw new IOException("Váratlan CSV fejléc, a várt fejléc: " + HEADER);
        }
        List<Berles> result = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                result.add(parse(line));
            } catch (RuntimeException e) {
                throw new IOException("Érvénytelen CSV sor (" + (i + 1) + ". sor): " + line + " (" + e.getMessage() + ")", e);
            }
        }
        return result;
    }

    static Berles parse(String line) {
        String[] f = line.split(";", -1);
        if (f.length != 7) {
            throw new IllegalArgumentException("7 oszlop helyett " + f.length + " található");
        }
        return new Berles(Integer.parseInt(f[0].trim()), Integer.parseInt(f[1].trim()),
                LocalDate.parse(f[2].trim()), LocalDate.parse(f[3].trim()),
                new BigDecimal(f[4].trim()), f[5].trim(), f[6].trim());
    }
}

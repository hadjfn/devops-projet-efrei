package faria.sasikumar.sylla.myfss.client;

import java.util.Map;

public record StatsSummary(
        long total,
        long active,
        long archived,
        Map<Integer, Long> countByYear,
        Map<String, Long> countByProgramme,
        double averageYear
) {
    public StatsSummary {
        countByYear = Map.copyOf(countByYear);
        countByProgramme = Map.copyOf(countByProgramme);
    }

    public static StatsSummary empty() {
        return new StatsSummary(0, 0, 0, Map.of(), Map.of(), 0.0);
    }
}

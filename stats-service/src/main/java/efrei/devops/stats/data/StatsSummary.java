package efrei.devops.stats.data;

import java.util.Map;

public record StatsSummary(
        long total,
        long active,
        long archived,
        Map<Integer, Long> countByYear,
        Map<String, Long> countByProgramme,
        double averageYear
) {
}

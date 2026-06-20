package efrei.devops.stats.service;

import efrei.devops.stats.data.ApprentiDto;
import efrei.devops.stats.data.StatsSummary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class StatsService {

    public StatsSummary computeSummary(List<ApprentiDto> apprentis) {
        if (apprentis == null || apprentis.isEmpty()) {
            return new StatsSummary(0, 0, 0, Map.of(), Map.of(), 0.0);
        }

        long total = apprentis.size();
        long archived = apprentis.stream().filter(ApprentiDto::archived).count();
        long active = total - archived;

        Map<Integer, Long> countByYear = new TreeMap<>(apprentis.stream()
                .filter(a -> a.annee() != null)
                .collect(Collectors.groupingBy(ApprentiDto::annee, Collectors.counting())));

        Map<String, Long> countByProgramme = apprentis.stream()
                .filter(a -> a.programme() != null && !a.programme().isBlank())
                .collect(Collectors.groupingBy(ApprentiDto::programme, Collectors.counting()));

        double averageYear = apprentis.stream()
                .filter(a -> a.annee() != null)
                .mapToInt(ApprentiDto::annee)
                .average()
                .orElse(0.0);

        return new StatsSummary(total, active, archived, countByYear, countByProgramme, averageYear);
    }
}

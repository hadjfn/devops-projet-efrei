package efrei.devops.stats.controller;

import efrei.devops.stats.data.ApprentiDto;
import efrei.devops.stats.data.StatsSummary;
import efrei.devops.stats.service.StatsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "stats-service");
    }

    @PostMapping("/summary")
    public ResponseEntity<StatsSummary> summary(@RequestBody @Valid List<ApprentiDto> apprentis) {
        return ResponseEntity.ok(statsService.computeSummary(apprentis));
    }
}

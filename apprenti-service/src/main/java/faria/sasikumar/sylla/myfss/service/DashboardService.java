package faria.sasikumar.sylla.myfss.service;

import faria.sasikumar.sylla.myfss.client.StatsClient;
import faria.sasikumar.sylla.myfss.client.StatsSummary;
import faria.sasikumar.sylla.myfss.model.Apprenti;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Coordinates a database snapshot and an optional remote summary, outside a DB transaction. */
@Service
public class DashboardService {
    private final ApprentiService apprentiService;
    private final StatsClient statsClient;

    public DashboardService(ApprentiService apprentiService, StatsClient statsClient) {
        this.apprentiService = apprentiService;
        this.statsClient = statsClient;
    }

    public Dashboard load() {
        List<Apprenti> all = apprentiService.getAllApprentis();
        List<Apprenti> active = all.stream().filter(apprenti -> !apprenti.isArchived()).toList();
        return new Dashboard(active, statsClient.fetchSummary(all));
    }

    public record Dashboard(List<Apprenti> apprentis, Optional<StatsSummary> stats) {
        public Dashboard {
            apprentis = List.copyOf(apprentis);
        }
    }
}

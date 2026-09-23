package faria.sasikumar.sylla.myfss.service;

import faria.sasikumar.sylla.myfss.client.StatsClient;
import faria.sasikumar.sylla.myfss.client.StatsSummary;
import faria.sasikumar.sylla.myfss.model.Apprenti;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DashboardServiceTest {
    @Test
    void usesOneDatasetForActiveListAndCompleteStats() {
        ApprentiService apprentices = mock(ApprentiService.class);
        StatsClient stats = mock(StatsClient.class);
        Apprenti active = new Apprenti();
        Apprenti archived = new Apprenti();
        archived.setArchived(true);
        List<Apprenti> all = List.of(active, archived);
        when(apprentices.getAllApprentis()).thenReturn(all);
        when(stats.fetchSummary(all)).thenReturn(Optional.of(StatsSummary.empty()));

        DashboardService.Dashboard result = new DashboardService(apprentices, stats).load();

        assertThat(result.apprentis()).containsExactly(active);
        assertThat(result.stats()).isPresent();
        verify(apprentices).getAllApprentis();
        verify(stats).fetchSummary(all);
        verifyNoMoreInteractions(apprentices, stats);
    }
}

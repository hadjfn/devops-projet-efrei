package efrei.devops.stats.service;

import efrei.devops.stats.data.ApprentiDto;
import efrei.devops.stats.data.StatsSummary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StatsServiceTest {

    private final StatsService service = new StatsService();

    @Test
    void emptyList_returnsZeroSummary() {
        StatsSummary s = service.computeSummary(List.of());
        assertThat(s.total()).isZero();
        assertThat(s.active()).isZero();
        assertThat(s.archived()).isZero();
        assertThat(s.averageYear()).isZero();
        assertThat(s.countByYear()).isEmpty();
        assertThat(s.countByProgramme()).isEmpty();
    }

    @Test
    void nullList_returnsZeroSummary() {
        StatsSummary s = service.computeSummary(null);
        assertThat(s.total()).isZero();
    }

    @Test
    void mixedList_aggregatesByYearAndProgramme() {
        List<ApprentiDto> apprentis = List.of(
                new ApprentiDto(1L, "Doe", "Jane", "BSc",  "DA", 1, false),
                new ApprentiDto(2L, "Doe", "John", "BSc",  "DA", 2, false),
                new ApprentiDto(3L, "Roe", "Ric",  "MSc",  "BI", 3, false),
                new ApprentiDto(4L, "Old", "Pat",  "BSc",  "DA", 3, true)
        );

        StatsSummary s = service.computeSummary(apprentis);

        assertThat(s.total()).isEqualTo(4);
        assertThat(s.active()).isEqualTo(3);
        assertThat(s.archived()).isEqualTo(1);
        assertThat(s.countByYear()).containsEntry(1, 1L)
                                   .containsEntry(2, 1L)
                                   .containsEntry(3, 2L);
        assertThat(s.countByProgramme()).containsEntry("BSc", 3L)
                                        .containsEntry("MSc", 1L);
        assertThat(s.averageYear()).isEqualTo((1 + 2 + 3 + 3) / 4.0);
    }

    @Test
    void apprentisWithNullYear_areExcludedFromAverages() {
        List<ApprentiDto> apprentis = List.of(
                new ApprentiDto(1L, "A", "A", "BSc", "DA", null, false),
                new ApprentiDto(2L, "B", "B", "BSc", "DA", 2,    false)
        );

        StatsSummary s = service.computeSummary(apprentis);

        assertThat(s.total()).isEqualTo(2);
        assertThat(s.countByYear()).containsOnlyKeys(2);
        assertThat(s.averageYear()).isEqualTo(2.0);
    }

    @Test
    void apprentisWithBlankProgramme_areExcludedFromGroupBy() {
        List<ApprentiDto> apprentis = List.of(
                new ApprentiDto(1L, "A", "A", "",   "DA", 1, false),
                new ApprentiDto(2L, "B", "B", null, "DA", 2, false),
                new ApprentiDto(3L, "C", "C", "BSc","DA", 3, false)
        );

        StatsSummary s = service.computeSummary(apprentis);

        assertThat(s.countByProgramme()).containsOnlyKeys("BSc");
    }
}

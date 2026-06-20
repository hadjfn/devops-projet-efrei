package efrei.devops.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efrei.devops.stats.data.ApprentiDto;
import efrei.devops.stats.data.StatsSummary;
import efrei.devops.stats.service.StatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatsService statsService;

    @Test
    void health_returnsUp() throws Exception {
        mockMvc.perform(get("/api/stats/health"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value("UP"))
               .andExpect(jsonPath("$.service").value("stats-service"));
    }

    @Test
    void summary_returnsAggregatedJson() throws Exception {
        StatsSummary stub = new StatsSummary(2, 2, 0,
                Map.of(1, 1L, 2, 1L),
                Map.of("BSc", 2L),
                1.5);
        when(statsService.computeSummary(anyList())).thenReturn(stub);

        List<ApprentiDto> body = List.of(
                new ApprentiDto(1L, "A", "A", "BSc", "DA", 1, false),
                new ApprentiDto(2L, "B", "B", "BSc", "DA", 2, false)
        );

        mockMvc.perform(post("/api/stats/summary")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(body)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(2))
               .andExpect(jsonPath("$.archived").value(0))
               .andExpect(jsonPath("$.countByProgramme.BSc").value(2))
               .andExpect(jsonPath("$.averageYear").value(1.5));
    }
}

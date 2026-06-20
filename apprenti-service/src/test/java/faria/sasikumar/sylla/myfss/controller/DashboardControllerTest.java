package faria.sasikumar.sylla.myfss.controller;

import faria.sasikumar.sylla.myfss.client.StatsClient;
import faria.sasikumar.sylla.myfss.client.StatsSummary;
import faria.sasikumar.sylla.myfss.config.SecurityConfig;
import faria.sasikumar.sylla.myfss.model.Apprenti;
import faria.sasikumar.sylla.myfss.service.ApprentiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import(SecurityConfig.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApprentiService apprentiService;

    @MockBean
    private StatsClient statsClient;

    @Test
    @WithMockUser(username = "test-user")
    void dashboard_rendersTemplateWithStats() throws Exception {
        Apprenti a = new Apprenti("Doe", "Alice", "a@e.fr", "0102030405", "BSc", "DA", 1);
        a.setId(1L);
        when(apprentiService.getAllApprentisNoArchived()).thenReturn(List.of(a));
        when(apprentiService.getAllApprentis()).thenReturn(List.of(a));
        when(statsClient.fetchSummary(anyList()))
                .thenReturn(new StatsSummary(1, 1, 0, Map.of(1, 1L), Map.of("BSc", 1L), 1.0));

        mockMvc.perform(get("/dashboard"))
               .andExpect(status().isOk())
               .andExpect(view().name("dashboard"))
               .andExpect(model().attribute("username", "test-user"))
               .andExpect(model().attributeExists("apprentis"))
               .andExpect(model().attributeExists("stats"));
    }

    @Test
    void dashboard_redirectsToLoginWhenAnonymous() throws Exception {
        mockMvc.perform(get("/dashboard"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrlPattern("**/login"));
    }
}

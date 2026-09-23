package faria.sasikumar.sylla.myfss.controller;

import faria.sasikumar.sylla.myfss.client.StatsSummary;
import faria.sasikumar.sylla.myfss.config.SecurityConfig;
import faria.sasikumar.sylla.myfss.model.Apprenti;
import faria.sasikumar.sylla.myfss.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import(SecurityConfig.class)
class DashboardControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private DashboardService dashboardService;

    @ParameterizedTest
    @ValueSource(strings = {"/", "/dashboard", "/apprentis/dashboard"})
    @WithMockUser(username = "test-user")
    void allDashboardRoutes_renderSameData(String route) throws Exception {
        when(dashboardService.load()).thenReturn(new DashboardService.Dashboard(List.of(), Optional.of(StatsSummary.empty())));
        mockMvc.perform(get(route))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("username", "test-user"))
                .andExpect(model().attribute("statsAvailable", true))
                .andExpect(model().attributeExists("stats"));
    }

    @Test
    @WithMockUser
    void unavailableStats_keepApprenticesVisibleWithAnExplicitMessage() throws Exception {
        Apprenti apprentice = new Apprenti("Doe", "Alice", "alice@example.org", "0102030405", "BSc", "DA", 1);
        apprentice.setId(1L);
        when(dashboardService.load()).thenReturn(new DashboardService.Dashboard(List.of(apprentice), Optional.empty()));
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("statsAvailable", false))
                .andExpect(model().attributeDoesNotExist("stats"))
                .andExpect(content().string(containsString("Statistiques temporairement indisponibles")))
                .andExpect(content().string(containsString("Alice")));
    }

    @Test
    void dashboard_redirectsToLoginWhenAnonymous() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}

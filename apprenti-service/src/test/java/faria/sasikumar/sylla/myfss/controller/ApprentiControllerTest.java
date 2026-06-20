package faria.sasikumar.sylla.myfss.controller;

import faria.sasikumar.sylla.myfss.config.SecurityConfig;
import faria.sasikumar.sylla.myfss.model.Apprenti;
import faria.sasikumar.sylla.myfss.repository.*;
import faria.sasikumar.sylla.myfss.service.ApprentiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApprentiController.class)
@Import(SecurityConfig.class)
class ApprentiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private ApprentiService apprentiService;
    @MockBean private EntrepriseRepository entrepriseRepo;
    @MockBean private EvaluationRepository evaluationRepo;
    @MockBean private MaitreApprentissageRepository maitreRepo;
    @MockBean private MissionRepository missionRepo;
    @MockBean private VisiteRepository visiteRepo;

    @Test
    @WithMockUser
    void newApprentiForm_returnsForm() throws Exception {
        mockMvc.perform(get("/apprentis/new"))
               .andExpect(status().isOk())
               .andExpect(view().name("apprenti_form"))
               .andExpect(model().attributeExists("apprenti"));
    }

    @Test
    @WithMockUser
    void apprentiDetails_returnsDetailsPage() throws Exception {
        Apprenti a = new Apprenti("Doe", "Alice", "a@e.fr", "0102030405", "BSc", "DA", 1);
        a.setId(1L);
        when(apprentiService.getApprenti(1L)).thenReturn(a);
        when(evaluationRepo.findAll()).thenReturn(List.of());
        when(missionRepo.findAll()).thenReturn(List.of());
        when(visiteRepo.findAll()).thenReturn(List.of());
        when(maitreRepo.findAll()).thenReturn(List.of());
        when(entrepriseRepo.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/apprentis/1"))
               .andExpect(status().isOk())
               .andExpect(view().name("apprenti_details"))
               .andExpect(model().attribute("apprenti", a));
    }

    @Test
    @WithMockUser
    void saveApprenti_redirectsToDashboardOnSuccess() throws Exception {
        when(apprentiService.createOrUpdateApprenti(any(Apprenti.class)))
                .thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/apprentis/save")
                       .with(csrf())
                       .param("nom", "Doe")
                       .param("prenom", "Alice")
                       .param("email", "alice@efrei.fr")
                       .param("telephone", "0102030405")
                       .param("programme", "BSc")
                       .param("majeure", "DA")
                       .param("annee", "1"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/apprentis/dashboard"));

        verify(apprentiService).createOrUpdateApprenti(any(Apprenti.class));
    }

    @Test
    @WithMockUser
    void saveApprenti_returnsFormOnValidationError() throws Exception {
        mockMvc.perform(post("/apprentis/save")
                       .with(csrf())
                       .param("nom", "") // invalid: NotBlank
                       .param("prenom", "Alice")
                       .param("email", "alice@efrei.fr")
                       .param("telephone", "0102030405")
                       .param("programme", "BSc")
                       .param("majeure", "DA")
                       .param("annee", "1"))
               .andExpect(status().isOk())
               .andExpect(view().name("apprenti_form"));
    }

    @Test
    @WithMockUser
    void deleteApprenti_redirectsToDashboard() throws Exception {
        mockMvc.perform(get("/apprentis/delete/1"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/apprentis/dashboard"));

        verify(apprentiService).deleteApprenti(1L);
    }

    @Test
    @WithMockUser
    void newYear_callsServiceAndRedirects() throws Exception {
        mockMvc.perform(post("/apprentis/newYear").with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/apprentis/dashboard"));

        verify(apprentiService).newAcademiqueYear();
    }
}

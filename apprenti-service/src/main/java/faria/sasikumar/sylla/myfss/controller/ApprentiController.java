package faria.sasikumar.sylla.myfss.controller;

import faria.sasikumar.sylla.myfss.model.Apprenti;
import faria.sasikumar.sylla.myfss.service.ApprentiService;
import faria.sasikumar.sylla.myfss.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/apprentis")
public class ApprentiController {

    private final ApprentiService apprentiService;
    private final EntrepriseRepository entrepriseRepo;
    private final EvaluationRepository evaluationRepo;
    private final MaitreApprentissageRepository maitreRepo;
    private final MissionRepository missionRepo;
    private final VisiteRepository visiteRepo;

    public ApprentiController(ApprentiService apprentiService,
                              EntrepriseRepository entrepriseRepo,
                              EvaluationRepository evaluationRepo,
                              MaitreApprentissageRepository maitreRepo,
                              MissionRepository missionRepo,
                              VisiteRepository visiteRepo) {
        this.apprentiService = apprentiService;
        this.entrepriseRepo = entrepriseRepo;
        this.evaluationRepo = evaluationRepo;
        this.maitreRepo = maitreRepo;
        this.missionRepo = missionRepo;
        this.visiteRepo = visiteRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        List<Apprenti> apprentis = apprentiService.getAllApprentisNoArchived();
        model.addAttribute("apprentis", apprentis);
        model.addAttribute("username", (principal != null) ? principal.getName() : "Invité");
        return "dashboard";
    }

    @GetMapping("/new")
    public String newApprentiForm(Model model) {
        model.addAttribute("apprenti", new Apprenti());
        return "apprenti_form";
    }

    @GetMapping("/edit/{id}")
    public String editApprentiForm(@PathVariable Long id, Model model) {
        Apprenti apprenti = apprentiService.getApprenti(id);
        model.addAttribute("apprenti", apprenti);
        return "apprenti_form";
    }

    @PostMapping("/save")
    public String saveApprenti(@Valid @ModelAttribute Apprenti apprenti, BindingResult result) {
        log.info("error : " + result);
        if (result.hasErrors()) {
            return "apprenti_form";
        }
        apprentiService.createOrUpdateApprenti(apprenti);
        return "redirect:/apprentis/dashboard";
    }

    @PostMapping("/newYear")
    public String newYear(){

        apprentiService.newAcademiqueYear();

        return "redirect:/apprentis/dashboard";
    }

    @PostMapping("/archive/{id}")
    public String archive(@PathVariable Long id ){
        apprentiService.archive(id);
        return "redirect:/apprentis/dashboard";
    }

    @GetMapping("/{id}")
    public String apprentiDetails(@PathVariable Long id, Model model) {
        Apprenti apprenti = apprentiService.getApprenti(id);
        model.addAttribute("apprenti", apprenti);
        model.addAttribute("evaluations", evaluationRepo.findAll());
        model.addAttribute("missions", missionRepo.findAll());
        model.addAttribute("visites", visiteRepo.findAll());
        model.addAttribute("maitres", maitreRepo.findAll());
        model.addAttribute("entreprises", entrepriseRepo.findAll());
        return "apprenti_details";
    }



    @GetMapping("/delete/{id}")
    public String deleteApprenti(@PathVariable Long id) {
        apprentiService.deleteApprenti(id);
        return "redirect:/apprentis/dashboard";
    }
}
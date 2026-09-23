package faria.sasikumar.sylla.myfss.controller;

import faria.sasikumar.sylla.myfss.model.Apprenti;
import faria.sasikumar.sylla.myfss.service.ApprentiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/apprentis")
public class ApprentiController {

    private final ApprentiService apprentiService;

    public ApprentiController(ApprentiService apprentiService) {
        this.apprentiService = apprentiService;
    }

    @InitBinder("apprenti")
    void bindEditableFields(WebDataBinder binder) {
        binder.setAllowedFields("id", "nom", "prenom", "email", "telephone", "programme", "majeure", "annee");
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
        if (result.hasErrors()) {
            return "apprenti_form";
        }
        apprentiService.createOrUpdateApprenti(apprenti);
        return "redirect:/apprentis/dashboard";
    }

    @PostMapping("/newYear")
    public String newYear() {
        apprentiService.newAcademiqueYear();

        return "redirect:/apprentis/dashboard";
    }

    @PostMapping("/archive/{id}")
    public String archive(@PathVariable Long id) {
        apprentiService.archive(id);
        return "redirect:/apprentis/dashboard";
    }

    @GetMapping("/{id}")
    public String apprentiDetails(@PathVariable Long id, Model model) {
        Apprenti apprenti = apprentiService.getApprenti(id);
        model.addAttribute("apprenti", apprenti);
        return "apprenti_details";
    }


    @PostMapping("/delete/{id}")
    public String deleteApprenti(@PathVariable Long id) {
        apprentiService.deleteApprenti(id);
        return "redirect:/apprentis/dashboard";
    }
}

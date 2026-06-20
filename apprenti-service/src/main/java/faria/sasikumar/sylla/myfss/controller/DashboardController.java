package faria.sasikumar.sylla.myfss.controller;

import faria.sasikumar.sylla.myfss.model.Apprenti;
import faria.sasikumar.sylla.myfss.service.ApprentiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class DashboardController {

    private final ApprentiService apprentiService;

    public DashboardController(ApprentiService apprentiService) {
        this.apprentiService = apprentiService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, Principal principal) {
        List<Apprenti> apprentis = apprentiService.getAllApprentisNoArchived();
        model.addAttribute("apprentis", apprentis);

        String username = (principal != null) ? principal.getName() : "Invité";
        model.addAttribute("username", username);

        return "dashboard";
    }
}

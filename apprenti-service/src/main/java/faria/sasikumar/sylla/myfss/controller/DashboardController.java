package faria.sasikumar.sylla.myfss.controller;

import faria.sasikumar.sylla.myfss.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard", "/apprentis/dashboard"})
    public String dashboard(Model model, Principal principal) {
        DashboardService.Dashboard dashboard = dashboardService.load();
        model.addAttribute("apprentis", dashboard.apprentis());
        dashboard.stats().ifPresent(stats -> model.addAttribute("stats", stats));
        model.addAttribute("statsAvailable", dashboard.stats().isPresent());
        model.addAttribute("username", principal != null ? principal.getName() : "Invité");
        return "dashboard";
    }
}

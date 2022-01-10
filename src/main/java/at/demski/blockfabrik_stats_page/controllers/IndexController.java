package at.demski.blockfabrik_stats_page.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {
    @GetMapping("/")
    public String greeting(Model model) {
        return "index";
    }
}

package at.demski.blockfabrik_stats_page.controllers;

import at.demski.blockfabrik_stats_page.service.DatabaseConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DataController {
    final DatabaseConnector dbConnector;

    public DataController(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    @GetMapping("/data/list")
    public String greeting(Model model) {
        model.addAttribute("entryList",dbConnector.getAllDesc());
        return "data_list";
    }
}

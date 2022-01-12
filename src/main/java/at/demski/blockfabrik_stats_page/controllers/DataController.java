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
    public String dataList(Model model) {
        model.addAttribute("entryList",dbConnector.getAllDesc());
        return "data_list";
    }

    @GetMapping("/data/charts")
    public String dataCharts(Model model) {
        model.addAttribute("dataDay3",dbConnector.getAllForDay(3).toArray());
        model.addAttribute("dataDay4",dbConnector.getAllForDay(4).toArray());
        return "data_charts";
    }
}

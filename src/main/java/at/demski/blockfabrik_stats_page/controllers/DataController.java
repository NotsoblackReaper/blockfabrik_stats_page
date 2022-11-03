package at.demski.blockfabrik_stats_page.controllers;

import at.demski.blockfabrik_stats_page.BlockfabrikStatsPageApplication;
import at.demski.blockfabrik_stats_page.entities.DayData;
import at.demski.blockfabrik_stats_page.service.DatabaseConnector;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class DataController {
    final DatabaseConnector dbConnector;

    public DataController(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    @GetMapping("/data/list")
    public String dataList(Model model) {
        List<DayData>data=dbConnector.getDayDataDesc(50);
        System.out.println("Accessing Data list: ");
        System.out.println("Got "+data.size()+" entries");
        model.addAttribute("dayList", data);
        model.addAttribute("data_scraping", BlockfabrikStatsPageApplication.data_scraping);
        return "data_list";
    }
}

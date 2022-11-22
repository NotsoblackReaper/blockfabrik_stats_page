package at.demski.blockfabrik_stats_page.controllers;

import at.demski.blockfabrik_stats_page.BlockfabrikStatsPageApplication;
import at.demski.blockfabrik_stats_page.entities.BlockfabrikDatapoint;
import at.demski.blockfabrik_stats_page.entities.DayData;
import at.demski.blockfabrik_stats_page.service.API.DatabaseConnector;

import at.demski.blockfabrik_stats_page.service.utils.DatapointUtils;
import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import at.demski.blockfabrik_stats_page.service.utils.tensorflow.ModelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.*;

@Controller
public class DataController {
    final DatabaseConnector dbConnector;

    public DataController(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    @GetMapping("/data/list")
    public String dataList(Model model, @RequestParam(value = "date", required=false) java.util.Date date) {
        Date dataDate= DateManager.today();
        if(date!=null){
            dataDate=new Date(date.getTime());
        }
        List<DayData>data=dbConnector.getDayDataDesc(7*4);
        List<BlockfabrikDatapoint>datapoints=dbConnector.getValuesForDay(dataDate);
        datapoints=DatapointUtils.averageDayValues(datapoints,20);
        Collections.reverse(datapoints);
        model.addAttribute("datapoints", datapoints.toArray());
        model.addAttribute("dayList", data);
        model.addAttribute("data_scraping", BlockfabrikStatsPageApplication.data_scraping);
        return "data_list";
    }
}

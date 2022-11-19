package at.demski.blockfabrik_stats_page.controllers;

import at.demski.blockfabrik_stats_page.entities.BlockfabrikDatapoint;
import at.demski.blockfabrik_stats_page.entities.WeatherData;
import at.demski.blockfabrik_stats_page.service.DatabaseConnector;
import at.demski.blockfabrik_stats_page.service.WeatherAPI;
import at.demski.blockfabrik_stats_page.service.utils.DatapointUtils;
import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import at.demski.blockfabrik_stats_page.service.utils.tensorflow.ModelHandler;
import org.springframework.cglib.core.Block;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class AiChartsController {

    final DatabaseConnector dbConnector;
    final ModelHandler modelHandler;

    public AiChartsController(DatabaseConnector dbConnector) {
        this.modelHandler=new ModelHandler();
        this.dbConnector = dbConnector;
    }

    private List<BlockfabrikDatapoint> getPrediction(Integer hour,int averageLength){
        int currentMinute=DateManager.minute()-DateManager.minute()%5;
        int currentHour =DateManager.hour();
        if(currentHour<7||(currentHour==7&&currentMinute<30)){
            currentHour=7;
            currentMinute=25;
        }

        if(hour!=null)currentHour=hour;
        int actualHour=currentHour;

        List<BlockfabrikDatapoint> values=dbConnector.getValuesForDay(DateManager.today(),currentHour,currentMinute);
        Collections.reverse(values);

        WeatherData weatherData= WeatherAPI.getCurrentWeather();

        while(currentHour<22){
            currentMinute+=5;
            if(currentMinute==60){
                ++currentHour;
                currentMinute=0;
            }
            int his5=values.size()>1?values.get(values.size()-1).getValue():0;
            int his10=values.size()>2?values.get(values.size()-2).getValue():0;
            int his15=values.size()>3?values.get(values.size()-3).getValue():0;
            float val=modelHandler.predict(DateManager.day(),weatherData.temperature, weatherData.downpour, weatherData.wind, currentHour,currentMinute,his5,his10,his15);
            int prediction= val<150? Math.round(val):(int)Math.floor(val);
            if(prediction>250)
                prediction=250;
            values.add(new BlockfabrikDatapoint(currentHour,currentMinute,prediction));
        }

        return DatapointUtils.averageDayValues(values,20);
    }

    @GetMapping("/experimental")
    public String homeChart(Model model, @RequestParam(value = "day", required=false) Integer day,@RequestParam(value = "hour", required=false) Integer hour){
        if(day==null)day= DateManager.day()-1;
        model.addAttribute("current",day);
        model.addAttribute("dayName", DateManager.dayName(day));

        int currentTime = DateManager.hour() * 100 + DateManager.minute();
        //If it is before opening hours, set the time to midnight, as to show everything in colour
        if(DateManager.hour()<7||DateManager.hour()<8&&DateManager.minute()<30)currentTime=2400;
        model.addAttribute("currentTime", currentTime);

        //If the selected day is the current day, show predicted values, otherwise show averages
        if(DateManager.day()-1==day){
            model.addAttribute("data", getPrediction(hour,20));
        }else{
            model.addAttribute("data", dbConnector.getHalfHourAveragesNew(day+1,20));
        }




        return "index_exp";
    }
}

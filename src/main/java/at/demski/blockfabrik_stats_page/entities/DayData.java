package at.demski.blockfabrik_stats_page.entities;

import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class DayData {
    @GeneratedValue
    @Id
    private Integer dayId;
    private Date date;
    private int weekday;
    private float temp;
    private float rain;
    private float wind;
    private boolean holiday;
    private String holiday_name;
    private boolean historic;


    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "day")
    @OrderBy("hour asc, minute asc")
    private Set<BlockfabrikDatapoint>datapoints=new HashSet<>();

    public DayData(Integer id,Date date, int weekday,float temp, float rain, float wind, boolean holiday, String holiday_name, boolean historic) {
        this.dayId=id;
        this.date = date;
        this.weekday= weekday;
        this.temp = temp;
        this.rain = rain;
        this.wind = wind;
        this.holiday = holiday;
        this.holiday_name = holiday_name;
        this.historic = historic;
    }

    public DayData(Date date, float temp, float rain, float wind, boolean holiday, String holiday_name, boolean historic) {
        this.date = date;
        this.weekday= DateManager.weekday(date);
        this.temp = temp;
        this.rain = rain;
        this.wind = wind;
        this.holiday = holiday;
        this.holiday_name = holiday_name;
        this.historic = historic;
    }

    public float getSimilarity(DayData other){
        float[]exponentMult={.3f,.35f,.2f};
        float[]weights={.7f,.8f,.3f};
        float exponentOffset=-0.3f;
        float obsoleteYears=2;
        float minVal=0.1f;
        float offset=0.0f;

        long daysBetween= ChronoUnit.DAYS.between(LocalDate.parse(date.toString()), LocalDate.parse(other.date.toString()));
        int weeksBetween= (int) (daysBetween/7);
        float outdated=1.0f-weeksBetween*(1f/(52f*obsoleteYears));

        if(other.weekday!=weekday)return 0;
        float similarity=0;

        float deltaTemp=Math.abs(temp-other.getTemp());
        float deltaRain=Math.abs(rain-other.getRain());
        float deltaWind=Math.abs(wind-other.getWind());

        double similarityTemp=exponentOffset+Math.pow(Math.E,-deltaTemp*exponentMult[0])*weights[0];
        if(similarityTemp<minVal)similarityTemp=minVal;
        double similarityRain=exponentOffset+Math.pow(Math.E,-deltaRain*exponentMult[1])*weights[1];
        if(similarityRain<minVal)similarityRain=minVal;
        double similarityWind=exponentOffset+Math.pow(Math.E,-deltaWind*exponentMult[2])*weights[2];
        if(similarityWind<minVal)similarityWind=minVal;

        similarity+=similarityTemp+similarityRain+similarityWind;

        boolean bothHoliday=isHoliday()&&other.isHoliday();
        if(bothHoliday)similarity+=0.2f;

        similarity*=outdated;
        similarity+=offset;

        if(similarity<.01f)similarity=.01f;
        if(similarity>1f)similarity=1f;
        return similarity;
    }
}

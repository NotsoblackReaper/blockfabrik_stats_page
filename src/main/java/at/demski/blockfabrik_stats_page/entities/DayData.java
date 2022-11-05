package at.demski.blockfabrik_stats_page.entities;

import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "day")
    private Set<BlockfabrikDatapoint>datapoints=new HashSet<>();


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
}

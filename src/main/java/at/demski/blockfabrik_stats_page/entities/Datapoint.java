package at.demski.blockfabrik_stats_page.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class Datapoint {
    @Id
    @GeneratedValue
    private Long id;
    private Integer datapoint_id;

    private int datapoint_act;
    private int datapoint_max;

    private Date datapoint_date;
    private int datapoint_day;
    private int datapoint_hour;
    private int datapoint_minute;

    public int getDatapoint_id() {
        return datapoint_id;
    }

    public void setDatapoint_id(Integer datapoint_id) {
        this.datapoint_id = datapoint_id;
    }

    public Integer getDatapoint_act() {
        return datapoint_act;
    }

    public void setDatapoint_act(int datapoint_act) {
        this.datapoint_act = datapoint_act;
    }

    public int getDatapoint_max() {
        return datapoint_max;
    }

    public void setDatapoint_max(int datapoint_max) {
        this.datapoint_max = datapoint_max;
    }

    public Date getDatapoint_date() {
        return datapoint_date;
    }

    public void setDatapoint_date(Date datapoint_date) {
        this.datapoint_date = datapoint_date;
    }

    public int getDatapoint_day() {
        return datapoint_day;
    }

    public void setDatapoint_day(int datapoint_day) {
        this.datapoint_day = datapoint_day;
    }

    public int getDatapoint_hour() {
        return datapoint_hour;
    }

    public void setDatapoint_hour(int datapoint_hour) {
        this.datapoint_hour = datapoint_hour;
    }

    public int getDatapoint_minute() {
        return datapoint_minute;
    }

    public void setDatapoint_minute(int datapoint_minute) {
        this.datapoint_minute = datapoint_minute;
    }
}

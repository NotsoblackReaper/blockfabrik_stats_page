package at.demski.blockfabrik_stats_page.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.cglib.core.Block;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Entity
public class BlockfabrikDatapoint {
    @GeneratedValue
    @Id
    private Integer datapoint_id;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "day_id", nullable = true)
    private DayData day;
    private int hour;
    private int minute;
    private int value;

    public int compareTime(BlockfabrikDatapoint other){
        if(other.getHour()>hour)return 1;
        if(other.getHour()<hour)return -1;
        if(other.getMinute()>minute)return 1;
        if(other.getMinute()<minute)return -1;
        return 0;
    }

    public BlockfabrikDatapoint(int hour,int minute,int value){
        this.hour=hour;
        this.minute=minute;
        this.value=value;
    }

    public BlockfabrikDatapoint(BlockfabrikDatapoint other){
        this.hour=other.hour;
        this.minute=other.minute;
        this.value=other.value;
    }
}

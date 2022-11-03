package at.demski.blockfabrik_stats_page.entities;

import lombok.*;

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
    private Integer day;
    private int hour;
    private int minute;
    private int value;
}

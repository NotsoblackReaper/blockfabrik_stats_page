package at.demski.blockfabrik_stats_page;

import at.demski.blockfabrik_stats_page.entities.DayData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DayDataSimilarityTest {


    Connection con;
    List<DayData> days;

    private List<DayData> getDays() throws SQLException {
        String query = "select * from day_data order by date asc";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        List<DayData> days = new ArrayList<>();

        while (rs.next()) {
            days.add(new DayData(
                    rs.getInt(1),
                    rs.getDate(2),
                    rs.getInt(3),
                    rs.getFloat(4),
                    rs.getFloat(5),
                    rs.getFloat(6),
                    rs.getBoolean(7),
                    rs.getString(8),
                    rs.getBoolean(9)
                    ));
        }

        return days;
    }

    @BeforeAll
    public void setup() throws SQLException {
        con = DriverManager.getConnection("jdbc:mariadb://192.168.1.82:3306/database?user=dev&password=lWGOPIhnDDgeepFtl6f6");
        days=getDays();
    }

    @AfterAll
    public void cleanup() throws SQLException {
        con.close();
    }

    @Test
    void Similarity(){
        for(int j=days.size()-1,k=0;k<20;j-=10,++k) {
            DayData root = days.get(j);

            float maxSim = Float.MIN_VALUE;
            DayData maxSimDay = null;

            float minSim = Float.MAX_VALUE;
            DayData minSimDay = null;

            //System.out.println(root);
            //System.out.println("\n==========================================\n");
            for (int i = 0; i < days.size() - 1; ++i) {
                if(i==j)continue;
                if (root.getWeekday() != days.get(i).getWeekday()) continue;
                DayData day = days.get(i);
                float sim = root.getRelevance(day);
                //System.out.println("Sim " + sim + " Comparing to:" + day);

                if (sim > maxSim) {
                    maxSim = sim;
                    maxSimDay = day;
                }
                if (sim < minSim) {
                    minSim = sim;
                    minSimDay = day;
                }
            }

            //System.out.println("\n==========================================\n");
            System.out.println(root);
            System.out.println("Min Sim " + minSim + " Comparing to:" + minSimDay);
            System.out.println("Max Sim " + maxSim + " Comparing to:" + maxSimDay);
            System.out.println("\n==========================================\n");
        }
    }
}

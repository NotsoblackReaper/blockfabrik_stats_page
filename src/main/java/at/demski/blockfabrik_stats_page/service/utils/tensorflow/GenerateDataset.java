package at.demski.blockfabrik_stats_page.service.utils.tensorflow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class GenerateDataset {

    public static void main(String[]args){
        GenerateDataset.WriteToCsv();
    }

    public static float[][] WriteToCsv() {
        Connection con;
        ResultSet rs = null;
        ResultSetMetaData rsmd=null;
        int count = 0;
        int columns=7;
        try {
            con = DriverManager.getConnection("jdbc:mariadb://192.168.1.82:3306/database?user=dev&password=lWGOPIhnDDgeepFtl6f6");
            String query = "select weekday, temp, rain, wind, hour, minute, value,\n" +
                    "       getOffsetValue(dp.day_id,dp.hour,dp.minute,5) as 5min,\n" +
                    "       getOffsetValue(dp.day_id,dp.hour,dp.minute,10) as 10min,\n" +
                    "       getOffsetValue(dp.day_id,dp.hour,dp.minute,15) as 15min\n" +
                    "from blockfabrik_datapoint dp join day_data dd on dd.day_id = dp.day_id where dd.historic=true and (dp.hour>7 or (dp.hour=7 and dp.minute>30));";
            String countQuery = "select count(*) from blockfabrik_datapoint dp join day_data dd on dd.day_id = dp.day_id where dd.historic=true and (dp.hour>7 or (dp.hour=7 and dp.minute>30))";
            Statement stmt = con.createStatement();
            rs=stmt.executeQuery(countQuery);
            if(rs.next())
            count = rs.getInt(1);
            rs = stmt.executeQuery(query);
            rsmd=rs.getMetaData();
            columns=rsmd.getColumnCount();
            System.out.println("Executed Queries");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        float[][] data = new float[count][columns];
        int row=0;
        try {
            while (rs.next()) {
                for(int i=0;i<columns;++i)
                    data[row][i]=rs.getFloat(i+1);
                ++row;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        StringBuilder csvContent= new StringBuilder();
        for(int i=0;i<columns;++i){
            try {
                csvContent.append(rsmd.getColumnName(i+1));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(i<columns-1)csvContent.append(';');
        }
        csvContent.append(System.lineSeparator());
        for(int i=0;i<count;++i){
            for(int j=0;j<columns;++j){
                csvContent.append(data[i][j]);
                if(j<columns-1)csvContent.append(';');
            }
            csvContent.append(System.lineSeparator());
        }
        try {
            File f = new File("datapoints_simple.csv");
            if (f.createNewFile()) {
                System.out.println("File created: " + f.getName());
            } else {
                System.out.println("File already exists.");
            }

            FileWriter fw = new FileWriter(f);
            fw.write(csvContent.toString());
            fw.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data;
    }
}

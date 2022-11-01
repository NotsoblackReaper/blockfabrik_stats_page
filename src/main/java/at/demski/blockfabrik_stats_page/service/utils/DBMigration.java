package at.demski.blockfabrik_stats_page.service.utils;

import at.demski.blockfabrik_stats_page.entities.WeatherData;
import at.demski.blockfabrik_stats_page.service.WeatherAPI;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBMigration {

    Connection con;

    public static void main(String[] args) {
        DBMigration migration = new DBMigration();
        System.out.println(WeatherAPI.getCurrentWeather().temperature + "°C");
        ResultSet rs = migration.getDatapoints();
        System.out.println("Got datapoints");
        migration.migrateData(rs, Date.valueOf("2022-10-31"));
    }

    public DBMigration() {
        try {
            con = DriverManager.getConnection("jdbc:mariadb://192.168.1.82:3306/database?user=dev&password=lWGOPIhnDDgeepFtl6f6");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void printResultSet(ResultSet rs) {
        printResultSet(rs, Integer.MAX_VALUE);
    }

    public void printResultSet(ResultSet rs, int limit) {
        ResultSetMetaData rsmd = null;
        try {
            rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            int row = 0;
            while (rs.next() && row++ < limit) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getDatapoints() {
        String query = "select * from datapoint order by datapoint_date desc, datapoint_hour desc, datapoint_minute desc";
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getDayId(Date date) {
        String query = "select day_id from day_data where date = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int generateDay(Date date) {
        String query = "insert into day_data (date, temp, rain, wind, holiday, holiday_name) values (?,?,?,?,?,?)";
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, date);
            WeatherData data=WeatherAPI.getWeather(date);

            stmt.setFloat(2, data.temperature);
            stmt.setFloat(3, data.downpour);
            stmt.setFloat(4, data.wind);

            stmt.setBoolean(5, false);
            stmt.setString(6, null);

            System.out.println("Inserting: " + data);

            if (stmt.executeUpdate() == 0)
                throw new SQLException("Creating user failed, no rows affected.");

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void insertDatapoint(int day, int hour, int minute, int value) {
        String query = "insert into blockfabrik_datapoint (day, hour, minute, value) values (?,?,?,?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, day);
            stmt.setInt(2, hour);
            stmt.setInt(3, minute);
            stmt.setInt(4, value);

            if (stmt.executeUpdate() == 0)
                throw new SQLException("Creating user failed, no rows affected.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public int countTable(String table){
        String query = "select count(*) from "+table;
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next())
            return rs.getInt(1);
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void migrateData(ResultSet datapoints_old, Date today) {
        try {
            con.rollback();
            int existingData=countTable("blockfabrik_datapoint");
            System.out.println(existingData+" existing entries");
            PreparedStatement stmt = con.prepareStatement("insert into blockfabrik_datapoint (day, hour, minute, value) values (?,?,?,?)");
            int row = 1;
            while (datapoints_old.next()) {
                int value = datapoints_old.getInt("datapoint_act");
                Date date = datapoints_old.getDate("datapoint_date");
                int hour = datapoints_old.getInt("datapoint_hour");
                int minute = datapoints_old.getInt("datapoint_minute");

                if(minute%5!=0)continue;

                int dayId = getDayId(date);
                if (dayId == 0)
                    dayId = generateDay(date);
                stmt.setInt(1, dayId);


                stmt.setInt(2, hour);
                stmt.setInt(3, minute);
                stmt.setInt(4, value);

                stmt.addBatch();
                if (row % 1000 == 0) {
                    System.out.println("Inserting rows...");
                    int []res=stmt.executeBatch();
                    int succ= Arrays.stream(res).filter(i -> i!= Statement.EXECUTE_FAILED).toArray().length;
                    System.out.println("Inserted "+succ+" rows");
                    int insertedData=countTable("blockfabrik_datapoint")-existingData;
                    System.out.println("Inserted " + insertedData + " rows total");
                    stmt.clearBatch();
                    con.commit();
                }
                row++;
            }
            stmt.executeBatch();
            con.commit();
            int total=countTable("blockfabrik_datapoint");
            System.out.println("Inserted " + row + " rows (total "+total+")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
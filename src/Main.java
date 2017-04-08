
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Main {

    public static void main(String[] args) {

        try {
            DBConnection.connectToDB("POC");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LocalDate today = new LocalDate();
        LocalDate startDate = today.minusMonths(1).dayOfMonth().withMinimumValue();
        LocalDate startNextMonth = today.dayOfMonth().withMinimumValue();

        System.out.println(startDate);
        System.out.println(startNextMonth);


        DateTimeFormatter dtfForPrint = DateTimeFormat.forPattern("E d MMM");
        DateTimeFormatter dtfForDB = DateTimeFormat.forPattern("yyyy-MM-dd");

        for (LocalDate date = startDate; date.isBefore(startNextMonth); date = date.plusDays(1)) {
            System.out.println(dtfForPrint.print(date));
            System.out.println(dtfForDB.print(date));
            for (int hour = 0; hour <= 23; hour++) {
                String query="SELECT uid FROM connexion WHERE (time_con BETWEEN '08:00:00' AND '10:00:00') AND date_con LIKE ?;";
                PreparedStatement stmt = null;
                try {
                    assert stmt != null;
                    stmt.setString(1,dtfForDB.print(date));
                    stmt = DBConnection.connection.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        int uid = rs.getInt("uid");
                        System.out.println(uid);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }




        DBConnection.closeConnection();
    }
}

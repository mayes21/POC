
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
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

        /* Init date/time */
        LocalDate today = new LocalDate();
        /* first day of every last month */
        LocalDate startDate = today.minusMonths(1).dayOfMonth().withMinimumValue();
        /* first day of current month */
        LocalDate startNextMonth = today.dayOfMonth().withMinimumValue();
        /* First time of day : MIDNIGHT */
        LocalTime endTime = LocalTime.MIDNIGHT.minusSeconds(1);

        /* Date/Time formatter */
        DateTimeFormatter dtfForPrint = DateTimeFormat.forPattern("E d MMM");
        DateTimeFormatter dtfForDB = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTimeFormatter timePattern = DateTimeFormat.forPattern("HH:mm:ss");

        for (LocalDate date = startDate; date.isBefore(startNextMonth); date = date.plusDays(1)) {
            System.out.println(dtfForPrint.print(date));
            LocalTime time = LocalTime.MIDNIGHT;
            do {
                System.out.println(timePattern.print(time));
                String query="SELECT count(*) FROM connexion WHERE (time_con BETWEEN ? AND ?) AND date_con LIKE ?";
                PreparedStatement preparedStatement;
                try {
                    /* prepare query */
                    preparedStatement = DBConnection.connection.prepareStatement(query);
                    preparedStatement.setString(1, time.toString());
                    preparedStatement.setString(2, time.plusHours(1).toString());
                    preparedStatement.setString(3,dtfForDB.print(date));
                    ResultSet rs = preparedStatement.executeQuery();
                    /* display result */
                    rs.next();
                    System.out.println(rs.getInt(1));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                time = time.plusHours(1);
            } while (time.isBefore(endTime) && !time.isEqual(LocalTime.MIDNIGHT));
        }

        DBConnection.closeConnection();
    }
}

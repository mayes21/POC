import com.opencsv.CSVWriter;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by amabb on 16/05/17.
 */
public class MainSecond {

    public static void main(String[] args) throws IOException {
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
        DateTimeFormatter dtfForExport = DateTimeFormat.forPattern("MM-yyyy");
        DateTimeFormatter timePatternForPrint = DateTimeFormat.forPattern("HH");

        String fileName = dtfForExport.print(LocalDate.now().minusMonths(1))+".csv";

        int nbDayOfMonth = today.minusMonths(1).dayOfMonth().getMaximumValue();
        String[] datesOfMonth = new String[nbDayOfMonth+1];

        int i = 1;
        for (LocalDate date = startDate; date.isBefore(startNextMonth); date = date.plusDays(1)) {
            datesOfMonth[i] = dtfForPrint.print(date);
            i++;
        }

        /* Create file with append parameter setted to true */
        FileWriter mFileWriter = null;
        try {
            mFileWriter = new FileWriter(fileName, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Write into CSV file using Opencsv library */
        try (CSVWriter writer = new CSVWriter(mFileWriter, ',', CSVWriter.NO_QUOTE_CHARACTER)) {
            writer.writeNext(datesOfMonth);

            LocalTime time = LocalTime.MIDNIGHT;
            String[] nbrConn = new String[nbDayOfMonth+1];

            do {
                int k=1;
                nbrConn[0] = timePatternForPrint.print(time) + "h-" + timePatternForPrint.print(time.plusHours(1)) + "h";
                for (LocalDate date = startDate; date.isBefore(startNextMonth); date = date.plusDays(1)) {
                    String query="SELECT count(*) FROM connexion WHERE (time_con BETWEEN ? AND ?) AND date_con LIKE ?";
                    PreparedStatement preparedStatement;
                    try {
                        /* prepare query */
                        preparedStatement = DBConnection.connection.prepareStatement(query);
                        preparedStatement.setString(1, timePattern.print(time));
                        preparedStatement.setString(2, timePattern.print(time.plusHours(1)));
                        preparedStatement.setString(3, dtfForDB.print(date));
                        ResultSet rs = preparedStatement.executeQuery();
                        while (rs.next()) {
                            nbrConn[k] = String.valueOf(rs.getInt(1));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    k++;
                }

                writer.writeNext(nbrConn);

                time = time.plusHours(1);
            } while (time.isBefore(endTime) && !time.isEqual(LocalTime.MIDNIGHT));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DBConnection.closeConnection();
    }

}


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * POC
 * <p>
 * Created by Amayas on 08/04/17.
 */
public class DBConnection {

    protected static Connection connection = null;
    private static Session session = null;


    private static void connectSSH() throws SQLException {
        String sshHost = "centos.lxc";
        String sshuser = "root";

        //String SshKeyFilepath = "/Users/XXXXXX/.ssh/id_rsa";

        int localPort = 8740; // any free port can be used
        String remoteHost = "127.0.0.1";
        int remotePort = 3306;
        String localSSHUrl = "localhost";
        String driverName = "com.mysql.jdbc.Driver";

        try {
            java.util.Properties config = new java.util.Properties();
            JSch jsch = new JSch();
            session = jsch.getSession(sshuser, sshHost, 22);
            session.setPassword("centos");
            config.put("StrictHostKeyChecking", "no");
            config.put("ConnectionAttempts", "3");
            session.setConfig(config);
            session.connect();

            System.out.println("SSH Connected");

            Class.forName(driverName).newInstance();

            int assinged_port = session.setPortForwardingL(localPort, remoteHost, remotePort);

            System.out.println("localhost:" + assinged_port + " -> " + remoteHost + ":" + remotePort);
            System.out.println("Port Forwarded");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void connectToDataBase(String dataBaseName) throws SQLException {
        String dbuserName = "root";
        String dbpassword = "gpFt9yoxg%dq";
        int localPort = 8740; // any free port can be used
        String localSSHUrl = "localhost";
        try {

            //mysql database connectivity
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(localSSHUrl);
            dataSource.setPortNumber(localPort);
            dataSource.setUser(dbuserName);
            dataSource.setAllowMultiQueries(true);

            dataSource.setPassword(dbpassword);
            dataSource.setDatabaseName(dataBaseName);

            connection = dataSource.getConnection();

            System.out.print("Connection to server successful!:" + connection + "\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void connectToDB(String databaseName) throws SQLException {
        connectSSH();
        connectToDataBase(databaseName);
    }

    private static void closeSSH() {
        if (session != null && session.isConnected()) {
            System.out.println("Closing SSH Connection");
            session.disconnect();
        }
    }

    private static void closeDBConnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Closing Database Connection");
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static void closeConnection() {
        closeDBConnect();
        closeSSH();
    }
}

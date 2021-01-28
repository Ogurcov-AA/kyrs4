package server;

import com.mysql.cj.jdbc.result.ResultSetImpl;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;

public class DatabaseHandler extends Config {
    Connection dbConnections;
    private int countFlag = 0;
    private String tempMsg = null;


    public Connection getDbConnections(String dbNames) throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbNames + "?useUnicode=true&serverTimezone=UTC&useSSL=true&verifyServerCertificate=false";
        Class.forName("com.mysql.jdbc.Driver");
        dbConnections = DriverManager.getConnection(connectionString, dbUser, dbPass);
        return dbConnections;
    }

    public String searchDialogKey(String msg) {
        String[] value = msg.split(" ");

        String select = "SELECT * FROM " + DIALOGKEY_TABLE + " WHERE " + USERONE + "=?" + " AND " +
                USERTWO + "=?";
        try {
            PreparedStatement prSt = getDbConnections(dbName).prepareStatement(select);
            prSt.setString(1, value[0]);
            prSt.setString(2, value[1]);
            ResultSet resultSet = prSt.executeQuery();
            if (!resultSet.next()) {
                if (countFlag == 0) {
                    countFlag++;
                    tempMsg = searchDialogKey(value[1] + " " + value[0]);
                } else {
                    tempMsg = createNewDialogKey(value[0], value[1]);
                }
            } else {
                tempMsg = resultSet.getString("dialogKey");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Reg: Error");
            e.printStackTrace();
        }
        return tempMsg;
    }

    private int generateKey() {
        int min = 100000;
        int max = 999999;
        int rnd;
        do {
            rnd = (int) (Math.random() * ((max - min) + 1)) + min;
        }
        while (checkKeyOnBD(String.valueOf(rnd)));
        return rnd;
    }

    protected boolean checkKeyOnBD(String value) {
        String select = "SELECT * FROM " + Config.DIALOGKEY_TABLE + " WHERE " + Config.DIALOGKEY +
                "=" + value;
        try {
            PreparedStatement prSt = getDbConnections(dbName).prepareStatement(select);
            ResultSet resultSet = prSt.executeQuery();
            if (!resultSet.next())
                return false;
            else return true;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("CheckError: Error");
            e.printStackTrace();
        }
        return false;
    }

    protected String createNewDialogKey(String userOne, String userTwo) {
        String insert = "INSERT INTO " + DIALOGKEY_TABLE + "( " + USERONE + ","
                + USERTWO + ","
                + DIALOGKEY + " ) VALUES (?,?,?)";
        String key = String.valueOf(generateKey());
        try {
            PreparedStatement prSt = getDbConnections(dbName).prepareStatement(insert);
            prSt.setString(1, userOne);
            prSt.setString(2, userTwo);
            prSt.setString(3, key);
            prSt.executeUpdate();
            if (createDialogKeyTable(key)) {
                return key;
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    public boolean createDialogKeyTable(String key) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + "m" + key + " (id INT(121) PRIMARY KEY AUTO_INCREMENT," +
                "date datetime NOT NULL," +
                "login varchar(121)," +
                "massege text," +
                "wasEdit TINYINT(1)," +
                "wasDelete TINYINT(1))";
        try {
            Statement prSt = getDbConnections(dbNameSecond).createStatement();
            prSt.executeUpdate(createTable);
            return true;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    protected boolean addMessageToBD(String msg) {
        String key = msg.substring(0, msg.indexOf(' '));
        msg = msg.substring(msg.indexOf(' ') + 1);
        Date date = new Date();
        DateFormat tt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        System.out.println(tt.format(date));
        String insert = "INSERT INTO " + "m" + key + "( " + MES_DATE + ","
                + MES_LOGIN + ","
                + MES_MESSAGE + ","
                + MES_WASEDIT +  "," +
                  MES_WASDELETE + " ) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement prSt = getDbConnections(dbNameSecond).prepareStatement(insert);
            prSt.setString(1, String.valueOf(tt.format(date)));
            prSt.setString(2, msg.substring(0, msg.indexOf(' ')));
            msg = msg.substring(msg.indexOf(' ') + 3);
            prSt.setString(3, msg);
            prSt.setBoolean(4, false);
            prSt.setBoolean(5,false);
            prSt.executeUpdate();
            return true;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public ResultSet countMsgInDialogs(String key) {
        ResultSet resultSet = null;

        String select = "SELECT COUNT(*) FROM " + "m" + key;

        try {
            PreparedStatement prSt = getDbConnections(dbNameSecond).prepareStatement(select);
            resultSet = prSt.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("CheckError: Error");
            e.printStackTrace();
        }
        return resultSet;
    }

    public ArrayList<String> getArrayWithNewMsg(int count, String key) {
        String select = "SELECT * FROM " + "m" + key + " WHERE " + Config.MES_ID + ">" + count + " AND " + Config.MES_WASDELETE + "=" + false ;
        ResultSet resultSet = null;
        ArrayList<String> temp = new ArrayList<>();
        try {
            PreparedStatement prSt = getDbConnections(dbNameSecond).prepareStatement(select);
            resultSet = prSt.executeQuery();

        while (resultSet.next()) {
                    String message = resultSet.getString("login") + " : " +
                            resultSet.getString("massege") + " (" +
                            resultSet.getString("date") + ")";
                    System.out.println(message);
                    temp.add(message);

         }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("CheckError: Error");
            e.printStackTrace();
        }
        return temp;
    }

    protected ResultSet checkKey(String value) {
        ResultSet resultSet = null;
        System.out.println(value);
        String select = "SELECT * FROM " + Config.DIALOGKEY_TABLE + " WHERE " + Config.DIALOGKEY +
                "=" + value.substring(0, value.indexOf(" "));
        try {
            PreparedStatement prSt = getDbConnections(dbName).prepareStatement(select);
            resultSet = prSt.executeQuery();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("CheckError: Error");
            e.printStackTrace();
        }
        return resultSet;
    }
}

package DataServ;

import java.sql.*;

public class DatabaseHandler extends Config {
    Connection dbConnections;

    public Connection getDbConnections() throws ClassNotFoundException, SQLException {
            String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?useUnicode=true&serverTimezone=UTC&useSSL=true&verifyServerCertificate=false";
           Class.forName("com.mysql.jdbc.Driver");
        dbConnections = DriverManager.getConnection(connectionString, dbUser, dbPass);
        return dbConnections;
    }

    public boolean signUpUser(String msg) {
        String value[] = msg.split(" ");

        String insert = "INSERT INTO " + Config.USER_TABLE + "(" +
                Config.LOGIN + "," + Config.PASS + "," + Config.SALT + "," +
                Config.NAME + "," + Config.SURNAME + "," +
                Config.COUNTRY + "," + Config.MAIL + "," +
                Config.BDAY + "," + Config.REGDAY + "," +
                Config.GENDER + "," + Config.IP + ")" +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement prSt = getDbConnections().prepareStatement(insert);
            prSt.setString(1, value[0]);
            prSt.setString(2, value[1]);
            prSt.setString(3, value[2]);
            prSt.setString(4, value[3]);
            prSt.setString(5, value[4]);
            prSt.setString(6, value[5]);
            prSt.setString(7, value[6]);
            prSt.setString(8, value[7]);
            prSt.setString(9, value[8]);
            prSt.setString(10, value[9]);
            prSt.setString(11, value[10]);
            prSt.executeUpdate();
            System.out.println("Reg: User added to the list");
            return true;
        } catch (SQLException e) {
            System.out.println("Reg: Error");
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("Reg: Error");
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet LoginUser(String loginPas) {

        String temp[] = loginPas.split(" ");
        String login = temp[0];
        String pass = temp[1];

        String select = "SELECT * FROM " + Config.USER_TABLE + " WHERE " +
                Config.LOGIN + "=? AND " + Config.PASS + "=?";
        ResultSet result = null;

        try {
            PreparedStatement prSt = getDbConnections().prepareStatement(select);
            prSt.setString(1, login);
            prSt.setString(2, pass);
            result = prSt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResultSet RepeatField(String value) {
        ResultSet result = null;
        String temp[] = value.split(" ");
        String sqlField = temp[0];
        String textField = temp[1];

        String select = "SELECT * FROM " + Config.USER_TABLE + " WHERE " +
                sqlField + "=?";

        try {
            PreparedStatement prSt = getDbConnections().prepareStatement(select);
            prSt.setString(1, textField);
            result = prSt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResultSet GetKeyUser(String value) {
        ResultSet result = null;

        String select = "SELECT " + "salt" + " FROM " + Config.USER_TABLE + " WHERE " +
                Config.LOGIN + "=?";

        try {
            PreparedStatement prSt = getDbConnections().prepareStatement(select);
            prSt.setString(1, value);
            result = prSt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResultSet GetUser(String login){
        ResultSet resultSet = null;

        String select = "SELECT * FROM " + Config.USER_TABLE +
                        " WHERE " + LOGIN + "=?";
        try {
            PreparedStatement prSt = getDbConnections().prepareStatement(select);
            prSt.setString(1, login);
            resultSet = prSt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet GetCountUser(){
        ResultSet resultSet = null;
           String select = "SELECT " + Config.LOGIN + "," + Config.NAME + "," +
                                Config.SURNAME + "," + Config.IP + "," +
                                Config.MAIL + "," + Config.BDAY + "," +
                                Config.COUNTRY + " FROM " + Config.USER_TABLE;
        try {
            PreparedStatement prSt = getDbConnections().prepareStatement(select);
            resultSet = prSt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resultSet;
    }




}

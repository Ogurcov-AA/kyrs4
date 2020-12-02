package DataServ;

import ChatNetwork.Connection;
import ChatNetwork.ConnectionLis;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class    DataServer implements ConnectionLis {
    public static void main(String[] args) {
        new DataServer();
    }

    private final ArrayList<Connection> connections = new ArrayList<>();

    private DataServer() {
        System.out.println("Server Run");
        try (ServerSocket serverSocket = new ServerSocket(8153)) {
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("Exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public synchronized void ConnectionReady(Connection connection) {
        connections.add(connection);
         System.out.println("+");
          //   SendToAllConnection("client: " + connection + "- online");
    }

    public int counter(ResultSet result) {
        int count = 0;
        while (true) {
            try {
                if (!result.next()) break;
                count++;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }return count;

    }

    public String CheckRepeat(ResultSet result, String value, String sufix) {
        if (value.equals("none")) {
            if (counter(result) != 0) {
                return sufix + "Error";
            } else return sufix + "Success";
        } else if (value.equals("one")) {
            if (counter(result) == 1) {
                return "Success";
            } else return "Error";
        }
        return "Error";
    }

    private String sendMes(ResultSet resultSet, String  sufix) {
        String msg = null;
            try {
                resultSet.next();
                msg = resultSet.getString(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
         return msg;
    }

    @Override
    public synchronized void ReceiveString(Connection connection, String value) {
        System.out.println("in programm : " + value);
        String msg = "Error";
        String msgArr[] = value.split(" ");
        value = "";

        DatabaseHandler dbHandler = new DatabaseHandler();

      for (int i=1;i<msgArr.length;i++){
        value += msgArr[i] + " ";
      }
      value = value.trim();
        if (msgArr[0].equals("repeat")) {
            ResultSet result = dbHandler.RepeatField(value);
            msg = CheckRepeat(result, "none",msgArr[1]);

        } else if (msgArr[0].equals("dataSign")) {
            ResultSet resultSet = dbHandler.LoginUser(value);
            msg = CheckRepeat(resultSet,"one", msgArr[0]);

        } else if (msgArr[0].equals("dataReg")) {
            if(dbHandler.signUpUser(value)==true){
                msg = "RegSuccess";
            }else {
                msg = "RegError";
            }
        }
        else if (msgArr[0].equals("getkey")){
            ResultSet result = dbHandler.GetKeyUser(value);
            msg = sendMes(result, "Key");
        }
        else if(msgArr[0].equals("getUser")){
            ResultSet result = dbHandler.GetUser(value);
            msg = ParseUserInfo(result);
        }
        else if(msgArr[0].equals("getUsersList")){
            ResultSet resultSet = dbHandler.GetCountUser();
            try {
                while(resultSet.next()) {
                    msg =   resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + " " +
                            resultSet.getString(4) + " " + resultSet.getString(5) + " " + resultSet.getString(6) + " " +
                            resultSet.getString(7);
                    connection.sendString(msg);
                }
                msg = "end";
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connection.sendString(msg);
    }


    public String ParseUserInfo(ResultSet resultSet) {
       String msg = "";
        try {
            resultSet.next();
            msg += "user ";
        msg += resultSet.getString(Config.ID) + " ";
        msg += resultSet.getString(Config.LOGIN) + " ";
        msg += resultSet.getString(Config.NAME) + " ";
        msg += resultSet.getString(Config.SURNAME) + " ";
        msg += resultSet.getString(Config.IP) + " ";
        msg += resultSet.getString(Config.COUNTRY) + " ";
        msg += resultSet.getString(Config.MAIL) + " ";
        msg += resultSet.getString(Config.BDAY) + " ";
        msg += resultSet.getString(Config.REGDAY) + " ";

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msg.trim();
    }



        @Override
        public synchronized void Disconnection(Connection connection) {
            connections.remove(connection);
            System.out.println("client: " + connection + "- disconnected");
        }

        @Override
        public synchronized void Exception(Connection connection, Exception e) {
            System.out.println("Exception: " + e);
        }

        private void SendToAllConnection(String value){
          //  System.out.println(value);
            for (int i=0; i < connections.size();i++)
                connections.get(i).sendString(value);
        }

}

package DataServ;

import ChatNetwork.*;

import java.io.*;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class    DataServer implements ConnectionLis {
    public static void main(String[] args) {
        new DataServer();
    }

    private final ArrayList<Connection> connections = new ArrayList<>();

    private DataServer() {
        System.out.println("Server Run");
        try (ServerSocket serverSocket = new ServerSocket(8163)) {
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
    public synchronized void ReceiveString(Connection connection, String value, InputStream bytes) {
        try {
            Thread.sleep(100);
            System.out.println( " ===--- " + bytes.available());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Seriazable seriazable = new Seriazable();
        List<String> log = new ArrayList<>(seriazable.saveDateFile("DataServer\\src\\resourse\\" +value, bytes));
        String msg = "Error";
        for(int item = 0;item<log.size();item++) {
            System.out.println("in programm : " + log.get(item));
            String msgArr[] = log.get(item).split(" ");
            log.remove(item);
            item--;
            DatabaseHandler dbHandler = new DatabaseHandler();
            value = "";
            for (int i = 1; i < msgArr.length; i++) {
                value += msgArr[i] + " ";
            }
            value = value.trim();
            if (msgArr[0].equals("repeat")) {
                ResultSet result = dbHandler.RepeatField(value);
                msg = CheckRepeat(result, "none", msgArr[1]);

            } else if (msgArr[0].equals("dataSign")) {
                ResultSet resultSet = dbHandler.LoginUser(value);
                msg = CheckRepeat(resultSet, "one", msgArr[0]);

            } else if (msgArr[0].equals("dataReg")) {
                if (dbHandler.signUpUser(value) == true) {
                    msg = "RegSuccess";
                } else {
                    msg = "RegError";
                }
            } else if (msgArr[0].equals("getkey")) {
                ResultSet result = dbHandler.GetKeyUser(value);
                msg = sendMes(result, "Key");
            } else if (msgArr[0].equals("getUser")) {
                ResultSet result = dbHandler.GetUser(value);
                msg = ParseUserInfo(result);
            } else if (msgArr[0].equals("getUsersList")) {
                ResultSet resultSet = dbHandler.GetCountUser();
                try {
                    ArrayList<String> listUser = new ArrayList<>();
                    while (resultSet.next()) {
                        msg = resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + " " +
                                resultSet.getString(4) + " " + resultSet.getString(5) + " " + resultSet.getString(6) + " " +
                                resultSet.getString(7);
                        listUser.add(msg);
                    }
                    connection.sendString(packageRequest(listUser));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        connection.sendString(packageRequest(msg));
    }
    public String packageRequest(String msg){
        String path ="DataServer\\src\\resourse\\" + "dataServer.dat";
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))){
            inputOutputMessage testmess1 = new MessageFile("DataServer" ,msg);
            System.out.println(testmess1);
            oos.writeObject(testmess1);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public String packageRequest(ArrayList<String> list){
        String path ="DataServer\\src\\resourse\\" + "AllUsersList.dat";
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))){
            for(var msg : list) {
                inputOutputMessage testmess1 = new MessageFile("DataServer", msg);
                System.out.println(testmess1);
                oos.writeObject(testmess1);
                oos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
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
                connections.get(i).sendString(packageRequest(value));
        }

}

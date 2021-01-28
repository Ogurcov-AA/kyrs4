package server;


import ChatNetwork.*;

import java.io.*;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PrivateChatServ extends DialogsInfo implements ConnectionLis {

    public static void main(String[] args){   new PrivateChatServ(); }

    private final ArrayList<Connection> connections = new ArrayList<>();
//    DialogsInfo dialogs;

    private PrivateChatServ(){
      //  dialogs = new DialogsInfo();
        System.out.println("Server Run");
        try (ServerSocket serverSocket = new ServerSocket(8015)){
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
        connection.sendString(Base64.getEncoder().encodeToString("/yOuRloGin".getBytes()));
    }

    public String packageRequest(String msg){
        String path ="PrivateChatServ\\src\\server\\resourse\\" + "privMess.dat";
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))){
            inputOutputMessage testmess1 = new MessageFile("app" ,msg);
            System.out.println(testmess1);
            oos.writeObject(testmess1);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }


    @Override
    public synchronized void ReceiveString(Connection connection, String value, InputStream bytes) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File file = new File("PrivateChatServ\\src\\server\\resourse\\");
        file.mkdir();
        Seriazable seriazable = new Seriazable();
        List<String> log = new ArrayList<>(seriazable.saveDateFile("PrivateChatServ\\src\\server\\resourse\\" +value, bytes));
        DatabaseHandler databaseHandler = new DatabaseHandler();
       // byte[] actualByte = Base64.getMimeDecoder().decode(value);
        //value = new String(actualByte);
        for(var item:log) {
            System.out.println("in server : " + item);
            String msg;
            if (item.substring(0, item.indexOf(' ')).equals("getkeyDialog")) {
                //     msg = "/getPrivateMSGkey" + " " + dialogs.searchDialogKey(value.substring(value.indexOf(' ')+1));
                msg = "/getPrivateMSGkey" + " " + databaseHandler.searchDialogKey(item.substring(item.indexOf(' ') + 1));
                //  dialogs.searchDialogKey(value.substring(value.indexOf(' ')+1));
                //connection.sendString(Base64.getEncoder().encodeToString(msg.getBytes()));
                connection.sendString(packageRequest(msg));

            }
            if (item.substring(0, item.indexOf(' ')).equals("/mYlOGin")) {
                connections.add(connection);
                connection.Login = item.split(" ")[1];
                System.out.println(connection + " - connected");
            }
            if (item.substring(0, item.indexOf(' ')).equals("counterInFile")) {
                item = item.substring(item.indexOf(' ') + 1);
                ArrayList<String> temp = new ArrayList<>();
                //   temp.addAll(dialogs.DifferenceStr(Integer.parseInt(value.substring(0, value.indexOf(' '))), value.substring(value.indexOf(' ') + 1)));
                temp.addAll(databaseHandler.getArrayWithNewMsg(Integer.parseInt(item.substring(0, item.indexOf(' '))), item.substring(item.indexOf(' ') + 1)));
                if (temp.size() == 0) {
                    msg = "/pmLogToKeySuccess " + item.substring(item.indexOf(' ') + 1);
                    connection.sendString(packageRequest(msg));
                    return;
                }
                for (var t : temp) {
                    System.out.println(t);
                    msg = "/pmLogToKey " + item.substring(item.indexOf(' ') + 1) + " " + t;
                    System.out.println(msg);
                    connection.sendString(packageRequest(msg));
                }
            }
            if (item.substring(0, item.indexOf(' ')).equals("/pm")) {
                String[] parseValue = item.split(" ");
                String tempValue = item.substring(item.indexOf(' ') + 1);
                databaseHandler.addMessageToBD(tempValue);
                //   dialogs.AddMessageToFile(tempValue);
                String firstUser = "";
                String secondUser = "";
                ResultSet resultSet = databaseHandler.checkKey(tempValue);
                try {
                    if (resultSet.next()) {
                        firstUser = resultSet.getString("userOne");

                        secondUser = resultSet.getString("userTwo");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                for (var con : connections) {
                    if (con.Login.equals(firstUser) && !firstUser.equals(parseValue[2]) ||
                            con.Login.equals(secondUser) && !secondUser.equals(parseValue[2])) {
                        con.sendString(packageRequest(item));
                        break;
                    }
                }
            }
        }
    }












    @Override
    public synchronized void Disconnection(Connection connection) {
        connections.remove(connection);
        System.out.println(connection + " - disconnected");
    }

    @Override
    public synchronized void Exception(Connection connection, Exception e) {
        System.out.println("Exception: " + e);
    }
}


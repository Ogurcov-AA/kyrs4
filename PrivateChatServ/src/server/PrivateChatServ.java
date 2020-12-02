package server;


import ChatNetwork.Connection;
import ChatNetwork.ConnectionLis;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Base64;

public class PrivateChatServ extends DialogsInfo implements ConnectionLis {

    public static void main(String[] args){   new PrivateChatServ(); }

    private final ArrayList<Connection> connections = new ArrayList<>();
    DialogsInfo dialogs;

    private PrivateChatServ(){
        dialogs = new DialogsInfo();
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

    @Override
    public synchronized void ReceiveString(Connection connection, String value) {

        byte[] actualByte = Base64.getMimeDecoder().decode(value);
        value = new String(actualByte);
        System.out.println("in server : " + value);
        String msg;
        if(value.substring(0,value.indexOf(' ')).equals("getkeyDialog")){
            msg = "/getPrivateMSGkey" + " " + dialogs.searchDialogKey(value.substring(value.indexOf(' ')+1));
            connection.sendString(Base64.getEncoder().encodeToString(msg.getBytes()));

        }
        if(value.substring(0,value.indexOf(' ')).equals("/mYlOGin")){
            connections.add(connection);
            connection.Login = value.split(" ")[1];
            System.out.println(connection + " - connected");
        }
        if(value.substring(0,value.indexOf(' ')).equals("counterInFile")) {
            value = value.substring(value.indexOf(' ') + 1);
            ArrayList<String> temp = new ArrayList<>();
            temp.addAll(dialogs.DifferenceStr(Integer.parseInt(value.substring(0, value.indexOf(' '))), value.substring(value.indexOf(' ') + 1)));
           if(temp.size() == 0){
               msg = "/pmLogToKeySuccess " + value.substring(value.indexOf(' ') +1);
               connection.sendString(Base64.getEncoder().encodeToString(msg.getBytes()));
               return;
           }
            for (var t : temp){
                System.out.println(t);
                msg = "/pmLogToKey " + value.substring(value.indexOf(' ') +1) + " " + t;
                System.out.println(msg);
                connection.sendString(Base64.getEncoder().encodeToString(msg.getBytes()));
            }
        }
        if(value.substring(0,value.indexOf(' ')).equals("/pm")){
            String[] parseValue = value.split(" ");
            String tempValue = value.substring(value.indexOf(' ')+1);
            dialogs.AddMessageToFile(tempValue);
            String firstUser = "";
            String secondUser = "";
            for(var listDialogs : dialogs.DialogsName){
                if(listDialogs.key.equals(parseValue[1])){
                   firstUser = listDialogs.UserOne;
                   secondUser = listDialogs.UserTwo;
                }
            }
            for(var con : connections){
                if(con.Login.equals(firstUser) && !firstUser.equals(parseValue[2]) ||
                   con.Login.equals(secondUser) && !secondUser.equals(parseValue[2])){
                    con.sendString(Base64.getEncoder().encodeToString(value.getBytes()));
                    break;
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


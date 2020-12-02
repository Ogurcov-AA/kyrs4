package sample;

import ChatNetwork.Connection;
import ChatNetwork.ConnectionLis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class MessageHandling implements ConnectionLis {

    protected static final String IP_ADDR = "localhost"; //"172.20.10.12";
    protected static final int PORT = 8085;
    public ArrayList<String> logs = new ArrayList<>();

    public Connection connection;

    protected boolean CreateConnection(String login){
        try {
            connection = new Connection(this,IP_ADDR, PORT,login);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void SendMes(String msg){
        msg = Base64.getEncoder().encodeToString(msg.getBytes());
        connection.sendString(msg);
    }



    @Override
    public void ConnectionReady(Connection connection) {

    }

    @Override
    public void ReceiveString(Connection connection, String value) {
            byte[] actualByte = Base64.getMimeDecoder().decode(value);
            logs.add(new String(actualByte));

    }

    @Override
    public void Disconnection(Connection connection) {
        connection.disconnected();
        logs.add("Вы отключились");
    }

    @Override
    public void Exception(Connection connection, Exception e) {
        logs.add("Connection exception : " + e);
    }


}

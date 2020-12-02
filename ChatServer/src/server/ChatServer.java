package server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;

import ChatNetwork.Connection;
import ChatNetwork.ConnectionLis;

public class ChatServer implements ConnectionLis {
    public static void main(String[] args){
        new ChatServer();
    }

    private final ArrayList<Connection> connections = new ArrayList<>();
    private FileWriter writer;

    private ChatServer(){

        System.out.println("Server Run");
        try (ServerSocket serverSocket = new ServerSocket(8085)){
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
        System.out.println(connection + " - connected");
        SendToAllConnection(Base64.getEncoder().encodeToString((connection + " - подключился").getBytes()));
    }

    @Override
    public synchronized void ReceiveString(Connection connection, String value) {
        if (!value.equals("null")) {
                    SendToAllConnection(value);
                }
    }

    @Override
    public synchronized void Disconnection(Connection connection) {
        connections.remove(connection);
        System.out.println(connection + " - disconnected");
        SendToAllConnection((Base64.getEncoder().encodeToString((connection + " - отключился").getBytes())));
    }

    @Override
    public synchronized void Exception(Connection connection, Exception e) {
        System.out.println("Exception: " + e);
    }

    private void SendToAllConnection(String value){
        PublicMessFile(value);
        CheckAliveConnection();
        if(connections.size()==0) return;
            for (int i = 0; i < connections.size(); i++) {
                connections.get(i).sendString(value);
            }
    }

    private void PublicMessFile(String value){
        LocalDateTime currentDate = LocalDateTime.now();
        try {
            writer = new FileWriter("F:\\Education\\sem4\\kyrs4\\ChatServer\\src\\server\\log\\PublicChatLog.txt", true);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            bufferWriter.write(  "[" + currentDate + "] " +value + System.getProperty("line.separator"));
            bufferWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CheckAliveConnection(){
        for (int i = 0;i<connections.size();i++){
            if(connections.get(i).CheckAliveConnect()==true){
                System.out.println("+");
                connections.remove(connections.get(i));
            }
        }
    }

}


package server;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.*;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import ChatNetwork.*;

public class ChatServer implements ConnectionLis {
    public static void main(String[] args){
        new ChatServer();
    }

    private final ArrayList<Connection> connections = new ArrayList<>();
    private FileWriter writer;

    private ChatServer(){

        System.out.println("Server Run");
        try (ServerSocket serverSocket = new ServerSocket(8095)){
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
      //  SendToAllConnection(Base64.getEncoder().encodeToString((connection + " - подключился").getBytes()));
        SendToAllConnection(connection + " - подключился");
    }

    @Override
    public synchronized void ReceiveString(Connection connection, String value, InputStream bytes) {
        try {
            Thread.sleep(100);
        System.out.println(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Seriazable seriazable = new Seriazable();
        File file = new File("ChatServer\\src\\server\\resourse\\");
        file.mkdir();
        List<String> log = new ArrayList<>(seriazable.saveDateFile("ChatServer\\src\\server\\resourse\\"+value, bytes));
        for(int item = 0; item < log.size();item++) {
           if (!log.get(item).equals("null")) {
                SendToAllConnection(log.get(item));
            }
            log.remove(item);
            item--;

        }
    }

    @Override
    public synchronized void Disconnection(Connection connection) {
        connections.remove(connection);
        System.out.println(connection + " - disconnected");
      //  SendToAllConnection((Base64.getEncoder().encodeToString((connection + " - отключился").getBytes())));
        SendToAllConnection(connection + " - отключился");
    }

    @Override
    public synchronized void Exception(Connection connection, Exception e) {
        System.out.println("Exception: " + e);
    }

    public String packageRequest(String msg){
        File file = new File("ChatServer\\src\\server\\resourse\\");
        file.mkdir();

        String path ="ChatServer\\src\\server\\resourse\\" + "ChatServer.dat";
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

    private void SendToAllConnection(String value){
        PublicMessFile(value);
        CheckAliveConnection();
        if(connections.size()==0) return;
            for (int i = 0; i < connections.size(); i++) {
                connections.get(i).sendString(packageRequest(value));
            }
    }

    private void PublicMessFile(String value){
        LocalDateTime currentDate = LocalDateTime.now();
        try {
            writer = new FileWriter("ChatServer\\src\\server\\log\\PublicChatLog.txt", true);
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


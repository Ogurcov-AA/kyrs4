package sample;

import ChatNetwork.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;

public class MessageHandling implements ConnectionLis {

    protected static final String IP_ADDR = "localhost"; //"172.20.10.12";
    protected static final int PORT = 8095;
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
    public String packageRequest(String msg){
        String path ="src\\resourse\\" + "SendPublickmess.dat";
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))){
            inputOutputMessage testmess1 = new MessageFile("App" ,msg);
            System.out.println(testmess1);
            oos.writeObject(testmess1);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public void SendMes(String msg){
     //   msg = Base64.getEncoder().encodeToString(msg.getBytes());
        connection.sendString(packageRequest(msg));
    }



    @Override
    public void ConnectionReady(Connection connection) {

    }

    @Override
    public void ReceiveString(Connection connection, String value, InputStream bytes) {
        Seriazable seriazable = new Seriazable();
        ArrayList<String> templog = new ArrayList<>(seriazable.saveDateFile("src\\resourse\\"+value,bytes));
        logs.addAll(templog);
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

package sample;

import ChatNetwork.Connection;
import ChatNetwork.ConnectionLis;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Timer;

public class PrivateMessage implements ConnectionLis {

    protected static final String IP_ADDR = "localhost"; //"172.20.10.12";
    protected static final int PORT = 8015;
    public Connection connection;
    private String Currentkey ;
    public String GetCurrentkey(){
        return Currentkey;
    }
    public ArrayList<String> logs  = new ArrayList<>();
    private final String pathToLog = "F:\\Education\\sem4\\kyrs4\\src\\cache\\";


    public PrivateMessage(){
    }

    protected boolean CreateConnection(){
        try {
            connection = new Connection(this,IP_ADDR, PORT);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void CheckDialogKey(String msg){
        Currentkey = msg;
        File dir = new File("F:\\Education\\sem4\\kyrs4\\src\\cache");
        File[] listDir = dir.listFiles();
        int strCount = -1;
        for (var s : listDir){
            if(s.isFile() && s.getName().equals(msg + ".txt")){
            strCount = CountLogStr();
            }
        }
        if(strCount==-1){
            File newFile = new File("F:\\Education\\sem4\\kyrs4\\src\\cache" + "\\" + msg + ".txt");
            try {
                newFile.createNewFile();
                strCount = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SendCountStr(strCount);
    //    CheckInputMsg(true);
    }

    private int CountLogStr(){
        int  strCount = 0;
        try {
            BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(pathToLog+Currentkey+".txt")));
            while(buff.readLine()!=null){
                strCount++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) { }
        return strCount;
    }

    public void SendLoginToGetKey(String CurrentUserLogin, String Login){
        String msg = "getkeyDialog " + CurrentUserLogin + " " + Login;
        msg = Base64.getEncoder().encodeToString(msg.getBytes());
        connection.sendString(msg);
    }

    private void SendCountStr(int counter){
        String msg = "counterInFile " + counter + " " + Currentkey;
        msg = Base64.getEncoder().encodeToString(msg.getBytes());
        connection.sendString(msg);
    }

    public Timer timerS;
   /*
    public void CheckInputMsg(boolean flag) {
      timerS =new java.util.Timer();
        Runnable task = () -> {
            timerS.schedule(new TimerTask() {
                public void run() {
                    if(flag == false){
                        return;
                    }
                    SendCountStr(CountLogStr());
                }
            }, 15000, 15000);
        };
        Thread myThread = new Thread(task, "timer");
        myThread.start();
    }

    */


    public void InputLogFile(String msg){
        String key = msg.substring(0,msg.indexOf(' '));
        msg = msg.substring(msg.indexOf(' ')+1);
        try {
            BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathToLog + key + ".txt",true)));
            msg = Base64.getEncoder().encodeToString(msg.getBytes());
            buff.write(msg + System.getProperty("line.separator"));
            buff.flush();
            buff.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void InputArrayWithFile(ArrayList<String> temp) {
     temp.clear();
        try {
            BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(pathToLog + Currentkey + ".txt")));
            String line;
            while ((line = buff.readLine()) != null) {
                line = new String(Base64.getMimeDecoder().decode(line.getBytes()));
                temp.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            }
    }

    public void newMessage(String msg){
        InputLogFile(msg.substring(msg.indexOf(' ')+1));

    }


    public void SendMes(String msg){
        msg = "/pm " + Currentkey + " " + msg;
        InputLogFile(msg.substring(msg.indexOf(' ')+1));
        msg = Base64.getEncoder().encodeToString(msg.getBytes());
        connection.sendString(msg);
        SendCountStr(CountLogStr());
    }
    public void sendMyLogin(String msg){
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
        System.out.println("Disconnected");
    }

    @Override
    public void Exception(Connection connection, Exception e) {

    }
}

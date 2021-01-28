package ChatNetwork;

import ChatNetwork.ConnectionLis;

import java.io.*;
import java.net.Socket;

public class    Connection {
    private final Socket socket;
    private final Thread nextThread;
    DataOutputStream outFileName;
    InputStream in;
    OutputStream out;


    public String Login;

    private ConnectionLis eventList;

    public Connection(ConnectionLis eventList, String ipAddr, int port) throws IOException{
        this(eventList, new Socket(ipAddr,port));
    }
    public Connection(ConnectionLis eventList, String ipAddr, int port, String login) throws IOException{
        this(eventList, new Socket(ipAddr,port));
    }

    public Connection(ConnectionLis eventList, Socket socket) throws IOException {
        this.socket = socket;
        outFileName  = new DataOutputStream(socket.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
        nextThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventList.ConnectionReady(Connection.this);
                    while (!nextThread.isInterrupted()){
                        DataInputStream fileNameInfo = new DataInputStream(socket.getInputStream());
                        eventList.ReceiveString(Connection.this,fileNameInfo.readUTF(),in);
                       // fileNameInfo.close();
                    }
                }catch (IOException e){
                    e.getMessage();
                }
                finally {
                    eventList.Disconnection(Connection.this);
                    return;
                }
            }
        });
        nextThread.start();
    }


    public synchronized void sendString (String msg) {
        try {
            File file = new File(msg);
            outFileName.writeUTF(file.getName());
            FileInputStream fis = new FileInputStream(file);
            byte[] bt = new byte[(int) file.length()];
            while ((fis.read(bt)) > 0) {
                out.write(bt);
                out.flush();
                if(fis.available()==0) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            //  disconnected();
        }
    }

    public synchronized void disconnected(){
        nextThread.interrupt();
        try{
            socket.close();
        }
        catch (IOException e){
            eventList.Exception(Connection.this, e);
        }
    }

    public String OutputAdr(){
        return String.valueOf(socket.getInetAddress());
    }

    public boolean CheckAliveConnect(){
        return socket.isClosed();
    }


    @Override
    public String toString(){
        return "Пользователь " + Login + socket.getInetAddress() ;
    }

}

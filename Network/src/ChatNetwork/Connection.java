package ChatNetwork;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class    Connection {
        private final Socket socket;
        private final Thread nextThread;
        private final BufferedReader in;
        private final BufferedWriter out;
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
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));

            nextThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                       eventList.ConnectionReady(Connection.this);
                        while (!nextThread.isInterrupted()){
                            eventList.ReceiveString(Connection.this, in.readLine());
                        }
                    }
                    catch (IOException e){

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
                out.write(msg + "\n");
                System.out.println("SystemLog (send): " + msg);
                out.flush();

            } catch (IOException e) {
                eventList.Exception(Connection.this, e);
                disconnected();
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

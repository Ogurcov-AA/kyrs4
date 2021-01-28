package sample;

import ChatNetwork.*;
import javafx.application.Platform;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static sample.LoginWin.showErrorMessage;

public class InputOutputData implements ConnectionLis {

    protected static final String IP_ADDR = "localhost"; // "172.20.10.12";
    protected static final int PORT = 8163;
    protected static ArrayList<String> log = new ArrayList<>();
    protected static boolean flag = false;
    protected static String currentKey = null;

    private Connection connection;

    private boolean CreateConnection(){
        try {
            connection = new Connection(this,IP_ADDR, PORT);
        return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String packageRequest(String msg){
     //   File file = new File("temp");
    //    file.mkdir();
        String path ="temp\\" + "InputOutput.dat";
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))){
            inputOutputMessage testmess1 = new MessageFile("DataInfo" ,msg);
            System.out.println(testmess1);
            oos.writeObject(testmess1);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    protected void GetKeySendConnection(String login){
        if(!CreateConnection()){
            showErrorMessage("?????? ?????????? ? ????? ??????", "?????????? ? ????? ?????? ?? ????????????," + "\n\r" +
                    "?????????? ?????");
            return;
        }  else {
            login = "getkey " + login;
            connection.sendString(packageRequest(login));
        }
    }

    Thread myThread;
    public synchronized boolean TimerLogin(String value){
        flag = false;
        Timer timer = new Timer();

        Runnable tt = () -> {
            timer.schedule(new TimerTask() {
                public void run() {
                    if (log.size() != 0) {
                        if (value.equals("login")) {
                            LoginUser();
                            myThread.interrupt();
                            timer.cancel();
                            return;
                        }
                        if (value.equals("validLogin")) {
                            CheckRepeatL();
                            myThread.interrupt();
                            timer.cancel();
                            return;
                        }
                        if (value.equals("validEmail")) {
                            CheckRepeatE();

                            myThread.interrupt();
                            timer.cancel();
                            return;
                        }
                        if (value.equals("getkey")) {
                            CheckKey();

                            myThread.interrupt();
                            timer.cancel();
                            return;
                        }
                        if (value.equals("checkLogin")) {
                            CheckLogin();

                            myThread.interrupt();
                            timer.cancel();
                            return;
                        }
                    }
                }
            }, 0, 100);
        };
            myThread = new Thread(tt, "timer");
        myThread.start();
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return flag;
    }

    private void CheckLogin() {
        for(int i =0;i<log.size();i++) {
            if (log.get(i).equals("Success")) {
                System.out.println("Success");
                log.remove(log.get(i));
                flag = true;
                return;
            }
            else{
                System.out.println("Error");
                log.remove(log.get(i));
                flag = false;
                return;
            }
        }
    }

    protected boolean GetUserInfo(String login){
        log.clear();
        login = "getUser " + login;
        connection.sendString(packageRequest(login));
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        if(log.size() > 0){
                            timer.cancel();
                            return;
                        }
                    }
                });
            }
        }, 10,200);
        try {
            Thread.currentThread().join(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i =0;i<log.size();i++){
            if(log.get(i).equals("null")){
                log.remove(log.get(i));
                return false;
            }
            else if(log.get(i).substring(0, log.get(i).indexOf(" ")+1).equals("user "))
            {
            //-    System.out.println(log.get(i).substring(log.get(i).indexOf(" ")+1,log.get(i).length()));
                setUserInfo(log.get(i).substring(log.get(i).indexOf(" ")+1,log.get(i).length()));
                log.remove(log.get(i));
                return true;
            }
        }
        return false;
    }


    private void setUserInfo(String user){
        String msg[] = user.split(" ");
        UserInfo.currentUser.setId(Integer.parseInt(msg[0]));
        UserInfo.currentUser.setLogin(msg[1]);
        UserInfo.currentUser.setName(msg[2]);
        UserInfo.currentUser.setSurname(msg[3]);
        UserInfo.currentUser.setIp(msg[4]);
        UserInfo.currentUser.setCountry(msg[5]);
        UserInfo.currentUser.setMail(msg[6]);
        UserInfo.currentUser.setbDay(msg[7]);
        UserInfo.currentUser.setRegDay(msg[8]);
    }

    protected boolean LoginUserSendConnection(String login, String pass){
            login = "dataSign " + login + " " + pass;
            connection.sendString(packageRequest(login));
    //    }
        TimerLogin("checkLogin");
        return flag;
    }


    protected void LoginUser(){
        for(int i =0;i<log.size();i++){
          if(log.get(i).equals("Success"))
          {
                System.out.println("Success");
                log.remove(log.get(i));
                return;
          }
          if(log.get(i).equals("KeyError")) {
              showErrorMessage("?????? ?????", "???????? ????? ??? ??????");
              log.remove(log.get(i));
              return;
          }
        }
    }


    public void CheckKey() {
        for (int i = 0; i < log.size(); i++) {
            if (log.get(i).equals("KeyError") || log.get(i).equals("Error")) {
                log.remove(log.get(i));
                flag = false;
            }
            else {
                currentKey = log.get(i);
                System.out.println(currentKey);
                log.remove(log.get(i));
            }
        }
        flag = true;
    }


    public void CheckRepeatL() {
        for (int i = 0; i < log.size(); i++) {
            if (log.get(i).equals("loginError")) {
                log.remove(log.get(i));
                    flag = false;
            } else if (log.get(i).equals("loginSuccess")) {
                log.remove(log.get(i));
                    flag = true;
            }
        }
    }


    public void CheckRepeatE() {
        for (int i = 0; i < log.size(); i++) {
            if (log.get(i).equals("mailError")) {
                log.remove(log.get(i));
                flag = false;
            } else if (log.get(i).equals("mailSuccess")) {
                log.remove(log.get(i));
                flag = true;
            }
        }
    }


    protected boolean OutputDate(String msgToOut){
        if(!CreateConnection()){
            showErrorMessage("Ошибка соединения с бд", "Повторите попытку позже");
       return false;
        }
        else{
            if(!msgToOut.isEmpty()) msgToOut = "dataReg " + msgToOut;
            msgToOut += " " + connection.OutputAdr();
            connection.sendString(packageRequest(msgToOut));
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    Platform.runLater(new Runnable() {
                        public void run() {
                       if(log.size() > 0){
                           timer.cancel();
                           return;
                       }
                        }
                    });
                }
            }, 10,500);
            try {
                Thread.currentThread().join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i =0;i<log.size();i++){
                if(log.get(i).equals("RegError")){
                    log.remove(log.get(i));
                return false;
                }
                else if(log.get(i).equals("RegSuccess"))
                {
                    log.remove(log.get(i));
                    return true;
                }
            }
        }
        return false;
    }



    public void RepeatFieldSendConnection(String value) {
        if (!CreateConnection()) {
            showErrorMessage("?????? ?????????? ? ????? ??????", "?????????? ? ????? ?????? ?? ????????????," + "\n\r" +
                    "?????????? ?????");
            return;
        } else {
            connection.sendString(packageRequest(value));
        }
    }


    @Override
    public void ConnectionReady(Connection connection) {

    }

    @Override
    public void ReceiveString(Connection connection, String value, InputStream bytes) {
        try {
            Thread.sleep(100);
            System.out.println("===----+ " + bytes.available());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Seriazable seriazable = new Seriazable();
        ArrayList<String> templog = new ArrayList<>(seriazable.saveDateFile("src\\"+value,bytes));
        log.addAll(templog);
        for(var item:log)
        System.out.println("In prgogramm  " +log.size()+" : " + item);
         }

    @Override
    public void Disconnection(Connection connection) {

    }

    @Override
    public void Exception(Connection connection, Exception e) {
    }


    public void NextThreadToGetUserList(){
        Runnable task = () -> {
            GetAllUserList();
        };
        Thread nextThread = new Thread(task,"getUser");
        nextThread.start();
    }


    public void ParseStrToUserInfo(){
        for(int i=0; i< Users.size();i++) {
            UserInfo tempUser = new UserInfo();
            String[] tempInfo = Users.get(i).split(" ");
            tempUser.setLogin(tempInfo[0]);
            tempUser.setName(tempInfo[1]);
            tempUser.setSurname(tempInfo[2]);
            tempUser.setIp(tempInfo[3]);
            tempUser.setMail(tempInfo[4]);
            tempUser.setbDay(tempInfo[5]);
            tempUser.setCountry(tempInfo[6]);
            UserInfo.UserList.add(tempUser);
        }
    }

    public void GetAllUserList(){
        try {
            connection.sendString(packageRequest("getUsersList"));
            Thread.sleep(500);
            for (int i = 0; i < log.size(); i++) {
                log.remove(log.get(i));
                ParseStrToUserInfo();
                connection.disconnected();
                Users.add(log.get(i));
                log.remove(log.get(i));
                i--;
            }
        }
        catch (Exception e ){
            e.getMessage();
        }
    }
        ArrayList<String> Users = new ArrayList<>();

}

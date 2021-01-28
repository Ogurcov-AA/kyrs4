package server;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

public class DialogsInfo {

    protected final ArrayList<DialogsInfo> DialogsName = new ArrayList<>();
    String key;
    String UserOne;
    String UserTwo;
    int countStr;

  private final String pathToPairLogin = "F:\\Education\\sem4\\kyrs4\\PrivateChatServ\\src\\server\\Properties\\PairDialogs";
  private final String dialogPath = "F:\\Education\\sem4\\kyrs4\\PrivateChatServ\\src\\server\\Dialogs\\";

    public DialogsInfo(){
        GetDialName();
    }

    public DialogsInfo(String key,String UserOne, String UserTwo,int countStr){
        this.key = key;
        this.UserOne = UserOne;
        this.UserTwo = UserTwo;
        this.countStr = countStr;
    }

    private void GetDialName() {
        try {
            BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(pathToPairLogin)));
            String line;
            while ((line = buff.readLine())!=null){
               line = new String(Base64.getMimeDecoder().decode(line.getBytes()));
                String[] str = line.split(" ");
                DialogsInfo temp = new DialogsInfo(str[0], str[1],str[2], GetCountOfStr(str[0]));
                DialogsName.add(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int GetCountOfStr(String path){
        int count=0;
        try {
        BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(dialogPath+path+".txt")));
            while ((buff.readLine())!=null){
                count++;
            }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        return count;
    }

    private int GenerateKey(){
        int min = 100000;
        int max = 999999;
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    public String searchDialogKey(String msg){
      System.out.println(msg);
        for(var s : DialogsName){
            if((msg.substring(0,msg.indexOf(' ')).equals(s.UserOne) && msg.substring(msg.indexOf(' ')+1).equals(s.UserTwo))||
                (msg.substring(0,msg.indexOf(' ')).equals(s.UserTwo) && msg.substring(msg.indexOf(' ')+1).equals(s.UserOne))){
                return s.key;
            }
        }
        String key = String.valueOf(GenerateKey());
        DialogsInfo newDialog = new DialogsInfo(key,msg.substring(0,msg.indexOf(' ')), msg.substring(msg.indexOf(' ')+1),0);
        File temp = new File(dialogPath + key + ".txt");
        try {
            temp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DialogsName.add(newDialog);
        SetKeyToFile(newDialog);
        return key;
    }

    private void SetKeyToFile(DialogsInfo dialog) {
        try {
            BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathToPairLogin,true)));
            buff.write(dialog.toString()+System.getProperty("line.separator"));
            buff.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public boolean AddMessageToFile(String str) {
        System.out.println(str);
        String key = str.substring(0,str.indexOf(' '));
        str = str.substring(str.indexOf(' ')+1);
        try {
            BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dialogPath + key + ".txt",true)));
            buff.write(Base64.getEncoder().encodeToString((str).getBytes())+System.getProperty("line.separator"));
            buff.close();
            for (var t : DialogsName){
                if(t.key.equals(key)){
                    t.countStr++;
                }
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

        @Override
        public String toString(){
        return Base64.getEncoder().encodeToString((key + " " + UserOne + " " + UserTwo).getBytes());
    }


    public ArrayList<String> DifferenceStr(int count, String key){
     ArrayList<String> temp = new ArrayList<>();
        for (var s : DialogsName){
        if(s.key.equals(key)){
                if(count==s.countStr)
                    System.out.println("OK " + s.countStr);
            if(count<s.countStr){
                try {
                int ttt = 0;
                    BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(dialogPath+key+".txt")));
                    String line;
                    while ((line = buff.readLine())!=null){
                        if(ttt==count){
                            line = new String(Base64.getDecoder().decode(line));
                            System.out.println(line);
                            temp.add(line);
                        }
                        else
                        ttt++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
     }
        return temp;
    }



}

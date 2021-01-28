package ChatNetwork;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Seriazable {

    public List<String> saveDateFile(String path, InputStream bytes) {
      //  File dir = new File(path);
       // dir.mkdir();
        //String path = "server\\src\\resourse\\" + str;
        try {
            System.out.println(bytes.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fOut = new FileOutputStream(path, false);
            byte[] bt;
            int countByte = bytes.available();
            if (bytes.available() < 1024) {
                bt = new byte[bytes.available()];
            } else {
                bt = new byte[1024];
            }
            while ((bytes.read(bt)) > 0) {
                countByte = countByte - bt.length;
                fOut.write(bt);
                fOut.flush();
                if (countByte > 0 && countByte < 1024) {
                    bt = new byte[countByte];
                }
                if (countByte == 0) break;
            }
            fOut.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
       return deserializer(path);
    }

    public List<String> deserializer(String path){
       List<String> log = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            boolean readObjectFlag = true;
            while(readObjectFlag) {
                try {
                    inputOutputMessage inputOutputMessage = (inputOutputMessage) objectInputStream.readObject();
                    switch (inputOutputMessage.getMessageType()) {
                        case MESSAGE : {
                            MessageFile messageFile = (MessageFile) inputOutputMessage;
                         //   String msg = new String(Base64.getMimeDecoder().decode(messageFile.getMessage()));
                                String msg = messageFile.getMessage();
                            System.out.println(msg);
                            log.add(msg);
                            break;
                        }
                        case IMAGE : {
                            ImageFile imageFile = (ImageFile) inputOutputMessage;
                            File file = new File(path  + imageFile.getFileName());
                            file.createNewFile();
                            OutputStream outputStream = new FileOutputStream(file);
                            outputStream.write(imageFile.bytes);
                            outputStream.flush();
                            outputStream.close();
                            log.add(path +"\\resourse\\" + imageFile.getFileName());
                            break;
                        }
                    }
                }
                catch (Exception ignored){
                    readObjectFlag=false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
            return log;
    }

}

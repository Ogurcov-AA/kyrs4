package ChatNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

public class ImageFile extends ChatNetwork.inputOutputMessage {

       private transient File file;
       private String fileName;
       public byte[] bytes;


       public ImageFile(String messageHeader, String filePath) {
           super(messageHeader);
           setMessageType(messageTypeEnum.IMAGE);
           file = new File(filePath);
           try {
               fileName = file.getName();
               InputStream in = new FileInputStream(file);
               bytes = new byte[in.available()];
               in.read(bytes);
           } catch (IOException e) {
               e.printStackTrace();
           }
           setSizeMessage(bytes.length);
           try {
               setHashMessage(ChatNetwork.CRCcheck.checkCrc(file.getPath()));
           } catch (NoSuchAlgorithmException | IOException e) {
               e.printStackTrace();
           }
       }


    @Override
    public String toString(){
            return super.getHashMessage() + " " + super.getMessageHeader() + " " + super.getMessageDate() + " " + super.getMessageType() + " " + super.getSizeMessage() + " " + file.getName();
        }

    public String getFileName() {
        return fileName;
    }

}

package ChatNetwork;

import java.io.Serializable;
import java.util.Base64;

public class MessageFile extends ChatNetwork.inputOutputMessage implements Serializable {

    private String message;

    public MessageFile(String messageHeader, String mess) {
        super(messageHeader);
        setHashMessage(String.valueOf(mess.hashCode()));
        setSizeMessage(mess.length());
        setMessageType(messageTypeEnum.MESSAGE);
     //   this.message = Base64.getEncoder().encodeToString(mess.getBytes());
            this.message = mess;
    }


    public String getMessage() {
        return message;
    }

    @Override
    public String toString(){
        return super.getHashMessage() + " " + super.getMessageHeader() + " " + super.getMessageDate() + " " + super.getMessageType() + " " + super.getSizeMessage() + " " + message;
    }
}

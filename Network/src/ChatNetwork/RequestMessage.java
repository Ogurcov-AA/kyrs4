package ChatNetwork;

import java.io.Serializable;
import java.util.Base64;

public class RequestMessage extends ChatNetwork.inputOutputMessage implements Serializable {

    private String message;

    public RequestMessage(String messageHeader, String mess) {
        super(messageHeader);
        setHashMessage(String.valueOf(mess.hashCode()));
        setSizeMessage(mess.length());
        setMessageType(messageTypeEnum.REQUEST);
        this.message = Base64.getEncoder().encodeToString(mess.getBytes());

    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString(){
        return super.getHashMessage() + " " + super.getMessageHeader() + " " + super.getMessageDate() + " " + super.getMessageType() + " " + super.getSizeMessage() + " " + message;
    }
}

package ChatNetwork;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class inputOutputMessage implements Serializable {
    private String messageHeader;
    private String hashMessage;
    private Integer sizeMessage;
    private String messageDate;
    private messageTypeEnum messageType;


    protected inputOutputMessage(String messageHeader) {
        this.messageHeader = messageHeader;
        Date date = new Date();
        DateFormat tt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        this.messageDate = tt.format(date);

    }

    public String getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(String messageHeader) {
        this.messageHeader = messageHeader;
    }

    public String getHashMessage() {
        return hashMessage;
    }

    public void setHashMessage(String hashMessage) {
        this.hashMessage = hashMessage;
    }

    public Integer getSizeMessage() {
        return sizeMessage;
    }

    public void setSizeMessage(Integer sizeMessage) {
        this.sizeMessage = sizeMessage;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public messageTypeEnum getMessageType() {
        return messageType;
    }

    public void setMessageType(messageTypeEnum messageType) {
    this.messageType = messageType;
    }
}

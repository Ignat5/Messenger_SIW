package com.example.messenger_siw.Model;

public class Chat {
    private String senderID;
    private String receiverID;
    private String message;
    private boolean isSeen;

    public Chat() {}

    public Chat(String senderID, String receiverID, String message,boolean isSeen) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.isSeen = isSeen;
    }




    public String getSenderID() {
        return senderID;
    }
    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }
    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public boolean getIsSeen() {
        return isSeen;
    }
}

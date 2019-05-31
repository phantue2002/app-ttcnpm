package com.ttcnpm.nuntius;

public class ModelChat {
    String message, receiver, sender, time;
    boolean isSeen;

    public ModelChat(){

    }
    public ModelChat(String message, String receiver, String sender, String time, boolean isSeen){

        this.message=message;
        this.receiver=receiver;
        this.sender=sender;
        this.time=time;
        this.isSeen= isSeen;
    }

    public String getMessage(){
        return message;
    }
    public String getReceiver(){
        return receiver;
    }
    public String getSender(){
        return sender;
    }
    public String getTime(){
        return time;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

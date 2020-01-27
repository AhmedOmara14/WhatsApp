package com.example.what.chat_send_message;

public class Messages {

    String Sender,Receiver,Message;
    String date,type,time,name_document;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName_document() {
        return name_document;
    }

    public Messages(String sender, String receiver, String message, String date, String type, String time, String name_document) {
        Sender = sender;
        Receiver = receiver;
        Message = message;
        this.date = date;
        this.type = type;
        this.time = time;
        this.name_document = name_document;
    }

    public void setName_document(String name_document) {
        this.name_document = name_document;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Messages() {
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}

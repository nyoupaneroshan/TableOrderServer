package com.example.tableorderserver.Module;



public class Sender {
    public String to;
    public Notification noftification;

    public Sender(String to, Notification noftification) {
        this.to = to;
        this.noftification = noftification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNoftification() {
        return noftification;
    }

    public void setNoftification(Notification noftification) {
        this.noftification = noftification;
    }
}

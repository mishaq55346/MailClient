package com.example.mailclient;

import androidx.annotation.NonNull;

public class Letter {
    String sender;
    String text;
    String subject;

    public Letter() {}
    public Letter(String sender, String text, String subject) {
        this.sender = sender;
        this.text = text;
        this.subject = subject;
    }

    @NonNull
    @Override
    public String toString() {
        String separator = "\n---------------\n";
        //return separator + sender + ": [" + subject + "] - " + text + separator;
        return separator + sender + separator;
    }
}

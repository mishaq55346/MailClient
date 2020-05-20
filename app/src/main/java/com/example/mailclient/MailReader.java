package com.example.mailclient;

import android.os.Build;
import android.util.Log;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;

public class MailReader {
    final String TAG = "Mail";
    public boolean successConnect = true;

    final int LETTERSONPAGE = 10;
    enum MailType{YANDEX, MAILRU, GMAIL}

    private Properties properties;
    private MailType mailType;
    private String LOGIN;
    private String PASS;
    private Store store;
    private Folder emailFolder;

    public MailReader(MailType mailType, String LOGIN, String PASS) {
        String host = "";
        this.mailType = mailType;
        this.LOGIN = LOGIN;
        this.PASS = PASS;
        if (mailType == MailType.YANDEX){
            host = "imap.yandex.ru";
            properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");
            properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        else if (mailType == MailType.MAILRU){
            host = "imap.mail.ru";
            properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");
            properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        else if (mailType == MailType.GMAIL){
            host = "imap.gmail.com";
            properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");
            properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        Session emailSession = Session.getDefaultInstance(properties);
        try {
            store = emailSession.getStore("imap");
            store.connect(host, LOGIN, PASS);
            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
        } catch (MessagingException e) {
            successConnect = false;
        }
    }
    public ArrayList<Letter> getLetters(int page){
        ArrayList<Letter> letters = new ArrayList<>();
        Message[] messages = new Message[0];
        try {
            messages = emailFolder.getMessages();
            for (int i = messages.length - page * LETTERSONPAGE - 1; i > messages.length - page * LETTERSONPAGE - LETTERSONPAGE; i--) {
                Letter letter = new Letter();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    letter.sender = decode(messages[i].getFrom()[0].toString());
                }
                Message mes = messages[i];
                letter.text = "";
                if (mes.getContentType().contains("text/")) {
                    letter.text = Jsoup.parse(mes.getContent().toString()).text();
                }
                if (mes.getContentType().contains("multipart/mixed") || mes.getContentType().contains("multipart/alternative")) {
                    Multipart multipart = (Multipart) mes.getContent();
                    int count = multipart.getCount();
                    for (int j = 0; j < count; j++) {
                        BodyPart part = multipart.getBodyPart(j);
                        if (part.isMimeType("text/plain")) {
                            letter.text = Jsoup.parse(part.getContent().toString()).text();
                        }
                        else if (part.isMimeType("text/html")) {
                            letter.text = Jsoup.parse(part.getContent().toString()).text();
                        }
                        else if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            String fileName = MimeUtility.decodeText(part.getFileName());
                            letter.text += " file " + fileName + ";";
                        }
                    }
                }
                letter.subject = messages[i].getSubject();
                letters.add(letter);
            }
        } catch (MessagingException | IOException e) {
            Log.d("Mail", e.toString());
        }
        return letters;
    }

    private String decode(String s) {
        final String ENCODED_PART_REGEX_PATTERN="=\\?([^?]+)\\?([^?]+)\\?([^?]+)\\?=";
        Pattern pattern= Pattern.compile(ENCODED_PART_REGEX_PATTERN);
        Matcher m=pattern.matcher(s);
        ArrayList<String> encodedParts=new ArrayList<String>();
        while(m.find())
            encodedParts.add(m.group(0));
        if(encodedParts.size()>0) {
            try {
                for(String encoded:encodedParts)
                    s=s.replace(encoded, MimeUtility.decodeText(encoded));
                return s;
            } catch(Exception ex) {return s;}
        } else
            return s;

    }
    public void closeConnection(){
        try {
            emailFolder.close(false);
            store.close();
        } catch (MessagingException e) {
            Log.d("Mail", e.toString());
        }
    }
}
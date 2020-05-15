package com.example.mailclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail extends AppCompatActivity {
    String LOGIN;
    String PASS;
    EditText to;
    EditText subject;
    EditText text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);
        Intent intent = getIntent();
        LOGIN = intent.getStringExtra("LOGIN");
        PASS = intent.getStringExtra("PASS");
        to = findViewById(R.id.send_email);
        subject = findViewById(R.id.send_subject);
        text = findViewById(R.id.send_text);
    }

    public void clickButton(View view){
        MailSender sender = new MailSender();
        sender.execute();
    }
    class MailSender extends AsyncTask{
        String subj;
        String body;
        String recepient;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            subj = subject.getText().toString();
            body = text.getText().toString();
            recepient = to.getText().toString();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            sendMail(subj, body, LOGIN, recepient, "");
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            finish();
        }
        public synchronized void sendMail(String subject, String body, String sender, String recipients, String filename) {
            try {
                Properties properties = new Properties();
                //Хост или IP-адрес почтового сервера
                if (sender.contains("@yandex.ru") || sender.contains("@ya.ru")){
                    properties.put("mail.smtp.host", "smtp.yandex.ru");
                }
                else if (sender.contains("@mail.ru")){
                    properties.put("mail.smtp.host", "smtp.mail.ru");
                }
                else if (sender.contains("@gmail.com")){
                    properties.put("mail.smtp.host", "smtp.gmail.com");
                }
                //Требуется ли аутентификация для отправки сообщения
                properties.put("mail.smtp.auth", "true");
                //Порт для установки соединения
                properties.put("mail.smtp.socketFactory.port", "465");
                //Фабрика сокетов, так как при отправке сообщения Yandex требует SSL-соединения
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                Session session = Session.getInstance(properties,
                        //Аутентификатор - объект, который передает логин и пароль
                        new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(LOGIN, PASS);
                            }
                        });

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender));
                message.setSubject(subject);
                if (recipients.indexOf(',') > 0)
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(recipients));
                else
                    message.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(recipients));
                message.setText(body);
                Transport.send(message);
            } catch (Exception e) {
                Log.d("Mail", e.toString());
            }
        }
    }
}

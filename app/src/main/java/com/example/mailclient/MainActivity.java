package com.example.mailclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {
    final String TAG = "Mail";
    static ArrayList<Letter> letters;
    String LOGIN = "misha.kolmykovqq1@yandex.ru";
    String PASS = "mishaq1";
    EditText edit_login;
    EditText edit_password;
    static MailReader reader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_login = findViewById(R.id.edit_log);
        edit_password = findViewById(R.id.edit_pass);
    }

    public void startApp(View view){
        LOGIN = edit_login.getText().toString();
        PASS = edit_password.getText().toString();

        MailConnector connector = new MailConnector();
        connector.execute();

    }

    class MailConnector extends AsyncTask{
        MailReader.MailType mailType;
        boolean recognized = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (LOGIN.contains("ya.ru") || LOGIN.contains("yandex.ru")){
                mailType = MailReader.MailType.YANDEX;
                recognized = true;
            }
            if (LOGIN.contains("mail.ru")){
                mailType = MailReader.MailType.MAILRU;
                recognized = true;
            }
            if (LOGIN.contains("gmail.com")){
                mailType = MailReader.MailType.GMAIL;
                recognized = true;
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.d(TAG, "reader before");
            reader = new MailReader(mailType, LOGIN, PASS);
            Log.d(TAG, "reader after " + reader);
            if (reader.successConnect)
                letters = reader.getLetters(0);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (reader.successConnect){
                Intent i = new Intent(MainActivity.this, ListActivity.class);
                i.putExtra("LOGIN", LOGIN);
                i.putExtra("PASS", PASS);
                startActivity(i);
            }
        }
    }


    class MailSender extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            //checkMailIMAP(LOGIN, PASS);
            MailReader reader = new MailReader(MailReader.MailType.YANDEX, LOGIN, PASS);
            reader.getLetters(0);
            reader.closeConnection();
            return null;
        }
    }
    public synchronized void sendMail(String subject, String body, String sender, String recipients, String filename) {
        try {
            Properties properties = new Properties();
            //Хост или IP-адрес почтового сервера
            properties.put("mail.smtp.host", "smtp.yandex.ru");
            //Требуется ли аутентификация для отправки сообщения
            properties.put("mail.smtp.auth", "true");
            //Порт для установки соединения
            properties.put("mail.smtp.socketFactory.port", "465");
            //Фабрика сокетов, так как при отправке сообщения Yandex требует SSL-соединения
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            Session session = Session.getDefaultInstance(properties,
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
            Log.e("Mail",e.toString());
        }
    }

}

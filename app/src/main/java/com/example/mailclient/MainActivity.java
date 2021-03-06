package com.example.mailclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final String TAG = "Mail";
    static ArrayList<Letter> letters;
    String LOGIN = "";
    String PASS = "";
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


}

package com.example.mailclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class LetterView extends AppCompatActivity {
    TextView sender;
    TextView subject;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_view);

        sender = findViewById(R.id.view_sender);
        subject = findViewById(R.id.view_subject);
        text = findViewById(R.id.view_text);
        Intent intent = getIntent();
        sender.setText(intent.getStringExtra("sender"));
        subject.setText(intent.getStringExtra("subject"));
        text.setText(intent.getStringExtra("text"));
    }
}

package com.example.mailclient;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LetterItem extends RecyclerView.ViewHolder {
    TextView sender;
    TextView subject;
    TextView text;
    public LetterItem(@NonNull View itemView) {
        super(itemView);
        sender = itemView.findViewById(R.id.sender);
        subject = itemView.findViewById(R.id.subject);
        text = itemView.findViewById(R.id.text);
    }
}

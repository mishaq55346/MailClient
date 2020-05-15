package com.example.mailclient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    String LOGIN = "misha.kolmykovqq1@yandex.ru";
    String PASS = "mishaq1";
    int current_page = 0;
    MailReader reader;
    ArrayList<Letter> letters = new ArrayList<>();
    LetterAdapter adapter;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    boolean isLoading = false;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent intent = getIntent();
        LOGIN = intent.getStringExtra("LOGIN");
        PASS = intent.getStringExtra("PASS");
        reader = MainActivity.reader;
        letters = MainActivity.letters;
        adapter = new LetterAdapter(letters);

        //Mail mail = new Mail();
        //mail.execute();
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //Log.d("Mail", "adapter set");
        //Log.d("Mail", String.valueOf(adapter.getItemCount()));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Letter letter = letters.get(position);
                        Intent i = new Intent(ListActivity.this, LetterView.class);
                        i.putExtra("sender", letter.sender);
                        i.putExtra("subject", letter.subject);
                        i.putExtra("text", letter.text);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                }));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastVisibleItemPosition() == letters.size() - 1) {
                        Log.d("scroll", "need to load more further");
                        MailDownloader downloader = new MailDownloader();
                        downloader.execute();


                        isLoading = true;
                    }
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionClose close = new ConnectionClose();
        close.execute();
    }

    public void clickFAB(View view) {
        Intent intent = new Intent(ListActivity.this, SendMail.class);
        intent.putExtra("LOGIN", LOGIN);
        intent.putExtra("PASS", PASS);
        startActivity(intent);
    }

    class ConnectionClose extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            reader.closeConnection();
            return null;
        }
    }

    class MailDownloader extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            current_page++;
            int scrollPosition = letters.size();
            int currentSize = scrollPosition;
            //int nextLimit = currentSize + 50;
            ArrayList<Letter> temp = reader.getLetters(current_page);
            for (int i = 0; i < temp.size(); i++) {
                letters.add(temp.get(i));
            }
             /*for (int i = nums.size(); i < nextLimit; i++){
                 nums.add(new NumItem(i + 1));
                 adapter.FillNum(i);
             }*/

            isLoading = false;
                return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            adapter.notifyDataSetChanged();
        }
    }

}
package com.example.mailclient;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LetterAdapter extends RecyclerView.Adapter {
    private List<Letter> letters;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void FillNum(int i){
        //letters.get(i).full_num = nf.format(letters.get(i).num);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LetterAdapter(ArrayList letters) {
        //Log.d("Mail", "received " + letters.size() + " letters in adapter create");
        this.letters = letters;
        for (int i = 0; i < letters.size(); i++) {
            FillNum(i);
        }
    }

    @Override
    public LetterItem onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LetterItem(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_numlist, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //if (position % 2 == 0)
        //    holder.itemView.setBackgroundColor(Color.WHITE);
        //else
        //    holder.itemView.setBackgroundColor(Color.GRAY);

        ((LetterItem)(holder)).sender.setText(letters.get(position).sender);
        ((LetterItem)(holder)).subject.setText(letters.get(position).subject);
        ((LetterItem)(holder)).text.setText(letters.get(position).text);


    }

    @Override
    public int getItemCount() {
        return letters != null ? letters.size() : 0;
    }
}

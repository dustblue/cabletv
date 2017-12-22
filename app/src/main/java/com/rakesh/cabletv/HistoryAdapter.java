package com.rakesh.cabletv;

/**
 * Created by Rakesh on 13-12-2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<Entry> entries;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, amount;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.trans_name);
            date = (TextView) view.findViewById(R.id.trans_date);
            amount = (TextView) view.findViewById(R.id.trans_amount);
        }
    }

    HistoryAdapter(List<Entry> entries) {
        this.entries = entries;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.name.setText(entry.getUserName());
        holder.date.setText(entry.getTransaction().getDateTime());
        holder.amount.setText(entry.getTransaction().getAmount());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}
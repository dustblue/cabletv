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

class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {

    private List<Transaction> mTransactions;
    private String mUsername;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, amount;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.trans_name);
            date = (TextView) view.findViewById(R.id.trans_date);
            amount = (TextView) view.findViewById(R.id.trans_amount);
        }
    }

    TransactionsAdapter(List<Transaction> transactions, String username) {
        this.mTransactions = transactions;
        this.mUsername = username;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Transaction transaction = mTransactions.get(position);
        holder.name.setText(mUsername);
        holder.date.setText(transaction.getDateTime());
        holder.amount.setText(String.valueOf(transaction.getAmount()));
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }
}
package com.rakesh.cabletv;

/**
 * Created by Rakesh on 13-12-2017.
 */

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.List;

class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    private List<UserEntry> mUsers;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView lastPaidDate;
        CheckedTextView name;

        MyViewHolder(View view) {
            super(view);
            name = (CheckedTextView) view.findViewById(R.id.item_name);
            lastPaidDate = (TextView) view.findViewById(R.id.item_last_paid_date);
        }
    }

    ListAdapter(List<UserEntry> users) {
        this.mUsers = users;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserEntry userEntry = mUsers.get(position);
        holder.name.setText(userEntry.getUser().getName());
        if(!userEntry.getUser().getStatus()) {
            holder.name.setTextColor(Color.RED);
        }
        if(userEntry.isIfPaid()) {
            holder.name.setCheckMarkDrawable(R.drawable.ic_check_green);
        } else {
            holder.name.setCheckMarkDrawable(R.drawable.ic_cross_red);
        }
        holder.lastPaidDate.setText(userEntry.getLastPaidDate());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
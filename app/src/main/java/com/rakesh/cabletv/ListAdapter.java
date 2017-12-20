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

class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    private List<User> mUsers;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, lastPaidDate;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.item_name);
            lastPaidDate = (TextView) view.findViewById(R.id.item_last_paid_date);
        }
    }

    ListAdapter(List<User> users) {
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
        User user = mUsers.get(position);
        holder.name.setText(user.getName());
        //YKWTODO
        holder.lastPaidDate.setText(user.getInstallDate());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
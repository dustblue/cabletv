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

    private List<User> users;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, interval;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.item_name);
            interval = (TextView) view.findViewById(R.id.item_status);
        }
    }

    ListAdapter(List<User> users) {
        this.users = users;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = users.get(position);
        holder.title.setText(user.getName());
        //holder.interval.setText(parseTime(user.getStartTime(), user.getEndTime()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
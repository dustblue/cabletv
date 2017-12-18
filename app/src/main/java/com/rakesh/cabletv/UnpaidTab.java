package com.rakesh.cabletv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UnpaidTab extends Fragment {

    TextView emptyText, listTitle;
    RecyclerView recyclerView;
    private ListAdapter mAdapter;
    List<User> userList;
    DBHandler db;
    String cluster;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_unpaid, container, false);

        emptyText = (TextView) v.findViewById(R.id.unpaid_empty_text);
        listTitle = (TextView) v.findViewById(R.id.unpaid_list_title);
        recyclerView = (RecyclerView) v.findViewById(R.id.unpaid_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ListAdapter(userList);
        recyclerView.setAdapter(mAdapter);

        db = new DBHandler(getContext());
        userList = new ArrayList<>();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cluster = bundle.getString("cluster", "none");
        }

        if (cluster.equals("none")) {
            userList = db.getAllUsers(false);
        } else {
            listTitle.setText(cluster);
            userList = db.getUsersByCluster(cluster, false);
        }

        mAdapter.notifyDataSetChanged();
        if (userList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        }

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(),
                        (view, position) -> {
                            Intent i = new Intent(getContext(), UserActivity.class);
                            i.putExtra("vc", userList.get(position).getVc());
                            startActivityForResult(i, 11);
                        })
        );

        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 11) {
            if (resultCode == Activity.RESULT_OK) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onStop(){
        db.close();
        super.onStop();
    }
}
package com.rakesh.cabletv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.rakesh.cabletv.Utilities.TYPE_ALL;
import static com.rakesh.cabletv.Utilities.TYPE_CLUSTER;
import static com.rakesh.cabletv.Utilities.TYPE_UNPAID;

public class ListActivity extends AppCompatActivity {

    TextView emptyText, listTitle;
    RecyclerView recyclerView;
    private ListAdapter mAdapter;
    List<User> userList;
    DBHandler db;
    int type;
    String cluster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        emptyText = (TextView) findViewById(R.id.empty_text);
        listTitle = (TextView) findViewById(R.id.list_title);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ListAdapter(userList);
        recyclerView.setAdapter(mAdapter);

        db = new DBHandler(this);
        userList = new ArrayList<>();

        Intent intent = getIntent();
        listTitle.setText(intent.getStringExtra("title"));
        type = intent.getIntExtra("type", TYPE_ALL);

        if (type == TYPE_ALL) {
            userList = db.getAllUsers();
        } else if (type == TYPE_CLUSTER) {
            cluster = intent.getStringExtra("cluster");
            userList = db.getUsersByCluster(cluster);
        } else if (type == TYPE_UNPAID) {
//            userList = db.getUnpaidUsers();
        }

        mAdapter.notifyDataSetChanged();
        if (userList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        }

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(),
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent i = new Intent(ListActivity.this, UserActivity.class);
                                i.putExtra("vc", userList.get(position).getVc());
                                startActivity(i);
                            }
                        })
        );
    }
}

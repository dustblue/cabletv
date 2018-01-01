package com.rakesh.cabletv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class InactiveActivity extends AppCompatActivity {

    TextView emptyText;
    RecyclerView recyclerView;
    ListAdapter adapter;
    List<UserEntry> userList;
    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inactive);

        emptyText = (TextView) findViewById(R.id.inactive_empty_text);
        recyclerView = (RecyclerView) findViewById(R.id.inactive_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        db = new DBHandler(this);
        userList = db.getInactiveUsers();

        if (userList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            adapter = new ListAdapter(userList);
            recyclerView.setAdapter(adapter);
        }

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this,
                        (view, position) -> {
                            Intent i = new Intent(InactiveActivity.this, UserActivity.class);
                            i.putExtra("vc", userList.get(position).getUser().getVc());
                            startActivityForResult(i, 12);
                        })
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12) {
            userList = db.getInactiveUsers();
            adapter.notifyDataSetChanged();
        }
    }
}

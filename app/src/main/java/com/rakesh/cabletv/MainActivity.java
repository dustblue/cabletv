package com.rakesh.cabletv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private final String items[] = {
            "Add User",
            "All Users",
            "Clusters"
    };

    private final int itemImages[] = {
            R.drawable.ic_add_user,
            R.drawable.ic_all_users,
            R.drawable.ic_clusters,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        GridAdapter adapter = new GridAdapter(items, itemImages);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(),
                        (view, position) -> {
                            switch (position) {
                                case 0:
                                    startActivity(new Intent(MainActivity.this, EditActivity.class));
                                    break;
                                case 1:
                                    startActivity(new Intent(MainActivity.this, ListActivity.class));
                                    break;
                                case 2:
                                    Intent i =new Intent(MainActivity.this, UserActivity.class);
                                    i.putExtra("vc", 1234);
                                    startActivity(i);
                                    break;
                                default:
                                    break;
                            }
                        })
        );
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> MainActivity.super.onBackPressed()).create().show();
    }
}
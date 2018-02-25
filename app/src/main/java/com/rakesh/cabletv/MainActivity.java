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
            "Clusters",
            "History",
            "Inactive",
            "Backup/Restore",
            "Collection",
            "Settings"
    };

    private final int itemImages[] = {
            R.drawable.ic_add_user,
            R.drawable.ic_all_users,
            R.drawable.ic_clusters,
            R.drawable.ic_history,
            R.drawable.ic_inactive,
            R.drawable.ic_backup,
            R.drawable.ic_collection,
            R.drawable.ic_settings
    };

    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHandler(this);
        db.clearUp();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        //TODO Add Feedback/Report Error Activity

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
                                    Intent i = new Intent(MainActivity.this, ListActivity.class);
                                    i.putExtra("cluster", "All Users");
                                    startActivity(i);
                                    break;
                                case 2:
                                    startActivity(new Intent(MainActivity.this, ClusterActivity.class));
                                    break;
                                case 3:
                                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                                    break;
                                case 4:
                                    startActivity(new Intent(MainActivity.this, InactiveActivity.class));
                                    break;
                                case 5:
                                    startActivity(new Intent(MainActivity.this, BackupRestoreActivity.class));
                                    break;
                                case 6:
                                    startActivity(new Intent(MainActivity.this, CollectionActivity.class));
                                    break;
                                case 7:
                                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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
package com.rakesh.cabletv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ClusterActivity extends AppCompatActivity {

    String[] clusters;
    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cluster_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        db = new DBHandler(this);
        clusters = db.getClusters();
        GridAdapter adapter = new GridAdapter(clusters, null);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(),
                        (view, position) -> {
                            Intent i = new Intent(ClusterActivity.this, ListActivity.class);
                            i.putExtra("cluster", clusters[position]);
                            startActivity(i);
                        })
        );
    }
}

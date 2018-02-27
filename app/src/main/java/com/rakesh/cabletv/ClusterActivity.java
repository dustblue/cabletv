package com.rakesh.cabletv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ClusterActivity extends AppCompatActivity {

    public static final int RESULT_CLUS = 23;
    String[] clusters;
    DBHandler db;
    TextView emptyText;
    GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster);

        emptyText = (TextView) findViewById(R.id.cluster_empty_text);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cluster_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        db = new DBHandler(this);
        clusters = db.getClusters();
        if (clusters != null) {
            adapter = new GridAdapter(clusters, null);
            recyclerView.setAdapter(adapter);
        } else {
            emptyText.setVisibility(View.VISIBLE);
        }

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(),
                        (view, position) -> {
                            Intent i = new Intent(ClusterActivity.this, ListActivity.class);
                            i.putExtra("cluster", clusters[position]);
                            startActivityForResult(i, RESULT_CLUS);
                        })
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CLUS && resultCode == RESULT_FIRST_USER) {
            clusters = db.getClusters();
            adapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

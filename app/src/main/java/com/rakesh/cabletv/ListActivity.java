package com.rakesh.cabletv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class ListActivity extends AppCompatActivity {

    final int numberOfTabs = 2;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence titles[] = {"All", "Unpaid"};
    String cluster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(cluster);

        Intent intent = getIntent();
        cluster = intent.getStringExtra("cluster");

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, numberOfTabs, cluster);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(position -> R.color.colorPrimary );
        tabs.setViewPager(pager);
    }


}

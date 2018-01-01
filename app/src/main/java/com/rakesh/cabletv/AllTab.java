package com.rakesh.cabletv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.util.List;

public class AllTab extends Fragment {

    TextView emptyText;
    RecyclerView recyclerView;
    private ListAdapter mAdapter;
    List<UserEntry> userList;
    DBHandler db;
    String cluster = "All Users";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_all, container, false);

        emptyText = (TextView) v.findViewById(R.id.all_empty_text);
        recyclerView = (RecyclerView) v.findViewById(R.id.all_list);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cluster = bundle.getString("cluster", "All Users");
        }

        db = new DBHandler(getContext());
        new getList(getActivity()).execute();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(),
                        (view, position) -> {
                            Intent i = new Intent(getContext(), UserActivity.class);
                            i.putExtra("vc", userList.get(position).getUser().getVc());
                            startActivityForResult(i, 10);
                        })
        );

        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            new getList(getActivity()).execute();
        }
    }

    @Override
    public void onStop() {
        db.close();
        super.onStop();
    }

    public class getList extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        getList(Context context) {
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Refreshing List...");
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Void doInBackground(Void... args) {
            if (cluster.equals("All Users")) {
                userList = db.getAllUsers();
            } else {
                userList = db.getUsersByCluster(cluster);
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (userList.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new ListAdapter(userList);
                recyclerView.setAdapter(mAdapter);
            }
        }
    }
}
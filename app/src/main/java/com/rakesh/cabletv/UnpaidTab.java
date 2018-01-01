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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.rakesh.cabletv.DBHandler.TAG;

public class UnpaidTab extends Fragment {

    TextView emptyText;
    RecyclerView recyclerView;
    private ListAdapter mAdapter;
    List<UserEntry> userList, unpaidList;
    DBHandler db;
    String cluster;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_unpaid, container, false);

        emptyText = (TextView) v.findViewById(R.id.unpaid_empty_text);
        recyclerView = (RecyclerView) v.findViewById(R.id.unpaid_list);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cluster = bundle.getString("cluster", "All Users");
        }

        db = new DBHandler(getContext());
        unpaidList = new ArrayList<>();
        new getUnpaidList(getActivity()).execute();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(),
                        (view, position) -> {
                            Intent i = new Intent(getContext(), UserActivity.class);
                            i.putExtra("vc", unpaidList.get(position).getUser().getVc());
                            startActivityForResult(i, 11);
                        })
        );

        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 11) {
            new getUnpaidList(getActivity()).execute();
        }
    }

    @Override
    public void onStop(){
        db.close();
        super.onStop();
    }

    public class getUnpaidList extends AsyncTask<Void, Void, Void> {
        private ProgressDialog mDialog;

        public getUnpaidList(Context context) {
            mDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            mDialog.setMessage("Refreshing List...");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        protected Void doInBackground(Void... args) {
            if (cluster.equals("All Users")) {
                userList = db.getAllUsers();
            } else {
                userList = db.getUsersByCluster(cluster);
            }
            if (!userList.isEmpty()) {
                unpaidList.clear();
                for(UserEntry userEntry : userList) {
                    if(!userEntry.isIfPaid()) {
                        unpaidList.add(userEntry);
                    }
                }
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }

            if (userList.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new ListAdapter(unpaidList);
                recyclerView.setAdapter(mAdapter);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.e(TAG, "onHiddenChanged called");
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume called");
        super.onResume();
    }
}
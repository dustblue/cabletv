package com.rakesh.cabletv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.rakesh.cabletv.UserActivity.RESULT_CHANGED;

public class AllTab extends Fragment {

    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText searchEdit;
    TextView emptyText;
    RecyclerView recyclerView;
    private ListAdapter mAdapter;
    List<UserEntry> userList, originalList;
    DBHandler db;
    String cluster = "All Users";
    int pos = 0;
    Toolbar toolbar;
    RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_all, container, false);
        setHasOptionsMenu(true);

        toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        emptyText = (TextView) v.findViewById(R.id.all_empty_text);
        recyclerView = (RecyclerView) v.findViewById(R.id.all_list);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(cluster);
        toolbar.setOnMenuItemClickListener(item -> {
            handleMenuSearch();
            return true;
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cluster = bundle.getString("cluster", "All Users");
        }

        db = new DBHandler(getContext());
        new getList(getActivity()).execute();

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(),
                        (view, position) -> {
                            Intent i = new Intent(getContext(), UserActivity.class);
                            i.putExtra("vc", userList.get(position).getUser().getVc());
                            pos = position;
                            startActivityForResult(i, 10);
                        })
        );

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        super.onPrepareOptionsMenu(menu);
    }

    protected void handleMenuSearch() {
        ActionBar action = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (isSearchOpened) {

            action.setDisplayShowCustomEnabled(false);
            action.setDisplayShowTitleEnabled(true);
            action.setTitle(cluster);

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }

            mSearchAction.setIcon(R.drawable.ic_search);
            new getList(getActivity()).execute();
            isSearchOpened = false;
        } else {

            action.setDisplayShowCustomEnabled(true);
            action.setCustomView(R.layout.search_bar);
            action.setDisplayShowTitleEnabled(false);

            searchEdit = (EditText) action.getCustomView().findViewById(R.id.editSearch);
            searchEdit.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    try {
                        doSearch(v.getText().toString().trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                        doSearch("");
                    }
                    return true;
                }
                return false;
            });

            searchEdit.requestFocus();

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchEdit, InputMethodManager.SHOW_IMPLICIT);
            }
            mSearchAction.setIcon(R.drawable.ic_close);
            isSearchOpened = true;
        }
    }

    private void doSearch(String text) {

        if (text.equals("")) {
            userList.clear();
            userList.addAll(originalList);
        } else {
            List<UserEntry> tempList = new ArrayList<>();
            for (UserEntry userEntry : originalList) {
                if (userEntry.getUser().search(text))
                    tempList.add(userEntry);
            }
            userList.clear();
            userList.addAll(tempList);
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_CHANGED) {
            new getList(getActivity()).execute();
        } else new getList(getActivity()).execute();
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
            originalList = userList;
            return null;
        }

        protected void onPostExecute(Void result) {
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (userList.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
            } else {
                try {
                    layoutManager.scrollToPosition(pos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAdapter = new ListAdapter(userList);
                recyclerView.setAdapter(mAdapter);
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
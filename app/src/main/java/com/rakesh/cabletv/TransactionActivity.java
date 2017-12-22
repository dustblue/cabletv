package com.rakesh.cabletv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Transaction> transactionsList;
    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        recyclerView = findViewById(R.id.transactions_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        db = new DBHandler(this);
        String vc = getIntent().getStringExtra("vc");
        String username = getIntent().getStringExtra("username");

        transactionsList = db.getTransactionsByUser(vc);
        TransactionsAdapter adapter = new TransactionsAdapter(transactionsList, username);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this,
                        (view, position) -> {
//                            TODO Add Edit Delete Transaction
//                            Intent i = new Intent(getContext(), UserActivity.class);
//                            i.putExtra("vc", entries.get(position).getVc());
//                            startActivityForResult(i, 10);
                        })
        );

    }
}

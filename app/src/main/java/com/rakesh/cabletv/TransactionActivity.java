package com.rakesh.cabletv;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Transaction> transactionsList;
    TransactionsAdapter adapter;
    DBHandler db;
    TextView emptyText;
    String username, vc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        emptyText = findViewById(R.id.trans_empty_text);
        recyclerView = findViewById(R.id.transactions_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        db = new DBHandler(this);
        vc = getIntent().getStringExtra("vc");
        username = getIntent().getStringExtra("username");

        transactionsList = db.getTransactionsByUser(vc);
        if (!transactionsList.isEmpty()) {
            adapter = new TransactionsAdapter(transactionsList, username);
            recyclerView.setAdapter(adapter);
        } else {
            emptyText.setVisibility(View.VISIBLE);
        }

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this,
                        (view, position) -> {
                            showTransactionsDialog(transactionsList.get(position));
                        })
        );

    }

    public void showTransactionsDialog(Transaction transaction) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.transaction_dialog, null);

        TextView amount = (TextView) dialogView.findViewById(R.id.dialog_amount);
        TextView date = (TextView) dialogView.findViewById(R.id.dialog_date);

        amount.setText("Amount : " + transaction.getAmount());
        date.setText(transaction.getDateTime());

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(username);
        //dialogBuilder.setMessage("Enter text below");
        dialogBuilder.setPositiveButton("Delete", (dialog, whichButton) -> {
            db.deleteTransaction(transaction);
            transactionsList.remove(transaction);
            adapter.notifyDataSetChanged();

            Snackbar.make(recyclerView, "Transaction Deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", v -> {
                        db.addTransaction(transaction, false);
                        transactionsList.add(transaction);
                        adapter.notifyDataSetChanged();
                        Snackbar.make(recyclerView, "Transaction Restored", Snackbar.LENGTH_SHORT)
                                .show();
                    })
                    .show();
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}

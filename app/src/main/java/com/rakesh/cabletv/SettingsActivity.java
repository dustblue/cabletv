package com.rakesh.cabletv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    Button clearUp, feedback, alter, reformat;
    TextView textView;
    DBHandler db;
    ConstraintLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = new DBHandler(this);
        parent = findViewById(R.id.settings);

        clearUp = (Button) findViewById(R.id.clear_up);
        clearUp.setOnClickListener(view -> new clearTask(this).execute());

        feedback = (Button) findViewById(R.id.feedback);
        feedback.setOnClickListener(view -> {
                    Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "rakesh.blyton@gmail.com", null));
                    email.putExtra(Intent.EXTRA_SUBJECT, "Feedback CableTV");
                    startActivity(Intent.createChooser(email, "Send Feedback:"));
                }
        );

        alter = (Button) findViewById(R.id.alter);
        alter.setEnabled(false);
        alter.setOnClickListener(view -> {
                db.addColumn();
//                Snackbar.make(parent, "Locked", Snackbar.LENGTH_SHORT).show();
        });

        reformat = (Button) findViewById(R.id.reformat);
        reformat.setEnabled(false);
        reformat.setOnClickListener(view -> {
                new reformatTask(this).execute();
//                Snackbar.make(parent, "Locked", Snackbar.LENGTH_SHORT).show();
        });

        textView = (TextView) findViewById(R.id.developer_text);
        textView.setOnClickListener(view -> showAuthDialog());
    }

    public void showAuthDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.auth_dialog, null);

        EditText pass = (EditText) dialogView.findViewById(R.id.auth);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Enter Dev password:");
        dialogBuilder.setPositiveButton("Go", (dialog, whichButton) -> {
            Calendar now = Calendar.getInstance();
            int key = now.get(Calendar.HOUR_OF_DAY) * 100 + now.get(Calendar.MINUTE);
            if (Integer.parseInt(pass.getText().toString()) == key) {
                Snackbar.make(parent, "Authenticated, unlocked", Snackbar.LENGTH_SHORT).show();
                alter.setEnabled(true);
                reformat.setEnabled(true);
            } else {
                Snackbar.make(parent, "Incorrect, Try again", Snackbar.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    class clearTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        clearTask(SettingsActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Clearing Up...");
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Void doInBackground(Void... args) {
            db.clearUp();
            return null;
        }

        protected void onPostExecute(Void result) {
            // do UI work here
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Snackbar.make(parent, "Cleared unknown characters!!", Snackbar.LENGTH_SHORT).show();
        }
    }

    class reformatTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        reformatTask(SettingsActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Reformatting Dates...");
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Void doInBackground(Void... args) {
            db.reformatDates();
            return null;
        }

        protected void onPostExecute(Void result) {
            // do UI work here
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Snackbar.make(parent, "Reformat Dates Successful", Snackbar.LENGTH_SHORT).show();
        }
    }
}

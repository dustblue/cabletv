package com.rakesh.cabletv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    Button clearUp, feedback;
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
                            "mailto","rakesh.blyton@gmail.com", null));
                    email.putExtra(Intent.EXTRA_SUBJECT, "Feedback CableTV");
                    startActivity(Intent.createChooser(email, "Send Feedback:"));
                }
        );
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

}

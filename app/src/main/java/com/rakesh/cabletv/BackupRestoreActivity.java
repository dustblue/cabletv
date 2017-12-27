package com.rakesh.cabletv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BackupRestoreActivity extends AppCompatActivity {

    TextView filePath, lastBackup, lastUsersRestore, lastTransRestore;
    Button changePath, backupNow, addUsers, addTransactions;
    View parent;
    String rootDir;
    DBHandler db;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static final String TAG = "com.rakesh.cabletv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        db = new DBHandler(this);
        parent = findViewById(R.id.backup_restore_parent);
        filePath = (TextView) findViewById(R.id.file_path);
        lastBackup = (TextView) findViewById(R.id.last_backup);
        lastUsersRestore = (TextView) findViewById(R.id.last_users_restore);
        lastTransRestore = (TextView) findViewById(R.id.last_trans_restore);
        changePath = (Button) findViewById(R.id.button_change_path);
        backupNow = (Button) findViewById(R.id.button_backup);
        addUsers = (Button) findViewById(R.id.button_users_restore);
        addTransactions = (Button) findViewById(R.id.button_trans_restore);

        preferences = getApplicationContext().getSharedPreferences("cabletv", 0);
        rootDir = Environment.getExternalStorageDirectory().toString();
        Log.e(TAG, rootDir);

        filePath.setText(preferences.getString("filepath", rootDir));
        lastBackup.setText(preferences.getString("backup_time", "Not Available"));
        lastUsersRestore.setText(preferences.getString("last_user_add", "Not Available"));
        lastTransRestore.setText(preferences.getString("last_trans_add", "Not Available"));

        changePath.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(Intent.createChooser(i, "Choose directory"), 99);
        });

        backupNow.setOnClickListener(v -> {
            new backup(this, preferences.getString("filepath", rootDir)).execute();
        });

        addUsers.setOnClickListener(v -> {
            new addUsers(this, rootDir + "/userlist.csv").execute();
//            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//            i.addCategory(Intent.CATEGORY_DEFAULT);
//            startActivityForResult(Intent.createChooser(i, "Choose Users CSV File"), 100);
        });

        addTransactions.setOnClickListener(v -> {
            new addTransactions(this, rootDir + "/userlist.csv").execute();
//            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//            i.addCategory(Intent.CATEGORY_DEFAULT);
//            startActivityForResult(Intent.createChooser(i, "Choose Transactions CSV File"), 101);
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED && data != null) {
            switch (requestCode) {
                case 99:
                    Log.e(TAG, "Result URI :" + data.getData());
                    editor = preferences.edit();
                    editor.putString("filepath", data.getData().toString());
                    editor.apply();
                    filePath.setText(preferences.getString("filepath", rootDir));
                    break;
                case 100:
                    new addUsers(this, rootDir + "/userlist.csv").execute();
                    break;
                case 101:
                    new addTransactions(this, rootDir + "/userlist.csv").execute();
                    break;
            }
        }
    }

    public String getTimeInstance() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss", Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    public void backupUsers(String storagePath) {

        try (CSVWriter writer = new CSVWriter(new FileWriter(storagePath
                + "/Users_Backup_" + getTimeInstance() + ".csv"))) {
            List<String[]> database = new ArrayList<>();
            database.addAll(db.backupUsers());
            writer.writeAll(database);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void backupTransactions(String storagePath) {

        try (CSVWriter writer = new CSVWriter(new FileWriter(storagePath
                + "/Transactions_Backup_" + getTimeInstance() + ".csv"))) {
            List<String[]> database = new ArrayList<>();
            database.addAll(db.backupTransactions());
            writer.writeAll(database);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class backup extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String mPath;

        public backup(BackupRestoreActivity activity, String path) {
            dialog = new ProgressDialog(activity);
            mPath = path;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Backing up...");
            dialog.setCancelable(false);
            //dialog.show();
        }

        protected Void doInBackground(Void... args) {
            backupUsers(mPath);
            backupTransactions(mPath);
            return null;
        }

        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            editor = preferences.edit();
            editor.putString("backup_time", getTimeInstance());
            editor.apply();
            lastBackup.setText(preferences.getString("backup_time", "Not Available"));
            Snackbar.make(parent, "Backup successful!!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void addUsersFromFile(String filename) {

        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            String[] row;
            List<?> content = reader.readAll();

            for (Object object : content) {
                row = (String[]) object;
                if (row.length == 7) {
                    db.addUser(new User(row[0], row[1], row[2], row[3], row[4]
                            , row[4].split(",")[1].trim(), row[5], Integer.parseInt(row[6]) > 0));
                } else {
                    Toast.makeText(this, "Invalid Format of Data", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    private class addUsers extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String mPath;

        public addUsers(BackupRestoreActivity activity, String path) {
            dialog = new ProgressDialog(activity);
            mPath = path;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Adding Users...");
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Void doInBackground(Void... args) {
            addUsersFromFile(mPath);
            return null;
        }

        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            editor = preferences.edit();
            editor.putString("last_user_add", getTimeInstance());
            editor.apply();
            lastUsersRestore.setText(preferences.getString("last_user_add", "Not Available"));
            Snackbar.make(parent, "Users added successfully!!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void addTransactionsFromFile(String filename) {

        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            String[] row;
            List<?> content = reader.readAll();

            for (Object object : content) {
                row = (String[]) object;
                if (row.length == 4) {
                    db.addTransaction(new Transaction(row[0], Integer.parseInt(row[2]), row[3]));
                } else {
                    Snackbar.make(parent, "Invalid Format of Data", Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    private class addTransactions extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String mPath;

        public addTransactions(BackupRestoreActivity activity, String path) {
            dialog = new ProgressDialog(activity);
            mPath = path;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Adding Transactions...");
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Void doInBackground(Void... args) {
            addTransactionsFromFile(mPath);
            return null;
        }

        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            editor = preferences.edit();
            editor.putString("last_trans_add", getTimeInstance());
            editor.apply();
            lastTransRestore.setText(preferences.getString("last_trans_add", "Not Available"));
            Snackbar.make(parent, "Transactions added successfully!!", Snackbar.LENGTH_SHORT).show();
        }
    }

}

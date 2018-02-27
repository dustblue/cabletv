package com.rakesh.cabletv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
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
//        Log.e(TAG, rootDir);

        filePath.setText(rootDir);
//      filePath.setText(preferences.getString("filepath", rootDir));
        lastBackup.setText(preferences.getString("backup_time", "Not Available"));
        lastUsersRestore.setText(preferences.getString("last_user_add", "Not Available"));
        lastTransRestore.setText(preferences.getString("last_trans_add", "Not Available"));

        changePath.setOnClickListener(v -> {
            //Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            //i.addCategory(Intent.CATEGORY_DEFAULT);
            //startActivityForResult(Intent.createChooser(i, "Choose directory"), 99);
        });

        backupNow.setOnClickListener(v -> {
            new backup(this, rootDir).execute();
//            new backup(this, preferences.getString("filepath", rootDir)).execute();
        });

        addUsers.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent,
                        "Choose Users CSV File"), 100);
            } catch (android.content.ActivityNotFoundException ex) {
                Snackbar.make(parent, "Please install a File Manager", Snackbar.LENGTH_SHORT).show();
            }
        });

        addTransactions.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent,
                        "Choose Transactions CSV File"), 101);
            } catch (android.content.ActivityNotFoundException ex) {
                Snackbar.make(parent, "Please install a File Manager", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};

            try (Cursor cursor = context.getContentResolver().query(uri, projection,
                    null, null, null)) {

                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = null;
            try {
                path = getPath(this, uri);
            } catch (URISyntaxException e) {
                Snackbar.make(parent, "URISyntaxException", Snackbar.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            switch (requestCode) {
                case 99:
                    editor = preferences.edit();
                    editor.putString("filepath", path);
                    editor.apply();
                    filePath.setText(preferences.getString("filepath", rootDir));
                    break;

                case 100:
                    if (path != null && path.substring(path.lastIndexOf(".")).equals(".csv"))
                        new addUsers(this, path).execute();
                    else
                        Snackbar.make(parent, "Wrong File Type", Snackbar.LENGTH_SHORT)
                                .setAction("CHOOSE", v -> addUsers.callOnClick()).show();
                    break;

                case 101:
                    if (path != null && path.substring(path.lastIndexOf(".")).equals(".csv"))
                        new addTransactions(this, path).execute();
                    else
                        Snackbar.make(parent, "Wrong File Type", Snackbar.LENGTH_SHORT)
                                .setAction("CHOOSE", v -> addUsers.callOnClick()).show();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

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

    public String trimNumber(String number) {
        if (number.length() > 10) {
            return number.substring(2);
        }
        return number;
    }

    public void addUsersFromFile(String filename) {

        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            String[] row;
            List<?> content = reader.readAll();

            for (Object object : content) {
                row = (String[]) object;
                if (row.length == 7) {
                    db.addUser(new User(row[0], row[1], row[2], trimNumber(row[3]), row[4]
                            , row[4].split(",")[1].trim(), row[5], Integer.parseInt(row[6]) > 0));
                } else {
                    Snackbar.make(parent, "Invalid Format of Data !", Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        Snackbar.make(parent, "Users added successfully!!", Snackbar.LENGTH_SHORT).show();

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
        }
    }

    public void addTransactionsFromFile(String filename) {

        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            String[] row;
            List<?> content = reader.readAll();

            for (Object object : content) {
                row = (String[]) object;
                if (row.length == 4) {
                    db.addTransaction(new Transaction(row[0], Integer.parseInt(row[2]), row[3]), true);
                } else {
                    Snackbar.make(parent, "Invalid Format of Data !", Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        Snackbar.make(parent, "Transactions added successfully!!", Snackbar.LENGTH_SHORT).show();

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
        }
    }

}

package com.rakesh.cabletv;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.rakesh.cabletv.UserActivity.RESULT_DELETED;

public class EditActivity extends AppCompatActivity {

    ProgressDialog dialog;
    DBHandler db;
    FloatingActionButton fabSave, fabDelete;
    EditText nameField, phoneField, addressField, cafField, vcField, installDateField;
    String name, phone, address, caf, vc1, installDate;
    int year, month, day;
    CheckBox isActive;
    Boolean editFlag = false;
    View parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        parent = findViewById(R.id.edit_act);
        db = new DBHandler(this);

        nameField = (EditText) findViewById(R.id.name_edit);
        phoneField = (EditText) findViewById(R.id.phone_edit);
        addressField = (EditText) findViewById(R.id.address_edit);
        cafField = (EditText) findViewById(R.id.caf_edit);
        vcField = (EditText) findViewById(R.id.vc_edit);
        installDateField = (EditText) findViewById(R.id.install_edit);
        isActive = (CheckBox) findViewById(R.id.active_box1);

        editOrAdd();

        installDateField.setOnClickListener(view -> showDatePicker());

        fabSave = (FloatingActionButton) findViewById(R.id.ok_edit);
        fabSave.setOnClickListener(view -> {
            name = nameField.getText().toString();
            phone = phoneField.getText().toString();
            address = addressField.getText().toString();
            caf = cafField.getText().toString();
            vc1 = vcField.getText().toString();
            installDate = installDateField.getText().toString();
            validateAndSave();

        });

        fabDelete = (FloatingActionButton) findViewById(R.id.delete);
        fabDelete.setOnClickListener(view -> {
            String vc = getIntent().getStringExtra("vc");
            if (vc != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("Do you want to delete this user? Press back to discard changes")
                        .setPositiveButton("YES, delete", (dialog, which) -> {
                            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            db.deleteUser(vc);
                            setResult(RESULT_DELETED);
                            finish();
                        }).setNegativeButton("NO", (dialog, which) -> dialog.cancel()).show();
            } else {
                onBackPressed();
            }
        });

    }

    public void showDatePicker() {
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(this, (datePicker, i, i1, i2) -> {
            day = i2;
            month = i1;
            year = i;
            installDateField.setText(i2 + "/" + (i1 + 1) + "/" + i);
        }, year, month, day);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    public void editOrAdd() {
        String vc = getIntent().getStringExtra("vc");
        if (vc != null) {
            User user = db.getUser(vc);
            nameField.setText(user.getName());
            phoneField.setText(user.getPhone());
            addressField.setText(user.getAddress());
            cafField.setText(user.getCaf());
            vcField.setText(user.getVc());
            installDateField.setText(user.getInstallDate());
            isActive.setChecked(user.getStatus());
            editFlag = true;
        } else {
            Calendar now = Calendar.getInstance();
            Date date = now.getTime();
            day = now.get(Calendar.DAY_OF_MONTH);
            month = now.get(Calendar.MONTH);
            year = now.get(Calendar.YEAR);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            installDateField.setText(dateFormat.format(date));
        }
    }

    public void validateAndSave() {
        if (!name.equals("")) {
            if (!phone.equals("") && phone.length() >= 6) {
                if (!address.equals("")) {
                    if (!caf.equals("")) {
                        if (!vc1.equals("")) {
                            if (!installDate.equals("")) {
                                new saveTask(this).execute();

                            } else {
                                makeSnackBar("Pick an Install Date!", 0);
                            }
                        } else {
                            makeSnackBar("Enter the VC!", 1);
                        }
                    } else {
                        makeSnackBar("Enter the CAF!", 2);
                    }
                } else {
                    makeSnackBar("Enter the Address!", 3);
                }
            } else {
                makeSnackBar("Enter a valid Number!", 4);
            }
        } else {
            makeSnackBar("Enter the Name!", 5);
        }
    }

    public void makeSnackBar(String text, final int viewID) {
        Snackbar.make(parent, text, Snackbar.LENGTH_SHORT)
                .setAction("GO", view -> {
                    if (viewID == 0)
                        installDateField.requestFocus();
                    else if (viewID == 1)
                        vcField.requestFocus();
                    else if (viewID == 2)
                        cafField.requestFocus();
                    else if (viewID == 3)
                        addressField.requestFocus();
                    else if (viewID == 4)
                        phoneField.requestFocus();
                    else if (viewID == 5)
                        nameField.requestFocus();
                }).show();
    }

    private class saveTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private Activity mActivity;

        public saveTask(EditActivity activity) {
            dialog = new ProgressDialog(activity);
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Saving...");
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Void doInBackground(Void... args) {
            User user = new User(vc1, caf, name, phone, address, address.split(",")[1].trim(),
                    installDate, isActive.isChecked());

            if (editFlag)
                db.updateUser(user, vc1);
            else
                db.addUser(user);

            return null;
        }

        protected void onPostExecute(Void result) {
            // do UI work here
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            setResult(RESULT_OK);
            mActivity.finish();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard")
                .setMessage("Do you want to exit? Changes will not be saved!")
                .setPositiveButton("YES, exit", (dialog, which) -> {
                    finish();
                    Toast.makeText(getApplicationContext(), "Discarded changes", Toast.LENGTH_SHORT).show();

                }).setNegativeButton("NO", (dialog, which) -> dialog.cancel()).show();
    }

    @Override
    protected void onStop() {
//        if (transaction_dialog.isShowing()) {
//            transaction_dialog.dismiss();
//        }
        super.onStop();
    }
}

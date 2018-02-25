package com.rakesh.cabletv;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserActivity extends AppCompatActivity {

    public static final int EDIT_REQUEST = 210;
    public static final int RESULT_DELETED = 270;
    public static final int RESULT_CHANGED = 274;
    DBHandler db;
    FloatingActionButton fabOk, fabEdit, fabTrans;
    CheckBox active;
    TextView nameText, phoneText, addressText, cafText, vcText, installDateText;
    EditText amountField, dateField;
    int year, month, day;
    View v;
    String vc, uri, amountText, dateText, finalDate;
    boolean isModified = false;
    SimpleDateFormat sdf;
    Calendar now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DBHandler(this);
        v = findViewById(R.id.user_activity);

        vc = getIntent().getStringExtra("vc");

        nameText = (TextView) findViewById(R.id.name_text);
        phoneText = (TextView) findViewById(R.id.phone_text);
        addressText = (TextView) findViewById(R.id.address_text);
        cafText = (TextView) findViewById(R.id.caf_text);
        vcText = (TextView) findViewById(R.id.vc_text);
        installDateText = (TextView) findViewById(R.id.install_date);
        active = (CheckBox) findViewById(R.id.active_box);

        fabOk = (FloatingActionButton) findViewById(R.id.ok);
        fabEdit = (FloatingActionButton) findViewById(R.id.edit);
        fabTrans = (FloatingActionButton) findViewById(R.id.transactions);

        setValues();

        amountField = (EditText) findViewById(R.id.amount);
        amountField.setText(R.string.defAmount);

        dateField = (EditText) findViewById(R.id.date_paid);
        now = Calendar.getInstance();
        Date date = now.getTime();
        day = now.get(Calendar.DAY_OF_MONTH);
        month = now.get(Calendar.MONTH);
        year = now.get(Calendar.YEAR);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        dateField.setText(dateFormat.format(date));

        dateField.setOnClickListener(view -> showDatePicker());
        dateField.setOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            if (hasFocus) {
                showDatePicker();
            }
        });

        uri = "tel:" + phoneText.getText().toString().trim();
        phoneText.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                //TODO Handle No Permission
                return;
            }
            startActivity(intent);
        });

        fabOk.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            amountText = amountField.getText().toString();
            dateText = dateField.getText().toString();

            if (!amountText.equals("")) {
                if (!dateText.equals("")) {
                    sdf = new SimpleDateFormat("HH-mm-ss", Locale.getDefault());
                    now = Calendar.getInstance();
                    finalDate = dateText + " " + sdf.format(now.getTime());
                    new saveTransaction(this).execute();
                } else {
                    Snackbar.make(view, "Pick a Date", Snackbar.LENGTH_SHORT)
                            .setAction("GO", view1 -> showDatePicker()).show();
                }
            } else {
                Snackbar.make(view, "Enter an amount", Snackbar.LENGTH_SHORT)
                        .setAction("GO", view12 -> amountField.requestFocus()).show();
            }
            isModified = true;
        });

        fabEdit.setOnClickListener(view -> {
            Intent i = new Intent(UserActivity.this, EditActivity.class);
            i.putExtra("vc", vcText.getText().toString().split(" ")[1]);
            startActivityForResult(i, EDIT_REQUEST);
            isModified = true;
        });

        fabTrans.setOnClickListener(view -> {
            Intent i = new Intent(UserActivity.this, TransactionActivity.class);
            i.putExtra("vc", vcText.getText().toString().split(" ")[1]);
            i.putExtra("username", nameText.getText());
            startActivity(i);
            isModified = true;
        });
    }

    public void setValues() {
        User user = db.getUser(vc);
        String foo = "CAF: " + user.getCaf();
        String bar = "VC: " + user.getVc();
        String far = "Installed On: " + user.getInstallDate();
        nameText.setText(user.getName());
        phoneText.setText(user.getPhone());
        addressText.setText(user.getAddress());
        cafText.setText(foo);
        vcText.setText(bar);
        installDateText.setText(far);
        if (user.getStatus())
            active.setChecked(true);
        else
            active.setChecked(false);
    }

    public void showDatePicker() {
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(this, (datePicker, i, i1, i2) -> {
            day = i2;
            month = i1;
            year = i;
            if (i1 + 1 <= 9)
                dateField.setText(i2 + "-0" + (i1 + 1) + "-" + i);
            else
                dateField.setText(i2 + "-" + (i1 + 1) + "-" + i);

        }, year, month, day);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.create();
        datePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        isModified = true;
        if (requestCode == EDIT_REQUEST) {
            if (resultCode == RESULT_DELETED) {
                setResult(RESULT_CHANGED);
                finish();
            } else {
                setValues();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isModified) {
            setResult(RESULT_CHANGED);
        }
        super.onBackPressed();
    }

    class saveTransaction extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private Activity mActivity;

        saveTransaction(UserActivity activity) {
            dialog = new ProgressDialog(activity);
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Adding Transaction...");
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Void doInBackground(Void... args) {
            db.addTransaction(new Transaction(vc, Integer.parseInt(amountText),
                    finalDate), false);
            return null;
        }

        protected void onPostExecute(Void result) {
            // do UI work here
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            showDialog();
        }
    }

    public static String formatDate(String date, String initDateFormat, String endDateFormat) throws ParseException {

        Date initDate = new SimpleDateFormat(initDateFormat, Locale.getDefault()).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat, Locale.getDefault());

        return formatter.format(initDate);
    }

    public void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog, null);

        TextView amount = (TextView) dialogView.findViewById(R.id.dialog_amount);
        TextView date = (TextView) dialogView.findViewById(R.id.dialog_date);

        amount.setText("Amount : " + Integer.parseInt(amountText));
        date.setText(dateText + " " + sdf.format(now.getTime()));

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Saved Transaction!!");
        //dialogBuilder.setMessage("Enter text below");
        dialogBuilder.setPositiveButton("Ok", (dialog, whichButton) -> {

        });
        dialogBuilder.setNegativeButton("Edit", (dialog, whichButton) -> {
            fabTrans.callOnClick();
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

    }
}

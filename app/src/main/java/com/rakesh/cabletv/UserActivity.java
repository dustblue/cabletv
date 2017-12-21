package com.rakesh.cabletv;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserActivity extends AppCompatActivity {

    public static final int EDIT_REQUEST_CODE = 21;
    DBHandler db;
    FloatingActionButton fabOk, fabEdit, fabTrans;
    TextView nameText, phoneText, addressText, cafText, vcText, installDateText;
    EditText amountField, dateField;
    int year, month, day;
    String vc, uri, amountText, dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DBHandler(this);

        vc = getIntent().getStringExtra("vc");

        nameText = (TextView) findViewById(R.id.name_text);
        phoneText = (TextView) findViewById(R.id.phone_text);
        addressText = (TextView) findViewById(R.id.address_text);
        cafText = (TextView) findViewById(R.id.caf_text);
        vcText = (TextView) findViewById(R.id.vc_text);
        installDateText = (TextView) findViewById(R.id.install_date);

        fabOk = (FloatingActionButton) findViewById(R.id.ok);
        fabEdit = (FloatingActionButton) findViewById(R.id.edit);
        fabTrans = (FloatingActionButton) findViewById(R.id.transactions);

        setValues();

        amountField = (EditText) findViewById(R.id.amount);
        amountField.setText(R.string.defAmount);

        dateField = (EditText) findViewById(R.id.date_paid);
        Calendar now = Calendar.getInstance();
        Date date = now.getTime();
        day = now.get(Calendar.DAY_OF_MONTH);
        month = now.get(Calendar.MONTH);
        year = now.get(Calendar.YEAR);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateField.setText(dateFormat.format(date));

        dateField.setOnClickListener(view -> showDatePicker());

        uri = "tel:" + phoneText.getText().toString().trim();
        phoneText.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                //TODO Handle Request Permission
                return;
            }
            startActivity(intent);
        });

        fabOk.setOnClickListener(view -> {
            amountText = amountField.getText().toString();
            dateText = dateField.getText().toString();

            if (!amountText.equals("")) {
                if (!dateText.equals("")) {
                    db.addTransaction(new Transaction(vc, amountText, dateText), Calendar.getInstance().getTime());
                } else {
                    Snackbar.make(view, "Pick a Date", Snackbar.LENGTH_SHORT)
                            .setAction("GO", view1 -> showDatePicker()).show();
                }
            } else {
                Snackbar.make(view, "Enter an amount", Snackbar.LENGTH_SHORT)
                        .setAction("GO", view12 -> amountField.requestFocus()).show();
            }

        });

        fabEdit.setOnClickListener(view -> {
            Intent i = new Intent(UserActivity.this, EditActivity.class);
            i.putExtra("vc", vcText.getText());
            startActivityForResult(i, EDIT_REQUEST_CODE);
        });

        fabTrans.setOnClickListener(view -> {
            Intent i = new Intent(UserActivity.this, TransactionActivity.class);
            i.putExtra("vc", vcText.getText());
            startActivity(i);
        });
    }

    public void setValues() {
        /*
        Dummy Data
        User user = new User();
        user.setName("Nithya");
        user.setPhone("9655768683");
        user.setAddress("73, Patel Street, Veeriampalayam");
        user.setCaf("CAF: ABCDEFG");
        user.setVc("VC: 12345R6");
        user.setInstallDate("Installed On: 01/12/17");
        */

        User user = db.getUser(vc);
        nameText.setText(user.getName());
        phoneText.setText(user.getPhone());
        addressText.setText(user.getAddress());
        cafText.setText(user.getCaf());
        vcText.setText(user.getVc());
        installDateText.setText(user.getInstallDate());

    }

    public void showDatePicker() {
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                day = i2;
                month = i1;
                year = i;
                dateField.setText(i2 + "/" + i1 + 1 + "/" + 1);
            }
        }, year, month, day);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                setValues();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }

}

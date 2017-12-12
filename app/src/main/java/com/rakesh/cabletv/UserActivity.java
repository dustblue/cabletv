package com.rakesh.cabletv;

import android.Manifest;
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
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserActivity extends AppCompatActivity {

    DBHandler db;
    FloatingActionButton fabOk, fabEdit;
    TextView nameText, phoneText, addressText, cafText, vcText, installDateText;
    EditText amountField, dateField;
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DBHandler(this);

        String vc = getIntent().getStringExtra("vc");
        User user = db.getUser(vc);

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

        nameText = (TextView) findViewById(R.id.name_text);
        nameText.setText(user.getName());

        phoneText = (TextView) findViewById(R.id.phone_text);
        phoneText.setText(user.getPhone());
        final String uri = "tel:" + user.getPhone().trim();

        phoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //TODO Handle Request Permission
                    return;
                }
                startActivity(intent);
            }
        });

        addressText = (TextView) findViewById(R.id.address_text);
        addressText.setText(user.getAddress());

        cafText = (TextView) findViewById(R.id.caf_text);
        cafText.setText(user.getCaf());

        vcText = (TextView) findViewById(R.id.vc_text);
        vcText.setText(user.getVc());

        installDateText = (TextView) findViewById(R.id.install_date);
        installDateText.setText(user.getInstallDate());

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

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        fabOk = (FloatingActionButton) findViewById(R.id.ok);
        fabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!amountField.getText().toString().trim().equals("")) {
                    if(!dateField.getText().toString().trim().equals("")) {
                        //TODO Update DB
                    } else {
                        Snackbar.make(view, "Pick a Date", Snackbar.LENGTH_SHORT)
                                .setAction("GO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showDatePicker();
                                    }
                                }).show();
                    }
                } else {
                    Snackbar.make(view, "Enter an amount", Snackbar.LENGTH_SHORT)
                            .setAction("GO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    amountField.requestFocus();
                                }
                            }).show();
                }

            }
        });

        fabEdit = (FloatingActionButton) findViewById(R.id.edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Edit yet to be implemented", Snackbar.LENGTH_LONG)
                        .setAction("OK", null).show();
            }
        });

    }

    public void showDatePicker() {
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                day = i2;
                month = i1;
                year = i;
                dateField.setText(i2 + "/" + i1+1 + "/" + 1);
            }
        }, year, month, day);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

}

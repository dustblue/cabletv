package com.rakesh.cabletv;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CollectionActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    View parent;
    EditText startTime, endTime, startDate, endDate;
    TextView total, totalText;
    Calendar chosenStartTime, chosenEndTime;
    DBHandler db;
    boolean firstTimeFlag;
    int mStartMinute, mEndMinute, mStartHour, mEndHour, mStartYear,
            mStartMonth, mStartDay, mEndYear, mEndMonth, mEndDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        firstTimeFlag = true;

        Calendar now = Calendar.getInstance();
        mStartMinute = now.get(Calendar.MINUTE);
        mEndMinute = now.get(Calendar.MINUTE);
        mStartHour = now.get(Calendar.HOUR_OF_DAY);
        mEndHour = now.get(Calendar.HOUR_OF_DAY);
        mStartDay = now.get(Calendar.DAY_OF_MONTH);
        mEndDay = now.get(Calendar.DAY_OF_MONTH);
        mStartMonth = now.get(Calendar.MONTH);
        mEndMonth = now.get(Calendar.MONTH);
        mStartYear = now.get(Calendar.YEAR);
        mEndYear = now.get(Calendar.YEAR);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_check);
        fab.setOnClickListener(this);

        parent = findViewById(R.id.collection);
        startTime = (EditText) findViewById(R.id.start_time);
        endTime = (EditText) findViewById(R.id.end_time);
        startDate = (EditText) findViewById(R.id.start_date);
        endDate = (EditText) findViewById(R.id.end_date);
        total = (TextView) findViewById(R.id.total);
        totalText = (TextView) findViewById(R.id.total_amount);

        startTime.setInputType(InputType.TYPE_NULL);
        endTime.setInputType(InputType.TYPE_NULL);
        startDate.setInputType(InputType.TYPE_NULL);
        endDate.setInputType(InputType.TYPE_NULL);

        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);

        startTime.setOnFocusChangeListener(this);
        endTime.setOnFocusChangeListener(this);
        startDate.setOnFocusChangeListener(this);
        endDate.setOnFocusChangeListener(this);

        chosenStartTime = Calendar.getInstance();
        chosenEndTime = Calendar.getInstance();

    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        int id = v.getId();
        if (id == R.id.start_time) {
            showStartTimePickerDialog();
        } else if (id == R.id.end_time) {
            showEndTimePickerDialog();
        } else if (id == R.id.start_date) {
            showStartDatePickerDialog();
        } else if (id == R.id.end_date) {
            showEndDatePickerDialog();
        } else if (id == R.id.fab_check) {
            validate();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        if (id == R.id.start_time && hasFocus) {
            showStartTimePickerDialog();
        } else if (id == R.id.end_time && hasFocus) {
            showEndTimePickerDialog();
        } else if (id == R.id.start_date && hasFocus) {
            showStartDatePickerDialog();
        } else if (id == R.id.end_date && hasFocus) {
            showEndDatePickerDialog();
        }
    }

    public void showStartDatePickerDialog() {
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            mStartYear = year;
            mStartMonth = monthOfYear;
            mStartDay = dayOfMonth;

            startDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            chosenStartTime.set(year, monthOfYear, dayOfMonth);

            if (firstTimeFlag) {
                endDate.requestFocus();
            }

        }, mStartYear, mStartMonth, mStartDay);
        //FIXME Title not working
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    public void showEndDatePickerDialog() {
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            mEndYear = year;
            mEndMonth = monthOfYear;
            mEndDay = dayOfMonth;

            endDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            chosenEndTime.set(year, monthOfYear, dayOfMonth);

            if (firstTimeFlag) {
                startTime.requestFocus();
            }

        }, mEndYear, mEndMonth, mEndDay);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    public void showStartTimePickerDialog() {
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(CollectionActivity.this, (timePicker, selectedHour, selectedMinute) -> {

            mStartHour = selectedHour;
            mStartMinute = selectedMinute;
            //todo Optimize

            if (mStartHour > 12) {
                if (selectedMinute / 10 == 0) {
                    startTime.setText((selectedHour - 12) + ":0" + selectedMinute + " PM");
                } else {
                    startTime.setText((selectedHour - 12) + ":" + selectedMinute + " PM");
                }
            } else if (mEndHour < 12) {
                if (selectedMinute / 10 == 0) {
                    startTime.setText(selectedHour + ":0" + selectedMinute + " AM");
                } else {
                    startTime.setText(selectedHour + ":" + selectedMinute + " AM");
                }
            } else {
                if (selectedMinute / 10 == 0) {
                    endTime.setText(selectedHour + ":0" + selectedMinute + " PM");
                } else {
                    endTime.setText(selectedHour + ":" + selectedMinute + " PM");
                }
            }

            chosenStartTime.set(Calendar.HOUR, selectedHour);
            chosenStartTime.set(Calendar.MINUTE, selectedMinute);
            chosenStartTime.set(Calendar.SECOND, 0);

            if (firstTimeFlag) {
                endTime.requestFocus();
            }

        }, mStartHour, mStartMinute, false);
        mTimePicker.setTitle("Start Time");
        mTimePicker.show();
    }

    public void showEndTimePickerDialog() {
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(CollectionActivity.this, (timePicker, selectedHour, selectedMinute) -> {

            mEndHour = selectedHour;
            mEndMinute = selectedMinute;
            //todo Optimize
            if (mEndHour > 12) {
                if (selectedMinute / 10 == 0) {
                    endTime.setText((selectedHour - 12) + ":0" + selectedMinute + " PM");
                } else {
                    endTime.setText((selectedHour - 12) + ":" + selectedMinute + " PM");
                }
            } else if (mEndHour < 12) {
                if (selectedMinute / 10 == 0) {
                    endTime.setText(selectedHour + ":0" + selectedMinute + " AM");
                } else {
                    endTime.setText(selectedHour + ":" + selectedMinute + " AM");
                }
            } else {
                if (selectedMinute / 10 == 0) {
                    endTime.setText(selectedHour + ":0" + selectedMinute + " PM");
                } else {
                    endTime.setText(selectedHour + ":" + selectedMinute + " PM");
                }
            }

            chosenEndTime.set(Calendar.HOUR, selectedHour);
            chosenEndTime.set(Calendar.MINUTE, selectedMinute);
            chosenEndTime.set(Calendar.SECOND, 0);

        }, mEndHour, mEndMinute, false);
        mTimePicker.setTitle("End Time");
        mTimePicker.show();
        firstTimeFlag = false;
    }

    public void validate() {
        if (!startTime.getText().toString().equals("")) {
            if (!startDate.getText().toString().equals("")) {
                if (!endTime.getText().toString().equals("")) {
                    if (!endDate.getText().toString().equals("")) {
                        if (checkIfEndLessThanStart()) {
                            checkCollection();
                        } else
                            Snackbar.make(parent, "End Time is before Start Time!", Snackbar.LENGTH_SHORT).show();
                    } else endDate.callOnClick();
                } else endTime.callOnClick();
            } else startDate.callOnClick();
        } else startTime.callOnClick();
    }

    public boolean checkIfEndLessThanStart() {
        return chosenStartTime.getTimeInMillis() <= chosenEndTime.getTimeInMillis();
    }

    public void checkCollection() {
        db = new DBHandler(this);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss", Locale.getDefault());
        int amount = db.getCollection(sdf.format(chosenEndTime.getTime()),
                sdf.format(chosenStartTime.getTime()));
        total.setText(String.valueOf(amount));
    }

}

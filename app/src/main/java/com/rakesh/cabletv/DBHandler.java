package com.rakesh.cabletv;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    public static final String TAG = "com.rakesh.cabletv";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "CableDB";

    private static final String USERS_TABLE = "Users";

    private static final String KEY_VC = "vc"; //Foreign Key for Transactions
    private static final String KEY_CAF = "caf";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_CLUSTER = "cluster";
    private static final String KEY_INSTALL_DATE = "installDate";
    private static final String KEY_STATUS = "status";

    private static final String TRANSACTIONS_TABLE = "Transactions";

    private static final String KEY_ID = "id"; //Foreign Key for Log
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DATE = "date";

    private static final String LOG_TABLE = "Log";

    private static final String KEY_LOGDATE = "logdate";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + USERS_TABLE + "("
                + KEY_VC + " TEXT PRIMARY KEY," + KEY_CAF + " TEXT,"
                + KEY_NAME + " TEXT," + KEY_PHONE + " TEXT,"
                + KEY_ADDRESS + " TEXT," + KEY_CLUSTER + " TEXT,"
                + KEY_INSTALL_DATE + " TEXT," + KEY_STATUS + " BOOLEAN" + ")";

        String CREATE_TRANS_TABLE = "CREATE TABLE IF NOT EXISTS " + TRANSACTIONS_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_VC + " TEXT,"
                + KEY_AMOUNT + " INTEGER," + KEY_DATE + " DATETIME, "
                + "FOREIGN KEY (" + KEY_VC + ") REFERENCES " + USERS_TABLE
                + "(" + KEY_VC + ")" + ")";

        String CREATE_LOG_TABLE = "CREATE TABLE IF NOT EXISTS " + LOG_TABLE + "("
                + KEY_LOGDATE + " DATETIME PRIMARY KEY, " + KEY_ID + " INTEGER, "
                + "FOREIGN KEY (" + KEY_ID + ") REFERENCES "
                + TRANSACTIONS_TABLE + "(" + KEY_ID + ")" + ")";

        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_TRANS_TABLE);
        db.execSQL(CREATE_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTIONS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE);
        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VC, user.getVc());
        values.put(KEY_CAF, user.getCaf());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_PHONE, user.getPhone());
        values.put(KEY_ADDRESS, user.getAddress());
        values.put(KEY_CLUSTER, user.getCluster());
        values.put(KEY_INSTALL_DATE, user.getInstallDate());
        values.put(KEY_STATUS, user.getStatus());

        db.insert(USERS_TABLE, null, values);
        db.close();
    }

    public void updateUser(User user, String vc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VC, user.getVc());
        values.put(KEY_CAF, user.getCaf());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_PHONE, user.getPhone());
        values.put(KEY_ADDRESS, user.getAddress());
        values.put(KEY_CLUSTER, user.getCluster());
        values.put(KEY_INSTALL_DATE, user.getInstallDate());
        values.put(KEY_STATUS, user.getStatus());

        db.update(USERS_TABLE, values, "vc = " + "\"" + vc + "\"", null);
        db.close();

    }

    public User getUser(String vc) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USERS_TABLE + " WHERE "
                + KEY_VC + " is " + "\"" + vc + "\"", null);

        User user = new User();
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            user.setVc(cursor.getString(0));
            user.setCaf(cursor.getString(1));
            user.setName(cursor.getString(2));
            user.setPhone(cursor.getString(3));
            user.setAddress(cursor.getString(4));
            user.setCluster(cursor.getString(5));
            user.setInstallDate(cursor.getString(6));
            user.setStatus((cursor.getInt(7) > 0));
        }

        cursor.close();
        db.close();

        return user;
    }

    public List<User> getAllUsers(Boolean all) {
        List<User> usersList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        //TODO if all = false then get unpaid

        Cursor cursor = db.rawQuery("SELECT * FROM " + USERS_TABLE, null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();

                user.setVc(cursor.getString(0));
                user.setCaf(cursor.getString(1));
                user.setName(cursor.getString(2));
                user.setPhone(cursor.getString(3));
                user.setAddress(cursor.getString(4));
                user.setCluster(cursor.getString(5));
                user.setInstallDate(cursor.getString(6));
                user.setStatus((cursor.getInt(7) > 0));

                //debug

                for (int i = 0; i < 8; i++)
                    Log.e(TAG, cursor.getString(i));
                Log.e(TAG, "\n");

                usersList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return usersList;
    }

    public List<User> getUsersByCluster(String cluster, Boolean all) {
        List<User> usersList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        //TODO if all = false then get unpaid
        String selectQuery = "SELECT * FROM " + USERS_TABLE + " WHERE cluster is " + "\"" + cluster + "\"";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();

                user.setVc(cursor.getString(0));
                user.setCaf(cursor.getString(1));
                user.setName(cursor.getString(2));
                user.setPhone(cursor.getString(3));
                user.setAddress(cursor.getString(4));
                user.setCluster(cursor.getString(5));
                user.setInstallDate(cursor.getString(6));
                user.setStatus((cursor.getInt(7) > 0));

                usersList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return usersList;
    }

    public String[] getClusters() {
        String[] clusters = new String[5];
        //TODO Trim String Array.
        int i = 0;
        String select = "SELECT DISTINCT " + KEY_CLUSTER + " FROM " + USERS_TABLE;

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (Cursor cursor = db.rawQuery(select, null)) {
                if (cursor.moveToFirst()) {
                    List valid = Arrays.asList(clusters);
                    do {
                        String temp = cursor.getString(0);
                        if (!valid.contains(temp.toLowerCase()) && !temp.equals("")) {
                                clusters[i] = temp;
                            i++;
                        }
                    } while (cursor.moveToNext());
                }
            }
        }

        return clusters;
    }

    public void addTransaction(Transaction t, Date date) {
        SQLiteDatabase db = this.getWritableDatabase();

        updateLog(date);

        ContentValues values = new ContentValues();
        values.put(KEY_VC, t.getVc());
        values.put(KEY_AMOUNT, t.getAmount());
        values.put(KEY_DATE, t.getDateTime());

        db.insert(TRANSACTIONS_TABLE, null, values);
        db.close();
    }

    public void updateTransaction(Transaction t) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT, t.getAmount());
        values.put(KEY_DATE, t.getDateTime());

        db.update(TRANSACTIONS_TABLE, values, "vc = " + "\"" + t.getVc() + "\"", null);
        db.close();
    }

    public void updateLog(Date date) {
        //TODO UpdateLog
    }
}

package com.rakesh.cabletv;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    public static final String TAG = "com.rakesh.cabletv";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "CableDB";

    private static final String TABLE_NAME = "Users";

    private static final String KEY_VC = "vc";
    private static final String KEY_CAF = "caf";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_CLUSTER = "cluster";
    private static final String KEY_INSTALL_DATE = "installDate";
    private static final String KEY_STATUS = "status";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_VC + " TEXT PRIMARY KEY," + KEY_CAF + " TEXT,"
                + KEY_NAME + " TEXT," + KEY_PHONE + " TEXT,"
                + KEY_ADDRESS + " TEXT," + KEY_CLUSTER + " TEXT,"
                + KEY_INSTALL_DATE + " TEXT," + KEY_STATUS + " BOOLEAN" + ")";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
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

        db.insert(TABLE_NAME, null, values);
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

        db.update(TABLE_NAME, values, "vc = " + "\"" + vc + "\"", null);
        db.close();

    }

    public List<User> getAllEvents() {
        List<User> usersList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
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

    public User getUser(String vc) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
                + KEY_VC + " is " + vc, null);

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

    public List<User> getUsersByCluster(String cluster) {
        List<User> usersList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE cluster is " + "\"" + cluster + "\"";
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
        String[] clusters = new String[15];
        int i = 0;
        String select = "SELECT DISTINCT " + KEY_CLUSTER + " FROM " + TABLE_NAME;

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (Cursor cursor = db.rawQuery(select, null)) {
                if (cursor.moveToFirst()) {
                    List valid = Arrays.asList(clusters);
                    do {
                        String temp = cursor.getString(0);
                        if (valid.contains(temp.toLowerCase())) {
                            // is valid
                            i--;
                        } else {
                            // not valid
                            if (temp.equals(""))
                                i--;
                            else
                                clusters[i] = temp.toLowerCase();
                        }
                        i++;
                    } while (cursor.moveToNext());
                }
            }
        }

        return clusters;
    }
}

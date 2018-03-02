package com.rakesh.cabletv;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
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
    private static final String KEY_NEW_DATE = "newdate";

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
                + KEY_ADDRESS + " TEXT," + KEY_CLUSTER + " TEXT COLLATE NOCASE,"
                + KEY_INSTALL_DATE + " TEXT," + KEY_STATUS + " BOOLEAN" + ")";

        String CREATE_TRANS_TABLE = "CREATE TABLE IF NOT EXISTS " + TRANSACTIONS_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_VC + " TEXT,"
                + KEY_AMOUNT + " INTEGER," + KEY_DATE + " DATETIME, "
                + "FOREIGN KEY (" + KEY_VC + ") REFERENCES " + USERS_TABLE
                + "(" + KEY_VC + ")" + ")";

        String CREATE_LOG_TABLE = "CREATE TABLE IF NOT EXISTS " + LOG_TABLE + "("
                + KEY_LOGDATE + " DATETIME DEFAULT (datetime('now','localtime')) PRIMARY KEY, " + KEY_ID + " INTEGER, "
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

        db.update(USERS_TABLE, values, KEY_VC + " = " + "\"" + vc + "\"", null);
        db.close();

    }

    public void deleteUser(String vc) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(USERS_TABLE, KEY_VC + " = ?", new String[]{vc});
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

    public String getLastPaid(String vc) {
        SQLiteDatabase db = this.getWritableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TRANSACTIONS_TABLE + " WHERE " +
                KEY_VC + " = " + "\"" + vc + "\"" + " ORDER BY " + KEY_NEW_DATE + " DESC", null)) {
            if (cursor.moveToFirst())
                return cursor.getString(3);

        } catch (Exception e) {
            return "Last Paid N/A";
        }
        return "Last Paid N/A";
    }

    public boolean checkIfPaid(String lastPaidDate) {
        boolean flag = false;
        Calendar now = Calendar.getInstance();
        try {
            String[] dates = lastPaidDate.split("-");
            int month;
            if (dates[1].length() > 1)
                month = (dates[1].charAt(0) - 48) * 10 + (dates[1].charAt(1) - 48);
            else
                month = (dates[1].charAt(0) - 48);
            int year = Integer.parseInt(dates[2].split(" ")[0]);
            int nowMonth = now.get(Calendar.MONTH) + 1;
            int nowYear = now.get(Calendar.YEAR);
            if (month >= nowMonth && year >= nowYear)
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public List<UserEntry> getAllUsers() {
        List<UserEntry> usersList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String select = "SELECT * FROM " + USERS_TABLE + " ORDER BY " + KEY_NAME;
        try (Cursor cursor = db.rawQuery(select, null)) {
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

                    /*debug

                    for (int i = 0; i < 8; i++)
                        Log.e(TAG, cursor.getString(i));
                    Log.e(TAG, "\n");

                    */

                    UserEntry userEntry = new UserEntry();
                    userEntry.setUser(user);
                    String lp = getLastPaid(cursor.getString(0));
                    userEntry.setLastPaidDate(lp);
                    if (lp.equals("Last Paid N/A"))
                        userEntry.setIfPaid(false);
                    else
                        userEntry.setIfPaid(checkIfPaid(lp));

                    usersList.add(userEntry);

                } while (cursor.moveToNext());
            }
        }
        db.close();

        return usersList;
    }

    public List<UserEntry> getUsersByCluster(String cluster) {
        List<UserEntry> usersList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + USERS_TABLE + " WHERE cluster is " + "\"" + cluster + "\""
                + " ORDER BY " + KEY_NAME;
        try (Cursor cursor = db.rawQuery(selectQuery, null)) {

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

                    UserEntry userEntry = new UserEntry();
                    userEntry.setUser(user);
                    String lp = getLastPaid(cursor.getString(0));
                    userEntry.setLastPaidDate(lp);
                    if (lp.equals("Last Paid N/A"))
                        userEntry.setIfPaid(false);
                    else
                        userEntry.setIfPaid(checkIfPaid(lp));

                    usersList.add(userEntry);

                } while (cursor.moveToNext());
            }

        }
        db.close();

        return usersList;
    }

    public List<UserEntry> getInactiveUsers() {
        List<UserEntry> usersList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String select = "SELECT * FROM " + USERS_TABLE + " WHERE " + KEY_STATUS
                + " = 0 ORDER BY " + KEY_NAME;
        try (Cursor cursor = db.rawQuery(select, null)) {
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

                    UserEntry userEntry = new UserEntry();
                    userEntry.setUser(user);
                    String temp = getLastPaid(cursor.getString(0));
                    userEntry.setLastPaidDate(temp);
                    if (temp.equals("Last Paid N/A"))
                        userEntry.setIfPaid(false);
                    else
                        userEntry.setIfPaid(checkIfPaid(temp));

                    usersList.add(userEntry);

                } while (cursor.moveToNext());
            }
        }
        db.close();

        return usersList;
    }

    public String[] getClusters() {
        List<String> clusters = new ArrayList<>();
        String temp;
        String select = "SELECT DISTINCT " + KEY_CLUSTER + " FROM " + USERS_TABLE;

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (Cursor cursor = db.rawQuery(select, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        temp = cursor.getString(0);
                        boolean flag = false;
                        for (String s : clusters) {
                            if (temp.equalsIgnoreCase(s)) {
                                flag = true;
                            }
                        }
                        if (!flag)
                            clusters.add(temp);
                    } while (cursor.moveToNext());
                }
            }
        }

        String[] cls = new String[clusters.size()];
        clusters.toArray(cls);
        return cls;

    }

    public void addTransaction(Transaction t, boolean restoreFlag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VC, t.getVc());
        values.put(KEY_AMOUNT, t.getAmount());
        values.put(KEY_DATE, t.getDateTime());
        values.put(KEY_NEW_DATE, t.getNewDateTime());

        db.insert(TRANSACTIONS_TABLE, null, values);

        if (!restoreFlag) {
            try (Cursor cursor = db.rawQuery("SELECT LAST_INSERT_ROWID() FROM " + TRANSACTIONS_TABLE, null)) {
                cursor.moveToFirst();
                int i = (cursor.getInt(0));
                updateLog(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        db.close();
    }

    public void deleteTransaction(Transaction t) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TRANSACTIONS_TABLE, KEY_VC + " = ? AND " + KEY_DATE + " = ?"
                , new String[]{t.getVc(), t.getDateTime()});
        db.close();
    }

    public List<Transaction> getTransactionsByUser(String vc) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TRANSACTIONS_TABLE + " WHERE " +
                KEY_VC + " = " + "\"" + vc + "\"" + " ORDER BY " + KEY_NEW_DATE + " DESC", null)) {
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.setVc(cursor.getString(1));
                    transaction.setAmount(cursor.getInt(2));
                    transaction.setDateTime(cursor.getString(3));

                    transactions.add(transaction);
                } while (cursor.moveToNext());
            }

        }
        db.close();

        return transactions;
    }

    public void updateLog(int i) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("INSERT INTO " + LOG_TABLE + " ( " + KEY_ID + " ) VALUES ( " + i + " )"); //wtf
        db.close();
    }

    public List<TransactionEntry> getLog() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<TransactionEntry> entries = new ArrayList<>();
        String s;

        try (Cursor cursor = db.rawQuery("SELECT * FROM " + LOG_TABLE
                + " ORDER BY " + KEY_LOGDATE + " DESC", null)) {

            if (cursor.moveToFirst()) {
                do {
                    s = cursor.getString(0);
                    int i = cursor.getInt(1);

                    try (Cursor c1 = db.rawQuery("SELECT * FROM " + TRANSACTIONS_TABLE
                            + " WHERE " + KEY_ID + " = " + i, null)) {
                        if (c1.moveToFirst()) {
                            Transaction transaction = new Transaction();
                            transaction.setVc(c1.getString(1));
                            transaction.setAmount(c1.getInt(2));
                            transaction.setDateTime(s);

                            try (Cursor c2 = db.rawQuery("SELECT " + KEY_NAME + " FROM " + USERS_TABLE + ", "
                                    + TRANSACTIONS_TABLE + " WHERE " + TRANSACTIONS_TABLE + "." + KEY_VC
                                    + " = " + USERS_TABLE + "." + KEY_VC
                                    + " AND " + KEY_ID + " is " + i, null)) {
                                if (c2.moveToFirst()) {
                                    TransactionEntry transactionEntry = new TransactionEntry();
                                    transactionEntry.setTransaction(transaction);
                                    transactionEntry.setUserName(c2.getString(0));
                                    entries.add(transactionEntry);
                                }
                            }
                        }
                    }

                } while (cursor.moveToNext());
            }
        }
        db.close();

        return entries;
    }

    public void clearUp() {
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + USERS_TABLE, null)) {
            if (cursor.moveToFirst()) {
                do {

                    String name = cursor.getString(2);
                    char c = name.charAt(name.length() - 1);
                    if (Character.getNumericValue(c) == -1 && !Character.isWhitespace(c)) {
                        ContentValues values = new ContentValues();
                        values.put(KEY_NAME, name.substring(0, name.length() - 1));

                        db.update(USERS_TABLE, values, KEY_VC + " =?"
                                , new String[]{cursor.getString(0)});
                    }
                } while (cursor.moveToNext());
            }
        }

    }

    public List<String[]> backupUsers() {
        List<String[]> usersList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT * FROM " + USERS_TABLE + " WHERE " + KEY_VC + " NOT NULL", null)) {
            if (cursor.moveToFirst()) {
                do {
                    String[] user = new String[]{
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(6),
                            cursor.getString(7)
                    };
                    usersList.add(user);
                } while (cursor.moveToNext());
            }
        }

        db.close();
        return usersList;
    }

    public List<String[]> backupTransactions() {
        List<String[]> transList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT * FROM " + LOG_TABLE
                + " ORDER BY " + KEY_LOGDATE + " DESC", null)) {

            if (cursor.moveToFirst()) {
                do {
                    int i = cursor.getInt(1);

                    try (Cursor c1 = db.rawQuery("SELECT * FROM " + TRANSACTIONS_TABLE
                            + " WHERE " + KEY_ID + " = " + i, null)) {

                        if (c1.moveToFirst()) {

                            try (Cursor c2 = db.rawQuery("SELECT " + KEY_NAME + " FROM " + USERS_TABLE + ", "
                                    + TRANSACTIONS_TABLE + " WHERE " + TRANSACTIONS_TABLE + "." + KEY_VC
                                    + " = " + USERS_TABLE + "." + KEY_VC
                                    + " AND " + KEY_ID + " is " + i, null)) {
                                if (c2.moveToFirst()) {

                                    String[] user = new String[]{
                                            c1.getString(1),
                                            c2.getString(0),
                                            c1.getString(2),
                                            c1.getString(3)
                                    };
                                    transList.add(user);

                                }
                            }
                        }
                    }
                }
                while (cursor.moveToNext());
            }
        }

        db.close();
        return transList;
    }

    public int getCollection(String start, String end) {
        SQLiteDatabase db = this.getWritableDatabase();
        int sum = 0;
        Cursor cursor = db.rawQuery("SELECT " + KEY_AMOUNT + " FROM " + TRANSACTIONS_TABLE
                + " WHERE " + KEY_DATE + " BETWEEN " + "\"" + start + "\""
                + " AND " + "\"" + end + "\"" + " AND " + "EXISTS (SELECT "
                + KEY_VC + " FROM " + USERS_TABLE + " WHERE " + USERS_TABLE + "." + KEY_VC
                + " = " + TRANSACTIONS_TABLE + "." + KEY_VC + ")", null);
        if (cursor.moveToFirst()) {
            do {
                sum += cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return sum;
    }

    public void addColumn() {
        SQLiteDatabase db = this.getWritableDatabase();

        String ALTER_TRANSACTIONS_TABLE = "ALTER TABLE " + TRANSACTIONS_TABLE + " ADD "
                + KEY_NEW_DATE + " DATETIME";

        try {
            db.execSQL(ALTER_TRANSACTIONS_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reformatDates() {
        SQLiteDatabase db = this.getWritableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TRANSACTIONS_TABLE, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Transaction t = new Transaction();
                    cursor.getInt(0);
                    t.setDateTime(cursor.getString(3));


                    ContentValues values = new ContentValues();
                    values.put(KEY_NEW_DATE, t.getNewDateTime());

                    db.update(TRANSACTIONS_TABLE, values
                            , KEY_ID + " = " + cursor.getInt(0), null);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

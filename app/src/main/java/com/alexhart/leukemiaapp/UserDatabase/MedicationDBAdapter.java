package com.alexhart.leukemiaapp.UserDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

public class MedicationDBAdapter {
    //setup fields for database table
    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DOSE = "dose";
    public static final String KEY_FREQUENCY = "freq";

    public static final String DATABASE_TABLE = "medicationdata";
    String TAG = "MedicationDBAdapter";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DBAdapter.DATABASE_NAME, null, DBAdapter.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public MedicationDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public MedicationDBAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.mDbHelper.close();
    }

    public long createMedicationData(String name, double dose, double freq){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_DOSE, dose);
        initialValues.put(KEY_FREQUENCY, freq);
        Log.d(TAG, "data created");

        return this.mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteUserInfoRow(long rowId) {

        return this.mDb.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + rowId, null) > 0; //$NON-NLS-1$
    }

    public void deleteAll() {
        Cursor c = getAllUserInfo();
        long rowId = c.getColumnIndexOrThrow(KEY_ROW_ID);
        if (c.moveToFirst()) {
            do {
                deleteUserInfoRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    public Cursor getAllUserInfo() {

        return this.mDb.query(DATABASE_TABLE, new String[] { KEY_ROW_ID,
                KEY_NAME, KEY_DOSE, KEY_FREQUENCY}, null, null, null, null, null);
    }

    public Cursor getMedicationRow(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROW_ID, KEY_NAME, KEY_DOSE,
                                KEY_FREQUENCY},
                        KEY_ROW_ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateMedicationData(long rowId, String name, String username,
                                        String password){
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_DOSE, username);
        args.put(KEY_FREQUENCY, password);

        return this.mDb.update(DATABASE_TABLE, args, KEY_ROW_ID + "=" + rowId, null) >0;
    }
}

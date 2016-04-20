package com.alexhart.leukemiaapp.UserDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by Alex on 8/28/2015.
 */
public class WaterDataDBAdapter {

    //setup fields for database table
    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_IN = "in";
    public static final String KEY_OUT = "out";
    public static final String KEY_DIF = "dif";

    public static final String DATABASE_TABLE = "waterdata";
    String TAG = "WaterDataDBAdapter";

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

    public WaterDataDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the UserData database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException
     *             if the database could be neither opened or created
     */
    public WaterDataDBAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * close return type: void
     */
    public void close() {
        this.mDbHelper.close();
    }

    /**
     * Create a new row of user data. If the data is successfully created return the new
     * rowId for the data, otherwise return a -1 to indicate failure.
     *
     * @param date
     * @param in
     * @param out
     * @return rowId or -1 if failed
     */
    public long createWaterData(String date, double in, double out){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_IN, in);
        initialValues.put(KEY_OUT, out);
        //todo dif method
        initialValues.put(KEY_DIF, 1);

        return this.mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the user data with the given rowId
     *
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteWaterData(long rowId) {

        return this.mDb.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Delete the user data with the given rowId
     */
    public void deleteAll() {
        Cursor c = getAllWaterData();
        long rowId = c.getColumnIndexOrThrow(KEY_ROW_ID);
        if (c.moveToFirst()) {
            do {
                deleteWaterData(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    /**
     * Return a Cursor over the list of all data in the database
     *
     * @return Cursor over all of the data
     */
    public Cursor getAllWaterData() {

        return this.mDb.query(DATABASE_TABLE, new String[] { KEY_ROW_ID,
                KEY_DATE, KEY_IN, KEY_OUT, KEY_DIF}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the data that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching data, if found
     * @throws SQLException if datda  could not be found/retrieved
     */
    public Cursor getWaterDataRow(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROW_ID, KEY_DATE, KEY_IN, KEY_OUT,
                                KEY_DIF},
                        KEY_ROW_ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getWaterDataColumn(String[] colName) throws SQLException {
        Cursor mCursor = mDb.query(DATABASE_TABLE, colName, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return  mCursor;
    }

    /**
     * Update the user data.
     *
     * @param rowId
     * @param date
     * @param in
     * @param out
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateWaterData(long rowId, String date, double in,
                                   double out){
        ContentValues args = new ContentValues();
        args.put(KEY_DATE, date);
        args.put(KEY_IN, in);
        args.put(KEY_OUT, out);

        return this.mDb.update(DATABASE_TABLE, args, KEY_ROW_ID + "=" + rowId, null) >0;
    }
}

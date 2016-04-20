package com.alexhart.leukemiaapp.UserDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * database class to handle setting up the database with separate tables
 * uses internal database helper class.
 */

public class DBAdapter {

    public static final String DATABASE_NAME = "Users";

    public static final int DATABASE_VERSION = 1;

    //create different tables
    private static final String CREATE_TABLE_WATER_DATA =
            "create table " + WaterDataDBAdapter.DATABASE_TABLE
                    + " (" + WaterDataDBAdapter.KEY_ROW_ID + " integer primary key autoincrement, "
                    + WaterDataDBAdapter.KEY_DATE+ " TEXT, "
                    + WaterDataDBAdapter.KEY_IN+ " DOUBLE, "
                    + WaterDataDBAdapter.KEY_OUT+ " DOUBLE, "
                    + WaterDataDBAdapter.KEY_DIF+ " DOUBLE" + ");";

    private static final String CREATE_TABLE_MEDICATION_INFO =
            "create table " + MedicationDBAdapter.DATABASE_TABLE
                    + " (" + MedicationDBAdapter.KEY_ROW_ID + " integer primary key autoincrement, "
                    + MedicationDBAdapter.KEY_NAME+ " TEXT, "
                    + MedicationDBAdapter.KEY_DOSE+ " DOUBLE, "
                    + MedicationDBAdapter.KEY_FREQUENCY+ " DOUBLE" + ");";

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        this.DBHelper = new DatabaseHelper(this.context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        private String TAG = "DBAdapter";
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_TABLE_WATER_DATA);
            db.execSQL(CREATE_TABLE_MEDICATION_INFO);
            Log.d(TAG, "Tables created!");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            // Adding any table mods to this guy here
        }
    }

    public DBAdapter open() throws SQLException
    {
        this.db = this.DBHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        this.DBHelper.close();
    }
}

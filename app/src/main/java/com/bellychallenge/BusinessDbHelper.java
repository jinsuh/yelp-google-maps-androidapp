package com.bellychallenge;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Suhong on 7/17/2016.
 */
public class BusinessDbHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Businesses.db";

    public static abstract class BusinessEntry implements BaseColumns {
        public static final String TABLE_NAME = "businesses";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_IMAGE = "pic";
        public static final String COLUMN_NAME_CLOSED = "closed";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BusinessEntry.TABLE_NAME + " (" +
                    BusinessEntry._ID + " INTEGER PRIMARY KEY," +
                    BusinessEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    BusinessEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    BusinessEntry.COLUMN_NAME_DISTANCE + DOUBLE_TYPE + COMMA_SEP +
                    BusinessEntry.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    BusinessEntry.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP +
                    BusinessEntry.COLUMN_NAME_IMAGE + BLOB_TYPE + COMMA_SEP +
                    BusinessEntry.COLUMN_NAME_CLOSED + INTEGER_TYPE +
                    ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BusinessEntry.TABLE_NAME;

    public BusinessDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}

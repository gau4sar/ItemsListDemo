package com.gaurav.officeitemsdemo.mvp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static io.fabric.sdk.android.Fabric.TAG;

public class SqlLiteDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ListItems.db";
    private static final int DATABASE_VERSION = 3;
    public static final String ITEM_TABLE_NAME = "items";
    public static final String ITEM_COLUMN_ID = "_id";
    public static final String ITEM_COLUMN_NAME = "name";
    public static final String ITEM_COLUMN_DESCRIPTION = "description";
    public static final String ITEM_COLUMN_IMAGE_PATH = "image";
    public static final String ITEM_COLUMN_COST = "cost";
    public static final String ITEM_COLUMN_LOCATION = "location";

    public SqlLiteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ITEM_TABLE_NAME + "(" +
                ITEM_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                ITEM_COLUMN_NAME + " TEXT, " +
                ITEM_COLUMN_DESCRIPTION + " TEXT, " +
                ITEM_COLUMN_IMAGE_PATH + " TEXT , " +
                ITEM_COLUMN_COST + " INTEGER, " +
                ITEM_COLUMN_LOCATION + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // onUpgrade is called whenever the database needs to be upgraded (i.e. when the DATABASE_VERSION has changed).
        // Drop the existing “items” table, and then call onCreate() to recreate it.

        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertItem(String name, String description, String imagePath, int cost, String location) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_COLUMN_NAME, name);
        contentValues.put(ITEM_COLUMN_DESCRIPTION, description);
        contentValues.put(ITEM_COLUMN_IMAGE_PATH, imagePath);
        contentValues.put(ITEM_COLUMN_COST, cost);
        contentValues.put(ITEM_COLUMN_LOCATION, location);
        db.insert(ITEM_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean updateItem(Integer id, String name, String description, String imagePath, int cost, String location) {
        Log.d(TAG, "Update Item at position : " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_COLUMN_NAME, name);
        contentValues.put(ITEM_COLUMN_DESCRIPTION, description);
        contentValues.put(ITEM_COLUMN_IMAGE_PATH, imagePath);
        contentValues.put(ITEM_COLUMN_COST, cost);
        contentValues.put(ITEM_COLUMN_LOCATION, location);
        db.update(ITEM_TABLE_NAME, contentValues, ITEM_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Cursor getItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + ITEM_TABLE_NAME + " WHERE " +
                ITEM_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
    }

    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + ITEM_TABLE_NAME, null);
    }

    public Integer deleteItem(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ITEM_TABLE_NAME,
                ITEM_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }


}

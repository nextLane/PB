package infinite.pb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "UrlsManager";

    // Contacts table name
    private static final String TABLE_URLS = "urlRequests";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_URL = "url";
    private static final String KEY_COUNT = "count";
    private static final String KEY_STATUS = "status";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_URLS + "("
                + KEY_ID + " INTEGER ," + KEY_URL + " TEXT PRIMARY KEY,"
                + KEY_COUNT + " INTEGER" +  KEY_STATUS + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_URLS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addUrl(UrlData url) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_URL, url.getURL());
        values.put(KEY_COUNT, url.getCount());
        values.put(KEY_STATUS, url.getStatus());

        // Inserting Row
        db.insert(TABLE_URLS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    UrlData getUrlData(String url) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_URLS, new String[] { KEY_ID,
                        KEY_URL, KEY_COUNT, KEY_STATUS }, KEY_URL + "=?",
                new String[] { url }, null, null, null, null);   //TODO:check the syntax once
        if (cursor != null)
            cursor.moveToFirst();

        UrlData ud = new UrlData(cursor.getString(1), Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)));
        return ud;
    }


    // TODO: Need is  1. getting n updating only count across a url 2.minimise db reads and writes

    // Getting All Data
    public List<UrlData> getAllUrlsData() {
        List<UrlData> urlList = new ArrayList<UrlData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_URLS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UrlData ud = new UrlData(cursor.getString(1),Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)));
                urlList.add(ud);
            } while (cursor.moveToNext());
        }

        // return contact list
        return urlList;
    }

    // Getting All URLs
    public List<String> getAllUrls() {
        List<String> urlList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT "+ KEY_URL +" FROM " + TABLE_URLS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String ud = cursor.getString(0);

                urlList.add(ud);
            } while (cursor.moveToNext());
        }

        // return contact list
        return urlList;
    }



    // Incrementing count for single url
    public int valueChange(String url, int count) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, count);
    //    values.put(KEY_STATUS, url.getStatus());

        // updating row
        return db.update(TABLE_URLS, values, KEY_ID + " = ?",
                new String[] { url});
    }

    // Incrementing count and setting status for single url
    public int valueChange(String url, int count, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, count);
        values.put(KEY_STATUS, status);

        // updating row
        return db.update(TABLE_URLS, values, KEY_ID + " = ?",
                new String[] { url});
    }

    // Updating single url
    public int updateContact(UrlData url) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, url.getCount());
        values.put(KEY_STATUS, url.getStatus());

        // updating row
        return db.update(TABLE_URLS, values, KEY_ID + " = ?",
                new String[] { url.getURL()});
    }

    // Deleting single URL
    public void deleteUrl(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_URLS, KEY_URL + " = ?",
                new String[] { url });
        db.close();
    }


    // Getting Urls Count
    public int getUrlsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_URLS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

}
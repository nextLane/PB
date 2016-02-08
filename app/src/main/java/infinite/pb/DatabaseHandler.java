package infinite.pb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* This class intends to provide a database handler that will help in performing CRUD operations, to manage a table storing the information like status and count for every URL
request that has been tracked from the device.*/

public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler mInstance = null;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UrlsDirManager";
    private static final String TABLE_URLS = "URLRequests";
    private static final String KEY_ID = "id";
    private static final String KEY_URL = "url";
    private static final String KEY_COUNT = "count";
    private static final String KEY_STATUS = "status";
    public static String Lock = "dblock";

    //using instance saves us from inconsistency if the db is accessed/updated from multiple locations, multiple times
    public static synchronized DatabaseHandler getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new DatabaseHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_URLS + "("
                + KEY_ID + " INTEGER ," + KEY_URL + " TEXT PRIMARY KEY,"
                + KEY_COUNT + " INTEGER, " +  KEY_STATUS + " INTEGER" + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_URLS);

        // Create tables again
        onCreate(db);
    }


    void addUrl(UrlData url) {

        synchronized (Lock) {
            SQLiteDatabase db = this.getWritableDatabase();

            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put(KEY_URL, url.getURL());
                values.put(KEY_COUNT, url.getCount());
                values.put(KEY_STATUS, url.getStatus());

                // Inserting Row
                db.insert(TABLE_URLS, null, values);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.d("Update:", "Error while trying to add post to database");
            } finally {
                db.endTransaction();
            }
            db.close();

        }

    }

//This method returns all information for any particular url
    UrlData getUrlData(String url) {

        UrlData ud = null;
        synchronized (Lock) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_URLS, new String[]{KEY_ID, KEY_URL, KEY_COUNT, KEY_STATUS}, KEY_URL + "=?",
                    new String[]{url}, null, null, null, null);   //TODO:check the syntax once
            try {
                if (cursor != null)
                    cursor.moveToFirst();
                ud = new UrlData(cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)));


            } catch (Exception e) {
                Log.d("Update:", "Error while trying to get posts from database");
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }

                db.close();

            }
        }
            return ud;
        }


    // TODO: Need is  1. getting n updating only count across a url 2.minimise db reads and writes

    //This method returns all information for all the urls in the relation

    public List<UrlData> getAllUrlsData() {
        List<UrlData> urlList = new ArrayList<UrlData>();

        String selectQuery = "SELECT  * FROM " + TABLE_URLS;

        synchronized (Lock) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            try {
                if (cursor.moveToFirst()) {
                    do {
                        UrlData ud = new UrlData(cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)));
                        urlList.add(ud);
                    } while (cursor.moveToNext());
                }

            } catch (Exception e) {
                Log.d("Update:", "Error while trying to get posts from database");
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                db.close();

            }
        }
            return urlList;
        }

    //This method returns the list of all urls saved in relation

    public List<String> getAllUrls() {
        List<String> urlList = new ArrayList<String>();
        String selectQuery = "SELECT "+ KEY_URL +" FROM " + TABLE_URLS;


        synchronized (Lock) {

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            try {
                if (cursor.moveToFirst()) {
                    do {
                        String ud = cursor.getString(0);

                        urlList.add(ud);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.d("Update:", "Error while trying to get posts from database");
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                db.close();

            }
        }
        return urlList;
    }


    //This method is to update the count across any particular URL in the relation

    public void valueChange(String url, int count) {

        synchronized (Lock) {

            SQLiteDatabase db = this.getWritableDatabase();
            Log.d("###count url:",url+"::"+count);
            ContentValues values = new ContentValues();
            values.put(KEY_COUNT, count);
            db.execSQL("UPDATE "+TABLE_URLS+"  SET "+KEY_COUNT+" ="+count+" WHERE "+KEY_URL+"= '"+url+"'");

            db.close();
        }
    }

    //This method is to update the count and status across any particular URL in the relation
    public void valueChange(String url, int count, int status) {

        synchronized (Lock) {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_COUNT,""+ count);
            values.put(KEY_STATUS, ""+status);

            // updating row
            db.update(TABLE_URLS, values, KEY_ID + " = ?",
                    new String[]{url});
            db.close();
        }
    }

    // This method is responsible to update the count, status (& any other fields that can be added) across any particular URL in the relation
    public void updateUrl(UrlData url) {

        synchronized (Lock) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_COUNT, ""+url.getCount());
            values.put(KEY_STATUS, ""+url.getStatus());

            // updating row
            db.update(TABLE_URLS, values, KEY_ID + " = ?",
                    new String[]{url.getURL()});

            db.close();
        }
    }

    // Deleting single URL data from the relation
    public void deleteUrl(String url) {

        synchronized (Lock) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete(TABLE_URLS, KEY_URL + " = ?", new String[]{url});
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.d("Update:", "Error while trying to delete all posts and users");
            } finally {
                db.endTransaction();
            }
            db.close();
        }
    }

    // Getting Urls Count
    public int getUrlsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_URLS;
        Cursor cursor;

        synchronized (Lock) {

            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(countQuery, null);
            cursor.close();
            db.close();
        }
        if (cursor != null)
            return cursor.getCount();
        else
            return 0;

    }

}
package dlp.bluelupin.dlp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;

/**
 * Created by subod on 21-Jul-16.
 */
public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 0;
    public static final String DATABASE_NAME = "Konnect.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CacheServiceCall");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void onCreate(SQLiteDatabase db) {

        // Create CacheServiceCall
        String CREATE_CacheServiceCalls_TABLE = "CREATE TABLE CacheServiceCall(id INTEGER PRIMARY KEY, url TEXT, dataIdentifier Text,  payload TEXT, lastCalled TEXT )";
        db.execSQL(CREATE_CacheServiceCalls_TABLE);
        String CREATE_DataEntity_TABLE = "CREATE TABLE DataEntity(id INTEGER PRIMARY KEY, server_id INTEGER, parent_id INTEGER,  sequence INTEGER, media_id INTEGER, thumbnail_media_id INTEGER, lang_resource_name TEXT, lang_resource_description TEXT, type TEXT, String url,created_at DATETIME, updated_at DATETIME, deleted_at DATETIME)";
        db.execSQL(CREATE_CacheServiceCalls_TABLE);
    }


     /* business logic persistance */

    // region cacheResource
    public boolean insertCacheServiceCall(CacheServiceCallData ob) {
        //CacheServiceCall(id INTEGER PRIMARY KEY, url TEXT,  lastCalled DATETIME )
        ContentValues values = new ContentValues();
        //System.out.println("url "+ob.getUrl()+"  lastCalledInsert "+ob.getLastCalled());
        values.put("url", ob.getUrl());
        values.put("dataIdentifier", ob.getDataIdentifier());
        values.put("payload", ob.getPayload());
        values.put("lastCalled", ob.getLastCalled());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("CacheServiceCall", null, values);
        db.close();
        return i > 0;
    }

    public CacheServiceCallData getCacheServiceCallByUrl(String url) {
        String query = "Select id, url, dataIdentifier, payload,  lastCalled FROM CacheServiceCall WHERE url = '" + url + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        CacheServiceCallData ob = new CacheServiceCallData();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ob.setId(Integer.parseInt(cursor.getString(0)));
            ob.setUrl(cursor.getString(1));
            ob.setDataIdentifier(cursor.getString(2));
            ob.setPayload(cursor.getString(3));
            ob.setLastCalled(cursor.getString(4));
            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public CacheServiceCallData getCacheServiceCallByDataIdentifier(String dataIdentifier) {
        String query = "Select id, url, dataIdentifier, payload,  lastCalled FROM CacheServiceCall WHERE dataIdentifier = '"
                + dataIdentifier + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        CacheServiceCallData ob = new CacheServiceCallData();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ob.setId(Integer.parseInt(cursor.getString(0)));
            ob.setUrl(cursor.getString(1));
            ob.setDataIdentifier(cursor.getString(2));
            ob.setPayload(cursor.getString(3));
            ob.setLastCalled(cursor.getString(4));
            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        if (ob != null) {
            Log.d(Consts.BASE_URL, "getCacheServiceCallByDataIdentifier called with" + "url = '" + ob.getUrl() + "' and dataIdentifier = '" +
                    ob.getDataIdentifier() + " " + ob.getLastCalled());
        }
        return ob;
    }


    public boolean updateCacheServiceCall(CacheServiceCallData ob) {
        ContentValues values = new ContentValues();

        String temp = ob.getLastCalled();

        values.put("lastCalled", ob.getLastCalled());
        values.put("payload", ob.getPayload());
        values.put("dataIdentifier", ob.getDataIdentifier());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getDataIdentifier() == null || ob.getDataIdentifier() == "") {
            i = db.update("CacheServiceCall", values, "url = '" + ob.getUrl() + "'", null); //db.insert("CacheServiceCall", null, values);
        } else {
            i = db.update("CacheServiceCall", values, "url = '" + ob.getUrl() + "' and dataIdentifier = '" +
                    ob.getDataIdentifier() + "'", null); //db.insert("CacheServiceCall", null, values);

            Log.d(Consts.LOG_TAG, "updateCacheServiceCall called with" + "url = '" + ob.getUrl() + "' and dataIdentifier = '" +
                    ob.getDataIdentifier() + " " + ob.getLastCalled());
        }
        db.close();
        return i > 0;
    }

    public boolean upsertCacheServiceCall(CacheServiceCallData ob) {
        CacheServiceCallData cacheSeviceCallData = null;
        if (ob.getDataIdentifier() == null || ob.getDataIdentifier() == "") {
            cacheSeviceCallData = getCacheServiceCallByUrl(ob.getUrl());

        } else {
            cacheSeviceCallData = getCacheServiceCallByDataIdentifier(ob.getDataIdentifier());
        }
        boolean done = false;
        if (cacheSeviceCallData == null) {
            done = insertCacheServiceCall(ob);
        } else {
            done = updateCacheServiceCall(ob);
        }
        return done;
    }

    public String getCacheServiceDateTime(String url) {
        CacheServiceCallData cacheSeviceCallData = getCacheServiceCallByUrl(url);
        if (cacheSeviceCallData != null) {
            return cacheSeviceCallData.getLastCalled();
        }
        return null;
    }

    public String getCacheServiceDateTimeUsingDataItentifier(String url) {
        CacheServiceCallData cacheSeviceCallData = getCacheServiceCallByDataIdentifier(url);
        if (cacheSeviceCallData != null) {
            return cacheSeviceCallData.getLastCalled();
        }
        return null;
    }

    public boolean setCacheServiceDateTime(CacheServiceCallData ob) {
        boolean result = upsertCacheServiceCall(ob);
        return result;
    }
    // endregion cacheResource
}

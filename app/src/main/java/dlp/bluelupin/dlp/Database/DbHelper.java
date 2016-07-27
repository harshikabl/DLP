package dlp.bluelupin.dlp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.Data;

/**
 * Created by subod on 21-Jul-16.
 */
public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dlp_db.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CacheServiceCall");
        db.execSQL("DROP TABLE IF EXISTS DataEntity");
        db.execSQL("DROP TABLE IF EXISTS ResourceEntity");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void onCreate(SQLiteDatabase db) {

        // Create CacheServiceCall
        String CREATE_CacheServiceCalls_TABLE = "CREATE TABLE CacheServiceCall(id INTEGER PRIMARY KEY, url TEXT, dataIdentifier Text,  payload TEXT, lastCalled TEXT )";
        db.execSQL(CREATE_CacheServiceCalls_TABLE);
        String CREATE_DataEntity_TABLE = "CREATE TABLE DataEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, parent_id INTEGER,  sequence INTEGER, media_id INTEGER, thumbnail_media_id INTEGER, lang_resource_name TEXT, lang_resource_description TEXT, type TEXT,  url TEXT,created_at DATETIME, updated_at DATETIME, deleted_at DATETIME)";
        //clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at
        db.execSQL(CREATE_DataEntity_TABLE);

        String CREATE_ResourceEntity_TABLE = "CREATE TABLE ResourceEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, name TEXT, content TEXT, language_id INTEGER, created_at DATETIME, updated_at DATETIME, deleted_at DATETIME)";
        //clientId, server_id , name , content , language_id ,created_at , updated_at , deleted_at
        db.execSQL(CREATE_ResourceEntity_TABLE);
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

    //region DataEntity
    public boolean upsertDataEntity(Data ob) {
        boolean done = false;
        Data data = null;
        if (ob.getId()!=0) {
            data = getDataEntityByClientId(ob.getId());
            if (data == null) {
                done = insertDataEntity(ob);
            } else {
                done = updateDataEntity(ob);
            }
        }
        return done;
    }

    public Data getDataEntityByClientId(int clientId) {
        String query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity WHERE clientId = " + clientId + " ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ob.setClientId(Integer.parseInt(cursor.getString(0)));
            ob.setId(Integer.parseInt(cursor.getString(1))); // this represets server Id
            ob.setParent_id(Integer.parseInt(cursor.getString(2)));
            ob.setSequence(cursor.getInt(3));
            ob.setMedia_id(Integer.parseInt(cursor.getString(4)));
            ob.setThumbnail_media_id(Integer.parseInt(cursor.getString(5)));
            ob.setLang_resource_name(cursor.getString(6));
            ob.setLang_resource_description(cursor.getString(7));
            ob.setType(cursor.getString(8));
            ob.setUrl(cursor.getString(9));
            ob.setCreated_at(cursor.getString(10));
            ob.setUpdated_at(cursor.getString(11));
            ob.setDeleted_at(cursor.getString(12));

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public boolean insertDataEntity(Data ob) {
//        clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at

        ContentValues values = new ContentValues();
        values.put("server_id", ob.getId());
        values.put("parent_id", ob.getParent_id());
        values.put("sequence", ob.getSequence());
        values.put("media_id", ob.getMedia_id());
        values.put("thumbnail_media_id", ob.getThumbnail_media_id());
        values.put("lang_resource_name", ob.getLang_resource_name());
        values.put("lang_resource_description", ob.getLang_resource_description());
        values.put("type", ob.getType());
        values.put("url", ob.getUrl());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("DataEntity", null, values);
        db.close();
        return i > 0;
    }

    public boolean updateDataEntity(Data ob) {

        ContentValues values = new ContentValues();
       // values.put("server_id", ob.getUrl());
        values.put("parent_id", ob.getParent_id());
        values.put("sequence", ob.getSequence());
        values.put("media_id", ob.getMedia_id());
        values.put("thumbnail_media_id", ob.getThumbnail_media_id());
        values.put("lang_resource_name", ob.getLang_resource_name());
        values.put("lang_resource_description", ob.getLang_resource_description());
        values.put("type", ob.getType());
        values.put("url", ob.getUrl());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() !=0) {
            i = db.update("DataEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "updateDataEntity called with" + " server_id = '" + ob.getId());

        db.close();
        return i > 0;
    }

    public List<Data> getDataEntityByParentId(Integer parentId) {
        String query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity";// WHERE parent_id is null ";

        if(parentId != null) {
             query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity WHERE parent_id = " + (int) parentId + " ";
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Data ob = new Data();
                ob.setClientId(Integer.parseInt(cursor.getString(0)));
                ob.setId(Integer.parseInt(cursor.getString(1))); // this represets server Id
                ob.setParent_id(Integer.parseInt(cursor.getString(2)));
                ob.setSequence(cursor.getInt(3));
                ob.setMedia_id(Integer.parseInt(cursor.getString(4)));
                ob.setThumbnail_media_id(Integer.parseInt(cursor.getString(5)));
                ob.setLang_resource_name(cursor.getString(6));
                ob.setLang_resource_description(cursor.getString(7));
                ob.setType(cursor.getString(8));
                ob.setUrl(cursor.getString(9));
                ob.setCreated_at(cursor.getString(10));
                ob.setUpdated_at(cursor.getString(11));
                ob.setDeleted_at(cursor.getString(12));
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }
    // endregion DataEntity

    //region ResourceEntity
    public boolean upsertResourceEntity(Data ob) {
        boolean done = false;
        Data data = null;
        if (ob.getId()!=0) {
            data = getResourceEntityById(ob.getId());
            if (data == null) {
                done = insertResourceEntity(ob);
            } else {
                done = updateResourceEntity(ob);
            }
        }
        return done;
    }

    public Data getResourceEntityById(int id) {
        String query = "Select clientId, server_id , name , content , language_id ,created_at , updated_at , deleted_at FROM ResourceEntity WHERE server_id = " + id + " " ;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ob.setClientId(Integer.parseInt(cursor.getString(0)));
            ob.setId(Integer.parseInt(cursor.getString(1))); // this represets server Id
            ob.setName(cursor.getString(2));
            ob.setContent(cursor.getString(3));
            ob.setLanguage_id(Integer.parseInt(cursor.getString(4)));
            ob.setCreated_at(cursor.getString(5));
            ob.setUpdated_at(cursor.getString(6));
            ob.setDeleted_at(cursor.getString(7));

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }



    public boolean insertResourceEntity(Data ob) {
//clientId, server_id , name , content , language_id ,created_at , updated_at , deleted_at

        ContentValues values = new ContentValues();
        values.put("server_id", ob.getId());
        values.put("name", ob.getName());
        values.put("content", ob.getContent());
        values.put("language_id", ob.getLanguage_id());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("ResourceEntity", null, values);
        db.close();
        return i > 0;
    }

    public boolean updateResourceEntity(Data ob) {

        ContentValues values = new ContentValues();
        //values.put("server_id", ob.getId());
        values.put("name", ob.getName());
        values.put("content", ob.getContent());
        values.put("language_id", ob.getLanguage_id());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() !=0) {
            i = db.update("ResourceEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "updateDataEntity called with" + " server_id = '" + ob.getId());

        db.close();
        return i > 0;
    }

    public Data getResourceEntityByName(String name, int language_id) {
        String query = "Select clientId, server_id , name , content , language_id ,created_at , updated_at , deleted_at FROM ResourceEntity WHERE name = '" + name + "' and language_id =" + language_id + "" ;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ob.setClientId(Integer.parseInt(cursor.getString(0)));
            ob.setId(Integer.parseInt(cursor.getString(1))); // this represets server Id
            ob.setName(cursor.getString(2));
            ob.setContent(cursor.getString(3));
            ob.setLanguage_id(Integer.parseInt(cursor.getString(4)));
            ob.setCreated_at(cursor.getString(5));
            ob.setUpdated_at(cursor.getString(6));
            ob.setDeleted_at(cursor.getString(7));

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public String getResourceContent(String name, int language_id) {
        String query = "Select clientId, server_id , name , content , language_id ,created_at , updated_at , deleted_at FROM ResourceEntity WHERE name = '" + name + "' and language_id =" + language_id + "" ;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ob.setClientId(Integer.parseInt(cursor.getString(0)));
            ob.setId(Integer.parseInt(cursor.getString(1))); // this represets server Id
            ob.setName(cursor.getString(2));
            ob.setContent(cursor.getString(3));
            ob.setLanguage_id(Integer.parseInt(cursor.getString(4)));
            ob.setCreated_at(cursor.getString(5));
            ob.setUpdated_at(cursor.getString(6));
            ob.setDeleted_at(cursor.getString(7));

            cursor.close();
            return ob.getContent();
        } else {
            ob = null;
        }
        db.close();
        return null;
    }

    public List<Data> getResources() {
        String query = "Select clientId, server_id , name , content , language_id ,created_at , updated_at , deleted_at FROM ResourceEntity" ;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Data ob = new Data();
                ob.setClientId(Integer.parseInt(cursor.getString(0)));
                ob.setId(Integer.parseInt(cursor.getString(1))); // this represets server Id
                ob.setName(cursor.getString(2));
                ob.setContent(cursor.getString(3));
                ob.setLanguage_id(Integer.parseInt(cursor.getString(4)));
                ob.setCreated_at(cursor.getString(5));
                ob.setUpdated_at(cursor.getString(6));
                ob.setDeleted_at(cursor.getString(7));
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }
    // endregion ResourceEntity
}

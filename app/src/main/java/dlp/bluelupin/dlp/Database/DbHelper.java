package dlp.bluelupin.dlp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.Data;

/**
 * Created by subod on 21-Jul-16.
 */
public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "dlp_db.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CacheServiceCall");
        db.execSQL("DROP TABLE IF EXISTS DataEntity");
        db.execSQL("DROP TABLE IF EXISTS ResourceEntity");
        db.execSQL("DROP TABLE IF EXISTS MediaEntity");
        db.execSQL("DROP TABLE IF EXISTS AccountEntity");
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

        String CREATE_MediaEntity_TABLE = "CREATE TABLE MediaEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, name TEXT, type TEXT, url TEXT, file_path TEXT, language_id INTEGER , created_at DATETIME, updated_at DATETIME, deleted_at DATETIME)";
        //clientId , server_id , name , type , url , file_path , language_id ,created_at , updated_at , deleted_at
        db.execSQL(CREATE_MediaEntity_TABLE);

        String CREATE_AccountEntity_TABLE = "CREATE TABLE AccountEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, name TEXT, email TEXT, phone TEXT, preferred_language_id INTEGER, role TEXT, api_token TEXT, otp INTEGER)";
        //clientId, server_id , name , email , phone ,preferred_language_id , role, api_token, otp
        db.execSQL(CREATE_AccountEntity_TABLE);
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
        if (ob.getId() != 0) {
            data = getDataEntityById(ob.getId());
            if (data == null) {
                done = insertDataEntity(ob);
            } else {
                done = updateDataEntity(ob);
            }
        }
        return done;
    }

    public Data getDataEntityById(int id) {
        String query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity WHERE clientId = " + id + " ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateDataEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    private void populateDataEntity(Cursor cursor, Data ob) {
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
        // values.put("server_id", ob.getId());
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
        if (ob.getId() != 0) {
            i = db.update("DataEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "updateDataEntity called with" + " server_id = '" + ob.getId());

        db.close();
        return i > 0;
    }

    public List<Data> getDataEntityByParentId(Integer parentId) {
        String query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity  WHERE parent_id = 0 order by sequence";

        if (parentId != null) {
            query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity WHERE parent_id = " + (int) parentId + " order by sequence";
        }

        return populateContentDataFromDb(query);
    }


    public List<Data> getDataEntityByParentIdAndType(Integer parentId, String type) {
        String query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity  WHERE parent_id = 0 and type = '" + type + "'  order by sequence";

        if (parentId != null) {
            query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity WHERE parent_id = " + (int) parentId + " and type = '" + type + "' order by sequence";
        }

        return populateContentDataFromDb(query);
    }

    public String getTypeOfChildren(Integer parentId) {
        String type = "";
        String query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity  WHERE parent_id = 0 order by sequence LIMIT 1";

        if (parentId != null) {
            query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity WHERE parent_id = " + (int) parentId + " order by sequence LIMIT 1";
        }

        List<Data> dataList = populateContentDataFromDb(query);
        if (dataList.size() > 0) {
            Data data = dataList.get(0);
            type = data.getType();
        }
        return type;
    }


    @NonNull
    private List<Data> populateContentDataFromDb(String query) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Data ob = new Data();
                populateDataEntity(cursor, ob);
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
        if (ob.getId() != 0) {
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
        String query = "Select clientId, server_id , name , content , language_id ,created_at , updated_at , deleted_at FROM ResourceEntity WHERE server_id = " + id + " ";

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
        if (ob.getId() != 0) {
            i = db.update("ResourceEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "updateDataEntity called with" + " server_id = '" + ob.getId());

        db.close();
        return i > 0;
    }

    public Data getResourceEntityByName(String name, int language_id) {

        String query = "Select clientId, server_id , name , content , language_id ,created_at , updated_at , deleted_at FROM ResourceEntity WHERE name = '" + name + "' and language_id =" + language_id + "";

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
        String query = "Select clientId, server_id , name , content , language_id ,created_at , updated_at , deleted_at FROM ResourceEntity WHERE name = '" + name + "' and language_id =" + language_id + "";

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
        String query = "Select clientId, server_id , name , content , language_id ,created_at , updated_at , deleted_at FROM ResourceEntity";

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

    //region MediaEntity
    public boolean upsertMediaEntity(Data ob) {
        boolean done = false;
        Data data = null;
        if (ob.getId() != 0) {
            data = getMediaEntityById(ob.getId());
            if (data == null) {
                done = insertMediaEntity(ob);
            } else {
                done = updateMediaEntity(ob);
            }
        }
        return done;
    }

    public Data getMediaEntityById(int id) {
        String query = "SELECT clientId , server_id , name , type , url , file_path , language_id ,created_at , updated_at , deleted_at  from MediaEntity WHERE clientId = " + id + " ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ob.setClientId(Integer.parseInt(cursor.getString(0)));
            ob.setId(Integer.parseInt(cursor.getString(1))); // this represents server Id
            ob.setName(cursor.getString(2));
            ob.setType(cursor.getString(3));
            ob.setUrl(cursor.getString(4));
            ob.setFile_path(cursor.getString(5));
            ob.setLanguage_id(cursor.getInt(6));
            ob.setCreated_at(cursor.getString(7));
            ob.setUpdated_at(cursor.getString(8));
            ob.setDeleted_at(cursor.getString(9));

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public boolean insertMediaEntity(Data ob) {
//clientId , server_id , name , type , url , file_path , language_id ,created_at , updated_at , deleted_at

        ContentValues values = new ContentValues();
        values.put("server_id", ob.getId());
        values.put("name", ob.getName());
        values.put("type", ob.getType());
        values.put("url", ob.getUrl());
        values.put("file_path", ob.getFile_path());
        values.put("language_id", ob.getLanguage_id());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("MediaEntity", null, values);
        db.close();
        return i > 0;
    }

    public boolean updateMediaEntity(Data ob) {

        ContentValues values = new ContentValues();
        // values.put("server_id", ob.getId());
        values.put("name", ob.getName());
        values.put("type", ob.getType());
        values.put("url", ob.getUrl());
        values.put("file_path", ob.getFile_path());
        values.put("language_id", ob.getLanguage_id());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("MediaEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "updateDataEntity called with" + " server_id = '" + ob.getId());

        db.close();
        return i > 0;
    }

    public List<Data> getAllMedia() {
        String query = "Select clientId , server_id , name , type , url , file_path , language_id ,created_at , updated_at , deleted_at FROM MediaEntity";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Data ob = new Data();
                ob.setClientId(Integer.parseInt(cursor.getString(0)));
                ob.setId(Integer.parseInt(cursor.getString(1))); // this represents server Id
                ob.setName(cursor.getString(2));
                ob.setType(cursor.getString(3));
                ob.setUrl(cursor.getString(4));
                ob.setFile_path(cursor.getString(5));
                ob.setLanguage_id(cursor.getInt(6));
                ob.setCreated_at(cursor.getString(7));
                ob.setUpdated_at(cursor.getString(8));
                ob.setDeleted_at(cursor.getString(9));
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }
    // endregion MediaEntity


    //account data save
    public boolean upsertAccountData(AccountData data) {
        AccountData profileData = getAccountData();
        boolean done = false;
        if (profileData == null) {
            done = insertAccountData(data);
        } else {
            done = updateAccountData(data);
        }
        return done;
    }

    public boolean insertAccountData(AccountData accountData) {

        ContentValues values = new ContentValues();
        values.put("server_id", accountData.getId());
        values.put("name", accountData.getName());
        values.put("email", accountData.getEmail());
        values.put("phone", accountData.getPhone());
        values.put("preferred_language_id", accountData.getPreferred_language_id());
        values.put("role", accountData.getRole());
        values.put("api_token", accountData.getApi_token());
        values.put("otp", accountData.getOtp());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("AccountEntity", null, values);
        db.close();
        return i > 0;
    }

    public boolean updateAccountData(AccountData accountData) {
        ContentValues values = new ContentValues();
        values.put("server_id", accountData.getId());
        values.put("name", accountData.getName());
        values.put("email", accountData.getEmail());
        values.put("phone", accountData.getPhone());
        values.put("preferred_language_id", accountData.getPreferred_language_id());
        values.put("role", accountData.getRole());
        values.put("api_token", accountData.getApi_token());
        values.put("otp", accountData.getOtp());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.update("AccountEntity", values, "server_id = '" + accountData.getId() + "'", null);

        db.close();
        return i > 0;
    }

    public AccountData getAccountData() {
        //clientId, server_id , name , email , phone ,preferred_language_id , role, api_token,otp
        String query = "Select clientId, server_id, name, email, phone, preferred_language_id, role,api_token,otp  FROM AccountEntity ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        AccountData ob = new AccountData();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            //ob.setId(Integer.parseInt(cursor.getString(0)));
            ob.setId(Integer.parseInt(cursor.getString(1)));
            ob.setName(cursor.getString(2));
            ob.setEmail(cursor.getString(3));
            ob.setPhone(cursor.getString(4));
            ob.setPreferred_language_id(Integer.parseInt(cursor.getString(5)));
            ob.setRole(cursor.getString(6));
            ob.setApi_token(cursor.getString(7));
            ob.setOtp(Integer.parseInt(cursor.getString(8)));
            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public boolean deleteAccountData() {
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("AccountEntity", null, null);
        db.close();
        result = true;
        return result;
    }
}

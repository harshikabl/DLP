package dlp.bluelupin.dlp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.util.Log;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.FavoritesData;
import dlp.bluelupin.dlp.Models.LanguageData;
import dlp.bluelupin.dlp.Utilities.Utility;

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
        //db.execSQL("DROP TABLE IF EXISTS AccountEntity");
        db.execSQL("DROP TABLE IF EXISTS FavoritesEntity");
        db.execSQL("DROP TABLE IF EXISTS DownloadMediaEntity");
        db.execSQL("DROP TABLE IF EXISTS DownloadingFileEntity");
        db.execSQL("DROP TABLE IF EXISTS MedialanguageLatestEntity");
        db.execSQL("DROP TABLE IF EXISTS LanguageEntity");
        db.execSQL("DROP TABLE IF EXISTS NotificationEntity");
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

        String CREATE_MediaEntity_TABLE = "CREATE TABLE MediaEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, name TEXT, type TEXT, url TEXT, download_url TEXT, thumbnail_url TEXT, thumbnail_file_path TEXT,  file_path TEXT, language_id INTEGER , created_at DATETIME, updated_at DATETIME, deleted_at DATETIME,Local_file_path TEXT)";
        //clientId , server_id , name , type , url , file_path , language_id ,created_at , updated_at , deleted_at
        db.execSQL(CREATE_MediaEntity_TABLE);

        if (!tableAlreadyExists(db, "AccountEntity")) {
            String CREATE_AccountEntity_TABLE = "CREATE TABLE AccountEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, name TEXT, email TEXT, phone TEXT, preferred_language_id INTEGER, role TEXT, api_token TEXT, otp INTEGER, isVerified INTEGER)";
            //clientId, server_id , name , email , phone ,preferred_language_id , role, api_token, otp
            db.execSQL(CREATE_AccountEntity_TABLE);
        }

        String CREATE_FavoritesEntity_TABLE = "CREATE TABLE FavoritesEntity(clientId INTEGER PRIMARY KEY, Content_id INTEGER, updated_at DATETIME, Favorites_Flag TEXT, FOREIGN KEY (Content_id) REFERENCES DataEntity(server_id))";
        //clientId, Content_id , updated_at,Favorites_Flag FavoritesEntity
        db.execSQL(CREATE_FavoritesEntity_TABLE);

        String CREATE_DownloadMediaEntity_TABLE = "CREATE TABLE DownloadMediaEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, name TEXT, type TEXT, url TEXT, file_path TEXT, language_id INTEGER , created_at DATETIME, updated_at DATETIME, deleted_at DATETIME,Local_file_path TEXT)";
        //clientId , server_id , name , type , url , file_path , language_id ,created_at , updated_at , deleted_at
        db.execSQL(CREATE_DownloadMediaEntity_TABLE);

        String CREATE_DownloadingFileEntity_TABLE = "CREATE TABLE DownloadingFileEntity(id INTEGER PRIMARY KEY, MediaId INTEGER, progress INTEGER, FOREIGN KEY (MediaId) REFERENCES DownloadMediaEntity(clientId))";
        //id , MediaId , progress
        db.execSQL(CREATE_DownloadingFileEntity_TABLE);

        String CREATE_MedialanguageLatestDataEntity_TABLE = "CREATE TABLE MedialanguageLatestEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, media_id INTEGER, language_id INTEGER ,file_path TEXT, url TEXT,created_at DATETIME, updated_at DATETIME, download_url TEXT, created_by INTEGER, updated_by INTEGER, cloud_transferred INTEGER)";
        //clientId , server_id  , media_id, language_id ,file_path ,url ,created_at , updated_at ,download_url ,created_by ,updated_by ,cloud_transferred
        db.execSQL(CREATE_MedialanguageLatestDataEntity_TABLE);

        String CREATE_LanguageEntity_TABLE = "CREATE TABLE LanguageEntity(id INTEGER PRIMARY KEY, LanguageId INTEGER, Name TEXT, DeletedAt TEXT ,code TEXT)";
        //id , LanguageId , Name ,DeletedAt
        db.execSQL(CREATE_LanguageEntity_TABLE);


        String CREATE_NotificationDataEntity_TABLE = "CREATE TABLE NotificationEntity(id INTEGER PRIMARY KEY, client_id INTEGER, send_at DATETIME,  message TEXT, language_id INTEGER, status TEXT, custom_data TEXT, created_by INTEGER, updated_by INTEGER, created_at DATETIME, updated_at DATETIME, deleted_at DATETIME)";
        //id , client_id , send_at ,  message , language_id , status , custom_data ,created_by ,updated_by , created_at , updated_at , deleted_at
        db.execSQL(CREATE_NotificationDataEntity_TABLE);

    }

    private Boolean tableAlreadyExists(SQLiteDatabase db, String tableName) {

        String query = "SELECT  COUNT(*) FROM  sqlite_master WHERE type='table' AND name='" + tableName + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (!cursor.moveToFirst()) {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
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
        String query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity WHERE server_id = " + id + " ";

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
        String query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity   WHERE parent_id = 0 and deleted_at IS  NULL order by sequence";

        if (parentId != null) {
            query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity WHERE parent_id = " + (int) parentId + "  and deleted_at IS  NULL order by sequence";
        }

        return populateContentDataFromDb(query);
    }


    public List<Data> getDataEntityByParentIdAndType(Integer parentId, String type) {
        String query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity  WHERE parent_id = 0 and type = '" + type + "' and deleted_at IS  NULL  order by sequence";

        if (parentId != null) {
            query = "Select clientId , server_id , parent_id ,  sequence , media_id , thumbnail_media_id , lang_resource_name , lang_resource_description , type ,  url,created_at , updated_at , deleted_at FROM DataEntity WHERE parent_id = " + (int) parentId + " and type = '" + type + "' and deleted_at IS  NULL order by sequence";
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

    public List<Data> getResourcesToDownload(Integer parentId) {
        List<Data> resourceListToDownload = new ArrayList<Data>();
        List<Data> children = getDataEntityByParentId(parentId);
        if (children.size() != 0) {
            for (Data child : children) {
                Data media = getMediaEntityById(child.getMedia_id());
                if (media != null && media.getLocalFilePath()== null) {
                    resourceListToDownload.add(media);
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "added media: " + media.getId() + " type: " + media.getType() + " download url: " + media.getDownload_url() + " localfilePath: " + media.getLocalFilePath());

                    }
                }
                List<Data> childContents = getResourcesToDownload(child.getId());
                resourceListToDownload.addAll(childContents);
//                if (Consts.IS_DEBUG_LOG) {
//                    Log.d(Consts.LOG_TAG, "childId: " + child.getId() + " childContents: " + childContents.size());
//                }
            }
        }
        return resourceListToDownload;
    }

    private List<Data> getDownloadResourceOfChild(String type, Data parent) {
        List<Data> resourceListToDownload = new ArrayList<Data>();
        List<Data> resourceListOfChild = getDataEntityByParentIdAndType(parent.getId(), type);
        for (Data child : resourceListOfChild) {
            Data media = getMediaEntityById(child.getMedia_id());
            if (media != null) {
                resourceListToDownload.add(media);
            }
        }
        return resourceListToDownload;
    }

    public List<Data> getThumbnailsToDownload(Integer parentId, List<Data> resourceListToDownload) {
        List<Data> children = getDataEntityByParentId(parentId);
        if (children.size() <= 0) {
            return resourceListToDownload;
        }
        for (Data child : children) {
            if (child.getThumbnail_media_id() != 0) {
                Data media = getMediaEntityById(child.getThumbnail_media_id());
                if (media != null) {
                    resourceListToDownload.add(media);
                }
            }
            resourceListToDownload.addAll(getThumbnailsToDownload(child.getId(), new ArrayList<Data>()));
        }
        return resourceListToDownload;
    }


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
        String query = "SELECT clientId , server_id , name , type ,download_url , thumbnail_url , thumbnail_file_path , url , file_path , language_id ,created_at , updated_at , deleted_at, Local_file_path  from MediaEntity WHERE server_id = " + id + " ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ob.setClientId(Integer.parseInt(cursor.getString(0)));
            ob.setId(Integer.parseInt(cursor.getString(1))); // this represents server Id
            ob.setName(cursor.getString(2));
            ob.setType(cursor.getString(3));
            ob.setDownload_url(cursor.getString(4));
            ob.setThumbnail_url(cursor.getString(5));
            ob.setThumbnail_file_path(cursor.getString(6));
            ob.setUrl(cursor.getString(7));
            ob.setFile_path(cursor.getString(8));
            ob.setLanguage_id(cursor.getInt(9));
            ob.setCreated_at(cursor.getString(10));
            ob.setUpdated_at(cursor.getString(11));
            ob.setDeleted_at(cursor.getString(12));
            ob.setLocalFilePath(cursor.getString(13));

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public Data getMediaEntityByIdAndLaunguageId(int id, int languageId) {
        String query = "SELECT clientId , server_id , name , type ,download_url , thumbnail_url , thumbnail_file_path , url , file_path , language_id ,created_at , updated_at , deleted_at, Local_file_path  from MediaEntity WHERE server_id = " + id + " ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ob.setClientId(Integer.parseInt(cursor.getString(0)));
            ob.setId(Integer.parseInt(cursor.getString(1))); // this represents server Id
            ob.setName(cursor.getString(2));
            ob.setType(cursor.getString(3));
            ob.setDownload_url(cursor.getString(4));
            ob.setThumbnail_url(cursor.getString(5));
            ob.setThumbnail_file_path(cursor.getString(6));
            ob.setUrl(cursor.getString(7));
            ob.setFile_path(cursor.getString(8));
            ob.setLanguage_id(cursor.getInt(9));
            ob.setCreated_at(cursor.getString(10));
            ob.setUpdated_at(cursor.getString(11));
            ob.setDeleted_at(cursor.getString(12));
            ob.setLocalFilePath(cursor.getString(13));

            cursor.close();

            Data mediaLanguage = getMedialanguageLatestDataEntityByMediaId(ob.getId(),languageId);
            if(mediaLanguage!=null) {
                ob.setUrl(mediaLanguage.getUrl());
                ob.setDownload_url(mediaLanguage.getDownload_url());
                ob.setLanguage_id(mediaLanguage.getLanguage_id());
            }
        } else {
            ob = null;
        }
        db.close();

        return ob;
    }

    public boolean insertMediaEntity(Data ob) {
//clientId , server_id , name , type , url , file_path , language_id ,created_at , updated_at , deleted_at,Local_file_path

        ContentValues values = new ContentValues();
        values.put("server_id", ob.getId());
        values.put("name", ob.getName());
        values.put("type", ob.getType());
        values.put("download_url", ob.getDownload_url());
        values.put("thumbnail_url", ob.getThumbnail_url());
        values.put("thumbnail_file_path", ob.getThumbnail_file_path());
        values.put("url", ob.getUrl());
        values.put("file_path", ob.getFile_path());
        values.put("language_id", ob.getLanguage_id());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());
        values.put("Local_file_path", ob.getLocalFilePath());

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
        values.put("download_url", ob.getDownload_url());
        values.put("thumbnail_url", ob.getThumbnail_url());
        values.put("thumbnail_file_path", ob.getThumbnail_file_path());
        values.put("file_path", ob.getFile_path());
        values.put("language_id", ob.getLanguage_id());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());
        values.put("Local_file_path", ob.getLocalFilePath());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("MediaEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "updateDataEntity called with" + " server_id = '" + ob.getId());

        db.close();
        return i > 0;
    }

    public boolean updateMediaLocalFilePathEntity(Data ob) {

        ContentValues values = new ContentValues();
        values.put("Local_file_path", ob.getLocalFilePath());

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
        String query = "Select clientId , server_id , name , type , download_url , thumbnail_url , thumbnail_file_path , url , file_path , language_id ,created_at , updated_at , deleted_at, Local_file_path FROM MediaEntity";

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
                ob.setDownload_url(cursor.getString(4));
                ob.setThumbnail_url(cursor.getString(5));
                ob.setThumbnail_file_path(cursor.getString(6));
                ob.setUrl(cursor.getString(7));
                ob.setFile_path(cursor.getString(8));
                ob.setLanguage_id(cursor.getInt(9));
                ob.setCreated_at(cursor.getString(10));
                ob.setUpdated_at(cursor.getString(11));
                ob.setDeleted_at(cursor.getString(12));
                ob.setLocalFilePath(cursor.getString(13));
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
        values.put("isVerified", accountData.getIsVerified());

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
        values.put("isVerified", accountData.getIsVerified());


        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.update("AccountEntity", values, "server_id = '" + accountData.getId() + "'", null);

        db.close();
        return i > 0;
    }

    //update account verified
    public boolean updateAccountDataVerified(AccountData accountData) {
        ContentValues values = new ContentValues();
        //values.put("server_id", accountData.getId());
        values.put("isVerified", accountData.getIsVerified());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.update("AccountEntity", values, "server_id = '" + accountData.getId() + "'", null);

        db.close();
        return i > 0;
    }

    public AccountData getAccountData() {
        //clientId, server_id , name , email , phone ,preferred_language_id , role, api_token,otp
        String query = "Select clientId, server_id, name, email, phone, preferred_language_id, role,api_token,otp,isVerified  FROM AccountEntity ";

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
            ob.setIsVerified(Integer.parseInt(cursor.getString(9)));
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

    //favorites data save
    public boolean upsertFavoritesData(FavoritesData favoritesData) {
        FavoritesData favData = getFavoritesData(favoritesData.getId());
        boolean done = false;
        if (favData == null) {
            done = insertFavoritesData(favoritesData);
        } else {
            done = updateFavoritesData(favoritesData);
        }
        return done;
    }

    public boolean insertFavoritesData(FavoritesData favoritesData) {

        ContentValues values = new ContentValues();
        values.put("Content_id", favoritesData.getId());
        values.put("updated_at", favoritesData.getUpdatedAt());
        values.put("Favorites_Flag", favoritesData.getFavoritesFlag());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("FavoritesEntity", null, values);
        db.close();
        return i > 0;
    }

    public boolean updateFavoritesData(FavoritesData favoritesData) {
        ContentValues values = new ContentValues();
        values.put("Content_id", favoritesData.getId());
        values.put("updated_at", favoritesData.getUpdatedAt());
        values.put("Favorites_Flag", favoritesData.getFavoritesFlag());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.update("FavoritesEntity", values, "Content_id = '" + favoritesData.getId() + "'", null);

        db.close();
        return i > 0;
    }

    //get favorites data into FavoritesEntity
    public FavoritesData getFavoritesData(int id) {
        //clientId, Content_id , updated_at,Favorites_Flag
        //String query = "Select FavoritesEntity.clientId, FavoritesEntity.Content_id, FavoritesEntity.updated_at, FavoritesEntity.Favorites_Flag  FROM FavoritesEntity INNER JOIN DataEntity ON FavoritesEntity.Content_id=DataEntity.server_id  where FavoritesEntity.Content_id = '" + id + "'";
        String query = "Select clientId, Content_id, updated_at, Favorites_Flag  FROM FavoritesEntity  where Content_id = '" + id + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        FavoritesData ob = new FavoritesData();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            //ob.setId(Integer.parseInt(cursor.getString(0)));
            ob.setId(Integer.parseInt(cursor.getString(1)));
            ob.setUpdatedAt(cursor.getString(2));
            ob.setFavoritesFlag(cursor.getString(3));
            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public List<FavoritesData> getFavoritesListData() {
        //clientId, Content_id , updated_at,Favorites_Flag
        List<FavoritesData> list = new ArrayList<FavoritesData>();
        String query = "Select FavoritesEntity.clientId, FavoritesEntity.Content_id, FavoritesEntity.updated_at, FavoritesEntity.Favorites_Flag, DataEntity.server_id , "
                + "DataEntity.parent_id ,  DataEntity.sequence , DataEntity.media_id , DataEntity.thumbnail_media_id , DataEntity.lang_resource_name , DataEntity.lang_resource_description , DataEntity.type ,  DataEntity.url,DataEntity.created_at , DataEntity.updated_at , DataEntity.deleted_at  FROM FavoritesEntity INNER JOIN DataEntity ON FavoritesEntity.Content_id=DataEntity.server_id  where FavoritesEntity.Favorites_Flag = '" + 1 + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                FavoritesData ob = new FavoritesData();
                //ob.setId(Integer.parseInt(cursor.getString(0)));
                ob.setId(Integer.parseInt(cursor.getString(1)));
                ob.setUpdatedAt(cursor.getString(2));
                ob.setFavoritesFlag(cursor.getString(3));

                ob.setServerId(Integer.parseInt(cursor.getString(4)));
                ob.setParent_id(Integer.parseInt(cursor.getString(5)));
                ob.setSequence(cursor.getInt(6));
                ob.setMedia_id(Integer.parseInt(cursor.getString(7)));
                ob.setThumbnail_media_id(Integer.parseInt(cursor.getString(8)));
                ob.setLang_resource_name(cursor.getString(9));
                ob.setLang_resource_description(cursor.getString(10));
                ob.setType(cursor.getString(11));
                ob.setUrl(cursor.getString(12));
                ob.setCreated_at(cursor.getString(13));
                ob.setUpdated_at(cursor.getString(14));
                ob.setDeleted_at(cursor.getString(15));

                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    //get chapters favorites and topic list data
    public List<FavoritesData> getFavoritesChaptersAndTopicListData(String type) {
        //clientId, Content_id , updated_at,Favorites_Flag
        List<FavoritesData> list = new ArrayList<FavoritesData>();
        String query = "Select FavoritesEntity.clientId, FavoritesEntity.Content_id, FavoritesEntity.updated_at, FavoritesEntity.Favorites_Flag, DataEntity.server_id , "
                + "DataEntity.parent_id ,  DataEntity.sequence , DataEntity.media_id , DataEntity.thumbnail_media_id , DataEntity.lang_resource_name , DataEntity.lang_resource_description , DataEntity.type ,  DataEntity.url,DataEntity.created_at , DataEntity.updated_at , DataEntity.deleted_at  FROM FavoritesEntity " +
                "INNER JOIN DataEntity ON FavoritesEntity.Content_id=DataEntity.server_id  where FavoritesEntity.Favorites_Flag = '" + 1 + "' and  DataEntity.type='" + type + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                FavoritesData ob = new FavoritesData();
                //ob.setId(Integer.parseInt(cursor.getString(0)));
                ob.setId(Integer.parseInt(cursor.getString(1)));
                ob.setUpdatedAt(cursor.getString(2));
                ob.setFavoritesFlag(cursor.getString(3));

                ob.setServerId(Integer.parseInt(cursor.getString(4)));
                ob.setParent_id(Integer.parseInt(cursor.getString(5)));
                ob.setSequence(cursor.getInt(6));
                ob.setMedia_id(Integer.parseInt(cursor.getString(7)));
                ob.setThumbnail_media_id(Integer.parseInt(cursor.getString(8)));
                ob.setLang_resource_name(cursor.getString(9));
                ob.setLang_resource_description(cursor.getString(10));
                ob.setType(cursor.getString(11));
                ob.setUrl(cursor.getString(12));
                ob.setCreated_at(cursor.getString(13));
                ob.setUpdated_at(cursor.getString(14));
                ob.setDeleted_at(cursor.getString(15));

                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    //download MediaEntity
    public boolean upsertDownloadMediaEntity(Data ob) {
        boolean done = false;
        Data data = null;
        if (ob.getId() != 0) {
            data = getDownloadMediaEntityById(ob.getId());
            if (data == null) {
                done = insertDownloadMediaEntity(ob);
            } else {
                done = updateDownloadMediaEntity(ob);
            }
        }
        return done;
    }

    public Data getDownloadMediaEntityById(int id) {
        String query = "SELECT clientId , server_id , name , type , url , file_path , language_id ,created_at , updated_at , deleted_at,Local_file_path  from DownloadMediaEntity WHERE server_id = " + id + " ";

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
            ob.setLocalFilePath(cursor.getString(10));

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public boolean insertDownloadMediaEntity(Data ob) {
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
        values.put("Local_file_path", ob.getLocalFilePath());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("DownloadMediaEntity", null, values);
        db.close();
        return i > 0;
    }

    public boolean updateDownloadMediaEntity(Data ob) {

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
        values.put("Local_file_path", ob.getLocalFilePath());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("DownloadMediaEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "DownloadMediaEntity called with" + " server_id = '" + ob.getId());

        db.close();
        return i > 0;
    }

    public boolean updateDownloadMediaLocalFilePathEntity(Data ob) {

        ContentValues values = new ContentValues();
        values.put("Local_file_path", ob.getLocalFilePath());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("DownloadMediaEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "updateDataEntity called with" + " server_id = '" + ob.getId());

        db.close();
        return i > 0;
    }

    //downloading file
    public boolean upsertDownloadingFileEntity(Data ob) {
        boolean done = false;
        Data data = null;
        if (ob.getMediaId() != 0) {
            data = getDownloadingFileEntityById(ob.getMediaId());
            if (data == null) {
                done = insertDownloadingFileEntity(ob);
            } else {
                done = updateDownloadingFileEntity(ob);
            }
        }
        return done;
    }

    public Data getDownloadingFileEntityById(int id) {
        String query = "SELECT MediaId , progress from DownloadingFileEntity WHERE MediaId = " + id + " ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ob.setMediaId(Integer.parseInt(cursor.getString(0)));
            ob.setProgress(Integer.parseInt(cursor.getString(1)));
            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public boolean insertDownloadingFileEntity(Data ob) {


        ContentValues values = new ContentValues();
        values.put("MediaId", ob.getMediaId());
        values.put("progress", ob.getProgress());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("DownloadingFileEntity", null, values);
        db.close();
        return i > 0;
    }

    public boolean updateDownloadingFileEntity(Data ob) {

        ContentValues values = new ContentValues();
        values.put("MediaId", ob.getMediaId());
        values.put("progress", ob.getProgress());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getMediaId() != 0) {
            i = db.update("DownloadingFileEntity", values, " MediaId = " + ob.getMediaId() + " ", null);
        }

        db.close();
        return i > 0;
    }

    //get All Downloading Media data through join into DownloadMediaEntity table and DownloadingFileEntity
    public List<Data> getAllDownloadingMediaFile() {
        //clientId , server_id , name , type , url , file_path , language_id ,created_at , updated_at , deleted_at, Local_file_path
        String query = "Select DownloadMediaEntity.clientId, DownloadMediaEntity.server_id, DownloadMediaEntity.name, DownloadMediaEntity.type, DownloadMediaEntity.url , "
                + "DownloadMediaEntity.file_path ,  DownloadMediaEntity.language_id , DownloadMediaEntity.created_at , DownloadMediaEntity.updated_at , DownloadMediaEntity.deleted_at, DownloadMediaEntity.Local_file_path , DownloadingFileEntity.MediaId , DownloadingFileEntity.progress FROM DownloadingFileEntity " +
                "INNER JOIN DownloadMediaEntity ON  DownloadingFileEntity.MediaId=DownloadMediaEntity.clientId ";

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
                ob.setLocalFilePath(cursor.getString(10));
                ob.setMediaId(Integer.parseInt(cursor.getString(11)));
                ob.setProgress(Integer.parseInt(cursor.getString(12)));
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    //delete downloaded file data by media id
    public boolean deleteFileDownloadedByMediaId(int id) {
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "MediaId = '" + id + "' ";
        db.delete("DownloadingFileEntity", query, null);
        db.close();
        result = true;
        return result;
    }


    //Medialanguage Latest DataEntity
    public boolean upsertMedialanguageLatestDataEntity(Data ob) {
        boolean done = false;
        Data data = null;
        if (ob.getId() != 0) {
            data = getMedialanguageLatestDataEntityById(ob.getId());
            if (data == null) {
                done = insertMedialanguageLatestDataEntity(ob);
            } else {
                done = updateMedialanguageLatestDataEntity(ob);
            }
        }
        return done;
    }

    //clientId , server_id  , media_id, language_id ,file_path ,url ,created_at , updated_at ,download_url ,created_by ,updated_by ,cloud_transferred
    public Data getMedialanguageLatestDataEntityById(int id) {
        String query = "Select clientId , server_id , media_id ,language_id ,file_path , url,created_at , updated_at,download_url,created_by,updated_by,cloud_transferred  FROM MedialanguageLatestEntity WHERE server_id = " + id + " ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateMedialanguageLatestDataEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public Data getMedialanguageLatestDataEntityByMediaId(int media_id, int languageId) {
        String query = "Select clientId , server_id , media_id ,language_id ,file_path , url,created_at , updated_at,download_url,created_by,updated_by,cloud_transferred  FROM MedialanguageLatestEntity WHERE media_id = " + media_id + " and language_id = " + languageId + "" ;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateMedialanguageLatestDataEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    //clientId , server_id  , media_id, language_id ,file_path ,url ,created_at , updated_at ,download_url ,created_by ,updated_by ,cloud_transferred
    private void populateMedialanguageLatestDataEntity(Cursor cursor, Data ob) {
        ob.setClientId(Integer.parseInt(cursor.getString(0)));
        ob.setId(Integer.parseInt(cursor.getString(1))); // this represets server Id
        ob.setMediaId(Integer.parseInt(cursor.getString(2)));
        ob.setLanguage_id(Integer.parseInt(cursor.getString(3)));
        ob.setFile_path(cursor.getString(4));
        ob.setUrl(cursor.getString(5));
        ob.setCreated_at(cursor.getString(6));
        ob.setUpdated_at(cursor.getString(7));
        ob.setDownload_url(cursor.getString(8));
        ob.setCreated_by(Integer.parseInt(cursor.getString(9)));
        ob.setUpdated_by(Integer.parseInt(cursor.getString(10)));
        ob.setCloud_transferred(Integer.parseInt(cursor.getString(11)));
    }

    //get all data
    public List<Data> getAllMedialanguageLatestDataEntity() {
        String query = "Select clientId , server_id , media_id ,language_id ,file_path , url,created_at , updated_at,download_url,created_by,updated_by,cloud_transferred  FROM MedialanguageLatestEntity ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Data ob = new Data();
                populateMedialanguageLatestDataEntity(cursor, ob);
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    public boolean insertMedialanguageLatestDataEntity(Data ob) {
        //clientId , server_id  , media_id, language_id ,file_path ,url ,created_at , updated_at ,download_url ,created_by ,updated_by ,cloud_transferred

        ContentValues values = new ContentValues();
        values.put("server_id", ob.getId());
        values.put("media_id", ob.getMedia_id());
        values.put("language_id", ob.getLanguage_id());
        values.put("file_path", ob.getFile_path());
        values.put("url", ob.getUrl());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("download_url", ob.getDownload_url());
        values.put("created_by", ob.getCreated_by());
        values.put("updated_by", ob.getUpdated_by());
        values.put("cloud_transferred", ob.getCloud_transferred());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("MedialanguageLatestEntity", null, values);
        db.close();
        return i > 0;
    }

    public boolean updateMedialanguageLatestDataEntity(Data ob) {

        ContentValues values = new ContentValues();
        // values.put("server_id", ob.getId());
        values.put("media_id", ob.getMedia_id());
        values.put("language_id", ob.getLanguage_id());
        values.put("file_path", ob.getFile_path());
        values.put("url", ob.getUrl());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("download_url", ob.getDownload_url());
        values.put("created_by", ob.getCreated_by());
        values.put("updated_by", ob.getUpdated_by());
        values.put("cloud_transferred", ob.getCloud_transferred());


        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("MedialanguageLatestEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "MedialanguageLatestEntity called with" + " server_id = '" + ob.getId());

        db.close();
        return i > 0;
    }


    //Languages DataEntity
    public boolean upsertLanguageDataEntity(LanguageData ob) {
        boolean done = false;
        LanguageData data = null;
        if (ob.getId() != 0) {
            data = getLanguageDataEntityById(ob.getId());
            if (data == null) {
                done = insertLanguageDataEntity(ob);
            } else {
                done = updateLanguageDataEntity(ob);
            }
        }
        return done;
    }

    //id , LanguageId , Name ,DeletedAt
    public LanguageData getLanguageDataEntityById(int id) {
        String query = "Select LanguageId , Name , DeletedAt ,code  FROM LanguageEntity WHERE LanguageId = " + id + " ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        LanguageData ob = new LanguageData();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateLanguageDataEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    private void populateLanguageDataEntity(Cursor cursor, LanguageData ob) {
        ob.setId(Integer.parseInt(cursor.getString(0)));
        ob.setName(cursor.getString(1));
        ob.setDeleted_at(cursor.getString(2));
        ob.setCode(cursor.getString(3));
    }

    //get all language data
    public List<LanguageData> getAllLanguageDataEntity() {
        String query = "Select LanguageId , Name , DeletedAt ,code FROM LanguageEntity ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<LanguageData> list = new ArrayList<LanguageData>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                LanguageData ob = new LanguageData();
                populateLanguageDataEntity(cursor, ob);
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    //insert language
    public boolean insertLanguageDataEntity(LanguageData ob) {
        //id , LanguageId , Name ,DeletedAt

        ContentValues values = new ContentValues();
        values.put("LanguageId", ob.getId());
        values.put("Name", ob.getName());
        values.put("DeletedAt", ob.getDeleted_at());
        values.put("code", ob.getCode());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("LanguageEntity", null, values);
        db.close();
        return i > 0;
    }

    //update language
    public boolean updateLanguageDataEntity(LanguageData ob) {

        ContentValues values = new ContentValues();
        values.put("LanguageId", ob.getId());
        values.put("Name", ob.getName());
        values.put("DeletedAt", ob.getDeleted_at());
        values.put("code", ob.getCode());


        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("LanguageEntity", values, " LanguageId = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "LanguageEntity called with" + " LanguageId = '" + ob.getId());

        db.close();
        return i > 0;
    }


    //Notification DataEntity
    public boolean upsertNotificationDataEntity(Data ob) {
        boolean done = false;
        Data data = null;
        if (ob.getId() != 0) {
            data = getNotificationDataEntityById(ob.getId());
            if (data == null) {
                done = insertNotificationDataEntity(ob);
            } else {
                done = updateNotificationDataEntity(ob);
            }
        }
        return done;
    }

    //id , client_id , send_at ,  message , language_id , status , custom_data ,created_by ,updated_by , created_at , updated_at , deleted_at
    public Data getNotificationDataEntityById(int id) {
        String query = "Select id, client_id , send_at , message ,  language_id , status , custom_data , created_by , updated_by , created_at , updated_at ,deleted_at FROM NotificationEntity WHERE id = " + id + " ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateNotificationDataEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    //get all notification data
    public List<Data> getAllNotificationDataEntity(int languageId) {
        String query = "Select id, client_id , send_at , message ,  language_id , status , custom_data , created_by , updated_by , created_at , updated_at ,deleted_at FROM NotificationEntity " +
                " where language_id = " + languageId;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Data ob = new Data();
                populateNotificationDataEntity(cursor, ob);
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    //id , client_id , send_at ,  message , language_id , status , custom_data ,created_by ,updated_by , created_at , updated_at , deleted_at
    private void populateNotificationDataEntity(Cursor cursor, Data ob) {
        ob.setId(Integer.parseInt(cursor.getString(0)));
        ob.setClientId(Integer.parseInt(cursor.getString(1)));
        ob.setSend_at(cursor.getString(2));
        ob.setMessage(cursor.getString(3));
        ob.setLanguage_id(cursor.getInt(4));
        ob.setStatus(cursor.getString(5));
        ob.setCustom_data(cursor.getString(6));
        ob.setCreated_by(cursor.getInt(7));
        ob.setUpdated_by(cursor.getInt(8));
        ob.setCreated_at(cursor.getString(9));
        ob.setUpdated_at(cursor.getString(10));
        ob.setDeleted_at(cursor.getString(11));

    }

    //insert notification
    public boolean insertNotificationDataEntity(Data ob) {
//id , client_id , send_at ,  message , language_id , status , custom_data ,created_by ,updated_by , created_at , updated_at , deleted_at

        ContentValues values = new ContentValues();
        values.put("id", ob.getId());
        values.put("client_id", ob.getClient_id());
        values.put("send_at", ob.getSend_at());
        values.put("message", ob.getMessage());
        values.put("language_id", ob.getLanguage_id());
        values.put("status", ob.getStatus());
        values.put("custom_data", ob.getCustom_data());
        values.put("created_by", ob.getCreated_by());
        values.put("updated_by", ob.getUpdated_by());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("NotificationEntity", null, values);
        db.close();
        return i > 0;
    }

    //update notification
    public boolean updateNotificationDataEntity(Data ob) {

        ContentValues values = new ContentValues();
        values.put("id", ob.getId());
        values.put("client_id", ob.getClient_id());
        values.put("send_at", ob.getSend_at());
        values.put("message", ob.getMessage());
        values.put("language_id", ob.getLanguage_id());
        values.put("status", ob.getStatus());
        values.put("custom_data", ob.getCustom_data());
        values.put("created_by", ob.getCreated_by());
        values.put("updated_by", ob.getUpdated_by());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("NotificationEntity", values, " id = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "NotificationEntity called with" + " id = '" + ob.getId());

        db.close();
        return i > 0;
    }
}

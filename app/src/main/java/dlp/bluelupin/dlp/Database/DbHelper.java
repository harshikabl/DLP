package dlp.bluelupin.dlp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.FavoritesData;
import dlp.bluelupin.dlp.Models.LanguageData;
import dlp.bluelupin.dlp.Models.QuizAnswer;
import dlp.bluelupin.dlp.Models.SimulatorData;
import dlp.bluelupin.dlp.Utilities.Utility;
//import org.apache.commons.io.FilenameUtils;

/**
 * Created by subod on 21-Jul-16.
 */
public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = Consts.dataBaseName;// Consts.outputDirectoryLocation +  "dlp_db.db"; //Consts.dataBaseName; //

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        if (Consts.IS_DEBUG_LOG) {
//            Log.d(Consts.LOG_TAG, "DB Path is " + DATABASE_NAME + " getDatabasePath" +  context.getDatabasePath(DATABASE_NAME));
//        }
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
        db.execSQL("DROP TABLE IF EXISTS QuizzesEntity");
        db.execSQL("DROP TABLE IF EXISTS QuizzesQuestionsEntity");
        db.execSQL("DROP TABLE IF EXISTS QuestionsOptionsEntity");
        db.execSQL("DROP TABLE IF EXISTS ContentQuizEntity");
        db.execSQL("DROP TABLE IF EXISTS QuizAnswerEntity");
        db.execSQL("DROP TABLE IF EXISTS SimulatorEntity");

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

        String CREATE_MediaEntity_TABLE = "CREATE TABLE MediaEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, name TEXT, type TEXT, url TEXT, download_url TEXT, thumbnail_url TEXT, thumbnail_file_path TEXT,  file_path TEXT, language_id INTEGER , created_at DATETIME, updated_at DATETIME, deleted_at DATETIME, thumbnail_url_Local_file_path TEXT, thumbnail_localFilename Text)";
        //clientId , server_id , name , type , url , file_path , language_id ,created_at , updated_at , deleted_at
        db.execSQL(CREATE_MediaEntity_TABLE);

        String CREATE_MedialanguageLatestDataEntity_TABLE = "CREATE TABLE MedialanguageLatestEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, media_id INTEGER, language_id INTEGER ,file_path TEXT,  url TEXT, download_url TEXT, Local_file_path TEXT, created_by INTEGER, updated_by INTEGER,created_at DATETIME, updated_at DATETIME, deleted_at DATETIME, cloud_transferred INTEGER,localFileName TEXT)";

        db.execSQL(CREATE_MedialanguageLatestDataEntity_TABLE);

        if (!tableAlreadyExists(db, "AccountEntity")) {
            String CREATE_AccountEntity_TABLE = "CREATE TABLE AccountEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, name TEXT, email TEXT, phone TEXT, preferred_language_id INTEGER, role TEXT, api_token TEXT, otp INTEGER, isVerified INTEGER)";
            //clientId, server_id , name , email , phone ,preferred_language_id , role, api_token, otp, isVerified
            db.execSQL(CREATE_AccountEntity_TABLE);
        }

        String CREATE_FavoritesEntity_TABLE = "CREATE TABLE FavoritesEntity(clientId INTEGER PRIMARY KEY, Content_id INTEGER, updated_at DATETIME, Favorites_Flag TEXT, FOREIGN KEY (Content_id) REFERENCES DataEntity(server_id))";
        //clientId, Content_id , updated_at,Favorites_Flag FavoritesEntity
        db.execSQL(CREATE_FavoritesEntity_TABLE);

        String CREATE_DownloadMediaEntity_TABLE = "CREATE TABLE DownloadMediaEntity(clientId INTEGER PRIMARY KEY, server_id INTEGER, name TEXT, type TEXT, url TEXT, file_path TEXT, language_id INTEGER , created_at DATETIME, updated_at DATETIME, deleted_at DATETIME,Local_file_path TEXT)";
        //clientId , server_id , name , type , url , file_path , language_id ,created_at , updated_at , deleted_at
        db.execSQL(CREATE_DownloadMediaEntity_TABLE);

        String CREATE_DownloadingFileEntity_TABLE = "CREATE TABLE DownloadingFileEntity(id INTEGER PRIMARY KEY, MediaId INTEGER, progress INTEGER, parentId INTEGER,isDownloaded INTEGER NOT NULL DEFAULT 0)";
        //id , MediaId , progress,isDownloaded INTEGER
        db.execSQL(CREATE_DownloadingFileEntity_TABLE);


        String CREATE_LanguageEntity_TABLE = "CREATE TABLE LanguageEntity(id INTEGER PRIMARY KEY, LanguageId INTEGER, Name TEXT, DeletedAt TEXT ,code TEXT)";
        //id , LanguageId , Name ,DeletedAt
        db.execSQL(CREATE_LanguageEntity_TABLE);


        String CREATE_NotificationDataEntity_TABLE = "CREATE TABLE NotificationEntity(id INTEGER PRIMARY KEY, client_id INTEGER, send_at DATETIME,  message TEXT, language_id INTEGER, status TEXT, custom_data TEXT, created_by INTEGER, updated_by INTEGER, created_at DATETIME, updated_at DATETIME, deleted_at DATETIME)";
        //id , client_id , send_at ,  message , language_id , status , custom_data ,created_by ,updated_by , created_at , updated_at , deleted_at
        db.execSQL(CREATE_NotificationDataEntity_TABLE);

        String CREATE_QuizzesDataEntity_TABLE = "CREATE TABLE QuizzesEntity(id INTEGER PRIMARY KEY, client_id INTEGER, name TEXT,  description TEXT, created_by INTEGER, updated_by INTEGER, created_at DATETIME, updated_at DATETIME, deleted_at DATETIME)";
        //id , client_id , name, description, created_by,updated_by,created_at,updated_at,deleted_at
        db.execSQL(CREATE_QuizzesDataEntity_TABLE);

        String CREATE_QuizzesQuestionsDataEntity_TABLE = "CREATE TABLE QuizzesQuestionsEntity(id INTEGER PRIMARY KEY, quiz_id INTEGER, sequence INTEGER, lang_resource_description TEXT, media_id INTEGER, audio_media_id INTEGER,answer_audio_media_id INTEGER,type TEXT,lang_resource_correct_answer TEXT, created_by INTEGER, updated_by INTEGER, created_at DATETIME, updated_at DATETIME, deleted_at DATETIME)";
        //id , quiz_id , sequence, lang_resource_description,media_id,audio_media_id,answer_audio_media_id,type,lang_resource_correct_answer, created_by,updated_by,created_at,updated_at,deleted_at
        db.execSQL(CREATE_QuizzesQuestionsDataEntity_TABLE);

        String CREATE_QuestionsOptionsDataEntity_TABLE = "CREATE TABLE QuestionsOptionsEntity(id INTEGER PRIMARY KEY, question_id INTEGER, sequence INTEGER, lang_resource_name TEXT, media_id INTEGER, is_correct INTEGER, created_by INTEGER, updated_by INTEGER, created_at DATETIME, updated_at DATETIME, deleted_at DATETIME)";
        //id , question_id , sequence, lang_resource_name,media_id,is_correct,created_by,updated_by,created_at,updated_at,deleted_at
        db.execSQL(CREATE_QuestionsOptionsDataEntity_TABLE);

        String CREATE_ContentQuizDataEntity_TABLE = "CREATE TABLE ContentQuizEntity(id INTEGER PRIMARY KEY, content_id INTEGER, quiz_id INTEGER,created_by INTEGER, updated_by INTEGER, created_at DATETIME, updated_at DATETIME, deleted_at DATETIME)";
        //id , content_id , quiz_id,created_by,updated_by,created_at,updated_at,deleted_at
        db.execSQL(CREATE_ContentQuizDataEntity_TABLE);

        String CREATE_QuizAnswerEntity_TABLE = "CREATE TABLE QuizAnswerEntity(id INTEGER PRIMARY KEY, quiz_id INTEGER, question_id INTEGER, option_id INTEGER, answer INTEGER, contentId INTEGER)";
        //id,quiz_id , question_id , option_id ,answer,contentId
        db.execSQL(CREATE_QuizAnswerEntity_TABLE);

        String CREATE_SimulatorDataEntity_TABLE = "CREATE TABLE SimulatorEntity(id INTEGER PRIMARY KEY, content_id INTEGER, url TEXT,  download_url TEXT, localPathUrl TEXT,language_id INTEGER,isDeleted INTEGER)";
        //id , content_id , url, download_url,localPathUrl,language_id,isDeleted   SimulatorEntity
        db.execSQL(CREATE_SimulatorDataEntity_TABLE);
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
            /*Log.d(Consts.BASE_URL, "getCacheServiceCall" + "url = '" + ob.getUrl() + "'  dataIdentifier = '" +
                    ob.getDataIdentifier() + " " + ob.getLastCalled());*/
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
        if(i>0){

            Log.d(Consts.LOG_TAG,"*******: updateCacheServiceCall: "+ ob.getUrl());
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
        cursor.close();
       // db.close();
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

        String query = "Select clientId, server_id , name , content , language_id ,created_at , updated_at , deleted_at FROM ResourceEntity WHERE name = '" + name + "' and language_id =" + language_id + " and deleted_at IS  NULL";

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

    public List<Data> getResourcesToDownload(Integer parentId, int languageId) {
        List<Data> resourceListToDownload = new ArrayList<Data>();
        List<Data> children = getDataEntityByParentId(parentId);
        if (children.size() != 0) {
            for (Data child : children) {
                //Data media = getMediaEntityById(child.getMedia_id());
                Data media = getMediaEntityByIdAndLaunguageId(child.getMedia_id(), languageId);
                if (media != null && media.getLocalFilePath() == null) {
                    resourceListToDownload.add(media);
                    if (Consts.IS_DEBUG_LOG) {
                        // Log.d(Consts.LOG_TAG, "added media: " + media.getId() + " type: " + media.getType() + " download url: " + media.getDownload_url() + " localfilePath: " + media.getLocalFilePath());

                    }
                }
                List<Data> childContents = getResourcesToDownload(child.getId(), languageId);
                resourceListToDownload.addAll(childContents);
//                if (Consts.IS_DEBUG_LOG) {
//                    Log.d(Consts.LOG_TAG, "childId: " + child.getId() + " childContents: " + childContents.size());
//                }
            }
        }
        return resourceListToDownload;
    }


    public List<Data> getThumbnailsToDownload(Integer parentId, List<Data> resourceListToDownload, int languageId) {
        List<Data> children = getDataEntityByParentId(parentId);
        if (children.size() <= 0) {
            return resourceListToDownload;
        }
        for (Data child : children) {
            if (child.getThumbnail_media_id() != 0) {
                Data media = getMediaEntityByIdAndLaunguageId(child.getThumbnail_media_id(), languageId);
                if (media != null) {
                    resourceListToDownload.add(media);
                }
            }
            resourceListToDownload.addAll(getThumbnailsToDownload(child.getId(), new ArrayList<Data>(), languageId));
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
        String query = "SELECT clientId , server_id , name , type ,download_url , thumbnail_url , thumbnail_file_path , url , file_path , language_id ,created_at , updated_at , deleted_at, thumbnail_url_Local_file_path, thumbnail_localFilename  from MediaEntity WHERE server_id = " + id + " ";

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
            ob.setThumbnail_url_Local_file_path(cursor.getString(13));
            ob.setThumbnail_localFilename(cursor.getString(14));

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public Data getMediaEntityByIdAndLaunguageId(int id, int languageId) {
        String query = "SELECT clientId , server_id , name , type ,download_url , thumbnail_url , thumbnail_file_path , url , file_path , language_id ,created_at , updated_at , deleted_at, thumbnail_url_Local_file_path, thumbnail_localFilename  from MediaEntity WHERE server_id = " + id + " ";

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
            ob.setThumbnail_url_Local_file_path(cursor.getString(13));
            ob.setThumbnail_localFilename(cursor.getString(14));
            cursor.close();

            Data mediaLanguage = getMedialanguageLatestDataEntityByMediaId(ob.getId(), languageId);
            if (mediaLanguage != null) {
                mediaLanguage.setType(ob.getType());
                mediaLanguage.setThumbnail_url(ob.getThumbnail_url());
                mediaLanguage.setThumbnail_url_Local_file_path(ob.getThumbnail_url_Local_file_path());
                ob = mediaLanguage;
            } else {
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "** Getting default language 1 medialanguage,/n getMediaEntityByIdAndLaunguageId: mediaLanguage is null for languageId:" + languageId + "media:" + ob);
                }
                Data mediaDefualtLanguage = getMedialanguageLatestDataEntityByMediaId(ob.getId(), 1);
                if (mediaDefualtLanguage != null) {
                    mediaDefualtLanguage.setType(ob.getType());
                    mediaDefualtLanguage.setThumbnail_url(ob.getThumbnail_url());
                    mediaDefualtLanguage.setThumbnail_url_Local_file_path(ob.getThumbnail_url_Local_file_path());
                    ob = mediaDefualtLanguage;
                } else {
                    Log.d(Consts.LOG_TAG, "** THIS SHOULD NOT HAPPEN. SERVER INVALID DATA,/n getMediaEntityByIdAndLaunguageId:Even default  mediaLanguage is null for languageId:" + 1 + "media:" + ob);
                }
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
        if (ob.getThumbnail_file_path() != null) {
            values.put("thumbnail_localFilename", Utility.getFileNameWithoutExtension(ob.getThumbnail_file_path()));
        }

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
        //values.put("thumbnail_file_path", ob.getThumbnail_file_path()); // this should not update

        values.put("file_path", ob.getFile_path());
        values.put("language_id", ob.getLanguage_id());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());
        if (ob.getThumbnail_file_path() != null) {
            values.put("thumbnail_localFilename", Utility.getFileNameWithoutExtension(ob.getThumbnail_file_path()));
        }

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("MediaEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        //Log.d(Consts.LOG_TAG, "updateDataEntity called with" + " server_id = '" + ob.getId());

        db.close();
        return i > 0;
    }

    public boolean updateMediaThumbnailLocalFilePathEntity(Data ob) {

        ContentValues values = new ContentValues();
        values.put("thumbnail_url_Local_file_path", ob.getThumbnail_url_Local_file_path());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("MediaEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        db.close();
        return i > 0;
    }

    public boolean updateMediaThumbnailLocalFilePathBasedOnName(String localThumbnailFileName, String localThumbnailFilePath) {

        ContentValues values = new ContentValues();
        values.put("thumbnail_url_Local_file_path", localThumbnailFilePath);

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (localThumbnailFileName != null) {
            i = db.update("MediaEntity", values, " thumbnail_localFilename = '" + localThumbnailFileName + "' ", null);
        }
        db.close();
        if (Consts.IS_DEBUG_LOG) {
            if (i > 0) {
                Log.d(Consts.LOG_TAG, "updateMediaThumbnailLocalFilePathBasedOnName: media with fileName updated: " + localThumbnailFileName + " with path " + localThumbnailFilePath);
            } else {
                Log.d(Consts.LOG_TAG, "updateMediaThumbnailLocalFilePathBasedOnName: media with fileName NOT found and updated: " + localThumbnailFileName + " with Path " + localThumbnailFilePath);
            }
        }
        return i > 0;
    }

    public boolean updateMediaLanguageLocalFilePathEntity(Data ob) {

        ContentValues values = new ContentValues();
        values.put("Local_file_path", ob.getLocalFilePath());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("MedialanguageLatestEntity", values, " server_id = " + ob.getId() + " ", null);
        }
        db.close();
        return i > 0;
    }

    public boolean updateMediaLanguageLocalFilePathBasedOnFilePath(String localFileName, String localFilePath) {

        ContentValues values = new ContentValues();
        values.put("Local_file_path", localFilePath);

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (localFileName != null) {
            i = db.update("MedialanguageLatestEntity", values, " localFileName = '" + localFileName + "' ", null);
        }
        db.close();
        if (Consts.IS_DEBUG_LOG) {
            if (i > 0) {
                Log.d(Consts.LOG_TAG, "updateMediaLanguageLocalFilePathBasedOnFilePath: media with fileName updated: " + localFileName + " with path " + localFilePath);
            } else {
                Log.d(Consts.LOG_TAG, "updateMediaLanguageLocalFilePathBasedOnFilePath: media with fileName NOT found and updated: " + localFileName + " with Path " + localFilePath);
            }
        }
        return i > 0;
    }

    public void setLocalPathOfAllMediaToNull() {
        List<Data> medias = getAllMedia();
        SQLiteDatabase db = this.getWritableDatabase();
        for (Data ob : medias) {
            ContentValues values = new ContentValues();
            values.put("Local_file_path", "");
            if (ob.getId() != 0) {
                db.update("MediaEntity", values, " server_id = " + ob.getId() + " ", null);
            }
        }
        db.close();
    }

    public List<Data> getAllMedia() {
        String query = "Select clientId , server_id , name , type , download_url , thumbnail_url , thumbnail_file_path , url , file_path , language_id ,created_at , updated_at , deleted_at, thumbnail_url_Local_file_path, thumbnail_localFilename FROM MediaEntity";

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
                ob.setThumbnail_url_Local_file_path(cursor.getString(13));
                ob.setThumbnail_localFilename(cursor.getString(14));
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }
    // endregion MediaEntity


    //account data_item save
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
        // values.put("isVerified", accountData.getIsVerified());


        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.update("AccountEntity", values, "phone = '" + accountData.getPhone() + "'", null);

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

    //favorites data_item save
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

    //get favorites data_item into FavoritesEntity
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

    //get chapters favorites and topic list data_item
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
    public boolean deleteFavoriteData() {
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("FavoritesEntity", null, null);
        db.close();
        result = true;
        return result;
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
        if (ob.getId() != 0) {
            data = getDownloadingFileEntityById(ob.getId());
            if (data == null) {
                done = insertDownloadingFileEntity(ob);
            } else {
                done = updateDownloadingFileEntity(ob);
            }
        }
        return done;
    }

    public List<Data> getAllDownloadingFileEntity() {
        String query = "SELECT MediaId , progress, parentId from DownloadingFileEntity";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Data ob = new Data();
                ob.setMediaId(Integer.parseInt(cursor.getString(0)));
                ob.setProgress(Integer.parseInt(cursor.getString(1)));
                ob.setParent_id(Integer.parseInt(cursor.getString(2)));
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    public Data getDownloadingFileEntityById(int id) {
        String query = "SELECT MediaId , progress, parentId from DownloadingFileEntity WHERE MediaId = " + id + " ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ob.setMediaId(Integer.parseInt(cursor.getString(0)));
            ob.setProgress(Integer.parseInt(cursor.getString(1)));
            ob.setParent_id(Integer.parseInt(cursor.getString(2)));
            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }


    public boolean insertDownloadingFileEntity(Data ob) {


        ContentValues values = new ContentValues();
        values.put("MediaId", ob.getId());
        values.put("progress", ob.getProgress());
        values.put("parentId", ob.getParent_id());
        values.put("isDownloaded", ob.getIsDownloaded());

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("DownloadingFileEntity", null, values);
        db.close();
        return i > 0;
    }

    public boolean updateDownloadingFileEntity(Data ob) {

        ContentValues values = new ContentValues();
        values.put("MediaId", ob.getId());
        values.put("progress", ob.getProgress());
        values.put("parentId", ob.getParent_id());

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("DownloadingFileEntity", values, " MediaId = " + ob.getId() + " ", null);
        }

        db.close();
        return i > 0;
    }

    //update isDownloaded flag for show downloaded file
    public boolean updateDownloadedFileFlag(int mediaId, int downloadFlag) {

        ContentValues values = new ContentValues();
        values.put("isDownloaded", downloadFlag);

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (mediaId != 0) {
            i = db.update("DownloadingFileEntity", values, " MediaId = " + mediaId + " ", null);
        }

        db.close();
        return i > 0;
    }

    //get All Downloading Media data
    public List<Data> getAllDownloadingMediaFile(int languageId) {
        String query = "SELECT MediaId , progress, parentId from DownloadingFileEntity where isDownloaded=0";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
//                Data ob = new Data();
//                ob.setMediaId(Integer.parseInt(cursor.getString(0)));
//                ob.setProgress(Integer.parseInt(cursor.getString(1)));
                Data media = getMediaEntityByIdAndLaunguageId(Integer.parseInt(cursor.getString(0)), languageId);
                if (media != null) {
                    media.setParent_id(Integer.parseInt(cursor.getString(2)));
                    list.add(media);

                }
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    //get All Downloaded Media data
    public List<Data> getAllDownloadedMediaFile(int languageId) {
        String query = "SELECT MediaId , progress, parentId from DownloadingFileEntity where isDownloaded=1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
//                Data ob = new Data();
//                ob.setMediaId(Integer.parseInt(cursor.getString(0)));
//                ob.setProgress(Integer.parseInt(cursor.getString(1)));
                Data media = getMediaEntityByIdAndLaunguageId(Integer.parseInt(cursor.getString(0)), languageId);
                if (media != null) {
                    media.setParent_id(Integer.parseInt(cursor.getString(2)));
                    list.add(media);

                }
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    //delete downloaded file  by parent id
    public boolean deleteFileDownloadedByParentId(int id) {
        long i = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "parentId = '" + id + "' ";
        i = db.delete("DownloadingFileEntity", query, null);
        db.close();
        return i > 0;
    }

    //delete downloaded file  by MediaId
    public boolean deleteFileDownloadedByMediaId(int id) {
        long i = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "MediaId = '" + id + "' ";
        db.delete("DownloadingFileEntity", query, null);
        db.close();

        if (Consts.IS_DEBUG_LOG) {
            if (i > 0) {
                Log.d(Consts.LOG_TAG, "deleting row from DownloadingFileEntity with mediaId: " + id);
            } else {
                // Log.d(Consts.LOG_TAG, "Unsuccessful deleting row from DownloadingFileEntity with mediaId: " + id);
            }
        }
        return i > 0;
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
        String query = "Select clientId , server_id , media_id , language_id  ,file_path , url , download_url , Local_file_path , created_by , updated_by ,created_at , updated_at , deleted_at , cloud_transferred, localFileName   FROM MedialanguageLatestEntity WHERE server_id = " + id + " ";

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
        String query = "Select clientId , server_id , media_id , language_id  ,file_path , url , download_url , Local_file_path , created_by , updated_by ,created_at , updated_at , deleted_at , cloud_transferred,localFileName    FROM MedialanguageLatestEntity WHERE media_id = " + media_id + " and language_id = " + languageId + "";

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


    //clientId , server_id , media_id , language_id  ,file_path , url , download_url , Local_file_path , created_by , updated_by ,created_at , updated_at , deleted_at , cloud_transferred
    private void populateMedialanguageLatestDataEntity(Cursor cursor, Data ob) {
        ob.setClientId(Integer.parseInt(cursor.getString(0)));
        ob.setId(Integer.parseInt(cursor.getString(1))); // this represets server Id
        ob.setMediaId(Integer.parseInt(cursor.getString(2)));
        ob.setLanguage_id(Integer.parseInt(cursor.getString(3)));
        ob.setFile_path(cursor.getString(4));
        ob.setUrl(cursor.getString(5));
        ob.setDownload_url(cursor.getString(6));
        ob.setLocalFilePath(cursor.getString(7));
        ob.setCreated_by(Integer.parseInt(cursor.getString(8)));
        ob.setUpdated_by(Integer.parseInt(cursor.getString(9)));
        ob.setCreated_at(cursor.getString(10));
        ob.setUpdated_at(cursor.getString(11));
        ob.setDeleted_at(cursor.getString(12));
        ob.setCloud_transferred(Integer.parseInt(cursor.getString(13)));
        ob.setLocalFileName(cursor.getString(14));
    }

    //get all data_item
    public List<Data> getAllMedialanguageLatestDataEntity() {
        String query = "Select clientId , server_id , media_id , language_id  ,file_path , url , download_url , Local_file_path , created_by , updated_by ,created_at , updated_at , deleted_at , cloud_transferred, localFileName  FROM MedialanguageLatestEntity ";

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
        values.put("download_url", ob.getDownload_url());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("download_url", ob.getDownload_url());
        values.put("created_by", ob.getCreated_by());
        values.put("updated_by", ob.getUpdated_by());
        values.put("cloud_transferred", ob.getCloud_transferred());
        if (ob.getFile_path() != null) {
            values.put("localFileName", Utility.getFileNameWithoutExtension(ob.getFile_path()));
        }

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
        values.put("download_url", ob.getDownload_url());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("download_url", ob.getDownload_url());
        values.put("created_by", ob.getCreated_by());
        values.put("updated_by", ob.getUpdated_by());
        values.put("cloud_transferred", ob.getCloud_transferred());
        if (ob.getFile_path() != null) {
            values.put("localFileName", Utility.getFileNameWithoutExtension(ob.getFile_path()));
        }

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

    //get all language data_item
    public List<LanguageData> getAllLanguageDataEntity() {
        String query = "Select LanguageId , Name , DeletedAt ,code FROM LanguageEntity ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<LanguageData> list = new ArrayList<LanguageData>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                LanguageData ob = new LanguageData();
                populateLanguageDataEntity(cursor, ob);
              /*  if (!ob.getCode().equalsIgnoreCase("or-IN")) {//for Oriya language disable
                    list.add(ob);
                }*/
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

    //get all notification data_item
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

    //------------------Quizzes Entity--------------------------
    public boolean upsertQuizzesDataEntity(Data ob) {
        boolean done = false;
        Data data = null;
        if (ob.getId() != 0) {
            data = getQuizzesDataEntityById(ob.getId());
            if (data == null) {
                done = insertQuizzesDataEntity(ob);
            } else {
                done = updateQuizzesDataEntity(ob);
            }
        }
        return done;
    }

    public Data getQuizzesDataEntityById(int id) {
        String query = "Select * FROM QuizzesEntity WHERE id = '" + id + "' ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateQuizzesDataEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    //get all Quizzes data_item
    public List<Data> getAllQuizzesDataEntity(int languageId) {
        String query = "Select * FROM QuizzesEntity where language_id = '" + languageId + "' and deleted_at IS  NULL";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Data ob = new Data();
                populateQuizzesDataEntity(cursor, ob);
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    //id , client_id , name, description, created_by,updated_by,created_at,updated_at,deleted_at
    private void populateQuizzesDataEntity(Cursor cursor, Data ob) {
        ob.setId(cursor.getInt(0));
        ob.setClientId(cursor.getInt(1));
        ob.setName(cursor.getString(2));
        ob.setDescription(cursor.getString(3));
        ob.setCreated_by(cursor.getInt(4));
        ob.setUpdated_by(cursor.getInt(5));
        ob.setCreated_at(cursor.getString(6));
        ob.setUpdated_at(cursor.getString(7));
        ob.setDeleted_at(cursor.getString(8));
    }

    //insert Quizzes
    public boolean insertQuizzesDataEntity(Data ob) {

        ContentValues values = new ContentValues();
        populateQuizzesValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("QuizzesEntity", null, values);
        db.close();
        return i > 0;
    }

    //id , client_id , name, description, created_by,updated_by,created_at,updated_at,deleted_at
    private void populateQuizzesValues(ContentValues values, Data ob) {
        values.put("id", ob.getId());
        values.put("client_id", ob.getClient_id());
        values.put("name", ob.getName());
        values.put("description", ob.getDescription());
        values.put("created_by", ob.getCreated_by());
        values.put("updated_by", ob.getUpdated_by());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());
    }

    //update Quizzes
    public boolean updateQuizzesDataEntity(Data ob) {

        ContentValues values = new ContentValues();
        populateQuizzesValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("QuizzesEntity", values, " id = '" + ob.getId() + "' ", null);
        }

        db.close();
        return i > 0;
    }

    //-----------Quizzes QuestionsEntity------------------
    public boolean upsertQuizzesQuestionsDataEntity(Data ob) {
        boolean done = false;
        Data data = null;
        if (ob.getId() != 0) {
            data = getQuizzesQuestionsDataEntityById(ob.getId());
            if (data == null) {
                done = insertQuizzesQuestionsDataEntity(ob);
            } else {
                done = updateQuizzesQuestionsDataEntity(ob);
            }
        }
        return done;
    }

    public Data getQuizzesQuestionsDataEntityById(int id) {
        String query = "Select * FROM QuizzesQuestionsEntity WHERE id = '" + id + "' ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateQuizzesQuestionsDataEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    //get all Quizzes Questions data_item
    public List<Data> getAllQuizzesQuestionsDataEntity(int quizId) {
        String query = "Select * FROM QuizzesQuestionsEntity where quiz_id = '" + quizId + "' and deleted_at IS  NULL";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Data ob = new Data();
                populateQuizzesQuestionsDataEntity(cursor, ob);
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    //get Question details with quizId and questionid
    public Data getQuestionDetailsData(int quizId, int questionId) {
        String query = "Select * FROM QuizzesQuestionsEntity where quiz_id = '" + quizId + "' and id = '" + questionId + "' and deleted_at IS  NULL";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateQuizzesQuestionsDataEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    private void populateQuizzesQuestionsDataEntity(Cursor cursor, Data ob) {
        ob.setId(cursor.getInt(0));
        ob.setQuiz_id(cursor.getInt(1));
        ob.setSequence(cursor.getInt(2));
        ob.setLang_resource_description(cursor.getString(3));
        ob.setMedia_id(cursor.getInt(4));
        ob.setAudio_media_id(cursor.getInt(5));
        ob.setAnswer_audio_media_id(cursor.getInt(6));
        ob.setType(cursor.getString(7));
        ob.setLang_resource_correct_answer(cursor.getString(8));
        ob.setCreated_by(cursor.getInt(9));
        ob.setUpdated_by(cursor.getInt(10));
        ob.setCreated_at(cursor.getString(11));
        ob.setUpdated_at(cursor.getString(12));
        ob.setDeleted_at(cursor.getString(13));
    }

    //insert Quizzes Questions
    public boolean insertQuizzesQuestionsDataEntity(Data ob) {

        ContentValues values = new ContentValues();
        populateQuizzesQuestionsValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("QuizzesQuestionsEntity", null, values);
        db.close();
        return i > 0;
    }

    private void populateQuizzesQuestionsValues(ContentValues values, Data ob) {
        values.put("id", ob.getId());
        values.put("quiz_id", ob.getQuiz_id());
        values.put("sequence", ob.getSequence());
        values.put("lang_resource_description", ob.getLang_resource_description());
        values.put("media_id", ob.getMedia_id());
        values.put("audio_media_id", ob.getAudio_media_id());
        values.put("answer_audio_media_id", ob.getAnswer_audio_media_id());
        values.put("type", ob.getType());
        values.put("lang_resource_correct_answer", ob.getLang_resource_correct_answer());
        values.put("created_by", ob.getCreated_by());
        values.put("updated_by", ob.getUpdated_by());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());
    }

    //update Quizzes Questions
    public boolean updateQuizzesQuestionsDataEntity(Data ob) {

        ContentValues values = new ContentValues();
        populateQuizzesQuestionsValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("QuizzesQuestionsEntity", values, " id = '" + ob.getId() + "' ", null);
        }

        db.close();
        return i > 0;
    }

    //--------------------Questions Options Entity---------------------
    public boolean upsertQuestionsOptionsDataEntity(Data ob) {
        boolean done = false;
        Data data = null;
        if (ob.getId() != 0) {
            data = getQuestionsOptionsDataEntityById(ob.getId());
            if (data == null) {
                done = insertQuestionsOptionsDataEntity(ob);
            } else {
                done = updateQuestionsOptionsDataEntity(ob);
            }
        }
        return done;
    }

    public Data getQuestionsOptionsDataEntityById(int id) {
        String query = "Select * FROM QuestionsOptionsEntity WHERE id = '" + id + "' ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateQuestionsOptionsDataEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    //get all Quizzes Questions Options data_item
    public List<Data> getAllQuestionsOptionsDataEntity(int id) {
        String query = "Select * FROM QuestionsOptionsEntity where question_id = '" + id + "' and deleted_at IS  NULL";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Data ob = new Data();
                populateQuestionsOptionsDataEntity(cursor, ob);
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    private void populateQuestionsOptionsDataEntity(Cursor cursor, Data ob) {
        ob.setId(cursor.getInt(0));
        ob.setQuestion_id(cursor.getInt(1));
        ob.setSequence(cursor.getInt(2));
        ob.setLang_resource_name(cursor.getString(3));
        ob.setMedia_id(cursor.getInt(4));
        ob.setIs_correct(cursor.getInt(5));
        ob.setCreated_by(cursor.getInt(6));
        ob.setUpdated_by(cursor.getInt(7));
        ob.setCreated_at(cursor.getString(8));
        ob.setUpdated_at(cursor.getString(9));
        ob.setDeleted_at(cursor.getString(10));
    }

    //insert Quizzes Questions Options
    public boolean insertQuestionsOptionsDataEntity(Data ob) {

        ContentValues values = new ContentValues();
        populateQuestionsOptionsValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("QuestionsOptionsEntity", null, values);
        db.close();
        return i > 0;
    }

    private void populateQuestionsOptionsValues(ContentValues values, Data ob) {
        values.put("id", ob.getId());
        values.put("question_id", ob.getQuestion_id());
        values.put("sequence", ob.getSequence());
        values.put("lang_resource_name", ob.getLang_resource_name());
        values.put("media_id", ob.getMedia_id());
        values.put("is_correct", ob.getIs_correct());
        values.put("created_by", ob.getCreated_by());
        values.put("updated_by", ob.getUpdated_by());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());
    }

    //update Quizzes Questions Options
    public boolean updateQuestionsOptionsDataEntity(Data ob) {

        ContentValues values = new ContentValues();
        populateQuestionsOptionsValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("QuestionsOptionsEntity", values, " id = '" + ob.getId() + "' ", null);
        }

        db.close();
        return i > 0;
    }

    //------------------Content Quiz Entity-------------
    public boolean upsertContentQuizEntity(Data ob) {
        boolean done = false;
        Data data = null;
        if (ob.getId() != 0) {
            data = getContentQuizEntityById(ob.getId());
            if (data == null) {
                done = insertContentQuizEntity(ob);
            } else {
                done = updateContentQuizEntity(ob);
            }
        }
        return done;
    }

    public Data getContentQuizEntityById(int id) {
        String query = "Select * FROM ContentQuizEntity WHERE id = '" + id + "' ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateContentQuizEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    public Data getContentQuizEntityByContentId(int contentId) {
        String query = "Select * FROM ContentQuizEntity WHERE content_id = '" + contentId + "' and deleted_at IS  NULL";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Data ob = new Data();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateContentQuizEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    //get all Content Quiz data_item
    public List<Data> getAllContentQuizEntity() {
        String query = "Select * FROM ContentQuizEntity WHERE deleted_at IS  NULL";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Data> list = new ArrayList<Data>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Data ob = new Data();
                populateContentQuizEntity(cursor, ob);
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }


    private void populateContentQuizEntity(Cursor cursor, Data ob) {
        ob.setId(cursor.getInt(0));
        ob.setContent_id(cursor.getInt(1));
        ob.setQuiz_id(cursor.getInt(2));
        ob.setCreated_by(cursor.getInt(3));
        ob.setUpdated_by(cursor.getInt(4));
        ob.setCreated_at(cursor.getString(5));
        ob.setUpdated_at(cursor.getString(6));
        ob.setDeleted_at(cursor.getString(7));
    }

    //insert Content Quiz
    public boolean insertContentQuizEntity(Data ob) {

        ContentValues values = new ContentValues();
        populateContentQuizValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("ContentQuizEntity", null, values);
        db.close();
        return i > 0;
    }

    //id , content_id , quiz_id,created_by,updated_by,created_at,updated_at,deleted_at
    private void populateContentQuizValues(ContentValues values, Data ob) {
        values.put("id", ob.getId());
        values.put("content_id", ob.getContent_id());
        values.put("quiz_id", ob.getQuiz_id());
        values.put("created_by", ob.getCreated_by());
        values.put("updated_by", ob.getUpdated_by());
        values.put("created_at", ob.getCreated_at());
        values.put("updated_at", ob.getUpdated_at());
        values.put("deleted_at", ob.getDeleted_at());
    }

    //update Content Quiz
    public boolean updateContentQuizEntity(Data ob) {

        ContentValues values = new ContentValues();
        populateContentQuizValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("ContentQuizEntity", values, " id = '" + ob.getId() + "' ", null);
        }

        db.close();
        return i > 0;
    }

    //-----------------Quiz Answer Entity--------------------
    public boolean upsertQuizAnswerEntity(QuizAnswer ob) {
        boolean done = false;
        QuizAnswer data = null;
        if (ob.getQuizId() != 0) {
            data = getQuizAnswerEntityById(ob.getQuizId(), ob.getQuestionId(), ob.getContentId());
            if (data == null) {
                done = insertQuizAnswerEntity(ob);
            } else {
                done = updateQuizAnswerEntity(ob);
            }
        }
        return done;
    }

    public QuizAnswer getQuizAnswerEntityById(int quizId, int questionId, int contentId) {
        String query = "Select * FROM QuizAnswerEntity WHERE quiz_id = '" + quizId + "' and question_id='" + questionId + "' and contentId='" + contentId + "' ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        QuizAnswer ob = new QuizAnswer();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateQuizAnswerEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    //get all Quiz Answer Entity data_item
    public List<QuizAnswer> getAllQuizAnswerEntity(int quizId, int contentId) {
        String query = "Select * FROM QuizAnswerEntity WHERE quiz_id = '" + quizId + "' and contentId='" + contentId + "' and answer='" + 1 + "' ";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<QuizAnswer> list = new ArrayList<QuizAnswer>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                QuizAnswer ob = new QuizAnswer();
                populateQuizAnswerEntity(cursor, ob);
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    private void populateQuizAnswerEntity(Cursor cursor, QuizAnswer ob) {
        //ob.setId(cursor.getInt(0));
        ob.setQuizId(cursor.getInt(1));
        ob.setQuestionId(cursor.getInt(2));
        ob.setOptionId(cursor.getInt(3));
        ob.setAnswer(cursor.getInt(4));
        ob.setContentId(cursor.getInt(5));
    }

    //insert Quiz Answer Entity
    public boolean insertQuizAnswerEntity(QuizAnswer ob) {

        ContentValues values = new ContentValues();
        populateQuizAnswerEntityValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("QuizAnswerEntity", null, values);
        db.close();
        return i > 0;
    }

    private void populateQuizAnswerEntityValues(ContentValues values, QuizAnswer ob) {
        values.put("quiz_id", ob.getQuizId());
        values.put("question_id", ob.getQuestionId());
        values.put("option_id", ob.getOptionId());
        values.put("answer", ob.getAnswer());
        values.put("contentId", ob.getContentId());
    }

    //quiz_id , question_id , option_id ,answer  QuizAnswerEntity
    //update Quiz Answer Entity
    public boolean updateQuizAnswerEntity(QuizAnswer ob) {

        ContentValues values = new ContentValues();
        populateQuizAnswerEntityValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getQuizId() != 0) {
            i = db.update("QuizAnswerEntity", values, " quiz_id = '" + ob.getQuizId() + "' and contentId='" + ob.getContentId() + "' and question_id='" + ob.getQuestionId() + "' ", null);
        }

        db.close();
        return i > 0;
    }

    // delete QuizAnswerEntity Data ById
    public boolean deleteQuizAnswerEntityById(int quizId, int contentId) {
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "quiz_id = '" + quizId + "' and contentId='" + contentId + "'";
        db.delete("QuizAnswerEntity", query, null);
        db.close();
        return result;
    }

    //-----------------SimulatorData Entity--------------------
    public boolean upsertSimulatorEntity(SimulatorData ob) {
        boolean done = false;
        SimulatorData data = null;
        if (ob.getId() != 0) {
            data = getSimulatorEntityById(ob.getId());
            if (data == null) {
                done = insertSimulatorEntity(ob);
            } else {
                done = updateSimulatorEntity(ob);
            }
        }
        return done;
    }

    public SimulatorData getSimulatorEntityById(int id) {
        String query = "Select * FROM SimulatorEntity WHERE id = '" + id + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        SimulatorData ob = new SimulatorData();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            populateSimulatorEntity(cursor, ob);

            cursor.close();
        } else {
            ob = null;
        }
        db.close();
        return ob;
    }

    //get all SimulatorData Entity data_item
    public List<SimulatorData> getAllSimulatorEntity(int parentId) {
        String query = "Select * FROM SimulatorEntity WHERE content_id = '" + parentId + "' WHERE deleted_at IS  NULL";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<SimulatorData> list = new ArrayList<SimulatorData>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                SimulatorData ob = new SimulatorData();
                populateSimulatorEntity(cursor, ob);
                list.add(ob);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    private void populateSimulatorEntity(Cursor cursor, SimulatorData ob) {
        ob.setId(cursor.getInt(0));
        ob.setParentId(cursor.getInt(1));
        ob.setUrl(cursor.getString(2));
        ob.setDownloadUrl(cursor.getString(3));
        ob.setLocalPathUrl(cursor.getString(4));
        ob.setLanguageId(cursor.getInt(5));
        ob.setIsDeleted(cursor.getInt(6));
    }

    //insert SimulatorData Entity
    public boolean insertSimulatorEntity(SimulatorData ob) {

        ContentValues values = new ContentValues();
        populateSimulatorEntityValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();

        long i = db.insert("SimulatorEntity", null, values);
        db.close();
        return i > 0;
    }

    private void populateSimulatorEntityValues(ContentValues values, SimulatorData ob) {
        values.put("id", ob.getId());
        values.put("content_id", ob.getParentId());
        values.put("url", ob.getUrl());
        values.put("download_url", ob.getDownloadUrl());
        values.put("localPathUrl", ob.getLocalPathUrl());
        values.put("language_id", ob.getLanguageId());
        values.put("isDeleted", ob.getIsDeleted());
    }

    //id , content_id , url, download_url,localPathUrl,language_id,isDeleted   SimulatorEntity
    //update SimulatorData Entity
    public boolean updateSimulatorEntity(SimulatorData ob) {

        ContentValues values = new ContentValues();
        populateSimulatorEntityValues(values, ob);

        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        if (ob.getId() != 0) {
            i = db.update("SimulatorEntity", values, " id = '" + ob.getId() + "' ", null);
        }

        db.close();
        return i > 0;
    }
}

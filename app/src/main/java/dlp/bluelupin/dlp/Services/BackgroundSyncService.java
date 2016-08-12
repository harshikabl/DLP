package dlp.bluelupin.dlp.Services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;
import dlp.bluelupin.dlp.Models.Data;

/**
 * Created by subod on 08-Aug-16.
 */
public class BackgroundSyncService   extends IntentService {

    private int result = Activity.RESULT_CANCELED;

    public BackgroundSyncService() {
        super("BackgroundSyncService");
    }

    // Will be called asynchronously be Android
    @Override
    protected void onHandleIntent(Intent intent) {
        if(Consts.IS_DEBUG_LOG)
            Log.d(Consts.LOG_TAG, "starting BackgroundSyncService onHandleIntent");
       // Uri data = intent.getData();
        String someData = intent.getStringExtra("appName");
        if(Consts.IS_DEBUG_LOG)
            Log.d(Consts.LOG_TAG, someData);

        Bundle extras = intent.getExtras();

        ContentServiceRequest request = new ContentServiceRequest();
        callContentAsync(extras);
        callResourceAsync(extras);
        callMediaAsync(extras);
        result = Activity.RESULT_OK; // success

        sendMessageIfAllCallsDone(extras);
    }

    private Boolean contentCallDone = false, resourceCallDone = false, mediaCallDone  = false;
    private void sendMessageIfAllCallsDone(Bundle extras)
    {
        if(contentCallDone && resourceCallDone && mediaCallDone)
        {
            if(Consts.IS_DEBUG_LOG)
            {
                Log.d(Consts.LOG_TAG, "BackgroundSyncServicemessage: ALL DONE");
            }
//            if (extras != null) {
//                Messenger messenger = (Messenger) extras.get("MESSENGER");
//                Message msg = Message.obtain();
//                msg.arg1 = result;
//                //msg.obj = output.getAbsolutePath();
//                try {
//                    messenger.send(msg);
//                    Log.d(Consts.LOG_TAG, "BackgroundSyncServicemessage: send");
//                } catch (android.os.RemoteException e1) {
//                    Log.d(Consts.LOG_TAG, "BackgroundSyncService: Exception sending message", e1);
//                }
//
//            }
        }
    }

    private void callContentAsync(final Bundle extras) {
        ContentServiceRequest request = new ContentServiceRequest();
        request.setPage(1);
        DbHelper db = new DbHelper(BackgroundSyncService.this);
        CacheServiceCallData cacheSeviceCallData = db.getCacheServiceCallByUrl(Consts.URL_CONTENT_LATEST);
        if (cacheSeviceCallData != null) {
            request.setStart_date(cacheSeviceCallData.getLastCalled());
            Log.d(Consts.LOG_TAG, "MainActivity: cacheSeviceCallData : " + cacheSeviceCallData.getLastCalled());
        }
        ServiceCaller sc = new ServiceCaller(BackgroundSyncService.this);
        sc.getAllContent(request, new IAsyncWorkCompletedCallback() {
            @Override
            public void onDone(String workName, boolean isComplete) {
                Log.d(Consts.LOG_TAG, "MainActivity: callContentAsync success result: " + isComplete);
                DbHelper db = new DbHelper(BackgroundSyncService.this);
                List<Data> data = db.getDataEntityByParentId(null);
                Log.d(Consts.LOG_TAG, "MainActivity: data count: " + data.size());
                contentCallDone = true;
                sendMessageIfAllCallsDone(extras);
            }
        });
    }

    private void callResourceAsync(final Bundle extras) {
        ContentServiceRequest request = new ContentServiceRequest();
        request.setPage(1);
        DbHelper db = new DbHelper(BackgroundSyncService.this);
        CacheServiceCallData cacheSeviceCallData = db.getCacheServiceCallByUrl(Consts.URL_LANGUAGE_RESOURCE_LATEST);
        if (cacheSeviceCallData != null) {
            request.setStart_date(cacheSeviceCallData.getLastCalled());
            Log.d(Consts.LOG_TAG, "MainActivity: cacheSeviceCallData for URL_LANGUAGE_RESOURCE_LATEST: " + cacheSeviceCallData.getLastCalled());
        }
        ServiceCaller sc = new ServiceCaller(BackgroundSyncService.this);
        sc.getAllResource(request, new IAsyncWorkCompletedCallback() {
            @Override
            public void onDone(String workName, boolean isComplete) {
                Log.d(Consts.LOG_TAG, "MainActivity: callResourceAsync success result: " + isComplete);
                DbHelper db = new DbHelper(BackgroundSyncService.this);
                List<Data> data = db.getResources();
                Log.d(Consts.LOG_TAG, "MainActivity: callResourceAsync data count: " + data.size());
                resourceCallDone = true;
                sendMessageIfAllCallsDone(extras);
            }
        });
    }

    private void callMediaAsync(final Bundle extras) {
        ContentServiceRequest request = new ContentServiceRequest();
        request.setPage(1);
        DbHelper db = new DbHelper(BackgroundSyncService.this);
        CacheServiceCallData cacheSeviceCallData = db.getCacheServiceCallByUrl(Consts.URL_MEDIA_LATEST);
        if (cacheSeviceCallData != null) {
            request.setStart_date(cacheSeviceCallData.getLastCalled());
            Log.d(Consts.LOG_TAG, "MainActivity: cacheSeviceCallData for URL_MEDIA_LATEST: " + cacheSeviceCallData.getLastCalled());
        }
        ServiceCaller sc = new ServiceCaller(BackgroundSyncService.this);
        sc.getAllMedia(request, new IAsyncWorkCompletedCallback() {
            @Override
            public void onDone(String workName, boolean isComplete) {
                Log.d(Consts.LOG_TAG, "MainActivity: callMediaAsync success result: " + isComplete);
                DbHelper db = new DbHelper(BackgroundSyncService.this);
                List<Data> data = db.getAllMedia();
                Log.d(Consts.LOG_TAG, "MainActivity: callMediaAsync data count: " + data.size());
                mediaCallDone = true;
                sendMessageIfAllCallsDone(extras);
            }
        });
    }
}
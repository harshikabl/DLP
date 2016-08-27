package dlp.bluelupin.dlp.Utilities;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.DownloadingFragment;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.OtpData;
import dlp.bluelupin.dlp.Services.IAsyncWorkCompletedCallback;
import dlp.bluelupin.dlp.Services.IServiceSuccessCallback;
import dlp.bluelupin.dlp.Services.ServiceCaller;
import dlp.bluelupin.dlp.Services.ServiceHelper;

/**
 * Created by Neeraj on 8/5/2016.
 */
public class BindService extends Service {
    int counter = 0;
    public URL[] urls;
    public int mediaId;


    private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        public BindService getService() {
            return BindService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        new DoBackgroundTask().execute(urls);
        //downoadFile(urls[0]);


        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        Toast.makeText(getBaseContext(), "Service Destroyed",
                Toast.LENGTH_SHORT).show();
    }

    private class DoBackgroundTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalBytesDownloaded = 0;
            for (int i = 0; i < count; i++) {
                //totalBytesDownloaded += DownloadFile(urls[i]);
                downoadFile(urls[i]);
                //---calculate percentage downloaded and
                // report its progress---
                publishProgress((int) (((i + 1) / (float) count) * 100));
            }
            return totalBytesDownloaded;
        }

        protected void onProgressUpdate(Integer... progress) {

            Log.d("Downloading files", String.valueOf(progress[0])
                    + "% downloaded");

          /*  Toast.makeText(getBaseContext(), String.valueOf(progress[0])
                    + "% downloaded", Toast.LENGTH_LONG).show();*/
        }

        protected void onPostExecute(Long result) {
           /* DbHelper dbHelper = new DbHelper(BindService.this);
            dbHelper.deleteFileDownloadedByMediaId(mediaId);*/
            // stopSelf();
        }
    }

    //download service call
    public void downoadFile(URL url) {
        if (Utility.isOnline(BindService.this)) {
            final ServiceHelper sh = new ServiceHelper(BindService.this);
            sh.callDownloadFileService(url.toString(),mediaId, new IServiceSuccessCallback<String>() {
                @Override
                public void onDone(final String callerUrl, final String result, String error) {
                }
            });

        } else {
            Utility.alertForErrorMessage(Consts.OFFLINE_MESSAGE, BindService.this);
        }
    }

}

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

/**
 * Created by Neeraj on 8/5/2016.
 */
public class BindService extends Service {
    int counter = 0;
    public URL[] urls;
    public int mediaId;

    static final int UPDATE_INTERVAL = 1000;

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

        return START_STICKY;
    }

    private int DownloadFile(URL url) {
        // TODO Auto-generated method stub

        String fileName = "";
        int count = 0;
        long total = 0;
        int progress = 0;
        File testDirectory = Utility.getFilePath(BindService.this);
        String localFilePath = null;
        if (Consts.IS_DEBUG_LOG) {
            if (url.toString().contains(".mp4")) {
                localFilePath = testDirectory + "/" + mediaId + ".mp4";
            }
            else {
                if (url.toString().contains(".jpg")) {
                    localFilePath = testDirectory + "/" + mediaId + ".jpg";
                } else if (url.toString().contains(".png")) {
                    localFilePath = testDirectory + "/" + mediaId + ".png";
                } else if (url.toString().contains(".jpeg")) {
                    localFilePath = testDirectory + "/" + mediaId + ".jpeg";
                } else {
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "downloading file worng formate");
                    }
                }
            }
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "localFilePath = " + localFilePath);
            }
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "downloading data");
            }

            URLConnection connection = url.openConnection();
            connection.connect();

            int lenghtOfFile = connection.getContentLength();
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "lenghtOfFile = " + lenghtOfFile);
            }

            is = url.openStream();
            testDirectory.mkdirs();
            if (!testDirectory.exists()) {
                testDirectory.mkdir();
            }

            fos = new FileOutputStream(localFilePath);

            byte data[] = new byte[1024];
            DbHelper dbHelper = new DbHelper(BindService.this);

            while ((count = is.read(data)) != -1) {
                total += count;
                int progress_temp = (int) total * 100 / lenghtOfFile;
                if (progress_temp % 10 == 0 && progress != progress_temp) {
                    progress = progress_temp;
                    Data downloadingFileData = new Data();
                    downloadingFileData.setMediaId(mediaId);
                    downloadingFileData.setProgress(progress);
                    dbHelper.upsertDownloadingFileEntity(downloadingFileData);

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(Consts.mBroadcastDeleteAction);
                    broadcastIntent.putExtra("progresss",progress);
                    LocalBroadcastManager.getInstance(BindService.this).sendBroadcast(broadcastIntent);
                    try
                    {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "total = " + progress);
                    }
                }
                fos.write(data, 0, count);
            }
            //update MediaEntity table for local file path
            Data fileData = new Data();
            fileData.setLocalFilePath(localFilePath);
            fileData.setId(mediaId);
            dbHelper.updateMediaLocalFilePathEntity(fileData);
            dbHelper.updateDownloadMediaLocalFilePathEntity(fileData);
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, url + ": " + fileName + " downloading finished");
            }

        } catch (Exception e) {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, url + ": " + fileName + " exception in downloadData");
            }
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }




        return progress;
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
                totalBytesDownloaded += DownloadFile(urls[i]);
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
            DbHelper dbHelper = new DbHelper(BindService.this);
            dbHelper.deleteFileDownloadedByMediaId(mediaId);
           // stopSelf();
        }
    }


}

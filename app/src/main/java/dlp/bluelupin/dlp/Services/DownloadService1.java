package dlp.bluelupin.dlp.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.DownloadData;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.Utility;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by subod on 05-Sep-16.
 */
public class DownloadService1 extends IntentService {

    public DownloadService1() {
        super("Download Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;
    private String urlPropertyForDownload = Consts.DOWNLOAD_URL;
    private Data media;


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String strJsonMedia = extras.getString(Consts.EXTRA_MEDIA);
            String urlPropertyForDownload = extras.getString(Consts.EXTRA_URLPropertyForDownload);
            Gson gson = new Gson();
            media = gson.fromJson(strJsonMedia, Data.class);
        }

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.hihlogo)
                .setContentTitle("Download")
                .setContentText("Downloading File" + media.getName())
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

        initDownload(media);

    }

    private void initDownload(Data media){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Consts.BASE_URL)
                .build();

        IServiceManager retrofitInterface = retrofit.create(IServiceManager.class);
        String downloadUrl = getDownloadFromMedia(media);
        if(downloadUrl!= null) {
            Call<ResponseBody> request = retrofitInterface.downloadFile(downloadUrl);
            try {

                downloadFile(request.execute().body(), media);

            } catch (IOException e) {

                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
        else
        {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "started downloading: media Id:" + media.getId() + " downloading Url: (" + urlPropertyForDownload   + ") "+ downloadUrl);
            }
        }
    }

    private String getDownloadFromMedia(Data media)
    {
        String downloadUrl = null;
        switch (urlPropertyForDownload)
        {
            case Consts.URL:
                downloadUrl = media.getUrl();
                break;
            case Consts.DOWNLOAD_URL:
                downloadUrl = media.getDownload_url();
                break;
            case Consts.THUMBNAIL_URL:
                downloadUrl = media.getThumbnail_url();
                break;
            default:
                downloadUrl = media.getDownload_url();
                break;
        }
        return downloadUrl;
    }

    private void downloadFile(ResponseBody body, Data media) throws IOException {


        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);

        String localFilePath = Consts.outputDirectoryLocation+ media.getFile_path();
        File outputFile = new File(localFilePath);

        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        DownloadData downloadData = new DownloadData();
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;


            downloadData.setId(media.getId());
            downloadData.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                downloadData.setCurrentFileSize((int) current);
                downloadData.setProgress(progress);

                sendNotification(downloadData);

                timeCount++;
            }

            output.write(data, 0, count);
        }
        onDownloadComplete(downloadData);
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "successfully downloaded: media Id:" + media.getId() + " downloading Url: " + media.getDownload_url() + " at " + localFilePath);
        }
        UpdateMediaInDB(localFilePath);
        output.flush();
        output.close();
        bis.close();

    }

    private void UpdateMediaInDB(String localPath)
    {
        DbHelper dbHelper = new DbHelper(DownloadService1.this);
        media.setLocalFilePath(localPath);
        if(dbHelper.updateMediaLocalFilePathEntity(media))
        {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "successfully downloaded and local file updated: media Id:" + media.getId() + " downloading Url: " + media.getDownload_url() + " at " + localPath);
            }
        }
    }

    private void sendNotification(DownloadData downloadData){

        sendIntent(downloadData);
//        notificationBuilder.setProgress(100,downloadData.getProgress(),false);
//        notificationBuilder.setContentText(String.format("Downloaded (%d/%d) MB",downloadData.getCurrentFileSize(),downloadData.getTotalFileSize()));
//        notificationManager.notify(0, notificationBuilder.build());
    }


    private void sendIntent(DownloadData downloadData){

        Intent intent = new Intent(Consts.MESSAGE_PROGRESS);
        intent.putExtra(Consts.EXTRA_DOWNLOAD_DATA,downloadData);
        LocalBroadcastManager.getInstance(DownloadService1.this).sendBroadcast(intent);
    }

    private void onDownloadComplete(DownloadData downlaodData){

        //DownloadData download = new DownloadData();
        downlaodData.setProgress(100);
        sendIntent(downlaodData);

//        notificationManager.cancel(0);
//        notificationBuilder.setProgress(0,0,false);
//        notificationBuilder.setContentText("File Downloaded: " + media.getId());
//        notificationManager.notify(0, notificationBuilder.build());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}

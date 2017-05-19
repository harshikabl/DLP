package dlp.bluelupin.dlp.Utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Services.IServiceManager;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by Neeraj on 5/18/2017.
 */

public abstract class DownloadFileAsync extends AsyncTask<Void, Integer, String>
        implements DownloadZipFileTaskCallback {
    private Context context;
    Data media;

    public DownloadFileAsync(Context context,Data media) {
        this.context = context;
        this.media = media;
    }

    public static volatile boolean shouldContinue = true;
    OkHttpClient okHttpClient;
    private int totalFileSize;


    @Override
    public String doInBackground(Void... voids) {
        String localFilePath = initDownload();
        return localFilePath;
    }

    @Override
    protected void onPostExecute(String localFilePath) {
        super.onPostExecute(localFilePath);
        receiveData(localFilePath);
    }

    public String initDownload() {
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Consts.BASE_URL)
                .client(okHttpClient)
                .build();


        IServiceManager retrofitInterface = retrofit.create(IServiceManager.class);
        String downloadUrl = media.getDownload_url();
        String localFilePath = "";
        if (downloadUrl != null) {
            Call<ResponseBody> request = retrofitInterface.downloadFile(downloadUrl);
            try {
                localFilePath = downloadFile(request.execute().body(), media);

            } catch (IOException e) {

                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
        return localFilePath;
    }

    private String downloadFile(ResponseBody body, Data media) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = 0;
        String localFilePath = "";
        if (body != null) {
            fileSize = body.contentLength();

            localFilePath = Consts.outputDirectoryLocation + media.getFile_path();
            InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
           /* if (urlPropertyForDownload.equalsIgnoreCase(Consts.THUMBNAIL_URL)) {
                if (media.getThumbnail_file_path() != null)
                    localFilePath = DownloadFileAsync.this.getFilesDir() + media.getThumbnail_file_path(); // Consts.outputDirectoryLocation + media.getThumbnail_file_path();
            }*/
            File outputFile = new File(localFilePath);
            OutputStream output = new FileOutputStream(outputFile);
            long total = 0;
            long startTime = System.currentTimeMillis();
            int timeCount = 1;
            //DownloadData downloadData = new DownloadData();
            while ((count = bis.read(data)) != -1) {

                total += count;
                totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
                double current = Math.round(total / (Math.pow(1024, 2)));

                int progress = (int) ((total * 100) / fileSize);

                long currentTime = System.currentTimeMillis() - startTime;


                // downloadData.setId(media.getId());
                //downloadData.setTotalFileSize(totalFileSize);

               /* if (currentTime > 1000 * timeCount) {

                    downloadData.setCurrentFileSize((int) current);
                    downloadData.setProgress(progress);
                    if (Consts.IS_DEBUG_LOG) {
                        //Log.d(Consts.LOG_TAG, "DownlaodService1: downlaodData Id:" + downloadData.getId() + " progress:" + downloadData.getProgress());
                    }
                    if (shouldContinue != false) {
                        sendNotification(downloadData);
                    }

                    timeCount++;
                }*/

                output.write(data, 0, count);
            }
            //onDownloadComplete(downloadData);
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "successfully downloaded: media Id:" + media.getId() + " downloading Url: " + media.getDownload_url() + " at " + localFilePath);
            }
            output.flush();
            output.close();
            bis.close();
        }
        return localFilePath;
    }

    @Override
    public abstract void receiveData(String result);
}

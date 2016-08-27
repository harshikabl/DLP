package dlp.bluelupin.dlp.Activities;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Utilities.Utility;

public class DownloadService extends IntentService {
    public static final int UPDATE_PROGRESS = 8344;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            downloadData(new URL("https://s3.ap-south-1.amazonaws.com/classkonnect-test/011+Resetting+data.mp4"));

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("FILE_DOWNLOADED_ACTION");
            getBaseContext().sendBroadcast(broadcastIntent);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private int DownloadFile(URL url) {
        int count;
        try {
            try {
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream("/sdcard/downloadedfile.mp4");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    //publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 100;
    }


    public void downloadData(URL url) {
        String fileName = "";

        File testDirectory = Utility.getFilePath(this);// new DownloadFile(Environment.getExternalStorageDirectory() + Contants.APP_DIRECTORY);
        String localFilePath = testDirectory + "/" + "test";

        InputStream is = null;
        FileOutputStream fos = null;
        try {
            if (Consts.IS_DEBUG_LOG) {
                Log.v(Consts.LOG_TAG, "downloading data");
            }

            URLConnection connection = url.openConnection();
            connection.connect();

            int lenghtOfFile = connection.getContentLength();
            if (Consts.IS_DEBUG_LOG) {
                Log.v(Consts.LOG_TAG, "lenghtOfFile = " + lenghtOfFile);
            }

            is = url.openStream();
            testDirectory.mkdirs();
            if (!testDirectory.exists()) {
                testDirectory.mkdir();
            }

            fos = new FileOutputStream(localFilePath);

            byte data[] = new byte[1024];

            int count = 0;
            long total = 0;
            int progress = 0;

            while ((count = is.read(data)) != -1) {
                total += count;
                int progress_temp = (int) total * 100 / lenghtOfFile;
                if (progress_temp % 10 == 0 && progress != progress_temp) {
                    progress = progress_temp;
                    if (Consts.IS_DEBUG_LOG) {
                        Log.v(Consts.LOG_TAG, "total = " + progress);
                    }
                }
                fos.write(data, 0, count);
            }

            if (Consts.IS_DEBUG_LOG) {
                Log.v(Consts.LOG_TAG, url + ": " + fileName + " downloading finished");
            }

        } catch (Exception e) {
            if (Consts.IS_DEBUG_LOG) {
                Log.v(Consts.LOG_TAG, url + ": " + fileName + " exception in downloadData");
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
        //return localFilePath;
    }

}

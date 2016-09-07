package dlp.bluelupin.dlp.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.InputStream;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;

/**
 * Created by subod on 28-Jul-16.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Data media;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    public DownloadImageTask(ImageView bmImage, Data media) {
        this.bmImage = bmImage;
        this.media = media;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap bitmap = null;
        if (media != null) {
            if (media.getLocalFilePath() != null) {
                File imgFile = new File(media.getLocalFilePath());
                if (imgFile.exists()) {
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "DownloadImageTask: localfilePath: " + media.getLocalFilePath());
                    }
                    return bitmap;
                }
            }
        }

        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            bitmap = BitmapFactory.decodeStream(in);
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "DownloadImageTask: downloading: " + urlDisplay);
            }
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            //e.printStackTrace();
        }

        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {


        bmImage.setImageBitmap(result);

    }
}
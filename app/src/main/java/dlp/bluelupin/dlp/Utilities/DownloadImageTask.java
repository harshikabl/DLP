package dlp.bluelupin.dlp.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import dlp.bluelupin.dlp.Consts;

/**
 * Created by subod on 28-Jul-16.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            e.printStackTrace();
        }
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "downloading " + urldisplay);
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {


        bmImage.setImageBitmap(result);

    }
}
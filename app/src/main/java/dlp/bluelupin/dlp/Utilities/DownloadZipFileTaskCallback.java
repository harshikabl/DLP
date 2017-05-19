package dlp.bluelupin.dlp.Utilities;

import java.util.List;


/**
 * Created by Neeraj on 5/18/2017.
 */
public interface DownloadZipFileTaskCallback {
    public void receiveData(String result);

    String doInBackground(Void... voids);
}

package dlp.bluelupin.dlp.Services;

/**
 * Created by subod on 25-Jul-16.
 */
public interface IAsyncWorkCompletedCallback {
    public void onDone(String workName, boolean isComplete);
}

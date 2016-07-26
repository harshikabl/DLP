package dlp.bluelupin.dlp.Services;

/**
 * Created by subod on 22-Jul-16.
 */
public interface IServiceSuccessCallback<T> {
    public void onDone(String callerUrl, T result, String error);
}

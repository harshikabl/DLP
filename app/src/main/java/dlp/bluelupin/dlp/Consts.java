package dlp.bluelupin.dlp;
/**
 * Created by subod on 21-Jul-16.
 */
public class Consts {
    public static final String LOG_TAG = "dlp_log";
    public final static boolean PROD =  false; //false;
    public final static String API_KEY = "XsMwq2updd3L5MZAtgwx7PAA0wKaylFnCejD0ei9WjSuwQVmXMQxGg3ZiH5X";

    public static  String BASE_URL= "http://180.151.10.60:8080/classkonnect/api/";
    public static String getBaseUrl() {
        if(PROD) {
            BASE_URL = "http://180.151.10.60:8080/classkonnect/api/";
            return BASE_URL;
        }
        return BASE_URL;
    }

    public static final String URL_CONTENT_LATEST= "v1/content/latest";

    public static final String URL_LANGUAGE_RESOURCE_LATEST= "v1/langresource/latest";

    public static final String URL_MEDIA_LATEST= "v1/media/latest";
}

package dlp.bluelupin.dlp;
/**
 * Created by subod on 21-Jul-16.
 */
public class Consts {
    public static final String LOG_TAG = "HIH";
    public static final Boolean IS_DEBUG_LOG = true;
    public final static boolean PROD = false; //false;
    public final static String API_KEY = "XsMwq2updd3L5MZAtgwx7PAA0wKaylFnCejD0ei9WjSuwQVmXMQxGg3ZiH5X";

    public static String BASE_URL = "http://dlp-qa.bluelup.in/api/v1/";//http://dlpdev.bluelup.in/api/v1/"; // http://dlp-qa.bluelup.in //"http://180.151.10.60:8080/classkonnect/api/";

    public static String getBaseUrl() {
        if (PROD) {
            BASE_URL = "http://dlp-qa.bluelup.in/api/v1/";
            return BASE_URL;
        }
        return BASE_URL;
    }

    public static final String URL_CONTENT_LATEST = "content/latest";

    public static final String URL_LANGUAGE_RESOURCE_LATEST = "langresource/latest";

    public static final String URL_MEDIA_LATEST = "media/latest";
    public static final String CREATE_NEW_USER = "user/create";
    public static final String VERIFY_OTP = "user/verifyotp";

    public static final String DownloadBroadcast="DownloadBroadcast";

    public static final String COURSE =  "Course";

    public static final String SUBJECT =  "Subject";
    public static final String CHAPTER =  "Chapter";
    public static final String TOPIC =  "Topic";

    public static final String SENDER = "HP-MOBKNT";


 // 'Course','Subject','Chapter','Topic','Text','Image','Video','Url','Audio','Comment','Home','Other','File','Folder'
    public static  final String OFFLINE_MESSAGE = "You are not online!!!!";
}

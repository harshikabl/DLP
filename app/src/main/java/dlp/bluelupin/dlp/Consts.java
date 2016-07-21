package dlp.bluelupin.dlp;
/**
 * Created by subod on 21-Jul-16.
 */
public class Consts {
    public static final String LOG_TAG = "dlp";
    public final static boolean PROD =  false; //false;

    public static  String BASE_URL= "http://180.151.10.60:8080/classkonnect/api/";
    public static String getBaseUrl() {
        if(PROD) {
            BASE_URL = "http://180.151.10.60:8080/classkonnect/api/";
            return BASE_URL;
        }
        return BASE_URL;
    }


}

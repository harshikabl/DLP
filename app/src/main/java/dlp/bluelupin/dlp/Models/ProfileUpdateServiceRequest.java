package dlp.bluelupin.dlp.Models;

/**
 * Created by Neeraj on 9/14/2016.
 */
public class ProfileUpdateServiceRequest {
    private String name;
    private int language_id;
    private String api_token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(int language_id) {
        this.language_id = language_id;
    }

    public String getApi_token() {
        return api_token;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }
}

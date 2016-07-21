package dlp.bluelupin.dlp.Models;

/**
 * Created by subod on 21-Jul-16.
 */
public class CacheServiceCallData {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLastCalled() {
        return lastCalled;
    }

    public void setLastCalled(String lastCalled) {
        this.lastCalled = lastCalled;
    }

    private int id;

    private String url;

    private String lastCalled;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getDataIdentifier() {
        return dataIdentifier;
    }

    public void setDataIdentifier(String dataIdentifier) {
        this.dataIdentifier = dataIdentifier;
    }

    private String payload;

    private String dataIdentifier;
}


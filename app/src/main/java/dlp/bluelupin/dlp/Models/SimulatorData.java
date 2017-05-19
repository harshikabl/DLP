package dlp.bluelupin.dlp.Models;

/**
 * Created by Neeraj on 5/19/2017.
 */

public class SimulatorData {
    private int id;
    private int parentId;
    private String url;
    private String downloadUrl;
    private String localPathUrl;
    private int languageId;
    private int isDeleted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getLocalPathUrl() {
        return localPathUrl;
    }

    public void setLocalPathUrl(String localPathUrl) {
        this.localPathUrl = localPathUrl;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}

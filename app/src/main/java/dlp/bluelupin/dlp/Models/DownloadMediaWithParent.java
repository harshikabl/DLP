package dlp.bluelupin.dlp.Models;

import java.util.List;

/**
 * Created by Neeraj on 9/27/2016.
 */
public class DownloadMediaWithParent {
    private List<Data> strJsonResourcesToDownloadList;
    private int parentId;

    public List<Data> getStrJsonResourcesToDownloadList() {
        return strJsonResourcesToDownloadList;
    }

    public void setStrJsonResourcesToDownloadList(List<Data> strJsonResourcesToDownloadList) {
        this.strJsonResourcesToDownloadList = strJsonResourcesToDownloadList;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
}

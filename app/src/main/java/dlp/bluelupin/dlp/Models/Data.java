package dlp.bluelupin.dlp.Models;

/**
 * Created by subod on 20-Jul-16.
 */

public class Data {
    private int id; // this represents server id

    private String updated_at;

    private int sequence;

    private String deleted_at;

    private int media_id;

    private String created_at;

    private int thumbnail_media_id;

    private String lang_resource_description;

    private String lang_resource_name;

    private String type;

    private String url;

    private int parent_id;

    private int clientId;

    private String file_path;

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }


    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    private int client_id;


    public int getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(int language_id) {
        this.language_id = language_id;
    }

    private int language_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String name;
    private String content;


    public int getClientId() {
        return clientId;
    }

    public void setClientId(int client_id) {
        this.clientId = client_id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public int getMedia_id() {
        return media_id;
    }

    public void setMedia_id(int media_id) {
        this.media_id = media_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getThumbnail_media_id() {
        return thumbnail_media_id;
    }

    public void setThumbnail_media_id(int thumbnail_media_id) {
        this.thumbnail_media_id = thumbnail_media_id;
    }

    public String getLang_resource_description() {
        return lang_resource_description;
    }

    public void setLang_resource_description(String lang_resource_description) {
        this.lang_resource_description = lang_resource_description;
    }

    public String getLang_resource_name() {
        return lang_resource_name;
    }

    public void setLang_resource_name(String lang_resource_name) {
        this.lang_resource_name = lang_resource_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    private String localFilePath;

    private int mediaId;
    private int progress;

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


    private String download_url;
    private int created_by;
    private int updated_by;
    private int cloud_transferred;

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public int getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(int updated_by) {
        this.updated_by = updated_by;
    }

    public int getCloud_transferred() {
        return cloud_transferred;
    }

    public void setCloud_transferred(int cloud_transferred) {
        this.cloud_transferred = cloud_transferred;
    }


    private String send_at;
    private String message;
    private String status;
    private String custom_data;

    public String getSend_at() {
        return send_at;
    }

    public void setSend_at(String send_at) {
        this.send_at = send_at;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustom_data() {
        return custom_data;
    }

    public void setCustom_data(String custom_data) {
        this.custom_data = custom_data;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", updated_at = " + updated_at + ", sequence = " + sequence + ", deleted_at = " + deleted_at + ", media_id = " + media_id + ", created_at = " + created_at + ", thumbnail_media_id = " + thumbnail_media_id + ", lang_resource_description = " + lang_resource_description + ", lang_resource_name = " + lang_resource_name + ", type = " + type + ", url = " + url + ", parent_id = " + parent_id + ", localFilePath = " + localFilePath + ", download_url = " + download_url + ", created_by = " + created_by + ", updated_by = " + updated_by + ", cloud_transferred = " + cloud_transferred + ", send_at = " + send_at + ", message = " + message + ", status = " + status + ", custom_data = " + custom_data + "]";
    }
}


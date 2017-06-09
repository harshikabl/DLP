package dlp.bluelupin.dlp.Models;

/**
 * Created by subod on 20-Jul-16.
 */

public class Data {
    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    private String option;
    private String answer;
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

    public String getThumbnail_url_Local_file_path() {
        return thumbnail_url_Local_file_path;
    }

    public void setThumbnail_url_Local_file_path(String thumbnail_url_Local_file_path) {
        this.thumbnail_url_Local_file_path = thumbnail_url_Local_file_path;
    }

    private String thumbnail_file_path;

    public String getThumbnail_file_path() {
        return thumbnail_file_path;
    }

    public void setThumbnail_file_path(String thumbnail_file_path) {
        this.thumbnail_file_path = thumbnail_file_path;
    }

    private String thumbnail_url_Local_file_path;

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

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    private String thumbnail_url;

    public String getThumbnail_localFilename() {
        return thumbnail_localFilename;
    }

    public void setThumbnail_localFilename(String thumbnail_localFilename) {
        this.thumbnail_localFilename = thumbnail_localFilename;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    private String thumbnail_localFilename;
    private String localFileName;
    private int isDownloaded = 0;

    public int getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(int isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    private int audio_media_id;
    private int answer_audio_media_id;
    private String lang_resource_correct_answer;
    private String lang_resource_correct_answer_description;


    public int getAudio_media_id() {
        return audio_media_id;
    }

    public void setAudio_media_id(int audio_media_id) {
        this.audio_media_id = audio_media_id;
    }

    public int getAnswer_audio_media_id() {
        return answer_audio_media_id;
    }

    public void setAnswer_audio_media_id(int answer_audio_media_id) {
        this.answer_audio_media_id = answer_audio_media_id;
    }

    public String getLang_resource_correct_answer() {
        return lang_resource_correct_answer;
    }

    public void setLang_resource_correct_answer(String lang_resource_correct_answer) {
        this.lang_resource_correct_answer = lang_resource_correct_answer;
    }

    public String getLang_resource_correct_answer_description() {
        return lang_resource_correct_answer_description;
    }

    public void setLang_resource_correct_answer_description(String lang_resource_correct_answer_description) {
        this.lang_resource_correct_answer_description = lang_resource_correct_answer_description;
    }

    private int question_id;
    private int is_correct;

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getIs_correct() {
        return is_correct;
    }

    public void setIs_correct(int is_correct) {
        this.is_correct = is_correct;
    }

    private int quiz_id;

    public int getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(int quiz_id) {
        this.quiz_id = quiz_id;
    }

    private int content_id;

    public int getContent_id() {
        return content_id;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    private Boolean isQuizAvailable = false;

    public Boolean getQuizAvailable() {
        return isQuizAvailable;
    }

    public void setQuizAvailable(Boolean quizAvailable) {
        isQuizAvailable = quizAvailable;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", updated_at = " + updated_at + ", sequence = " + sequence + ", deleted_at = " + deleted_at + ", media_id = " + media_id + ", created_at = " + created_at + ", thumbnail_media_id = " + thumbnail_media_id + ", lang_resource_description = " + lang_resource_description + ", lang_resource_name = " + lang_resource_name + ", type = " + type + ", url = " + url + ", parent_id = " + parent_id + ", localFilePath = " + localFilePath + ", download_url = " + download_url + ", created_by = " + created_by + ", updated_by = " + updated_by + ", cloud_transferred = " + cloud_transferred + ", send_at = " + send_at + ", message = " + message + ", status = " + status + ", custom_data = " + custom_data + " thumbnail_url =" + thumbnail_url + ", thumbnail_localFilename = " + thumbnail_localFilename + ", localFileName = " + localFileName + ", isDownloaded=" + isDownloaded + " ]";
    }
}


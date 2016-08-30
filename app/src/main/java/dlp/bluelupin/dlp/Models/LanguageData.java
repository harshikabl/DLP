package dlp.bluelupin.dlp.Models;

/**
 * Created by Neeraj on 8/30/2016.
 */
public class LanguageData {
    private int id;

    private String name;

    private String deleted_at;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", name = " + name + ", deleted_at = " + deleted_at + ", code = " + code + "]";
    }
}


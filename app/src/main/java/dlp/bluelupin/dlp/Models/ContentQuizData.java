package dlp.bluelupin.dlp.Models;

/**
 * Created by Neeraj on 4/26/2017.
 */

public class ContentQuizData {
        private int created_by;
        private int id;
        private Pivot pivot;
        private String updated_at;
        private String description;
        private String name;
        private String deleted_at;
        private String created_at;
        private int updated_by;
        private int client_id;

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pivot getPivot() {
        return pivot;
    }

    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(int updated_by) {
        this.updated_by = updated_by;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [created_by = "+created_by+", id = "+id+", pivot = "+pivot+", updated_at = "+updated_at+", description = "+description+", name = "+name+", deleted_at = "+deleted_at+", created_at = "+created_at+", updated_by = "+updated_by+", client_id = "+client_id+"]";
    }
}



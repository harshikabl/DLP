package dlp.bluelupin.dlp.Models;

/**
 * Created by subod on 20-Jul-16.
 */

public class Data
{
    private String id;

    private String updated_at;

    private String sequence;

    private String deleted_at;

    private String media_id;

    private String created_at;

    private String thumbnail_media_id;

    private String lang_resource_description;

    private String lang_resource_name;

    private String type;

    private String url;

    private String parent_id;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getUpdated_at ()
    {
        return updated_at;
    }

    public void setUpdated_at (String updated_at)
    {
        this.updated_at = updated_at;
    }

    public String getSequence ()
    {
        return sequence;
    }

    public void setSequence (String sequence)
    {
        this.sequence = sequence;
    }

    public String getDeleted_at ()
{
    return deleted_at;
}

    public void setDeleted_at (String deleted_at)
    {
        this.deleted_at = deleted_at;
    }

    public String getMedia_id ()
{
    return media_id;
}

    public void setMedia_id (String media_id)
    {
        this.media_id = media_id;
    }

    public String getCreated_at ()
    {
        return created_at;
    }

    public void setCreated_at (String created_at)
    {
        this.created_at = created_at;
    }

    public String getThumbnail_media_id ()
{
    return thumbnail_media_id;
}

    public void setThumbnail_media_id (String thumbnail_media_id)
    {
        this.thumbnail_media_id = thumbnail_media_id;
    }

    public String getLang_resource_description ()
{
    return lang_resource_description;
}

    public void setLang_resource_description (String lang_resource_description)
    {
        this.lang_resource_description = lang_resource_description;
    }

    public String getLang_resource_name ()
    {
        return lang_resource_name;
    }

    public void setLang_resource_name (String lang_resource_name)
    {
        this.lang_resource_name = lang_resource_name;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getUrl ()
{
    return url;
}

    public void setUrl (String url)
    {
        this.url = url;
    }

    public String getParent_id ()
{
    return parent_id;
}

    public void setParent_id (String parent_id)
    {
        this.parent_id = parent_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", updated_at = "+updated_at+", sequence = "+sequence+", deleted_at = "+deleted_at+", media_id = "+media_id+", created_at = "+created_at+", thumbnail_media_id = "+thumbnail_media_id+", lang_resource_description = "+lang_resource_description+", lang_resource_name = "+lang_resource_name+", type = "+type+", url = "+url+", parent_id = "+parent_id+"]";
    }
}


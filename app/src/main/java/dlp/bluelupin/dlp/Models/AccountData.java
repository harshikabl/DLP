package dlp.bluelupin.dlp.Models;

/**
 * Created by Neeraj on 7/28/2016.
 */
public class AccountData {
        private int id;

        private String created_by;

        private String phone;

        private String updated_at;

        private String email;

        private String api_token;

        private String name;

        private String created_at;

        private int preferred_language_id;

        private String role;

        private String client_id;

        private int otp;



    public String getCreated_by ()
    {
        return created_by;
    }

    public void setCreated_by (String created_by)
    {
        this.created_by = created_by;
    }

    public String getPhone ()
    {
        return phone;
    }

    public void setPhone (String phone)
    {
        this.phone = phone;
    }

    public String getUpdated_at ()
    {
        return updated_at;
    }

    public void setUpdated_at (String updated_at)
    {
        this.updated_at = updated_at;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getApi_token ()
    {
        return api_token;
    }

    public void setApi_token (String api_token)
    {
        this.api_token = api_token;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getCreated_at ()
    {
        return created_at;
    }

    public void setCreated_at (String created_at)
    {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPreferred_language_id() {
        return preferred_language_id;
    }

    public void setPreferred_language_id(int preferred_language_id) {
        this.preferred_language_id = preferred_language_id;
    }

    public String getRole ()
    {
        return role;
    }

    public void setRole (String role)
    {
        this.role = role;
    }

    public String getClient_id ()
    {
        return client_id;
    }

    public void setClient_id (String client_id)
    {
        this.client_id = client_id;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    private int isVerified;

    public int getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(int isVerified) {
        this.isVerified = isVerified;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", created_by = "+created_by+", phone = "+phone+", updated_at = "+updated_at+", email = "+email+", api_token = "+api_token+", name = "+name+", created_at = "+created_at+", preferred_language_id = "+preferred_language_id+", role = "+role+", client_id = "+client_id+", otp = "+otp+"]";
    }
}


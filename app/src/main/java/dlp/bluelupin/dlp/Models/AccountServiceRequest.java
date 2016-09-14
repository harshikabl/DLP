package dlp.bluelupin.dlp.Models;

/**
 * Created by Neeraj on 7/28/2016.
 */
public class AccountServiceRequest {
    private String phone;

    private String email;

    private String name;

    private String api_token;

    private int preferred_language_id;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApi_token() {
        return api_token;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }

    public int getPreferred_language_id() {
        return preferred_language_id;
    }

    public void setPreferred_language_id(int preferred_language_id) {
        this.preferred_language_id = preferred_language_id;
    }

    private String device_token;
    private String service;
    private Boolean is_development;

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Boolean getIs_development() {
        return is_development;
    }

    public void setIs_development(Boolean is_development) {
        this.is_development = is_development;
    }

    @Override
    public String toString() {
        return "ClassPojo [phone = " + phone + ", email = " + email + ", name = " + name + ", api_token = " + api_token + ", preferred_language_id = " + preferred_language_id + ", device_token = " + device_token + ", service = " + service + ", is_development = " + is_development + "]";
    }
}


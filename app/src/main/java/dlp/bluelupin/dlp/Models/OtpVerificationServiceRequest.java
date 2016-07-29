package dlp.bluelupin.dlp.Models;

/**
 * Created by Neeraj on 7/28/2016.
 */
public class OtpVerificationServiceRequest {
    private String otp;
    private String api_token;

    public String getApi_token() {
        return api_token;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @Override
    public String toString() {
        return "ClassPojo [otp = " + otp + ", api_token = " + api_token + "]";
    }
}

package dlp.bluelupin.dlp.Services;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.AccountServiceRequest;
import dlp.bluelupin.dlp.Models.ContentData;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;
import dlp.bluelupin.dlp.Models.OtpData;
import dlp.bluelupin.dlp.Models.OtpVerificationServiceRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by subod on 19-Jul-16.
 */
public interface IServiceManager {
    @POST(Consts.URL_CONTENT_LATEST)
    Call<ContentData> latestContent(@Body ContentServiceRequest request);

    @POST(Consts.URL_LANGUAGE_RESOURCE_LATEST)
    Call<ContentData> latestResource(@Body ContentServiceRequest request);

    @POST(Consts.URL_MEDIA_LATEST)
    Call<ContentData> latestMedia(@Body ContentServiceRequest request);

    @POST(Consts.CREATE_NEW_USER)
    Call<AccountData> accountCreate(@Body AccountServiceRequest request);

    @POST(Consts.VERIFY_OTP)
    Call<OtpData> otpVerify(@Body OtpVerificationServiceRequest request);

   // @Streaming
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    @POST(Consts.MediaLanguage_Latest)
    Call<ContentData> MedialanguageLatestContent(@Body ContentServiceRequest request);

}

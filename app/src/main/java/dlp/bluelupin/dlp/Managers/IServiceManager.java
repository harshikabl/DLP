package dlp.bluelupin.dlp.Managers;

import dlp.bluelupin.dlp.Models.ContentData;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by subod on 19-Jul-16.
 */
public interface IServiceManager {
    @POST("v1/content/latest")
    Call<ContentData> latestContent(@Body ContentServiceRequest request);
}

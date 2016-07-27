package dlp.bluelupin.dlp.Services;

import dlp.bluelupin.dlp.Consts;
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
    @POST(Consts.URL_CONTENT_LATEST)
    Call<ContentData> latestContent(@Body ContentServiceRequest request);

    @POST(Consts.URL_LANGUAGE_RESOURCE_LATEST)
    Call<ContentData> latestResource(@Body ContentServiceRequest request);

    @POST(Consts.URL_MEDIA_LATEST)
    Call<ContentData> latestMedia(@Body ContentServiceRequest request);
}

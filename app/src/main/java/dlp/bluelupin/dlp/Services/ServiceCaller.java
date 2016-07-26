package dlp.bluelupin.dlp.Services;

import android.content.Context;
import android.util.Log;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.ContentData;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;

/**
 * Created by subod on 25-Jul-16.
 */
public class ServiceCaller {
    Context context;

    public ServiceCaller(Context context) {
        this.context = context;
    }

    public void getAllContent(final ContentServiceRequest request, final IAsyncWorkCompletedCallback workCompletedCallback) {
        final ServiceHelper sh = new ServiceHelper(context);
        sh.callContentService(request, new IServiceSuccessCallback<ContentData>() {
            @Override
            public void onDone(final String callerUrl, final ContentData result, String error) {
                Boolean success = false;
                if (result != null) {
                    if (request.getPage() <= result.getLast_page()) {
                        Log.d(Consts.LOG_TAG, "Recursively calling next content page: " + result.getCurrent_page());
                        final ContentServiceRequest nextRequest = new ContentServiceRequest();
                        nextRequest.setPage(result.getCurrent_page() + 1);
                        getAllContent(nextRequest, new IAsyncWorkCompletedCallback() {
                            @Override
                            public void onDone(String workName, boolean isComplete) {
                                // Log.d(Consts.LOG_TAG, "Success: Service caller getAllContent done at page: " + nextRequest.getPage());
                                if (nextRequest.getPage() > result.getLast_page()) {
                                    Log.d(Consts.LOG_TAG, "Content Parsed successfully till page: " + result.getCurrent_page());
                                    workCompletedCallback.onDone("getAllContent", true);
                                }
//                                } else {
//                                    // all parsed successfully; recursion complete
//
//
//                                    workCompletedCallback.onDone("getAllContent", true);
//                                }
                            }
                        });

                    } else {
                        // all parsed successfully; recursion complete
                       // Log.d(Consts.LOG_TAG, "Content Parsed successfully till page: " + result.getCurrent_page());
                        success = true;
                        workCompletedCallback.onDone("getAllContent", success);
                    }

                } else {
                    success = false;
                }
            }
        });
    }
}

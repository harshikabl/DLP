package dlp.bluelupin.dlp.Services;

import android.content.Context;
import android.util.Log;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.AccountServiceRequest;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.ContentData;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;
import dlp.bluelupin.dlp.Models.OtpData;
import dlp.bluelupin.dlp.Models.OtpVerificationServiceRequest;

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

                                } else {
                                    // all parsed successfully; recursion complete
                                    workCompletedCallback.onDone("getAllContent", true);
                                }
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

    public void getAllResource(final ContentServiceRequest request, final IAsyncWorkCompletedCallback workCompletedCallback) {
        final ServiceHelper sh = new ServiceHelper(context);
        sh.callResourceService(request, new IServiceSuccessCallback<ContentData>() {
            @Override
            public void onDone(final String callerUrl, final ContentData result, String error) {
                Boolean success = false;
                if (result != null) {
                    if (request.getPage() <= result.getLast_page()) {
                        Log.d(Consts.LOG_TAG, "Recursively calling next resource page: " + result.getCurrent_page());
                        final ContentServiceRequest nextRequest = new ContentServiceRequest();
                        nextRequest.setPage(result.getCurrent_page() + 1);
                        getAllResource(nextRequest, new IAsyncWorkCompletedCallback() {
                            @Override
                            public void onDone(String workName, boolean isComplete) {

                                if (nextRequest.getPage() > result.getLast_page()) {
                                    Log.d(Consts.LOG_TAG, "resource Parsed successfully till page: " + result.getCurrent_page());
                                    workCompletedCallback.onDone("getAllResource", true);

                                } else {
                                    // all parsed successfully; recursion complete
                                    workCompletedCallback.onDone("getAllResource", true);
                                }
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

    public void getAllMedia(final ContentServiceRequest request, final IAsyncWorkCompletedCallback workCompletedCallback) {
        final ServiceHelper sh = new ServiceHelper(context);
        sh.callMediaService(request, new IServiceSuccessCallback<ContentData>() {
            @Override
            public void onDone(final String callerUrl, final ContentData result, String error) {
                Boolean success = false;
                if (result != null) {
                    if (request.getPage() <= result.getLast_page()) {
                        Log.d(Consts.LOG_TAG, "Recursively calling next Media page: " + result.getCurrent_page());
                        final ContentServiceRequest nextRequest = new ContentServiceRequest();
                        nextRequest.setPage(result.getCurrent_page() + 1);
                        getAllMedia(nextRequest, new IAsyncWorkCompletedCallback() {
                            @Override
                            public void onDone(String workName, boolean isComplete) {

                                if (nextRequest.getPage() > result.getLast_page()) {
                                    Log.d(Consts.LOG_TAG, "Media Parsed successfully till page: " + result.getCurrent_page());
                                    workCompletedCallback.onDone("getAllMedia", true);

                                } else {
                                    // all parsed successfully; recursion complete
                                    workCompletedCallback.onDone("getAllMedia", true);
                                }
                            }
                        });

                    } else {
                        // all parsed successfully; recursion complete
                        // Log.d(Consts.LOG_TAG, "Content Parsed successfully till page: " + result.getCurrent_page());
                        success = true;
                        workCompletedCallback.onDone("getAllMedia", success);
                    }

                } else {
                    success = false;
                }
            }
        });
    }

    //call create account service
    public void CreateAccount(final AccountServiceRequest request, final IAsyncWorkCompletedCallback workCompletedCallback) {
        final ServiceHelper sh = new ServiceHelper(context);
        sh.callCreateAccountService(request, new IServiceSuccessCallback<AccountData>() {
            @Override
            public void onDone(final String callerUrl, final AccountData result, String error) {
                Boolean success = false;
                if (result != null) {
                    success = true;
                    workCompletedCallback.onDone("AccountCreated", success);

                } else {
                    success = false;
                    workCompletedCallback.onDone("Account not created", success);
                }
            }
        });
    }

    //call OTP verification service
    public void OtpVerification(final OtpVerificationServiceRequest request, final IAsyncWorkCompletedCallback workCompletedCallback) {
        final ServiceHelper sh = new ServiceHelper(context);
        sh.callOtpVerificationService(request, new IServiceSuccessCallback<OtpData>() {
            @Override
            public void onDone(final String callerUrl, final OtpData result, String error) {
                Boolean success = false;
                if (result != null) {
                    success = true;
                    workCompletedCallback.onDone(result.getMessage(), success);

                } else {
                    success = false;
                    workCompletedCallback.onDone("OTP not virify", success);
                }
            }
        });
    }
}

package dlp.bluelupin.dlp.Services;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.AccountServiceRequest;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.ContentData;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.LanguageData;
import dlp.bluelupin.dlp.Models.OtpData;
import dlp.bluelupin.dlp.Models.OtpVerificationServiceRequest;
import dlp.bluelupin.dlp.Models.ProfileUpdateServiceRequest;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.IServiceManager;
import dlp.bluelupin.dlp.Services.IServiceSuccessCallback;
import dlp.bluelupin.dlp.Utilities.Utility;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by subod on 22-Jul-16.
 */
public class ServiceHelper {

    IServiceManager service;
    Context context;

    public ServiceHelper(Context context) {
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Consts.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(IServiceManager.class);
    }

    public void callContentService(ContentServiceRequest request, final IServiceSuccessCallback<ContentData> callback) {
        request.setApi_token(Consts.API_KEY);

        Call<ContentData> cd = service.latestContent(request);
        final DbHelper dbhelper = new DbHelper(context);
        cd.enqueue(new Callback<ContentData>() {
            @Override
            public void onResponse(Call<ContentData> call, final Response<ContentData> response) {
                final ContentData cd = response.body();
                //Log.d(Consts.LOG_TAG, cd.toString());
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        for (Data d : response.body().getData()) {
                            if (dbhelper.upsertDataEntity(d)) {
                                // publishProgress(cd.getCurrent_page() * cd.getPer_page() / cd.getTotal());
                                // Log.d(Consts.LOG_TAG,"successfully adding Data for page: "+ cd.getCurrent_page());
                            } else {
                                Log.d(Consts.LOG_TAG, "failure adding Data for page: " + cd.getCurrent_page());
                            }
                        }
                        String lastcalled = response.headers().get("last_request_date");
                        Log.d(Consts.LOG_TAG, "response last_request_date: " + lastcalled);
                        if (lastcalled != null) {
                            DbHelper dbhelper = new DbHelper(context);
                            CacheServiceCallData ob = new CacheServiceCallData();
                            ob.setUrl(Consts.URL_CONTENT_LATEST);
                            ob.setLastCalled(lastcalled);

                            dbhelper.upsertCacheServiceCall(ob);
                        }
                        callback.onDone(Consts.URL_CONTENT_LATEST, cd, null);
                        return null;
                    }
                }.execute();

            }

            @Override
            public void onFailure(Call<ContentData> call, Throwable t) {
                Log.d(Consts.LOG_TAG, "Failure in service latestContent" + t.toString());
                callback.onDone(Consts.URL_CONTENT_LATEST, null, t.toString());
            }

        });
    }

    public void callResourceService(ContentServiceRequest request, final IServiceSuccessCallback<ContentData> callback) {
        request.setApi_token(Consts.API_KEY);

        final Call<ContentData> cd = service.latestResource(request);
        final DbHelper dbhelper = new DbHelper(context);
        cd.enqueue(new Callback<ContentData>() {
            @Override
            public void onResponse(Call<ContentData> call, final Response<ContentData> response) {
                final ContentData cd = response.body();
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {

                        //Log.d(Consts.LOG_TAG, cd.toString());

                        for (Data d : cd.getData()) {
                            if (dbhelper.upsertResourceEntity(d)) {
                                // publishProgress(cd.getCurrent_page() * cd.getPer_page() / cd.getTotal());
                                //Log.d(Consts.LOG_TAG,"successfully adding Data for page: "+ cd.getCurrent_page());
                            } else {
                                Log.d(Consts.LOG_TAG, "failure adding resource Data for page: " + cd.getCurrent_page());
                            }
                        }
                        String lastCalled = response.headers().get("last_request_date");
                        Log.d(Consts.LOG_TAG, "response last_request_date: " + lastCalled);
                        if (lastCalled != null) {
                            DbHelper dbhelper = new DbHelper(context);
                            CacheServiceCallData ob = new CacheServiceCallData();
                            ob.setUrl(Consts.URL_LANGUAGE_RESOURCE_LATEST);
                            ob.setLastCalled(lastCalled);

                            dbhelper.upsertCacheServiceCall(ob);
                        }
                        callback.onDone(Consts.URL_LANGUAGE_RESOURCE_LATEST, cd, null);
                        return null;
                    }
                }.execute();

            }

            @Override
            public void onFailure(Call<ContentData> call, Throwable t) {
                Log.d(Consts.LOG_TAG, "Failure in service latestResource" + t.toString());
                callback.onDone(Consts.URL_LANGUAGE_RESOURCE_LATEST, null, t.toString());
            }

        });
    }

    public void callMediaService(ContentServiceRequest request, final IServiceSuccessCallback<ContentData> callback) {
        request.setApi_token(Consts.API_KEY);

        final Call<ContentData> cd = service.latestMedia(request);
        final DbHelper dbhelper = new DbHelper(context);
        cd.enqueue(new Callback<ContentData>() {
            @Override
            public void onResponse(Call<ContentData> call, final Response<ContentData> response) {
                final ContentData cd = response.body();
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        //Log.d(Consts.LOG_TAG, cd.toString());
                        for (Data d : response.body().getData()) {
                            if (dbhelper.upsertMediaEntity(d)) {
                                // publishProgress(cd.getCurrent_page() * cd.getPer_page() / cd.getTotal());
                                // Log.d(Consts.LOG_TAG,"successfully adding Data for page: "+ cd.getCurrent_page());
                                Data data = dbhelper.getMediaEntityByIdAndLaunguageId(d.getId(),
                                        Utility.getLanguageIdFromSharedPreferences(context));
                                if (data != null) {
                                    if (data.getLocalFilePath() != null && !data.getLocalFilePath().equals("")) {
                                        if (Consts.IS_DEBUG_LOG) {
                                            Log.d(Consts.LOG_TAG, "getLocalFilePath " + null);
                                        }
                                    } else {
                                        //upsert media entity if not downloaded
                                        dbhelper.upsertDownloadMediaEntity(d);
                                        if (Consts.IS_DEBUG_LOG) {
                                            //Log.d(Consts.LOG_TAG, " Data for page: " + d);
                                        }
                                    }
                                }
                            } else {
                                if (Consts.IS_DEBUG_LOG) {
                                    Log.d(Consts.LOG_TAG, "failure adding Media Data for page: " + cd.getCurrent_page());
                                }
                            }
                        }
                        String lastCalled = response.headers().get("last_request_date");
                        Log.d(Consts.LOG_TAG, "response last_request_date: " + lastCalled);
                        if (lastCalled != null) {
                            DbHelper dbhelper = new DbHelper(context);
                            CacheServiceCallData ob = new CacheServiceCallData();
                            ob.setUrl(Consts.URL_MEDIA_LATEST);
                            ob.setLastCalled(lastCalled);

                            dbhelper.upsertCacheServiceCall(ob);
                        }
                        callback.onDone(Consts.URL_MEDIA_LATEST, cd, null);
                        return null;
                    }
                }.execute();

            }

            @Override
            public void onFailure(Call<ContentData> call, Throwable t) {
                Log.d(Consts.LOG_TAG, "Failure in service latest Media" + t.toString());
                callback.onDone(Consts.URL_MEDIA_LATEST, null, t.toString());
            }

        });
    }

    //call medialanguage/latest Service
    public void callMedialanguageLatestContentService(ContentServiceRequest request, final IServiceSuccessCallback<ContentData> callback) {
        request.setApi_token(Consts.API_KEY);

        final Call<ContentData> cd = service.MedialanguageLatestContent(request);
        final DbHelper dbhelper = new DbHelper(context);
        cd.enqueue(new Callback<ContentData>() {
            @Override
            public void onResponse(Call<ContentData> call, final Response<ContentData> response) {
                final ContentData cd = response.body();
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        //Log.d(Consts.LOG_TAG, cd.toString());
                        if (response.body() != null) {
                            for (Data d : response.body().getData()) {
                                if (dbhelper.upsertMedialanguageLatestDataEntity(d)) {
                                    // publishProgress(cd.getCurrent_page() * cd.getPer_page() / cd.getTotal());
                                    // Log.d(Consts.LOG_TAG,"successfully adding Data for page: "+ cd.getCurrent_page());
                                } else {
                                    Log.d(Consts.LOG_TAG, "failure adding Data for page: " + cd.getCurrent_page());
                                }
                            }
                        }
                        String lastcalled = response.headers().get("last_request_date");
                        Log.d(Consts.LOG_TAG, "response last_request_date: " + lastcalled);
                        if (lastcalled != null) {
                            DbHelper dbhelper = new DbHelper(context);
                            CacheServiceCallData ob = new CacheServiceCallData();
                            ob.setUrl(Consts.MediaLanguage_Latest);
                            ob.setLastCalled(lastcalled);

                            dbhelper.upsertCacheServiceCall(ob);
                        }
                        callback.onDone(Consts.MediaLanguage_Latest, cd, null);
                        return null;
                    }
                }.execute();


            }

            @Override
            public void onFailure(Call<ContentData> call, Throwable t) {
                Log.d(Consts.LOG_TAG, "Failure in service callMedialanguageLatestAsync" + t.toString());
                callback.onDone(Consts.MediaLanguage_Latest, null, t.toString());
            }

        });
    }

    //create account service
    public void callCreateAccountService(AccountServiceRequest request, final IServiceSuccessCallback<AccountData> callback) {
        request.setApi_token(Consts.API_KEY);

        final DbHelper dbhelper = new DbHelper(context);
        String device_token = Utility.getDeviceIDFromSharedPreferences(context);
        if (device_token != null) {
            request.setDevice_token(device_token);
        }
        request.setService(Consts.SERVICE);
        request.setIs_development(Consts.IS_DEVELOPMENT);
        Call<AccountData> ac = service.accountCreate(request);
        Log.d(Consts.LOG_TAG, "payload***" + request);
        ac.enqueue(new Callback<AccountData>() {
            @Override
            public void onResponse(Call<AccountData> call, Response<AccountData> response) {
                AccountData data = response.body();

                if (data != null) {
                    Log.d(Consts.LOG_TAG, "account create data_item:" + data.toString());
                    if (dbhelper.upsertAccountData(data)) {
                        Utility.setUserServerIdIntoSharedPreferences(context, data.getId());//for check verification done or not
                        Log.d(Consts.LOG_TAG, "Account successfully add in database ");
                    }
                    callback.onDone(Consts.CREATE_NEW_USER, data, null);
                } else {
                    callback.onDone(Consts.CREATE_NEW_USER, null, null);
                }

            }

            @Override
            public void onFailure(Call<AccountData> call, Throwable t) {
                Log.d(Consts.LOG_TAG, "Failure in service account create" + t.toString());
                callback.onDone(Consts.CREATE_NEW_USER, null, t.toString());
            }

        });
    }

    //updated profile service
    public void callProfileUpdateService(ProfileUpdateServiceRequest request, final IServiceSuccessCallback<AccountData> callback) {

        final DbHelper dbhelper = new DbHelper(context);
        AccountData accountDataApToken = dbhelper.getAccountData();
        if (accountDataApToken != null) {
            if (accountDataApToken.getApi_token() != null) {
                request.setApi_token(accountDataApToken.getApi_token());
            }
        }
        Call<AccountData> ac = service.profileUpdated(request.getApi_token(), request);
        Log.d(Consts.LOG_TAG, "payload***" + request);
        ac.enqueue(new Callback<AccountData>() {
            @Override
            public void onResponse(Call<AccountData> call, Response<AccountData> response) {
                AccountData data = response.body();

                if (data != null) {
                    Log.d(Consts.LOG_TAG, "profile updated data_item:" + data.toString());
                    if (dbhelper.upsertAccountData(data)) {
                        Log.d(Consts.LOG_TAG, "Profile updated successfully in database ");
                    }
                    callback.onDone(Consts.Profile_Update, data, null);
                } else {
                    callback.onDone(Consts.Profile_Update, null, null);
                }

            }

            @Override
            public void onFailure(Call<AccountData> call, Throwable t) {
                Log.d(Consts.LOG_TAG, "Failure in service Profile_Update" + t.toString());
                callback.onDone(Consts.Profile_Update, null, t.toString());
            }

        });
    }

    //Otp verification service
    public void callOtpVerificationService(OtpVerificationServiceRequest request, final IServiceSuccessCallback<OtpData> callback) {


        final DbHelper dbhelper = new DbHelper(context);
        final AccountData accountData = new AccountData();
        final int serverId = Utility.getUserServerIdFromSharedPreferences(context);
        AccountData accountDataApToken = dbhelper.getAccountData();
        if (accountDataApToken != null) {
            if (accountDataApToken.getApi_token() != null) {
                request.setApi_token(accountDataApToken.getApi_token());
            }
        }
        Log.d(Consts.LOG_TAG, "payload***" + request);
        Call<OtpData> ac = service.otpVerify(request.getApi_token(), request);
        ac.enqueue(new Callback<OtpData>() {
            @Override
            public void onResponse(Call<OtpData> call, Response<OtpData> response) {
                OtpData data = response.body();
                if (data != null) {
                    accountData.setId(serverId);
                    accountData.setIsVerified(1);
                    //update account verified for check account verified or not
                    dbhelper.updateAccountDataVerified(accountData);
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "response data_item:" + response.toString());
                        Log.d(Consts.LOG_TAG, "Otp verify data_item:" + data.toString());
                    }
                    Toast.makeText(context, context.getString(R.string.registered), Toast.LENGTH_LONG).show();
                    //Log.d(Consts.LOG_TAG, "Otp verify successfully ");
                    callback.onDone(Consts.VERIFY_OTP, data, null);
                } else {
                    accountData.setId(serverId);
                    accountData.setIsVerified(0);
                    //update account verified for check account verified or not
                    dbhelper.updateAccountDataVerified(accountData);
                    Toast.makeText(context, context.getString(R.string.enter_valid_otp), Toast.LENGTH_LONG).show();
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "response data_item:" + response.toString());
                        Log.d(Consts.LOG_TAG, "Otp not verify");
                    }
                    callback.onDone(Consts.VERIFY_OTP, null, null);
                }

            }

            @Override
            public void onFailure(Call<OtpData> call, Throwable t) {
                Log.d(Consts.LOG_TAG, "Failure in service latestContent" + t.toString());
                callback.onDone(Consts.VERIFY_OTP, null, t.toString());
            }

        });
    }

    //download file request delete
    public void callDownloadDeleteRequest(String fileUrl) {
        Call<ResponseBody> call = service.downloadFileWithDynamicUrlSync(fileUrl);
        call.cancel();
    }

    //download file service
    public void callDownloadFileService(final String fileUrl, final int mediaId, final IServiceSuccessCallback<String> callback) {
        Call<ResponseBody> call = service.downloadFileWithDynamicUrlSync(fileUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "server contacted and has file");
                    }

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(), fileUrl, mediaId);
                    if (writtenToDisk) {
                        DbHelper dbHelper = new DbHelper(context);
                    }
                    callback.onDone(fileUrl, response.toString(), null);
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "file download was a success? " + writtenToDisk);
                    }
                } else {
                    callback.onDone(fileUrl, null, null);
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "server contact failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (Consts.IS_DEBUG_LOG) {
                    Log.e(Consts.LOG_TAG, "error");
                }
            }
        });


    }

    //write download file
    private boolean writeResponseBodyToDisk(ResponseBody body, String fileUrl, int mediaId) {
        try {
            //File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");

            File testDirectory = Utility.getFilePath(context);
            String localFilePath = null;

            if (fileUrl.contains(".mp4")) {
                localFilePath = testDirectory + File.separator + mediaId + ".mp4";
            } else {
                if (fileUrl.contains(".jpg")) {
                    localFilePath = testDirectory + File.separator + mediaId + ".jpg";
                } else if (fileUrl.contains(".png")) {
                    localFilePath = testDirectory + File.separator + mediaId + ".png";
                } else if (fileUrl.contains(".jpeg")) {
                    localFilePath = testDirectory + File.separator + mediaId + ".jpeg";
                } else {
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "downloading file worng formate");
                    }
                }
            }
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "localFilePath = " + localFilePath);
            }


            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try {
                testDirectory.mkdirs();
                if (!testDirectory.exists()) {
                    testDirectory.mkdir();
                }

                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                long total = 0;
                int progress = 0;

                inputStream = body.byteStream();
                long lenghtOfFile = body.contentLength();
                outputStream = new FileOutputStream(localFilePath);
                DbHelper dbHelper = new DbHelper(context);
                while (true) {
                    int read = inputStream.read(fileReader);
                    total += read;
                    int progress_temp = (int) ((int) total * 100 / lenghtOfFile);

                    progress = progress_temp;
                    Data downloadingFileData = new Data();
                    downloadingFileData.setMediaId(mediaId);
                    downloadingFileData.setProgress(progress);
                    dbHelper.upsertDownloadingFileEntity(downloadingFileData);

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(Consts.mBroadcastProgressUpdateAction);
                    broadcastIntent.putExtra("progresss", progress);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);

                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "progress: " + progress + " progress_temp " + progress_temp);
                    }
                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;


                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }
                }
                //update MediaEntity table for local file path
                Data fileData = new Data();
                fileData.setLocalFilePath(localFilePath);
                fileData.setId(mediaId);
                dbHelper.updateMediaLanguageLocalFilePathEntity(fileData);
                dbHelper.updateDownloadMediaLocalFilePathEntity(fileData);

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    //call languages service
    public void calllanguagesService(final IServiceSuccessCallback<String> callback) {
        Call<List<LanguageData>> call = service.getLanguage(Consts.Languages);
        final DbHelper dbHelper = new DbHelper(context);
        call.enqueue(new Callback<List<LanguageData>>() {
            @Override
            public void onResponse(Call<List<LanguageData>> call, Response<List<LanguageData>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (LanguageData data : response.body()) {
                            if (!data.getCode().equalsIgnoreCase("or-IN")) {
                                if (dbHelper.upsertLanguageDataEntity(data)) {
                                    // Log.d(Consts.LOG_TAG,"successfully adding Data: "+ data_item.getName());
                                } else {
                                    Log.d(Consts.LOG_TAG, "failure adding Data for page: " + data.getName());
                                }
                            }
                        }
                    }
                    callback.onDone(Consts.Languages, "Done", null);
                } else {
                    callback.onDone(Consts.Languages, null, null);
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "server response false");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<LanguageData>> call, Throwable t) {
                callback.onDone(Consts.Languages, null, null);
                if (Consts.IS_DEBUG_LOG) {
                    Log.e(Consts.LOG_TAG, "error");
                }
            }
        });
    }

    //call notification service to get notification data_item
    public void callNotificationService(ContentServiceRequest request, final IServiceSuccessCallback<ContentData> callback) {
        request.setApi_token(Consts.API_KEY);

        int languageId = Utility.getLanguageIdFromSharedPreferences(context);
        if (languageId != 0) {
            request.setLanguage_id(languageId);
        }
        Call<ContentData> cd = service.NotificationContent(request);
        final DbHelper dbhelper = new DbHelper(context);
        cd.enqueue(new Callback<ContentData>() {
            @Override
            public void onResponse(Call<ContentData> call, Response<ContentData> response) {
                ContentData cd = response.body();
                //Log.d(Consts.LOG_TAG, cd.toString());

                for (Data d : response.body().getData()) {
                    if (dbhelper.upsertNotificationDataEntity(d)) {
                        // Log.d(Consts.LOG_TAG,"successfully adding Data for page: "+ cd.getCurrent_page());
                    } else {
                        Log.d(Consts.LOG_TAG, "failure adding Data for page: " + cd.getCurrent_page());
                    }
                }
                String lastcalled = response.headers().get("last_request_date");
                Log.d(Consts.LOG_TAG, "response last_request_date: " + lastcalled);
                if (lastcalled != null) {
                    DbHelper dbhelper = new DbHelper(context);
                    CacheServiceCallData ob = new CacheServiceCallData();
                    ob.setUrl(Consts.Notifications);
                    ob.setLastCalled(lastcalled);

                    dbhelper.upsertCacheServiceCall(ob);
                }
                callback.onDone(Consts.Notifications, cd, null);
            }

            @Override
            public void onFailure(Call<ContentData> call, Throwable t) {
                Log.d(Consts.LOG_TAG, "Failure in service latestContent" + t.toString());
                callback.onDone(Consts.Notifications, null, t.toString());
            }

        });
    }

}

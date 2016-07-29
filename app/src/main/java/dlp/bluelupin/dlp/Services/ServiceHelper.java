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
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.OtpData;
import dlp.bluelupin.dlp.Models.OtpVerificationServiceRequest;
import dlp.bluelupin.dlp.Services.IServiceManager;
import dlp.bluelupin.dlp.Services.IServiceSuccessCallback;
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
            public void onResponse(Call<ContentData> call, Response<ContentData> response) {
                ContentData cd = response.body();
                //Log.d(Consts.LOG_TAG, cd.toString());

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

        Call<ContentData> cd = service.latestResource(request);
        final DbHelper dbhelper = new DbHelper(context);
        cd.enqueue(new Callback<ContentData>() {
            @Override
            public void onResponse(Call<ContentData> call, Response<ContentData> response) {
                ContentData cd = response.body();
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

        Call<ContentData> cd = service.latestMedia(request);
        final DbHelper dbhelper = new DbHelper(context);
        cd.enqueue(new Callback<ContentData>() {
            @Override
            public void onResponse(Call<ContentData> call, Response<ContentData> response) {
                ContentData cd = response.body();
                //Log.d(Consts.LOG_TAG, cd.toString());

                for (Data d : response.body().getData()) {
                    if (dbhelper.upsertMediaEntity(d)) {
                        // publishProgress(cd.getCurrent_page() * cd.getPer_page() / cd.getTotal());
                        // Log.d(Consts.LOG_TAG,"successfully adding Data for page: "+ cd.getCurrent_page());
                    } else {
                        Log.d(Consts.LOG_TAG, "failure adding Media Data for page: " + cd.getCurrent_page());
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
            }

            @Override
            public void onFailure(Call<ContentData> call, Throwable t) {
                Log.d(Consts.LOG_TAG, "Failure in service latest Media" + t.toString());
                callback.onDone(Consts.URL_MEDIA_LATEST, null, t.toString());
            }

        });
    }

    //create account service
    public void callCreateAccountService(AccountServiceRequest request, final IServiceSuccessCallback<AccountData> callback) {
        request.setApi_token(Consts.API_KEY);
        Log.d(Consts.LOG_TAG, "payload***" + request);
        Call<AccountData> ac = service.accountCreate(request);
        final DbHelper dbhelper = new DbHelper(context);
        ac.enqueue(new Callback<AccountData>() {
            @Override
            public void onResponse(Call<AccountData> call, Response<AccountData> response) {
                AccountData data = response.body();

                if (data != null) {
                    Log.d(Consts.LOG_TAG, "account create data:" + data.toString());
                    if (dbhelper.upsertAccountData(data)) {
                        Log.d(Consts.LOG_TAG, "Account successfully add in database ");
                    }
                    callback.onDone(Consts.CREATE_NEW_USER, data, null);
                } else {
                    callback.onDone(Consts.CREATE_NEW_USER, null, null);
                }

            }

            @Override
            public void onFailure(Call<AccountData> call, Throwable t) {
                Log.d(Consts.LOG_TAG, "Failure in service latestContent" + t.toString());
                callback.onDone(Consts.URL_CONTENT_LATEST, null, t.toString());
            }

        });
    }

    //Otp verification service
    public void callOtpVerificationService(OtpVerificationServiceRequest request, final IServiceSuccessCallback<OtpData> callback) {
        //request.setApi_token(Consts.API_KEY);
        Log.d(Consts.LOG_TAG, "payload***" + request);
        Call<OtpData> ac = service.otpVerify(request);
        final DbHelper dbhelper = new DbHelper(context);
        ac.enqueue(new Callback<OtpData>() {
            @Override
            public void onResponse(Call<OtpData> call, Response<OtpData> response) {
                OtpData data = response.body();
                if (data != null) {
                    Log.d(Consts.LOG_TAG, "response data:" + response.toString());
                    Log.d(Consts.LOG_TAG, "Otp verify data:" + data.toString());
                    //Log.d(Consts.LOG_TAG, "Otp verify successfully ");
                    callback.onDone(Consts.CREATE_NEW_USER, data, null);
                } else {
                    Log.d(Consts.LOG_TAG, "response data:" + response.toString());
                    Log.d(Consts.LOG_TAG, "Otp not verify");
                    callback.onDone(Consts.CREATE_NEW_USER, null, null);
                }

            }

            @Override
            public void onFailure(Call<OtpData> call, Throwable t) {
                Log.d(Consts.LOG_TAG, "Failure in service latestContent" + t.toString());
                callback.onDone(Consts.URL_CONTENT_LATEST, null, t.toString());
            }

        });
    }
}

package dlp.bluelupin.dlp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import dlp.bluelupin.dlp.Activities.LanguageActivity;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Services.BackgroundSyncService;
import dlp.bluelupin.dlp.Services.IAsyncWorkCompletedCallback;
import dlp.bluelupin.dlp.Services.ServiceCaller;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 7/22/2016.
 */
public class SplashActivity extends Activity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    GregorianCalendar calendar;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000; // for checking play services installed or not
    GoogleCloudMessaging gcm;
    String regid;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private Context context;
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "569234788511"; //key=AIzaSyBvKebpsTcu2PB0krI1DBcp2CVAsygk-Gc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        context = SplashActivity.this;

        if (savedInstanceState == null) {

            setGooglePlayServices();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                checkRegistered();
            }
        }, 800);
    }


    private void invokeServiceForBackgroundUpdate() {
        calendar = (GregorianCalendar) Calendar.getInstance();
        if (Consts.IS_DEBUG_LOG)
            Log.d(Consts.LOG_TAG, "starting invokeServiceForBackgroundUpdate" + Calendar.getInstance().getTime());

        Intent myIntent = new Intent(SplashActivity.this, BackgroundSyncService.class);
        Messenger messenger = new Messenger(new Handler() {
            public void handleMessage(Message message) {
                if (Consts.IS_DEBUG_LOG)
                    Log.d(Consts.LOG_TAG, "handleMessage invokeServiceForBackgroundUpdate " + Calendar.getInstance().getTime());
                Object path = message.obj;
                if (message.arg1 == RESULT_OK) {
                    Toast.makeText(SplashActivity.this,
                            "Done Sync", Toast.LENGTH_LONG)
                            .show();
                    if (Consts.IS_DEBUG_LOG)
                        Log.d(Consts.LOG_TAG, "Sync: Messenger: handleMessage");

                } else {
                    Toast.makeText(SplashActivity.this, "sync failed.",
                            Toast.LENGTH_LONG).show();
                    if (Consts.IS_DEBUG_LOG)
                        Log.d(Consts.LOG_TAG, "Sync Messenger: handleMessage failed");
                }

            }

            ;
        });
        myIntent.putExtra("MESSENGER", messenger);
        //myIntent.setData(Uri.parse("http://"));//
        myIntent.putExtra("appName", "some data");
        pendingIntent = PendingIntent.getService(SplashActivity.this, 0,
                myIntent, 0);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                1500, pendingIntent);
    }

    //check user registered or not
    private void checkRegistered() {
        DbHelper dbhelper = new DbHelper(SplashActivity.this);
        AccountData accountData = dbhelper.getAccountData();
        if (accountData != null && !accountData.equals("")) {


            if (accountData.getIsVerified() == 1) {
                invokeServiceForBackgroundUpdate(); // app already ran once; put all updates in background
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            } else {
                dbhelper.deleteAccountData();
                // app running for the first time
                Intent mainIntent = new Intent(SplashActivity.this, LanguageActivity.class);
                startActivity(mainIntent);
                finish();
            }
        } else {
            // app running for the first time
            Intent mainIntent = new Intent(SplashActivity.this, LanguageActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }


    private void setGooglePlayServices() {
        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                Log.d(Consts.LOG_TAG, "GCM regId " + regid);
                //update DeviceId setDeviceIDIntoSharedPreferences
                Utility.setDeviceIDIntoSharedPreferences(context, regid);
            }
        } else {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "No valid Google Play Services APK found.");
            }
        }
        // endregion regular stuff
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                if (Consts.IS_DEBUG_LOG) {
                    Log.i(Consts.LOG_TAG, "This device is not supported.");
                }
                finish();
            }
            return true;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "Registration not found.");
            }
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = Utility.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "App version changed.");
            }
            return "";
        }
        return registrationId;
    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;


                    //update DeviceId setDeviceIDIntoSharedPreferences
                     Utility.setDeviceIDIntoSharedPreferences(context, regid);


                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = Utility.getAppVersion(context);
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "Saving regId on app version " + appVersion);
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(SplashActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
}

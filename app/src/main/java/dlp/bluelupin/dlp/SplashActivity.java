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



    @Override
    protected void onResume() {
        super.onResume();
    }
}

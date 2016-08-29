package dlp.bluelupin.dlp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import dlp.bluelupin.dlp.Activities.LanguageActivity;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Services.BackgroundSyncService;

/**
 * Created by Neeraj on 7/22/2016.
 */
public class SplashActivity extends Activity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    GregorianCalendar calendar;

    private Context context;
    private long timeIntervel = 18000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        context = SplashActivity.this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                checkRegistered();
            }
        }, 800);
    }

    //call service at every 5 hours of intervel
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
                timeIntervel, pendingIntent);
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
}

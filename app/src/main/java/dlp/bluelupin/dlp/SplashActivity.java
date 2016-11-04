package dlp.bluelupin.dlp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import dlp.bluelupin.dlp.Activities.LanguageActivity;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.LanguageData;
import dlp.bluelupin.dlp.Services.BackgroundSyncService;
import dlp.bluelupin.dlp.Services.IServiceSuccessCallback;
import dlp.bluelupin.dlp.Services.ServiceHelper;
import dlp.bluelupin.dlp.Utilities.Utility;

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
        Consts.playYouTubeFlag = true;//for play online you tube video device back press handel
        if (Utility.isTablet(this)) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


    }


    @Override
    protected void onStart(){
        super.onStart();
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "calling start ");
        }
        DbHelper dbHelper = new DbHelper(context);
        AccountData sd =  dbHelper.getAccountData();
        if(determineFirstTimeLaunch()) {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "firstTimeLaunch " + Utility.getAppVersion(context) + "");
            }
            copyDBWhenFirstRun();
            markFirstTimeLaunch();
        } //determineFirstTimeLaunch
        callGetAllLanguage();
    };

    private Boolean determineFirstTimeLaunch()
    {
        SharedPreferences prefs = context.getSharedPreferences("appState", Context.MODE_PRIVATE);
        if (prefs != null) {
            String firstTimeLaunchVersion = prefs.getString("firstTimeLaunch", null);
            if (firstTimeLaunchVersion != null) {
                return false;
            }
        }
        return true;
//
    }




    private void markFirstTimeLaunch()
    {
        SharedPreferences prefs = context.getSharedPreferences("appState", Context.MODE_PRIVATE);
        if (prefs != null) {
        SharedPreferences.Editor editor = prefs.edit();
            editor.putString("firstTimeLaunch", Utility.getAppVersion(context) + "");
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "firstTimeLaunch " + Utility.getAppVersion(context) + "");
            }
            editor.commit();
        }
    }

    //call service at every 5 hours of intervel
    private void invokeServiceForBackgroundUpdate() {
        calendar = (GregorianCalendar) Calendar.getInstance();
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "starting invokeServiceForBackgroundUpdate" + Calendar.getInstance().getTime());
        }

        Intent myIntent = new Intent(SplashActivity.this, BackgroundSyncService.class);
        Messenger messenger = new Messenger(new Handler() {
            public void handleMessage(Message message) {
                if (Consts.IS_DEBUG_LOG)
                    Log.d(Consts.LOG_TAG, "handleMessage invokeServiceForBackgroundUpdate " + Calendar.getInstance().getTime());
                Object path = message.obj;
                if (message.arg1 == RESULT_OK) {
                    // Toast.makeText(SplashActivity.this, "Done Sync", Toast.LENGTH_LONG).show();
                    if (Consts.IS_DEBUG_LOG)
                        Log.d(Consts.LOG_TAG, "Sync: Messenger: handleMessage");

                } else {
                    // Toast.makeText(SplashActivity.this, "sync failed.", Toast.LENGTH_LONG).show();
                    if (Consts.IS_DEBUG_LOG)
                        Log.d(Consts.LOG_TAG, "Sync Messenger: handleMessage failed");
                }

            }

            ;
        });
        myIntent.putExtra("MESSENGER", messenger);
        //myIntent.setData(Uri.parse("http://"));//
        myIntent.putExtra("appName", "some data_item");
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

    private void copyDBWhenFirstRun() {
        String db_fileName = "dlp_db.db";
        File dbFile = context.getDatabasePath(db_fileName);
        dbFile.delete(); // because this is first time delete the DB
        final String outFileName = dbFile.getPath();//"/data/data/dlp.bluelupin.dlp/databases/" + db_fileName;// Consts.outputDirectoryLocation + db_fileName; //DB_PATH + NAME;
        try {
            if (!dbFile.exists()) {
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "DB DOES NOT exists. Copying DB from asset. DB Path is " + outFileName);
                }
                //this.getWritableDatabase().close();
                //Open your local db as the input stream

                final InputStream myInput = context.getAssets().open(db_fileName, Context.MODE_PRIVATE);

                //Open the empty db as the output stream
                final OutputStream myOutput = new FileOutputStream(outFileName);
                //final FileOutputStream myOutput = context.openFileOutput(outFileName, Context.MODE_PRIVATE);

                //transfer bytes from the inputfile to the outputfile
                final byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                //Close the streams
                myOutput.flush();
                ((FileOutputStream) myOutput).getFD().sync();
                myOutput.close();
                myInput.close();
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "DB file copied Successfully " + outFileName);
                }
            }
        } catch (IOException ex) {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "Exception!!!. DB asset DOES  NOT exists!! " + ex.getMessage());
            }
        } catch (Exception ex) {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "Exception!!!. DB asset DOES  NOT exists!! " + ex.getMessage());
            }
        }

    }


    private void init() {
        //set language
        Utility.setLangRecreate(SplashActivity.this, Utility.getLanguageCodeFromSharedPreferences(SplashActivity.this));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                checkRegistered();
            }
        }, 800);
    }

    //Language service call
    public void callGetAllLanguage() {
        if (Utility.isOnline(SplashActivity.this)) {
            final ServiceHelper sh = new ServiceHelper(SplashActivity.this);
            sh.calllanguagesService(new IServiceSuccessCallback<String>() {
                @Override
                public void onDone(final String callerUrl, String d, String error) {
                    DbHelper db = new DbHelper(SplashActivity.this);
                    List<LanguageData> data = db.getAllLanguageDataEntity();
                    init();
                    if (Consts.IS_DEBUG_LOG) {
                        // Log.d(Consts.LOG_TAG, "SplashActivity: callGetAllLanguage data_item count: " + data_item.size() + "  " + data_item);
                    }
                }
            });

        } else {
            init();
        }
    }

    //alert for error message
    public void alertForErrorMessage(String errorMessage, Context mContext) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        Typeface VodafoneExB = Typeface.createFromAsset(mContext.getAssets(), "fonts/VodafoneExB.TTF");
        final AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.alertAnimation;
        View view = alert.getLayoutInflater().inflate(R.layout.custom_error_alert, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setTypeface(VodafoneExB);
        TextView ok = (TextView) view.findViewById(R.id.Ok);
        ok.setTypeface(VodafoneExB);
        LinearLayout layout_titleBar = (LinearLayout) view.findViewById(R.id.alert_layout_titleBar);
        View divider = (View) view.findViewById(R.id.divider);
        GradientDrawable drawable = (GradientDrawable) ok.getBackground();
        drawable.setStroke(5, Color.parseColor("#e60000"));// set stroke width and stroke color
        drawable.setColor(Color.parseColor("#e60000")); //
        title.setText(errorMessage);
        alert.setCustomTitle(view);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
    }
}

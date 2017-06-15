package dlp.bluelupin.dlp.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dlp.bluelupin.dlp.Adapters.NotificationAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.SimpleItemTouchHelperCallback;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.AccountServiceRequest;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.IAsyncWorkCompletedCallback;
import dlp.bluelupin.dlp.Services.ServiceCaller;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;

public class NotificationsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title, leftArrow;
    private RecyclerView notificationRecyclerView;
    private Boolean initFlag = false;
    private TextView back, noRecordIcon;
    Typeface materialdesignicons_font;
    Typeface VodafoneExB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        if (Utility.isTablet(this)) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = (TextView) toolbar.findViewById(R.id.title);


        Typeface custom_fontawesome = FontManager.getFontTypeface(this, "fonts/fontawesome-webfont.ttf");
        materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(this, "fonts/materialdesignicons-webfont.otf");
        VodafoneExB = FontManager.getFontTypeface(this, "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = FontManager.getFontTypeface(this, "fonts/VodafoneRg.ttf");

        title.setTypeface(VodafoneExB);

        callNotificationService();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
    }


    //call notification service
    private void callNotificationService() {
        ContentServiceRequest request = new ContentServiceRequest();
        request.setPage(1);
        DbHelper db = new DbHelper(NotificationsActivity.this);
        CacheServiceCallData cacheSeviceCallData = db.getCacheServiceCallByUrl(Consts.Notifications);
        if (cacheSeviceCallData != null) {
            // request.setStart_date(cacheSeviceCallData.getLastCalled());
            Log.d(Consts.LOG_TAG, "NotificationActivity: cacheSeviceCallData : " + cacheSeviceCallData.getLastCalled());
        }
        ServiceCaller sc = new ServiceCaller(NotificationsActivity.this);
        final CustomProgressDialog customProgressDialog = new CustomProgressDialog(this, R.mipmap.syc);

        if (Utility.isOnline(this)) {
            customProgressDialog.show();
            sc.getAllNotificationContent(request, new IAsyncWorkCompletedCallback() {
                @Override
                public void onDone(String workName, boolean isComplete) {
                    setNotification();
                    Log.d(Consts.LOG_TAG, "NotificationActivity: callContentAsync success result: " + isComplete);
                    customProgressDialog.dismiss();
                }
            });
        } else {
            setNotification();
            Utility.alertForErrorMessage(getString(R.string.online_msg), NotificationsActivity.this);
        }
    }

    //set notification data_item into adapter
    private void setNotification() {
        DbHelper db = new DbHelper(NotificationsActivity.this);
        int languageId = Utility.getLanguageIdFromSharedPreferences(NotificationsActivity.this);
        List<Data> data = db.getAllNotificationDataEntity(languageId);
        if (data != null && data.size() > 0) {
            Collections.sort(data, new Comparator<Data>() {
                //2016-07-08 12:06:30
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

                public int compare(Data m1, Data m2) {
                    Date date1 = null;
                    Date date2 = null;
                    int shortdate = 0;
                    try {
                        date1 = sdf.parse(m1.getSend_at());
                        date2 = sdf.parse(m2.getSend_at());
                        // Log.d(Contants.LOG_TAG, "date**********   " + date1 + "***" + date2);
                        //Log.d(Contants.LOG_TAG, " output.format(date1);**********   " + output.format(date1));
                        if (date1 != null && date2 != null) {
                            shortdate = date1.getTime() > date2.getTime() ? -1 : 1;//descending
                            //shortdate = date1.getTime() > date2.getTime() ? 1 : -1;     //ascending
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return shortdate;
                }
            });

            notificationRecyclerView = (RecyclerView) findViewById(R.id.notificationRecyclerView);
            notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            notificationRecyclerView.setHasFixedSize(true);
            //notificationRecyclerView.setNestedScrollingEnabled(false);
            //Log.d(Consts.LOG_TAG, "NotificationActivity: data_item count: " + data_item.size());
            NotificationAdapter notificationsAdapter = new NotificationAdapter(NotificationsActivity.this, data);
            notificationRecyclerView.setAdapter(notificationsAdapter);
            //ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(notificationsAdapter);
            //ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            //touchHelper.attachToRecyclerView(notificationRecyclerView);
        } else {
            //initFlag = true;
            setContentView(R.layout.no_record_found);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            title = (TextView) toolbar.findViewById(R.id.title);
            title.setTypeface(VodafoneExB);
            noRecordIcon = (TextView) findViewById(R.id.noRecordIcon);
            noRecordIcon.setTypeface(materialdesignicons_font);
            noRecordIcon.setText(Html.fromHtml("&#xf187;"));
            //init();

        }

    }
}

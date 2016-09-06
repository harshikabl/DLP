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

import java.util.ArrayList;
import java.util.List;

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
import dlp.bluelupin.dlp.Utilities.Utility;

public class NotificationsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title, leftArrow;
    private RecyclerView notificationRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        if (Utility.isTablet(this)) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        leftArrow = (TextView) toolbar.findViewById(R.id.leftArrow);
        title = (TextView) toolbar.findViewById(R.id.title);
        leftArrow.setOnClickListener(this);
        Typeface custom_fontawesome = Typeface.createFromAsset(this.getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface materialdesignicons_font = Typeface.createFromAsset(this.getAssets(), "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneRg.ttf");
        leftArrow.setTypeface(materialdesignicons_font);
        leftArrow.setText(Html.fromHtml("&#xf04d;"));
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
            Utility.alertForErrorMessage(Consts.OFFLINE_MESSAGE, NotificationsActivity.this);
        }
    }

    //set notification data into adapter
    private void setNotification() {
        DbHelper db = new DbHelper(NotificationsActivity.this);
        int languageId = Utility.getLanguageIdFromSharedPreferences(NotificationsActivity.this);
        List<Data> data = db.getAllNotificationDataEntity(languageId);
        if (data != null) {
            notificationRecyclerView = (RecyclerView) findViewById(R.id.notificationRecyclerView);
            notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            notificationRecyclerView.setHasFixedSize(true);
            //notificationRecyclerView.setNestedScrollingEnabled(false);
            Log.d(Consts.LOG_TAG, "NotificationActivity: data count: " + data.size());
            NotificationAdapter notificationsAdapter = new NotificationAdapter(NotificationsActivity.this, data);
            notificationRecyclerView.setAdapter(notificationsAdapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(notificationsAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(notificationRecyclerView);
        }

    }
}

package dlp.bluelupin.dlp.Activities;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Adapters.NotificationAdapter;
import dlp.bluelupin.dlp.Fragments.SimpleItemTouchHelperCallback;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.Utility;

public class NotificationsActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView title, leftArrow;

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

        List<String> list = new ArrayList<String>();
        list.add("English");
        list.add("Hindi");
        list.add("Tamil");
        list.add("English");
        list.add("Hindi");
        list.add("Tamil");
        NotificationAdapter notificationsAdapter = new NotificationAdapter(this, list);
        RecyclerView notificationRecyclerView = (RecyclerView)findViewById(R.id.notificationRecyclerView);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationRecyclerView.setHasFixedSize(true);
        //notificationRecyclerView.setNestedScrollingEnabled(false);
        notificationRecyclerView.setAdapter(notificationsAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(notificationsAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(notificationRecyclerView);


    }

    @Override
    public void onClick(View v) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
    }
}

package dlp.bluelupin.dlp;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import dlp.bluelupin.dlp.Activities.LanguageActivity;
import dlp.bluelupin.dlp.Activities.NotificationsActivity;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.CourseFragment;
import dlp.bluelupin.dlp.Fragments.FavoritesFragment;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Services.IAsyncWorkCompletedCallback;
import dlp.bluelupin.dlp.Services.IServiceManager;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Services.ServiceCaller;
import dlp.bluelupin.dlp.Utilities.CardReaderHelper;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.DecompressZipFile;
import dlp.bluelupin.dlp.Utilities.Utility;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private TextView title;
    public FrameLayout downloadContainer;
    private static MainActivity mainActivity;
    CustomProgressDialog customProgressDialog;

    public static MainActivity getInstace() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        setSupportActionBar(toolbar);
        Typeface VodafoneExB = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneExB.TTF");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) toolbar.findViewById(R.id.title);
        title.setTypeface(VodafoneExB);
        customProgressDialog = new CustomProgressDialog(this, R.mipmap.syc);

        downloadContainer = (FrameLayout) findViewById(R.id.downloadContainer);
        final DbHelper dbhelper = new DbHelper(this);

        customProgressDialog.show();
        callSync();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ContentServiceRequest request = new ContentServiceRequest();
//                callContentAsync();
//                callResourceAsync();
//                callMediaAsync();
//            }
//
//            ;
//
//
////         Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.name);
        TextView email = (TextView) header.findViewById(R.id.email);
        AccountData accountData = dbhelper.getAccountData();
        if (accountData != null && !accountData.equals("")) {
            name.setText(accountData.getName());
            email.setText(accountData.getEmail());
        }

    }


    private void setUpCourseFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CourseFragment fragment = CourseFragment.newInstance("", "");
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.out_to_right, R.anim.in_from_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.notification) {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, LanguageActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        } else if (id == R.id.zip) {
            Utility.verifyStoragePermissions(this);
            CardReaderHelper cardReaderHelper = new CardReaderHelper(MainActivity.this);
            String SDPath = ""; // get this location from sharedpreferance;
            cardReaderHelper.readDataFromSDCard(SDPath);

        } else if (id == R.id.favorites) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FavoritesFragment fragment = FavoritesFragment.newInstance("", "");
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
            transaction.replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.logout) {
            DbHelper dbhelper = new DbHelper(MainActivity.this);
            dbhelper.deleteAccountData();
            Toast.makeText(MainActivity.this, "Account deleted successfully", Toast.LENGTH_LONG).show();
            Intent mainIntent = new Intent(MainActivity.this, LanguageActivity.class);
            startActivity(mainIntent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setScreenTitle(String heading) {
        this.setTitle(heading);
        title.setText(heading);
    }

    public void setdownloadContainer(boolean on) {
        if (on) {
            downloadContainer.setVisibility(View.VISIBLE);
        } else {
            downloadContainer.setVisibility(View.GONE);
        }
    }

    private void callSync() {
        callContentAsync();
        callResourceAsync();
        callMediaAsync();
    }

    private Boolean contentCallDone = false, resourceCallDone = false, mediaCallDone = false;

    private void sendMessageIfAllCallsDone() {
        if (contentCallDone && resourceCallDone && mediaCallDone) {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "Sync Call done in Splash");
            }
            customProgressDialog.dismiss();
            setUpCourseFragment();
        }
    }

    private void callContentAsync() {
        ContentServiceRequest request = new ContentServiceRequest();
        request.setPage(1);
        DbHelper db = new DbHelper(MainActivity.this);
        CacheServiceCallData cacheSeviceCallData = db.getCacheServiceCallByUrl(Consts.URL_CONTENT_LATEST);
        if (cacheSeviceCallData != null) {
            request.setStart_date(cacheSeviceCallData.getLastCalled());
            Log.d(Consts.LOG_TAG, "MainActivity: cacheSeviceCallData : " + cacheSeviceCallData.getLastCalled());
        }
        ServiceCaller sc = new ServiceCaller(MainActivity.this);
        sc.getAllContent(request, new IAsyncWorkCompletedCallback() {
            @Override
            public void onDone(String workName, boolean isComplete) {
                Log.d(Consts.LOG_TAG, "MainActivity: callContentAsync success result: " + isComplete);
                DbHelper db = new DbHelper(MainActivity.this);
                List<Data> data = db.getDataEntityByParentId(null);
                Log.d(Consts.LOG_TAG, "MainActivity: data count: " + data.size());
                contentCallDone = true;
                sendMessageIfAllCallsDone();
            }
        });
    }

    private void callResourceAsync() {
        ContentServiceRequest request = new ContentServiceRequest();
        request.setPage(1);
        DbHelper db = new DbHelper(MainActivity.this);
        CacheServiceCallData cacheSeviceCallData = db.getCacheServiceCallByUrl(Consts.URL_LANGUAGE_RESOURCE_LATEST);
        if (cacheSeviceCallData != null) {
            request.setStart_date(cacheSeviceCallData.getLastCalled());
            Log.d(Consts.LOG_TAG, "MainActivity: cacheSeviceCallData for URL_LANGUAGE_RESOURCE_LATEST: " + cacheSeviceCallData.getLastCalled());
        }
        ServiceCaller sc = new ServiceCaller(MainActivity.this);
        sc.getAllResource(request, new IAsyncWorkCompletedCallback() {
            @Override
            public void onDone(String workName, boolean isComplete) {
                Log.d(Consts.LOG_TAG, "MainActivity: callResourceAsync success result: " + isComplete);
                DbHelper db = new DbHelper(MainActivity.this);
                List<Data> data = db.getResources();
                Log.d(Consts.LOG_TAG, "MainActivity: callResourceAsync data count: " + data.size());
                resourceCallDone = true;
                sendMessageIfAllCallsDone();
            }
        });
    }

    private void callMediaAsync() {
        ContentServiceRequest request = new ContentServiceRequest();
        request.setPage(1);
        DbHelper db = new DbHelper(MainActivity.this);
        CacheServiceCallData cacheSeviceCallData = db.getCacheServiceCallByUrl(Consts.URL_MEDIA_LATEST);
        if (cacheSeviceCallData != null) {
            request.setStart_date(cacheSeviceCallData.getLastCalled());
            Log.d(Consts.LOG_TAG, "MainActivity: cacheSeviceCallData for URL_MEDIA_LATEST: " + cacheSeviceCallData.getLastCalled());
        }
        ServiceCaller sc = new ServiceCaller(MainActivity.this);
        sc.getAllMedia(request, new IAsyncWorkCompletedCallback() {
            @Override
            public void onDone(String workName, boolean isComplete) {
                Log.d(Consts.LOG_TAG, "MainActivity: callMediaAsync success result: " + isComplete);
                DbHelper db = new DbHelper(MainActivity.this);
                List<Data> data = db.getAllMedia();
                Log.d(Consts.LOG_TAG, "MainActivity: callMediaAsync data count: " + data.size());
                mediaCallDone = true;
                sendMessageIfAllCallsDone();
            }
        });
    }
}



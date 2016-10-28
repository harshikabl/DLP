package dlp.bluelupin.dlp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dlp.bluelupin.dlp.Activities.LanguageActivity;
import dlp.bluelupin.dlp.Activities.NotificationsActivity;
import dlp.bluelupin.dlp.Activities.VideoPlayerActivity;
import dlp.bluelupin.dlp.Adapters.NavigationMenuAdapter;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.CourseFragment;
import dlp.bluelupin.dlp.Fragments.FavoritesFragment;
import dlp.bluelupin.dlp.Fragments.SelectLocationFragment;
import dlp.bluelupin.dlp.Fragments.WebFragment;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.LanguageData;
import dlp.bluelupin.dlp.Services.IAsyncWorkCompletedCallback;
import dlp.bluelupin.dlp.Services.IServiceManager;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Services.IServiceSuccessCallback;
import dlp.bluelupin.dlp.Services.ServiceCaller;
import dlp.bluelupin.dlp.Services.ServiceHelper;
import dlp.bluelupin.dlp.Utilities.CardReaderHelper;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.DecompressZipFile;
import dlp.bluelupin.dlp.Utilities.Utility;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int PERMISSION_REQUEST_CODE = 1;

    private Toolbar toolbar;
    private TextView title;
    public FrameLayout downloadContainer;
    private static MainActivity mainActivity;
    private CustomProgressDialog customProgressDialog;

    public static MainActivity getInstace() {
        return mainActivity;
    }

    private TextView name, email;
    private ImageView splashImage;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        setSupportActionBar(toolbar);
        Typeface VodafoneExB = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneExB.TTF");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) toolbar.findViewById(R.id.title);
        splashImage = (ImageView) findViewById(R.id.splashImage);
        title.setTypeface(VodafoneExB);
        customProgressDialog = new CustomProgressDialog(this, R.mipmap.syc);
        if (Utility.isTablet(this)) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //alertForProgressBar();


        downloadContainer = (FrameLayout) findViewById(R.id.downloadContainer);
        final DbHelper dbhelper = new DbHelper(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Typeface VodafoneRg = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneRg.ttf");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setItemIconTintList(null);

        //View header = navigationView.getHeaderView(0);
        Typeface custom_fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface materialdesignicons_font = Typeface.createFromAsset(getAssets(), "fonts/materialdesignicons-webfont.otf");
        View header = findViewById(R.id.navHader);
        name = (TextView) header.findViewById(R.id.name);
        email = (TextView) header.findViewById(R.id.email);
        TextView logOut = (TextView) header.findViewById(R.id.logOut);
        name.setTypeface(VodafoneExB);
        email.setTypeface(VodafoneRg);
        logOut.setTypeface(materialdesignicons_font);
        logOut.setText(Html.fromHtml("&#xf343;"));
        TextView createBy = (TextView) findViewById(R.id.createBy);
        createBy.setTypeface(VodafoneRg);
        LinearLayout poweredBylayout = (LinearLayout) findViewById(R.id.poweredBylayout);

        setProfileDetails();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper dbhelper = new DbHelper(MainActivity.this);
                dbhelper.deleteAccountData();
                String msg = getString(R.string.logout);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(MainActivity.this, LanguageActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                finish();
            }
        });

        //registerReceiver();
        String localFolderPath = Consts.outputDirectoryLocation;
        File outputFolder = new File(localFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        setMenuLayout();

        poweredBylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                WebFragment fragment = WebFragment.newInstance("http://bluelup.in/pwrdby", "");
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right)
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
                closeDrawer();//nav drawer close
            }
        });

        readExternalFilesAsync();

    }

    //alert for progress bar
    public void alertForProgressBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.alertAnimation;
        View view = alert.getLayoutInflater().inflate(R.layout.progressbar_alert, null);
        alert.setCustomTitle(view);
        alert.setCanceledOnTouchOutside(false);
    }

    //set user profile
    public void setProfileDetails() {
        DbHelper dbhelper = new DbHelper(MainActivity.this);
        AccountData accountData = dbhelper.getAccountData();
        if (accountData != null && !accountData.equals("")) {
            name.setText(accountData.getName());
            email.setText(accountData.getEmail());
        }
    }

    private void readExternalFilesAsync() {
        Utility.verifyStoragePermissions((Activity) MainActivity.this);

        //final CustomProgressDialog customProgressDialog = new CustomProgressDialog(MainActivity.this, R.mipmap.syc);
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                CardReaderHelper cardReaderHelper = new CardReaderHelper(MainActivity.this);
                String folderLocation = Consts.outputDirectoryLocation;
                cardReaderHelper.ReadAppDataFolder(folderLocation);
                return true;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                customProgressDialog.show();
                //alert.show();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                if (Utility.isOnline(MainActivity.this)) {
                    callSync();
                } else {
                    customProgressDialog.dismiss();
                    Utility.alertForErrorMessage(getString(R.string.online_msg), MainActivity.this);
                    setUpCourseFragment();
                }

            }
        }.execute(null, null, null);

    }

    //set slider item value
    public void setMenuLayout() {
        Typeface VodafoneRgBd = Typeface.createFromAsset(getAssets(), "fonts/VodafoneRgBd.ttf");

        ListView menuList = (ListView) findViewById(R.id.lst_menu_items);
        menuList.setDivider(null);
        List<String> itemList = new ArrayList<String>();
        itemList.add("Home");
        itemList.add("Notification");
        itemList.add("Favorites");
        itemList.add("Downloads");
        itemList.add("Profile");
        itemList.add("Change Language");
        itemList.add("Change Downloads Folder");
        itemList.add("Terms of use");
        itemList.add("About Us");

        List<String> menuIconList = new ArrayList<String>();
        menuIconList.add("f2dc");
        menuIconList.add("f09c");
        menuIconList.add("f4ce");
        menuIconList.add("f1da");
        menuIconList.add("f631");
        menuIconList.add("f493");
        menuIconList.add("f24d");
        menuIconList.add("f219");
        menuIconList.add("f2fd");

        List<String> displayNameList = new ArrayList<String>();
        displayNameList.add(getString(R.string.home));
        displayNameList.add(getString(R.string.notification));
        displayNameList.add(getString(R.string.Favorites));
        displayNameList.add(getString(R.string.Downloads));
        displayNameList.add(getString(R.string.Profile));
        displayNameList.add(getString(R.string.Change_Language));
        displayNameList.add(getString(R.string.Change_Folder));
        displayNameList.add(getString(R.string.Terms_of_use));
        displayNameList.add(getString(R.string.about_us));
        NavigationMenuAdapter navigationMenuAdapter = new NavigationMenuAdapter(MainActivity.this, itemList, menuIconList, displayNameList);
        menuList.setAdapter(navigationMenuAdapter);
    }

    private void setUpCourseFragment() {
        //splashImage.setVisibility(View.GONE);
        try {

            FragmentManager fragmentManager = getSupportFragmentManager();
            CourseFragment fragment = CourseFragment.newInstance("", "");
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right).replace(R.id.container, fragment)
                    //.addToBackStack(null)
                    .commit();

        } catch (Exception e) {
            Log.d(Consts.LOG_TAG, "Error Message: " + e.getMessage());
        }
        //overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

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


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
      /*  // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.notification) {
            //item.setIcon(R.drawable.ic_media_play);
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
            String SDPath = Utility.getSelectFolderPathFromSharedPreferences(MainActivity.this);// get this location from sharedpreferance;
            if (SDPath != null && !SDPath.equals("")) {
                cardReaderHelper.readDataFromSDCard(SDPath);
            } else {
                cardReaderHelper.readDataFromSDCard(Consts.inputDirectoryLocation);
            }

        } else if (id == R.id.favorites) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FavoritesFragment fragment = FavoritesFragment.newInstance("", "");
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
            transaction.replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.selectFolder) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            SelectLocationFragment fragment = SelectLocationFragment.newInstance("", "");
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
            transaction.replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_send) {

        } else if (id == R.id.logout) {
            DbHelper dbhelper = new DbHelper(MainActivity.this);
            dbhelper.deleteAccountData();
            Toast.makeText(MainActivity.this, "You have been logged out.", Toast.LENGTH_LONG).show();
            Intent mainIntent = new Intent(MainActivity.this, LanguageActivity.class);
            startActivity(mainIntent);
            finish();
        }*/


        return true;
    }

    //close drawer after item select
    public void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        //return true;
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

    public void callSync() {
        callContentAsync();
        callResourceAsync();
        callMediaAsync();
        callMedialanguageLatestAsync();
    }

    private Boolean contentCallDone = false, resourceCallDone = false, mediaCallDone = false;

    private void sendMessageIfAllCallsDone() {
        if (contentCallDone && resourceCallDone && mediaCallDone) {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "Sync Call done in Splash");
            }
            if (customProgressDialog != null && customProgressDialog.isShowing()) {
                customProgressDialog.dismiss();
            }

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
                //DbHelper db = new DbHelper(MainActivity.this);
                //List<Data> data = db.getDataEntityByParentId(null);
                // Log.d(Consts.LOG_TAG, "MainActivity: data_item count: " + data.size());
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
               /* Log.d(Consts.LOG_TAG, "MainActivity: callResourceAsync success result: " + isComplete);
                DbHelper db = new DbHelper(MainActivity.this);
                List<Data> data = db.getResources();
                Log.d(Consts.LOG_TAG, "MainActivity: callResourceAsync data_item count: " + data.size());*/
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
               /* Log.d(Consts.LOG_TAG, "MainActivity: callMediaAsync success result: " + isComplete);
                DbHelper db = new DbHelper(MainActivity.this);
                List<Data> data = db.getAllMedia();
                Log.d(Consts.LOG_TAG, "MainActivity: callMediaAsync data_item count: " + data.size());*/
                mediaCallDone = true;
                sendMessageIfAllCallsDone();
            }
        });
    }

    private void callMedialanguageLatestAsync() {
        ContentServiceRequest request = new ContentServiceRequest();
        request.setPage(1);
        DbHelper db = new DbHelper(MainActivity.this);
        CacheServiceCallData cacheSeviceCallData = db.getCacheServiceCallByUrl(Consts.MediaLanguage_Latest);
        if (cacheSeviceCallData != null) {
            request.setStart_date(cacheSeviceCallData.getLastCalled());
            Log.d(Consts.LOG_TAG, "MainActivity: cacheSeviceCallData : " + cacheSeviceCallData.getLastCalled());
        }
        ServiceCaller sc = new ServiceCaller(MainActivity.this);
        sc.getAllMedialanguageLatestContent(request, new IAsyncWorkCompletedCallback() {
            @Override
            public void onDone(String workName, boolean isComplete) {
              /*  if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "MainActivity: callMedialanguageLatestAsync success result: " + isComplete);
                }
                DbHelper db = new DbHelper(MainActivity.this);
                List<Data> data = db.getAllMedialanguageLatestDataEntity();
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "MainActivity: callMedialanguageLatestAsync data_item count: " + data.size());
                }*/
                sendMessageIfAllCallsDone();
            }
        });
    }


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ContentServiceRequest request = new ContentServiceRequest();
//                callContentAsync();
//                callResourceAsync();
//                callMediaAsync();
//            };
////         Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//
//        });
}



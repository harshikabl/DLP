package dlp.bluelupin.dlp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.List;

import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Services.CallContentServiceTask;
import dlp.bluelupin.dlp.Services.IAsyncWorkCompletedCallback;
import dlp.bluelupin.dlp.Services.IServiceManager;
import dlp.bluelupin.dlp.Models.ContentData;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Services.IServiceSuccessCallback;
import dlp.bluelupin.dlp.Services.ServiceCaller;
import dlp.bluelupin.dlp.Services.ServiceHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Consts.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final IServiceManager service = retrofit.create(IServiceManager.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final DbHelper dbhelper = new DbHelper(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {;
                ContentServiceRequest request = new ContentServiceRequest();
                callContentAsync();
            };

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

//                ContentServiceRequest request = new ContentServiceRequest();
//                request.setApi_token(Consts.API_KEY);
//                Call<ContentData> cd = service.latestContent(request);
//
//                cd.enqueue(new Callback<ContentData>() {
//                    @Override
//                    public void onResponse(Call<ContentData> call, Response<ContentData> response) {
//                        Log.d("Response", response.body().toString());
//                        for (Data d:response.body().getData()) {
//                            if(dbhelper.upsertDataEntity(d)) {
//                                Log.d(Consts.LOG_TAG,"successfully adding Data: "+ d.toString());
//                            }
//                            else
//                            {
//                                Log.d(Consts.LOG_TAG,"failure adding Data: "+ d.toString());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ContentData> call, Throwable t) {
//                        Log.d(Consts.LOG_TAG, "Failure in service latestContent" +  t.toString());
//                    }
//
//                });



        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void callContentAsync()
    {
        ContentServiceRequest request = new ContentServiceRequest();
        request.setPage(1);
        DbHelper db = new DbHelper(MainActivity.this);
        CacheServiceCallData cacheSeviceCallData = db.getCacheServiceCallByUrl(Consts.URL_CONTENT_LATEST);
        if(cacheSeviceCallData != null) {
            request.setStart_date(cacheSeviceCallData.getLastCalled());
            Log.d(Consts.LOG_TAG, "MainActivity: cacheSeviceCallData : " + cacheSeviceCallData.getLastCalled());
        }
        ServiceCaller sc = new ServiceCaller(MainActivity.this);
        sc.getAllContent(request, new IAsyncWorkCompletedCallback() {
            @Override
            public void onDone(String workName, boolean isComplete) {
                Log.d(Consts.LOG_TAG, "MainActivity: callContentAsync success result: " + isComplete);
                DbHelper db = new DbHelper(MainActivity.this);
                List<Data> data =  db.getDataEntityByParentId(null);
                Log.d(Consts.LOG_TAG, "MainActivity: data count: " + data.size());
            }
        });
//        new AsyncTask<ContentServiceRequest, Integer, Boolean>() {
//
//            @Override
//            protected Boolean doInBackground(ContentServiceRequest... params) {
//                final ContentServiceRequest request = params[0];
//                ServiceCaller sc = new ServiceCaller(MainActivity.this);
//                sc.getAllContent(request, new IAsyncWorkCompletedCallback() {
//                    @Override
//                    public void onDone(String workName, boolean isComplete) {
//                        Log.d(Consts.LOG_TAG, "MainActivity: callContentAsync success result: " + isComplete);
//
//                    }
//
//                });
//                return true;
//            }
//
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected void onPostExecute(Boolean s) {
//                DbHelper db = new DbHelper(MainActivity.this);
//                List<Data> data =  db.getDataEntityByParentId(null);
//                Log.d(Consts.LOG_TAG, "MainActivity: data count: " + data.size());
//                super.onPostExecute(s);
//            }
//
//            @Override
//            protected void onProgressUpdate(Integer... values) {
//                super.onProgressUpdate(values);
//            }
//
//        }.execute(request);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

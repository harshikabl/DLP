package dlp.bluelupin.dlp.Fragments;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Activities.DownloadService;
import dlp.bluelupin.dlp.Adapters.DownloadingAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.BindService;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DownloadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadingFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mediaId;
    private String mediaUrl;


    public DownloadingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param Id     Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DownloadingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadingFragment newInstance(int Id, String param2) {
        DownloadingFragment fragment = new DownloadingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, Id);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mediaId = getArguments().getInt(ARG_PARAM1);
            mediaUrl = getArguments().getString(ARG_PARAM2);
        }
    }

    private Context context;
    private BindService serviceBinder;
    Intent serviceIntent;

    private boolean mReceiversRegistered;
    DownloadingAdapter downloadingAdapter;
    RecyclerView downloadingRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_downloading, container, false);

        context = getActivity();
        if (Utility.isTablet(context)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        init(view);
        return view;
    }

    private void init(View view) {
        MainActivity rootActivity = (MainActivity) getActivity();
        rootActivity.setScreenTitle("Daksh");

        Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
        TextView download = (TextView) view.findViewById(R.id.download);
        download.setTypeface(VodafoneExB);


        DbHelper dbHelper = new DbHelper(getActivity());
        List<Data> data = dbHelper.getAllDownloadingMediaFile();

        downloadingAdapter = new DownloadingAdapter(context, data);
        downloadingRecyclerView = (RecyclerView) view.findViewById(R.id.downloadingRecyclerView);
        downloadingRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        downloadingRecyclerView.setHasFixedSize(true);
        //downloadingRecyclerView.setNestedScrollingEnabled(false);
        downloadingRecyclerView.setAdapter(downloadingAdapter);

        serviceIntent = new Intent(context, BindService.class);
        context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        registerReceiver();

    }

    BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           /* if (intent.getAction().equals(mBroadcastDeleteAction)) {
                //LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                context.stopService(intent);
                Toast.makeText(context, "DownloadFile cancel!",
                        Toast.LENGTH_LONG).show();
            }*/
            if (intent.getAction().equals(Consts.mBroadcastDeleteAction)) {
                int pro = intent.getIntExtra("progresss", 0);

                DbHelper dbHelper = new DbHelper(getActivity());
                List<Data> data = dbHelper.getAllDownloadingMediaFile();
                downloadingAdapter = new DownloadingAdapter(context, data);
                downloadingRecyclerView.setAdapter(downloadingAdapter);
                downloadingAdapter.notifyDataSetChanged();

                Toast.makeText(context, "progress  update= " + pro, Toast.LENGTH_LONG).show();
            }
        }
    };

    private void registerReceiver() {
        unregisterReceiver();
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter
                .addAction(Consts.mBroadcastDeleteAction);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(intentReceiver, intentToReceiveFilter);
        mReceiversRegistered = true;
    }

    private void unregisterReceiver() {
        if (mReceiversRegistered) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(intentReceiver);
            mReceiversRegistered = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            //---called when the connection is made---
            serviceBinder = ((BindService.MyBinder) service).getService();
            try {
                URL[] urls = new URL[]{
                        new URL(mediaUrl)};
                //---assign the URLs to the service through the serviceBinder object---
                serviceBinder.urls = urls;
                serviceBinder.mediaId = mediaId;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            context.startService(serviceIntent);
        }

        public void onServiceDisconnected(ComponentName className) {
            //---called when the service disconnects---
            serviceBinder = null;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


}

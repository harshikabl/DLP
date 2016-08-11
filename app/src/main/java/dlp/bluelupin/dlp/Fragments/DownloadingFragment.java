package dlp.bluelupin.dlp.Fragments;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import dlp.bluelupin.dlp.Activities.DownloadService;
import dlp.bluelupin.dlp.MainActivity;
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
    private String mParam1;
    private String mParam2;


    public DownloadingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DownloadingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadingFragment newInstance(String param1, String param2) {
        DownloadingFragment fragment = new DownloadingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private Context context;
    private BindService serviceBinder;
    Intent serviceIntent;

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
        /* List<String> list=new ArrayList<String>();
        DownloadingAdapter downloadingAdapter = new DownloadingAdapter(context, list);
        RecyclerView downloadingRecyclerView = (RecyclerView) view.findViewById(R.id.downloadingRecyclerView);
        downloadingRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        downloadingRecyclerView.setHasFixedSize(true);
        //downloadingRecyclerView.setNestedScrollingEnabled(false);
        downloadingRecyclerView.setAdapter(downloadingAdapter);*/


        serviceIntent = new Intent(context, DownloadService.class);
        context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        BroadcastReceiver intentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "File downloaded!",
                        Toast.LENGTH_LONG).show();
            }
        };
    }

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            //---called when the connection is made---
            serviceBinder = ((BindService.MyBinder) service).getService();
            try {
                URL[] urls = new URL[]{
                        new URL("https://s3.ap-south-1.amazonaws.com/classkonnect-test/011+Resetting+data.mp4")};
                //---assign the URLs to the service through the serviceBinder object---
                serviceBinder.urls = urls;
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

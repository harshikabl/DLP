package dlp.bluelupin.dlp.Fragments;


import android.app.ActivityManager;
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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Adapters.DownloadingAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.DownloadData;
import dlp.bluelupin.dlp.Models.DownloadMediaWithParent;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.DownloadService1;
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
    private static final String ARG_RESOURCES = "resources";

    // TODO: Rename and change types of parameters
    private int mediaId;
    private String mediaUrl;
    private List<Data> resourcesToDownloadList = null;
    private List<Data> uniqueResourcesToDownload = null;
    private int parentId;
    List<DownloadMediaWithParent> downloadMediaWithParentList = null;

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

    public static DownloadingFragment newInstance(String strJsonMediaList) {
        DownloadingFragment fragment = new DownloadingFragment();
        Bundle args = new Bundle();

        // resourcesToDownloadList
        args.putString(ARG_RESOURCES, strJsonMediaList);

        fragment.setArguments(args);
        return fragment;
    }

    public static DownloadingFragment newInstance(String strJsonMediaList, int parentId) {
        DownloadingFragment fragment = new DownloadingFragment();
        Bundle args = new Bundle();

        // resourcesToDownloadList
        args.putString(ARG_RESOURCES, strJsonMediaList);
        args.putInt("parentId", parentId);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mediaId = getArguments().getInt(ARG_PARAM1);
            mediaUrl = getArguments().getString(ARG_PARAM2);
            parentId = getArguments().getInt("parentId", 0);
            if (getArguments().getString(ARG_RESOURCES) != null) {
                String strResourcesToDownloadList = getArguments().getString(ARG_RESOURCES);
                resourcesToDownloadList = new Gson().fromJson(strResourcesToDownloadList, new TypeToken<List<Data>>() {
                }.getType());
            }
        }
    }

    private Context context;
    private BindService serviceBinder;
    Intent serviceIntent;

    private boolean mReceiversRegistered;
    DownloadingAdapter downloadingAdapter;
    RecyclerView downloadingRecyclerView;
    Typeface materialdesignicons_font;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_downloading, container, false);

        context = getActivity();
        if (Utility.isTablet(context)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        init();
        return view;
    }

    private void init() {
        MainActivity rootActivity = (MainActivity) getActivity();
        rootActivity.setScreenTitle(getString(R.string.app_name));
        materialdesignicons_font = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
        TextView download = (TextView) view.findViewById(R.id.download);
        download.setTypeface(VodafoneExB);

        downloadingRecyclerView = (RecyclerView) view.findViewById(R.id.downloadingRecyclerView);
        downloadingRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        downloadingRecyclerView.setHasFixedSize(true);
        //downloadingRecyclerView.setNestedScrollingEnabled(false);


//        serviceIntent = new Intent(context, BindService.class);
//        context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
//        context.startService(serviceIntent);

        registerReceiver();
        DbHelper dbHelper = new DbHelper(getActivity());
//        List<Data> data_item = dbHelper.getAllDownloadingMediaFile();


        Gson gson = new Gson();
        if (resourcesToDownloadList != null) {
            uniqueResourcesToDownload = getResourcesToDownload(resourcesToDownloadList);
            for (Data media : uniqueResourcesToDownload) {
                Intent intent = new Intent(context, DownloadService1.class);
                String strJsonmedia = gson.toJson(media);
                intent.putExtra(Consts.EXTRA_MEDIA, strJsonmedia);
                intent.putExtra(Consts.EXTRA_URLPropertyForDownload, Consts.DOWNLOAD_URL);
                context.startService(intent);
                Data downlaodingMedia = new Data();
                if (media.getMediaId() != 0) {
                    downlaodingMedia.setId(media.getMediaId());
                } else {
                    downlaodingMedia.setId(media.getId());
                }
                downlaodingMedia.setParent_id(parentId);
                dbHelper.upsertDownloadingFileEntity(downlaodingMedia);
            }
        }
        List<Data> dataList = dbHelper.getAllDownloadingMediaFile(Utility.getLanguageIdFromSharedPreferences(context));
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "**** media dataList: " + dataList);
        }
        downloadMediaWithParentList = new ArrayList<DownloadMediaWithParent>();
        downloadMediaWithParentList.addAll(GetDownloadMediaWithParent(dataList));

        if (downloadMediaWithParentList != null && downloadMediaWithParentList.size() > 0) {
            downloadingAdapter = new DownloadingAdapter(context, downloadMediaWithParentList);
            downloadingRecyclerView.setAdapter(downloadingAdapter);
        } else {
            view = view.inflate(context, R.layout.no_record_found_fragment, null);
            TextView noRecordIcon = (TextView) view.findViewById(R.id.noRecordIcon);
            noRecordIcon.setTypeface(materialdesignicons_font);
            noRecordIcon.setText(Html.fromHtml("&#xf187;"));
        }
    }

    private List<DownloadMediaWithParent> GetDownloadMediaWithParent(List<Data> dataList) {
        List<DownloadMediaWithParent> result = new ArrayList<DownloadMediaWithParent>();
        for (Data downloadingMedia : dataList) {
            DownloadMediaWithParent ob = determineIfParentExistsInList(downloadingMedia.getParent_id(), result);
            Boolean isNew = false;
            if (ob == null) {
                ob = new DownloadMediaWithParent();
                ob.setParentId(downloadingMedia.getParent_id());
                isNew = true;
            }

            List<Data> temp = ob.getStrJsonResourcesToDownloadList();
            if (temp == null) {
                temp = new ArrayList<Data>();
            }
            temp.add(downloadingMedia);
            ob.setStrJsonResourcesToDownloadList(temp);
            if (isNew) {
                result.add(ob);
            }
        }

        return result;
    }

    private DownloadMediaWithParent determineIfParentExistsInList(int parentId, List<DownloadMediaWithParent> list) {

        for (DownloadMediaWithParent downloadMediaWithParent : list) {

            if (downloadMediaWithParent.getParentId() == parentId) {

                return downloadMediaWithParent;
            }
        }
        return null;
    }

    private List<Data> getResourcesToDownload(List<Data> inputList) {
        List<Data> outputList = new ArrayList<Data>();
        for (Data media : inputList) {
            if (media.getLocalFilePath() == null) {
                if (!isItemAlreadyExists(media, outputList)) {
                    outputList.add(media);
                }
            }
        }
        return outputList;
    }

    private Boolean isItemAlreadyExists(Data item, List<Data> inputList) {
        for (Data media : inputList) {
            if (media.getId() == item.getId()) {
                return true;
            }
        }
        return false;
    }

    private void registerReceiver() {

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this.getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Consts.MESSAGE_PROGRESS);
        intentFilter.addAction(Consts.MESSAGE_CANCEL_DOWNLOAD);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Consts.IS_DEBUG_LOG) {
                //Log.d(Consts.LOG_TAG, "**** received message in Downloadingfragment: " + intent.getAction());
            }
            if (intent.getAction().equals(Consts.MESSAGE_CANCEL_DOWNLOAD)) {

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


                /*Intent intent1 = new Intent(context, DownloadService1.class);
                String strJsonmedia = gson.toJson(strJsonMedia);
                intent1.putExtra(Consts.EXTRA_MEDIA, strJsonmedia);
                intent1.putExtra(Consts.EXTRA_URLPropertyForDownload, Consts.DOWNLOAD_URL);*/


                context.stopService(intent);
                // Toast.makeText(context, getString(R.string.downloading_cancel), Toast.LENGTH_LONG).show();
                unregisterReceiver();

                //String strJsonMedia = intent.getExtras().getString(Consts.EXTRA_MEDIA);
                int parentId = intent.getExtras().getInt("parentId", 0);
                //Gson gson = new Gson();
                //Data selectedMedia = gson.fromJson(strJsonMedia, Data.class);
                DbHelper dbhelper = new DbHelper(context);
                //List<Data> dataList1 = dbhelper.getAllDownloadingFileEntity();

                List<DownloadMediaWithParent> listWithoutDownload = new ArrayList<DownloadMediaWithParent>();
                if (parentId != 0) {
                    for (DownloadMediaWithParent media : downloadMediaWithParentList) {
                        if (media.getParentId() != parentId) {
                            listWithoutDownload.add(media);
                            if (Consts.IS_DEBUG_LOG) {
                                Log.d(Consts.LOG_TAG, "**** media Id: " + media.getParentId() + " dl cancelled:");
                            }
                        } else {
                            Boolean deleteFlag = dbhelper.deleteFileDownloadedByParentId(parentId);
                            if (deleteFlag) {
                                if (Consts.IS_DEBUG_LOG) {
                                    Log.d(Consts.LOG_TAG, "**** Delete " + parentId);
                                }
                            }

                        }

                    }
                    //List<Data> dataList2 = dbhelper.getAllDownloadingFileEntity();
                    DownloadService1.shouldContinue = false;
                    downloadMediaWithParentList = listWithoutDownload;
                    downloadingAdapter = new DownloadingAdapter(context, downloadMediaWithParentList);
                    downloadingRecyclerView.setAdapter(downloadingAdapter);
                    downloadingAdapter.notifyDataSetChanged();
                }
            } // cancel download

            if (intent.getAction().equals(Consts.MESSAGE_PROGRESS)) {
                if (DownloadService1.shouldContinue == false) {
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "Do not update % progress as DownloadService1.shouldContinue: " + DownloadService1.shouldContinue);
                    }
                    unregisterReceiver();
                    return;
                } else {
                    DownloadData download = intent.getParcelableExtra(Consts.EXTRA_DOWNLOAD_DATA);
                    if (download != null) {
                        if (downloadMediaWithParentList != null && downloadMediaWithParentList.size() > 0) {
                            for (DownloadMediaWithParent media : downloadMediaWithParentList) {
                                for (Data data : media.getStrJsonResourcesToDownloadList()) {
                                    if (data.getId() == download.getId()) {
                                        data.setProgress(download.getProgress());
                                        if (Consts.IS_DEBUG_LOG) {
                                            Log.d(Consts.LOG_TAG, "**** media Id: " + download.getId() + " progress:" + download.getProgress() + "%");
                                        }
                                    }
                                }
                            }
                            downloadingRecyclerView.setAdapter(downloadingAdapter);
                            downloadingAdapter.notifyDataSetChanged();
                        }
                    }
                }

//                if(download.getProgress() == 100){
//
//                    mProgressText.setText("File Download Complete");
//
//                } else {
//
//                    mProgressText.setText(String.format("Downloaded (%d/%d) MB",download.getCurrentFileSize(),download.getTotalFileSize()));
//
//                }
            }
        }
    };

    private Data getDataFromResourcesToDownloadList(int id) {

        return null;
    }


   /* BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Consts.mBroadcastDeleteAction)) {
                //LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                context.stopService(intent);
                Toast.makeText(context, getString(R.string.downloading_cancel), Toast.LENGTH_LONG).show();
            }
            if (intent.getAction().equals(Consts.mBroadcastProgressUpdateAction)) {
                int pro = intent.getIntExtra("progresss", 0);

                DbHelper dbHelper = new DbHelper(getActivity());
                List<Data> data_item = dbHelper.getAllDownloadingMediaFile(Utility.getLanguageIdFromSharedPreferences(context));

                downloadingAdapter = new DownloadingAdapter(context, data_item);
                downloadingRecyclerView.setAdapter(downloadingAdapter);
                downloadingAdapter.notifyDataSetChanged();

                // Toast.makeText(context, "progress  update= " + pro, Toast.LENGTH_LONG).show();
            }
        }
    };
*/
   /* private void registerReceiver() {
        unregisterReceiver();
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter
                .addAction(Consts.mBroadcastProgressUpdateAction);
      intentToReceiveFilter
                .addAction(Consts.mBroadcastDeleteAction);
       LocalBroadcastManager.getInstance(getActivity()).registerReceiver(intentReceiver, intentToReceiveFilter);
        mReceiversRegistered = true;
  }*/

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
       /* if (mReceiversRegistered) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(intentReceiver);
            mReceiversRegistered = false;
        }*/
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

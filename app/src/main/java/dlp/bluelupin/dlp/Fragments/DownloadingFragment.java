package dlp.bluelupin.dlp.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import dlp.bluelupin.dlp.Activities.DownloadingBroadcastReceiver;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DownloadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadingFragment extends Fragment implements View.OnClickListener{
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
    private TextView cancelIcon, cancel, uploadMsg, description;
    private ProgressBar mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_downloading, container, false);

        context = getActivity();
        init(view);
        return view;
    }

    private void init(View view) {
        MainActivity rootActivity = (MainActivity) getActivity();
        rootActivity.setdownloadContainer(true);

        Typeface materialdesignicons_font = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneRg.ttf");
        cancelIcon = (TextView) view.findViewById(R.id.cancelIcon);
        cancelIcon.setTypeface(materialdesignicons_font);
        cancelIcon.setText(Html.fromHtml("&#xf156"));
        cancelIcon.setOnClickListener(this);
        cancel = (TextView) view.findViewById(R.id.cancel);
        uploadMsg = (TextView) view.findViewById(R.id.uploadMsg);
        uploadMsg.setTypeface(VodafoneExB);
        cancel.setTypeface(VodafoneExB);
        description = (TextView) view.findViewById(R.id.description);
        description.setTypeface(VodafoneRg);
        mProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        //mProgress.setProgress(25);   // Main Progress
        // mProgress.setSecondaryProgress(50); // Secondary Progress
        // mProgress.setMax(100); // Maximum Progress
        new ShowCustomProgressBarAsyncTask().execute();
        Intent intent = new Intent();
        intent.setAction(Consts.DownloadBroadcast);
        context.sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cancelIcon:
                MainActivity rootActivity = (MainActivity) getActivity();
                rootActivity.setdownloadContainer(false);
                break;
        }
    }

    public class ShowCustomProgressBarAsyncTask extends AsyncTask<Void, Integer, Void> {
        int myProgress;

        @Override
        protected void onPostExecute(Void result) {
           /* textview.setText("Finish work with custom ProgressBar");
            button1.setEnabled(true);
            progressBar2.setVisibility(View.INVISIBLE);*/
        }

        @Override
        protected void onPreExecute() {
            myProgress = 0;
            //progressBar.setSecondaryProgress(0);
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (myProgress < 100) {
                myProgress++;
                publishProgress(myProgress);
                SystemClock.sleep(100);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgress.setProgress(values[0]);
            mProgress.setSecondaryProgress(values[0] + 5);
        }
    }
}

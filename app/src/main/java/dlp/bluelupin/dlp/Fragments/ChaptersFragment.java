package dlp.bluelupin.dlp.Fragments;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.List;

import dlp.bluelupin.dlp.Activities.DownloadService;
import dlp.bluelupin.dlp.Adapters.ChaptersAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChaptersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChaptersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChaptersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int parentId;
    private String type;

    private OnFragmentInteractionListener mListener;

    public ChaptersFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ChaptersFragment newInstance(Integer parentId, String type) {
        ChaptersFragment fragment = new ChaptersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, parentId);
        args.putString(ARG_PARAM2, type);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView chapterTitle;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parentId = getArguments().getInt(ARG_PARAM1);
            type = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;
    ViewGroup newcontainer;
    LayoutInflater newinflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chapters, container, false);
        newcontainer = container;
        newinflater = inflater;
        context = getActivity();
        if (Utility.isTablet(context)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        init();
        return view;
    }

    private void init() {
        MainActivity rootActivity = (MainActivity) getActivity();
        rootActivity.setScreenTitle(type);

        Typeface custom_fontawesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface materialdesignicons_font = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneRg.ttf");
        chapterTitle = (TextView) view.findViewById(R.id.chapterTitle);
        chapterTitle.setTypeface(VodafoneExB);

        chapterTitle.setText(type);

        DbHelper db = new DbHelper(context);
        List<Data> dataList = db.getDataEntityByParentIdAndType(parentId, type);
        if (dataList.size() == 0) {
            view = view.inflate(context, R.layout.no_record_found_fragment, null);
            TextView noRecordIcon = (TextView) view.findViewById(R.id.noRecordIcon);
            TextView back = (TextView) view.findViewById(R.id.back);
            noRecordIcon.setTypeface(materialdesignicons_font);
            noRecordIcon.setText(Html.fromHtml("&#xf187;"));
        } else {
            ChaptersAdapter chaptersAdapter = new ChaptersAdapter(context, dataList, type);
            RecyclerView chaptersRecyclerView = (RecyclerView) view.findViewById(R.id.chaptersRecyclerView);
            chaptersRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            chaptersRecyclerView.setHasFixedSize(true);
            //chaptersRecyclerView.setNestedScrollingEnabled(false);
            chaptersRecyclerView.setAdapter(chaptersAdapter);
        }
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "Chapter Fragment: data count: " + dataList.size());
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            // mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}

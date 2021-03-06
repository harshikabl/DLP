package dlp.bluelupin.dlp.Fragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Adapters.ChaptersAdapter;
import dlp.bluelupin.dlp.Adapters.ContentRecycleAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int parentId;
    private String contentTitle;


    public ContentFragment() {
        // Required empty public constructor
    }


    public static ContentFragment newInstance(int parentId, String contentTitle) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, parentId);
        args.putString(ARG_PARAM2, contentTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parentId = getArguments().getInt(ARG_PARAM1);
            contentTitle = getArguments().getString(ARG_PARAM2);
        }
    }

    private Context context;
    private TextView content_title;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_content, container, false);
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
        rootActivity.setScreenTitle(contentTitle);
        //rootActivity.hideSplashImage(true);
        rootActivity.setShowQuestionIconOption(false);
        Typeface custom_fontawesome = FontManager.getFontTypeface(context, "fonts/fontawesome-webfont.ttf");
        Typeface materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = FontManager.getFontTypeface(context, "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
//        content_title = (TextView) view.findViewById(R.id.content_title);
//        content_title.setTypeface(VodafoneExB);


        DbHelper db = new DbHelper(context);
        List<Data> dataList = db.getDataEntityByParentId(parentId);
        if (dataList.size() == 0) {
            view = view.inflate(context, R.layout.no_record_found_fragment, null);
            TextView noRecordIcon = (TextView) view.findViewById(R.id.noRecordIcon);
            noRecordIcon.setTypeface(materialdesignicons_font);
            noRecordIcon.setText(Html.fromHtml("&#xf187;"));
        } else {

            //ContentAdapter contentAdapter = new ContentAdapter(context, dataList);
            ContentRecycleAdapter contentAdapter = new ContentRecycleAdapter(context, dataList);
            contentAdapter.setHasStableIds(true);
            //ListView listView = (ListView) view.findViewById(R.id.listView);

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.content_recycler_view);
            recyclerView.setAdapter(contentAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            //listView.setAdapter(contentAdapter);
        }
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "Content Fragment: data_item count: " + dataList.size());
        }


    }

}

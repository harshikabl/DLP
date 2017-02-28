package dlp.bluelupin.dlp.Fragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Adapters.ShowDownloadedFileAdapter;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.DownloadMediaWithParent;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowDownloadedMediaFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowDownloadedMediaFileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ShowDownloadedMediaFileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowDownloadedMediaFileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowDownloadedMediaFileFragment newInstance(String param1, String param2) {
        ShowDownloadedMediaFileFragment fragment = new ShowDownloadedMediaFileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ShowDownloadedMediaFileFragment newInstance(String param1) {
        ShowDownloadedMediaFileFragment fragment = new ShowDownloadedMediaFileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
    View view;
    RecyclerView downloadedRecyclerView;
    Typeface materialdesignicons_font;
    private ShowDownloadedFileAdapter ShowFileAdaapter;
    List<DownloadMediaWithParent> downloadMediaWithParentList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_downloaded_media_file, container, false);

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

        materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = FontManager.getFontTypeface(context, "fonts/VodafoneExB.TTF");

        downloadedRecyclerView = (RecyclerView) view.findViewById(R.id.downloadedRecyclerView);
        downloadedRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        downloadedRecyclerView.setHasFixedSize(true);

        DbHelper dbHelper = new DbHelper(context);
        List<Data> dataList = dbHelper.getAllDownloadedMediaFile(Utility.getLanguageIdFromSharedPreferences(context));
        downloadMediaWithParentList = new ArrayList<DownloadMediaWithParent>();
        downloadMediaWithParentList.addAll(GetDownloadMediaWithParent(dataList));
        if (downloadMediaWithParentList != null && downloadMediaWithParentList.size() > 0) {
            ShowFileAdaapter = new ShowDownloadedFileAdapter(context, downloadMediaWithParentList);
            downloadedRecyclerView.setAdapter(ShowFileAdaapter);
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
            DbHelper dbHelper = new DbHelper(context);
            /*Data media = dbHelper.getMediaEntityById(downloadingMedia.getId());
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "**** media filepath: " + media.getFile_path());
            }*/
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
}

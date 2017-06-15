package dlp.bluelupin.dlp.Fragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dlp.bluelupin.dlp.Adapters.FavoritesListAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.FavoritesData;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FavoritesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FavoritesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesListFragment newInstance(String param1) {
        FavoritesListFragment fragment = new FavoritesListFragment();
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
        }
    }

    private Context context;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorites_list, container, false);
        context = getActivity();
        if (Utility.isTablet(context)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        init();
        return view;
    }

    public void init() {
        MainActivity rootActivity = (MainActivity) getActivity();
        rootActivity.setScreenTitle(context.getString(R.string.Favorites));
        rootActivity.setShowQuestionIconOption(false);
        Typeface materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");

        DbHelper dbHelper = new DbHelper(context);
        List<FavoritesData> favoritesData = dbHelper.getFavoritesChaptersAndTopicListData(mParam1);
        if (favoritesData.size() == 0) {
            view = view.inflate(context, R.layout.no_record_found_fragment, null);
            TextView noRecordIcon = (TextView) view.findViewById(R.id.noRecordIcon);
            noRecordIcon.setTypeface(materialdesignicons_font);
            noRecordIcon.setText(Html.fromHtml("&#xf187;"));
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, " No fave ");
            }
        } else {
            //Log.d(Consts.LOG_TAG, " yes fave ");
            FavoritesListAdapter favoritesAdapter = new FavoritesListAdapter(context, favoritesData);
            RecyclerView favoritesRecyclerView = (RecyclerView) view.findViewById(R.id.favoritesRecyclerView);
            favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            favoritesRecyclerView.setHasFixedSize(true);
            //favoritesRecyclerView.setNestedScrollingEnabled(false);
            favoritesRecyclerView.setAdapter(favoritesAdapter);
        }
    }
}

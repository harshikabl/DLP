package dlp.bluelupin.dlp.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Adapters.ChaptersAdapter;
import dlp.bluelupin.dlp.Adapters.FavoritesListAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.R;

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
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesListFragment newInstance(String param1, String param2) {
        FavoritesListFragment fragment = new FavoritesListFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites_list, container, false);
        context = getActivity();
        init(view);
        return view;
    }

    public void init(View view) {
        MainActivity rootActivity = (MainActivity) getActivity();
        rootActivity.setScreenTitle("Favorites");
        List<String> list = new ArrayList<String>();
        list.add("English");
        list.add("Hindi");
        list.add("Tamil");
        list.add("English");
        list.add("Hindi");
        list.add("Tamil");
        FavoritesListAdapter chaptersAdapter = new FavoritesListAdapter(context, list);
        RecyclerView chaptersRecyclerView = (RecyclerView) view.findViewById(R.id.favoritesRecyclerView);
        chaptersRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        chaptersRecyclerView.setHasFixedSize(true);
        //chaptersRecyclerView.setNestedScrollingEnabled(false);
        chaptersRecyclerView.setAdapter(chaptersAdapter);
    }
}

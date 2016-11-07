package dlp.bluelupin.dlp.Fragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dlp.bluelupin.dlp.Adapters.FavoritesPagerAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
    private ViewPager viewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favotites, container, false);
        context = getActivity();

        if (Utility.isTablet(context)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        init(view);
        return view;
    }

    public void init(View view) {
        //TabHost mTabHost = getTabHost();
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        //Add tabs icon with setIcon() or simple text with .setText()
        //tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.checkbox).setText(context.getString(R.string.chapters)));
        // tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.hihlogo).setText(context.getString(R.string.topic)));
        //setUpCustomView(tabLayout);
    }

   /* private void setUpCustomView(TabLayout tabLayout) {
        View tabOne = (View) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        TextView one = (TextView) tabOne.findViewById(R.id.text);
        TextView icon1 = (TextView) tabOne.findViewById(R.id.icon);
        one.setText("CHAPTERS");
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        View tabTwo = (View) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        TextView two = (TextView) tabTwo.findViewById(R.id.text);
        TextView icon2 = (TextView) tabTwo.findViewById(R.id.icon);
        two.setText("TOPICS");

        tabLayout.getTabAt(0).setCustomView(tabOne);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

      *//*  //Add fragments
        PagerAdapter adapter = new PagerAdapter(context);
        adapter.addFragment(new Home());
        adapter.addFragment(new Profile());
        adapter.addFragment(new Setting());*//*
    }
*/
    private void setupViewPager(ViewPager viewPager) {
        FavoritesPagerAdapter adapter = new FavoritesPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFrag(new FavoritesListFragment().newInstance(Consts.CHAPTER), context.getString(R.string.chapters));
        adapter.addFrag(new FavoritesListFragment().newInstance(Consts.TOPIC), context.getString(R.string.topic));
        viewPager.setAdapter(adapter);
    }
}

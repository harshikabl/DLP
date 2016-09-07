package dlp.bluelupin.dlp.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectLocationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public SelectLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectLocationFragment newInstance(String param1, String param2) {
        SelectLocationFragment fragment = new SelectLocationFragment();
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
    private int PICKFILE_REQUEST_CODE = 101;
    private TextView path;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_location, container, false);
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
        rootActivity.setScreenTitle("Select Location");

        Typeface VodafoneExB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(getActivity().getAssets(), "fonts/VodafoneRg.ttf");
        TextView defaultText = (TextView) view.findViewById(R.id.defaultText);
        path = (TextView) view.findViewById(R.id.path);
        TextView selectFolder = (TextView) view.findViewById(R.id.selectFolder);
        defaultText.setTypeface(VodafoneExB);
        path.setTypeface(VodafoneRg);
        selectFolder.setTypeface(VodafoneRg);
        selectFolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");

                startActivityForResult(Intent.createChooser(intent, "Select folder"), PICKFILE_REQUEST_CODE);
            }
        });
        String SDPath = Utility.getSelectFolderPathFromSharedPreferences(context);// get this location from sharedpreferance;
        if (SDPath != null) {
            path.setText(SDPath);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_REQUEST_CODE) {
            if (data != null) {
                String Fpath = data.getDataString();
                if (Fpath != null) {
                    Utility.setSelectFolderPathIntoSharedPreferences(context, Fpath);
                    path.setText(Fpath);
                    Toast.makeText(context, Fpath, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}

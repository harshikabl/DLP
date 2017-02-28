package dlp.bluelupin.dlp.Fragments;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.DirectoryChooserDialog;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;
import dlp.bluelupin.filedialog.FileChooserActivity;

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
    private String m_chosenDir = "";
    private boolean m_newFolderEnabled = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_location, container, false);
        context = getActivity();
        if (Utility.isTablet(context)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        init(view);
        return view;
    }

    private void init(View view) {
        MainActivity rootActivity = (MainActivity) getActivity();
        rootActivity.setScreenTitle(getString(R.string.select_location));

        Typeface VodafoneExB = FontManager.getFontTypeface(context, "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        TextView defaultText = (TextView) view.findViewById(R.id.defaultText);
        path = (TextView) view.findViewById(R.id.path);
        TextView selectFolder = (TextView) view.findViewById(R.id.selectFolder);
        defaultText.setTypeface(VodafoneExB);
        path.setTypeface(VodafoneRg);
        selectFolder.setTypeface(VodafoneRg);
        selectFolder.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLocationFragment.this.getActivity(), FileChooserActivity.class);
                intent.putExtra(FileChooserActivity.INPUT_FOLDER_MODE, true);
                startActivityForResult(intent, 0);


//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                //File file = new File(String.valueOf(Environment.getExternalStorageDirectory()));
//                //intent.setDataAndType(Uri.fromFile(file), "*/*");
//                intent.setType("*/*");
//                startActivityForResult(Intent.createChooser(intent, "Select folder"), PICKFILE_REQUEST_CODE);
//              /*  DirectoryChooserDialog directoryChooserDialog =
//                        new DirectoryChooserDialog(context,
//                                new DirectoryChooserDialog.ChosenDirectoryListener() {
//                                    @Override
//                                    public void onChosenDir(String chosenDir) {
//                                        m_chosenDir = chosenDir;
//                                        Utility.setSelectFolderPathIntoSharedPreferences(context, chosenDir);
//                                        path.setText(chosenDir);
//                                        Toast.makeText(context, "Chosen directory: " + chosenDir, Toast.LENGTH_LONG).show();
//                                    }
//                                });*/
//                //directoryChooserDialog.chooseDirectory(m_chosenDir);
                m_newFolderEnabled = !m_newFolderEnabled;


            }
        });
        String SDPath = Utility.getSelectFolderPathFromSharedPreferences(context);// get this location from sharedpreferance;
        if (SDPath != null) {
            path.setText(SDPath);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String filePath = "";

            Bundle bundle = data.getExtras();
            if(bundle != null)
            {
                File file = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                filePath = file.getAbsolutePath();
            }
            String message = "selected: " + filePath;
            Toast toast = Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG);
            toast.show();
            Utility.setSelectFolderPathIntoSharedPreferences(context, filePath);
            path.setText(filePath);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICKFILE_REQUEST_CODE) {
//            if (data != null) {
//                String Fpath = data.getDataString();
//                File fileDirectory = new File(Fpath);
//                String absolutePath = fileDirectory.getAbsolutePath();
//                // String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//                //String main = baseDir + "/samVaad";
//                String[] parts = absolutePath.split("samVaad.txt");
//                String strPart1 = parts[0];
//                String finalPath = "";
//                if (strPart1.contains("/file:")) {
//                    String[] strArray = strPart1.split("/file:");
//                    finalPath = strArray[1];
//                }
//                if (Consts.IS_DEBUG_LOG) {
//                    Log.d(Consts.LOG_TAG, "You selected path " + finalPath);
//                }
//                if (finalPath != null) {
//                    Utility.setSelectFolderPathIntoSharedPreferences(context, finalPath);
//                    path.setText(finalPath);
//                    //Toast.makeText(context, Fpath, Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//
//
//    }

}

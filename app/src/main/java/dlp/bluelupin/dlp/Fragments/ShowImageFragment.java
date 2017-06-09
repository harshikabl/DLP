package dlp.bluelupin.dlp.Fragments;


import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.FontManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String imageUrl;
    private String mParam2;


    public ShowImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowImageFragment newInstance(String param1, String param2) {
        ShowImageFragment fragment = new ShowImageFragment();
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
            imageUrl = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_image, container, false);
        context = getActivity();
        init();
        return view;
    }

    private void init() {
        Typeface materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        TextView closeIcon = (TextView) view.findViewById(R.id.closeIcon);
        closeIcon.setTypeface(materialdesignicons_font);
        closeIcon.setText(Html.fromHtml("&#xf156;"));
        TextView close = (TextView) view.findViewById(R.id.close);
        close.setTypeface(VodafoneRg);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        LinearLayout closeLayout = (LinearLayout) view.findViewById(R.id.closeLayout);
        if (imageUrl != null && !image.equals("")) {
            Uri uri = Uri.fromFile(new File(imageUrl));
            if (uri != null) {
                Picasso.with(context).load(uri).into(image);
            }
        }
        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
            }
        });
    }
}

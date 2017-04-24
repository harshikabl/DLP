package dlp.bluelupin.dlp.Fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dlp.bluelupin.dlp.Adapters.ChaptersAdapter;
import dlp.bluelupin.dlp.Adapters.QuizAnswerAdapter;
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
 * {@link QuizTextFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuizTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizTextFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
  List<Data> answer_list;
    public QuizTextFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizTextFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizTextFragment newInstance(String param1, String param2) {
        QuizTextFragment fragment = new QuizTextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView quit_text, quit_icon, skip_text, skip_icon, multiple_text, question, question_no1,question_no2, listen_text, listen_icon, question_title, select, submit_text, submit_Icon;
    private Context context;
    Typeface materialdesignicons_font;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_quiz_text, container, false);
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
        materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = FontManager.getFontTypeface(context, "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        Typeface VodafoneRgBd = FontManager.getFontTypeface(context, "fonts/VodafoneRgBd.ttf");
        Typeface VodafoneLt = FontManager.getFontTypeface(context, "fonts/VodafoneLt.ttf");
        quit_text = (TextView) view.findViewById(R.id.quit_text);
        skip_text = (TextView) view.findViewById(R.id.skip_text);
        multiple_text = (TextView) view.findViewById(R.id.multiple_text);
        question = (TextView) view.findViewById(R.id.question);
        question_no1 = (TextView) view.findViewById(R.id.question_no1);
        question_no2 = (TextView) view.findViewById(R.id.question_no2);
        listen_text = (TextView) view.findViewById(R.id.listen_text);
        listen_icon = (TextView) view.findViewById(R.id.listen_icon);
        question_title = (TextView) view.findViewById(R.id.question_title);
        select = (TextView) view.findViewById(R.id.select);
        submit_text = (TextView) view.findViewById(R.id.submit_text);
        submit_Icon = (TextView) view.findViewById(R.id.submit_Icon);
        quit_text.setTypeface(VodafoneExB);
        skip_text.setTypeface(VodafoneExB);
        multiple_text.setTypeface(VodafoneLt);
        question.setTypeface(VodafoneRg);
        question_no2.setTypeface(VodafoneRg);
        question_no1.setTypeface(VodafoneRgBd);
        listen_text.setTypeface(VodafoneRg);
        question_title.setTypeface(VodafoneRgBd);
        select.setTypeface(VodafoneRg);
        quit_icon = (TextView) view.findViewById(R.id.quit_icon);
        quit_icon.setTypeface(materialdesignicons_font);
        quit_icon.setText(Html.fromHtml("&#xf425;"));
        skip_icon = (TextView) view.findViewById(R.id.skip_icon);
        skip_icon.setTypeface(materialdesignicons_font);
        skip_icon.setText(Html.fromHtml("&#xf4ad;"));
        listen_icon.setTypeface(materialdesignicons_font);
        listen_icon.setText(Html.fromHtml("&#xf57e;"));
        submit_Icon.setTypeface(materialdesignicons_font);
        submit_Icon.setText(Html.fromHtml("&#xf054;"));
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.quizRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        QuizAnswerAdapter adapter = new QuizAnswerAdapter(getActivity(), answer_list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    /*//alert for error message
    public void alertForErrorMessage(String errorMessage, Context mContext) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        Typeface VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        Typeface VodafoneExB = Typeface.createFromAsset(mContext.getAssets(), "fonts/VodafoneExB.TTF");
        final AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.alertAnimation;
        View view = alert.getLayoutInflater().inflate(R.layout.quiz_quit_alert, null);
        TextView title1 = (TextView) view.findViewById(R.id.title1);
        title1.setTypeface(VodafoneRg);
        TextView title2 = (TextView) view.findViewById(R.id.title2);
        title2.setTypeface(VodafoneRg);
        TextView quiz_text = (TextView) view.findViewById(R.id.quit_text);
        quiz_text.setTypeface(VodafoneRg);
        LinearLayout quit_layout = (LinearLayout) view.findViewById(R.id.quit_layout);
        title1.setText(errorMessage);
        alert.setCustomTitle(view);
        quit_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
    }*/
}

package dlp.bluelupin.dlp.Fragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.QuizAnswer;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link QuizResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizResultFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int quizId, contentId;
    private int totalQuestion;


    public QuizResultFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static QuizResultFragment newInstance(int quizId, int totalQuestion, int contentId) {
        QuizResultFragment fragment = new QuizResultFragment();
        Bundle args = new Bundle();
        args.putInt("quizId", quizId);
        args.putInt("totalQuestion", totalQuestion);
        args.putInt("contentId", contentId);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView trophyIcon, score_text, restart_icon, restart_text, quit_text, quit_icon;
    private Context context;
    Typeface materialdesignicons_font;
    private Handler handler = new Handler();
    TextView out;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            totalQuestion = getArguments().getInt("totalQuestion");
            quizId = getArguments().getInt("quizId");
            contentId = getArguments().getInt("contentId");
        }
    }

    View view;
    private List<QuizAnswer> correctAnsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_quiz_result, container, false);
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
        trophyIcon = (TextView) view.findViewById(R.id.trophyIcon);
        score_text = (TextView) view.findViewById(R.id.score_text);
        out = (TextView) view.findViewById(R.id.out);
        restart_icon = (TextView) view.findViewById(R.id.restart_icon);
        restart_text = (TextView) view.findViewById(R.id.restart_text);
        quit_text = (TextView) view.findViewById(R.id.quit_text);
        quit_icon = (TextView) view.findViewById(R.id.quit_icon);
        quit_text.setTypeface(VodafoneExB);
        restart_text.setTypeface(VodafoneExB);
        score_text.setTypeface(VodafoneLt);
        out.setTypeface(VodafoneRg);
        trophyIcon.setTypeface(materialdesignicons_font);
        trophyIcon.setText(Html.fromHtml("&#xf53a;"));
        quit_icon.setTypeface(materialdesignicons_font);
        quit_icon.setText(Html.fromHtml("&#xf054;"));
        restart_icon.setTypeface(materialdesignicons_font);
        restart_icon.setText(Html.fromHtml("&#xf459;"));
        LinearLayout restart_layout = (LinearLayout) view.findViewById(R.id.restart_layout);
        restart_layout.setOnClickListener(this);
        LinearLayout exitLayout = (LinearLayout) view.findViewById(R.id.exitLayout);
        exitLayout.setOnClickListener(this);
        DbHelper dbhelper = new DbHelper(context);
        correctAnsList = dbhelper.getAllQuizAnswerEntity(quizId, contentId);

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.progress_bar_circle);
        final ProgressBar mProgress = (ProgressBar) view.findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(totalQuestion); // Secondary Progress
        mProgress.setMax(totalQuestion); // Maximum Progress
        mProgress.setProgressDrawable(drawable);
        out = (TextView) view.findViewById(R.id.out);
        mProgress.setProgress(correctAnsList.size());
        out.setText(correctAnsList.size() + " OUT OF " + totalQuestion);

       /* new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (correctAnsList.size() < totalQuestion) {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mProgress.setProgress(correctAnsList.size());
                            out.setText(correctAnsList.size() + " OUT OF " + totalQuestion);

                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(20); //thread will take approx 3 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
*/

    }


    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        switch (v.getId()) {
            case R.id.restart_layout:
                QuizQuestionFragment fragment = QuizQuestionFragment.newInstance(quizId, contentId);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right).replace(R.id.container, fragment)
                        //.addToBackStack(null)
                        .commitAllowingStateLoss();
                break;
            case R.id.exitLayout:
                fragmentManager.popBackStack();
                break;
        }
    }
}

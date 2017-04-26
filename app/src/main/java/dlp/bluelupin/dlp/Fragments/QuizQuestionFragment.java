package dlp.bluelupin.dlp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Adapters.QuizQuestionAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.QuizAnswer;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;


public class QuizQuestionFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int quizId;
    private String mParam2;
    List<Data> question_list;
    private static RecyclerView recyclerView;


    public QuizQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizQuestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizQuestionFragment newInstance(int quizId, String param2) {
        QuizQuestionFragment fragment = new QuizQuestionFragment();
        Bundle args = new Bundle();
        args.putInt("QuizId", quizId);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView quit_text, quit_icon, skip_text, skip_icon, multiple_text, question, question_no;
    private TextView totalQuestion, listen_text, listen_icon, question_title, select, submit_text, submit_Icon;
    private Context context;
    Typeface materialdesignicons_font, VodafoneRg;
    private int questionNo = 1;
    private int questionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quizId = getArguments().getInt("QuizId");
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_quiz_question, container, false);
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
        VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        Typeface VodafoneRgBd = FontManager.getFontTypeface(context, "fonts/VodafoneRgBd.ttf");
        Typeface VodafoneLt = FontManager.getFontTypeface(context, "fonts/VodafoneLt.ttf");
        quit_text = (TextView) view.findViewById(R.id.quit_text);
        quit_text.setOnClickListener(this);
        skip_text = (TextView) view.findViewById(R.id.skip_text);
        skip_text.setOnClickListener(this);
        multiple_text = (TextView) view.findViewById(R.id.multiple_text);
        question = (TextView) view.findViewById(R.id.question);
        question_no = (TextView) view.findViewById(R.id.question_no);
        totalQuestion = (TextView) view.findViewById(R.id.totalQuestion);
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
        totalQuestion.setTypeface(VodafoneRg);
        question_no.setTypeface(VodafoneRgBd);
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
        recyclerView = (RecyclerView) view.findViewById(R.id.quizRecyclerView);
        LinearLayout submitLayout = (LinearLayout) view.findViewById(R.id.submitLayout);
        submitLayout.setOnClickListener(this);
        setValue();
    }

    //set value
    private void setValue() {
        List<String> OptionAtoZList = new ArrayList<String>();
        OptionAtoZList.add("A");
        OptionAtoZList.add("B");
        OptionAtoZList.add("C");
        OptionAtoZList.add("D");
        OptionAtoZList.add("E");
        OptionAtoZList.add("F");
        OptionAtoZList.add("G");
        OptionAtoZList.add("H");


        DbHelper dbHelper = new DbHelper(context);
        Data quizData = dbHelper.getQuizzesDataEntityById(quizId);
        if (quizData != null) {
            List<Data> questionList = dbHelper.getAllQuizzesQuestionsDataEntity(quizData.getId());
            totalQuestion.setText("/" + String.valueOf(questionList.size()));
            question_no.setText(String.valueOf(questionNo));
            Data questionData = dbHelper.getQuestionDetailsData(quizId, questionNo);
            if (questionData != null) {
                questionId = questionData.getId();
                question_title.setText(questionData.getLang_resource_description());
                List<Data> optionList = dbHelper.getAllQuestionsOptionsDataEntity(questionData.getId());
                if (optionList != null) {
                    // recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    QuizQuestionAdapter adapter = new QuizQuestionAdapter(getActivity(), optionList, OptionAtoZList, quizId, questionData.getId());
                    recyclerView.setAdapter(adapter);// set adapter on recyclerview
                    adapter.notifyDataSetChanged();// Notify the adapter
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitLayout:
                questionNo = questionNo + 1;
                DbHelper dbhelper = new DbHelper(context);
                QuizAnswer answer = new QuizAnswer();
                answer.setQuizId(quizId);
                answer.setQuestionId(questionId);
                SharedPreferences prefs = context.getSharedPreferences("OptionPreferences", Context.MODE_PRIVATE);
                if (prefs != null) {
                    int optionId = prefs.getInt("optionId", 0);
                    answer.setOptionId(optionId);
                }
                answer.setAnswer(1);
                boolean flage = dbhelper.upsertQuizAnswerEntity(answer);
                if (flage) {
                    setValue();
                }
                break;
            case R.id.skip_text:
                questionNo = questionNo + 1;
                setValue();
                break;
            case R.id.quit_text:
                alertForOuitMessage();
                break;
        }
    }

    //alert for Ouit message
    public void alertForOuitMessage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        alert.setCustomTitle(view);
        TextView quit_icon = (TextView) view.findViewById(R.id.quit_icon);
        quit_icon.setTypeface(materialdesignicons_font);
        quit_icon.setText(Html.fromHtml("&#xf425;"));
        quit_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });
        alert.show();
    }

}

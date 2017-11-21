package dlp.bluelupin.dlp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dlp.bluelupin.dlp.Activities.VideoPlayerActivity;
import dlp.bluelupin.dlp.Adapters.QuizQuestionAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.QuizAnswer;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.DownloadFileAsync;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;


public class QuizQuestionFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int quizId, contentId;
    List<Data> question_list;
    private static RecyclerView recyclerView;

    private boolean play;

    public QuizQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QuizQuestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizQuestionFragment newInstance(int quizId, int contentId) {
        QuizQuestionFragment fragment = new QuizQuestionFragment();
        Bundle args = new Bundle();
        args.putInt("QuizId", quizId);
        args.putInt("contentId", contentId);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView quit_text, quit_icon, skip_text, skip_icon, multiple_text, question, question_no;
    private TextView listen_text, listen_icon, question_title, select, submit_text, submit_Icon;
    private Context context;
    Typeface materialdesignicons_font, VodafoneRg;
    private int questionNo = 0;
    private int questionId;
    private List<Data> questionList;
    private Data media;
    private Data answerMedia;
    MainActivity rootActivity;
    private MediaPlayer mediaPlayer;
    CustomProgressDialog customProgressDialog;
    List<Data> optionList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quizId = getArguments().getInt("QuizId");
            contentId = getArguments().getInt("contentId");
        }
    }

    View view;
    private SharedPreferences shareprefs;

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
        rootActivity = (MainActivity) getActivity();
        rootActivity.setScreenTitle(context.getString(R.string.Quiz));
        rootActivity.setShowQuestionIconOption(true);
        DbHelper dbHelper = new DbHelper(context);
        Data quizData = dbHelper.getQuizzesDataEntityById(quizId);
        if (quizData != null) {
            question_list=getQuestionList(quizData.getId());
            //questionList = dbHelper.getAllQuizzesQuestionsDataEntity(quizData.getId());
            rootActivity.totalQuestion.setText("/" + String.valueOf(questionList.size()));
        }
        materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = FontManager.getFontTypeface(context, "fonts/VodafoneExB.TTF");
        VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        Typeface VodafoneRgBd = FontManager.getFontTypeface(context, "fonts/VodafoneRgBd.ttf");
        Typeface VodafoneLt = FontManager.getFontTypeface(context, "fonts/VodafoneLt.ttf");
        quit_text = (TextView) view.findViewById(R.id.quit_text);
        quit_text.setOnClickListener(this);
        skip_text = (TextView) view.findViewById(R.id.skip_text);
        skip_text.setOnClickListener(this);

        question = (TextView) view.findViewById(R.id.question);
        question_no = (TextView) view.findViewById(R.id.question_no);

        listen_text = (TextView) view.findViewById(R.id.listen_text);
        listen_icon = (TextView) view.findViewById(R.id.listen_icon);
        question_title = (TextView) view.findViewById(R.id.question_title);
        select = (TextView) view.findViewById(R.id.select);
        submit_text = (TextView) view.findViewById(R.id.submit_text);
        submit_Icon = (TextView) view.findViewById(R.id.submit_Icon);
        quit_text.setTypeface(VodafoneExB);
        skip_text.setTypeface(VodafoneExB);

        question.setTypeface(VodafoneRg);

        question_no.setTypeface(VodafoneRgBd);
        listen_text.setTypeface(VodafoneExB);
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
        LinearLayout listenLayout = (LinearLayout) view.findViewById(R.id.listenLayout);
        listenLayout.setOnClickListener(this);

        shareprefs = context.getSharedPreferences("OptionPreferences", Context.MODE_PRIVATE);
        setValue();
        customProgressDialog = new CustomProgressDialog(context, R.mipmap.syc);

    }

    //set value
    private void setValue() {
        if (questionList.size() == questionNo + 1) {
            submit_text.setText(context.getString(R.string.Submit));
        } else {
            submit_text.setText(context.getString(R.string.Next));
        }

        List<String> OptionAtoZList = new ArrayList<String>();
        OptionAtoZList.add("1");
        OptionAtoZList.add("2");
        OptionAtoZList.add("3");
        OptionAtoZList.add("4");
        OptionAtoZList.add("5");
        OptionAtoZList.add("6");
        OptionAtoZList.add("7");
        OptionAtoZList.add("8");

        DbHelper dbHelper = new DbHelper(context);
        question_no.setText(String.valueOf(questionNo + 1));
        rootActivity.question_no.setText(String.valueOf(questionNo + 1));
        if (questionList != null && questionList.size() > 0) {
            Data questionData = dbHelper.getQuestionDetailsData(quizId, questionList.get(questionNo).getId());
            if (questionData != null) {
                if (questionData.getAudio_media_id() != 0) {//get question media
                    media = dbHelper.getMediaEntityByIdAndLaunguageId(questionData.getAudio_media_id(),
                            Utility.getLanguageIdFromSharedPreferences(context));
                }
                if (questionData.getAnswer_audio_media_id() != 0) {//get answer media
                    answerMedia = dbHelper.getMediaEntityByIdAndLaunguageId(questionData.getAnswer_audio_media_id(),
                            Utility.getLanguageIdFromSharedPreferences(context));
                }
                String title = "";
                questionId = questionData.getId();
                Data descriptionResource = dbHelper.getResourceEntityByName(questionData.getLang_resource_description(),
                        Utility.getLanguageIdFromSharedPreferences(context));
                if (descriptionResource != null) {
                    question_title.setText(Html.fromHtml(descriptionResource.getContent()));
                    title = descriptionResource.getContent();
                }
                Data correct_answer_descriptionResource = dbHelper.getResourceEntityByName(questionData.getLang_resource_correct_answer_description(),
                        Utility.getLanguageIdFromSharedPreferences(context));
                String correct_answer_description = null;
                if (correct_answer_descriptionResource != null) {
                    correct_answer_description = correct_answer_descriptionResource.getContent();
                }
                optionList=getQuestionOptionList(questionData.getId());
                //optionList = dbHelper.getAllQuestionsOptionsDataEntity(questionData.getId());
                if (optionList != null) {
                    // recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    QuizQuestionAdapter adapter = new QuizQuestionAdapter(getActivity(), optionList, OptionAtoZList, questionNo, title, questionList.size(), answerMedia, correct_answer_description);
                    recyclerView.setAdapter(adapter);// set adapter on recyclerview
                    adapter.notifyDataSetChanged();// Notify the adapter
                }
            }
        }
    }


    private void playAudioAsync(final String url) {
        final String listenText = (String) listen_text.getText();

        listen_text.setText(context.getString(R.string.Buffering));
        new AsyncTask<String, Void, MediaPlayer>() {

            @Override
            protected MediaPlayer doInBackground(String... params) {
                if (mediaPlayer == null) {
                    Uri myUri = Uri.parse(url);
                    mediaPlayer = MediaPlayer.create(getActivity(), myUri);
                }
                return mediaPlayer;
            }

            @Override
            protected void onPostExecute(MediaPlayer mediaplayer) {
                super.onPostExecute(mediaplayer);
                if (url != null && !url.equals("")) {

                    try {
                        Uri myUri = Uri.parse(url);

                        if (mediaPlayer == null) {

                            mediaPlayer = MediaPlayer.create(context, myUri);

                        }
                        listen_text.setText(listenText);
                        if (mediaPlayer.isPlaying()) {//listen_text.getText() == "PAUSE") {
                            listen_icon.setText(Html.fromHtml("&#xf57e;"));
                            listen_text.setText(context.getString(R.string.Listen));//"LISTEN");
                            mediaPlayer.pause();

                        } else {


                            listen_icon.setText(Html.fromHtml("&#xf3e4;"));
                            listen_text.setText(context.getString(R.string.Pause));
                            mediaPlayer.start();


                        }
                    } catch (Exception e) {
                        Log.d(Consts.LOG_TAG, "**************: play audio " + e.getMessage());
                    }
                }
            }
        }.execute(url);
    }


    //downloading Audio File File
    private void downloadAudioFile(final Data mediaData) {
        // final CustomProgressDialog customProgressDialog = new CustomProgressDialog(context, R.mipmap.syc);
        if (Utility.isOnline(context)) {
            //customProgressDialog.show();

            DownloadFileAsync task = new DownloadFileAsync(context, mediaData) {
                @Override
                public void receiveData(String result) {
                    if (!result.equals("")) {
                        DbHelper dbHelper = new DbHelper(context);
                        mediaData.setLocalFilePath(result);
                        if (dbHelper.updateMediaLanguageLocalFilePathEntity(mediaData)) {
                            if (Consts.IS_DEBUG_LOG) {
                                Log.d(Consts.LOG_TAG, "successfully downloaded and local file updated: media Id:" + media.getId() + " downloading Url: " + media.getDownload_url() + " at " + result);
                            }
                        }
                    }
                    // customProgressDialog.dismiss();
                }
            };
            task.execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitLayout:
                Boolean selectCheck = false;
                if (shareprefs != null) {
                    selectCheck = shareprefs.getBoolean("selectCheck", false);
                }
                if (selectCheck) {
                    if (questionList.size() == questionNo + 1) {//check all question done or not
                        setAnswerForLastQuestion();
                    } else {
                        setAnswer();
                    }
                    resetMediaPlayer();
                } else {
                    Utility.alertForErrorMessage("Please select option", context);
                }
                break;
            case R.id.skip_text:
                if (questionList.size() == questionNo + 1) {//check all question done or not
                    setAnswerForLastQuestion();
                } else {
                    setAnswer();
                }
                resetMediaPlayer();
                break;
            case R.id.quit_text:
                alertForOuitMessage();
                break;
            case R.id.listenLayout:
                listenQuestionAudio();

                break;
        }
    }

    //reset MediaPlayer after select new question or skip question
    private void resetMediaPlayer() {
        if (mediaPlayer != null) {
            //mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            listen_icon.setText(Html.fromHtml("&#xf57e;"));
            listen_text.setText("Listen");
        }
    }

    //set answer into data base
    private void setAnswer() {
        DbHelper dbhelper = new DbHelper(context);
        questionNo = questionNo + 1;
        QuizAnswer answer = new QuizAnswer();
        answer.setQuizId(quizId);
        answer.setQuestionId(questionId);
        int optionId = shareprefs.getInt("optionId", 0);
        answer.setOptionId(optionId);
        int correctAns = shareprefs.getInt("correctAns", 0);
        answer.setAnswer(correctAns);
        answer.setContentId(contentId);
        boolean flage = dbhelper.upsertQuizAnswerEntity(answer);
        if (flage) {
            shareprefs.edit().clear().commit();//clear select OptionPreferences
            setValue();
        }
    }

    //set answer into data base for last question
    private void setAnswerForLastQuestion() {

        DbHelper dbhelper = new DbHelper(context);
        questionNo = questionNo + 1;
        QuizAnswer answer = new QuizAnswer();
        answer.setQuizId(quizId);
        answer.setQuestionId(questionId);
        int optionId = shareprefs.getInt("optionId", 0);
        answer.setOptionId(optionId);
        int correctAns = shareprefs.getInt("correctAns", 0);
        answer.setAnswer(correctAns);
        answer.setContentId(contentId);
        boolean flage = dbhelper.upsertQuizAnswerEntity(answer);
        if (flage) {
            shareprefs.edit().clear().commit();//clear select OptionPreferences
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            QuizResultFragment fragment = QuizResultFragment.newInstance(quizId, questionList.size(), contentId);
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right)
                    .replace(R.id.container, fragment)
                    //.addToBackStack(null)
                    .commit();
        }
    }

    //lesten question Audio
    private void listenQuestionAudio() {

        if (media != null) {

            if (media.getType().equals("Audio")) {
                String url = media.getLocalFilePath();
                if (url != null && !url.equals("")) {
                    playAudioAsync(url);
                } else {
                    if (Utility.isOnline(context)) {
                        downloadAudioFile(media);
                        url = media.getUrl();
                        playAudioAsync(url);
                    } else {
                        // "you are offline"
                    }
                }
            }

        }
    }


    //alert for Ouit message

    public void alertForOuitMessage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.alertAnimation;
        View view = alert.getLayoutInflater().inflate(R.layout.quiz_quit_alert, null);
        TextView title1 = (TextView) view.findViewById(R.id.title1);
        title1.setText(context.getString(R.string.quiz_quit_are));
        title1.setTypeface(VodafoneRg);
        TextView title2 = (TextView) view.findViewById(R.id.title2);
        title2.setText(context.getString(R.string.quiz_quit_progress));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (shareprefs != null) {
            shareprefs.edit().clear().commit();//clear select OptionPreferences
        }
    }


    private List<Data> getQuestionList(int quizId) {
        DbHelper dbHelper = new DbHelper(context);
        questionList = dbHelper.getAllQuizzesQuestionsDataEntity(quizId);
        if (questionList != null && questionList.size() > 0) {
            Collections.sort(questionList, new Comparator<Data>() {
            @Override
            public int compare(Data data1, Data data2) {
                return Integer.valueOf(data1.getSequence()).compareTo(Integer.valueOf(data2.getSequence()));
            }
        });}
        return questionList;
    }

    private List<Data> getQuestionOptionList(int questionId) {
        DbHelper dbHelper = new DbHelper(context);
        optionList = dbHelper.getAllQuestionsOptionsDataEntity(questionId);
        if (optionList != null && optionList.size() > 0) {
            Collections.sort(optionList, new Comparator<Data>() {
                @Override
                public int compare(Data data1, Data data2) {
                    return Integer.valueOf(data1.getSequence()).compareTo(Integer.valueOf(data2.getSequence()));
                }
            });}
        return optionList;
    }
}

package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.ShowImageFragment;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.QuizAnswer;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.DownloadService1;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.DownloadFileAsync;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by harsh on 25-04-2017.
 */

public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.ViewHolder> {
    private List<Data> optionList;
    Typeface VodafoneRg, VodafoneRgBd;
    Typeface materialdesignicons_font;
    private Context context;
    private List<String> OptionAtoZList;
    private int selectedItemPosition = -1;
    private int questionNo;
    private String questionTitle;
    private int totalNo;
    private Boolean correctAnsFlage = false;
    private Data answerMedia;
    private CustomProgressDialog customProgressDialog;
    private Boolean onTimeClick = false;
    private MediaPlayer mediaPlayer;
private String correct_answer_description;
    public QuizQuestionAdapter(Context context, List<Data> optionList, List<String> OptionAtoZList, int questionNo, String questionTitle, int totalNo, Data answerMedia ,String correct_answer_description) {
        this.optionList = optionList;
        this.OptionAtoZList = OptionAtoZList;
        this.context = context;
        this.questionNo = questionNo;
        this.questionTitle = questionTitle;
        this.totalNo = totalNo;
        this.answerMedia = answerMedia;
        this.correct_answer_description=correct_answer_description;
        VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        materialdesignicons_font = FontManager.getFontTypeface(context, "fonts/materialdesignicons-webfont.otf");
        VodafoneRgBd = FontManager.getFontTypeface(context, "fonts/VodafoneRgBd.ttf");
        customProgressDialog = new CustomProgressDialog(context, R.mipmap.syc);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_text_item, parent, false);
        QuizQuestionAdapter.ViewHolder viewHolder = new QuizQuestionAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final QuizQuestionAdapter.ViewHolder holder, final int position) {

        holder.option.setText(OptionAtoZList.get(position).toString() + ")");
        final DbHelper dbHelper = new DbHelper(context);
        if (optionList.get(position).getLang_resource_name() != null) {
            Data titleResource = dbHelper.getResourceEntityByName(optionList.get(position).getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (titleResource != null) {
                holder.answer.setText(Html.fromHtml(titleResource.getContent()));
            }
        }
        if (selectedItemPosition == position) {
            holder.radio.setChecked(true);
        } else {
            holder.radio.setChecked(false);
        }

        //set image
        final Data media = dbHelper.getMediaEntityByIdAndLaunguageId(optionList.get(position).getMedia_id(),
                Utility.getLanguageIdFromSharedPreferences(context));
        if (media != null && media.getDownload_url() != null) {
            holder.viewIcon.setText(Html.fromHtml("&#xf616;"));
            holder.viewLayout.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.VISIBLE);
            holder.mainlayout.setPadding(5, 0, 5, 0);
            holder.rowLayout.setPadding(0, 0, 0, 0);
            if (media.getLocalFilePath() == null) {
                if (Utility.isOnline(context)) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, DownloadService1.class);
                    String strJsonmedia = gson.toJson(media);
                    intent.putExtra(Consts.EXTRA_MEDIA, strJsonmedia);
                    intent.putExtra(Consts.EXTRA_URLPropertyForDownload, Consts.DOWNLOAD_URL);
                    context.startService(intent);
                    new DownloadImageTask(holder.image, customProgressDialog)
                            .execute(media.getDownload_url());
                    holder.viewLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ShowImageFragment fragment = ShowImageFragment.newInstance(media.getDownload_url(), "");
                            navigateToFragment(fragment);
                        }
                    });
                }
            } else {
                Uri uri = Uri.fromFile(new File(media.getLocalFilePath()));
                if (uri != null) {
                    Picasso.with(context).load(uri).into(holder.image);
                }
                holder.viewLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowImageFragment fragment = ShowImageFragment.newInstance(media.getLocalFilePath(), "");
                        navigateToFragment(fragment);
                    }
                });
            }
        } else {
            holder.viewLayout.setVisibility(View.GONE);
            holder.image.setVisibility(View.GONE);
            holder.mainlayout.setPadding(5, 10, 5, 10);
            holder.rowLayout.setPadding(0, 16, 0, 16);
        }

       /* //for correct ans highlite
        holder.optionLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        if (optionList.get(position).getIs_correct() == 1) {
            if (correctAnsFlage) {
                holder.optionLayout.setBackgroundColor(Color.parseColor("#ADFF2F"));
            }
        }*/
        //after one time click layout disable
        if (onTimeClick) {
            holder.optionLayout.setEnabled(false);
            if (selectedItemPosition == position) {//only select option view not disable
                holder.viewLayout.setEnabled(true);
            } else {
                holder.viewLayout.setEnabled(false);
            }
        }
        holder.optionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = context.getSharedPreferences("OptionPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("optionId", optionList.get(position).getId());
                editor.putBoolean("selectCheck", true);
                if (optionList.get(position).getIs_correct() == 1) {
                    editor.putInt("correctAns", 1);
                    correctAnsFlage = false;
                } else {
                    correctAnsFlage = true;
                    editor.putInt("correctAns", 0);
                    String correctTitle = "";
                    String correctAnsDescription = "";
                    int correctPosition = 0;
                    for (int i = 0; i < optionList.size(); i++) {
                        if (optionList.get(i).getIs_correct() == 1) {
                            correctTitle = optionList.get(i).getLang_resource_name();
                            //correctAnsDescription = optionList.get(i).getLang_resource_correct_answer_description();
                            correctPosition = i;
                        }
                    }
                    alertForWrongANS(correctTitle, correctPosition);
                }
                editor.commit();
                onTimeClick = true;
                selectedItemPosition = position;
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    //alert for Ouit message
    public void alertForWrongANS(String correctTitle, int correctPosition) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.getWindow().getAttributes().windowAnimations = R.style.alertAnimation;
        View view = alert.getLayoutInflater().inflate(R.layout.quiz_wrong_ans_alert, null);
        TextView correctIcon = (TextView) view.findViewById(R.id.correctIcon);
        correctIcon.setTypeface(materialdesignicons_font);
        TextView description = (TextView) view.findViewById(R.id.description);
        description.setTypeface(VodafoneRg);
        TextView descriptionDetails = (TextView) view.findViewById(R.id.descriptionDetails);
        descriptionDetails.setTypeface(VodafoneRg);
        TextView desIcon = (TextView) view.findViewById(R.id.desIcon);
        desIcon.setTypeface(materialdesignicons_font);
        final TextView listen_icon = (TextView) view.findViewById(R.id.listen_icon);
        listen_icon.setTypeface(materialdesignicons_font);
        TextView correctAns = (TextView) view.findViewById(R.id.correctAns);
        correctAns.setTypeface(VodafoneRg);
        final TextView option = (TextView) view.findViewById(R.id.option);
        option.setTypeface(VodafoneRg);
        TextView optionDetails = (TextView) view.findViewById(R.id.optionDetails);
        optionDetails.setTypeface(VodafoneRg);
        TextView close_text = (TextView) view.findViewById(R.id.close_text);
        close_text.setTypeface(VodafoneRg);
        TextView quit_icon = (TextView) view.findViewById(R.id.quit_icon);
        LinearLayout listenLayout = (LinearLayout) view.findViewById(R.id.listenLayout);
        final TextView listen_text = (TextView) view.findViewById(R.id.listen_text);
        quit_icon.setTypeface(materialdesignicons_font);
        quit_icon.setText(Html.fromHtml("&#xf156;"));
        correctIcon.setText(Html.fromHtml("&#xf134;"));
        listen_icon.setText(Html.fromHtml("&#xf57e;"));
        desIcon.setText(Html.fromHtml("&#xf2fd;"));
        DbHelper dbHelper = new DbHelper(context);
        Data titleResource = dbHelper.getResourceEntityByName(correctTitle,
                Utility.getLanguageIdFromSharedPreferences(context));
        if (titleResource != null) {
            optionDetails.setText(Html.fromHtml(titleResource.getContent()));
        }
        if (correct_answer_description != null) {
            descriptionDetails.setText(Html.fromHtml(correct_answer_description));
        }

        option.setText(OptionAtoZList.get(correctPosition).toString() + ")");

        LinearLayout quit_layout = (LinearLayout) view.findViewById(R.id.quit_layout);
        alert.setCustomTitle(view);
        resetMediaPlayer(listen_text, listen_icon);//reset media player
        quit_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                resetMediaPlayer(listen_text, listen_icon);//reset media player
            }
        });
        listenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenCorrectAnsAudio( listen_icon,listen_text);
            }
        });

        alert.show();

    }

    //lesten ans Audio
    private void listenCorrectAnsAudio(TextView listen_icon,TextView listen_text) {
        if (answerMedia != null) {

            if (answerMedia.getType().equals("Audio")) {
                String url = answerMedia.getLocalFilePath();
                if (url != null && !url.equals("")) {
                    playAudioAsync(url,listen_icon, listen_text);
                } else {
                    if (Utility.isOnline(context)) {
                        downloadAudioFile(answerMedia);
                        url = answerMedia.getUrl();
                        playAudioAsync(url, listen_icon, listen_text);
                    } else {
                        // "you are offline"
                    }
                }
            }

        }}
    private void playAudioAsync(final String url, final TextView listen_icon, final TextView listen_text) {

        final String listenText = (String) listen_text.getText();

        listen_text.setText(context.getString(R.string.Buffering));
        new AsyncTask<String, Void, MediaPlayer>() {

            @Override
            protected MediaPlayer doInBackground(String... params) {
                if (mediaPlayer == null) {
                    Uri myUri = Uri.parse(url);
                    mediaPlayer = MediaPlayer.create(context, myUri);
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
                        if (mediaPlayer.isPlaying()   ){//listen_text.getText() == "PAUSE") {
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
                                Log.d(Consts.LOG_TAG, "successfully downloaded and local file updated: media Id:" + answerMedia.getId() + " downloading Url: " + answerMedia.getDownload_url() + " at " + result);
                            }
                        }
                    }
                    // customProgressDialog.dismiss();
                }
            };
            task.execute();
        }
    }

    private void playOfflineAudio(TextView listen_text, TextView listen_icon) {
        String url;
        if (answerMedia != null) {

            url = answerMedia.getLocalFilePath();
            if (url != null && !url.equals("")) {
                try {
                    Uri myUri = Uri.parse(url);
                    mediaPlayer = MediaPlayer.create(context, myUri);
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(context, myUri);
                    }
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        listen_icon.setText(Html.fromHtml("&#xf3e4;"));
                        listen_text.setText("Pause");

                    } else {
                        mediaPlayer.start();
                        listen_icon.setText(Html.fromHtml("&#xf57e;"));
                        listen_text.setText("Listen");
                    }

                } catch (Exception e) {
                }
            }
        }
    }

    //reset MediaPlayer after select new question or skip question
    private void resetMediaPlayer(TextView listen_text, TextView listen_icon) {
        if (mediaPlayer != null) {
            //mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            listen_icon.setText(Html.fromHtml("&#xf57e;"));
            listen_text.setText("Listen");
        }
    }

    private void playOnlineAudio(TextView listen_text, TextView listen_icon) {
        String url;
        if (Utility.isOnline(context)) {
            if (answerMedia != null) {
                url = answerMedia.getUrl();
                if (url != null && !url.equals("")) {
               /* Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "audio*//*");
                startActivity(intent);*/
                    try {
                        Uri myUri = Uri.parse(url);
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer.create(context, myUri);
                        }
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            listen_icon.setText(Html.fromHtml("&#xf3e4;"));
                            listen_text.setText("Pause");

                        } else {
                            mediaPlayer.start();
                            listen_icon.setText(Html.fromHtml("&#xf57e;"));
                            listen_text.setText("Listen");
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public void navigateToFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
        transaction.replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView option, answer, viewIcon, view;
        public RadioButton radio;
        public LinearLayout mainlayout, optionLayout, viewLayout, rowLayout;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            option = (TextView) itemView.findViewById(R.id.option);
            answer = (TextView) itemView.findViewById(R.id.answer);
            radio = (RadioButton) itemView.findViewById(R.id.radio);
            viewIcon = (TextView) itemView.findViewById(R.id.viewIcon);
            view = (TextView) itemView.findViewById(R.id.view);
            option.setTypeface(VodafoneRg);
            answer.setTypeface(VodafoneRg);
            viewIcon.setTypeface(materialdesignicons_font);
            view.setTypeface(VodafoneRgBd);
            mainlayout = (LinearLayout) itemView.findViewById(R.id.mainlayout);
            rowLayout = (LinearLayout) itemView.findViewById(R.id.rowLayout);
            optionLayout = (LinearLayout) itemView.findViewById(R.id.optionLayout);
            image = (ImageView) itemView.findViewById(R.id.image);
            viewLayout = (LinearLayout) itemView.findViewById(R.id.viewLayout);

        }
    }

}

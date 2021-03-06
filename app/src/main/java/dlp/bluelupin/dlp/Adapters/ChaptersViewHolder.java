package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.File;

import dlp.bluelupin.dlp.Fragments.CourseFragment;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.ScaleImageView;

/**
 * Created by Neeraj on 7/26/2016.
 */
public class ChaptersViewHolder extends RecyclerView.ViewHolder {
    public TextView chapterTitle, chapterDescription, favorite, download, downloadIcon;
    public android.support.v7.widget.CardView cardView;
    public ScaleImageView chapterImage;
    public TextView starIcon, quiz,quiz_Icon,arrowIcon,start_quiz_Icon;
    public RelativeLayout starIconlayout, downloadIconlayout;
    public LinearLayout download_layout, favoriteLayout;
    public ProgressBar progressBar;
    public LinearLayout quizStartLayout,quizLayout,titleLayout,buttonLayout;
    public View divView;


    public ChaptersViewHolder(View itemView) {
        super(itemView);

        cardView = (android.support.v7.widget.CardView) itemView.findViewById(R.id.card_view);
        cardView.setCardElevation(2);
        cardView.setRadius(10);
        chapterTitle = (TextView) itemView.findViewById(R.id.chapterTitle);
        chapterDescription = (TextView) itemView.findViewById(R.id.chapterDescription);
        favorite = (TextView) itemView.findViewById(R.id.favourite);
        starIcon = (TextView) itemView.findViewById(R.id.starIcon);
        download = (TextView) itemView.findViewById(R.id.download);
        downloadIcon = (TextView) itemView.findViewById(R.id.downloadIcon);
        chapterImage = (ScaleImageView) itemView.findViewById(R.id.chapterImage);
        favoriteLayout = (LinearLayout) itemView.findViewById(R.id.favoriteLayout);
        download_layout = (LinearLayout) itemView.findViewById(R.id.download_layout);
        starIconlayout = (RelativeLayout) itemView.findViewById(R.id.starIconlayout);
        downloadIconlayout = (RelativeLayout) itemView.findViewById(R.id.downloadIconlayout);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        quiz = (TextView) itemView.findViewById(R.id.quiz);
        quiz_Icon = (TextView) itemView.findViewById(R.id.quiz_Icon);
        start_quiz_Icon = (TextView) itemView.findViewById(R.id.start_quiz_Icon);
        arrowIcon = (TextView) itemView.findViewById(R.id.arrowIcon);
        divView = (View) itemView.findViewById(R.id.divView);
        quizStartLayout = (LinearLayout) itemView.findViewById(R.id.quizStartLayout);
        quizLayout = (LinearLayout) itemView.findViewById(R.id.quizLayout);
        titleLayout = (LinearLayout) itemView.findViewById(R.id.titleLayout);
        buttonLayout = (LinearLayout) itemView.findViewById(R.id.buttonLayout);

    }


}
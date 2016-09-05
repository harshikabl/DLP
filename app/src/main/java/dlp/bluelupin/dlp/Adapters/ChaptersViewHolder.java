package dlp.bluelupin.dlp.Adapters;

import android.media.Image;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dlp.bluelupin.dlp.Fragments.CourseFragment;
import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 7/26/2016.
 */
public class ChaptersViewHolder extends RecyclerView.ViewHolder  {
    public TextView chapterTitle, chapterDescription,favorite,download;
    public android.support.v7.widget.CardView cardView;
    public ImageView chapterImage;
    public LinearLayout favorite_layout,download_layout;
    public ImageView starIcon,downloadIcon;

    public ChaptersViewHolder(View itemView) {
        super(itemView);

        cardView = (android.support.v7.widget.CardView) itemView.findViewById(R.id.card_view);
        cardView.setCardElevation(2);
        cardView.setRadius(10);
        chapterTitle = (TextView) itemView.findViewById(R.id.chapterTitle);
        chapterDescription = (TextView) itemView.findViewById(R.id.chapterDescription);
       // favorite = (TextView) itemView.findViewById(R.id.favorite);
        starIcon = (ImageView) itemView.findViewById(R.id.starIcon);
        // = (TextView) itemView.findViewById(R.id.download);
        downloadIcon = (ImageView) itemView.findViewById(R.id.downloadIcon);
        chapterImage = (ImageView) itemView.findViewById(R.id.chapterImage);
        favorite_layout= (LinearLayout) itemView.findViewById(R.id.favorite_layout);
        download_layout= (LinearLayout) itemView.findViewById(R.id.download_layout);
    }


}
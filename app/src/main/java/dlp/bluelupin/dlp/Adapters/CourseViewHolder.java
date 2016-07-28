package dlp.bluelupin.dlp.Adapters;

import android.graphics.Typeface;
import android.media.Image;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dlp.bluelupin.dlp.Fragments.CourseFragment;
import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 7/26/2016.
 */
public class CourseViewHolder extends RecyclerView.ViewHolder {
    public TextView courseTitle, courseDescription;
    public android.support.v7.widget.CardView cardView;
    public ImageView courseImage;

    public CourseViewHolder(View itemView) {
        super(itemView);

        cardView = (android.support.v7.widget.CardView) itemView.findViewById(R.id.card_view);
        cardView.setCardElevation(2);
        cardView.setRadius(10);
        courseTitle = (TextView) itemView.findViewById(R.id.courseTitle);
        courseDescription = (TextView) itemView.findViewById(R.id.courseDescription);
        courseImage = (ImageView) itemView.findViewById(R.id.courseImage);
    }

}

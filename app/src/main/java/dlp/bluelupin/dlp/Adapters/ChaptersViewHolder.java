package dlp.bluelupin.dlp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 7/26/2016.
 */
public class ChaptersViewHolder extends RecyclerView.ViewHolder {
    public TextView courseType, courseDetails,favorite,starIcon,download,downloadIcon;
    public android.support.v7.widget.CardView cardView;

    public ChaptersViewHolder(View itemView) {
        super(itemView);

        cardView = (android.support.v7.widget.CardView) itemView.findViewById(R.id.card_view);
        cardView.setCardElevation(2);
        cardView.setRadius(10);
        courseType = (TextView) itemView.findViewById(R.id.courseType);
        courseDetails = (TextView) itemView.findViewById(R.id.courseDetails);
        favorite = (TextView) itemView.findViewById(R.id.favorite);
        starIcon = (TextView) itemView.findViewById(R.id.starIcon);
        download = (TextView) itemView.findViewById(R.id.download);
        downloadIcon = (TextView) itemView.findViewById(R.id.downloadIcon);
    }

}
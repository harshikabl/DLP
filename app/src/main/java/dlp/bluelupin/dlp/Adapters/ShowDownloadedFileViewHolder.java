package dlp.bluelupin.dlp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 10/28/2016.
 */

public class ShowDownloadedFileViewHolder extends RecyclerView.ViewHolder {
    public TextView mediaTitle, mediaDescription;
    public android.support.v7.widget.CardView cardView;
    public ImageView mediaImage;
    public TextView cancelIcon;
    public ListView mediaList;

    public ShowDownloadedFileViewHolder(View itemView) {
        super(itemView);

        cardView = (android.support.v7.widget.CardView) itemView.findViewById(R.id.card_view);
        cardView.setCardElevation(2);
        cardView.setRadius(10);
        mediaTitle = (TextView) itemView.findViewById(R.id.mediaTitle);
        mediaDescription = (TextView) itemView.findViewById(R.id.mediaDescription);
        mediaImage = (ImageView) itemView.findViewById(R.id.mediaImage);
        cancelIcon = (TextView) itemView.findViewById(R.id.cancelIcon);
        mediaList = (ListView) itemView.findViewById(R.id.mediaList);
    }


}
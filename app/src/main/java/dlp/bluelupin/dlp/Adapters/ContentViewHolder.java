package dlp.bluelupin.dlp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 7/27/2016.
 */
public class ContentViewHolder extends RecyclerView.ViewHolder {
    public android.support.v7.widget.CardView cardView;
    //public TextView contentTitle, contentDescription;
    public LinearLayout contentContainer;
    public TextView playIcon;

    public ContentViewHolder(View itemView) {
        super(itemView);

        cardView = (android.support.v7.widget.CardView) itemView.findViewById(R.id.card_view);
        cardView.setTag(this);
        cardView.setCardElevation(2);
        cardView.setRadius(10);
//        contentTitle = (TextView) itemView.findViewById(R.id.contentTitle);
//        contentDescription = (TextView) itemView.findViewById(R.id.contentDescription);
        contentContainer = (LinearLayout) itemView.findViewById(R.id.contentContainer);

        playIcon = (TextView) itemView.findViewById(R.id.playIcon);
    }


}
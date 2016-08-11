package dlp.bluelupin.dlp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 8/10/2016.
 */
public class FavoritesViewHolder extends RecyclerView.ViewHolder  {
    public TextView chapterTitle, chapterDescription,favorite,starIcon,download,downloadIcon;
    public android.support.v7.widget.CardView cardView;
    public ImageView chapterImage;
    public LinearLayout favorite_layout;

    public FavoritesViewHolder(View itemView) {
        super(itemView);

        cardView = (android.support.v7.widget.CardView) itemView.findViewById(R.id.card_view);
        cardView.setCardElevation(2);
        cardView.setRadius(10);
        chapterTitle = (TextView) itemView.findViewById(R.id.chapterTitle);
        chapterDescription = (TextView) itemView.findViewById(R.id.chapterDescription);
        favorite = (TextView) itemView.findViewById(R.id.favorite);
        starIcon = (TextView) itemView.findViewById(R.id.starIcon);
        download = (TextView) itemView.findViewById(R.id.download);
        downloadIcon = (TextView) itemView.findViewById(R.id.downloadIcon);
        chapterImage = (ImageView) itemView.findViewById(R.id.chapterImage);
        favorite_layout= (LinearLayout) itemView.findViewById(R.id.favorite_layout);
    }


}
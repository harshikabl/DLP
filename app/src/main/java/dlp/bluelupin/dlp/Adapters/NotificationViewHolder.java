package dlp.bluelupin.dlp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 7/28/2016.
 */
public class NotificationViewHolder extends RecyclerView.ViewHolder {
    public TextView notification, notificationDescription,dateTime;
    public android.support.v7.widget.CardView cardView;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        cardView = (android.support.v7.widget.CardView) itemView.findViewById(R.id.card_view);
        cardView.setCardElevation(2);
        cardView.setRadius(10);
        notification = (TextView) itemView.findViewById(R.id.notification);
        notificationDescription = (TextView) itemView.findViewById(R.id.notificationDescription);
        dateTime= (TextView) itemView.findViewById(R.id.dateTime);
    }
}

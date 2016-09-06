package dlp.bluelupin.dlp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 7/28/2016.
 */
public class NotificationViewHolder extends RecyclerView.ViewHolder {
    public TextView  notificationDescription,dateTime,on;
    public LinearLayout cardView;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        cardView = (LinearLayout) itemView.findViewById(R.id.card_view);
        //cardView.setCardElevation(2);
        //cardView.setRadius(10);
        notificationDescription = (TextView) itemView.findViewById(R.id.notificationDescription);
        dateTime= (TextView) itemView.findViewById(R.id.dateTime);
        on= (TextView) itemView.findViewById(R.id.on);
    }
}

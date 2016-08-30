package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dlp.bluelupin.dlp.Fragments.ItemTouchHelperInterface;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 7/28/2016.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> implements ItemTouchHelperInterface {
    Context context;
    private List<Data>  itemList;
    private SparseBooleanArray selectedItems;

    public NotificationAdapter(Context context, List<Data> itemList) {
        this.context = context;
        this.itemList = itemList;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_view_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotificationViewHolder holder, final int position) {
        // Set the selected state of the row depending on the position
        holder.cardView.setSelected(selectedItems.get(position, false));

        Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneRg.ttf");
        holder.notification.setTypeface(VodafoneExB);
        holder.notificationDescription.setTypeface(VodafoneRg);
        holder.dateTime.setTypeface(VodafoneRg);
        holder.notification.setText("Notification " + itemList.get(position).getId());
        holder.notificationDescription.setText(itemList.get(position).getMessage());
        holder.dateTime.setText(itemList.get(position).getSend_at());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionItemRow(holder, position);
            }
        });
    }

    // Save the selected positions to the SparseBooleanArray for show the selection item row
    public void showSelectionItemRow(NotificationViewHolder holder, final int position) {
        if (selectedItems.get(holder.getAdapterPosition(), false)) {
            selectedItems.delete(position);
            holder.cardView.setSelected(false);
        } else {
            selectedItems.put(holder.getAdapterPosition(), true);
            holder.cardView.setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(itemList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(itemList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position);
        selectedItems.delete(position);

    }
}


package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import dlp.bluelupin.dlp.Fragments.ItemTouchHelperInterface;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 7/28/2016.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {
    Context context;
    private List<Data> itemList;
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



        Typeface materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = FontManager.getFontTypeface(context, "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        holder.notificationDescription.setTypeface(VodafoneRg);
        holder.date.setTypeface(VodafoneRg);
        holder.dateIcon.setTypeface(materialdesignicons_font);
        holder.timeIcon.setTypeface(materialdesignicons_font);
        holder.time.setTypeface(VodafoneRg);
        holder.dateIcon.setText(Html.fromHtml("&#xf0f5;"));
        holder.timeIcon.setText(Html.fromHtml("&#xf150;"));
        holder.notificationDescription.setText(Html.fromHtml(itemList.get(position).getMessage()));
        //2016-09-05 10:54:43
        String sendAtdate = Utility.convertDate(itemList.get(position).getSend_at());
        if (sendAtdate != null && !sendAtdate.equals("")) {
            //get value based on array position Wednesday,15,Jun,6,30,AM,2000,06 index(0,1,2,3,4,5,6,7)
            String[] startArray = sendAtdate.split(",");
            holder.date.setText(startArray[1] + "-" + startArray[2] + "-" + startArray[6]);
            holder.time.setText(startArray[3] + ":" + startArray[4] + " " + startArray[5]);
        }


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

   /* @Override
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

    }*/
}


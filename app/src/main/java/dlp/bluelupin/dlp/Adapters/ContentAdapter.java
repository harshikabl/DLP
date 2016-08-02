package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.ContentFragment;
import dlp.bluelupin.dlp.Fragments.DownloadingFragment;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 7/27/2016.
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentViewHolder> {
    private List<Data> itemList;
    private Context context;
    private Boolean favFlage = false;

    public ContentAdapter(Context context, List<Data> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_list_view_item, parent, false);
        return new ContentViewHolder(layoutView);
    }


    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneRg.ttf");
//        holder.contentTitle.setTypeface(VodafoneExB);
//        holder.contentDescription.setTypeface(VodafoneRg);

        final DbHelper dbHelper = new DbHelper(context);
        final Data data = itemList.get(position);

        if (Consts.IS_DEBUG_LOG)
            Log.d(Consts.LOG_TAG, " data id: " + data.getId() + " type: " + data.getType());
        if (data.getLang_resource_name() != null) {

            Data resource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context).ordinal());
            if (resource != null) {

                if (data.getType().equalsIgnoreCase("Text")) {
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, " resource text: " + resource.getContent());
                    }
                    TextView dynamicTextView = new TextView(context);
                    dynamicTextView.setText(resource.getContent());

                    dynamicTextView.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    holder.contentContainer.addView(dynamicTextView);
                }
            }
            if (data.getType().equalsIgnoreCase("Image")) {
                if (data.getMedia_id() != 0) {
                    ImageView dynamicImageView = new ImageView(context);
                    dynamicImageView.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    Data media = dbHelper.getMediaEntityById(data.getMedia_id());
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, " Image Url: " + media.getUrl());
                    }
                    if (media != null) {
                        new DownloadImageTask(dynamicImageView)
                                .execute(media.getUrl());
                    }
                    holder.contentContainer.addView(dynamicImageView);
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

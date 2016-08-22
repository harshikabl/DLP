package dlp.bluelupin.dlp.Adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import dlp.bluelupin.dlp.Activities.DownloadService;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.DownloadingFragment;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.ServiceHelper;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 8/5/2016.
 */
public class DownloadingAdapter extends RecyclerView.Adapter<DownloadingViewHolder> {

    private List<Data> itemList;
    private Context context;
    private Boolean favFlage = false;
    private String type;

    public DownloadingAdapter(Context context, List<Data> list) {
        this.itemList = list;
        this.context = context;
        this.type = type;
    }

    @Override
    public DownloadingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.downloading_list_view_item, parent, false);
        return new DownloadingViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final DownloadingViewHolder holder, int position) {
        Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneRg.ttf");
        Typeface materialdesignicons_font = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.otf");
        holder.mediaTitle.setTypeface(VodafoneExB);
        holder.mediaDescription.setTypeface(VodafoneRg);
        holder.downloadProgress.setTypeface(VodafoneExB);
        holder.download.setTypeface(VodafoneRg);
        holder.cancelIcon.setTypeface(materialdesignicons_font);
        holder.cancelIcon.setText(Html.fromHtml("&#xf156"));
        // starting the download service
        final DbHelper dbHelper = new DbHelper(context);
        final Data data = itemList.get(position);

        final Data resource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                Utility.getLanguageIdFromSharedPreferences(context).ordinal());
        if (data.getLang_resource_name() != null) {
            Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context).ordinal());
            if (titleResource != null) {
                holder.mediaTitle.setText(titleResource.getContent());
            }
        }
        if (data.getLang_resource_description() != null) {
            Data descriptionResource = dbHelper.getResourceEntityByName(data.getLang_resource_description(),
                    Utility.getLanguageIdFromSharedPreferences(context).ordinal());
            if (descriptionResource != null) {
                holder.mediaDescription.setText(descriptionResource.getContent());
            }
        }
        if (data.getThumbnail_media_id() != 0) {
            Data media = dbHelper.getMediaEntityById(data.getThumbnail_media_id());
            if (media != null) {
                //holder.chapterImage.
                new DownloadImageTask(holder.mediaImage)
                        .execute(media.getUrl());
            }
        }
        if (data.getProgress() != 0) {
            holder.downloadProgress.setText(data.getProgress() + "% Completed");
            holder.mProgress.setProgress(data.getProgress());
        }
        holder.cancelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Consts.mBroadcastDeleteAction);
                broadcastIntent.putExtra("mediaId",data.getId());
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);*/
                final ServiceHelper sh = new ServiceHelper(context);
                Data media = dbHelper.getMediaEntityById(data.getThumbnail_media_id());
                if (media != null) {
                    String url = media.getUrl();
                    sh.callDownloadDeleteRequest(url);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}


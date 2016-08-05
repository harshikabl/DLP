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
import dlp.bluelupin.dlp.Fragments.DownloadingFragment;
import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 8/5/2016.
 */
public class DownloadingAdapter extends RecyclerView.Adapter<DownloadingViewHolder> {

    private List<String> list;
    private Context context;
    private Boolean favFlage = false;
    private String type;

    public DownloadingAdapter(Context context, List<String> list) {
        this.list = list;
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
        holder.mediaTitle.setText(list.get(position));
        // starting the download service


    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}


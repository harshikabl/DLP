package dlp.bluelupin.dlp.Adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.DownloadingFragment;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.DownloadMediaWithParent;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.DownloadService1;
import dlp.bluelupin.dlp.Services.ServiceHelper;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 8/5/2016.
 */
public class DownloadingAdapter extends RecyclerView.Adapter<DownloadingViewHolder> {

    private List<DownloadMediaWithParent> itemList;
    private Context context;
    private Boolean favFlage = false;
    private String type;
    private CustomProgressDialog customProgressDialog;

    public DownloadingAdapter(Context context, List<DownloadMediaWithParent> list) {
        this.itemList = list;
        this.context = context;
        this.type = type;
        customProgressDialog = new CustomProgressDialog(context, R.mipmap.syc);
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
        final DownloadMediaWithParent dataWithParent = itemList.get(position);
        Data data = dbHelper.getDataEntityById(dataWithParent.getParentId());
        if (data != null) {
            final Data resource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (data.getLang_resource_name() != null) {
                Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                        Utility.getLanguageIdFromSharedPreferences(context));
                if (titleResource != null) {
                    holder.mediaTitle.setText(titleResource.getContent());
                }
            }
        }

        if (data.getThumbnail_media_id() != 0) {
            Data media = dbHelper.getMediaEntityByIdAndLaunguageId(data.getThumbnail_media_id(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (media != null && media.getDownload_url() != null) {
                if (media.getLocalFilePath() == null) {
                    if (Utility.isOnline(context)) {
                        Gson gson = new Gson();
                        Intent intent = new Intent(context, DownloadService1.class);
                        String strJsonmedia = gson.toJson(media);
                        intent.putExtra(Consts.EXTRA_MEDIA, strJsonmedia);
                        intent.putExtra(Consts.EXTRA_URLPropertyForDownload, Consts.DOWNLOAD_URL);
                        context.startService(intent);
                        new DownloadImageTask(holder.mediaImage, customProgressDialog)
                                .execute(media.getDownload_url());
                    }
                } else {
                    File imgFile = new File(media.getLocalFilePath());
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        holder.mediaImage.setImageBitmap(bitmap);
                    }
                }

            }
        }
//        final Data data_item = dataWithParent.getStrJsonResourcesToDownloadList();
//        holder.mediaTitle.setText(data_item.getFile_path());


        DataAdapter dataAdapter = new DataAdapter(context, dataWithParent.getStrJsonResourcesToDownloadList());
        holder.progressList.setAdapter(dataAdapter);
        Utility.justifyListViewHeightBasedOnChildrenForDisableScrool(holder.progressList);
        dataAdapter.notifyDataSetChanged();
//        if (Consts.IS_DEBUG_LOG) {
//            Log.d(Consts.LOG_TAG, "**** setting progress for media Id: " + data_item.getId() + " progress:" + data_item.getProgress() + "%");
//        }
//        if (data_item.getProgress() != 0) {
//            holder.downloadProgress.setText(data_item.getProgress() + "% Completed");
//            holder.mProgress.setProgress(data_item.getProgress());
//        }
//        if (data_item.getProgress() >= 100) {
//            holder.cardView.setVisibility(View.INVISIBLE);
//        }
        holder.cancelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteFileDownloadedByParentId(dataWithParent.getParentId());//delete media by parent id
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Consts.mBroadcastDeleteAction);
                broadcastIntent.putExtra("mediaId", dataWithParent.getParentId());
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
                holder.cardView.setVisibility(View.INVISIBLE);
                Gson gson = new Gson();
                //String strJsonmedia = gson.toJson(data_item);
                Intent intent = new Intent(Consts.MESSAGE_CANCEL_DOWNLOAD);
                intent.putExtra(Consts.EXTRA_MEDIA, dataWithParent.getParentId()); // strJsonmedia
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "**** sending cancel message in DownloadingAdapter: " + intent.getAction());
                }
            }
        });

    }


   /* private FrameLayout makeProgressBar(int progress) {

        FrameLayout frameLayout = new FrameLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        layoutParams.setMargins(0, 0, 0, 0);
        frameLayout.setLayoutParams(layoutParams);
        ProgressBar dynamicProgress = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        dynamicProgress.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dynamicProgress.setProgress(progress);
        dynamicProgress.setIndeterminate(false);
        dynamicProgress.setVisibility(View.VISIBLE);
        dynamicProgress.setProgressDrawable(context.getResources().getDrawable(R.drawable.custom_progressbar));

        frameLayout.addView(dynamicProgress);
        return frameLayout;
    }*/

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}


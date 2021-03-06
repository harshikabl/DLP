package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.ChaptersFragment;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.DownloadService1;
import dlp.bluelupin.dlp.SplashActivity;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.LogAnalyticsHelper;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 7/26/2016.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseViewHolder> {

    private List<Data> itemList;
    private Context context;
    private CustomProgressDialog customProgressDialog;

    public CourseAdapter(Context context, List<Data> itemList) {
        this.itemList = itemList;
        this.context = context;
        customProgressDialog = new CustomProgressDialog(context, R.mipmap.syc);
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_view_item, parent, false);
        return new CourseViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final CourseViewHolder holder, int position) {

        Typeface VodafoneExB = FontManager.getFontTypeface(context, "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        Typeface materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");
        holder.courseTitle.setTypeface(VodafoneExB);
        holder.courseDescription.setTypeface(VodafoneRg);
        holder.cardView.setCardBackgroundColor(Color.parseColor("#00000000"));
        holder.learnIcon.setTypeface(materialdesignicons_font);
        holder.learnIcon.setText(Html.fromHtml("&#xf5da;"));
        holder.learnLable.setTypeface(VodafoneExB);

        final DbHelper dbHelper = new DbHelper(context);
        final Data data = itemList.get(position);
        if (data.getLang_resource_name() != null) {
            Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (titleResource != null) {
                holder.courseTitle.setText(Html.fromHtml(titleResource.getContent()));
            }
        }

        if (data.getLang_resource_description() != null) {
            Data descriptionResource = dbHelper.getResourceEntityByName(data.getLang_resource_description(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (descriptionResource != null) {
                holder.courseDescription.setText(Html.fromHtml(descriptionResource.getContent()));
            }
        }

        if (data.getThumbnail_media_id() != 0) {
            Data media = dbHelper.getMediaEntityByIdAndLaunguageId(data.getThumbnail_media_id(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (media != null && media.getUrl() != null) {
                if (media.getLocalFilePath() == null) {
                    if (Utility.isOnline(context)) {
                        Gson gson = new Gson();
                        Intent intent = new Intent(context, DownloadService1.class);
                        String strJsonmedia = gson.toJson(media);
                        intent.putExtra(Consts.EXTRA_MEDIA, strJsonmedia);
                        intent.putExtra(Consts.EXTRA_URLPropertyForDownload, Consts.URL);
                        context.startService(intent);
                        new DownloadImageTask(holder.courseImage, customProgressDialog)
                                .execute(media.getUrl());
                      customProgressDialog.dismiss();
                    }
                } else {
                 /*   File imgFile = new File(media.getLocalFilePath());
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        holder.courseImage.setImageBitmap(bitmap);
                    }*/
                    Uri uri = Uri.fromFile(new File(media.getLocalFilePath()));
                    if (uri != null) {
                        Picasso.with(context).load(uri).into(holder.courseImage);
                    } else {
                        if (Utility.isOnline(context)) {
                            Gson gson = new Gson();
                            Intent intent = new Intent(context, DownloadService1.class);
                            String strJsonmedia = gson.toJson(media);
                            intent.putExtra(Consts.EXTRA_MEDIA, strJsonmedia);
                            intent.putExtra(Consts.EXTRA_URLPropertyForDownload, Consts.URL);
                            context.startService(intent);
                            new DownloadImageTask(holder.courseImage, customProgressDialog)
                                    .execute(media.getUrl());
                            customProgressDialog.dismiss();
                        }
                    }
                }

            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                String type = dbHelper.getTypeOfChildren(data.getId());
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "Navigating to  data_item id: " + data.getId() + " type: " + type);
                }

                logAnalytics(data);

                ChaptersFragment fragment = ChaptersFragment.newInstance(data.getId(), type);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                transaction.replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void logAnalytics(Data data) {

        if (data.getLang_resource_name() != null) {
            DbHelper dbHelper = new DbHelper(context);
            Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (titleResource != null) {
                LogAnalyticsHelper analyticsHelper = new LogAnalyticsHelper(context);
                Bundle bundle = new Bundle();
                bundle.putString("Name", titleResource.getContent());
                bundle.putString("Language", Utility.getLanguageIdFromSharedPreferences(context) + "");
                analyticsHelper.logEvent("Course", bundle);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

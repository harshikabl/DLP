package dlp.bluelupin.dlp.Adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.ChaptersFragment;
import dlp.bluelupin.dlp.Fragments.ContentFragment;
import dlp.bluelupin.dlp.Fragments.DownloadingFragment;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.FavoritesData;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.DownloadService1;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.LogAnalyticsHelper;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 7/26/2016.
 */
public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersViewHolder> {

    private List<Data> itemList;
    private Context context;
    private String type;
    private String contentTitle;

    public ChaptersAdapter(Context context, List<Data> itemList, String type) {
        this.itemList = itemList;
        this.context = context;
        this.type = type;
    }

    @Override
    public ChaptersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapters_list_view_item, parent, false);
        return new ChaptersViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ChaptersViewHolder holder, int position) {
        Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneRg.ttf");
        holder.chapterTitle.setTypeface(VodafoneExB);
        holder.chapterDescription.setTypeface(VodafoneRg);
        holder.cardView.setCardBackgroundColor(Color.parseColor("#EEEEEE"));

        // holder.downloadIcon.setImageResource(R.drawable.downloadupdate);
        Typeface materialdesignicons_font = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.otf");
//        holder.starIcon.setTypeface(materialdesignicons_font);
//        holder.starIcon.setText(Html.fromHtml("&#xf4ce;"));
        holder.favorite.setTypeface(VodafoneRg);
        holder.download.setTypeface(VodafoneRg);
        holder.downloadIcon.setTypeface(materialdesignicons_font);


        //show and hide favorite icon layout only in chapter layout
        /*if (type.equalsIgnoreCase(Consts.CHAPTER)) {
            holder.favorite_layout.setVisibility(View.VISIBLE);
        } else {
            holder.favorite_layout.setVisibility(View.GONE);
        }*/

        final DbHelper dbHelper = new DbHelper(context);
        final Data data = itemList.get(position);

        final Data resource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                Utility.getLanguageIdFromSharedPreferences(context));
        if (data.getLang_resource_name() != null) {
            Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (titleResource != null) {
                holder.chapterTitle.setText(titleResource.getContent());
            }
        }

        if (data.getLang_resource_description() != null) {
            Data descriptionResource = dbHelper.getResourceEntityByName(data.getLang_resource_description(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (descriptionResource != null) {
                holder.chapterDescription.setText(descriptionResource.getContent());
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
                        new DownloadImageTask(holder.chapterImage)
                                .execute(media.getDownload_url());
                    }
                } else {
                    File imgFile = new File(media.getLocalFilePath());
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        holder.chapterImage.setImageBitmap(bitmap);
                    }
                }

            }
        }

        DbHelper dbhelper = new DbHelper(context);
        // REMOVE BELOW IN PROD
        //dbHelper.setLocalPathOfAllMediaToNull();
        // REMOVE ABOVE IN PROD


        final List<Data> resourcesToDownloadList = dbhelper.getResourcesToDownload(data.getId(), Utility.getLanguageIdFromSharedPreferences(context));
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "Number of  downloads for chapter: " + data.getId() + " is: " + resourcesToDownloadList.size());
        }
        if (resourcesToDownloadList.size() <= 0) {
            // holder.downloadIcon.setTextColor(Color.parseColor("#ffffff"));
            holder.downloadIcon.setText(Html.fromHtml("&#xf12c;"));
        } else {
            holder.downloadIcon.setText(Html.fromHtml("&#xf1da;"));
            // holder.downloadIcon.setTextColor(Color.parseColor("#ffffff"));
        }
        //if meadia not downloaded then show download_layout
        if (data.getThumbnail_media_id() != 0) {
            final Data media = dbHelper.getDownloadMediaEntityById(data.getThumbnail_media_id());
            if (media != null) {
                holder.download_layout.setVisibility(View.VISIBLE);

                holder.download_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (resourcesToDownloadList.size() > 0) {
//                        {
//                            List<Data> resourceListToDownload = new ArrayList<Data>();
//                            List<Data> resourceToDownload = dbhelper.getThumbnailsToDownload(data.getId(),resourceListToDownload);
//                            if (Consts.IS_DEBUG_LOG) {
//                                Log.d(Consts.LOG_TAG, "Number of thumbnailsToDownload to download for chapter: " + data.getId() + " is: " + resourceToDownload.size());
//                                for (Data resource:resourceToDownload) {
//                                    Log.d(Consts.LOG_TAG, "Resource to be DL: " + resource.getId() + " url: " + resource.getUrl());
//                                }
//                            }
//                        }
                            if (Utility.isOnline(context)) {
                                Gson gson = new Gson();
                                String strJsonResourcesToDownloadList = gson.toJson(resourcesToDownloadList);
//
                                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                                DownloadingFragment fragment = DownloadingFragment.newInstance(strJsonResourcesToDownloadList);
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                                transaction.replace(R.id.container, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                Utility.alertForErrorMessage(context.getString(R.string.online_msg), context);
                            }

                        }
                    }
                });

            } else {

                holder.download_layout.setVisibility(View.GONE);
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = dbHelper.getTypeOfChildren(data.getId());
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "Navigating to  data id: " + data.getId() + " type: " + type);
                }
                if (type.equalsIgnoreCase(Consts.COURSE) || type.equalsIgnoreCase(Consts.CHAPTER) || type.equalsIgnoreCase(Consts.TOPIC)) {
                    logAnalytics(type, data);
                    FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                    ChaptersFragment fragment = ChaptersFragment.newInstance(data.getId(), type);

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                    transaction.replace(R.id.container, fragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    if (data.getLang_resource_name() != null) {
                        Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                                Utility.getLanguageIdFromSharedPreferences(context));
                        if (titleResource != null) {
                            contentTitle = titleResource.getContent();
                        }
                    }
                    logAnalytics(type, data);
                    FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                    ContentFragment fragment = ContentFragment.newInstance(data.getId(), contentTitle);

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                    transaction.replace(R.id.container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        isFavorites(data, holder);//set favorites icon
        holder.favoriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavoritesAfterClick(data);
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_animation));//onclick animation

            }
        });
    }

    private void logAnalytics(String contentType, Data data) {

        if (data.getLang_resource_name() != null) {
            DbHelper dbHelper = new DbHelper(context);
            Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (titleResource != null) {
                LogAnalyticsHelper analyticsHelper = new LogAnalyticsHelper(context);
                Bundle bundle = new Bundle();
                bundle.putString("Name", titleResource.getContent());
                bundle.putString("Language", Utility.getLanguageIdFromSharedPreferences(context) + "");
                analyticsHelper.logEvent(contentType, bundle);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    //set favorites
    private void setFavoritesAfterClick(Data data) {
        DbHelper dbHelper = new DbHelper(context);
        FavoritesData favoritesData = dbHelper.getFavoritesData(data.getId());
        FavoritesData favoData = new FavoritesData();
        if (favoritesData != null) {
            if (favoritesData.getFavoritesFlag().equals("1")) {
                favoData.setId(data.getId());
                favoData.setFavoritesFlag("0");
            } else {
                favoData.setId(data.getId());
                favoData.setFavoritesFlag("1");
            }
        } else {
            favoData.setId(data.getId());
            favoData.setFavoritesFlag("1");
        }
        dbHelper.upsertFavoritesData(favoData);
        notifyDataSetChanged();
    }

    //set favorites icon
    private void isFavorites(Data data, ChaptersViewHolder holder) {
        DbHelper dbHelper = new DbHelper(context);
        FavoritesData favoritesData = dbHelper.getFavoritesData(data.getId());
        if (favoritesData != null) {
            if (favoritesData.getFavoritesFlag().equals("1")) {
                // holder.starIcon.setText(Html.fromHtml("&#xf4ce;"));
                // holder.starIcon.setTextColor(Color.parseColor("#ffffff"));

                holder.starIcon.setImageResource(R.drawable.markedfav);
            } else {
                // holder.starIcon.setText(Html.fromHtml("&#xf4d2;"));
                // holder.starIcon.setTextColor(Color.parseColor("#e60000"));

                holder.starIcon.setImageResource(R.drawable.markfav);
            }
        } else {
            holder.starIcon.setImageResource(R.drawable.markfav);
            //  holder.starIcon.setText(Html.fromHtml("&#xf4d2;"));
            // holder.starIcon.setTextColor(Color.parseColor("#e60000"));

        }
    }
}

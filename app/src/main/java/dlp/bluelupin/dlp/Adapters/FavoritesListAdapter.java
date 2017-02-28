package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
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
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 8/4/2016.
 */
public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {

    private List<FavoritesData> favoritesList;
    private Context context;
    private Boolean favFlage = false;
    private String type;
    private CustomProgressDialog customProgressDialog;

    public FavoritesListAdapter(Context context, List<FavoritesData> favoritesData) {
        this.favoritesList = favoritesData;
        this.context = context;
        this.type = type;
        customProgressDialog = new CustomProgressDialog(context, R.mipmap.syc);
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_list_view_item, parent, false);
        return new FavoritesViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(FavoritesViewHolder holder, final int position) {
        Typeface VodafoneExB = FontManager.getFontTypeface(context, "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        Typeface materialdesignicons_font = FontManager.getFontTypeface(context, "fonts/materialdesignicons-webfont.otf");
        holder.chapterTitle.setTypeface(VodafoneExB);
        holder.chapterDescription.setTypeface(VodafoneRg);
        holder.starIcon.setTypeface(materialdesignicons_font);

        final DbHelper dbHelper = new DbHelper(context);
        final FavoritesData data = favoritesList.get(position);
        if (data.getLang_resource_name() != null) {
            Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (titleResource != null) {
                holder.chapterTitle.setText(Html.fromHtml(titleResource.getContent()));
            }
        }

        if (data.getLang_resource_description() != null) {
            Data descriptionResource = dbHelper.getResourceEntityByName(data.getLang_resource_description(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (descriptionResource != null) {
                holder.chapterDescription.setText(Html.fromHtml(descriptionResource.getContent()));
            }
        }

        if (data.getThumbnail_media_id() != 0) {
            Data media = dbHelper.getMediaEntityByIdAndLaunguageId(data.getThumbnail_media_id(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (media != null && media.getDownload_url() != null) {
                if (media.getLocalFilePath() == null) {
                    //holder.chapterImage.
                    /*if (Utility.isOnline(context)) {
                        new DownloadImageTask(holder.chapterImage, customProgressDialog)
                                .execute(media.getUrl());
                    }*/
                    if (Utility.isOnline(context)) {
                        Gson gson = new Gson();
                        Intent intent = new Intent(context, DownloadService1.class);
                        String strJsonmedia = gson.toJson(media);
                        intent.putExtra(Consts.EXTRA_MEDIA, strJsonmedia);
                        intent.putExtra(Consts.EXTRA_URLPropertyForDownload, Consts.DOWNLOAD_URL);
                        context.startService(intent);
                        new DownloadImageTask(holder.chapterImage, customProgressDialog)
                                .execute(media.getDownload_url());
                    }
                } else {
                  /*  File imgFile = new File(media.getLocalFilePath());
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        holder.chapterImage.setImageBitmap(bitmap);
                    }*/
                    Uri uri = Uri.fromFile(new File(media.getLocalFilePath()));
                    if (uri != null) {
                        Picasso.with(context).load(uri).placeholder(R.drawable.imageplaceholder).into(holder.chapterImage);
                    }
                }
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = dbHelper.getTypeOfChildren(data.getId());
                if (Consts.IS_DEBUG_LOG)
                    Log.d(Consts.LOG_TAG, "Navigating to  data_item id: " + data.getId() + " type: " + type);
                if (type.equalsIgnoreCase(Consts.COURSE) || type.equalsIgnoreCase(Consts.CHAPTER) || type.equalsIgnoreCase(Consts.TOPIC)) {
                    FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                    ChaptersFragment fragment = ChaptersFragment.newInstance(data.getId(), type);

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                    transaction.replace(R.id.container, fragment)
                            //.addToBackStack(null)
                            .commit();
                } else {
                    FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                    ContentFragment fragment = ContentFragment.newInstance(data.getId(), "");

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                    transaction.replace(R.id.container, fragment)
                            //.addToBackStack(null)
                            .commit();
                }
            }
        });

        //if meadia not downloaded then show download_layout
        if (data.getThumbnail_media_id() != 0) {
            final Data media = dbHelper.getDownloadMediaEntityById(data.getThumbnail_media_id());
            if (media != null) {
                holder.download_layout.setVisibility(View.VISIBLE);
                holder.downloadIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                        DownloadingFragment fragment = DownloadingFragment.newInstance(data.getThumbnail_media_id(), media.getUrl());
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                        transaction.replace(R.id.container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });

            } else {
                holder.download_layout.setVisibility(View.GONE);
            }
        }


        isFavorites(data, holder);//set favorites icon
        holder.favorite_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorites(data, position);
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_animation));//onclick animation

            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    //set favorites
    private void setFavorites(FavoritesData data, int position) {
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
        favoritesList.remove(position);//remove unfavourites item from list
        notifyDataSetChanged();
    }

    //set favorites icon
    private void isFavorites(FavoritesData data, FavoritesViewHolder holder) {
        DbHelper dbHelper = new DbHelper(context);
        FavoritesData favoritesData = dbHelper.getFavoritesData(data.getId());
        if (favoritesData != null) {
            if (favoritesData.getFavoritesFlag().equals("1")) {
                holder.starIcon.setText(Html.fromHtml("&#xf4ce;"));
                holder.starIcon.setTextColor(Color.parseColor("#ffffff"));
            } else {
                holder.starIcon.setText(Html.fromHtml("&#xf4d2;"));
                holder.starIcon.setTextColor(Color.parseColor("#ffffff"));
            }
        } else {
            holder.starIcon.setText(Html.fromHtml("&#xf4d2;"));
            holder.starIcon.setTextColor(Color.parseColor("#ffffff"));
        }
    }
}

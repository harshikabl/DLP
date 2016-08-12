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

import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.ChaptersFragment;
import dlp.bluelupin.dlp.Fragments.ContentFragment;
import dlp.bluelupin.dlp.Fragments.DownloadingFragment;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.FavoritesData;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 8/4/2016.
 */
public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {

    private List<FavoritesData> favoritesList;
    private Context context;
    private Boolean favFlage = false;
    private String type;

    public FavoritesListAdapter(Context context, List<FavoritesData> favoritesData) {
        this.favoritesList = favoritesData;
        this.context = context;
        this.type = type;
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_list_view_item, parent, false);
        return new FavoritesViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(FavoritesViewHolder holder, final int position) {
        Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneRg.ttf");
        holder.chapterTitle.setTypeface(VodafoneExB);
        holder.chapterDescription.setTypeface(VodafoneRg);

        Typeface materialdesignicons_font = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.otf");
        holder.starIcon.setTypeface(materialdesignicons_font);
        holder.starIcon.setText(Html.fromHtml("&#xf4ce;"));
        holder.favorite.setTypeface(VodafoneExB);
        holder.download.setTypeface(VodafoneExB);
        holder.downloadIcon.setTypeface(materialdesignicons_font);
        holder.downloadIcon.setText(Html.fromHtml("&#xf1da;"));


        final DbHelper dbHelper = new DbHelper(context);
        final FavoritesData data = favoritesList.get(position);
        if (data.getLang_resource_name() != null) {
            Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context).ordinal());
            if (titleResource != null) {
                holder.chapterTitle.setText(titleResource.getContent());
            }
        }

        if (data.getLang_resource_description() != null) {
            Data descriptionResource = dbHelper.getResourceEntityByName(data.getLang_resource_description(),
                    Utility.getLanguageIdFromSharedPreferences(context).ordinal());
            if (descriptionResource != null) {
                holder.chapterDescription.setText(descriptionResource.getContent());
            }
        }

        if (data.getThumbnail_media_id() != 0) {
            Data media = dbHelper.getMediaEntityById(data.getThumbnail_media_id());
            if (media != null) {
                //holder.chapterImage.
                new DownloadImageTask(holder.chapterImage)
                        .execute(media.getUrl());
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = dbHelper.getTypeOfChildren(data.getId());
                if (Consts.IS_DEBUG_LOG)
                    Log.d(Consts.LOG_TAG, "Navigating to  data id: " + data.getId() + " type: " + type);
                if (type.equalsIgnoreCase(Consts.COURSE) || type.equalsIgnoreCase(Consts.CHAPTER) || type.equalsIgnoreCase(Consts.TOPIC)) {
                    FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                    ChaptersFragment fragment = ChaptersFragment.newInstance(data.getId(), type);

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                    transaction.replace(R.id.container, fragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                    ContentFragment fragment = ContentFragment.newInstance(data.getId(), "");

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                    transaction.replace(R.id.container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });


        holder.downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                DownloadingFragment fragment = DownloadingFragment.newInstance("", "");
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
                transaction.replace(R.id.downloadContainer, fragment)
                        //.addToBackStack(null)
                        .commit();
            }
        });


        isFavorites(data, holder);//set favorites icon
        holder.starIcon.setOnClickListener(new View.OnClickListener() {
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
                holder.starIcon.setText(Html.fromHtml("&#xf4d2;"));
                holder.starIcon.setTextColor(Color.parseColor("#e60000"));
            } else {
                holder.starIcon.setText(Html.fromHtml("&#xf4ce;"));
                holder.starIcon.setTextColor(Color.parseColor("#000000"));
            }
        } else {
            holder.starIcon.setText(Html.fromHtml("&#xf4ce;"));
            holder.starIcon.setTextColor(Color.parseColor("#000000"));
        }
    }
}

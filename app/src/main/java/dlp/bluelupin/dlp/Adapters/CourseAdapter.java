package dlp.bluelupin.dlp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.ChaptersFragment;
import dlp.bluelupin.dlp.Fragments.CourseFragment;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 7/26/2016.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseViewHolder> {

    private List<Data> itemList;
    private Context context;

    public CourseAdapter(Context context, List<Data> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_view_item, parent, false);
        return new CourseViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final CourseViewHolder holder, int position) {
        Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneRg.ttf");
        holder.courseTitle.setTypeface(VodafoneExB);
        holder.courseDescription.setTypeface(VodafoneRg);

        final DbHelper dbHelper = new DbHelper(context);
        final Data data = itemList.get(position);
        if(data.getLang_resource_name() != null) {
            Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context).ordinal());
            if (titleResource != null) {
                holder.courseTitle.setText(titleResource.getContent());
            }
        }

        if(data.getLang_resource_description() != null) {
            Data descriptionResource = dbHelper.getResourceEntityByName(data.getLang_resource_description(),
                    Utility.getLanguageIdFromSharedPreferences(context).ordinal());
            if (descriptionResource != null) {
                holder.courseTitle.setText(descriptionResource.getContent());
            }
        }

        if(data.getThumbnail_media_id() != 0)
        {
            Data media = dbHelper.getMediaEntityById(data.getThumbnail_media_id());
            if(media != null) {
                //holder.chapterImage.
                new DownloadImageTask(holder.courseImage)
                        .execute(media.getUrl());
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                String type = dbHelper.getTypeOfChildren(data.getId());
                if(Consts.IS_DEBUG_LOG)
                    Log.d(Consts.LOG_TAG, "the type for data is: " + data.getId() + " type: " + type);

                ChaptersFragment fragment = ChaptersFragment.newInstance(data.getId(), type);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                transaction.replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

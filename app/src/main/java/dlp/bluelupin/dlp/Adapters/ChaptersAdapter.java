package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import java.util.List;

import dlp.bluelupin.dlp.Fragments.ChaptersFragment;
import dlp.bluelupin.dlp.Fragments.ContentFragment;
import dlp.bluelupin.dlp.Fragments.DownloadingFragment;
import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 7/26/2016.
 */
public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersViewHolder> {

    private List<String> itemList;
    private Context context;
    private Boolean favFlage = false;

    public ChaptersAdapter(Context context, List<String> itemList) {
        this.itemList = itemList;
        this.context = context;
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
        holder.courseType.setTypeface(VodafoneExB);
        holder.courseDetails.setTypeface(VodafoneRg);
        holder.courseDetails.setText(itemList.get(position).toString());
        Typeface materialdesignicons_font = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.otf");
        holder.starIcon.setTypeface(materialdesignicons_font);
        holder.starIcon.setText(Html.fromHtml("&#xf4ce;"));
        holder.favorite.setTypeface(VodafoneExB);
        holder.download.setTypeface(VodafoneExB);
        holder.downloadIcon.setTypeface(materialdesignicons_font);
        holder.downloadIcon.setText(Html.fromHtml("&#xf1da;"));
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
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                ContentFragment fragment = ContentFragment.newInstance("", "");
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                transaction.replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        if (favFlage) {
            holder.starIcon.setText(Html.fromHtml("&#xf4d2;"));
            holder.starIcon.setTextColor(Color.parseColor("#e60000"));
        } else {
            holder.starIcon.setText(Html.fromHtml("&#xf4ce;"));
            holder.starIcon.setTextColor(Color.parseColor("#ffffff"));
        }
        holder.starIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favFlage) {
                    favFlage = false;

                } else {
                    favFlage = true;
                }
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_animation));//onclick animation
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.style.LineHeightSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dlp.bluelupin.dlp.Activities.VideoPlayerActivity;
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
        Data resource = null;
        if (data.getLang_resource_name() != null) {

            resource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context).ordinal());
            if (resource != null) {

                if (data.getType().equalsIgnoreCase("Text")) {
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, " resource text: " + resource.getContent());
                    }
                    TextView dynamicTextView = new TextView(context);
                    dynamicTextView.setTypeface(VodafoneRg);
                    dynamicTextView.setText(Html.fromHtml(resource.getContent()));

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(0, 10, 0, 10);
                    dynamicTextView.setLayoutParams(layoutParams);
                    holder.contentContainer.addView(dynamicTextView);
                }
            }
        }
        if (data.getType().equalsIgnoreCase("Image")) {
            if (data.getMedia_id() != 0) {
                Data media = dbHelper.getMediaEntityById(data.getMedia_id());
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "Media id" + media.getId() + " Image Url: " + media.getUrl());
                }
                if (media != null) {
                    String titleText = null;
                    if (resource != null) {
                        titleText = resource.getContent();
                    }
                    FrameLayout imageContainer = makeImage(media.getUrl(), titleText);
                    holder.contentContainer.addView(imageContainer);
                }

            }
        }

        if (data.getType().equalsIgnoreCase("video")) {
            if (data.getMedia_id() != 0) {
                Data media = dbHelper.getMediaEntityById(data.getMedia_id());
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "Media id" + media.getId() + " video Url: " + media.getUrl());
                }
                if (media != null) {
                    String titleText = null;
                    if (resource != null) {
                        titleText = resource.getContent();
                    }
                    if (data.getThumbnail_media_id() != 0) {
                        Data videoThumbnail = dbHelper.getMediaEntityById(data.getThumbnail_media_id());
                        FrameLayout imageContainer = makeImage(videoThumbnail.getUrl(), titleText);
                        holder.contentContainer.addView(imageContainer);
                    }
                }

            }
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getType().equalsIgnoreCase("video")) {
                    if (data.getMedia_id() != 0) {
                        Data media = dbHelper.getMediaEntityById(data.getMedia_id());
                        navigateBasedOnMediaType(media);
                    }
                }
            }
        });
    }

    private void navigateBasedOnMediaType(Data media) {
        String url;
        switch (media.getType()) {
            case "video":
                url = media.getUrl();
                if (url != null && !url.equals("")) {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("videoUrl", url);
                    context.startActivity(intent);
                }
                break;
            case "Youtube":
                url = media.getUrl();
                if (url != null && !url.equals("")) {
                   /* Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("videoUrl", url);
                    context.startActivity(intent);*/
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                break;

        }
    }

    private FrameLayout makeImage(String url, String titleText) {
        FrameLayout frameLayout = new FrameLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, 10, 0, 10);
        frameLayout.setLayoutParams(layoutParams);
        ImageView dynamicImageView = new ImageView(context);
        dynamicImageView.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        new DownloadImageTask(dynamicImageView)
                .execute(url);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setVerticalGravity(Gravity.BOTTOM);
        linearLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_black));//;background="@drawable/gradient_black"

        TextView dynamicTextView = new TextView(context);
        if (titleText != null) {
            dynamicTextView.setText(Html.fromHtml(titleText));
        }

        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewParams.gravity = Gravity.BOTTOM;
        //textViewParams.setMargins(0,10,0,10);
        dynamicTextView.setLayoutParams(textViewParams);

        linearLayout.addView(dynamicTextView);

        frameLayout.addView(dynamicImageView);
        frameLayout.addView(linearLayout);
        return frameLayout;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

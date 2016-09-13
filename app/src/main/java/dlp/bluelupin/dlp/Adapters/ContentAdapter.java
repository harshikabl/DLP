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
import android.text.style.LineHeightSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import dlp.bluelupin.dlp.Activities.VideoPlayerActivity;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.ContentFragment;
import dlp.bluelupin.dlp.Fragments.DownloadingFragment;
import dlp.bluelupin.dlp.Fragments.WebFragment;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.DownloadService1;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 7/27/2016.
 */
public class ContentAdapter extends BaseAdapter {
    private List<Data> itemList;
    private Context context;
    private Boolean favFlage = false;
    LayoutInflater inflater;

    public ContentAdapter(Context context, List<Data> itemList) {
        this.itemList = itemList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.content_list_view_item, null);
            holder.contentContainer = (LinearLayout) convertView.findViewById(R.id.contentContainer);
            holder.playIcon = (TextView) convertView.findViewById(R.id.playIcon);
            Typeface materialdesignicons_font = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.otf");
            holder.playIcon.setTypeface(materialdesignicons_font);
            holder.cardView = (android.support.v7.widget.CardView) convertView.findViewById(R.id.card_view);
            holder.cardView.setCardElevation(2);
            holder.cardView.setRadius(10);


            Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
            Typeface VodafoneRg = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneRg.ttf");

            final DbHelper dbHelper = new DbHelper(context);
            final Data data = itemList.get(position);
            Log.d(Consts.LOG_TAG, " view create********************* " + data.getId());

            if (Consts.IS_DEBUG_LOG)
                Log.d(Consts.LOG_TAG, " data id: " + data.getId() + " type: " + data.getType());
            Data resource = null;
            if (data.getLang_resource_name() != null) {

                resource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                        Utility.getLanguageIdFromSharedPreferences(context));
                if (resource != null) {

                    if (data.getType().equalsIgnoreCase("Text")) {
                        if (Consts.IS_DEBUG_LOG) {
                            Log.d(Consts.LOG_TAG, " resource text: " + resource.getContent());
                        }
                        TextView dynamicTextView = new TextView(context);
                        dynamicTextView.setTextSize(18);
                        dynamicTextView.setTypeface(VodafoneRg);
                        dynamicTextView.setText(Html.fromHtml(resource.getContent()));

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.setMargins(10, 10, 10, 10);
                        dynamicTextView.setLayoutParams(layoutParams);
                        holder.contentContainer.addView(dynamicTextView);
                    }
                }
            }
            if (data.getType().equalsIgnoreCase("Image")) {
                if (data.getMedia_id() != 0) {
                    Data media = dbHelper.getMediaEntityByIdAndLaunguageId(data.getMedia_id(),
                            Utility.getLanguageIdFromSharedPreferences(context));

                    if (media != null) {
                        if (Consts.IS_DEBUG_LOG) {
                            //  Log.d(Consts.LOG_TAG, "Media id " + media.getId() + " Image Url: " + media.getUrl());
                        }
                        String titleText = null;
                        if (resource != null) {
                            titleText = resource.getContent();
                        }
                        FrameLayout imageContainer = makeImage(media, titleText);
                        if (imageContainer != null) {
                            holder.contentContainer.addView(imageContainer);
                        }
                    }

                }
            }

            if (data.getType().equalsIgnoreCase("video")) {
                if (data.getMedia_id() != 0) {
                    Data media = dbHelper.getMediaEntityByIdAndLaunguageId(data.getMedia_id(),
                            Utility.getLanguageIdFromSharedPreferences(context));
                    if (media != null) {

                        String titleText = null;
                        if (resource != null) {
                            titleText = resource.getContent();
                        }
                        Data videoThumbnail = dbHelper.getMediaEntityById(data.getMedia_id());

                        FrameLayout imageContainer = makeThumbnailImage(videoThumbnail, titleText);
                        if (imageContainer != null) {
                            holder.contentContainer.addView(imageContainer);
                            holder.playIcon.setText(Html.fromHtml("&#xf40d;"));
                        }
                    }

                }
            }

            if (data.getType().equalsIgnoreCase("Url")) {
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, " Url resource text: " + resource.getContent());
                }
                TextView dynamicTextView = new TextView(context);
                dynamicTextView.setTextSize(18);
                dynamicTextView.setTypeface(VodafoneRg);
                dynamicTextView.setText(Html.fromHtml(resource.getContent()));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(10, 10, 10, 10);
                dynamicTextView.setLayoutParams(layoutParams);
                holder.contentContainer.addView(dynamicTextView);
            }

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getType().equalsIgnoreCase("video")) {
                        if (data.getMedia_id() != 0) {
                            // Data media = dbHelper.getMediaEntityById(data.getMedia_id());

                            Data media = dbHelper.getMediaEntityByIdAndLaunguageId(data.getMedia_id(),
                                    Utility.getLanguageIdFromSharedPreferences(context));
                            if (media != null) {
                                // get media url based on language
                                Data mediaLanguage = dbHelper.getMedialanguageLatestDataEntityByMediaId(media.getId(),
                                        Utility.getLanguageIdFromSharedPreferences(context));
                                if (mediaLanguage != null) {
                                    mediaLanguage.setType(media.getType());
                                    media = mediaLanguage;

                                    if (Consts.IS_DEBUG_LOG) {
                                        Log.d(Consts.LOG_TAG, "mediaLanguage id" + mediaLanguage.getId() + " video Url: " + mediaLanguage.getUrl() + "language Id" + mediaLanguage.getLanguage_id());
                                    }
                                }
                                if (Consts.IS_DEBUG_LOG) {
                                    Log.d(Consts.LOG_TAG, "Media id" + media.getId() + " video Url: " + media.getUrl());
                                }

                                navigateBasedOnMediaType(media);
                            }
                        }
                    } else if (data.getType().equalsIgnoreCase("Url")) {
                        if (data.getUrl() != null) {
                            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                            WebFragment fragment = WebFragment.newInstance(data.getUrl(), "");
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                            transaction.replace(R.id.container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                }
            });
            //convertView.setTag(data.getId());
        } else {
            // holder = (ViewHolder) convertView.getTag();
            return convertView;
        }
        return convertView;
    }


    private void navigateBasedOnMediaType(Data media) {
        String url;
        if (media.getType() != null) {
            switch (media.getType()) {
                case "Video":
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
    }

    private FrameLayout makeImage(Data media, String titleText) {
        FrameLayout frameLayout = new FrameLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        layoutParams.setMargins(0, 0, 0, 0);
        frameLayout.setLayoutParams(layoutParams);
        ImageView dynamicImageView = new ImageView(context);
        dynamicImageView.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dynamicImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        if (url != null) {
//            new DownloadImageTask(dynamicImageView)
//                    .execute(url);
//        }
        if (media != null && media.getDownload_url() != null) {
            if (media.getLocalFilePath() == null) {

                Gson gson = new Gson();
                Intent intent = new Intent(context, DownloadService1.class);
                String strJsonmedia = gson.toJson(media);
                intent.putExtra(Consts.EXTRA_MEDIA, strJsonmedia);
                intent.putExtra(Consts.EXTRA_URLPropertyForDownload, Consts.DOWNLOAD_URL);
                context.startService(intent);
                new DownloadImageTask(dynamicImageView)
                        .execute(media.getDownload_url());
            } else {
                File imgFile = new File(media.getLocalFilePath());
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    dynamicImageView.setImageBitmap(bitmap);
                }
            }

        }

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setVerticalGravity(Gravity.BOTTOM);
        linearLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_black));//;background="@drawable/gradient_black"

        TextView dynamicTextView = new TextView(context);
        dynamicTextView.setTextSize(18);
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

    private FrameLayout makeThumbnailImage(Data media, String titleText) {

        FrameLayout frameLayout = new FrameLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        layoutParams.setMargins(0, 0, 0, 0);
        frameLayout.setLayoutParams(layoutParams);
        ImageView dynamicImageView = new ImageView(context);
        dynamicImageView.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dynamicImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        if (url != null) {
//            new DownloadImageTask(dynamicImageView)
//                    .execute(url);
//        }

        if (media != null && media.getThumbnail_url() != null) {
            if (media.getThumbnail_url_Local_file_path() == null) {

                Gson gson = new Gson();
                Intent intent = new Intent(context, DownloadService1.class);
                String strJsonmedia = gson.toJson(media);
                intent.putExtra(Consts.EXTRA_MEDIA, strJsonmedia);
                intent.putExtra(Consts.EXTRA_URLPropertyForDownload, Consts.THUMBNAIL_URL);
                context.startService(intent);
                new DownloadImageTask(dynamicImageView)
                        .execute(media.getThumbnail_url());
            } else {
                File imgFile = new File(media.getThumbnail_url_Local_file_path());
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    dynamicImageView.setImageBitmap(bitmap);
                }
            }

        }


        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setVerticalGravity(Gravity.BOTTOM);
        linearLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_black));//;background="@drawable/gradient_black"

        TextView dynamicTextView = new TextView(context);
        dynamicTextView.setTextSize(18);
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
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ViewHolder {
        public android.support.v7.widget.CardView cardView;
        //public TextView contentTitle, contentDescription;
        public LinearLayout contentContainer;
        public TextView playIcon;

    }
}

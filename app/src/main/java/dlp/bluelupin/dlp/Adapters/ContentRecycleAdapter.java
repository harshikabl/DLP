package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import dlp.bluelupin.dlp.Activities.VideoPlayerActivity;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.WebFragment;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.SimulatorData;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.DownloadService1;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.DecompressZipFile;
import dlp.bluelupin.dlp.Utilities.DownloadFileAsync;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.LogAnalyticsHelper;
import dlp.bluelupin.dlp.Utilities.ScaleImageView;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by subod on 13-Sep-16.
 */
public class ContentRecycleAdapter extends RecyclerView.Adapter<ContentViewHolder> {
    private List<Data> itemList;
    private Context context;
    private Boolean favFlage = false;
    Typeface VodafoneExB;// = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
    Typeface VodafoneRg;// = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneRg.ttf");
    Typeface materialdesignicons_font;// = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.otf");
    private CustomProgressDialog customProgressDialog;
    private String contentTitle;
    LogAnalyticsHelper analyticsHelper = null;

    public ContentRecycleAdapter(Context context, List<Data> itemList) {
        this.itemList = itemList;
        this.context = context;
        VodafoneExB = FontManager.getFontTypeface(context, "fonts/VodafoneExB.TTF");
        VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");
        materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");
        customProgressDialog = new CustomProgressDialog(context, R.mipmap.syc);
    }


    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_list_view_item, parent, false);
        ContentViewHolder contentViewHolder = new ContentViewHolder(layoutView);
        contentViewHolder.playIcon.setTypeface(materialdesignicons_font);
        contentViewHolder.setIsRecyclable(true);
        return contentViewHolder;
    }

    @Override
    public long getItemId(int position) {
        //return super.getItemId(position);
        return itemList.get(position).getId();
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {

        analyticsHelper = new LogAnalyticsHelper(context);
        final DbHelper dbHelper = new DbHelper(context);
        final Data data = itemList.get(position);
        holder.contentContainer.removeAllViews();

        if (Consts.IS_DEBUG_LOG)
            Log.d(Consts.LOG_TAG, " data_item id: " + data.getId() + " type: " + data.getType());
        Data resource = null;
        if (data.getLang_resource_name() != null) {
            resource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (resource != null) {
                if (data.getType().equalsIgnoreCase("Text")) {
                    //  if(holder.getItemId() == )
                    View view = holder.contentContainer.findViewById(data.getId());
                    if (view == null) {
                        view = addDynamicTextView(holder, resource);
                        view.setId(resource.getId());
                        view.setTag(resource.getId());
                        holder.contentContainer.addView(view);
                    }
                }
            } //resource != null
        } //data_item.getLang_resource_name() != null
        if (data.getLang_resource_description() != null) {
            resource = dbHelper.getResourceEntityByName(data.getLang_resource_description(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (resource != null) {
                if (data.getType().equalsIgnoreCase("Text")) {
                    //  if(holder.getItemId() == )
                    View view = holder.contentContainer.findViewById(resource.getId());
                    if (view == null) {
                        view = addDescriptionDynamicTextView(holder, resource);
                        view.setId(resource.getId());
                        view.setTag(resource.getId());
                        holder.contentContainer.addView(view);
                    }
                }
            } //resource != null
        } //data_item.getLang_resource_name() != null
        if (data.getType().equalsIgnoreCase("Image")) {
            addDynamicImageView(holder, data, resource);
        }
        if (data.getType().equalsIgnoreCase("Video")) {
            holder.playIcon.setVisibility(View.VISIBLE);
            addDynamicVideo(holder, data, resource);
        } else {
            holder.playIcon.setVisibility(View.GONE);
        }

        if (data.getType().equalsIgnoreCase("Url")) {
            addDynamicUrl(holder, resource);
        }

        if (data.getType().equalsIgnoreCase("Simulator")) {
            addSimulator(holder, data, resource);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getType().equalsIgnoreCase("video")) {
                    playVideoOnSelect(data);
                } else if (data.getType().equalsIgnoreCase("Url")) {
                    openUrlOnSelect(data);
                } else if (data.getType().equalsIgnoreCase("Simulator")) {
                    openSimulatorFile(data);
                }
            }
        });

        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, " returning NEW convertview with position: " + position + ", data_item: " + itemList.get(position));
        }
        // }
    }

    private void playVideoOnSelect(Data data) {
        if (data.getMedia_id() != 0) {
            final DbHelper dbHelper = new DbHelper(context);
            Data media = dbHelper.getMediaEntityByIdAndLaunguageId(data.getMedia_id(),
                    Utility.getLanguageIdFromSharedPreferences(context));

            if (media != null) {
                analyticsHelper.logEvent(media.getFile_path() + " Seen", null);
                logAnalytics("Video", media);
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "Media id" + media.getId() + " video Url: " + media.getUrl());
                }
                navigateBasedOnMediaType(media);
            }
        }
    }

    private void openUrlOnSelect(Data data) {
        if (data.getUrl() != null) {
            final DbHelper dbHelper = new DbHelper(context);
            if (data.getLang_resource_name() != null) {
                Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                        Utility.getLanguageIdFromSharedPreferences(context));
                if (titleResource != null) {
                    contentTitle = titleResource.getContent();
                    analyticsHelper.logEvent(titleResource.getContent() + " Visited", null);
                }
            }
            logAnalytics("Url", data);

            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            WebFragment fragment = WebFragment.newInstance(data.getUrl(), contentTitle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
            transaction.replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void addDynamicUrl(ContentViewHolder holder, Data resource) {
//        if (Consts.IS_DEBUG_LOG) {
//            Log.d(Consts.LOG_TAG, " Url resource text: " + resource.getContent());
//        }
        TextView dynamicTextView = new TextView(context);
        dynamicTextView.setTextSize(18);
        dynamicTextView.setTypeface(VodafoneRg);
        if (resource != null) {
            dynamicTextView.setText(Html.fromHtml(resource.getContent()));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(10, 10, 10, 10);
        dynamicTextView.setLayoutParams(layoutParams);
        holder.contentContainer.addView(dynamicTextView);
    }

    private void addDynamicVideo(ContentViewHolder holder, Data data, Data resource) {
        if (data.getMedia_id() != 0) {
            final DbHelper dbHelper = new DbHelper(context);
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


    private void addDynamicImageView(ContentViewHolder holder, Data data, Data resource) {

        if (data.getMedia_id() != 0) {
            final DbHelper dbHelper = new DbHelper(context);
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

    private View addDynamicTextView(ContentViewHolder holder, Data resource) {
//        if (Consts.IS_DEBUG_LOG) {
//            Log.d(Consts.LOG_TAG, " resource text: " + resource.getContent());
//        }
        TextView dynamicTextView = new TextView(context);
        dynamicTextView.setTextSize(22);
        dynamicTextView.setTypeface(VodafoneRg, Typeface.BOLD);

        dynamicTextView.setText(Html.fromHtml(resource.getContent()));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(10, 10, 10, 10);
        dynamicTextView.setLayoutParams(layoutParams);

        return dynamicTextView;
    }

    private View addDescriptionDynamicTextView(ContentViewHolder holder, Data resource) {
//        if (Consts.IS_DEBUG_LOG) {
//            Log.d(Consts.LOG_TAG, " resource text: " + resource.getContent());
//        }
        TextView dynamicTextView = new TextView(context);
        dynamicTextView.setTextSize(18);
        dynamicTextView.setTypeface(VodafoneRg);
        dynamicTextView.setLineSpacing(1.1f, 1.1f);
        dynamicTextView.setText(Html.fromHtml(resource.getContent()));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(10, 10, 10, 10);
        dynamicTextView.setLayoutParams(layoutParams);

        return dynamicTextView;
    }

    private FrameLayout makeImage(Data media, String titleText) {
        FrameLayout frameLayout = new FrameLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        layoutParams.setMargins(0, 0, 0, 0);
        frameLayout.setLayoutParams(layoutParams);
        //ImageView dynamicImageView = new ImageView(context);
        ScaleImageView dynamicImageView = new ScaleImageView(context);
        dynamicImageView.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        // dynamicImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        if (url != null) {
//            new DownloadImageTask(dynamicImageView)
//                    .execute(url);
//        }
        if (media != null && media.getDownload_url() != null) {
            if (media.getLocalFilePath() == null) {
                if (Utility.isOnline(context)) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, DownloadService1.class);
                    String strJsonmedia = gson.toJson(media);
                    intent.putExtra(Consts.EXTRA_MEDIA, strJsonmedia);
                    intent.putExtra(Consts.EXTRA_URLPropertyForDownload, Consts.DOWNLOAD_URL);
                    context.startService(intent);
                    new DownloadImageTask(dynamicImageView, customProgressDialog)
                            .execute(media.getDownload_url());
                }
            } else {
                /*File imgFile = new File(media.getLocalFilePath());
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    dynamicImageView.setImageBitmap(bitmap);
                }*/
                Uri uri = Uri.fromFile(new File(media.getLocalFilePath()));
                if (uri != null) {
                    Picasso.with(context).load(uri).into(dynamicImageView);
                }
            }
        }

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setVerticalGravity(Gravity.BOTTOM);
        //linearLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_black));//;background="@drawable/gradient_black"

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

    private void navigateBasedOnMediaType(Data media) {
        String url;
        if (media.getType() != null) {
            switch (media.getType()) {
                case "Video":
                    url = media.getLocalFilePath();
                    if (url != null && !url.equals("")) {
                        showOfflineVideo(media);
                    } else {
                        showOnlineVideo(media);
                    }
                    break;
                case "Youtube":
                    url = media.getLocalFilePath();
                    if (url != null && !url.equals("")) {
                        showOfflineVideo(media);
                    } else {
                        if (Utility.isOnline(context)) {
                            url = media.getUrl();
                            if (url != null && !url.equals("")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                intent.putExtra("force_fullscreen", true);
                                context.startActivity(intent);
                                Consts.playYouTubeFlag = false;//after device back press MainAtivity don't reload
                            }
                        } else {
                            Utility.alertForErrorMessage(context.getString(R.string.online_msg), context);
                        }
                    }
                    break;
            }
        }
    }

    private void showOfflineVideo(Data media) {
        String url;
        url = media.getLocalFilePath();
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra("videoUrl", url);
        context.startActivity(intent);
    }

    private void showOnlineVideo(Data media) {
        String url;
        if (Utility.isOnline(context)) {
            url = media.getUrl();
            if (url != null && !url.equals("")) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("videoUrl", url);
                context.startActivity(intent);
            }
        }
    }


    private FrameLayout makeThumbnailImage(Data media, String titleText) {

        FrameLayout frameLayout = new FrameLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        layoutParams.setMargins(0, 0, 0, 0);
        frameLayout.setLayoutParams(layoutParams);
        //ImageView dynamicImageView = new ImageView(context);
        ScaleImageView dynamicImageView = new ScaleImageView(context);
        dynamicImageView.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        //dynamicImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        if (url != null) {
//            new DownloadImageTask(dynamicImageView)
//                    .execute(url);
//        }

        if (media != null && media.getThumbnail_url() != null) {
            if (media.getThumbnail_url_Local_file_path() == null) {
                if (Utility.isOnline(context)) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, DownloadService1.class);
                    String strJsonmedia = null;
                    try {
                        strJsonmedia = gson.toJson(media);
                    } catch (Exception e) {
                        Log.d(Consts.LOG_TAG, "Error Message: " + e.getMessage());
                    }

                    intent.putExtra(Consts.EXTRA_MEDIA, strJsonmedia);
                    intent.putExtra(Consts.EXTRA_URLPropertyForDownload, Consts.THUMBNAIL_URL);
                    context.startService(intent);
                    new DownloadImageTask(dynamicImageView, customProgressDialog)
                            .execute(media.getThumbnail_url());
                }
            } else {
               /* File imgFile = new File(media.getThumbnail_url_Local_file_path());
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    dynamicImageView.setImageBitmap(bitmap);
                }*/
                Uri uri = Uri.fromFile(new File(media.getThumbnail_url_Local_file_path()));
                if (uri != null) {
                    Picasso.with(context).load(uri).into(dynamicImageView);
                }
            }
        }

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setVerticalGravity(Gravity.BOTTOM);
        //linearLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_black));//;background="@drawable/gradient_black"

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

        View backgroundLayer = new View(context);
        LinearLayout.LayoutParams backgroundLayerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        backgroundLayer.setLayoutParams(backgroundLayerParams);
        backgroundLayer.setBackgroundColor(Color.parseColor("#000000"));
        backgroundLayer.setAlpha(.3f);


        frameLayout.addView(dynamicImageView);
        frameLayout.addView(linearLayout);
        frameLayout.addView(backgroundLayer);
        return frameLayout;
    }

    //show SimulatorData
    private void addSimulator(ContentViewHolder holder, Data data, Data resource) {

        if (data.getMedia_id() != 0) {
            final DbHelper dbHelper = new DbHelper(context);
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
                LinearLayout mainLayout = new LinearLayout(context);
                mainLayout.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                mainLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout rightIconLayout = new LinearLayout(context);
                rightIconLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams rightIconParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rightIconLayout.setLayoutParams(rightIconParams);
                rightIconParams.width = 0;
                rightIconParams.weight = 2;
                TextView rightIconTextView = new TextView(context);
                rightIconTextView.setTextSize(25);
                rightIconTextView.setTypeface(materialdesignicons_font);
                rightIconTextView.setGravity(Gravity.CENTER);
                rightIconTextView.setText(Html.fromHtml("&#xf186;"));
                rightIconTextView.setBackgroundColor(Color.parseColor("#ac0000"));
                rightIconTextView.setTextColor(Color.parseColor("#ffffff"));
                rightIconTextView.setPadding(0, 30, 0, 30);
                rightIconLayout.addView(rightIconTextView);


                LinearLayout iconLayout = new LinearLayout(context);
                iconLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                iconLayout.setLayoutParams(iconParams);
                iconParams.width = 0;
                iconParams.weight = 2;
                TextView iconTextView = new TextView(context);
                iconTextView.setTextSize(25);
                iconTextView.setTypeface(materialdesignicons_font);
                iconTextView.setGravity(Gravity.CENTER);
                iconTextView.setText(Html.fromHtml("&#xf054;"));
                iconTextView.setBackgroundColor(Color.parseColor("#ac0000"));
                iconTextView.setTextColor(Color.parseColor("#ffffff"));
                iconTextView.setPadding(0, 30, 0, 30);
                iconLayout.addView(iconTextView);
                // LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //iconTextView.setLayoutParams(iconParams);

                LinearLayout textLayout = new LinearLayout(context);
                textLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textLayout.setLayoutParams(textParams);
                textParams.width = 0;
                textParams.weight = 6;
                TextView dynamicTextView = new TextView(context);
                dynamicTextView.setTextSize(22);
                dynamicTextView.setTypeface(VodafoneRg);
                dynamicTextView.setGravity(Gravity.LEFT);
                dynamicTextView.setText(Html.fromHtml(context.getString(R.string.Run_Simulator)));
                dynamicTextView.setBackgroundColor(Color.parseColor("#e60000"));
                dynamicTextView.setTextColor(Color.parseColor("#ffffff"));
                dynamicTextView.setPadding(16, 30, 0, 30);
                textLayout.addView(dynamicTextView);

                mainLayout.addView(rightIconLayout);
                mainLayout.addView(textLayout);
                mainLayout.addView(iconLayout);

                if (mainLayout != null) {
                    holder.contentContainer.addView(mainLayout);
                }
                populateSimulatorFile(media);
            }
        }
    }

    //extrac SimulatorData zipFile
    private void extracSimulatorzipFile(final String zipLocalPath, final Data media) {
        final CustomProgressDialog customProgressDialog = new CustomProgressDialog(context, R.mipmap.syc);
        customProgressDialog.show();
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                final DbHelper dbHelper = new DbHelper(context);
                final SimulatorData simulatorData = new SimulatorData();
                simulatorData.setId(media.getId());
                simulatorData.setParentId(media.getParent_id());
                simulatorData.setDownloadUrl(media.getDownload_url());
                simulatorData.setLanguageId(Utility.getLanguageIdFromSharedPreferences(context));
                simulatorData.setUrl(media.getUrl());

                // Determine if output path exists
                String strUnzipLocation = Consts.outputDirectoryLocation + "samvaadSimulator/" + media.getLocalFileName() + "/";
                File unzipLocation = new File(strUnzipLocation);
                if (!unzipLocation.exists()) {
                    unzipLocation.mkdirs();
                }
                DecompressZipFile decompressZipFile = new DecompressZipFile(context);
                String localFilePath = decompressZipFile.unzipSimulator(zipLocalPath, strUnzipLocation);
                simulatorData.setLocalPathUrl(strUnzipLocation);
                dbHelper.upsertSimulatorEntity(simulatorData);
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                customProgressDialog.dismiss();
            }
        }.execute();
    }

    //downloading Simulator zipFile
    private void downloadSimulatorzipFile(final Data media) {
        final CustomProgressDialog customProgressDialog = new CustomProgressDialog(context, R.mipmap.syc);
        if (Utility.isOnline(context)) {
            customProgressDialog.show();
            DownloadFileAsync task = new DownloadFileAsync(context, media) {
                @Override
                public void receiveData(String result) {
                    if (!result.equals("")) {
                        extracSimulatorzipFile(result, media);
                    }
                    customProgressDialog.dismiss();
                }
            };
            task.execute();
        }
    }

    // populate Simulator File
    private void populateSimulatorFile(Data media) {
        DbHelper dbHelper = new DbHelper(context);
        if (media != null && media.getDownload_url() != null) {
            SimulatorData simulatorData = dbHelper.getSimulatorEntityById(media.getId());
            if (simulatorData == null) {
                downloadSimulatorzipFile(media);
            }
        }
    }

    //open Simulator
    private void openSimulatorFile(Data data) {
        if (data.getMedia_id() != 0) {
            final DbHelper dbHelper = new DbHelper(context);
            Data media = dbHelper.getMediaEntityByIdAndLaunguageId(data.getMedia_id(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (media != null) {
                SimulatorData simulatorData = dbHelper.getSimulatorEntityById(media.getId());
                if (simulatorData != null) {
                    if (simulatorData.getLocalPathUrl() != null && !simulatorData.getLocalPathUrl().equals("")) {
                        String simulaterUrl = simulatorData.getLocalPathUrl() + "index.html";

                        String htmlFile = getDataFromDataBase(simulaterUrl);
                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                        WebFragment fragment = WebFragment.newInstance(simulatorData.getLocalPathUrl(), context.getString(R.string.Simulator));
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right);
                        transaction.replace(R.id.container, fragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        populateSimulatorFile(media);
                    }
                }
            }
        }
    }

    //get file as string from local
    public String getDataFromDataBase(String filePath) {
        StringBuilder text = new StringBuilder();
        File file = new File(filePath);
        //Read text from file
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }

    @Override
    public int getItemCount() {
        if (itemList.size() != 0) {
            return this.itemList.size();
        }
        return 0;
    }

    private void logAnalytics(String contentType, Data data) {

        if (data != null && data.getFile_path() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("Name", data.getFile_path());
            bundle.putString("Language", Utility.getLanguageIdFromSharedPreferences(context) + "");
            analyticsHelper.logEvent(contentType, bundle);
        }
    }
}

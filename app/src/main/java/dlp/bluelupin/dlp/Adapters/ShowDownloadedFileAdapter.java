package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.DownloadMediaWithParent;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.DownloadService1;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.DownloadImageTask;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 10/28/2016.
 */

public class ShowDownloadedFileAdapter extends RecyclerView.Adapter<ShowDownloadedFileViewHolder> {
    Context context;
    private List<DownloadMediaWithParent> itemList;
    private CustomProgressDialog customProgressDialog;

    public ShowDownloadedFileAdapter(Context context, List<DownloadMediaWithParent> itemList) {
        this.context = context;
        this.itemList = itemList;
        customProgressDialog = new CustomProgressDialog(context, R.mipmap.syc);
    }

    @Override
    public ShowDownloadedFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_downloaded_file_item, parent, false);
        return new ShowDownloadedFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShowDownloadedFileViewHolder holder, final int position) {




        Typeface materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = FontManager.getFontTypeface(context, "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = FontManager.getFontTypeface(context, "fonts/VodafoneRg.ttf");

        holder.mediaTitle.setTypeface(VodafoneExB);
        holder.mediaDescription.setTypeface(VodafoneRg);
        holder.cancelIcon.setTypeface(materialdesignicons_font);
        holder.cancelIcon.setText(Html.fromHtml("&#xf156;"));
        holder.cardView.setCardBackgroundColor(Color.parseColor("#EEEEEE"));
        final DbHelper dbHelper = new DbHelper(context);
        final DownloadMediaWithParent dataWithParent = itemList.get(position);
        final Data data = dbHelper.getDataEntityById(dataWithParent.getParentId());

        if (data != null) {

            final Data resource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                    Utility.getLanguageIdFromSharedPreferences(context));
            if (data.getLang_resource_name() != null) {
                Data titleResource = dbHelper.getResourceEntityByName(data.getLang_resource_name(),
                        Utility.getLanguageIdFromSharedPreferences(context));
                if (titleResource != null) {
                    holder.mediaTitle.setText(Html.fromHtml(titleResource.getContent()));
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
                   /* File imgFile = new File(media.getLocalFilePath());
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        holder.mediaImage.setImageBitmap(bitmap);
                    }*/
                    Uri uri = Uri.fromFile(new File(media.getLocalFilePath()));
                    if (uri != null) {
                        Picasso.with(context).load(uri).placeholder(R.drawable.imageplaceholder).into(holder.mediaImage);
                    }
                }

            }
        }
       /* ShowDownloadedMediaFileNameAdapter dataAdapter = new ShowDownloadedMediaFileNameAdapter(context, dataWithParent.getStrJsonResourcesToDownloadList());
        holder.mediaList.setAdapter(dataAdapter);
        Utility.justifyListViewHeightBasedOnChildrenForDisableScrool(holder.mediaList);
        dataAdapter.notifyDataSetChanged();*/

        holder.cancelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Data> dataList = dataWithParent.getStrJsonResourcesToDownloadList();
                if (dataList != null) {
                    for (Data data1 : dataList) {
                        UpdateMediaInDB(position, data1);
                        dbHelper.deleteFileDownloadedByMediaId(data1.getId());//delete media by id
                    }
                    itemList.remove(position);
                    notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void UpdateMediaInDB(int position, Data data1) {
        DbHelper dbHelper = new DbHelper(context);
        Data media = dbHelper.getMediaEntityByIdAndLaunguageId(data1.getId(), Utility.getLanguageIdFromSharedPreferences(context));
        if (media != null) {
            media.setLocalFilePath(null);
            // media.setThumbnail_url_Local_file_path(null);
            if (dbHelper.updateMediaLanguageLocalFilePathEntity(media)) {
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "successfully downloaded and THUMBNAIL local file updated: media Id:" + media.getId() + " downloading Url: " + media.getDownload_url() + " at " + media.getLocalFilePath());
                }
            }
        } else {
            Data mediaLanguage = dbHelper.getMedialanguageLatestDataEntityById(data1.getId());
            mediaLanguage.setLocalFilePath(null);
            // mediaLanguage.setThumbnail_url_Local_file_path(null);
            if (dbHelper.updateMediaLanguageLocalFilePathEntity(mediaLanguage)) {
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "successfully downloaded and local file updated: media Id:" + mediaLanguage.getId() + " downloading Url: " + mediaLanguage.getDownload_url() + " at " + mediaLanguage.getLocalFilePath());
                }
            }
        }
    }

}


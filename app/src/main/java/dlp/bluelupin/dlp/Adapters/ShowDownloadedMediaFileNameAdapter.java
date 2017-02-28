package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.DownloadService1;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.Utility;

import static android.R.attr.data;

/**
 * Created by Neeraj on 10/28/2016.
 */

public class ShowDownloadedMediaFileNameAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context mContext;
    List<Data> dataList;
    Typeface materialdesignicons_font;

    public ShowDownloadedMediaFileNameAdapter(Context context, List<Data> list) {
        this.mContext = context;
        this.dataList = list;
        inflater = LayoutInflater.from(mContext);


       materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(mContext, "fonts/materialdesignicons-webfont.otf");

    }

    @Override
    public int getCount() {
        if (dataList != null) {
            return dataList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ShowDownloadedMediaFileNameAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new ShowDownloadedMediaFileNameAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.show_downloded_file_name_item, null);
            holder.mediaName = (TextView) convertView.findViewById(R.id.mediaName);
            holder.cancelIcon = (TextView) convertView.findViewById(R.id.cancelIcon);
            holder.cancelIcon.setTypeface(materialdesignicons_font);
            holder.cancelIcon.setText(Html.fromHtml("&#xf156;"));
            convertView.setTag(holder);
        } else {
            holder = (ShowDownloadedMediaFileNameAdapter.ViewHolder) convertView.getTag();
        }
        holder.mediaName.setText(dataList.get(position).getLocalFileName());
        final DbHelper dbHelper = new DbHelper(mContext);
        holder.cancelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateMediaInDB(position);
                dbHelper.deleteFileDownloadedByMediaId(dataList.get(position).getId());//delete media by id
                dataList.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public class ViewHolder {
        TextView mediaName, cancelIcon;
    }

    private void UpdateMediaInDB(int position) {
        DbHelper dbHelper = new DbHelper(mContext);
        Data media = dbHelper.getMediaEntityByIdAndLaunguageId(dataList.get(position).getId(), Utility.getLanguageIdFromSharedPreferences(mContext));
        if (media != null) {
            media.setLocalFilePath(null);
            // media.setThumbnail_url_Local_file_path(null);
            if (dbHelper.updateMediaLanguageLocalFilePathEntity(media)) {
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "successfully downloaded and THUMBNAIL local file updated: media Id:" + media.getId() + " downloading Url: " + media.getDownload_url() + " at " + media.getLocalFilePath());
                }
            }
        } else {
            Data mediaLanguage = dbHelper.getMedialanguageLatestDataEntityById(dataList.get(position).getId());
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

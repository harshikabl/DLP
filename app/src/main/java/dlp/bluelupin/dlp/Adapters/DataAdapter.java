package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.LanguageData;
import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 9/28/2016.
 */
public class DataAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context mContext;
    List<Data> dataList;

    public DataAdapter(Context context, List<Data> list) {
        this.mContext = context;
        this.dataList = list;
        inflater = LayoutInflater.from(mContext);
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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.data_item, null);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.progressBar.setProgress(dataList.get(position).getProgress());


        return convertView;
    }

    public class ViewHolder {
        public ProgressBar progressBar;
    }
}

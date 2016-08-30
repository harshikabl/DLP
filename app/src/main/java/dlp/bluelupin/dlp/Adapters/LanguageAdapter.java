package dlp.bluelupin.dlp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import dlp.bluelupin.dlp.Models.LanguageData;
import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 7/22/2016.
 */
public class LanguageAdapter  extends BaseAdapter {

    LayoutInflater inflater;
    Context mContext;
    List<LanguageData> languageList;
    public LanguageAdapter(Context context, List<LanguageData> list) {
        this.mContext=context;
        this.languageList=list;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (languageList != null) {
            return languageList.size();
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
            convertView = inflater.inflate(R.layout.language_item, null);
            holder.language = (TextView) convertView.findViewById(R.id.language);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.language.setText(languageList.get(position).getName());

        return convertView;
    }

    public class ViewHolder {
        public TextView language;
    }
}

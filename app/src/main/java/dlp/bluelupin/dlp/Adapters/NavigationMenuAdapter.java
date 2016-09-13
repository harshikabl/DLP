package dlp.bluelupin.dlp.Adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;

import dlp.bluelupin.dlp.Activities.AccountSettingsActivity;
import dlp.bluelupin.dlp.Activities.LanguageActivity;
import dlp.bluelupin.dlp.Activities.NotificationsActivity;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Fragments.AboutUsFragment;
import dlp.bluelupin.dlp.Fragments.CourseFragment;
import dlp.bluelupin.dlp.Fragments.DownloadingFragment;
import dlp.bluelupin.dlp.Fragments.FavoritesFragment;
import dlp.bluelupin.dlp.Fragments.SelectLocationFragment;
import dlp.bluelupin.dlp.Fragments.TermsOfUseFragment;
import dlp.bluelupin.dlp.Fragments.UserProfileFragment;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.CardReaderHelper;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 9/2/2016.
 */
public class NavigationMenuAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<String> menuList = null;
    private List<String> iconList = null;
    HashSet<Integer> selectedPosition = new HashSet<>();

    public NavigationMenuAdapter(Context context,
                                 List<String> menuList, List<String> iconList) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.menuList = menuList;
        this.iconList = iconList;
    }

    @Override
    public int getCount() {
        if (menuList != null) {
            return menuList.size();
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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.menu_item, null);
            holder.menuTitel = (TextView) convertView.findViewById(R.id.menuTitel);
            holder.menuIcon = (TextView) convertView.findViewById(R.id.menuIcon);
            holder.menuItemLayout = (LinearLayout) convertView.findViewById(R.id.menuItemLayout);
            Typeface materialdesignicons_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/materialdesignicons-webfont.otf");
            Typeface custom_fontawesome = Typeface.createFromAsset(mContext.getAssets(), "fonts/fontawesome-webfont.ttf");
            Typeface VodafoneRgBd = Typeface.createFromAsset(mContext.getAssets(), "fonts/VodafoneRgBd.ttf");
            Typeface VodafoneRg = Typeface.createFromAsset(mContext.getAssets(), "fonts/VodafoneRg.ttf");
            holder.menuTitel.setTypeface(VodafoneRg);
            holder.menuIcon.setTypeface(materialdesignicons_font);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.menuTitel.setTag(position);
        holder.menuTitel.setText(menuList.get(position).toString());
        holder.menuIcon.setText(Html.fromHtml("&#x" + iconList.get(position).toString() + ";"));
//----------fill selected value------
        if (selectedPosition.contains(position)) {
            holder.menuItemLayout.setBackgroundColor(Color.parseColor("#e60000"));
            holder.menuTitel.setTextColor(Color.WHITE);
            holder.menuIcon.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.menuItemLayout.setBackgroundColor(Color.WHITE);
            holder.menuIcon.setTextColor(Color.parseColor("#e60000"));
            holder.menuTitel.setTextColor(Color.parseColor("#4a4d4e"));
        }
        holder.menuTitel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (menuList.get(position).toString().equalsIgnoreCase("Notification")) {
                    Intent intent = new Intent(mContext, NotificationsActivity.class);
                    mContext.startActivity(intent);
                    Activity activity = (Activity) mContext;
                    activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
                } else if (menuList.get(position).toString().equalsIgnoreCase("Favorites")) {
                    FavoritesFragment fragment = FavoritesFragment.newInstance("", "");
                    navigateToFragment(fragment);
                } else if (menuList.get(position).toString().equalsIgnoreCase("Downloads")) {
//                    Utility.verifyStoragePermissions((Activity) mContext);
//                    CardReaderHelper cardReaderHelper = new CardReaderHelper(mContext);
//                    String SDPath = Utility.getSelectFolderPathFromSharedPreferences(mContext);// get this location from sharedpreferance;
//                    if (SDPath != null && !SDPath.equals("")) {
//                        cardReaderHelper.readDataFromSDCard(SDPath);
//                    } else {
//                        cardReaderHelper.readDataFromSDCard(Consts.inputDirectoryLocation);
//                    }

                    //readExternalFilesAsync();

                } else if (menuList.get(position).toString().equalsIgnoreCase("Change Language")) {
                    Intent intent = new Intent(mContext, LanguageActivity.class);
                    mContext.startActivity(intent);
                    Activity activity = (Activity) mContext;
                    activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
                } else if (menuList.get(position).toString().equalsIgnoreCase("Change Downloads Folder")) {
                    SelectLocationFragment fragment = SelectLocationFragment.newInstance("", "");
                    navigateToFragment(fragment);
                } else if (menuList.get(position).toString().equalsIgnoreCase("Home")) {
                    CourseFragment fragment = CourseFragment.newInstance("", "");
                    navigateToFragment(fragment);
                } else if (menuList.get(position).toString().equalsIgnoreCase("Profile")) {
                    UserProfileFragment aboutUs = UserProfileFragment.newInstance("", "");
                    navigateToFragment(aboutUs);
                } else if (menuList.get(position).toString().equalsIgnoreCase("About Us")) {
                    AboutUsFragment aboutUs = AboutUsFragment.newInstance("", "");
                    navigateToFragment(aboutUs);
                } else if (menuList.get(position).toString().equalsIgnoreCase("Terms of use")) {
                    TermsOfUseFragment aboutUs = TermsOfUseFragment.newInstance("", "");
                    navigateToFragment(aboutUs);
                }
                int pos = (int) v.getTag();
                if (selectedPosition.contains(pos)) {
                    //selectedPosition.remove(pos);
                    //selectedPosition.clear();
                    notifyDataSetChanged();
                } else {
                    selectedPosition.clear();
                    selectedPosition.add(pos);
                    notifyDataSetChanged();
                }
                MainActivity rootActivity = (MainActivity) mContext;
                rootActivity.closeDrawer();
            }
        });

        return convertView;
    }



    public class ViewHolder {
        public TextView menuTitel;
        public TextView menuIcon;
        public LinearLayout menuItemLayout;
    }

}

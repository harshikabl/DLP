package dlp.bluelupin.dlp.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dlp.bluelupin.dlp.R;


/**
 * Created by Neeraj on 7/28/2016.
 */
public class Utility {

    //set languagae
    public static void setLanguageIntoSharedPreferences(Context context, String language) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("LANG", language).commit();
        setLangRecreate(context, language);
    }

    //set language into locale
    public static void setLangRecreate(Context context, String langval) {
        Configuration config = new Configuration();
        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    private void gatLanguageFromSharedPreferences(Context context) {
        //geting language from SharedPreferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Configuration config = context.getResources().getConfiguration();

        String lang = settings.getString("LANG", "");
        if (!lang.equals("") && !config.locale.getLanguage().equals(lang)) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        int version = 1;
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            // throw new RuntimeException("Could not get package name: " + e);
        }
        return version;
    }

    //get app version name
    public static String getAppVersionName(Context context) {
        String version = "";
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            // throw new RuntimeException("Could not get package name: " + e);

        }
        return version;
    }

    //get app package name
    public static String getAppPackageName(Context context) {
        String pkName = "";
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            pkName = packageInfo.packageName;

        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            // throw new RuntimeException("Could not get package name: " + e);

        }
        return pkName;
    }


    //convert date for gate day month year hours min am/pm
    public static String convertDate(String date) {
        StringBuilder sb = new StringBuilder();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aaa", Locale.ENGLISH);
            Date dat = sdf.parse(date);
            String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", dat);//Thursday
            String stringMonth = (String) android.text.format.DateFormat.format("MMM", dat); //Jun
            String intMonth = (String) android.text.format.DateFormat.format("MM", dat); //06
            String year = (String) android.text.format.DateFormat.format("yyyy", dat); //2013
            String day = (String) android.text.format.DateFormat.format("dd", dat); //20
            String hours = (String) android.text.format.DateFormat.format("h", dat); //20
            String min = (String) android.text.format.DateFormat.format("mm", dat); //20
            String ampm = (String) android.text.format.DateFormat.format("aa", dat); //20
            //Log.d(Contants.LOG_TAG, "dayOfTheWeek*********" + dayOfTheWeek);
            //Log.d(Contants.LOG_TAG, "year*********" + year);
            //Log.d(Contants.LOG_TAG, "stringMonth*********" + stringMonth+"  "+intMonth);
           /* Log.d(Contants.LOG_TAG, "day*********" + day);
            Log.d(Contants.LOG_TAG, "hours*********" + hours);
            Log.d(Contants.LOG_TAG, "min*********" + min);
            Log.d(Contants.LOG_TAG, "ampm*********" + ampm);*/
            List<String> list = new ArrayList<String>();
            list.add(dayOfTheWeek);
            list.add(day);
            list.add(stringMonth);
            list.add(hours);
            list.add(min);
            list.add(ampm);
            list.add(year);
            list.add(intMonth);
            String delim = "";
            for (String i : list) {
                sb.append(delim).append(i);
                delim = ",";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    //alert for error message
    public static void alertForErrorMessage(String errorMessage, Context mContext) {
        // Log.d(Contants.LOG_TAG, "getAppPackageName****" + Utility.getAppPackageName(mContext));
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        final AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.alertAnimation;
        View view = alert.getLayoutInflater().inflate(R.layout.custom_error_alert, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView ok = (TextView) view.findViewById(R.id.Ok);
        LinearLayout layout_titleBar = (LinearLayout) view.findViewById(R.id.alert_layout_titleBar);
        View divider = (View) view.findViewById(R.id.divider);
        GradientDrawable drawable = (GradientDrawable) ok.getBackground();
        drawable.setStroke(5, Color.parseColor("#000000"));// set stroke width and stroke color
        drawable.setColor(Color.parseColor("#000000")); //
        title.setText(errorMessage);
        alert.setCustomTitle(view);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    //calculate total days between to days
    public static int daysBetween(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aaa", Locale.ENGLISH);
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = sdf.parse(startDate);
            date2 = sdf.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
    }
}

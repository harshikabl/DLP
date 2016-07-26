package dlp.bluelupin.dlp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dlp.bluelupin.dlp.Adapters.LanguageAdapter;
import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 7/22/2016.
 */
public class LanguageActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView languageIcon,chooseLanguage,description;
    private  Spinner spinner;
    private TextView tickIcon,done,title;
    private LinearLayout doneLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        setContentView(R.layout.activity_language);
        init();
    }
    //get all id's
    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Typeface custom_fontawesome = Typeface.createFromAsset(this.getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface materialdesignicons_font = Typeface.createFromAsset(this.getAssets(), "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneRg.ttf");

        languageIcon= (TextView) findViewById(R.id.languageIcon);
        chooseLanguage= (TextView) findViewById(R.id.chooseLanguage);
        description= (TextView) findViewById(R.id.description);
        done= (TextView) findViewById(R.id.done);
        doneLayout= (LinearLayout) findViewById(R.id.doneLayout);
        doneLayout.setOnClickListener(this);
        title= (TextView) findViewById(R.id.title);
        tickIcon= (TextView) findViewById(R.id.tickIcon);
        tickIcon.setTypeface(custom_fontawesome);
        title.setTypeface(VodafoneExB);
        done.setTypeface(VodafoneRg);
        languageIcon.setTypeface(custom_fontawesome);
        chooseLanguage.setTypeface(VodafoneExB);
        description.setTypeface(VodafoneRg);
        languageIcon.setText(Html.fromHtml("&#xf1ab;"));
        tickIcon.setText(Html.fromHtml("&#xf00c;"));

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.getBackground().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        List<String> list = new ArrayList<String>();
        list.add("English");
        list.add("Hindi");
        list.add("Tamil");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.language_item, list);
        LanguageAdapter languageAdapter=new LanguageAdapter(this,list);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(languageAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setLanguage(int langpos){
        switch(langpos) {
            case 0: //English
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                setLangRecreate("en");
                return;
            case 1: //Hindi
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "hi").commit();
                setLangRecreate("hi");
                return;
            default: //By default set to english
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                setLangRecreate("en");
                return;
        }
    }
    //set language into locale
    public void setLangRecreate(String langval) {
        Configuration config = new Configuration();
        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.doneLayout:
                Intent intent=new Intent(this,AccountSettings.class);
                startActivity(intent);
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.click_animation));//onclick animation
                break;
        }
    }

   @Override
   public void onBackPressed() {
       super.onBackPressed();
       overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
   }
}
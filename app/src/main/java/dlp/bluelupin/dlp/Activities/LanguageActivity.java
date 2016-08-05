package dlp.bluelupin.dlp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Adapters.LanguageAdapter;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.EnumLanguage;
import dlp.bluelupin.dlp.Utilities.Utility;

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
        title= (TextView) toolbar.findViewById(R.id.title);
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
        list.add("Telugu");
        list.add("Tamil");
        list.add("Kannada");
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
                Utility.setLanguageIntoSharedPreferences(this, EnumLanguage.en);
                return;
            case 1: //Hindi
                Utility.setLanguageIntoSharedPreferences(this, EnumLanguage.hi);
                return;
            case 2: //telugu
                Utility.setLanguageIntoSharedPreferences(this, EnumLanguage.te);
                return;
            case 3: //tamil
                Utility.setLanguageIntoSharedPreferences(this, EnumLanguage.ta);
                return;
            case 4: //Kannada
                Utility.setLanguageIntoSharedPreferences(this, EnumLanguage.kn);
                return;
            default: //By default set to english
                Utility.setLanguageIntoSharedPreferences(this, EnumLanguage.en);
                return;
        }
    }


    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.doneLayout:
//                Intent intent=new Intent(this,AccountSettingsActivity.class);
//                startActivity(intent);
//                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.click_animation));//onclick animation
//                break;
//        }

        checkRegistered();
    }

    private void checkRegistered() {
        DbHelper dbhelper = new DbHelper(LanguageActivity.this);
        AccountData accountData = dbhelper.getAccountData();
        if (accountData != null && !accountData.equals("")) {
            Intent mainIntent = new Intent(LanguageActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        } else {
            Intent mainIntent = new Intent(LanguageActivity.this, AccountSettings.class);
            startActivity(mainIntent);
            finish();
        }
    }

   @Override
   public void onBackPressed() {
       super.onBackPressed();
       overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
   }
}
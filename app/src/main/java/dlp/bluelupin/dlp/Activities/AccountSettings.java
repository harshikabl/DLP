package dlp.bluelupin.dlp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dlp.bluelupin.dlp.Adapters.LanguageAdapter;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 7/25/2016.
 */
public class AccountSettings extends AppCompatActivity implements View.OnClickListener {
    private TextView chooseLanguage;
    private Spinner spinner;
    private TextView title, leftArrow;
    private TextView emailLable, nameLable, phoneLable, lanLable, genderLable, cancel, save;

    private EditText enterName,enterEmail,enterPhone;
    String name_string,pnone_no_string,email_string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        setContentView(R.layout.activity_account_settings);
        init();
    }

    //get all id's
    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        leftArrow = (TextView) toolbar.findViewById(R.id.leftArrow);
        title = (TextView) toolbar.findViewById(R.id.title);
        Typeface custom_fontawesome = Typeface.createFromAsset(this.getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface materialdesignicons_font = Typeface.createFromAsset(this.getAssets(), "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneRg.ttf");



        leftArrow.setTypeface(materialdesignicons_font);
        leftArrow.setOnClickListener(this);
        RadioGroup radioGrp = (RadioGroup) findViewById(R.id.radioGrp);
        RadioButton male = (RadioButton) findViewById(R.id.radioM);
        RadioButton female = (RadioButton) findViewById(R.id.radioF);
        int textColor = Color.parseColor("#e60000");
        emailLable = (TextView) findViewById(R.id.emailLable);
        nameLable = (TextView) findViewById(R.id.nameLable);
        phoneLable = (TextView) findViewById(R.id.phoneLable);
        lanLable = (TextView) findViewById(R.id.lanLable);
        genderLable = (TextView) findViewById(R.id.genderLable);
        cancel = (TextView) findViewById(R.id.cancel);
        save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(this);
        CheckBox autoPlay = (CheckBox) findViewById(R.id.autoPlay);
        title.setTypeface(VodafoneExB);
        nameLable.setTypeface(VodafoneRg);
        emailLable.setTypeface(VodafoneRg);
        phoneLable.setTypeface(VodafoneRg);
        lanLable.setTypeface(VodafoneRg);
        autoPlay.setTypeface(VodafoneRg);
        genderLable.setTypeface(VodafoneRg);
        cancel.setTypeface(VodafoneRg);
        save.setTypeface(VodafoneRg);
        male.setTypeface(VodafoneRg);
        female.setTypeface(VodafoneRg);
        leftArrow.setText(Html.fromHtml("&#xf04d;"));
        enterName= (EditText) findViewById(R.id.enterName);
        enterEmail= (EditText) findViewById(R.id.enterEmail);
        enterPhone= (EditText) findViewById(R.id.enterPhone);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.getBackground().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        List<String> list = new ArrayList<String>();
        list.add("English");
        list.add("Hindi");
        list.add("Tamil");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.language_item, list);
        LanguageAdapter languageAdapter = new LanguageAdapter(this, list);
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
                Utility.setLanguageIntoSharedPreferences(this, "en");
                return;
            case 1: //Hindi
                Utility.setLanguageIntoSharedPreferences(this, "hi");
                return;
            default: //By default set to english
                Utility.setLanguageIntoSharedPreferences(this, "en");
                return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
    }

    // ----validation -----
    private boolean isValidate() {
        String numberRegex = "[0-9]+";
        String emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        // String emailReg = "^[A-Za-z0-9_.]+(\\.[_A-Za-z0-9]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        name_string = enterName.getText().toString().trim();
        email_string = enterEmail.getText().toString().trim();
        pnone_no_string = enterPhone.getText().toString().trim();
        if (pnone_no_string.length() == 0) {
            Utility.alertForErrorMessage("Enter phone number",this);
            return false;
        } else if (pnone_no_string.length() != 10) {
            Utility.alertForErrorMessage("Enter a valid phone number", this);
            return false;
        }else if (!pnone_no_string.matches(numberRegex)) {
            Utility.alertForErrorMessage("Enter a valid phone number",this);
            return false;
        }
        else if (email_string.length() == 0) {
            Utility.alertForErrorMessage("Enter E-mail address", this);
            return false;
        } else if (!email_string.matches(emailRegex)) {
            Utility.alertForErrorMessage("Enter valid E-mail address", this);
            return false;
        }
        return true;

    }






    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.leftArrow:
                Intent intent=new Intent(this,LanguageActivity.class);
                startActivity(intent);
            break;
            case R.id.save:
                Intent intentOtp=new Intent(this,VerificationActivity.class);
                startActivity(intentOtp);
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.click_animation));//onclick animation
                break;
        }
    }

}

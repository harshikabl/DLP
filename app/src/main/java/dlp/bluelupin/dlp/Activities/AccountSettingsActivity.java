package dlp.bluelupin.dlp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dlp.bluelupin.dlp.Adapters.LanguageAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.AccountServiceRequest;
import dlp.bluelupin.dlp.Models.LanguageData;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.IAsyncWorkCompletedCallback;
import dlp.bluelupin.dlp.Services.ServiceCaller;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.EnumLanguage;
import dlp.bluelupin.dlp.Utilities.FontManager;
import dlp.bluelupin.dlp.Utilities.LogAnalyticsHelper;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 7/25/2016.
 */
public class AccountSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView chooseLanguage;
    private Spinner spinner;
    private TextView title, leftArrow;
    private TextView emailLable, nameLable, phoneLable, lanLable, genderLable, cancel, save, autoPlayCheck;

    private EditText enterName, enterEmail, enterPhone;
    String name_string, pnone_no_string, email_string;
    List<LanguageData> data;
    private Boolean checkFlag = false;
    private ImageView autoPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        setContentView(R.layout.activity_account_settings);
        if (Utility.isTablet(this)) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        init();
    }

    //get all id's
    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        leftArrow = (TextView) toolbar.findViewById(R.id.leftArrow);
        title = (TextView) toolbar.findViewById(R.id.title);

        Typeface custom_fontawesome = FontManager.getFontTypeface(this, "fonts/fontawesome-webfont.ttf");
        Typeface materialdesignicons_font = FontManager.getFontTypeface(this, "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = FontManager.getFontTypeface(this, "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = FontManager.getFontTypeface(this, "fonts/VodafoneRg.ttf");

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
        // cancel = (TextView) findViewById(R.id.cancel);
        save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(this);
        autoPlayCheck = (TextView) findViewById(R.id.autoPlayCheck);
        autoPlay = (ImageView) findViewById(R.id.autoPlay);
        autoPlay.setOnClickListener(this);
        autoPlay.setImageResource(R.drawable.checkbox);
        title.setTypeface(VodafoneExB);
        nameLable.setTypeface(VodafoneExB);
        emailLable.setTypeface(VodafoneExB);
        phoneLable.setTypeface(VodafoneExB);
        lanLable.setTypeface(VodafoneExB);
        autoPlayCheck.setTypeface(VodafoneRg);
        genderLable.setTypeface(VodafoneExB);
        //cancel.setTypeface(VodafoneRg);
        save.setTypeface(VodafoneRg);
        male.setTypeface(VodafoneRg);
        female.setTypeface(VodafoneRg);
        leftArrow.setText(Html.fromHtml("&#xf04d;"));
        enterName = (EditText) findViewById(R.id.enterName);
        enterEmail = (EditText) findViewById(R.id.enterEmail);
        enterPhone = (EditText) findViewById(R.id.enterPhone);

        spinner = (Spinner) findViewById(R.id.spinner);
        // style="@style/Widget.AppCompat.Spinner.Underlined"
        spinner.getBackground().setColorFilter(Color.parseColor("#4a4d4e"), PorterDuff.Mode.SRC_ATOP);


        DbHelper db = new DbHelper(AccountSettingsActivity.this);
        data = db.getAllLanguageDataEntity();
        if (data != null) {
            LanguageAdapter languageAdapter = new LanguageAdapter(this, data);
            //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(languageAdapter);
            int languagePos = Utility.getLanguagePositionFromSharedPreferences(this);
            spinner.setSelection(languagePos);//set default value
        }
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

    //call create account service
    private void callCreateAccountService() {
        final CustomProgressDialog customProgressDialog = new CustomProgressDialog(this, R.mipmap.syc);

        int languageId = Utility.getLanguageIdFromSharedPreferences(this);

        AccountServiceRequest accountServiceRequest = new AccountServiceRequest();
        accountServiceRequest.setName(name_string);
        accountServiceRequest.setEmail(email_string);
        accountServiceRequest.setPhone(pnone_no_string);
        accountServiceRequest.setPreferred_language_id(languageId);
        if (Utility.isOnline(this)) {
            customProgressDialog.show();
            ServiceCaller sc = new ServiceCaller(AccountSettingsActivity.this);
            sc.CreateAccount(accountServiceRequest, new IAsyncWorkCompletedCallback() {
                @Override
                public void onDone(String workName, boolean isComplete) {

                    if (isComplete) {
                        if (Consts.IS_DEBUG_LOG) {
                            Log.d(Consts.LOG_TAG, " callCreateAccountService success result: " + isComplete);
                        }
                        Intent intentOtp = new Intent(AccountSettingsActivity.this, VerificationActivity.class);
                        startActivity(intentOtp);
                        Toast.makeText(AccountSettingsActivity.this, getString(R.string.otp_sent), Toast.LENGTH_LONG).show();
                        customProgressDialog.dismiss();
                    } else {
                        Utility.alertForErrorMessage(getString(R.string.account_not_create), AccountSettingsActivity.this);
                        customProgressDialog.dismiss();
                    }

                }
            });
        } else {
            AccountData accountData = new AccountData();
            accountData.setName(name_string);
            accountData.setEmail(email_string);
            accountData.setPhone(pnone_no_string);
            accountData.setIsVerified(1);
            DbHelper dbhelper = new DbHelper(AccountSettingsActivity.this);
            dbhelper.upsertAccountData(accountData);
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, " account Data insert in offline mode: " + accountData);
            }
            Intent intent = new Intent(AccountSettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }

    private void setLanguage(int langpos) {
        if (data != null) {
            String StringCode = data.get(langpos).getCode();
            String[] parts = StringCode.split("-");
            String code = parts[0];
            String part2 = parts[1];
            Utility.setLanguageIntoSharedPreferences(this, data.get(langpos).getId(), code, langpos);
            LogAnalyticsHelper analyticsHelper = new LogAnalyticsHelper(this);
            analyticsHelper.logSetUserProperty("Language", code);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
    }

    //to hide keyboard from otside touch
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // ----validation -----
    private boolean isValidate() {
        String numberRegex = "[0-9]+";
        String emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        // String emailReg = "^[A-Za-z0-9_.]+(\\.[_A-Za-z0-9]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        name_string = enterName.getText().toString().trim();
        email_string = enterEmail.getText().toString().trim();
        pnone_no_string = enterPhone.getText().toString().trim();
        if (name_string.length() == 0) {
            Utility.alertForErrorMessage(getString(R.string.enter_name), this);
            return false;
        } else if (email_string.length() == 0) {
            Utility.alertForErrorMessage(getString(R.string.enter_e_mail), this);
            return false;
        } else if (!email_string.matches(emailRegex)) {
            Utility.alertForErrorMessage(getString(R.string.enter_valid_mail), this);
            return false;
        } else if (pnone_no_string.length() == 0) {
            Utility.alertForErrorMessage(getString(R.string.enter_phone_no), this);
            return false;
        } else if (pnone_no_string.length() != 10) {
            Utility.alertForErrorMessage(getString(R.string.enter_valid_phone), this);
            return false;
        } else if (!pnone_no_string.matches(numberRegex)) {
            Utility.alertForErrorMessage(getString(R.string.enter_valid_phone), this);
            return false;
        }

        return true;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftArrow:
                Intent intent = new Intent(this, LanguageActivity.class);
                startActivity(intent);
                break;
            case R.id.save:
                if (isValidate()) {
                    callCreateAccountService();
                    v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.click_animation));//onclick animation
                }
                break;
            case R.id.autoPlay:
                if (checkFlag) {
                    checkFlag = false;
                    autoPlay.setImageResource(R.drawable.checkbox);
                } else {
                    checkFlag = true;
                    autoPlay.setImageResource(R.drawable.checkboxchecked);
                }
                break;
        }
    }

}

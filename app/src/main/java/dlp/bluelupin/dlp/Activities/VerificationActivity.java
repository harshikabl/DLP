package dlp.bluelupin.dlp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.AccountServiceRequest;
import dlp.bluelupin.dlp.Models.OtpVerificationServiceRequest;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.IAsyncWorkCompletedCallback;
import dlp.bluelupin.dlp.Services.ServiceCaller;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * Created by Neeraj on 7/25/2016.
 */
public class VerificationActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title, leftArrow;
    private TextView appName, phoneNo, description, otpLable, otpmsg, verify, resend_otp;
    private EditText oneNo, twoNo, threeNo, fourNo, fiveNo, sixNo;
    private String one_string, two_string, three_string, four_string, five_string, six_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        setContentView(R.layout.activity_verification);
        if (Utility.isTablet(this)) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = (TextView) toolbar.findViewById(R.id.title);
        leftArrow = (TextView) toolbar.findViewById(R.id.leftArrow);
        Typeface custom_fontawesome = Typeface.createFromAsset(this.getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface materialdesignicons_font = Typeface.createFromAsset(this.getAssets(), "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneRg.ttf");
        Typeface VodafoneLt = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneLt.ttf");
        Typeface VodafoneRgBd = Typeface.createFromAsset(this.getAssets(), "fonts/VodafoneRgBd.ttf");


        leftArrow.setTypeface(materialdesignicons_font);
        leftArrow.setText(Html.fromHtml("&#xf04d;"));
        leftArrow.setOnClickListener(this);
        title.setTypeface(VodafoneExB);
        resend_otp = (TextView) findViewById(R.id.resend_otp);
        resend_otp.setPaintFlags(resend_otp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        resend_otp.setOnClickListener(this);
        verify = (TextView) findViewById(R.id.verify);
        verify.setTypeface(VodafoneRg);
        verify.setOnClickListener(this);
        otpLable = (TextView) findViewById(R.id.otpLable);
        otpLable.setTypeface(VodafoneExB);
        description = (TextView) findViewById(R.id.description);
        description.setTypeface(VodafoneRgBd);
        phoneNo = (TextView) findViewById(R.id.phoneNo);
        phoneNo.setTypeface(VodafoneLt);
        appName = (TextView) findViewById(R.id.appName);
        appName.setTypeface(VodafoneRg);
        oneNo = (EditText) findViewById(R.id.oneNo);
        twoNo = (EditText) findViewById(R.id.twoNo);
        threeNo = (EditText) findViewById(R.id.threeNo);
        fourNo = (EditText) findViewById(R.id.fourNo);
        fiveNo = (EditText) findViewById(R.id.fiveNo);
        sixNo = (EditText) findViewById(R.id.sixNo);

        DbHelper dbHelper = new DbHelper(VerificationActivity.this);
        AccountData acData = dbHelper.getAccountData();
        if (acData != null) {
            phoneNo.setText(acData.getPhone());
        }
        setFocusChangeInEditText();

    }

    // ----validation -----
    private boolean isValidate() {
        String numberRegex = "[0-9]+";

        one_string = oneNo.getText().toString().trim();
        two_string = twoNo.getText().toString().trim();
        three_string = threeNo.getText().toString().trim();
        four_string = fourNo.getText().toString().trim();
        five_string = fiveNo.getText().toString().trim();
        six_string = sixNo.getText().toString().trim();
        if (one_string.length() == 0) {
            Utility.alertForErrorMessage("Please enter OTP", VerificationActivity.this);
            return false;
        } else if (!one_string.matches(numberRegex)) {
            Utility.alertForErrorMessage("Please enter a valid OTP.", VerificationActivity.this);
            return false;
        } else if (two_string.length() == 0) {
            Utility.alertForErrorMessage("Please enter OTP", VerificationActivity.this);
            return false;
        } else if (!two_string.matches(numberRegex)) {
            Utility.alertForErrorMessage("Please enter a valid OTP.", VerificationActivity.this);
            return false;
        } else if (three_string.length() == 0) {
            Utility.alertForErrorMessage("Please enter OTP", VerificationActivity.this);
            return false;
        } else if (!three_string.matches(numberRegex)) {
            Utility.alertForErrorMessage("Please enter a valid OTP.", VerificationActivity.this);
            return false;
        } else if (four_string.length() == 0) {
            Utility.alertForErrorMessage("Please enter OTP", VerificationActivity.this);
            return false;
        } else if (!four_string.matches(numberRegex)) {
            Utility.alertForErrorMessage("Please enter a valid OTP.", VerificationActivity.this);
            return false;
        } else if (five_string.length() == 0) {
            Utility.alertForErrorMessage("Please enter OTP", VerificationActivity.this);
            return false;
        } else if (!five_string.matches(numberRegex)) {
            Utility.alertForErrorMessage("Please enter a valid OTP.", VerificationActivity.this);
            return false;
        } else if (six_string.length() == 0) {
            Utility.alertForErrorMessage("Please enter OTP", VerificationActivity.this);
            return false;
        } else if (!six_string.matches(numberRegex)) {
            Utility.alertForErrorMessage("Please enter a valid OTP.", VerificationActivity.this);
            return false;
        }

        return true;

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftArrow:
                Intent intent = new Intent(this, AccountSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.verify:
                if (isValidate()) {
                    String otp = one_string + two_string + three_string + four_string + five_string + six_string;
                    callOTPVerificationService(otp);
                }
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.click_animation));//onclick animation
                break;
            case R.id.resend_otp:
                callResendOtpService();
                break;
        }
    }
    //call OTP verification service
    private void callOTPVerificationService(String otp) {
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, " otp*****   " + otp);
        }
        OtpVerificationServiceRequest otpServiceRequest = new OtpVerificationServiceRequest();
        DbHelper dbHelper = new DbHelper(VerificationActivity.this);
        AccountData accountData = dbHelper.getAccountData();
        if (accountData != null) {
            if (accountData.getApi_token() != null) {
                otpServiceRequest.setApi_token(accountData.getApi_token());
            }
            otpServiceRequest.setOtp(otp);
            if (Utility.isOnline(this)) {
                final DbHelper dbhelper = new DbHelper(VerificationActivity.this);
                ServiceCaller sc = new ServiceCaller(VerificationActivity.this);
                sc.OtpVerification(otpServiceRequest, new IAsyncWorkCompletedCallback() {
                    @Override
                    public void onDone(String message, boolean isComplete) {
                        int serverId = Utility.getUserServerIdFromSharedPreferences(VerificationActivity.this);
                        AccountData accountData = new AccountData();
                        if (isComplete) {
                            accountData.setId(serverId);
                            accountData.setIsVerified(1);
                            //update account verified for check account verified or not
                            dbhelper.updateAccountDataVerified(accountData);
                            if (Consts.IS_DEBUG_LOG) {
                                Log.d(Consts.LOG_TAG, " callOTPVerificationService success result: " + isComplete);
                            }
                            Intent intentOtp = new Intent(VerificationActivity.this, MainActivity.class);
                            intentOtp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentOtp);
                            Toast.makeText(VerificationActivity.this, "You are registered successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            accountData.setId(serverId);
                            accountData.setIsVerified(0);
                            //update account verified for check account verified or not
                            dbhelper.updateAccountDataVerified(accountData);
                            //Toast.makeText(VerificationActivity.this, "Please enter a valid OTP.", Toast.LENGTH_LONG).show();
                            Utility.alertForErrorMessage("Please enter a valid OTP.", VerificationActivity.this);
                        }
                    }
                });
            } else {
                Utility.alertForErrorMessage(Consts.OFFLINE_MESSAGE, VerificationActivity.this);
            }
        }
    }
    //call resend OTP service
    private void callResendOtpService() {
        final CustomProgressDialog customProgressDialog = new CustomProgressDialog(this, R.mipmap.syc);

        DbHelper dbHelper = new DbHelper(VerificationActivity.this);
        AccountData acData = dbHelper.getAccountData();
        int languageId = Utility.getLanguageIdFromSharedPreferences(this);
        if (acData != null) {
            AccountServiceRequest accountServiceRequest = new AccountServiceRequest();
            accountServiceRequest.setName(acData.getName());
            accountServiceRequest.setEmail(acData.getEmail());
            accountServiceRequest.setPhone(acData.getPhone());
            accountServiceRequest.setPreferred_language_id(languageId);
            if (Utility.isOnline(this)) {
                customProgressDialog.show();
                ServiceCaller sc = new ServiceCaller(VerificationActivity.this);
                sc.CreateAccount(accountServiceRequest, new IAsyncWorkCompletedCallback() {
                    @Override
                    public void onDone(String workName, boolean isComplete) {

                        if (isComplete) {
                            if (Consts.IS_DEBUG_LOG) {
                                Log.d(Consts.LOG_TAG, " callCreateAccountService success result: " + isComplete);
                            }
                            Intent intentOtp = new Intent(VerificationActivity.this, VerificationActivity.class);
                            startActivity(intentOtp);
                            customProgressDialog.dismiss();
                            Toast.makeText(VerificationActivity.this, "OTP has been sent to the provided Phone Number.", Toast.LENGTH_LONG).show();
                        } else {
                            Utility.alertForErrorMessage("OTP can not send", VerificationActivity.this);
                            customProgressDialog.dismiss();
                        }

                    }
                });
            } else {
                Utility.alertForErrorMessage(Consts.OFFLINE_MESSAGE, VerificationActivity.this);
            }
        }


    }

    //whene enter no in editText focus change into next editText
    private void setFocusChangeInEditText() {
        oneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (oneNo.length() == 1) {
                    oneNo.clearFocus();
                    twoNo.requestFocus();
                    twoNo.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        twoNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (twoNo.length() == 1) {
                    twoNo.clearFocus();
                    threeNo.requestFocus();
                    threeNo.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        threeNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (threeNo.length() == 1) {
                    threeNo.clearFocus();
                    fourNo.requestFocus();
                    fourNo.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fourNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (fourNo.length() == 1) {
                    fourNo.clearFocus();
                    fiveNo.requestFocus();
                    fiveNo.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fiveNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (fiveNo.length() == 1) {
                    fiveNo.clearFocus();
                    sixNo.requestFocus();
                    sixNo.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
    }
}

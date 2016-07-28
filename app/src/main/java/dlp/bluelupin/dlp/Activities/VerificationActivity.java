package dlp.bluelupin.dlp.Activities;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Spinner;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        setContentView(R.layout.activity_verification);
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
        otpmsg = (TextView) findViewById(R.id.otpmsg);
        otpmsg.setTypeface(VodafoneRg);
        otpLable = (TextView) findViewById(R.id.otpLable);
        otpLable.setTypeface(VodafoneRg);
        description = (TextView) findViewById(R.id.description);
        description.setTypeface(VodafoneRg);
        phoneNo = (TextView) findViewById(R.id.phoneNo);
        phoneNo.setTypeface(VodafoneRg);
        appName = (TextView) findViewById(R.id.appName);
        appName.setTypeface(VodafoneRg);

    }

    //call OTP verification service
    private void callOTPVerificationService() {
        final CustomProgressDialog customProgressDialog = new CustomProgressDialog(this, R.mipmap.syc);
        customProgressDialog.show();
        OtpVerificationServiceRequest otpServiceRequest = new OtpVerificationServiceRequest();
        DbHelper dbHelper = new DbHelper(VerificationActivity.this);
        AccountData accountData = dbHelper.getAccountData();
        if (accountData != null) {
            if (accountData.getApi_token() != null) {
                otpServiceRequest.setApi_token(accountData.getApi_token());
            }
            otpServiceRequest.setOtp("");
        }
        if (Utility.isOnline(this)) {
            ServiceCaller sc = new ServiceCaller(VerificationActivity.this);
            sc.OtpVerification(otpServiceRequest, new IAsyncWorkCompletedCallback() {
                @Override
                public void onDone(String workName, boolean isComplete) {

                    if (isComplete) {
                        Log.d(Consts.LOG_TAG, " callCreateAccountService success result: " + isComplete);
                        Intent intentOtp = new Intent(VerificationActivity.this, MainActivity.class);
                        startActivity(intentOtp);
                        customProgressDialog.dismiss();
                    } else {
                        customProgressDialog.dismiss();
                    }

                }
            });
        } else {
            Utility.alertForErrorMessage(Consts.OFFLINE_MESSAGE, VerificationActivity.this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftArrow:
                Intent intent = new Intent(this, AccountSettings.class);
                startActivity(intent);
                break;
            case R.id.verify:
                Intent intentMain = new Intent(this, MainActivity.class);
                startActivity(intentMain);
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

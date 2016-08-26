package dlp.bluelupin.dlp.Utilities;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.OtpVerificationServiceRequest;
import dlp.bluelupin.dlp.Services.IAsyncWorkCompletedCallback;
import dlp.bluelupin.dlp.Services.ServiceCaller;

/**
 * Created by Neeraj on 7/29/2016.
 */
public class OtpService extends IntentService {
    private Context context;

    public OtpService() {
        super(OtpService.class.getSimpleName());

    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public OtpService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = OtpService.this;
        if (intent != null) {
            String otp = intent.getStringExtra("otp");
            callOTPVerificationService(otp);
        }
    }

    //call OTP verification service
    private void callOTPVerificationService(String otp) {
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, " otp*****   " + otp);
        }
        OtpVerificationServiceRequest otpServiceRequest = new OtpVerificationServiceRequest();
        DbHelper dbHelper = new DbHelper(OtpService.this);
        AccountData accountData = dbHelper.getAccountData();
        if (accountData != null) {
            if (accountData.getApi_token() != null) {
                otpServiceRequest.setApi_token(accountData.getApi_token());
            }
            otpServiceRequest.setOtp(otp);
            if (Utility.isOnline(this)) {
                final DbHelper dbhelper = new DbHelper(context);
                ServiceCaller sc = new ServiceCaller(OtpService.this);
                sc.OtpVerification(otpServiceRequest, new IAsyncWorkCompletedCallback() {
                    @Override
                    public void onDone(String message, boolean isComplete) {
                        int serverId = Utility.getUserServerIdFromSharedPreferences(context);
                        AccountData accountData = new AccountData();
                        if (isComplete) {
                            accountData.setId(serverId);
                            accountData.setIsVerified(1);
                            //update account verified for check account verified or not
                            dbhelper.updateAccountDataVerified(accountData);
                            if (Consts.IS_DEBUG_LOG) {
                                Log.d(Consts.LOG_TAG, " callCreateAccountService success result: " + isComplete);
                            }
                            Intent intentOtp = new Intent(OtpService.this, MainActivity.class);
                            intentOtp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentOtp);
                            Toast.makeText(OtpService.this, "You are registered successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            accountData.setId(serverId);
                            accountData.setIsVerified(0);
                            //update account verified for check account verified or not
                            dbhelper.updateAccountDataVerified(accountData);
                            Toast.makeText(OtpService.this, "Please enter a valid OTP.", Toast.LENGTH_LONG).show();
                            //Utility.alertForErrorMessage("Please enter a valid OTP.", getApplicationContext());
                        }
                    }
                });
            } else {
                Toast.makeText(OtpService.this, Consts.OFFLINE_MESSAGE, Toast.LENGTH_LONG).show();
                //Utility.alertForErrorMessage(Consts.OFFLINE_MESSAGE, getApplicationContext());
            }
        }

    }
}

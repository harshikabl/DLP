package dlp.bluelupin.dlp.Utilities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.OtpVerificationServiceRequest;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.IAsyncWorkCompletedCallback;
import dlp.bluelupin.dlp.Services.ServiceCaller;

/**
 * Created by Neeraj on 7/29/2016.
 */
public class SmsReceiver extends BroadcastReceiver {
    Context mContext;
    public static String verificationCode;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();


                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "Received SMS: " + message + ", Sender: " + senderAddress);
                    }
                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains(Consts.SENDER.toLowerCase())) {
                        return;
                    }

                    // verification code from sms
                    String otpCode = getVerificationCode(message);
                    if (otpCode != null) {
                        verificationCode = otpCode;
                        if (Consts.IS_DEBUG_LOG) {
                            Log.d(Consts.LOG_TAG, "OTP received: " + verificationCode);
                        }
                    }

                }
                callOTPVerificationService(verificationCode);
            }
        } catch (Exception e) {
            Log.d(Consts.LOG_TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        Pattern digitPatternRegex = Pattern.compile("\\d{6}");
        Matcher matcher = digitPatternRegex.matcher(message);
        while (matcher.find()) {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "code: " + matcher.group(0));
            }
            code = matcher.group(0);
        }

        return code;
    }

    //call OTP verification service
    private void callOTPVerificationService(String otp) {
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, " otp*****   " + otp);
        }
        OtpVerificationServiceRequest otpServiceRequest = new OtpVerificationServiceRequest();
        otpServiceRequest.setOtp(otp);
        if (Utility.isOnline(mContext)) {
            ServiceCaller sc = new ServiceCaller(mContext);
            sc.OtpVerification(otpServiceRequest, new IAsyncWorkCompletedCallback() {
                @Override
                public void onDone(String message, boolean isComplete) {
                    if (isComplete) {
                        if (Consts.IS_DEBUG_LOG) {
                            Log.d(Consts.LOG_TAG, " callOTPVerificationService success result: " + isComplete);
                        }
                        Intent intentOtp = new Intent(mContext, MainActivity.class);
                        intentOtp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intentOtp);

                    } else {
                        //Utility.alertForErrorMessage("Please enter a valid OTP.", mContext);
                    }
                }
            });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.online_msg), Toast.LENGTH_LONG).show();
            //Utility.alertForErrorMessage(mContext.getString(R.string.online_msg), mContext);
        }
    }

}

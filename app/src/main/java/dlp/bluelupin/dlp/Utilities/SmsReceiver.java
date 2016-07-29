package dlp.bluelupin.dlp.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import dlp.bluelupin.dlp.Consts;

/**
 * Created by Neeraj on 7/29/2016.
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.d(Consts.LOG_TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains(Consts.SENDER.toLowerCase())) {
                        return;
                    }

                    // verification code from sms
                    String verificationCode = getVerificationCode(message);

                    Log.d(Consts.LOG_TAG, "OTP received: " + verificationCode);

                    Intent serviceIntent = new Intent(context, OtpService.class);
                    serviceIntent.putExtra("otp", verificationCode);
                    context.startService(serviceIntent);
                }
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
        int index = message.indexOf("is");

        if (index != -1) {
            int start = index + 3;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}

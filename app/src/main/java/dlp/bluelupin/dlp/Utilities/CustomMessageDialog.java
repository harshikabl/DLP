package dlp.bluelupin.dlp.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dlp.bluelupin.dlp.R;

/**
 * Created by Neeraj on 2/10/2017.
 */

public class CustomMessageDialog extends Dialog {
    private ImageView iv;
    private TextView tv;

    public CustomMessageDialog(Context context, String message) {
        super(context);
        DisplayMetrics dimension = context.getResources().getDisplayMetrics();
        int width = dimension.widthPixels;
        int height = dimension.heightPixels;
        /*Typeface type = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto_Medium.ttf");*/
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        LinearLayout layout = new LinearLayout(context);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // layout.setBackgroundResource(R.color.white);
        layout.setLayoutParams(lParams);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        iv = new ImageView(context);
        //iv.setImageResource(resourceIdOfImage);

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv = new TextView(context);
        tv.setText(message);
        tv.setTextSize(context.getResources().getDimension(R.dimen.dimValue_16)
                / context.getResources().getDisplayMetrics().density);
        //	tv.setTypeface(type);
        tv.setTextColor(context.getResources().getColor(android.R.color.black));
        //layout.addView(iv, ivParams);
        layout.addView(tv, tvParams);
        addContentView(layout, lParams);
    }

    @Override
    public void show() {
        super.show();
        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF,
                .5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(1500);
        iv.setAnimation(anim);
    }
}

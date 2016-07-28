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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import dlp.bluelupin.dlp.R;


public class CustomProgressDialog extends Dialog {
	private ImageView iv;
	private TextView tv;

	public CustomProgressDialog(Context context, int resourceIdOfImage) {
		super(context, R.style.TransparentProgressDialog);
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

		LayoutParams lParams = new LayoutParams(
				width / 2, height / 5);
		// layout.setBackgroundResource(R.color.white);
		layout.setLayoutParams(lParams);
		layout.setGravity(Gravity.CENTER);
		layout.setOrientation(LinearLayout.HORIZONTAL);

		LayoutParams ivParams = new LayoutParams(
				width / 6, height / 6);
		iv = new ImageView(context);
		iv.setImageResource(resourceIdOfImage);

		LayoutParams tvParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tv = new TextView(context);
		//tv.setText("Loading...");
		tv.setTextSize(context.getResources().getDimension(R.dimen.dimValue_16)
				/ context.getResources().getDisplayMetrics().density);
	//	tv.setTypeface(type);
		tv.setTextColor(context.getResources().getColor(android.R.color.white));
		layout.addView(iv, ivParams);
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

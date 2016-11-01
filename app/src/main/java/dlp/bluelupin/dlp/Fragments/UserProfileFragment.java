package dlp.bluelupin.dlp.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import dlp.bluelupin.dlp.Activities.VerificationActivity;
import dlp.bluelupin.dlp.Adapters.LanguageAdapter;
import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.MainActivity;
import dlp.bluelupin.dlp.Models.AccountData;
import dlp.bluelupin.dlp.Models.AccountServiceRequest;
import dlp.bluelupin.dlp.Models.LanguageData;
import dlp.bluelupin.dlp.Models.ProfileUpdateServiceRequest;
import dlp.bluelupin.dlp.R;
import dlp.bluelupin.dlp.Services.IAsyncWorkCompletedCallback;
import dlp.bluelupin.dlp.Services.ServiceCaller;
import dlp.bluelupin.dlp.Utilities.CustomProgressDialog;
import dlp.bluelupin.dlp.Utilities.LogAnalyticsHelper;
import dlp.bluelupin.dlp.Utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;
    private Context context;
    private TextView nameLable, emailLable, email, phoneLable, phone, lanLable, save;
    private Spinner spinner;
    private EditText enterName;
    private List<LanguageData> data;
    private String name_string;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        context = getActivity();
        if (Utility.isTablet(context)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        init();
        return view;
    }

    private void init() {
        MainActivity rootActivity = (MainActivity) getActivity();
        rootActivity.setScreenTitle(context.getString(R.string.Profile));
        Typeface custom_fontawesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface materialdesignicons_font = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.otf");
        Typeface VodafoneExB = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneExB.TTF");
        Typeface VodafoneRg = Typeface.createFromAsset(context.getAssets(), "fonts/VodafoneRg.ttf");
        nameLable = (TextView) view.findViewById(R.id.nameLable);
        emailLable = (TextView) view.findViewById(R.id.emailLable);
        email = (TextView) view.findViewById(R.id.email);
        phoneLable = (TextView) view.findViewById(R.id.phoneLable);
        phone = (TextView) view.findViewById(R.id.phone);
        lanLable = (TextView) view.findViewById(R.id.lanLable);
        save = (TextView) view.findViewById(R.id.save);
        enterName = (EditText) view.findViewById(R.id.enterName);
        emailLable.setTypeface(VodafoneExB);
        nameLable.setTypeface(VodafoneExB);
        phoneLable.setTypeface(VodafoneExB);
        lanLable.setTypeface(VodafoneExB);
        save.setTypeface(VodafoneRg);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.getBackground().setColorFilter(Color.parseColor("#4a4d4e"), PorterDuff.Mode.SRC_ATOP);
        save.setOnClickListener(this);
        DbHelper dbhelper = new DbHelper(context);
        data = dbhelper.getAllLanguageDataEntity();
        if (data != null) {
            LanguageAdapter languageAdapter = new LanguageAdapter(context, data);
            //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(languageAdapter);
            int languagePos = Utility.getLanguagePositionFromSharedPreferences(context);
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
        AccountData accountData = dbhelper.getAccountData();
        if (accountData != null && !accountData.equals("")) {
            enterName.setText(accountData.getName());
            email.setText(accountData.getEmail());
            phone.setText(accountData.getPhone());
        }
    }

    private void setLanguage(int langpos) {
        if (data != null) {
            String StringCode = data.get(langpos).getCode();
            String[] parts = StringCode.split("-");
            String code = parts[0];
            String part2 = parts[1];
            Utility.setLanguageIntoSharedPreferences(context, data.get(langpos).getId(), code, langpos);
            LogAnalyticsHelper analyticsHelper = new LogAnalyticsHelper(context);
            analyticsHelper.logSetUserProperty("Language", code);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                if (isValidate()) {
                    callProfileUpdateService();
                }
                break;
        }
    }

    // ----validation -----
    private boolean isValidate() {
        String numberRegex = "[0-9]+";
        String emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        // String emailReg = "^[A-Za-z0-9_.]+(\\.[_A-Za-z0-9]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        name_string = enterName.getText().toString().trim();
        if (name_string.length() == 0) {
            Utility.alertForErrorMessage(getString(R.string.enter_name), context);
            return false;
        }
        return true;
    }

    //call create account service
    private void callProfileUpdateService() {
        final CustomProgressDialog customProgressDialog = new CustomProgressDialog(context, R.mipmap.syc);

        int languageId = Utility.getLanguageIdFromSharedPreferences(context);

        ProfileUpdateServiceRequest ServiceRequest = new ProfileUpdateServiceRequest();
        ServiceRequest.setName(name_string);
        ServiceRequest.setLanguage_id(languageId);
        if (Utility.isOnline(context)) {
            customProgressDialog.show();
            ServiceCaller sc = new ServiceCaller(context);
            sc.updateProfile(ServiceRequest, new IAsyncWorkCompletedCallback() {
                @Override
                public void onDone(String workName, boolean isComplete) {

                    if (isComplete) {
                        if (Consts.IS_DEBUG_LOG) {
                            Log.d(Consts.LOG_TAG, " callProfileUpdateService success result: " + isComplete);
                        }
                        Toast.makeText(context, getString(R.string.profile_update), Toast.LENGTH_LONG).show();
                        MainActivity rootActivity = (MainActivity) getActivity();
                        rootActivity.setProfileDetails();
                        customProgressDialog.dismiss();
                    } else {
                        Utility.alertForErrorMessage(getString(R.string.profile_not_updated), context);
                        customProgressDialog.dismiss();
                    }

                }
            });
        } else {
            Utility.alertForErrorMessage(context.getString(R.string.online_msg), context);
        }
    }
}

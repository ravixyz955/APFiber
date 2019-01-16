package com.example.user.apfiber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;

import com.example.user.apfiber.fragment.SigninFragment;
import com.example.user.apfiber.utils.DataUtils;

public class RegistrationActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        fragmentManager = getSupportFragmentManager();
//        replaceFragment(SigninFragment.newInstance(null, null));
        if (TextUtils.isEmpty(DataUtils.getMobileNumber(this))) {
            if (getIntent().hasExtra("active")) {
//            replaceFragment(OTPFragment.newInstance(null, null));
            } else if (getIntent().hasExtra("complete")) {
//            replaceFragment(ProfileFragment.newInstance(null, null));
            } else if (getIntent().hasExtra("complete")) {
//            replaceFragment(ProfileFragment.newInstance(null, null));
            } else {
                replaceFragment(SigninFragment.newInstance(null, null));
            }
        } else {
            startActivity(new Intent(this, SurveySelectionActivity.class));
            finish();
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.register_container, fragment);
        fragmentTransaction.commit();
    }
}

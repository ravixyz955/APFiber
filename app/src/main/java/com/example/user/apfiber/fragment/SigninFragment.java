package com.example.user.apfiber.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.apfiber.MainActivity;
import com.example.user.apfiber.R;
import com.example.user.apfiber.SurveySelectionActivity;
import com.example.user.apfiber.network.model.AuthenticateUserRequest;
import com.example.user.apfiber.network.model.User;
import com.example.user.apfiber.network.service.UserAPIService;
import com.example.user.apfiber.utils.DataUtils;
import com.example.user.apfiber.utils.NetworkUtils;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SigninFragment extends Fragment {

    private static final String EMAIL_PATTERN = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{4,}$";

    private EditText contactText;
    private EditText passwordText;
    private Button signinBtn;

    private UserAPIService userAPIService;

    private ProgressDialog progressDialog;

    public SigninFragment() {
        // Required empty public constructor
    }

    public static SigninFragment newInstance(String param1, String param2) {
        return new SigninFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAPIService = NetworkUtils.provideUserAPIService(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(
                R.layout.fragment_signin, container, false);
        contactText = (EditText) root.findViewById(R.id.signin_contact);
        passwordText = (EditText) root.findViewById(R.id.signin_password);
        signinBtn = (Button) root.findViewById(R.id.signin_btn);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignin();
            }
        });
        return root;
    }

    private void doSignin() {
        AuthenticateUserRequest request = new AuthenticateUserRequest();
        final String contact = contactText.getText().toString();
        final String password = passwordText.getText().toString();

        if (!TextUtils.isEmpty(contact)) {
            request.setContact(contact);
        } else {
            contactText.setError("Cannot be blank!");
            return;
        }

        if (!TextUtils.isEmpty(password)) {
            request.setPassword(password);
        } else {
            passwordText.setError("Cannot be blank!");
            return;
        }

        if (NetworkUtils.isConnectingToInternet(getActivity())) {
            progressDialog.show();
            userAPIService.authenticate(request).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Response<User> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        User user = response.body();
                        DataUtils.saveMobile(getActivity(), contact);
                        DataUtils.saveZonal(getActivity(), user.getZonal());
                        DataUtils.saveDistrict(getActivity(), user.getDistrict());
                        DataUtils.savePassword(getActivity(), password);
                        DataUtils.setActive(getActivity(), false);
                        DataUtils.saveToken(getActivity(), user.getToken());
                        startActivity(new Intent(getActivity(), SurveySelectionActivity.class));
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Failed to signin!", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No Internet!", Toast.LENGTH_LONG).show();
        }
    }
}

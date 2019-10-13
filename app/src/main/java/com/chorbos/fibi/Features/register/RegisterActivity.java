package com.chorbos.fibi.Features.register;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.chorbos.fibi.Features.main.DashBoardActivity;
import com.chorbos.fibi.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends MvpActivity<RegisterView, RegisterPresenter> implements RegisterView {



    @BindView(R.id.nameET)
    TextInputEditText nameET;

    @BindView(R.id.nameIL)
    TextInputLayout nameIL;

    @BindView(R.id.emailET)
    TextInputEditText emailET;

    @BindView(R.id.emailIL)
    TextInputLayout emailIL;

    @BindView(R.id.passwordET)
    TextInputEditText passwordET;

    @BindView(R.id.passwordIL)
    TextInputLayout passwordIL;

    @BindView(R.id.loadingView)
    ProgressBar loadingView;

    @BindView(R.id.content)
    View contentView;

    @BindView(R.id.rootView)
    View rootView;

    private Locale locale;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        passwordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.passwordET || id == EditorInfo.IME_ACTION_DONE) {
                    tryRegisterUser();
                    return true;
                }
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @NonNull
    @Override
    public RegisterPresenter createPresenter() {
        return new RegisterPresenter();
    }

    @OnClick(R.id.register_button)
    public void onViewClicked() {
        tryRegisterUser();
    }

    private void tryRegisterUser() {
        //Utility.hideKeyboard(this);
        nameIL.setError(null);
        emailIL.setError(null);
        passwordIL.setError(null);
        presenter.registerUser(nameET.getText().toString(), emailET.getText().toString(), passwordET.getText().toString(), locale);
    }

    @Override
    public void showForm() {
        loadingView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNameErrorEmpty() {
        showForm();
        nameIL.setError("Field is required");

    }

    @Override
    public void showEmailErrorEmpty() {
        showForm();
        emailIL.setError("Field is required");
    }

    @Override
    public void showEmailFormatError() {
        showForm();
        emailIL.setError("Enter a valid email");
    }

    @Override
    public void showPasswordErrorEmpty() {
        showForm();
        passwordIL.setError("Field is required");
    }

    @Override
    public void showPasswordLengthError() {
        showForm();
        passwordIL.setError("Password should have at least 4 characters");
    }


    @Override
    public void showEmployeeAlreadyExist() {
        showForm();
        emailIL.setError("There is a user already with this email");
    }

    @Override
    public void registerSuccessful() {
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showInternetError() {
        showForm();
        Snackbar.make(rootView, "Check your internet connection", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showServerError() {
        showForm();
        Snackbar.make(rootView, "Cannot connect to server", Snackbar.LENGTH_LONG).show();
    }
}

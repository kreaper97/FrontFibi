package com.chorbos.fibi.Features.login;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.chorbos.fibi.Features.main.DashBoardActivity;
import com.chorbos.fibi.Features.register.RegisterActivity;
import com.chorbos.fibi.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends MvpActivity<LoginView, LoginPresenter> implements LoginView {

    @Nullable
    @BindView(R.id.loadingView)
    ProgressBar mLoadingView;

    @Nullable
    @BindView(R.id.usernameET)
    EditText mUsernameET;

    @Nullable
    @BindView(R.id.passwordET)
    EditText mPasswordET;

    @Nullable
    @BindView(R.id.content)
    ConstraintLayout mContentView;

    @Nullable
    @BindView(R.id.rootView)
    CoordinatorLayout mRootView;

    @Nullable
    @BindView(R.id.usernameIL)
    TextInputLayout mUsernameIL;

    @Nullable
    @BindView(R.id.passwordIL)
    TextInputLayout mPasswordIL;

    private FirebaseAnalytics firebaseAnalytics;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (mPasswordET != null) {
            mPasswordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.passwordET || id == EditorInfo.IME_ACTION_DONE) {
                        tryLoginUser();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @OnClick({R.id.register_button, R.id.login_button})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.register_button:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.login_button:
                tryLoginUser();
                break;
        }
    }

    private void tryLoginUser() {
        //Utility.hideKeyboard(this);
        if (mUsernameIL != null && mPasswordIL != null && mUsernameET != null && mPasswordET != null) {
            mUsernameIL.setError(null);
            mPasswordIL.setError(null);
            presenter.loginUser(mUsernameET.getText().toString(), mPasswordET.getText().toString());
        }

    }

    @Override
    public void showForm() {
        if (mLoadingView != null && mContentView != null) {
            mLoadingView.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showLoading() {
        if (mLoadingView != null && mContentView != null) {
            mContentView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void loginSuccessful() {
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showUsernameErrorEmpty() {
        if (mUsernameIL != null) {
            mUsernameIL.setError("Insert username");
        }
    }

    @Override
    public void showPasswordErrorEmpty() {
        if (mPasswordIL != null) {
            mPasswordIL.setError("Insert password");
        }
    }

    @NonNull
    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void showInternetError() {
        showForm();
        if (mRootView != null) {
            Snackbar.make(mRootView, "Check your internet connection", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showServerError() {
        showForm();
        if (mRootView != null) {
            Snackbar.make(mRootView, "Cannot connect to server", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showUserPassError() {
        showForm();
        if (mRootView != null) {
            Snackbar.make(mRootView, "Incorrect username/password", Snackbar.LENGTH_LONG).show();
        }
    }

}

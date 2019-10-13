package com.chorbos.fibi.Features.login;

import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface LoginView extends MvpView {

    void showForm();

    void showLoading();

    void showUsernameErrorEmpty();

    void showPasswordErrorEmpty();

    void loginSuccessful();

    void showInternetError();

    void showServerError();

    void showUserPassError();
}

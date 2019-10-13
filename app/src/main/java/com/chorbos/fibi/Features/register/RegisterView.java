package com.chorbos.fibi.Features.register;

import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface RegisterView extends MvpView {

    void showForm();

    void showLoading();

    void showNameErrorEmpty();

    void showEmailErrorEmpty();

    void showEmailFormatError();

    void showPasswordErrorEmpty();

    void showPasswordLengthError();

    void showEmployeeAlreadyExist();

    void registerSuccessful();

    void showInternetError();

    void showServerError();

}

package com.chorbos.fibi.Features.register;


import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chorbos.fibi.Models.User;
import com.chorbos.fibi.Rest.ApiService;
import com.chorbos.fibi.Rest.ServiceGenerator;
import com.google.gson.JsonObject;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.io.IOException;
import java.util.Locale;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPresenter extends MvpBasePresenter<RegisterView> {


    @Nullable
    private Call<User> userCall;

    void registerUser(final String name, final String email, final String password, @NonNull Locale locale) {
        boolean fieldsErrors = checkFieldsErrors(name, email, password);
        if (!fieldsErrors) {
            onProcessStart();
            ApiService apiService = ServiceGenerator.createService(ApiService.class);
            JsonObject req = new JsonObject();
            req.addProperty("username", name);
            req.addProperty("password", password);
            req.addProperty("email", email);
            int localeId = ApiService.OTHERS_LANG;
            if (locale.getLanguage().equals("es")) {
                localeId = ApiService.SPANISH_LANG;
            }
            userCall = apiService.registerUser(String.valueOf(req));
            final int finalLocaleId = localeId;
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful()) {
                        final User user = response.body();
                        if (user != null) {
                            Realm realm = null;
                            try {
                                realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(@NonNull Realm realm) {
                                        realm.copyToRealmOrUpdate(user);
                                        onLoginSuccess();
                                    }
                                });
                            } catch (Exception e) {
                                onServerError(500, null);
                            } finally {
                                if (realm != null) {
                                    realm.close();
                                }
                            }
                        } else {
                            onServerError(500, response);
                        }
                    } else {
                        onServerError(response.code(), response);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    if (!(t instanceof IOException)) {

                    }
                    onInternetError();
                }
            });
        }
    }

    private boolean checkFieldsErrors(String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            ifViewAttached(new ViewAction<RegisterView>() {
                @Override
                public void run(@NonNull RegisterView view) {
                    view.showNameErrorEmpty();
                }
            });
            return true;
        }
        if (TextUtils.isEmpty(email)) {
            ifViewAttached(new ViewAction<RegisterView>() {
                @Override
                public void run(@NonNull RegisterView view) {
                    view.showEmailErrorEmpty();
                }
            });
            return true;
        }
        if (!isEmailValid(email)) {
            ifViewAttached(new ViewAction<RegisterView>() {
                @Override
                public void run(@NonNull RegisterView view) {
                    view.showEmailFormatError();
                }
            });
            return true;
        }
        if (TextUtils.isEmpty(password)) {
            ifViewAttached(new ViewAction<RegisterView>() {
                @Override
                public void run(@NonNull RegisterView view) {
                    view.showPasswordErrorEmpty();
                }
            });
            return true;
        }
        if (password.length() < 4) {
            ifViewAttached(new ViewAction<RegisterView>() {
                @Override
                public void run(@NonNull RegisterView view) {
                    view.showPasswordLengthError();
                }
            });
            return true;
        }
        return false;
    }


    private void onProcessStart() {
        ifViewAttached(new ViewAction<RegisterView>() {
            @Override
            public void run(@NonNull RegisterView view) {
                view.showLoading();
            }
        });
    }

    private void onLoginSuccess() {
        userCall = null;
        ifViewAttached(new ViewAction<RegisterView>() {
            @Override
            public void run(@NonNull RegisterView view) {
                view.registerSuccessful();
            }
        });
    }

    private void onServerError(final int code, final Response<User> response) {
        userCall = null;
        ifViewAttached(new ViewAction<RegisterView>() {
            @Override
            public void run(@NonNull RegisterView view) {
                if (code == 409) {
                    view.showEmployeeAlreadyExist();
                } else {
                    view.showServerError();
                }
            }
        });
    }


    private void onInternetError() {
        userCall = null;
        ifViewAttached(new ViewAction<RegisterView>() {
            @Override
            public void run(@NonNull RegisterView view) {
                view.showInternetError();
            }
        });
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void detachView() {
        if (userCall != null) {
            userCall.cancel();
            userCall = null;
        }
        super.detachView();
    }

}

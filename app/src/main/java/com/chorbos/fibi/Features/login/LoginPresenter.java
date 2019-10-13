package com.chorbos.fibi.Features.login;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chorbos.fibi.Models.User;
import com.chorbos.fibi.Rest.ApiService;
import com.chorbos.fibi.Rest.ServiceGenerator;
import com.chorbos.fibi.Service.async.SyncTrainingsAsyncTask;
import com.google.gson.JsonObject;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.io.IOException;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginPresenter extends MvpBasePresenter<LoginView> implements SyncTrainingsAsyncTask.SyncTrainingsListener{


    @Nullable
    private Call<User> userCall;

    void loginUser(final String username, final String password) {
        if (TextUtils.isEmpty(username)) {
            ifViewAttached(new ViewAction<LoginView>() {
                @Override
                public void run(@NonNull LoginView view) {
                    view.showUsernameErrorEmpty();
                }
            });
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ifViewAttached(new ViewAction<LoginView>() {
                @Override
                public void run(@NonNull LoginView view) {
                    view.showPasswordErrorEmpty();
                }
            });
            return;
        }
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            onProcessStart();
            ApiService apiService = ServiceGenerator.createService(ApiService.class);
            JsonObject req = new JsonObject();
            req.addProperty("username", username);
            req.addProperty("password", password);//25/07/2019 12:30:00
            userCall = apiService.loginUser(String.valueOf(req));
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull final Response<User> response) {
                    if (response.isSuccessful()) {
                        final User user = response.body();
                        if (user != null) {
                            Realm realm = null;
                            try {
                                realm = Realm.getDefaultInstance();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(@NonNull Realm realm) {
                                        realm.copyToRealmOrUpdate(user);
                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
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

    private void onProcessStart() {
        ifViewAttached(new ViewAction<LoginView>() {
            @Override
            public void run(@NonNull LoginView view) {
                view.showLoading();
            }
        });
    }

    private void onLoginSuccess() {
        userCall = null;
        syncDevice();
        ifViewAttached(new ViewAction<LoginView>() {
            @Override
            public void run(@NonNull LoginView view) {
                view.loginSuccessful();
            }
        });
    }

    private void syncDevice() {
        new SyncTrainingsAsyncTask(this).execute();
    }


    private void onServerError(final int code, final Response<User> response) {
        userCall = null;
        ifViewAttached(new ViewAction<LoginView>() {
            @Override
            public void run(@NonNull LoginView view) {
                if (code == 401) {
                    view.showUserPassError();
                } else {
                    view.showServerError();
                    if (response != null) {

                    }
                }
            }
        });
    }

    private void onInternetError() {
        userCall = null;
        ifViewAttached(new ViewAction<LoginView>() {
            @Override
            public void run(@NonNull LoginView view) {
                view.showInternetError();
            }
        });
    }

    @Override
    public void detachView() {
        if (userCall != null) {
            userCall.cancel();
            userCall = null;
        }
        super.detachView();
    }

    @Override
    public void syncTrainingsNowListener(boolean isSuccess, boolean isUnauthorized) {

    }
}

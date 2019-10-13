package com.chorbos.fibi.Service.async;


import android.location.Location;
import android.os.AsyncTask;

import com.chorbos.fibi.Models.User;
import com.chorbos.fibi.Rest.ApiService;
import com.chorbos.fibi.Rest.ServiceGenerator;

import java.io.IOException;
import java.lang.ref.WeakReference;

import io.realm.Realm;

public class SyncTrainingsAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final WeakReference<SyncTrainingsListener> weakListener;
    private boolean isSuccess = true;
    private boolean isUnauthorized = false;
    private Realm realm;

    public interface SyncTrainingsListener {
        void syncTrainingsNowListener(boolean isSuccess, boolean isUnauthorized);
    }

    public SyncTrainingsAsyncTask(SyncTrainingsListener listener) {
        this.weakListener = new WeakReference<>(listener);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            realm = Realm.getDefaultInstance();
            //syncTrainings();
        } catch (Exception e) {
            isSuccess = false;
        } finally {
            closeRealm();
        }
        return isSuccess;
    }

    /*private void syncTrainings() {
        final User user = realm.where(User.class).findFirst();
        if (projectId != null) {
            project = realm.where(Project.class).equalTo("id", projectId).findFirst();
        }
        final Action action = new Action(actionId, location, project, comment);
        if (user != null && user.getToken() != null) {
            String token = user.getToken();
            ApiService apiService = ServiceGenerator.createService(ApiService.class, token);
            Call<Action> actionCall = apiService.sendUserAction(action.getLocalId(), action.getAction(), action.getDateApiFormat(), action.getCoordinates(), projectId, true, comment);
            try {
                Response<Action> actionResponse = actionCall.execute();
                if (actionResponse.isSuccessful() && actionResponse.body() != null) {
                    final Action actionFromApi = actionResponse.body();
                    if (actionFromApi != null) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                action.setSync(true);
                                Date createDate = actionFromApi.getCreateDate();
                                action.setCreateDate(createDate);
                                realm.copyToRealmOrUpdate(action);
                                RealmResults<Action> actionRealmResults = realm.where(Action.class).sort("createDate", Sort.DESCENDING).findAll();
                                if (actionRealmResults.size() > 0) {
                                    /*Action lastAction = actionRealmResults.first();
                                    user.setLastAction(lastAction);
                                }
                            }
                        });
                    }
                } else {
                    isSuccess = false;
                    if (actionResponse.code() == 401) {
                        isUnauthorized = true;
                    }
                }
            } catch (IOException e) {
                isSuccess = false;
            } catch (Exception e) {
                isSuccess = false;
            }
        } else {
            isSuccess = false;
        }
}*/


    @Override
    protected void onPostExecute(Boolean isSuccess) {
        SyncTrainingsListener listener = weakListener.get();
        if (listener != null) {
            listener.syncTrainingsNowListener(isSuccess, isUnauthorized);
        }
    }

    @Override
    protected void onCancelled() {
        closeRealm();
        super.onCancelled();
    }

    private void closeRealm() {
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }
}

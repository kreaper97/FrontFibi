package com.chorbos.fibi.Service.async;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chorbos.fibi.Models.Aula;
import com.chorbos.fibi.Models.Training;

import org.apache.poi.ss.formula.functions.T;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class SyncUserProfileAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final WeakReference<SyncUserProfileAsyncTaskListener> weakListener;
    private Realm realm;
    private boolean isSuccess = true;
    private static final String TAG = "SyncUserProfileAsyncTask";

    public interface SyncUserProfileAsyncTaskListener {
        void onProfileSync(boolean isSuccess);
    }

    public SyncUserProfileAsyncTask(SyncUserProfileAsyncTaskListener listener) {
        this.weakListener = new WeakReference<>(listener);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            isSuccess = false;
        } finally {
            closeRealm();
        }
        return isSuccess;
    }

    private void deleteRealm(Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.deleteAll();
            }
        });
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        SyncUserProfileAsyncTaskListener syncCompanyAndAnctionListener = weakListener.get();
        if (syncCompanyAndAnctionListener != null) {
            syncCompanyAndAnctionListener.onProfileSync(isSuccess);
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


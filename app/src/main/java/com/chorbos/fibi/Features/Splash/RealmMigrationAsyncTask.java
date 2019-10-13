package com.chorbos.fibi.Features.Splash;


import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.IntDef;

import com.chorbos.fibi.Models.User;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

import io.realm.Realm;

public class RealmMigrationAsyncTask extends AsyncTask<Context, Void, Integer> {


    // Define the list of accepted constants and declare the StartMode annotation
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({START_LOGIN, START_MAIN})
    public @interface StartMode {
    }


    public interface RealmMigrationListener {
        void onRealmMigrationDone(@StartMode Integer startMode);
    }

    static final int START_LOGIN = 0;
    static final int START_MAIN = 1;

    private WeakReference<RealmMigrationListener> weakListener;

    public RealmMigrationAsyncTask(RealmMigrationListener listener) {
        this.weakListener = new WeakReference<>(listener);
    }

    @Override
    protected Integer doInBackground(Context... contexts) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            User user = realm.where(User.class).findFirst();
            if (user != null) {
                return START_MAIN;
            } else {
                return START_LOGIN;
            }

        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @Override
    protected void onPostExecute(@StartMode Integer startMode) {
        if (weakListener.get() != null) {
            weakListener.get().onRealmMigrationDone(startMode);
        }
    }
}

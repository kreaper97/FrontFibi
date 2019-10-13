package com.chorbos.fibi.Application;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;
import com.chorbos.fibi.Migration.MigrationApp;
import com.chorbos.fibi.Models.Training;
import com.chorbos.fibi.Models.User;
import com.chorbos.fibi.Service.SyncAssignmentsJob;
import com.chorbos.fibi.Service.SyncUserProfileJob;
import com.google.firebase.FirebaseApp;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FibiApp extends MultiDexApplication {
    private static final String TAG = "FibiApp";

    @NonNull
    public static RealmConfiguration getRealmConfiguration() {
        return new RealmConfiguration.Builder()
                .compactOnLaunch()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .migration(new MigrationApp())
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        Realm.init(this);
        Realm.setDefaultConfiguration(getRealmConfiguration());
        //SyncUserProfileJob.scheduleJobRecurring(this);
    }
}

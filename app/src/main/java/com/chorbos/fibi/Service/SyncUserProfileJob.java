package com.chorbos.fibi.Service;

import android.content.Context;

import com.chorbos.fibi.Service.async.SyncTrainingsAsyncTask;
import com.chorbos.fibi.Service.async.SyncUserProfileAsyncTask;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class SyncUserProfileJob extends JobService implements SyncUserProfileAsyncTask.SyncUserProfileAsyncTaskListener {
    public static final String TAG = "SyncUserProfileJob";
    public static final String TAG_JOB_NOW = "SyncUserProfileJob-NOW";
    private JobParameters job;
    private SyncUserProfileAsyncTask syncUserProfileAsyncTask;

    public static void scheduleJobRecurring(Context context) {
        // Create a new dispatcher using the Google Play driver.
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job myJob = dispatcher.newJobBuilder()
                .setService(SyncUserProfileJob.class) // the JobService that will be called
                .setTag(SyncUserProfileJob.TAG)        // uniquely identifies the job
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(86400, 129600))
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        dispatcher.mustSchedule(myJob);
    }

    public static void scheduleJobNow(Context context) {
        // Create a new dispatcher using the Google Play driver.
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job myJob = dispatcher.newJobBuilder()
                .setService(SyncUserProfileJob.class) // the JobService that will be called
                .setTag(SyncUserProfileJob.TAG_JOB_NOW)        // uniquely identifies the job
                .setRecurring(false)
                .setTrigger(Trigger.NOW)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        dispatcher.mustSchedule(myJob);
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        this.job = job;
        syncUserProfileAsyncTask = new SyncUserProfileAsyncTask(this);
        syncUserProfileAsyncTask.execute();
        return true;

    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (syncUserProfileAsyncTask != null) {
            syncUserProfileAsyncTask.cancel(true);
        }
        return true;
    }

    @Override
    public void onProfileSync(boolean isSuccess) {

    }
}

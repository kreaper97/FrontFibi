package com.chorbos.fibi.Service;

import android.content.Context;

import com.chorbos.fibi.Service.async.SyncAssignmentsAsyncTask;
import com.chorbos.fibi.Service.async.SyncTrainingsAsyncTask;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class SyncAssignmentsJob extends JobService implements SyncAssignmentsAsyncTask.SyncAssignmentsListener{
    public static final String TAG = "SyncAssigmnetsJob";
    public static final String TAG_JOB_NOW = "SyncAssignmentsJob-NOW";
    private JobParameters job;
    private SyncAssignmentsAsyncTask syncAssignmentsAsyncTask;

    public static void scheduleJobRecurring(Context context) {
        // Create a new dispatcher using the Google Play driver.
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job myJob = dispatcher.newJobBuilder()
                .setService(SyncAssignmentsJob.class) // the JobService that will be called
                .setTag(SyncAssignmentsJob.TAG)        // uniquely identifies the job
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
                .setService(SyncAssignmentsJob.class) // the JobService that will be called
                .setTag(SyncAssignmentsJob.TAG_JOB_NOW)        // uniquely identifies the job
                .setRecurring(false)
                .setTrigger(Trigger.NOW)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        dispatcher.mustSchedule(myJob);
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        this.job = job;
        syncAssignmentsAsyncTask = new SyncAssignmentsAsyncTask(this);
        syncAssignmentsAsyncTask.execute();
        return true;

    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (syncAssignmentsAsyncTask != null) {
            syncAssignmentsAsyncTask.cancel(true);
        }
        return true;
    }

    @Override
    public void syncAssignmentsNowListener(boolean isSuccess, boolean isUnauthorized) {

    }
}

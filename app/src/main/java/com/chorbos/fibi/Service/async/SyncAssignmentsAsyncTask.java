package com.chorbos.fibi.Service.async;

    import android.os.AsyncTask;
    import java.lang.ref.WeakReference;

    import io.realm.Realm;

public class SyncAssignmentsAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final WeakReference<SyncAssignmentsListener> weakListener;
    private boolean isSuccess = true;
    private boolean isUnauthorized = false;
    private Realm realm;

    public interface SyncAssignmentsListener {
        void syncAssignmentsNowListener(boolean isSuccess, boolean isUnauthorized);
    }

    public SyncAssignmentsAsyncTask(SyncAssignmentsListener listener) {
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



    @Override
    protected void onPostExecute(Boolean isSuccess) {
        SyncAssignmentsListener listener = weakListener.get();
        if (listener != null) {
            listener.syncAssignmentsNowListener(isSuccess, isUnauthorized);
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

package com.chorbos.fibi.Features.assignmentsFeed;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chorbos.fibi.Features.main.ui.profile.ProfileFragment;
import com.chorbos.fibi.Models.Assignment;
import com.chorbos.fibi.Models.User;
import com.chorbos.fibi.R;
import com.chorbos.fibi.Rest.ApiService;

import org.apache.poi.ss.formula.functions.T;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class UserAssignmentsDialogFragment extends DialogFragment implements AssignmentsRecyclerViewAdapter.OnListAssignmentInteractionListener {

    private RecyclerView mList;
    private UserAssignmentDialogListener mListener;
    private Realm realm;
    private User user;
    private View mProgressView;
    private View mFormView;
    private AssignmentsRecyclerViewAdapter mAdapter;
    private RealmResults<Assignment> assignmentRealmResults;
    @Override
    public void onListAssignmentInteraction(String ass_name) {
        mListener.onAssignmentSelected(ass_name);
        try {
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface UserAssignmentDialogListener {
        void onAssignmentSelected(String amend);

    }

    public static UserAssignmentsDialogFragment newInstance() {
        return new UserAssignmentsDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (UserAssignmentDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement UserAssignmentDialogListener");
        }
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View rootView = inflater.inflate(R.layout.dialog_assigments, null);
        mProgressView = rootView.findViewById(R.id.progressView);
        mFormView = rootView.findViewById(R.id.formView);
        mList = rootView.findViewById(R.id.list);
        builder.setView(rootView);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        if (user != null) {
            assignmentRealmResults = realm.where(Assignment.class)
                    .findAllAsync();
            mAdapter = new AssignmentsRecyclerViewAdapter(this,assignmentRealmResults,true);
            showProgress(false);
            if (getContext() != null) {
                mList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                mList.setAdapter(mAdapter);
                mList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            }
        }
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public boolean isReady() {
        return getContext() != null && isAdded();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }
}

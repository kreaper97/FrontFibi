package com.chorbos.fibi.Features.main.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.chorbos.fibi.Features.assignmentsFeed.UserAssignmentsDialogFragment;
import com.chorbos.fibi.Features.main.TrainingAdapter;
import com.chorbos.fibi.Features.main.ui.profile.ProfileViewModel;
import com.chorbos.fibi.Models.Assignment;
import com.chorbos.fibi.Models.Training;
import com.chorbos.fibi.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Response;

public class SearchFragment extends Fragment implements TrainingAdapter.OnListActionInteractionListener {

    RecyclerView datalist;
    TextView emptyView;
    private Realm realm;
    Spinner spinner;
    private RealmResults<Training> trainingsRealmResults;
    private TrainingAdapter adapter;
    private ProfileViewModel profileViewModel;
    private ArrayAdapter<String> spinneradapter;
    private ArrayList<String> spinnerArray = new ArrayList<>();
    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        trainingsRealmResults = realm.where(Training.class)
                .findAllAsync();

        //profileViewModel =
        // ViewModelProviders.of(this).get(ProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_search, container, false);
        datalist = root.findViewById(R.id.datalist);
        emptyView = root.findViewById(R.id.emptyView);
        spinner = root.findViewById(R.id.spinnerAssignment);
        RealmResults<Assignment> assignmentRealm = realm.where(Assignment.class).findAll();
        spinnerArray.add("");
        for(Assignment assignment : assignmentRealm){
            spinnerArray.add(assignment.getName());
        }
        spinneradapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        spinner.setAdapter(spinneradapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinner.getItemAtPosition(i).toString().equals("")){
                    trainingsRealmResults = realm.where(Training.class)
                            .findAllAsync();
                    adapter.updateData(trainingsRealmResults);
                }else {
                    trainingsRealmResults = realm.where(Training.class)
                            .equalTo("assignment", spinner.getSelectedItem().toString())
                            .findAllAsync();
                    adapter.updateData(trainingsRealmResults);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (adapter == null) {
            adapter = new TrainingAdapter(getActivity().getApplicationContext(), trainingsRealmResults, this);
            datalist.setAdapter(adapter);
            datalist.setHasFixedSize(true);
            datalist.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        } else {
            adapter.updateData(trainingsRealmResults);
        }
        trainingsRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Training>>() {
            @Override
            public void onChange(@NonNull RealmResults<Training> actions) {
                if (actions.isEmpty()) {
                    showEmptyView();
                } else {
                    adapter.updateData(actions);
                    showForm();
                }
            }
        });
        datalist.setAdapter(adapter);
        return root;
    }
    private void showForm() {
        if (emptyView != null && datalist != null) {
            emptyView.setVisibility(View.GONE);
            datalist.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyView() {
        if (emptyView != null && datalist != null) {
            datalist.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }
    private void showAssignmentsTypes() {
        DialogFragment dialog = UserAssignmentsDialogFragment.newInstance();

        dialog.show(getFragmentManager(), "UserAssignmentsDialogFragment");
    }

    @Override
    public void onListActionInteraction(String id) {

    }

    @Override
    public void onApplySuccess() {

    }

    @Override
    public void onApplyError(int errorCode, String error) {
        Snackbar.make(root,error,Snackbar.LENGTH_LONG).show();
    }

}
package com.chorbos.fibi.Features.main.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chorbos.fibi.Features.main.MyTrainingAdapter;
import com.chorbos.fibi.Features.main.TrainingAdapter;
import com.chorbos.fibi.Models.Assignment;
import com.chorbos.fibi.Models.Training;
import com.chorbos.fibi.Models.User;
import com.chorbos.fibi.R;
import com.chorbos.fibi.Rest.ApiService;
import com.chorbos.fibi.Rest.ServiceGenerator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment implements MyTrainingAdapter.OnListActionInteractionListener {
    private Realm realm;
    RecyclerView datalist;
    TextView emptyView;
    private RealmResults<Training> trainingsRealmResults;
    TextView rateTV;
    TextView koinTV;
    Switch aSwitch;
    private MyTrainingAdapter adapter;
    private DashboardViewModel dashboardViewModel;
    SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "DashboardFragment";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        datalist = root.findViewById(R.id.datalist);
        emptyView = root.findViewById(R.id.emptyView);
        rateTV = root.findViewById(R.id.rate);
        koinTV = root.findViewById(R.id.koinremaining);
        realm = Realm.getDefaultInstance();
        aSwitch = root.findViewById(R.id.mswitch);
        User user = realm.where(User.class).findFirst();
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    trainingsRealmResults = realm.where(Training.class)
                            .equalTo("creatorId",user.getUserId())
                            .findAllAsync();
                    adapter.updateData(trainingsRealmResults);
                }else{
                    trainingsRealmResults = realm.where(Training.class)
                            .equalTo("studentId",user.getUserId())
                            .findAllAsync();
                    adapter.updateData(trainingsRealmResults);
                }
            }
        });

        rateTV.setText(String.valueOf(user.getReputation()));
        koinTV.setText(String.valueOf(user.getKoins()));
        swipeRefreshLayout=(SwipeRefreshLayout)root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                syncTrainings();
                trainingsRealmResults = realm.where(Training.class)
                        .equalTo("creatorId",user.getUserId())
                        .findAllAsync();
                adapter.updateData(trainingsRealmResults);

            }
        });
        trainingsRealmResults = realm.where(Training.class)
                .equalTo("creatorId",user.getUserId())
                .findAllAsync();
        if (adapter == null) {
            adapter = new MyTrainingAdapter(getContext(), trainingsRealmResults, this);
            datalist.setAdapter(adapter);
            datalist.setHasFixedSize(true);
            datalist.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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

    private void syncTrainings() {
            ApiService apiService = ServiceGenerator.createService(ApiService.class);
            Call<RealmList<Training>> trainingsCall = apiService.getTrainings();
            trainingsCall.enqueue(new Callback<RealmList<Training>>() {
                @Override
                public void onResponse(Call<RealmList<Training>> call, Response<RealmList<Training>> response) {
                    if (response.isSuccessful()) {
                        RealmList<Training> trainings = response.body();
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                for (Training training: trainings) {
                                    Training traininginRealm = realm.where(Training.class).equalTo("trainId",training.getTrainId()).findFirst();
                                    if(traininginRealm != null) {
                                        if(traininginRealm.getAssignment() == null) {
                                            ArrayList<Assignment> arrayList = new ArrayList<>();
                                            arrayList.addAll(realm.where(Assignment.class).findAll());
                                            ArrayList<String> finalarray = new ArrayList<>();
                                            for (Assignment assignment : arrayList) {
                                                finalarray.add(assignment.getName());
                                            }
                                            Collections.shuffle(finalarray);
                                            training.setAssignment(finalarray.get(0));
                                        }else{
                                            training.setAssignment(traininginRealm.getAssignment());
                                        }
                                    }
                                    realm.copyToRealmOrUpdate(training);
                                }

                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<RealmList<Training>> call, Throwable t) {

                }
            });

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


    @Override
    public void onListActionInteraction(String id) {

    }

    @Override
    public void onApplySuccess() {

    }

    @Override
    public void onApplyError(int errorCode, String response) {

    }
}
package com.chorbos.fibi.Features.main.ui.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.chorbos.fibi.Features.main.TrainingAdapter;
import com.chorbos.fibi.Features.main.ui.add.AddViewModel;
import com.chorbos.fibi.Models.Training;
import com.chorbos.fibi.Models.User;
import com.chorbos.fibi.R;
import com.chorbos.fibi.Rest.ApiService;
import com.chorbos.fibi.Rest.ServiceGenerator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

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

public class AddFragment extends Fragment implements TrainingAdapter.OnListActionInteractionListener {

    private Realm realm;
    RecyclerView datalist;
    private RealmResults<Training> trainingsRealmResults;
    EditText ass,koins,day,hour,aula;
    private TrainingAdapter adapter;
    private AddViewModel addViewModel;
    Button create;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addViewModel =
                ViewModelProviders.of(this).get(AddViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add, container, false);
        ass = root.findViewById(R.id.assignment);
        koins = root.findViewById(R.id.koins);
        day = root.findViewById(R.id.day);
        hour = root.findViewById(R.id.hora);
        aula = root.findViewById(R.id.aula);
        create = root.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm = Realm.getDefaultInstance();
                User user = realm.where(User.class).findFirst();
                JsonObject req = new JsonObject();
                req.addProperty("creatorId", user.getUserId());
                req.addProperty("time", hour.getText().toString());
                req.addProperty("date", day.getText().toString());
                req.addProperty("price", koins.getText().toString());
                req.addProperty("aula", aula.getText().toString());//25/07/2019 12:30:00
                ApiService apiService = ServiceGenerator.createService(ApiService.class);
                Call<Training> trainingCall = apiService.createAssignment(String.valueOf(req));
                trainingCall.enqueue(new Callback<Training>() {
                    @Override
                    public void onResponse(Call<Training> call, Response<Training> response) {
                        if(response.isSuccessful()){
                            final Training training = response.body();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    training.setAssignment(ass.getText().toString());
                                    realm.copyToRealmOrUpdate(training);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Training> call, Throwable t) {

                    }
                });
            }
        });
        return root;
    }


    @Override
    public void onListActionInteraction(String id) {

    }

    @Override
    public void onApplySuccess() {
        Snackbar.make(getView(), "Apply succes!!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onApplyError(int errorCode, String response) {
        Snackbar.make(getView(), response, Snackbar.LENGTH_LONG).show();
    }
}
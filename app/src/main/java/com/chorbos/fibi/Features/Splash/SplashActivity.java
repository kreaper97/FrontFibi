package com.chorbos.fibi.Features.Splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.chorbos.fibi.Features.login.LoginActivity;
import com.chorbos.fibi.Features.main.DashBoardActivity;
import com.chorbos.fibi.Models.Assignment;
import com.chorbos.fibi.Models.Training;
import com.chorbos.fibi.R;
import com.chorbos.fibi.Rest.ApiService;
import com.chorbos.fibi.Rest.ServiceGenerator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity implements RealmMigrationAsyncTask.RealmMigrationListener {

    @Nullable
    private Call<RealmList<Training>> trainingsCall;
    private Call<RealmList<Assignment>> assignmentsCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //animationView = findViewById(R.id.animation_view);
        //startCheckAnimation();
        syncTrainings();
        syncAssignments();
        new RealmMigrationAsyncTask(this).execute(this);
    }
    private void syncAssignments(){
        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        assignmentsCall = apiService.getAssignments();
        assignmentsCall.enqueue(new Callback<RealmList<Assignment>>() {
            @Override
            public void onResponse(Call<RealmList<Assignment>> call, Response<RealmList<Assignment>> response) {
                if (response.isSuccessful()) {
                    RealmList assignments = response.body();
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(assignments);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RealmList<Assignment>> call, Throwable t) {

            }
        });
    }

    private void syncTrainings() {
        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        trainingsCall = apiService.getTrainings();
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onRealmMigrationDone(@RealmMigrationAsyncTask.StartMode Integer startMode) {
        Intent intent = null;
        if (startMode == RealmMigrationAsyncTask.START_LOGIN) {
            intent = new Intent(this, LoginActivity.class);
        } else if (startMode == RealmMigrationAsyncTask.START_MAIN) {
            intent = new Intent(this, DashBoardActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

}

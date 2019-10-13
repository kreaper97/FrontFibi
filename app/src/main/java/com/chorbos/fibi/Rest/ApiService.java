package com.chorbos.fibi.Rest;


import androidx.annotation.NonNull;

import com.chorbos.fibi.Models.Assignment;
import com.chorbos.fibi.Models.Aula;
import com.chorbos.fibi.Models.Training;
import com.chorbos.fibi.Models.User;

import java.util.List;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    String DEVICE_TYPE = "android";

    int OTHERS_LANG = 1;

    int SPANISH_LANG = 2;

    //POST

    @NonNull
    @POST("users/register")
    Call<User> registerUser(
            @Body String data
    );

    @NonNull
    @Headers("Content-Type: application/json")
    @POST("users/login")
    Call<User> loginUser(
            @Body String data
    );


    @NonNull
    @Headers("Content-Type: application/json")
    @POST("trainingsScheduled")
    Call<Training> applyAssignment(
            @Body String data
    );

    @NonNull
    @Headers("Content-Type: application/json")
    @POST("trainingAds")
    Call<Training> createAssignment(
            @Body String data
    );


    //GET

    @GET("users")
    Call<RealmList<User>> getUsers();

    @NonNull
    @FormUrlEncoded
    @GET("users/{id}")
    Call<User> getUser(
            @Path("id") int id
    );

    @NonNull
    @Headers("Content-Type: application/json")
    @GET("trainingAds")
    Call<RealmList<Training>> getTrainings();

    @NonNull
    @Headers("Content-Type: application/json")
    @GET("assignments")
    Call<RealmList<Assignment>> getAssignments();

    @NonNull
    @Headers("Content-Type: application/json")
    @GET("classes")
    Call<RealmList<Aula>> getClasses();

}


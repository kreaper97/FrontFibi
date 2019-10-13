package com.chorbos.fibi.Models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @NonNull
    @PrimaryKey
    @SerializedName("userId")
    private String userId;

    @SerializedName("username")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("reputation")
    private Double reputation;

    @SerializedName("koins")
    private int koins;


    public int getKoins() {
        return koins;
    }

    public void setKoins(int koins) {
        this.koins = koins;
    }
    public Double getReputation() {
        return reputation;
    }


    public void setReputation(Double reputation) {
        this.reputation = reputation;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


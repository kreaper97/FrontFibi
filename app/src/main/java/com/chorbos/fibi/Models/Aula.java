package com.chorbos.fibi.Models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Aula extends RealmObject {

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }
    public Aula(){}
    public Aula(String name){
        this.name = name;
    }

    @NonNull
    @PrimaryKey
    @SerializedName("name")
    private String name;


}


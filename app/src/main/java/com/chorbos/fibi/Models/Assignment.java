package com.chorbos.fibi.Models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Assignment extends RealmObject {

    @NonNull
    @PrimaryKey
    @SerializedName("assignmentName")
    private String name;

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }
}


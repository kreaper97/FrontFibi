package com.chorbos.fibi.Models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Training extends RealmObject {

    @NonNull
    @PrimaryKey
    @SerializedName("trainId")
    private String trainId;


    @SerializedName("professorId")
    private String professorId;

    @SerializedName("studentid")
    private String studentId;

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @SerializedName("creatorId")
    private String creatorId;

    @SerializedName("price")
    private int koins_price;

    @SerializedName("crated_date")
    private String created_date;

    @SerializedName("train_date")
    private String assignment_date;

    private String assignment;

    @SerializedName("aula")
    private String assignment_class;



    public Training(String trainId,String professorId, String studentId, String assignment, int koins_price,String assignment_class,String created_date,String assignment_date){
        this.trainId = trainId;
        this.professorId = professorId;
        this.studentId = studentId;
        this.assignment = assignment;
        this.koins_price = koins_price;
        this.assignment_class =  assignment_class;
        this.created_date = created_date;
        this.assignment_date = assignment_date;
    }
    public Training(){}



    @NonNull
    public String getTrainId() {
        return trainId;
    }


    public void setTrainId(@NonNull String userId) {
        this.trainId = userId;
    }

    public String getProfessor() {
        return professorId;
    }

    public void setProfessor(String professor) {
        this.professorId = professor;
    }

    public String getStudent() {
        return studentId;
    }

    public void setStudent(String student) {
        this.studentId = student;
    }

    public int getKoins_price() {
        return koins_price;
    }

    public void setKoins_price(int koins_price) {
        this.koins_price = koins_price;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getAssignment_date() {
        return assignment_date;
    }

    public void setAssignment_date(String assignment_date) {
        this.assignment_date = assignment_date;
    }

    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    public String getAssignment_class() {
        return assignment_class;
    }

    public void setAssignment_class(String assignment_class) {
        this.assignment_class = assignment_class;
    }


}


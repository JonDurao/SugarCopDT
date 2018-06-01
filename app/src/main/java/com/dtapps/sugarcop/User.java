package com.dtapps.sugarcop;

import java.io.Serializable;

/**
 * Created by jon.durao on 10/3/2017.
 */

public class User implements Serializable {
    public static final long SerialVersionUID = 201710031052L;

    private String mGenre, mName, mSurname, mUsername, mHeight;
    private float mSugar, mWeight;
    private int mAge;

    public User() {}

    public User(String name, String surname, String username, String genre, float sugar, float weight, int age, String height) {
        this.mAge = age;
        this.mGenre = genre;
        this.mHeight = height;
        this.mName = name;
        this.mSugar = sugar;
        this.mSurname = surname;
        this.mUsername = username;
        this.mWeight = weight;
    }

    public int getmAge() {
        return mAge;
    }

    public String getmGenre() {
        return mGenre;
    }

    public String getmHeight() {
        return mHeight;
    }

    public String getmName() {
        return mName;
    }

    public float getmSugar() {
        return mSugar;
    }

    public String getmSurname() {
        return mSurname;
    }

    public String getmUsername() {
        return mUsername;
    }

    public float getmWeight() {
        return mWeight;
    }

    public void setmGenre(String mGenre) {
        this.mGenre = mGenre;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmSurname(String mSurname) {
        this.mSurname = mSurname;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void setmSugar(float mSugar) {
        this.mSugar = mSugar;
    }

    public void setmWeight(float mWeight) {
        this.mWeight = mWeight;
    }

    public void setmAge(int mAge) {
        this.mAge = mAge;
    }

    public void setmHeight(String mHeight) {
        this.mHeight = mHeight;
    }

    @Override
    public String toString() {
        return getmName() + " | " + getmSurname() + " | " + getmSugar() + " | " + getmAge();
    }
}

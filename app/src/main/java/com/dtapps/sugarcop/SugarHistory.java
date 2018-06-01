package com.dtapps.sugarcop;

import java.io.Serializable;

/**
 * Created by jon.durao on 11/2/2017.
 */

public class SugarHistory implements Serializable {
    public static final long SerialVersionUID = 201711021056L;

    private String mSugar, mDate;

    public SugarHistory() {
    }

    public SugarHistory(String mSugar, String mDate) {
        this.mSugar = mSugar;
        this.mDate = mDate;
    }

    public String getmSugar() {
        return mSugar;
    }

    public void setmSugar(String mSugar) {
        this.mSugar = mSugar;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }
}

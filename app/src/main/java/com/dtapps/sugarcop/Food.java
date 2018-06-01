package com.dtapps.sugarcop;

import java.io.Serializable;

/**
 * Created by jon.durao on 10/5/2017.
 */

public class Food implements Serializable {
    public static final long SerialVersionUID = 201710231524L;
    private float mSugarPerHundred, mSugarPerPortion, mSugarPerItem, mQuantity, mQuantityPortion;
    private int mConfirmed, mConfirmedByThisUser;
    private String mId, mName, mBrand, mCategories, mBarcode, mUnit, mNormilizedName, mNormilizedBrand;

    public Food() {}

    public Food(String id, String name, String brand, String categories, String barcode, String unit,
                float sugarPerHundred, float sugarPerPortion, float sugarPerItem, float quantityPortion,
                float quantity, int confirmed, int confirmedByThisUser, String normalizedName, String normalizedBrand) {
        this.mId = id;
        this.mName = name;
        this.mBrand = brand;
        this.mCategories = categories;
        this.mBarcode = barcode;
        this.mUnit = unit;
        this.mSugarPerHundred = sugarPerHundred;
        this.mSugarPerPortion = sugarPerPortion;
        this.mQuantityPortion = quantityPortion;
        this.mSugarPerItem = sugarPerItem;
        this.mQuantity = quantity;
        this.mConfirmed = confirmed;
        this.mConfirmedByThisUser = confirmedByThisUser;
        this.mNormilizedName = normalizedName;
        this.mNormilizedBrand = normalizedBrand;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmBrand() {
        return mBrand;
    }

    public void setmBrand(String mBrand) {
        this.mBrand = mBrand;
    }

    public String getmCategories() {
        return mCategories;
    }

    public void setmCategories(String mCategories) {
        this.mCategories = mCategories;
    }

    public String getmBarcode() {
        return mBarcode;
    }

    public void setmBarcode(String mBarcode) {
        this.mBarcode = mBarcode;
    }

    public String getmUnit() {
        return mUnit;
    }

    public void setmUnit(String mUnit) {
        this.mUnit = mUnit;
    }

    public float getmSugarPerHundred() {
        return mSugarPerHundred;
    }

    public void setmSugarPerHundred(float mSugarPerHundred) {
        this.mSugarPerHundred = mSugarPerHundred;
    }

    public float getmSugarPerItem() {
        return mSugarPerItem;
    }

    public void setmSugarPerItem(float mSugarPerItem) {
        this.mSugarPerItem = mSugarPerItem;
    }

    public float getmSugarPerPortion() {
        return mSugarPerPortion;
    }

    public void setmSugarPerPortion(float mSugarPerPortion) {
        this.mSugarPerPortion = mSugarPerPortion;
    }

    public float getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(float mQuantity) {
        this.mQuantity = mQuantity;
    }

    public float getmQuantityPortion() {
        return mQuantityPortion;
    }

    public void setmQuantityPortion(float mQuantityPortion) {
        this.mQuantityPortion = mQuantityPortion;
    }

    public int getmConfirmed() {
        return mConfirmed;
    }

    public void setmConfirmed(int mConfirmed) {
        this.mConfirmed = mConfirmed;
    }

    public int getmConfirmedByThisUser() {
        return mConfirmedByThisUser;
    }

    public void setmConfirmedByThisUser(int mConfirmedByThisUser) {
        this.mConfirmedByThisUser = mConfirmedByThisUser;
    }

    public String getmNormilizedName() {
        return mNormilizedName;
    }

    public void setmNormilizedName(String mNormilizedName) {
        this.mNormilizedName = mNormilizedName;
    }

    public String getmNormilizedBrand() {
        return mNormilizedBrand;
    }

    public void setmNormilizedBrand(String mNormilizedBrand) {
        this.mNormilizedBrand = mNormilizedBrand;
    }
}

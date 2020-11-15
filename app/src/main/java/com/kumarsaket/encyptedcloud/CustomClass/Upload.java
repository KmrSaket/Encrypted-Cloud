package com.kumarsaket.encyptedcloud.CustomClass;

public class Upload {

    private String mName;
    private String mImageUrl;
    private String mKeyName;

    public Upload() {
//        empty const. needed
    }


    public Upload(String mName, String mImageUrl, String mKeyName) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.mKeyName = mKeyName;
    }


    public String getmKeyName() {
        return mKeyName;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}

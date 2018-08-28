package com.sunxy.hermes.core.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class Responce implements Parcelable{

    //    响应的对象
    private String data;

    public String getData(){
        return data;
    }

    public Responce(String data) {
        this.data = data;
    }

    protected Responce(Parcel in) {
        data = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Responce> CREATOR = new Creator<Responce>() {
        @Override
        public Responce createFromParcel(Parcel in) {
            return new Responce(in);
        }

        @Override
        public Responce[] newArray(int size) {
            return new Responce[size];
        }
    };

}

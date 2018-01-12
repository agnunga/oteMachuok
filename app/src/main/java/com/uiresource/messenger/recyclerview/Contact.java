package com.uiresource.messenger.recyclerview;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Created by Ag.
 */

public class Contact implements Serializable{
    public long id;
    public String number;
    String name;
    int image;


    public int getImage(){
        return image;
    }
    public void setImage(@DrawableRes int img){
        image = img;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

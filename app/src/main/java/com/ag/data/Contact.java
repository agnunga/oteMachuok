package com.ag.data;

import android.graphics.Bitmap;

public class Contact {
    private Long id;
    private String name;
    private String number;
    private Bitmap image;
    private String photoUri;
    private String thumbNailUri;

    private String address;
    /**
     * We don't want to serialize it. :) because we can't serialize it.
     */
    private transient Bitmap photo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getThumbNailUri() {
        return thumbNailUri;
    }

    public void setThumbNailUri(String thumbNailUri) {
        this.thumbNailUri = thumbNailUri;
    }

    public Contact(){

    }

    public Contact(Long id, String name, String number, Bitmap image) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.image = image;
    }

    public static Contact parseCached(String s) {
        String[] line = s.split("\t");
        Long recipientId = new Long(line[0]);
        String name = line[1];
        String number = line[2];
        return new Contact(recipientId, name, number, null);
    }

    @Override
    public String toString() {
        return id + "\t" + name + "\t" + number;
    }

    public String getFormatted() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" <");
        sb.append(number);
        sb.append(">");
        return sb.toString();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}

/*

Contacts DB fields:

times_contacted
primary_organization
phonetic_name
type
mode
last_time_contacted
_sync_time
_id
_sync_id
number_key
primary_email
name
sort_string
primary_phone
im_account
_sync_account
_sync_version
send_to_voicemail
custom_ringtone
status
_sync_local_id
number
label
display_name
_sync_dirty
im_handle
starred
notes
im_protocol
*/

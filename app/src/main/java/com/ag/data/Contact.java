package com.ag.data;

public class Contact {
    private long id;
    private String name;
    private String number;
    private int image;
    private String photoUri;
    private String thumbNailUri;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
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

    public Contact(long id, String name, String number, int image) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.image = image;
    }

    public static Contact parseCached(String s) {
        String[] line = s.split("\t");
        int recipientId = Integer.parseInt(line[0]);
        String name = line[1];
        String number = line[2];
        int image = Integer.parseInt(line[3]);
        return new Contact(recipientId, name, number, image);
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

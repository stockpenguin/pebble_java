package com.stockpenguin.pebble.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchDialogDataHolder implements Parcelable {
    private PebbleList<String> photoUrls;
    private PebbleList<String> usernames;
    private PebbleList<String> uids;

    /* data to be passed */
    private String photoUrl;
    private String username;
    private String uid;

    public SearchDialogDataHolder() {
        photoUrls = new PebbleList<>();
        usernames = new PebbleList<>();
        uids = new PebbleList<>();
    }

    public void clearLists() {
        this.photoUrls.clear();
        this.usernames.clear();
        this.uids.clear();
    }

    protected SearchDialogDataHolder(Parcel parcel) {
        photoUrl = parcel.readString();
        username = parcel.readString();
        uid = parcel.readString();
    }

    public static final Creator<SearchDialogDataHolder> CREATOR = new Creator<SearchDialogDataHolder>() {
        @Override
        public SearchDialogDataHolder createFromParcel(Parcel parcel) {
            return new SearchDialogDataHolder(parcel);
        }

        @Override
        public SearchDialogDataHolder[] newArray(int i) {
            return new SearchDialogDataHolder[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(photoUrls.get(0));
        parcel.writeString(usernames.get(0));
        parcel.writeString(uids.get(0));
    }

    public void addPhotoUrl(String photoUrl) {
        this.photoUrls.add(photoUrl);
    }

    public void addUsername(String username) {
        this.usernames.add(username);
    }

    public void addUid(String uid) {
        this.uids.add(uid);
    }

    public String getPhotoUrl(int index) {
        return this.photoUrls.get(index);
    }

    public String getUsername(int index) {
        return this.usernames.get(index);
    }

    public String getUid(int index) {
        return this.uids.get(index);
    }

    public PebbleList<String> getPhotoUrls() {
        return this.photoUrls;
    }

    public PebbleList<String> getUsernames() {
        return this.usernames;
    }

    public PebbleList<String> getUids() {
        return this.uids;
    }

    public String getPhotoUrl() { return this.photoUrl; }
    public String getUsername() { return this.username; }
    public String getUid() { return this.uid; }
}

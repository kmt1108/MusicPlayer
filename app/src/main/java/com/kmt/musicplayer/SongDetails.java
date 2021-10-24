package com.kmt.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

public class SongDetails implements Parcelable {
    private String mPath;
    private String mTitle;
    private String mAlbum;
    private String mArtist;
    private int mYear;

    public SongDetails(String mPath, String mTitle, String mAlbum, String mArtist, int mYear) {
        this.mPath = mPath;
        this.mTitle = mTitle;
        this.mAlbum = mAlbum;
        this.mArtist = mArtist;
        this.mYear = mYear;
    }

    protected SongDetails(Parcel in) {
        mPath = in.readString();
        mTitle = in.readString();
        mAlbum = in.readString();
        mArtist = in.readString();
        mYear = in.readInt();
    }

    public static final Creator<SongDetails> CREATOR = new Creator<SongDetails>() {
        @Override
        public SongDetails createFromParcel(Parcel in) {
            return new SongDetails(in);
        }

        @Override
        public SongDetails[] newArray(int size) {
            return new SongDetails[size];
        }
    };

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

    @Override
    public String toString() {
        return mPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeString(mTitle);
        dest.writeString(mAlbum);
        dest.writeString(mArtist);
        dest.writeInt(mYear);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try{
            SongDetails song= (SongDetails) obj;
            if (song!=null){
                return mTitle.equals(song.getmTitle())||mArtist.equals(song.getmArtist());
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }
}

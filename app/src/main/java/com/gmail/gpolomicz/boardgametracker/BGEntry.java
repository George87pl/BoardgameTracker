package com.gmail.gpolomicz.boardgametracker;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class BGEntry implements Comparable, Serializable {

    private int id;
    private String name;
    private String description;
    private int rating;
    private String image;
    private String pubDate;
    private int value;
    private boolean isChecked;

    public BGEntry() {
    }

    public BGEntry(int id, String name, String description, int rating, String image, String pubDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.image = image;
        this.pubDate = pubDate;
        isChecked = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPubDate() {
        return pubDate;
    }

    void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }

    void setIschecked() {
        if (isChecked) {
            isChecked = false;
        } else {
            isChecked = true;
        }
    }

    @Override
    public String toString() {
        return "BGEntry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rating='" + rating + '\'' +
                ", image='" + image + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object object) {
        int comperValue = ((BGEntry)object).getValue();
        return comperValue - this.value;
    }
}

package com.gmail.gpolomicz.boardgametracker;

import java.io.Serializable;

public class PlayerEntry implements Serializable {

    private int id;
    private String name;
    private String note;
    private String image;
    private boolean checked;
    private boolean winner;

    public PlayerEntry(int id, String name, String note, String image) {
        this.id = id;
        this.name = name;
        this.note = note;
        this.image = image;
        checked = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        return "PlayerEntry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", image='" + image + '\'' +
                ", checked=" + checked +
                ", winner=" + winner +
                '}';
    }
}

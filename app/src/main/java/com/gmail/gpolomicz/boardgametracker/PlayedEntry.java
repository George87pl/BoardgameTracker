package com.gmail.gpolomicz.boardgametracker;

import java.io.Serializable;
import java.util.List;

public class PlayedEntry implements Serializable {

    private int id;
    private BGEntry game;
    private List<PlayerEntry> players;
    private String note;
    private String date;
    private String image;

    PlayedEntry() {
    }

    public PlayedEntry(int id, BGEntry game, List<PlayerEntry> players, String note, String date, String image) {
        this.id = id;
        this.game = game;
        this.players = players;
        this.note = note;
        this.date = date;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BGEntry getGame() {
        return game;
    }

    public void setGame(BGEntry game) {
        this.game = game;
    }

    public List<PlayerEntry> getPlayers() {
        return players;
    }

    void setPlayers(List<PlayerEntry> players) {
        this.players = players;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PlayedEntry{" +
                "id=" + id +
                ", game=" + game +
                ", players=" + players +
                ", note='" + note + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}


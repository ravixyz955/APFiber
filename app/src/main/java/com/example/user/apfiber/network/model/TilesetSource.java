package com.example.user.apfiber.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apple on 24/01/18.
 */

public class TilesetSource {

    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private String type;

    @SerializedName("tiles")
    private String[] tiles;

    @SerializedName("tileSize")
    private int tileSize;

    public TilesetSource() {
    }

    public TilesetSource(String id, String type, String[] tiles, int tileSize) {
        this.id = id;
        this.type = type;
        this.tiles = tiles;
        this.tileSize = tileSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getTiles() {
        return tiles;
    }

    public void setTiles(String[] tiles) {
        this.tiles = tiles;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }
}

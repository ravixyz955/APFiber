package com.example.user.apfiber.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apple on 24/01/18.
 */

public class TilesetLayer {

    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private String type;

    @SerializedName("source")
    private String source;

    @SerializedName("tileSize")
    private int tileSize;

    @SerializedName("minzoom")
    private int minZoom;

    @SerializedName("maxzoom")
    private int maxZoom;

    public TilesetLayer() {
    }

    public TilesetLayer(String id, String type, String source, int tileSize, int minZoom, int maxZoom) {
        this.id = id;
        this.type = type;
        this.source = source;
        this.tileSize = tileSize;
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }

    public int getMinZoom() {
        return minZoom;
    }

    public void setMinZoom(int minZoom) {
        this.minZoom = minZoom;
    }

    public int getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(int maxZoom) {
        this.maxZoom = maxZoom;
    }
}

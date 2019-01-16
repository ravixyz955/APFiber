package com.example.user.apfiber.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by apple on 23/01/18.
 */

public class Tileset {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("tileset_id")
    private String tilesetId;

    @SerializedName("tileset_thumbnail")
    private String thumbnail;

    @SerializedName("tileset_date")
    private Date date;

    @SerializedName("source")
    private TilesetSource source;

    @SerializedName("layer")
    private TilesetLayer layer;

    public Tileset() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTilesetId() {
        return tilesetId;
    }

    public void setTilesetId(String tilesetId) {
        this.tilesetId = tilesetId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TilesetSource getSource() {
        return source;
    }

    public void setSource(TilesetSource source) {
        this.source = source;
    }

    public TilesetLayer getLayer() {
        return layer;
    }

    public void setLayer(TilesetLayer layer) {
        this.layer = layer;
    }
}

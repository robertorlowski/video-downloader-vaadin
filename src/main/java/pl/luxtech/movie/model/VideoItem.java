/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.luxtech.movie.model;

import java.net.URL;
import java.util.UUID;

import com.github.axet.vget.info.VideoInfo;

/**
 *
 * @author Robert.Orlowski
 */
public class VideoItem {

    private UUID ID = UUID.randomUUID();
    private String url = "";
    private String title = "";
    private String folder = "";
    private Boolean error = false;
    private Float progress = -1f;

    private URL icon;
    private VideoInfo info;

    public VideoItem(String url, String folder) {
        this.url = url;
        this.folder = folder;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public VideoInfo getInfo() {
        return info;
    }

    public void setInfo(VideoInfo info) {
        this.info = info;
        this.title = info.getTitle();
        this.icon = info.getIcon();
    }

    public URL getIcon() {
        return icon;
    }

    public void setIcon(URL icon) {
        this.icon = icon;
    }

    public UUID getID() {
        return ID;
    }

    public Float getProgress() {
        return progress;
    }

    public void setProgress(Float progress) {
        this.progress = progress;
    }

}

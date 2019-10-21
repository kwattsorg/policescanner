package net.kwatts.android.NetScanner;

import android.arch.lifecycle.ViewModel;

public class PlayerViewModel extends ViewModel {
    private String streamLocation = "default_location";
    private String streamGenre = "default_genre";
    private String streamDescription = "default_description";
    private String streamURL = "default_url";

    public String getStreamLocation() {
        return streamLocation;
    }
    public void setStreamLocation(String loc) {
        this.streamLocation = loc;
    }

    public String getStreamGenre() {
        return streamGenre;
    }
    public void setStreamGenre(String genre) {
        this.streamGenre = genre;
    }

    public String getStreamDescription() {
        return streamDescription;
    }
    public void setStreamDescription(String descr) {
        this.streamDescription = descr;
    }

    public String getStreamURL() {
        return streamURL;
    }
    public void setStreamURL(String url) {
        this.streamURL = url;
    }
}
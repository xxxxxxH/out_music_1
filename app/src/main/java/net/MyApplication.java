package net;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import net.model.FolderSongs;
import net.model.FolderVideo;
import net.model.Videos;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Android 1 on 16-Apr-18.
 */

public class MyApplication extends Application {
    static Context context;
    private MyApplication instance;
    ArrayList<Videos> videolist;
    List<FolderVideo> folderVideos;
    List<FolderSongs> folderSongs;

    @Override
    public void onCreate() {

        MultiDex.install(this);
        super.onCreate();
        instance = this;
        context = getApplicationContext();

    }


    public static Context getContext() {
        return context;
    }

    public MyApplication getInstance() {
        if (instance == null) {
            instance = this;
        }

        return instance;
    }

    public ArrayList<Videos> getVideolist() {
        return videolist;
    }

    public void setVideolist(ArrayList<Videos> videolist) {
        this.videolist = videolist;
    }

    public List<FolderVideo> getFolderVideos() {
        return folderVideos;
    }

    public void setFolderVideos(List<FolderVideo> folderVideos) {
        this.folderVideos = folderVideos;
    }

    public List<FolderSongs> getFolderSongs() {
        return folderSongs;
    }

    public void setFolderSongs(List<FolderSongs> folderSongs) {
        this.folderSongs = folderSongs;
    }

}

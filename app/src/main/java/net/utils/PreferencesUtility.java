/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package net.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

public final class PreferencesUtility {

    public static String IS_FIRST_TIME = "is_first_time";
    public static final String ARTIST_SORT_ORDER = "artist_sort_order";
    public static final String GENER_SORT_ORDER = "gener_sort_order";
    public static final String ARTIST_SONG_SORT_ORDER = "artist_song_sort_order";
    public static final String ARTIST_ALBUM_SORT_ORDER = "artist_album_sort_order";
    public static final String ALBUM_SORT_ORDER = "album_sort_order";
    public static final String ALBUM_SONG_SORT_ORDER = "album_song_sort_order";
    public static final String SONG_SORT_ORDER = "song_sort_order";
    private static final String NOW_PLAYING_SELECTOR = "now_paying_selector";
    private static final String TOGGLE_ANIMATIONS = "toggle_animations";
    private static final String TOGGLE_SYSTEM_ANIMATIONS = "toggle_system_animations";
    private static final String TOGGLE_ARTIST_GRID = "toggle_artist_grid";
    private static final String TOGGLE_ALBUM_GRID = "toggle_album_grid";
    private static final String TOGGLE_GENER_GRID = "toggle_gener_grid";
    private static final String TOGGLE_PLAYLIST_VIEW = "toggle_playlist_view";
    private static final String TOGGLE_SHOW_AUTO_PLAYLIST = "toggle_show_auto_playlist";
    private static final String LAST_FOLDER = "last_folder";

    private static final String TOGGLE_HEADPHONE_PAUSE = "toggle_headphone_pause";
    private static final String THEME_PREFERNCE = "theme_preference";
    private static final String START_PAGE_INDEX = "start_page_index";
    private static final String START_PAGE_PREFERENCE_LASTOPENED = "start_page_preference_latopened";
    private static final String NOW_PLAYNG_THEME_VALUE = "now_playing_theme_value";
    private static final String TOGGLE_XPOSED_TRACKSELECTOR = "toggle_xposed_trackselector";
    public static final String LAST_ADDED_CUTOFF = "last_added_cutoff";
    public static final String GESTURES = "gestures";
    public static final String FULL_UNLOCKED = "full_version_unlocked";

    public static String TOGGLE_SNOWFALL = "toggle_snowfall";
    public static String IMAGE_SHOW_SNOWFALL_POS = "IMAGESHOWSNOWFALLPOS";


    public static String SORTBY = "sortby";
    public static String SORT_COLUMN = "sort_column";
    public static String VIDEO_VIEW_TYPE = "video_view_type";


    private static PreferencesUtility sInstance;

    private static SharedPreferences mPreferences;

    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }

    public void setIsFirstTime(final Boolean is_shuffle) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(IS_FIRST_TIME, is_shuffle);
        editor.apply();
    }

    public boolean isShowSnawFall() {
        return mPreferences.getBoolean(TOGGLE_SNOWFALL, true);
    }

    public void setShowSnawFall(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(TOGGLE_SNOWFALL, b);
        editor.apply();
    }

    public void setShowSnawFallImagePos(final int pos) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(IMAGE_SHOW_SNOWFALL_POS, pos);
        editor.apply();
    }

    public int getShowSnawFallImagePos() {
        return mPreferences.getInt(IMAGE_SHOW_SNOWFALL_POS, 0);
    }

    public boolean isArtistsInGrid() {
        return mPreferences.getBoolean(TOGGLE_ARTIST_GRID, true);
    }

    public void setArtistsInGrid(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(TOGGLE_ARTIST_GRID, b);
        editor.apply();
    }

    public boolean isAlbumsInGrid() {
        return mPreferences.getBoolean(TOGGLE_ALBUM_GRID, true);
    }

    public void setAlbumsInGrid(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(TOGGLE_ALBUM_GRID, b);
        editor.apply();
    }

    public boolean isGenerInGrid() {
        return mPreferences.getBoolean(TOGGLE_GENER_GRID, true);
    }

    public void setGenerInGrid(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(TOGGLE_GENER_GRID, b);
        editor.apply();
    }

    public boolean pauseEnabledOnDetach() {
        return mPreferences.getBoolean(TOGGLE_HEADPHONE_PAUSE, true);
    }

    public String getTheme() {
        return mPreferences.getString(THEME_PREFERNCE, "light");
    }


    private void setSortOrder(final String key, final String value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public final String getArtistSortOrder() {
        return mPreferences.getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z);
    }

    public void setArtistSortOrder(final String value) {
        setSortOrder(ARTIST_SORT_ORDER, value);
    }


    public final String getArtistSongSortOrder() {
        return mPreferences.getString(ARTIST_SONG_SORT_ORDER,
                SortOrder.ArtistSongSortOrder.SONG_A_Z);
    }


    public final String getAlbumSortOrder() {
        return mPreferences.getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z);
    }

    public void setAlbumSortOrder(final String value) {
        setSortOrder(ALBUM_SORT_ORDER, value);
    }

    public final String getAlbumSongSortOrder() {
        return mPreferences.getString(ALBUM_SONG_SORT_ORDER,
                SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
    }

    public void setAlbumSongSortOrder(final String value) {
        setSortOrder(ALBUM_SONG_SORT_ORDER, value);
    }

    public final String getSongSortOrder() {
        return mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z);
    }

    public void setSongSortOrder(final String value) {
        setSortOrder(SONG_SORT_ORDER, value);
    }



    public void storeLastFolder(String path) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(LAST_FOLDER, path);
        editor.apply();
    }

    public String getLastFolder() {
        return mPreferences.getString(LAST_FOLDER, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath());
    }


    public void setSORTBY(final String key) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SORTBY, key);
        editor.apply();
    }

    public String getSORTBY() {
        return mPreferences.getString(SORTBY, "DESC");
    }

    public void setSORTCOLUMN(final String key) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SORT_COLUMN, key);
        editor.apply();
    }

    public String getSORTCOLUMN() {
        return mPreferences.getString(SORT_COLUMN, "duration"); //date_added
    }

    public void setViewType(final String key) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(VIDEO_VIEW_TYPE, key);
        editor.apply();
    }

    public String getViewType() {
        return mPreferences.getString(VIDEO_VIEW_TYPE, "files");
    }
}


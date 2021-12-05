/*
 * Copyright (C) 2012 Andrew Neal
 * Copyright (C) 2014 The CyanogenMod Project
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package net.utils;

import android.provider.MediaStore;

/**
 * Holds all of the sort orders for each list type.
 *
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public final class SortOrder {

    public SortOrder() {
    }

    public interface ArtistSortOrder {
        /* ArtistModel sort order A-Z */
        String ARTIST_A_Z = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;

        /* ArtistModel sort order Z-A */
        String ARTIST_Z_A = ARTIST_A_Z + " DESC";

        /* ArtistModel sort order number of songs */
        String ARTIST_NUMBER_OF_SONGS = MediaStore.Audio.Artists.NUMBER_OF_TRACKS
                + " DESC";

        /* ArtistModel sort order number of albums */
        String ARTIST_NUMBER_OF_ALBUMS = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
                + " DESC";
    }

    /**
     * AlbumModel sort order entries.
     */
    public interface AlbumSortOrder {
        /* AlbumModel sort order A-Z */
        String ALBUM_A_Z = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

        /* AlbumModel sort order Z-A */
        String ALBUM_Z_A = ALBUM_A_Z + " DESC";

        /* AlbumModel sort order songs */
        String ALBUM_NUMBER_OF_SONGS = MediaStore.Audio.Albums.NUMBER_OF_SONGS
                + " DESC";

        /* AlbumModel sort order artist */
        String ALBUM_ARTIST = MediaStore.Audio.Albums.ARTIST;

        /* AlbumModel sort order year */
        String ALBUM_YEAR = MediaStore.Audio.Albums.FIRST_YEAR + " DESC";

    }

    /**
     * SongModel sort order entries.
     */
    public interface SongSortOrder {
        /* SongModel sort order A-Z */
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        /* SongModel sort order Z-A */
        String SONG_Z_A = SONG_A_Z + " DESC";

        /* SongModel sort order artist */
        String SONG_ARTIST = MediaStore.Audio.Media.ARTIST;

        /* SongModel sort order album */
        String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;

        /* SongModel sort order year */
        String SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC";

        /* SongModel sort order duration */
        String SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC";


        /* SongModel sort order filename */
        String SONG_FILENAME = MediaStore.Audio.Media.DATA;
    }

    /**
     * AlbumModel song sort order entries.
     */
    public interface AlbumSongSortOrder {
        /* AlbumModel song sort order A-Z */
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;


        /* AlbumModel song sort order track list */
        String SONG_TRACK_LIST = MediaStore.Audio.Media.TRACK + ", "
                + MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    }

    /**
     * ArtistModel song sort order entries.
     */
    public interface ArtistSongSortOrder {
        /* ArtistModel song sort order A-Z */
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    }



}

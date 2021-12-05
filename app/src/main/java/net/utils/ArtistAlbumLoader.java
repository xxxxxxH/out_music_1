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
import android.database.Cursor;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import net.general.GlobalApp;
import net.model.AlbumModel;

import java.util.ArrayList;

public class ArtistAlbumLoader {

    public static ArrayList<AlbumModel> getAlbumsForArtist(Context context, long artistID) {

        ArrayList<AlbumModel> albumList = new ArrayList<AlbumModel>();
        Cursor cursor = makeAlbumForArtistCursor(context, artistID);

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    long _id = cursor.getLong(0);
                    String _title = cursor.getString(1);
                    String _artistName = cursor.getString(2);
                    long _artistId = cursor.getLong(3);
                    int _songCount = cursor.getInt(4);
                    int _year = cursor.getInt(5);
                    AlbumModel album = new AlbumModel(_artistId, _artistName, _id, _songCount, _title, GlobalApp.INSTANCE.getImgUri(_id), _year);
                    albumList.add(album);
                }
                while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return albumList;
    }


    public static Cursor makeAlbumForArtistCursor(Context context, long artistID) {
        Cursor cursor;
        if (artistID == -1)
            return null;

        if (Build.VERSION.SDK_INT >= 29) {
            cursor = context.getContentResolver().query(MediaStore.Audio.Artists.Albums.getContentUri("external", artistID), new String[]{"album_id", "album", "artist", "numsongs", "minyear"}, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        } else {
            cursor = context.getContentResolver().query(MediaStore.Audio.Artists.Albums.getContentUri("external", artistID), new String[]{BaseColumns._ID, "album", "artist", "numsongs", "minyear"}, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        }

        return cursor;
    }
}

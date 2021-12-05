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
import android.net.Uri;
import android.provider.MediaStore;

import net.general.GlobalApp;
import net.model.AlbumModel;

import java.util.ArrayList;
import java.util.List;

public class GenerAlbumLoader {

    public static ArrayList<AlbumModel> getAlbumsForGener(Context context, long generID) {

        ArrayList<AlbumModel> albumList = new ArrayList();
        List<AlbumModel> tempList = new ArrayList();
        Cursor cursor = makeAlbumForGenerCursor(context, generID);
        int numsongs = 0;
        int minyear = 0;
        long temp_id = 0;

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {

                    Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                    String string = "_id=" + cursor.getLong(0);
                    Cursor cursor1 = context.getContentResolver().query(uri, new String[]{"numsongs", "minyear"}, string, null, null);

                    if (cursor1 != null) {
                        if (cursor1.moveToFirst()) {
                            numsongs = cursor1.getInt(0);
                            minyear = cursor1.getInt(1);
                        }
                    }

                    AlbumModel album = new AlbumModel(cursor.getInt(3), cursor.getString(2), cursor.getLong(0), numsongs, cursor.getString(1), GlobalApp.INSTANCE.getImgUri(cursor.getLong(0)), minyear);

                    if (temp_id != cursor.getLong(0)) {
                        albumList.add(album);
                        temp_id = cursor.getLong(0);
                    }

                }
                while (cursor.moveToNext());

        }
        if (cursor != null)
            cursor.close();
        return albumList;
    }


    public static Cursor makeAlbumForGenerCursor(Context context, long generID) {

        if (generID == -1)
            return null;

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Genres.Members.getContentUri("external", generID), new String[]{"album_id", "album", "artist", "artist_id"}, null, null, null);

        return cursor;
    }


}

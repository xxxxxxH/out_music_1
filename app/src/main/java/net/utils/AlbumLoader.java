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
import android.provider.MediaStore;

import net.general.GlobalApp;
import net.model.AlbumModel;

import java.util.ArrayList;
import java.util.List;

public class AlbumLoader {

    static Context context;

    public static AlbumModel getAlbum(Cursor cursor) {
        AlbumModel album = new AlbumModel();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                long _id = cursor.getLong(0);
                String _title = cursor.getString(1);
                String _artistName = cursor.getString(2);
                long _artistId = cursor.getLong(3);
                int _songCount = cursor.getInt(4);
                int _year = cursor.getInt(5);
                album = new AlbumModel(_artistId, _artistName, _id, _songCount, _title, GlobalApp.INSTANCE.getImgUri(_id), _year);
            }

        }
        if (cursor != null)
            cursor.close();
        return album;
    }


    public static List<AlbumModel> getAlbumsForCursor(Cursor cursor) {
        ArrayList<AlbumModel> arrayList = new ArrayList<AlbumModel>();
        int native_count = 1;

        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                long _id = cursor.getLong(0);
                String _title = cursor.getString(1);
                String _artistName = cursor.getString(2);
                long _artistId = cursor.getLong(3);
                int _songCount = cursor.getInt(4);
                int _year = cursor.getInt(5);
                arrayList.add(new AlbumModel(_artistId, _artistName, _id, _songCount, _title, GlobalApp.INSTANCE.getImgUri(_id), _year));

            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

    public static List<AlbumModel> getAllAlbums(Context context) {
        return getAlbumsForCursor(makeAlbumCursor(context, null, null));
    }

    public static Cursor makeAlbumCursor(Context ctx, String selection, String[] paramArrayOfString) {
        context = ctx;
        final String albumSortOrder = PreferencesUtility.getInstance(context).getAlbumSortOrder();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, new String[]{"_id", "album", "artist", "artist_id", "numsongs", "minyear"}, selection, paramArrayOfString, albumSortOrder);

        return cursor;
    }
}

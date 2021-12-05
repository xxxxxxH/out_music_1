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


import net.model.ArtistModel;

import java.util.ArrayList;
import java.util.List;

public class ArtistLoader {


    static Context context;
    public static ArtistModel getArtist(Cursor cursor) {
        ArtistModel artist = new ArtistModel();
        if (cursor != null) {
            if (cursor.moveToFirst())
                artist = new ArtistModel(cursor.getInt(2),cursor.getLong(0),cursor.getString(1),cursor.getInt(3));
        }
        if (cursor != null)
            cursor.close();
        return artist;
    }

    public static List<ArtistModel> getArtistsForCursor(Cursor cursor) {
        ArrayList<ArtistModel> arrayList = new ArrayList<ArtistModel>();
        int  native_count = 1;

        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                arrayList.add(new ArtistModel(cursor.getInt(2),cursor.getLong(0),cursor.getString(1),cursor.getInt(3)));

            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

    public static List<ArtistModel> getAllArtists(Context context) {
        return getArtistsForCursor(makeArtistCursor(context, null, null));
    }

    public static ArtistModel getArtist(Context context, long id) {
        return getArtist(makeArtistCursor(context, "_id=?", new String[]{String.valueOf(id)}));
    }

    public static Cursor makeArtistCursor(Context ctx, String selection, String[] paramArrayOfString) {
        context = ctx;
        final String artistSortOrder = PreferencesUtility.getInstance(ctx).getArtistSortOrder();
        Cursor cursor = ctx.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, new String[]{"_id", "artist", "number_of_albums", "number_of_tracks"}, selection, paramArrayOfString, artistSortOrder);
        return cursor;
    }
}

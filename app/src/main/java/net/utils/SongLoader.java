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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;

import net.general.GlobalApp;
import net.model.SongsModel;

import java.util.ArrayList;

public class SongLoader {

    private static final long[] sEmptyList = new long[0];

    public static ArrayList<SongsModel> getSongsForCursor(Cursor cursor) {
        ArrayList<SongsModel> arrayList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                long id = cursor.getLong(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                int duration = cursor.getInt(4);
                int trackNumber = cursor.getInt(5);
                long artistId = cursor.getInt(6);
                long albumId = cursor.getLong(7);
                String data = cursor.getString(8);
                String size = cursor.getString(9);

                if (!cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)).contains("audio/amr") && !cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)).contains("audio/aac")) {
                    arrayList.add(new SongsModel(id, album, albumId, artist, duration,GlobalApp.INSTANCE.getImgUri(albumId), data, title, "", 0, size, trackNumber, artistId, 0));
                }


            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

    public static SongsModel getSongForCursor(Cursor cursor) {
        SongsModel song = new SongsModel();
        if ((cursor != null) && (cursor.moveToFirst())) {
            long id = cursor.getLong(0);
            String title = cursor.getString(1);
            String artist = cursor.getString(2);
            String album = cursor.getString(3);
            int duration = cursor.getInt(4);
            int trackNumber = cursor.getInt(5);
            long artistId = cursor.getInt(6);
            long albumId = cursor.getLong(7);
            String data = cursor.getString(8);
            String size = cursor.getString(9);

            song = new SongsModel(id, album, albumId, artist, duration,GlobalApp.INSTANCE.getImgUri(albumId), data, title, "", 0, size, trackNumber, artistId, 0);
        }

        if (cursor != null)
            cursor.close();
        return song;
    }

    public static final long[] getSongListForCursor(Cursor cursor) {
        if (cursor == null) {
            return sEmptyList;
        }
        final int len = cursor.getCount();
        final long[] list = new long[len];
        cursor.moveToFirst();
        int columnIndex = -1;
        try {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
        } catch (final IllegalArgumentException notaplaylist) {
            columnIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        }
        for (int i = 0; i < len; i++) {
            list[i] = cursor.getLong(columnIndex);
            cursor.moveToNext();
        }
        cursor.close();
        cursor = null;
        return list;
    }

    public static SongsModel getSongFromPath(String songPath, Context context) {
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.DATA;
        String[] selectionArgs = {songPath};
        String[] projection = new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id", "_data", "_size"};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            SongsModel song = getSongForCursor(cursor);
            cursor.close();
            return song;
        } else return new SongsModel();
    }

    public static ArrayList<SongsModel> getAllSongs(Context context) {
        return getSongsForCursor(makeSongCursor(context, null, null));
    }

    public static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString) {
        final String songSortOrder = PreferencesUtility.getInstance(context).getSongSortOrder();
        //  String orderBy = Global.sharedpreferences.getString(Global.SORTCOLUMN,"title")+" "+ Global.sharedpreferences.getString(Global.SORTBY,"ASC");

        return makeSongCursor(context, selection, paramArrayOfString, songSortOrder);
    }

    private static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString, String sortOrder) {
        String selectionStatement = "is_music=1 AND title != ''";

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = selectionStatement + " AND " + selection;
        }
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id", "_data", "_size", "mime_type"}, selectionStatement, paramArrayOfString, sortOrder);

    }


}

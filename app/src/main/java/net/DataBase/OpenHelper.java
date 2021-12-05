package net.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;


import net.general.GeneralFunction;
import net.model.AlertPlayList;
import net.model.HistoryModel;
import net.model.SongsModel;

import java.util.ArrayList;

public class OpenHelper extends SQLiteOpenHelper
{

	public static OpenHelper oh;
	Boolean flag = false;
	public static final String ID = "ID";
	//Cursor cursor;
	// ================= likes table ===============================================================
	public static final String DATABASE_NAME = "MUSICPLAYER";


	public static final String TABLE_NAME_ADD_TO_FAVOURITE = "ADD_TO_FAVOURITE";
	public static final String ADD_TO_FAVOURITE_SONG_ID = "ADD_TO_FAVOURITE_SONG_ID";
	public static final String ADD_TO_FAVOURITE_LINK = "ADD_TO_FAVOURITE_LINK";

	public static final String TABLE_NAME_ADD_TO_HISTORY = "ADD_TO_HISTORY";
	public static final String ADD_TO_HISTORY_SONG_ID = "ADD_TO_HISTORY_SONG_ID";
	public static final String ADD_TO_HISTORY_DATE = "ADD_TO_HISTORY_DATE";

	// =================Add to Queue table==========================================================
	public static final String TABLE_NAME_ADD_TO_QUEUE = "ADD_TO_QUEUE";
	public static final String ADD_TO_QUEUE_SONG_ID = "songid";
	public static final String ADD_TO_QUEUE_IMAGE = "image";
	public static final String ADD_TO_QUEUE_LINK = "songpath";
	public static final String ADD_TO_QUEUE_SINGER = "singer";
	public static final String ADD_TO_QUEUE_NAME = "songname";
	public static final String ADD_TO_QUEUE_SONG_SIZE = "songsize";


	// ==============Playlist table===================================

	private final String TABLE_NAME_PLAYLIST = "PLAYLIST";
	private final String PLAYLIST_ID = "playlistid";
	private final String PLAYLIST_NAME = "playlistname";
	private final String PLAYLIST_IMAGE = "playlistimage";


	// ==============Add to Playlist Songs table======================

	private final String TABLE_NAME_ADD_TO_PLAYLIST = "ADD_TO_PLAYLIST";
	private final String ADD_TO_PLAYLIST_QUEUE_SONG_ID = "songid";
	private final String ADD_TO_PLAYLIST_QUEUE_LINK = "songpath";


	String CREATE_ADD_TO_FAVOURITE_TABLE = "create table " + TABLE_NAME_ADD_TO_FAVOURITE + " (" + ID + " integer primary key autoincrement," + ADD_TO_FAVOURITE_SONG_ID + " text" + "," + ADD_TO_FAVOURITE_LINK + " text" + ")";
	String CREATE_ADD_TO_HISTORY_TABLE = "create table " + TABLE_NAME_ADD_TO_HISTORY + " (" + ID + " integer primary key autoincrement," + ADD_TO_HISTORY_SONG_ID + " text" + "," + ADD_TO_HISTORY_DATE + " text" + ")";
	String CREATE_ADD_TO_QUEUE_TABLE = "create table " + TABLE_NAME_ADD_TO_QUEUE + " (" + ID + " integer primary key autoincrement,"
			+ ADD_TO_QUEUE_SONG_ID + " text" + ","
			+ ADD_TO_QUEUE_NAME + " text" + ","
			+ ADD_TO_QUEUE_IMAGE + " text" + ","
			+ ADD_TO_QUEUE_LINK + " text" + ","
			+ ADD_TO_QUEUE_SONG_SIZE + " text" + ","
			+ ADD_TO_QUEUE_SINGER + " text)";

	String CREATE_ADD_TO_PLAYLIST_TABLE = "create table " + TABLE_NAME_ADD_TO_PLAYLIST + " ("
			+ ID + " integer primary key autoincrement,"
			+ PLAYLIST_ID + " text,"
			+ ADD_TO_PLAYLIST_QUEUE_SONG_ID +" text,"
			+ ADD_TO_PLAYLIST_QUEUE_LINK + " text)";

	String CREATE_PLAYLIST_TABLE = "create table " + TABLE_NAME_PLAYLIST + " (" + PLAYLIST_ID + " integer primary key autoincrement," + PLAYLIST_NAME + " text " + "," + PLAYLIST_IMAGE + " text" + ")";





	public OpenHelper(Context context)
	{
		super (context, DATABASE_NAME, null, 4);
	}

	public static OpenHelper sharedInstance (Context context)
	{
		if (oh == null)
		{
			oh = new OpenHelper (context);
		}
		return oh;
	}

	@Override
	public void onCreate (SQLiteDatabase db)
	{
		db.execSQL(CREATE_ADD_TO_FAVOURITE_TABLE);
		db.execSQL(CREATE_ADD_TO_HISTORY_TABLE);
		db.execSQL(CREATE_ADD_TO_QUEUE_TABLE);
		db.execSQL(CREATE_ADD_TO_PLAYLIST_TABLE);
		db.execSQL(CREATE_PLAYLIST_TABLE);
	}

	@Override
	public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
	{
		if(newVersion>oldVersion)
		{

			if(!isFieldExist(db,"ADD_TO_QUEUE","songsize"))
			{
				db.execSQL("ALTER TABLE  `ADD_TO_QUEUE` ADD  `songsize` TEXT NOT NULL DEFAULT  '0'");
			}

		}
	}



	public String insertHistory (long songId,String historyDate)
	{
		//Cursor res;

		SQLiteDatabase db = this.getWritableDatabase ();
				ContentValues contentValues = new ContentValues ();
				contentValues.put (ADD_TO_HISTORY_SONG_ID, songId);
				contentValues.put (ADD_TO_HISTORY_DATE, historyDate);
				long id = db.insert (TABLE_NAME_ADD_TO_HISTORY, null, contentValues);


		return id+"";
	}

	//====================================queue data=============================================

	public boolean  insertQueue (long songId,String songUri,String songName,String songPath,String songArtist,String songSize)
	{
		//Cursor res;

		SQLiteDatabase db = this.getWritableDatabase ();

		String select_query = "SELECT * FROM " + TABLE_NAME_ADD_TO_QUEUE + " WHERE " + ADD_TO_QUEUE_SONG_ID + "='" + songId + "'";

		Cursor cursor = db.rawQuery (select_query, null);
		if(cursor != null && cursor.getCount()>0)
		{
			return false;
		}
		else
		{
			ContentValues contentValues = new ContentValues();
			contentValues.put (ADD_TO_QUEUE_SONG_ID, songId);
			contentValues.put (ADD_TO_QUEUE_NAME, songName);
			contentValues.put (ADD_TO_QUEUE_IMAGE, songUri);
			contentValues.put (ADD_TO_QUEUE_LINK, songPath);
			contentValues.put (ADD_TO_QUEUE_SINGER, songArtist);
			contentValues.put (ADD_TO_QUEUE_SONG_SIZE, songSize);
			long id = db.insert (TABLE_NAME_ADD_TO_QUEUE,null,contentValues);
			return true;
		}

	}

	public void clearQueue ()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete (TABLE_NAME_ADD_TO_QUEUE, null, null);
		db.close();
	}

	public ArrayList<SongsModel> getQueueData (Context context)
	{
		String select_query = "SELECT * FROM " + TABLE_NAME_ADD_TO_QUEUE;
		SQLiteDatabase db = this.getWritableDatabase ();
		Cursor cursor = db.rawQuery (select_query, null);
		ArrayList<SongsModel> mediaItemses = new ArrayList<SongsModel> ();

		try {
			if (cursor != null && cursor.getCount () > 0)
			{
				if (cursor.moveToFirst())
				{
					do
					{
						SongsModel data = new SongsModel();
						data.setQueue_id(cursor.getInt(cursor.getColumnIndex (ID)));
						data.setSong_id (cursor.getLong (cursor.getColumnIndex (ADD_TO_QUEUE_SONG_ID)));
						data.setArtist (cursor.getString (cursor.getColumnIndex (ADD_TO_QUEUE_SINGER)));
						data.setTitle (cursor.getString (cursor.getColumnIndex (ADD_TO_QUEUE_NAME)));
						data.setPath(cursor.getString(cursor.getColumnIndex(ADD_TO_QUEUE_LINK)));
						data.setImg_uri(Uri.parse(cursor.getString(cursor.getColumnIndex(ADD_TO_QUEUE_IMAGE))));
						data.setSize(cursor.getString(cursor.getColumnIndex(ADD_TO_QUEUE_SONG_SIZE)));
						mediaItemses.add(data);

					} while (cursor.moveToNext ());
				}
			}
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}

		return mediaItemses;
	}



	public void deleteQueueSong(long id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete (TABLE_NAME_ADD_TO_QUEUE, ADD_TO_QUEUE_SONG_ID + "=?", new String[] { String.valueOf(id)});
		db.close();
	}

	public boolean isQueuelist(long id)
	{
		String select_query = "SELECT * FROM " + TABLE_NAME_ADD_TO_QUEUE +" WHERE "+ ADD_TO_QUEUE_SONG_ID +"="+id;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery (select_query, null);

		try {
			if(cursor != null)
            {
                if(cursor.getCount() > 0)
                {
                    return  true;
                }

            }
		} finally {
			if(cursor != null)
			{
				cursor.close();
			}
		}

		return  false;
	}


	public int getNext(int queu_id)
	{
		int id=0;
		String select_query = " SELECT "+ ID +" FROM " + TABLE_NAME_ADD_TO_QUEUE +" WHERE "+ID+">"+queu_id;
		SQLiteDatabase db = this.getWritableDatabase ();
		Cursor cursor = db.rawQuery (select_query, null);

		if(cursor != null)
		{
			if(cursor.moveToFirst())
			{
				id = cursor.getInt(cursor.getColumnIndex (ID));
			}
			else
			{
				id=0;
			}
			cursor.close();
		}
		else
		{
			id = 0;
		}

		return id;
	}
	public  int getCurrent_song(int queu_id)
	{
		int id=0;
		String select_query = " SELECT "+ ID +" FROM " + TABLE_NAME_ADD_TO_QUEUE +" WHERE "+ID+"="+queu_id;
		SQLiteDatabase db = this.getWritableDatabase ();
		Cursor cursor = db.rawQuery (select_query, null);

		try
		{
			if(cursor != null)
            {
                if(cursor.moveToFirst())
                {
                    id = cursor.getInt(cursor.getColumnIndex (ID));
                }
                else
				{
                    id=0;
                }
            }
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
		return id;
	}

	//------------------------playlist----------------------------------------------------------

	public String insertPlaylist (String playlist_name,String image)
	{
		SQLiteDatabase db = this.getWritableDatabase ();
		ContentValues contentValues = new ContentValues ();
		contentValues.put (PLAYLIST_NAME, playlist_name);
		contentValues.put (PLAYLIST_IMAGE, image);
		long id = db.insert (TABLE_NAME_PLAYLIST, null, contentValues);
		return id+"";
	}


	public boolean AddToPlaylistData(String playlistid,long songid,String link)
	{
		String select_query = "SELECT * FROM " + TABLE_NAME_ADD_TO_PLAYLIST + " WHERE " + PLAYLIST_ID + "='" + playlistid + "' and "+ ADD_TO_PLAYLIST_QUEUE_LINK + "='" + link + "'";
		SQLiteDatabase db = this.getWritableDatabase ();
		Cursor cursor = db.rawQuery (select_query, null);
		try
		{
			if (cursor != null)
            {
                if (cursor.getCount ()>0)
                {
                    return false;
                }
                else
                {
                    ContentValues contentValues = new ContentValues ();
                    contentValues.put (PLAYLIST_ID, playlistid);
                    contentValues.put (ADD_TO_PLAYLIST_QUEUE_SONG_ID,songid);
					contentValues.put (ADD_TO_PLAYLIST_QUEUE_LINK, link);
                    long id = db.insert (TABLE_NAME_ADD_TO_PLAYLIST, null, contentValues);
                    return true;
                }
            }
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}
		return false;
	}

	public ArrayList<SongsModel> getPlayListData(String playlistid, Context context)
	{
		String select_query = "SELECT * FROM " + TABLE_NAME_ADD_TO_PLAYLIST + " WHERE " + PLAYLIST_ID + "='" + playlistid +"'";
		SQLiteDatabase db = this.getWritableDatabase ();
		Cursor cursor = db.rawQuery (select_query, null);
		ArrayList<SongsModel> mediaItemses = new ArrayList<> ();
		try
		{
			if (cursor != null)
            {
                if (cursor.getCount () > 0 && cursor.moveToFirst())
                {
                    {
                        do
                        {
							long song_id = cursor.getLong (cursor.getColumnIndex (ADD_TO_PLAYLIST_QUEUE_SONG_ID));
                            int queue_id = cursor.getInt (cursor.getColumnIndex (ID));
                            SongsModel mediaItemse = new GeneralFunction().fatchSongsDetail(song_id,context,queue_id);
                            mediaItemses.add(mediaItemse);
                        }
                        while (cursor.moveToNext ());
                    }
                }
            }
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}

		}

		return mediaItemses;

	}

	public void deletePlayListSong(long songid)
	{
		SQLiteDatabase db = this.getWritableDatabase ();
		String del_query = "DELETE  FROM "+TABLE_NAME_ADD_TO_PLAYLIST+" WHERE "+ADD_TO_PLAYLIST_QUEUE_SONG_ID+"='"+songid+"'";
		db.execSQL(del_query);
	}

	public void deletePlayListSongsData(String playlistId)
	{
		SQLiteDatabase db = this.getWritableDatabase ();
		String del_query = "DELETE  FROM "+TABLE_NAME_ADD_TO_PLAYLIST+" WHERE "+PLAYLIST_ID+"='"+playlistId+"'";
		db.execSQL(del_query);
	}

	public void deletePlayList(String playlistid)
	{
		deletePlayListSongsData(playlistid);

		SQLiteDatabase db = this.getWritableDatabase ();
		String del_query = "DELETE  FROM "+TABLE_NAME_PLAYLIST+" WHERE "+PLAYLIST_ID+"='"+playlistid+"'";
		db.execSQL(del_query);
	}


	public ArrayList<AlertPlayList> getPlaylist ()
	{
		String select_query = "SELECT * FROM " + TABLE_NAME_PLAYLIST;
		SQLiteDatabase db = this.getWritableDatabase ();
		Cursor cursor = db.rawQuery (select_query, null);
		ArrayList<AlertPlayList> alertPlayListArrayList = new ArrayList<> ();
		try {
			if (cursor != null)
            {
                if (cursor.getCount () > 0)
                {
                    if (cursor.moveToFirst ()) // change 9/1 movetoNext
                    {
                        do
                        {
                            AlertPlayList alertPlayList = new AlertPlayList();
                            alertPlayList.setTitle(cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME)));
                            alertPlayList.setImg_url(cursor.getString(cursor.getColumnIndex(PLAYLIST_IMAGE)));
                            alertPlayList.setId(cursor.getInt(cursor.getColumnIndex(PLAYLIST_ID)) + "");

							String query = "SELECT * FROM " + TABLE_NAME_ADD_TO_PLAYLIST + " WHERE " + PLAYLIST_ID + "='" + cursor.getInt(cursor.getColumnIndex(PLAYLIST_ID)) +"'";

							Cursor c = db.rawQuery (query, null);
							if(c != null)
							{
								alertPlayList.setSongCount(c.getCount());
							}

                            alertPlayListArrayList.add (alertPlayList);
                        }
                        while (cursor.moveToNext ());
                    }

                }
            }
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}

		}
		return alertPlayListArrayList;
	}

	public boolean checkPlaylistExistOrNot(String playlist_name)
	{
		String select_query = "SELECT * FROM " + TABLE_NAME_PLAYLIST + " WHERE " + PLAYLIST_NAME + "='" + playlist_name + "'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery (select_query, null);
		if (cursor != null)
		{
			if (cursor.moveToNext ())
			{
				return true;
			}
			cursor.close();
		}
		return false;
	}

	public boolean isPlaylist(long id)
	{
		String select_query = "SELECT * FROM " + TABLE_NAME_ADD_TO_PLAYLIST +" WHERE "+ ADD_TO_PLAYLIST_QUEUE_SONG_ID +"="+id;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery (select_query, null);

		if(cursor != null)
		{
			if(cursor.getCount() > 0)
			{
				return  true;
			}
			cursor.close();
		}

		return  false;
	}

//--------------------------------------------------------------------------------------------------


	public void deleteFavoriteSong(String id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete (TABLE_NAME_ADD_TO_FAVOURITE, ADD_TO_FAVOURITE_SONG_ID + "=?", new String[] { String.valueOf(id)});
		db.close();
	}
	public void deleteHistorySong(String id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete (TABLE_NAME_ADD_TO_HISTORY, ADD_TO_HISTORY_SONG_ID + "=?", new String[] { String.valueOf(id)});
		db.close();
	}
	public void deleteHistory()
	{
		SQLiteDatabase db = this.getReadableDatabase ();
		db.delete (TABLE_NAME_ADD_TO_HISTORY, null, null);
		db.close ();
	}



	public ArrayList<HistoryModel> getHistory ()
	{
		String select_query = "SELECT * FROM " + TABLE_NAME_ADD_TO_HISTORY +" ORDER BY "+ADD_TO_HISTORY_DATE+" DESC";
		SQLiteDatabase db = this.getWritableDatabase ();
		Cursor cursor = db.rawQuery(select_query, null);
		ArrayList<HistoryModel> historyArrayList = new ArrayList<HistoryModel> ();
		//Log.e ("Mediaitem", cursor.getCount () + "");
		if (cursor != null)
		{
			if (cursor.getCount () > 0)
			{
				if (cursor.moveToFirst ())
				{
					do
					{
						String songId = cursor.getString(cursor.getColumnIndex (ADD_TO_HISTORY_SONG_ID));
						String historyDate = cursor.getString(cursor.getColumnIndex (ADD_TO_HISTORY_DATE));

						HistoryModel history = new HistoryModel();
						history.setSongId(songId);
						history.setHistoryDate(historyDate);
						historyArrayList.add (history);
					} while (cursor.moveToNext ());
				}

			}
			cursor.close();
		}
		return historyArrayList;
	}
	public void updateHistory (long id ,String date)
	{

		SQLiteDatabase db = this.getWritableDatabase ();

		ContentValues args = new ContentValues();
		args.put(ADD_TO_HISTORY_DATE, date);

		db.update(TABLE_NAME_ADD_TO_HISTORY,
				args,
				ADD_TO_HISTORY_SONG_ID + " =? ",
				new String[]{id+""});

	}

	public int getTotalHistorySongs()
	{
		String select_query = "SELECT * FROM " + TABLE_NAME_ADD_TO_HISTORY;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(select_query, null);
		int numRows = 0;
		try
		{
			if (cursor != null)
                	numRows = cursor.getCount();
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}

		return numRows;
	}

	public String firstQueueRecod()
	{
		String select_query = "SELECT * FROM " + TABLE_NAME_ADD_TO_HISTORY +" LIMIT 1";
		SQLiteDatabase db = this.getWritableDatabase ();
		Cursor cursor = db.rawQuery (select_query, null);
		String id = "0";
		if (cursor != null)
		{
			if (cursor.getCount () > 0)
			{
				if (cursor.moveToFirst())
				{
					id = cursor.getString(cursor.getColumnIndex(ADD_TO_HISTORY_SONG_ID));
				}
			}
			cursor.close();
		}
		return  id;
	}

	public boolean isHistory(long id)
	{

			String select_query = "SELECT * FROM " + TABLE_NAME_ADD_TO_HISTORY +" WHERE "+ ADD_TO_HISTORY_SONG_ID +"="+id;
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery (select_query, null);

		try {
			if(cursor != null)
            {
                if(cursor.getCount() > 0)
                {
                    return  true;
                }

            }
		} finally {
			if(cursor != null)
			{
				cursor.close();
			}
		}


		return  false;
	}




	//-----------------for show db-----------------------------------------------------

	public ArrayList<Cursor> getData(String Query)
	{
		//get writable database
		SQLiteDatabase sqlDB =this.getWritableDatabase();
		String[] columns = new String[] { "mesage" };
		//an array list of cursor to save two cursors one has results from the query
		//other cursor stores error message if any errors are triggered
		ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
		MatrixCursor Cursor2= new MatrixCursor(columns);
		alc.add(null);
		alc.add (null);


		try{
			String maxQuery = Query ;
			//execute the query results will be save in Cursor c
			Cursor c = sqlDB.rawQuery(maxQuery, null);

			//add value to cursor2
			Cursor2.addRow(new Object[] { "Success" });

			alc.set(1,Cursor2);
			if (null != c && c.getCount() > 0)
			{

				alc.set(0,c);
				c.moveToFirst();

				return alc ;
			}
			return alc;
		}
		catch(SQLException sqlEx)
		{
			Log.d("printing exception", sqlEx.getMessage());
			//if any exceptions are triggered save the error message to cursor an return the arraylist
			Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
			alc.set(1,Cursor2);
			return alc;
		}
		catch(Exception ex)
		{
			Log.d("printing exception",ex.getMessage());
			//if any exceptions are triggered save the error message to cursor an return the arraylist
			Cursor2.addRow(new Object[] { ""+ex.getMessage() });
			alc.set(1,Cursor2);
			return alc;
		}
	}


	public static boolean isFieldExist(SQLiteDatabase db, String tableName, String fieldName)
	{
		boolean isExist = false;

		Cursor res = null;

		try {

			res = db.rawQuery("Select * from "+ tableName +" limit 1", null);

			int colIndex = res.getColumnIndex(fieldName);
			if (colIndex!=-1){
				isExist = true;
			}

		} catch (Exception e) {
		} finally {
			try { if (res !=null){ res.close(); } } catch (Exception e1) {}
		}

		return isExist;
	}
}

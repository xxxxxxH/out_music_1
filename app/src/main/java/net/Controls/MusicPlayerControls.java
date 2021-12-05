package net.Controls;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import net.DataBase.OpenHelper;
import net.Services.MusicService;
import net.basicmodel.R;
import net.event.MessageEvent;
import net.general.GlobalApp;
import net.model.SongsModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class MusicPlayerControls {

    //========= PLayer Control =======
    public static SongsModel item = new SongsModel();
    public static int SONG_NUMBER = 0;

    public static Handler PROGRESSBAR_HANDLER;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    public  static String str_type_past = "";
    public static OpenHelper openHelper;

    public static void startSongsWithQueue(final Context context, final ArrayList<SongsModel> dataSet, int position , final String str_type)
    {

        MusicPlayerControls.SONG_NUMBER = position;

        sp = context.getSharedPreferences(context.getString(R.string.preference_file_key), 0);
        editor = sp.edit();
        str_type_past = sp.getString("str_type", str_type);


        SharedPreferences.Editor editor1 = GlobalApp.INSTANCE.getSharedpreferences().edit();
        editor1.putInt(GlobalApp.INSTANCE.getSONGNUMBER(), MusicPlayerControls.SONG_NUMBER);
        editor1.commit();


        openHelper = OpenHelper.sharedInstance(context);
        openHelper.clearQueue();

        GlobalApp.INSTANCE.setMediaItemsArrayList(dataSet);

        if(!GlobalApp.INSTANCE.isServiceRunning(MusicService.class.getName(),context))
        {
            Intent musIntent = new Intent(context,MusicService.class);
            context.startService(musIntent);
        }


        EventBus.getDefault().post(new MessageEvent("fillQueueAdapter"));
//        SongsMainFragment.fillQueueAdapter();

//        SongsMainFragment.playerViewpager.setCurrentItem(position);
        EventBus.getDefault().post(new MessageEvent("setCurrentItem",position));

        new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    try
                    {
                        for (int i = 0; i < GlobalApp.INSTANCE.getMediaItemsArrayList().size(); i++)
                        {
                            SongsModel mediaItems = GlobalApp.INSTANCE.getMediaItemsArrayList().get(i);
                            openHelper.insertQueue(mediaItems.getSong_id(), mediaItems.getImg_uri() + "", mediaItems.getTitle(), mediaItems.getPath(), mediaItems.getArtist(), mediaItems.getSize());

                            if (GlobalApp.INSTANCE.getBreak_insert_queue())
                            {
                                GlobalApp.INSTANCE.setBreak_insert_queue(false);
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            editor.putString("str_type", str_type);
            editor.commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MusicService.playSong();
            }
        },200);

    }
}

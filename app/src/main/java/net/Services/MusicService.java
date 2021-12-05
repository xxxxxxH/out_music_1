package net.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;


import com.squareup.picasso.Picasso;

import net.Controls.MusicPlayerControls;
import net.DataBase.OpenHelper;
import net.basicmodel.R;
import net.event.MessageEvent;
import net.fragment.SongsMainFragment;
import net.general.GlobalApp;
import net.model.SongsModel;
import net.receiver.NotificationBroadcast;

import org.greenrobot.eventbus.EventBus;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MusicService extends Service implements
		MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
		MediaPlayer.OnCompletionListener,AudioManager.OnAudioFocusChangeListener{

	public static final String NOTIFY_PREVIOUS = "com.MUSIC.previous";
	public static final String NOTIFY_DELETE = "com.MUSIC.delete";
	public static final String NOTIFY_PAUSE = "com.MUSIC.pause";
	public static final String NOTIFY_PLAY = "com.MUSIC.play";
	public static final String NOTIFY_NEXT = "com.MUSIC.next";
	public static final String OPEN_AUDIO_PLAYER = "com.MUSIC.audioplayer";

	public static final int NOTIFICATION_ID = 1008;
	private ComponentName remoteComponentName;
	private RemoteControlClient remoteControlClient;
	private static boolean currentVersionSupportLockScreenControls = false;
	public static NotificationManager mNotifyManager;
	public static MediaPlayer player;

	private final IBinder musicBind = new MusicBinder();
	public static boolean shuffle=false;
	private static boolean repeat=false;
	private static int repeat_id;
	private Random rand;
	public static Timer timer;
	private RemoteViews simpleContentView;
	private Notification notification;
	OpenHelper openHelper;
	static Context context;
	AudioManager am = null;
	AudioManager.OnAudioFocusChangeListener changeListener;


	public void onCreate(){
		super.onCreate();

		player = new MediaPlayer();
		context = this;
		timer = new Timer();
		openHelper = new OpenHelper(this);

		if (GlobalApp.INSTANCE.getSharedpreferences() == null) {
			GlobalApp.INSTANCE.savePrefrence(context);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {


		rand=new Random();
		initMusicPlayer();
		checkPhoneCall();
		changeListener = this;

		am = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		int result = am.requestAudioFocus((AudioManager.OnAudioFocusChangeListener) this, AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);

		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

			pausePlayer();
		}



		currentVersionSupportLockScreenControls = GlobalApp.INSTANCE.currentVersionSupportLockScreenControls();
		if(currentVersionSupportLockScreenControls)
		{
			RegisterRemoteClient();
		}

		registerReceiver( buttonplaypauseReceiver, new IntentFilter( GlobalApp.INSTANCE.TIME_UP ) );
		registerReceiver( widgetplaypauseReceiver, new IntentFilter(GlobalApp.BROADCAST_PLAYPAUSE ));
		registerReceiver( widgetnextReceiver, new IntentFilter(GlobalApp.BROADCAST_NEXT ));
		registerReceiver( widgetprevReceiver, new IntentFilter(GlobalApp.BROADCAST_PREV ));
		registerReceiver( widgetpauseReceiver, new IntentFilter(GlobalApp.BROADCAST_PAUSE ));

		return START_NOT_STICKY;
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		am =(AudioManager)getSystemService(Context.AUDIO_SERVICE);

		context.sendBroadcast(new Intent(GlobalApp.BROADCAST_PAUSE));
	}


	public static class MainTask extends TimerTask
	{
		public void run ()
		{

			try
			{
				handler.sendEmptyMessage (0);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}
	}
	private static final Handler handler = new Handler ()
	{
		@Override
		public void handleMessage (Message msg)
		{
			if (player != null) {
				try
				{
					if(player.isPlaying ())
					{
						int progress = (player.getCurrentPosition () * 100) / player.getDuration ();
						Integer i[] = new Integer[3];
						i[0] = player.getCurrentPosition();
						i[1] = player.getDuration();
						i[2] = progress;
						MusicPlayerControls.PROGRESSBAR_HANDLER.sendMessage(MusicPlayerControls.PROGRESSBAR_HANDLER.obtainMessage (0, i));
					}
				}
				catch (Exception e)
				{

				}
			}
		}
	};



	public  void initMusicPlayer()
	{
		player.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}


	public class MusicBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return musicBind;
	}

	@Override
	public boolean onUnbind(Intent intent)
	{

		return false;
	}

	public void playerStart()
	{
		if(player != null)
		{
			go();
		}
		else
		{

			playSong();
		}
	}

	public static void playSong()
	{
		repeat_id = MusicPlayerControls.SONG_NUMBER;

//		if(SongsMainFragment.playerSlidingUpPanelLayout != null && SongsMainFragment.playerSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN)
//		{
//			SongsMainFragment.playerSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//		}
		EventBus.getDefault().post(new MessageEvent("playSong"));
		if(GlobalApp.INSTANCE.getSharedpreferences() == null)
		{
			GlobalApp.INSTANCE.savePrefrence(context);
		}
		SharedPreferences.Editor editor1 = GlobalApp.INSTANCE.getSharedpreferences().edit();
		editor1.putInt(GlobalApp.INSTANCE.getSONGNUMBER(), MusicPlayerControls.SONG_NUMBER);
		editor1.commit();

//		SongsMainFragment.changeUi(MusicPlayerControls.SONG_NUMBER);
		EventBus.getDefault().post(new MessageEvent("changeUi"));

		if(player != null) {
			player.reset();
		}
		else
		{
			player = new MediaPlayer();
			player.reset();

		}

		SongsModel playSong = null;
		try
		{
			playSong = GlobalApp.INSTANCE.getMediaItemsArrayList().get(MusicPlayerControls.SONG_NUMBER);
		}
		catch (Exception e)
		{
			playSong = new SongsModel();
			MusicPlayerControls.SONG_NUMBER = 0;
			e.printStackTrace();
		}


		FileInputStream is = null;
		try
		{
			is = new FileInputStream(playSong.getPath());
			player.setDataSource(is.getFD());
			// player.setDataSource("http://streaming.radionomy.com/JamendoLounge?lang=en-US%2cen%3bq%3d0.9%2cgu-IN%3bq%3d0.8%2cgu%3bq%3d0.7%2chi-IN%3bq%3d0.6%2chi%3bq%3d0.5");
			player.prepareAsync();
			is.close();
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}

		SharedPreferences.Editor editor = GlobalApp.INSTANCE.getSharedpreferences().edit();
		editor.putString(GlobalApp.INSTANCE.getPREFREANCE_LAST_SONG_KEY(), MusicPlayerControls.SONG_NUMBER+"");
		editor.putString("songId",playSong.getSong_id()+"");
		editor.commit();

	}


	@Override
	public void onCompletion(MediaPlayer mp)
	{
		try
		{
			if(repeat)
            {
                MusicPlayerControls.SONG_NUMBER = repeat_id;
                playSong();
            }
            else if (player != null && player.getCurrentPosition() > 0)
            {
                mp.reset();
                playNext();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		mp.reset();
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		mp.start();

		musicPlayerNotification();
		timer.scheduleAtFixedRate(new MainTask(), 0, 1000);


		SimpleDateFormat currentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String history = currentdate.format(new Date());
		try
		{
			if(!openHelper.isHistory(GlobalApp.INSTANCE.getMediaItemsArrayList().get(MusicPlayerControls.SONG_NUMBER).getSong_id()))
            {
                if(openHelper.getTotalHistorySongs()>50)
                {
                    String id = openHelper.firstQueueRecod();
                    openHelper.deleteHistorySong(id);
                }
                openHelper.insertHistory(GlobalApp.INSTANCE.getMediaItemsArrayList().get(MusicPlayerControls.SONG_NUMBER).getSong_id(), history);
            }
            else
            {
                try
                {
                    openHelper.updateHistory(GlobalApp.INSTANCE.getMediaItemsArrayList().get(MusicPlayerControls.SONG_NUMBER).getSong_id(), history);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			EventBus.getDefault().post(new MessageEvent("changeButton"));
//			SongsMainFragment.changeButton();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	public static int getDur(){
		return player.getDuration();
	}

	public static boolean isPng(){
		if(player != null)
		{
			return player.isPlaying();
		}
		return false;
	}

	public void pausePlayer()
	{
		if(GlobalApp.INSTANCE.isServiceRunning(this.getClass().getName(),context))
		{
			if (player != null)
			{
				player.pause();
				if(GlobalApp.INSTANCE.getSharedpreferences() == null)
				{
					GlobalApp.INSTANCE.savePrefrence(context);
				}
				musicPlayerNotification();
//				SongsMainFragment.changeButton();
				EventBus.getDefault().post(new MessageEvent("changeButton"));
			}
		}
	}

	public static void seek(int posn){
		player.seekTo(posn);
	}

	public void go()
	{
		player.start();

//		SongsMainFragment.changeButton();
		EventBus.getDefault().post(new MessageEvent("changeButton"));
		try
		{
			musicPlayerNotification();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void playPrev()
	{
		if(shuffle)
		{
			int newSong = MusicPlayerControls.SONG_NUMBER;
			while(newSong==MusicPlayerControls.SONG_NUMBER)
			{
				if(GlobalApp.INSTANCE.getMediaItemsArrayList() != null)
					newSong=rand.nextInt(GlobalApp.INSTANCE.getMediaItemsArrayList().size());
			}
			MusicPlayerControls.SONG_NUMBER=newSong;
		}
		else
		{
			MusicPlayerControls.SONG_NUMBER--;
			if (MusicPlayerControls.SONG_NUMBER < 0)
				MusicPlayerControls.SONG_NUMBER = GlobalApp.INSTANCE.getMediaItemsArrayList().size() - 1;
		}
			playSong();
	}

	//skip to next
	public void playNext()
	{
		if(shuffle)
		{
			int newSong = MusicPlayerControls.SONG_NUMBER;
			while(newSong==MusicPlayerControls.SONG_NUMBER)
			{
				newSong=rand.nextInt(GlobalApp.INSTANCE.getMediaItemsArrayList().size());
			}
			MusicPlayerControls.SONG_NUMBER=newSong;
		}
		else
		{
			MusicPlayerControls.SONG_NUMBER++;
			if(MusicPlayerControls.SONG_NUMBER >= GlobalApp.INSTANCE.getMediaItemsArrayList().size())
				MusicPlayerControls.SONG_NUMBER=0;
		}
		playSong();
	}

	//===player notification
	@SuppressLint("NewApi")
	private void musicPlayerNotification()
	{
		if(GlobalApp.INSTANCE.getMediaItemsArrayList() != null && GlobalApp.INSTANCE.getMediaItemsArrayList().size()>0)
		{

			SongsModel mediaItem = GlobalApp.INSTANCE.getMediaItemsArrayList().get(GlobalApp.INSTANCE.getSharedpreferences().getInt(GlobalApp.INSTANCE.getSONGNUMBER(), 0));
			String songName = mediaItem.getTitle();
			String artist = mediaItem.getArtist();
			Uri image = mediaItem.getImg_uri();

			Intent notIntent = new Intent(this, SongsMainFragment.class);
			notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);


			simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.small_notification);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					notification = new NotificationCompat.Builder(getApplicationContext(), context.getString(R.string.default_notification_channel_id))
							.setSmallIcon(R.drawable.notification_icon)
							.setContentTitle(songName)
							.setContentIntent(pendInt)
							.setCustomContentView(simpleContentView)
							.build();
				} else {
					notification = new NotificationCompat.Builder(getApplicationContext())
							.setSmallIcon(R.drawable.notification_icon)
							.setContentTitle(songName)
							.setContentIntent(pendInt)
							.setCustomContentView(simpleContentView)
							.build();
				}


			if (notification != null) {
				notification.contentView = simpleContentView;
				setListeners(simpleContentView,songName,artist,image);


				try
				{
					if (!player.isPlaying()) {
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						mNotifyManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);


						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
							String channelId = context.getString(R.string.default_notification_channel_id);
							NotificationChannel channel = new NotificationChannel(channelId,   "MyMusic", NotificationManager.IMPORTANCE_NONE);
							channel.enableLights(false);
							channel.enableVibration(false);
							channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
							mNotifyManager.createNotificationChannel(channel);
						}

						mNotifyManager.notify(NOTIFICATION_ID, notification);

					} else {
						notification.flags |= Notification.FLAG_NO_CLEAR;
						mNotifyManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
							String channelId = context.getString(R.string.default_notification_channel_id);
							NotificationChannel channel = new NotificationChannel(channelId,   "MyMusic", NotificationManager.IMPORTANCE_NONE);
							channel.enableLights(false);
							channel.enableVibration(false);
							channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
							mNotifyManager.createNotificationChannel(channel);
						}

						mNotifyManager.notify(NOTIFICATION_ID, notification);
					}


					startForeground(NOTIFICATION_ID, notification);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else
		{
			stopService();
			this.stopSelf();
		}
	}
	@SuppressLint("NewApi")
	private void RegisterRemoteClient ()
	{
		remoteComponentName = new ComponentName (getApplicationContext (), new NotificationBroadcast().ComponentName ());
		try {
			if (remoteControlClient == null) {
				Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
				mediaButtonIntent.setComponent (remoteComponentName);
				PendingIntent mediaPendingIntent = PendingIntent.getBroadcast (this, 0, mediaButtonIntent, 0);
				remoteControlClient = new RemoteControlClient (mediaPendingIntent);
			}

		}
		catch (Exception ex)
		{

		}
	}

	public void setListeners(RemoteViews view,String songName,String artist,Uri image)
	{
		Intent previous = new Intent (NOTIFY_PREVIOUS);
		previous.setClass(context,NotificationBroadcast.class);
		Intent delete = new Intent (NOTIFY_DELETE);
		delete.setClass(context,NotificationBroadcast.class);
		Intent pause = new Intent (NOTIFY_PAUSE);
		pause.setClass(context,NotificationBroadcast.class);
		Intent next = new Intent (NOTIFY_NEXT);
		next.setClass(context, NotificationBroadcast.class);
		Intent play = new Intent (NOTIFY_PLAY);
		play.setClass(context,NotificationBroadcast.class);
		Intent openPlayer = new Intent (OPEN_AUDIO_PLAYER);
		openPlayer.setClass(context,NotificationBroadcast.class);

		PendingIntent pPrevious = PendingIntent.getBroadcast (getApplicationContext (), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent (R.id.img_previous_notificationbar_layout, pPrevious);

		PendingIntent pOpenplayer = PendingIntent.getBroadcast (getApplicationContext (), 0, openPlayer, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent (R.id.img_album_notificationbar_layout, pOpenplayer);

		PendingIntent pDelete = PendingIntent.getBroadcast (getApplicationContext (), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent (R.id.img_remove_notificationbar_layout, pDelete);

		view.setImageViewResource(R.id.img_previous_notificationbar_layout, R.drawable.ic_skip_previous);
		view.setImageViewResource(R.id.img_next_notificationbar_layout, R.drawable.ic_skip_next);
		view.setImageViewResource(R.id.img_remove_notificationbar_layout, R.drawable.ic_close);

        view.setTextViewText(R.id.txt_songname_notificationbar_layout, songName);
        view.setTextViewText(R.id.txt_singername_notificationbar_layout, artist);

		Picasso.get().load(image).error(R.mipmap.splash_icon).into(view, R.id.img_album_notificationbar_layout, NOTIFICATION_ID, notification);


			if (isPng())
			{
				PendingIntent pPause = PendingIntent.getBroadcast (getApplicationContext (), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
				view.setOnClickPendingIntent(R.id.img_play_notificationbar_layout, pPause);
				view.setImageViewResource(R.id.img_play_notificationbar_layout, R.drawable.ic_pause_circle_outline);
			}
			else
			{
				PendingIntent pPlay = PendingIntent.getBroadcast (getApplicationContext (), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
				view.setOnClickPendingIntent(R.id.img_play_notificationbar_layout, pPlay);
				view.setImageViewResource(R.id.img_play_notificationbar_layout, R.drawable.ic_play_circle_outline);
			}

		PendingIntent pNext = PendingIntent.getBroadcast (getApplicationContext (), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent (R.id.img_next_notificationbar_layout, pNext);
	}


	public void checkPhoneCall()
	{
		if (GlobalApp.INSTANCE.isServiceRunning (MusicService.class.getName (),getApplicationContext()))
		{
			PhoneStateListener phoneStateListener = new PhoneStateListener()
			{
				@Override
				public void onCallStateChanged(int state, String incomingNumber)
				{
					if (state == TelephonyManager.CALL_STATE_RINGING)
					{
						//pausePlayer();
						context.sendBroadcast(new Intent(GlobalApp.BROADCAST_PLAYPAUSE));
					}
					else if(state == TelephonyManager.CALL_STATE_IDLE)
					{

					}
					else if(state == TelephonyManager.CALL_STATE_OFFHOOK)
					{
						context.sendBroadcast(new Intent(GlobalApp.BROADCAST_PLAYPAUSE));
						//A call is dialing, active or on hold
					}
					super.onCallStateChanged(state, incomingNumber);
				}
			};
			TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			if(mgr != null)
			{
				mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
			}
		}

	}

	@Override
	public void onDestroy()
	{
		unregisterReceiver(buttonplaypauseReceiver);
		unregisterReceiver(widgetplaypauseReceiver);
		unregisterReceiver(widgetnextReceiver);
		unregisterReceiver(widgetprevReceiver);
		unregisterReceiver(widgetpauseReceiver);
		stopForeground(true);


		if (player != null)
		{
			pausePlayer();
			player.stop ();
			player = null;
			if(mNotifyManager != null)
			{
				mNotifyManager.cancel (NOTIFICATION_ID);
			}
			stopSelf();
		}

	}

	//toggle shuffle
	public static void setShuffle()
	{
		if(shuffle)
		{
			SharedPreferences.Editor editor = GlobalApp.INSTANCE.getSharedpreferences().edit();
			editor.putBoolean(GlobalApp.INSTANCE.getIS_SHUFFLE(), false);
			editor.commit();
			shuffle = false;
		}
		else
		{
			SharedPreferences.Editor editor = GlobalApp.INSTANCE.getSharedpreferences().edit();
			editor.putBoolean(GlobalApp.INSTANCE.getIS_SHUFFLE(), true);
			editor.commit();
			shuffle = true;
		}
	}

	public static void setRepeat()
	{
		if(repeat)
		{
			SharedPreferences.Editor editor = GlobalApp.INSTANCE.getSharedpreferences().edit();
			editor.putBoolean(GlobalApp.INSTANCE.getIS_REPEAT(), false);
			editor.commit();
			repeat = false;
		}
		else
		{
			SharedPreferences.Editor editor = GlobalApp.INSTANCE.getSharedpreferences().edit();
			editor.putBoolean(GlobalApp.INSTANCE.getIS_REPEAT(), true);
			editor.commit();
			repeat = true;
		}
	}

	public static boolean isShuffle()
	{
		return shuffle;
	}
	public static boolean isRepeat()
	{
		return repeat;
	}


	public static void stopService()
	{

		if(player != null)
		{
			player.pause();
		}

		try
		{
			if(mNotifyManager != null)
			{
				mNotifyManager.cancel(NOTIFICATION_ID);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
//		SongsMainFragment.changeButton();
		EventBus.getDefault().post("changeButton");
	}


	//---------for sleep timer
	BroadcastReceiver buttonplaypauseReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (player.isPlaying())
			{
				try
				{
					//stopService();
					context.sendBroadcast(new Intent(GlobalApp.BROADCAST_PLAYPAUSE));
					SharedPreferences.Editor editor = GlobalApp.INSTANCE.getSharedpreferences().edit();
					editor.putString(GlobalApp.INSTANCE.getSLEEP_TIMER(),  "false");
					editor.commit();
				}
				catch ( Exception e )
				{
				}
			}
		}
	};
	//-----------------------for app widget=------------------------------

	BroadcastReceiver widgetplaypauseReceiver = new BroadcastReceiver( ) {
		@Override
		public void onReceive( Context context, Intent intent ) {

			am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			am.requestAudioFocus(changeListener, AudioManager.STREAM_MUSIC,
					AudioManager.AUDIOFOCUS_GAIN);

			if(player != null)
			{
				if (isPng())
				{
					try
					{
						pausePlayer();
					}
					catch (Exception e)
					{
						stopService();
					}
				}
				else
				{
					try
					{
						if(player != null && player.getDuration()>0)
						{
							playerStart();
						}
						else
						{
							playSong();
						}
					}
					catch (Exception e)
					{
						playSong();
					}
				}
			}
			else
			{

				playSong();
			}
		}
	};

	BroadcastReceiver widgetnextReceiver = new BroadcastReceiver( ) {
		@Override
		public void onReceive( Context context, Intent intent ) {

			if(player!=null)
			{
				player.reset();
				playNext();
			}

			}
	};

	BroadcastReceiver widgetprevReceiver = new BroadcastReceiver( ) {
		@Override
		public void onReceive( Context context, Intent intent ) {

			if(player!=null)
			{
				playPrev();
			}

			}
	};

	BroadcastReceiver widgetpauseReceiver = new BroadcastReceiver( ) {
		@Override
		public void onReceive( Context context, Intent intent ) {

			if(player!=null)
			{
				pausePlayer();
			}

		}
	};

}
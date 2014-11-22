package com.kazma.mathfactor;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * The play service.
 */
public class PlayService extends Service implements MediaPlayer.OnPreparedListener, 
													MediaPlayer.OnCompletionListener,
													MediaPlayer.OnErrorListener {

    /** The log tag. */
    private static final String LOG_TAG = TheMathFactorActivity.LOG_TAG;

    /** The id of the notification (unique within the app) */
	private static final int NOTIFICATION_PLAY_ID = 1;
    /** The mediaplayer. */ 
    private MediaPlayer mMediaPlayer = null;
	
    /** The item. */
    private Item mItem;
    
    /** The object that receives interactions from clients. */
    private final IBinder mBinder = new LocalBinder();
    
    private Database mDatabase;
    /**
     * Class for PlayActivity to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        PlayService getService() {
            return PlayService.this;
        }
    }
    
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * Starts the play.
	 * 
	 * @param item the episode item to play
	 */
	public void startPlay(Item item) {
		 
	    mItem = item;
	    if (mMediaPlayer != null) {
	    	mMediaPlayer.stop();
	    	mMediaPlayer.reset();
	    } else {
	    	mMediaPlayer = new MediaPlayer();
	    	mMediaPlayer.setOnPreparedListener(this);
	    }
		try {
			mMediaPlayer.setDataSource(mItem.getDownload());
			mMediaPlayer.prepareAsync();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    	mMediaPlayer.setOnPreparedListener(this);
    	mMediaPlayer.setOnCompletionListener(this);
    	mMediaPlayer.setOnErrorListener(this);
    	mDatabase = DatabaseFactory.getDatabase(getApplication());
	}
	   
	/**
	 * Callback that indicates media file is ready for playback.
	 * 
	 * @param mp the mediaplayer instance
	 */
	@Override
	public void onPrepared(MediaPlayer mp) {
    	mMediaPlayer.seekTo(mDatabase.getPosition(mItem));
		mMediaPlayer.start();
		_startForeground();
		mDatabase.setLength(mItem, getDuration());
	}
	

	/**
	 * Get the current item.
	 * 
	 * @return the current item
	 */
	public Item getItem() {
		return mItem;
	}
	
	/**
	 * Return the percentage of current position to total buffer length
	 * 
	 * @return the percentage of current position
	 */
    public int getBufferPercentage() {
    	if (mMediaPlayer == null) {
    		Log.e(LOG_TAG, "getBufferPercentage(): mediaplayer null");
    		return 0;
    	}
        int percentage = (mMediaPlayer.getCurrentPosition() * 100) / mMediaPlayer.getDuration();
        return percentage;
    }

    /**
     * Return current position in milliseconds
     * 
     * @return current position in milliseconds
     */
    public int getCurrentPosition() { 
      	if (mMediaPlayer == null) {
    		Log.e(LOG_TAG, "getCurrentPosition(): mediaplayer null");
    		return 0;
    	}
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * Return buffer duration in milliseconds
     * 
     * @return buffer duration
     */
    public int getDuration() {
      	if (mMediaPlayer == null) {
    		Log.e(LOG_TAG, "getDuration(): mediaplayer null");
    		return 0;
    	}
        return mMediaPlayer.getDuration();
    }
    

    /**
     * Return whether the mediaplayer is playing.
     * 
     * @return whether the mediaplayer is playing
     */
    public boolean isPlaying() {
      	if (mMediaPlayer == null) {
    		Log.e(LOG_TAG, "isPlaying(): mediaplayer null");
    		return false;
    	}
        return mMediaPlayer.isPlaying();
    }

    /**
     * Pause mediaplayer.
     */
    public void pause() {
      	if (mMediaPlayer == null) {
    		Log.e(LOG_TAG, "pause(): mediaplayer null");
    		return ;
    	}
        if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
        mDatabase.setPosition(mItem, getCurrentPosition());
        stopForeground(true);
    }
    
	/**
     * Seek to a position.
     * 
     * @param pos the position in milliseconds
     */
    public void seekTo(int pos) {
      	if (mMediaPlayer == null) {
    		Log.e(LOG_TAG, "seekTo(): mediaplayer null");
    		return;
    	}
        mMediaPlayer.seekTo(pos);
    }

    /**
     * Start the mediaplayer.
     */
    public void start() {
      	if (mMediaPlayer == null) {
    		Log.e(LOG_TAG, "start(): mediaplayer null");
    		return;
    	}
        mMediaPlayer.start();
        _startForeground();
    }

    
    /**
     * Callback from mediaplayer to indicate completion.
     * @param mp the mediaplayer
     */
	@Override
	public void onCompletion(MediaPlayer mp) {
		stopForeground(true);
		mDatabase.setPosition(mItem, getDuration());
		mMediaPlayer.release();
		mMediaPlayer = null;
	}

	/**
	 * Callback from mediaplayer to indicate error.
	 * 
	 * @param mp the mediaplayer
	 * @param what the type of error
	 * @param extra an extra error code
	 * @return
	 */
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.e(LOG_TAG, "An unhandled mediaplayer error has occurred:" + what + " " + extra);
		return false;
	}
	
	/**
	 * Makes this a foreground service.
	 */
	private void _startForeground() {
		
		Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
		intent.putExtra("title", mItem.getTitle());
		intent.putExtra("download", mItem.getDownload());
		intent.putExtra("link", mItem.getLink());
		// create a pending notification and make it a foreground service
	    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
	    		         	PendingIntent.FLAG_UPDATE_CURRENT);
	 
	    Notification notification = new Notification(); 
	    notification.tickerText = mItem.getTitle();
	    notification.icon = android.R.drawable.ic_media_play;
	    notification.flags |= Notification.FLAG_ONGOING_EVENT;
	    notification.setLatestEventInfo(getApplicationContext(), "The Math Factor",
	    								"Playing: " + mItem.getTitle(), pendingIntent);
	    
	    startForeground(NOTIFICATION_PLAY_ID, notification);
	}
}

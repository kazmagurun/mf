package com.kazma.mathfactor;

import com.kazma.mathfactor.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

/**
 * Plays the episode.
 */
public class PlayActivity extends Activity implements MediaPlayerControl {
	
	/** The log tag. */
	final static String LOG_TAG = TheMathFactorActivity.LOG_TAG;
	
	/** The webview. */
	private WebView _webView;

	/** The mediacontroller. */
	private MediaController _mediaController;
	
	/** The item. */
	private Item _item;
	
	/** The play service. */
	private PlayService _playService = null;
	/** Whether the service is bound. */
	private boolean _bound = false;
	/** The service connection. */
	private ServiceConnection _serviceConnection = new ServiceConnection() {
		
		/**
		 * Called when a connection to the play service is established.
		 * 
		 * @param className the concrete component name of the service that has been connected
		 * @param service the IBinder interface to the service
		 */
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(LOG_TAG, "Connected to playservice");
			_playService = ((PlayService.LocalBinder)service).getService();
			
			Item currentItem = _playService.getItem();
			if (currentItem == null || ! currentItem.getTitle().equals(_item.getTitle())) {
				_playService.startPlay(_item);
			}
		}

		/**
		 * Called when a connection to the play service has been lost.
		 * 
		 * @param className the concrete component name of the service that has been disconnected
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(LOG_TAG, "Disconnected from playservice");
			_playService = null;
		}
	};
	
	/** 
	 * Called when the activity is first created. Creates a new mediaplayer and starts playing.
	 * 
	 * @param savedInstanceState saved state
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.play);        
        _webView = (WebView) findViewById(R.id.webview);
        WebSettings settings = _webView.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setBuiltInZoomControls(true);
        _webView.setWebViewClient(new WebViewClient() {
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		return false;
        	}
        });
    }
  
    /**
     * Loads a webview the episode.
     */
    @Override
    protected void onStart() {
        super.onStart(); 
        
        String title = getIntent().getStringExtra("title");
        String link = getIntent().getStringExtra("link");
    	String download = getIntent().getStringExtra("download"); 
    	_item = new Item(title, link, download);
    	
        _webView.loadUrl(link);
        
    	Intent intent = new Intent(this, PlayService.class);
    	// start the service. this will make sure it will be around until explicitly stopped
    	startService(intent);
    	// bind to the service. 
    	bindService(intent, _serviceConnection, 0);
    	_bound = true;
    	
        _mediaController = new MediaController(this);
        _mediaController.setMediaPlayer(this);
        _mediaController.setAnchorView(findViewById(R.id.audioView));
    }
    
    /**
     * Stop the play activity.
     */
    @Override
    protected void onStop() {
    	super.onStop();
    	// to prevent leaks on configuration change
    	if (_mediaController != null) _mediaController.hide();
    }
    
    /**
     * Destroy the play activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (_bound) unbindService(_serviceConnection);
    }       
 
    /**
     * Whether mediaplayer control can pause.
     */
    @Override
    public boolean canPause() {
    	return true;
    }

    /**
     * Whether mediaplayer control can seek backward.
     * 
     * @return whether mediaplayer control can seek backward
     */
    @Override
    public boolean canSeekBackward() {
        return true;
    }

    /**
     * Whether mediaplayer control can seek forward.
     * 
     * @return whether mediaplayer control can seek forward
     */
    @Override
    public boolean canSeekForward() {
        return true;
    }

	/**
	 * Return the percentage of current position to total buffer length.
	 * 
	 * @return the percentage of current position
	 */
    @Override    
    public int getBufferPercentage() {
        return _playService == null ? 0 : _playService.getBufferPercentage();
    }

    /**
     * Return current position in milliseconds.
     * 
     * @return current position
     */
    @Override
    public int getCurrentPosition() {
    	return _playService == null ? 0 : _playService.getCurrentPosition();
    }

    /**
     * Return buffer duration in milliseconds.
     * 
     * @return buffer duration
     */
    @Override
    public int getDuration() {
        return _playService == null ? 0 : _playService.getDuration();
    }

    /**
     * Return whether the mediaplayer is playing.
     * 
     * @return whether the mediaplayer is playing
     */
    @Override
    public boolean isPlaying() {
        return _playService == null ? false : _playService.isPlaying();
    }

    /**
     * Pause mediaplayer.
     */
    @Override
    public void pause() {
        if (_playService != null) _playService.pause(); 
    }
    
    /**
     * Seek to a position.
     * 
     * @param pos the position in milliseconds
     */
    @Override
    public void seekTo(int pos) {
        if (_playService != null) _playService.seekTo(pos);
    }

    /**
     * Start the mediaplayer.
     */
    @Override
    public void start() {
        if (_playService != null) _playService.start();
    }

    /**
     * Process the touch event.
     * 
     * @param event the motion event
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (_mediaController != null) _mediaController.show();        
        return false;
    }

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}
}

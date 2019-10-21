package net.kwatts.android.NetScanner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import net.kwatts.android.NetScanner.service.IPlaybackService;

import timber.log.Timber;

public class PlaybackService extends Service implements OnPreparedListener, OnCompletionListener, OnErrorListener,
OnInfoListener {


  public static final String INTENT_FILTER = "PlaybackServiceIntentFilter";
  public static final String INTENT_AUDIO_SESSION_ID = "intent_audio_session_id";

  public final MediaPlayer player = new MediaPlayer();
  public Context mContext;
  public String mCurrentUrl;
  
  private OnPreparedListener mOnPreparedListener;
  private OnBufferingUpdateListener mOnBufferingUpdateListener;
  private OnErrorListener mOnErrorListener;

  
  boolean isPlaying = false;
  boolean isStop = false;

  @Override
  public void onCreate() {
    super.onCreate();
    //player.setOnBufferingUpdateListener(this);
    
    mContext = this;
    
    player.setOnCompletionListener(this);
    player.setOnErrorListener(this);
    player.setOnInfoListener(this);
    player.setOnPreparedListener(this);

    //put the same message as in the filter you used in the activity when registering the receiver
    Intent intent = new Intent(INTENT_FILTER);
    intent.putExtra(INTENT_AUDIO_SESSION_ID, player.getAudioSessionId());
	LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (player.isPlaying()) {
      player.stop();
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
	  return mBinder;
  }


  private final IPlaybackService.Stub mBinder = new IPlaybackService.Stub() {
	  public boolean play(String url) {
          //Intent intent = new Intent(INTENT_FILTER);
          //intent.putExtra(INTENT_AUDIO_SESSION_ID, player.getAudioSessionId());
          //LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

		  mCurrentUrl = url;

		  Timber.d("Starting media player stream for " + mCurrentUrl + "..." );
		  Thread startThread = new Thread() { 
		         public void run() {
		 		    try {
						  String url = Util.getRedirectUrl(mCurrentUrl);
						  Timber.d("Got the redirect url: " + url);
		 			      player.reset();
		 			      player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		 			      player.setDataSource(url);
		 			      player.prepare(); // may take a bit to buffer
		 			      player.start();
		 			      //player.setVolume(1.0f, 1.0f);

		 			    } catch (Exception e) {
		 			    	// Display the error, close the window
		 			    	Timber.d( "Unable to stream the station:" + e.getMessage());
		 		            Message msg_down = PlayerFragment.mHandler.obtainMessage(PlayerFragment.MSG_STATUS_RED);
		 		            msg_down.obj = "Station might be down: " + e.getMessage();
							PlayerFragment.mHandler.sendMessage(msg_down);
		 		            // Stop Animation
		 		            Message msg_stopAnimation = PlayerFragment.mHandler.obtainMessage(PlayerFragment.MSG_STOP_LOADING_ANIMATION);
							PlayerFragment.mHandler.sendMessage(msg_stopAnimation);
		 		            // Disable button
		 		            Message msg_disable_pause = PlayerFragment.mHandler.obtainMessage(PlayerFragment.MSG_DISABLE_PAUSE_BUTTON);
							PlayerFragment.mHandler.sendMessage(msg_disable_pause);
		 		            
		 			    	player.stop();	
		 			    	
		 			    }
		        	 
		         }
		  };
		  
		  startThread.start();
		  
/*		  
			Log.d(MSG_TAG, "In the play for the playback service, playing:" + url);		
            Message msg = PlayerActivity.mHandler.obtainMessage(PlayerActivity.MSG_STATUS_GREY);
            msg.obj = "Loading the stream...";
            PlayerActivity.mHandler.sendMessage(msg);
            
			//PlayerActivity.footerText.setTextColor(Color.WHITE);
			//PlayerActivity.footerText.setText("Loading audio stream " + url);

		    if (player.isPlaying()) {
		      player.stop();
		    }

		    try {
		    	
		      if (isPlaying == true) { 
		    	  player.reset();
		      }
		      
		      player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		      player.setDataSource(url); 
		      player.prepare();
		      player.setVolume(1.0f, 1.0f);

		    } catch (Exception e) {
		    	// Display the error, close the window
		    	Log.d(MSG_TAG, "Unable to stream the station:" + e.getMessage());
	            Message msg_down = PlayerActivity.mHandler.obtainMessage(PlayerActivity.MSG_STATUS_RED);
	            msg_down.obj = "Station might be down: " + e.getMessage();
	            PlayerActivity.mHandler.sendMessage(msg_down);		    	
		    	player.stop();	
		    	
		    }
		    */

		    return true;
		  }

	  public void stop() {
			Timber.d("Stopping the mediaplayer stream...");
			Thread stopThread = new Thread() { 
				public void run() {
					try {			
						Message msg_stop = PlayerFragment.mHandler.obtainMessage(PlayerFragment.MSG_STATUS_GREY);
						msg_stop.obj = "Stopping stream...";
						PlayerFragment.mHandler.sendMessage(msg_stop);
						isStop = true;	
						//if (player.isPlaying()) {
						player.stop();
						player.reset();
						// https://developer.android.com/guide/topics/media/mediaplayer
						//player.release()
						//player = null;
					} catch (Exception e) {
						Timber.e(e);
					}
				}
			};

			stopThread.start();
		    /* Suck. Need to state a state and check if it's still resetting at the top.
		    new Thread(new Runnable() {
		        public void run() {
		        	    Log.d(MSG_TAG, "Resetting the stream...");
		                player.reset();
		        }

		    }).start(); */ 
		    isPlaying = false;
		    //stopSelf();   
		  }

		  public int getDuration() {
		    return player.getDuration();
		  }

		  public int getCurrentPosition() {
		    return player.getCurrentPosition();
		  }

		  public void start() {
		    player.start();
		  }

		  public void pause() {
		    Message msg = PlayerFragment.mHandler.obtainMessage(PlayerFragment.MSG_STATUS_YELLOW);
		    msg.obj = "Paused";
		    PlayerFragment.mHandler.sendMessage(msg);
		    player.pause();
		  }

		  public boolean isPlaying() {
		    return player.isPlaying();
		  }

		  public void seekTo(int pos) {
		    player.seekTo(pos);
		  }

		  public void setLooping(boolean looping) {
		    player.setLooping(looping);
		  }

		  public void setVolume(float leftVolume, float rightVolume) {
		    player.setVolume(leftVolume, rightVolume);
		  }
  };
  

@Override
public void onPrepared(MediaPlayer arg0) {
	Timber.d( "In inPrepared, starting to play the stream!");
    player.setOnCompletionListener(this);
	player.start();
    	
	//Message msg_stop_animation = PlayerFragment.mHandler.obtainMessage(PlayerFragment.MSG_STOP_LOADING_ANIMATION);
	//PlayerFragment.mHandler.sendMessage(msg_stop_animation);
    	
	Message msg_set_pause = PlayerFragment.mHandler.obtainMessage(PlayerFragment.MSG_ENABLE_PAUSE_BUTTON);
	PlayerFragment.mHandler.sendMessage(msg_set_pause);
	Message msg_start = PlayerFragment.mHandler.obtainMessage(PlayerFragment.MSG_STATUS_GREEN);
	msg_start.obj = "Playing...";
	PlayerFragment.mHandler.sendMessage(msg_start);
}

@Override
public void onCompletion(MediaPlayer mp) {
	Timber.d("In onCompletion, should never get here.");
	
}

@Override
public boolean onError(MediaPlayer mp, int what, int extra) {
	Timber.d("Error: " + what + " " + extra);
	return false;
}

@Override
public boolean onInfo(MediaPlayer mp, int what, int extra) {
	Timber.d("Info: " + what + " " + extra);
	return false;
}

}


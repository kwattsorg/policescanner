package net.kwatts.android.NetScanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.kwatts.android.NetScanner.service.IPlaybackService;

import timber.log.Timber;


public class PlayerFragment extends Fragment {

    public static final String MSG_TAG = "NetScanner";

    private FragmentActivity mFragmentActivity;

    String mStreamLocation;
    String mStreamGenre;
    String mStreamTitle;
    String mStreamURL;

    //
    public static ImageButton playButton;
    ImageButton closeButton;
    public static ImageButton pauseButton;

    public TextView headerText;
    public TextView genreText;
    public TextView locationText;

    private PlayerViewModel viewModel;

    public static TextView statusText;

    public static ImageView playerAnimationView;
    public static AnimationDrawable playingAnimation;

    public NotificationManager mNotificationManager;

    public static Notification mNotification;

    public IPlaybackService mPlaybackService = null;

    public String mCurrentStream;
    public String mCurrentTitle;

    boolean isStarted = false;
    boolean isStop = false;


    public static final int MSG_STATUS_GREEN = 1;
    public static final int MSG_STATUS_RED = 2;
    public static final int MSG_STATUS_GREY = 3;
    public static final int MSG_START_LOADING_ANIMATION = 4;
    public static final int MSG_STOP_LOADING_ANIMATION = 5;
    public static final int MSG_START_TIMER = 6;
    public static final int MSG_STOP_TIMER = 7;
    public static final int MSG_ENABLE_PAUSE_BUTTON = 8;
    public static final int MSG_DISABLE_PAUSE_BUTTON = 9;
    public static final int MSG_ENABLE_PLAY_BUTTON = 10;
    public static final int MSG_DISABLE_PLAY_BUTTON = 11;
    public static final int MSG_STATUS_YELLOW = 12;


    // Need handler for callbacks to the UI thread
    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MSG_STATUS_YELLOW:
                    String status_yellow = (String) msg.obj;
                    statusText.setTextColor(Color.YELLOW);
                    statusText.setText(status_yellow);
                    break;
                case MSG_STATUS_GREEN:
                    String status_green = (String) msg.obj;
                    statusText.setTextColor(Color.GREEN);
                    statusText.setText(status_green);
                    break;
                case MSG_STATUS_RED:
                    String status_red = (String) msg.obj;
                    statusText.setTextColor(Color.RED);
                    statusText.setText(status_red);
                    break;
                case MSG_STATUS_GREY:
                    String status_grey = (String) msg.obj;
                    statusText.setTextColor(Color.LTGRAY);
                    statusText.setText(status_grey);
                    break;
                case MSG_START_LOADING_ANIMATION:
                    PlayerFragment.playerAnimationView.setVisibility(View.VISIBLE);
                    playingAnimation.start();
                    break;
                case MSG_STOP_LOADING_ANIMATION:
                    PlayerFragment.playerAnimationView.setVisibility(View.GONE);
                    break;
                case MSG_ENABLE_PAUSE_BUTTON:
                    playButton.setImageResource(R.drawable.button_pause);
                    playButton.setEnabled(true);
                    playButton.setVisibility(View.VISIBLE);
                    break;
                case MSG_DISABLE_PAUSE_BUTTON:
                    playButton.setImageResource(R.drawable.button_pause);
                    playButton.setEnabled(false);
                    break;
                case MSG_ENABLE_PLAY_BUTTON:
                    break;
                case MSG_DISABLE_PLAY_BUTTON:
                    break;
                case MSG_START_TIMER:
                    break;
                case MSG_STOP_TIMER:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private Intent mIntent;
    private RelativeLayout mRelativeLayout;


    /**
     * During creation, if arguments have been supplied to the fragment
     * then parse those out.
     */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // add your code here which executes when fragment's instance initializes
        viewModel = ViewModelProviders.of(this.getActivity()).get(PlayerViewModel.class);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player, container, false);
        // add your code here to draw the UI for the first time means in this method ...
        // ... we can get the reference of the views which are created in our xml file

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // add your code here which executes after the execution of onCreateView() method.
        mRelativeLayout = (RelativeLayout) view;
        headerText   = view.findViewById(R.id.player_header);
        genreText    = view.findViewById(R.id.player_genre);
        locationText = view.findViewById(R.id.player_location);
        statusText = view.findViewById(R.id.player_status);
        playButton = view.findViewById(R.id.play_imagebutton);

    }

    // Called when the activity for this fragment is created
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            //Gets the activity that called this fragment
            mFragmentActivity = getActivity();
            // set the notification manager
            //mNotificationManager = (NotificationManager) mFragmentActivity.getSystemService(Context.NOTIFICATION_SERVICE);

//            mFragmentActivity.bindService(new Intent(mFragmentActivity, PlaybackService.class), mPlayerConnection, Context.BIND_AUTO_CREATE);
/*
            mIntent = mFragmentActivity.getIntent(); //  Intent intent = new Intent(getActivity().getIntent());
            String streamURL = mFragmentActivity.getIntent().getStringExtra("com.kwatts.android.NetScanner.StreamURL");
            String streamTitle = mFragmentActivity.getIntent().getStringExtra("com.kwatts.android.NetScanner.StreamTitle");
            String stringGenre = mFragmentActivity.getIntent().getStringExtra("com.kwatts.android.NetScanner.StreamGenre");
            String stringLocation = mFragmentActivity.getIntent().getStringExtra("com.kwatts.android.NetScanner.StreamLocation");

            mCurrentTitle = streamTitle;
            mCurrentStream = streamURL;

            headerText.setText(streamTitle);
            genreText.setText(stringGenre);
            locationText.setText(stringLocation);

            playerAnimationView.setBackgroundResource(R.drawable.myanim);
            playingAnimation = (AnimationDrawable) playerAnimationView.getBackground();
            playerAnimationView.post(new Starter());
            playButton.setEnabled(false);
            playButton.setVisibility(View.INVISIBLE);
            footerText.setTextColor(Color.LTGRAY);
            footerText.setText("Loading...");



            playerAnimationView.setBackgroundResource(R.drawable.myanim);
            playingAnimation = (AnimationDrawable) playerAnimationView.getBackground();
            playerAnimationView.post(new Starter());
            playButton.setEnabled(false);
            playButton.setVisibility(View.INVISIBLE);
            footerText.setTextColor(Color.LTGRAY);
            footerText.setText("Loading...");
*/
            // set the notification manager
        } catch (Exception e) {
            Timber.e(e);
        }

    }



    /**
     * Class for interacting with the Player Service.
     */

/*
    private ServiceConnection mPlayerConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            try {
                mPlaybackService = IPlaybackService.Stub.asInterface(service);
                Timber.d( "Service is connected...");
                //playButton.setImageResource(R.drawable.button_pause);
                //new PlayerTask().execute(mCurrentStream);
                //mPlaybackService.play(mCurrentStream);
            } catch (Exception e) { }
        }

        public void onServiceDisconnected(ComponentName className) {
            mPlaybackService = null;
        }
    };
*/
    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
        try {
            //mPlaybackService.stop();
            //unbindService(mPlayerConnection);
        } catch (Exception e) {

        }

    }

    public void onDestroy() {
        super.onDestroy();
        try {
            mPlaybackService.stop();
            mNotificationManager.cancel(1);
            //mFragmentActivity.unbindService(mPlayerConnection);
        } catch (Exception e) {

        }
    }

    /*
    public void playStream(String stream) {

     try {

    	mCurrentStream = stream;

    	Log.d(MSG_TAG, "In playstream about to play:" + stream);
    	//PlayerActivity.footerText.setTextColor(Color.LTGRAY);
    	//PlayerActivity.footerText.setText("Loading audio stream...");
    	playButton.setImageResource(R.drawable.button_pause);
    	mPlaybackService.play(mCurrentStream);


     } catch (Exception e) {
    		Log.d(MSG_TAG, "Exception: " );
    		e.printStackTrace();
    	}
    }
    */

    public void prepare() {
        headerText.setText(viewModel.getStreamDescription());
        genreText.setText(viewModel.getStreamGenre());
        locationText.setText(viewModel.getStreamLocation());
        //statusText.setTextColor(Color.LTGRAY);
        //statusText.setText("Ready...");
        playButton.setEnabled(true);
    }

    public void startPlayer() {
        playButton.setImageResource(R.drawable.button_play);
    }

    public void pausePlayer() {
        playButton.setImageResource(R.drawable.button_pause);
        playButton.setEnabled(false);
    }


    public void playClickHandler(View target) {
        try {
            if (mPlaybackService.isPlaying()) {
                mPlaybackService.pause();
                playButton.setImageResource(R.drawable.button_play);
            } else {
                //service.start();
                playButton.setImageResource(R.drawable.button_pause);

                playerAnimationView.setVisibility(View.VISIBLE);
                playingAnimation.start();
                playButton.setEnabled(false);

                statusText.setTextColor(Color.LTGRAY);
                statusText.setText("Reconnecting..");

                mPlaybackService.play(mStreamURL);
            }


        } catch (Exception e) {
            Timber.e(e);
        }

    }

    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Police Codes").setIcon(android.R.drawable.ic_menu_info_details);
        //menu.add(0, 2, 0, "List View").setIcon(android.R.drawable.ic_menu_add);
        //menu.add(0, 3, 0, "About").setIcon(android.R.drawable.ic_menu_send);
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                //Intent policeCodeIntent = new Intent(PlayerActivity.this,PoliceCodeActivity.class);
                //PlayerActivity.this.startActivity(policeCodeIntent);
                return true;
            case 2:
                return true;
            case 3:
                return true;

        }
        return false;
    }


    // For Animation start

    class Starter implements Runnable {

        public void run() {
            playingAnimation.start();

        }

    }


}





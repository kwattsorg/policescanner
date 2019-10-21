package net.kwatts.android.NetScanner;

///import com.google.android.maps.*;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chibde.visualizer.LineBarVisualizer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.libraries.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.libraries.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.UiSettings;
import com.google.android.libraries.maps.model.BitmapDescriptor;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.Marker;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import net.kwatts.android.NetScanner.model.County;
import net.kwatts.android.NetScanner.model.Feed;
import net.kwatts.android.NetScanner.model.Feeds;
import net.kwatts.android.NetScanner.service.IPlaybackService;

import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import timber.log.Timber;

//import android.widget.*;
// https://github.com/googlemaps/android-samples/blob/master/tutorials/MapWithMarker/app/src/main/java/com/example/mapwithmarker/MapsMarkerActivity.java
// www.radioreference.com
// google maps API: http://code.google.com/apis/maps/documentation/webservices/index.html
// baloon popup: http://github.com/jgilfelt/android-mapviewballoons#readme

//TODO (One item a day)
// - #1 (Done 8/3) Add a baloon popup for the scanner sources. It should give the basic information, then when clicked... 
// - #2 (Partly Done 8/4) The player popup needs a (1) close button (2) bigger (3) possible steg line sound thing? 
// - #2.1 Add to map overlay based on radio source, ie, plane for airport.
// -- need icons for Aviation, Rail, Public Safety, Amateur Radio, Marine, Disaster Event
// -- https://iconmonstr.com/?s=disaster
// - #3 Need to figure out state/location based off geocoordinates -- reverse geocoding
// - #3.1 http://stackoverflow.com/questions/1773717/android-maps-how-to-determine-map-center-after-a-drag-has-been-completed
// - Stretch: A search feature, when found will animate/zoomto that tower
// -          Need to update a state when moved there.


//v2 notes
// Using Google Maps v3 https://developers.google.com/maps/documentation/android-sdk/start
// ZOOM levels https://developers.google.com/maps/documentation/android-sdk/views#zoom:
//  1: World
//  5: Landmass/continent
//  10: City
//  15: Streets
//  20: Buildings
//TODO: add clustering https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering


public class MainActivity extends AppCompatActivity
		implements OnMyLocationButtonClickListener, OnMyLocationClickListener, OnMarkerClickListener,OnMapReadyCallback,
		ActivityCompat.OnRequestPermissionsResultCallback {


	public static final String MSG_TAG = "NetScanner";


	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
	private static final int AUDIO_PERMISSION_REQUEST_CODE = 2;
	private static final int WRITE_EXTERNAL_STORAGE_PERMS = 3;


	private static final float DEFAULT_ZOOM = 8f;
	private static final String DEFAULT_STATE_USA = "California";
	public static final String CHANNEL_ID = "main";

	private boolean mPermissionDenied = false;


	public static GoogleMap mMap;

	public PlayerFragment mPlayerFragment;
	public BottomSheetBehavior mBottomSheetBehaviour;

	public static LocationManager mLocationManager;
	public static Location mLastLocation;

	public IPlaybackService mPlaybackService = null;
	private PlayerViewModel playerViewModel;

	public LineBarVisualizer mLineBarVisualizer;

	public AdView mAdView;
	public View mAdmobAds;


	String mCurrentState = "California";
	List<String> stateList = new ArrayList<String>();
	
	public static Random mRandom = new Random();
	public static Context mContext;

	public static NotificationCompat.Builder mNotificationBuilder;
	public static NotificationManagerCompat mNotificationManager;

    //Feeds mFeeds;
	public static final int TOAST_MSG = 1;
	public static final int GOTOSTATE_MSG = 2;
	public static final int GOTOLATLON_MSG = 3;

	public static BitmapDescriptor mMarkerPolice;
	public static BitmapDescriptor mMarkerAmbulance;
	public static BitmapDescriptor mMarkerAirport;
	public static BitmapDescriptor mMarkerBroadcast;
	public static BitmapDescriptor mMarkerDefault;
	
	// Used to determine if a feed has the same geo-coords as another one, offsets if it does.
	public static List<LatLng> coordsList = new ArrayList<LatLng>();
	
    // Need handler for callbacks to the UI thread
    public static Handler mHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
            switch (msg.what)
            {
            case TOAST_MSG:
            		String message = (String) msg.obj;
        			Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    break;    
            case GOTOSTATE_MSG:
            		String state = (String) msg.obj;
					Double lat = Constants.stateLookupMap.get(state).latitude;
					Double lon = Constants.stateLookupMap.get(state).longitude;
				    Timber.d( "Loading " + state + " and coordinates " + lat + "," + lon);
					loadFeed(state);
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),DEFAULT_ZOOM));
            		break;
            case GOTOLATLON_MSG:
					LatLng loc = (LatLng) msg.obj;
					String s = getStateUsingGoogle(loc.latitude, loc.longitude);
			  	    Timber.d( "Loading " + s + " and coordinates " + loc.latitude + "," + loc.longitude);
			  	    loadFeed(s);
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,DEFAULT_ZOOM));
					break;
            default:
                    super.handleMessage(msg);
            }
    	}
    };
    
    

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();

		//Permission for audio visualizer
		enableAudio();

		MobileAds.initialize(this, App.ADMOB_ID);
		playerViewModel = ViewModelProviders.of(this).get(PlayerViewModel.class);
		mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);

		//Sets up the notification channel for media player
		createNotificationChannel();
		mNotificationManager = NotificationManagerCompat.from(this);



		try {
			setContentView(R.layout.main_activity);

			//Ad banner: https://developers.google.com/admob/android/banner?hl=en-US
			mAdmobAds = findViewById(R.id.adView);
			LinearLayout topLinearLayout = findViewById(R.id.topLinearLayout);
			if (App.mSharedPref.getBoolean("disableAds", false)) {
				 topLinearLayout.removeView(mAdmobAds);
			} else {
				mAdView = findViewById(R.id.adView);
				AdRequest adRequest = new AdRequest.Builder().build();
				mAdView.loadAd(adRequest);
			}



			mLineBarVisualizer = findViewById(R.id.visualizer);
			mLineBarVisualizer.setColor(ContextCompat.getColor(this, R.color.colorMarkerFill));
			// define custom number of bars you want in the visualizer between (10 - 256).
			mLineBarVisualizer.setDensity(50);


			// Bottom slideout view: http://thetechnocafe.com/make-bottom-sheet-android/
			View nestedScrollView = findViewById(R.id.nestedScrollView);
			mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView);
			mBottomSheetBehaviour.setPeekHeight(0);
			mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
			mBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
				@Override
				public void onStateChanged(@NonNull View bottomSheet, int newState) {
					String state = "";

					switch (newState) {
						case BottomSheetBehavior.STATE_DRAGGING: {
							state = "DRAGGING";
							break;
						}
						case BottomSheetBehavior.STATE_SETTLING: {
							state = "SETTLING";
							break;
						}
						case BottomSheetBehavior.STATE_EXPANDED: {
							state = "EXPANDED";
							break;
						}
						case BottomSheetBehavior.STATE_COLLAPSED: {
							state = "COLLAPSED";
							break;
						}
						case BottomSheetBehavior.STATE_HIDDEN: {
							state = "HIDDEN";
							break;
						}
					}

					//Toast.makeText(MainActivity.this, "Bottom Sheet State Changed to: " + state, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSlide(@NonNull View bottomSheet, float slideOffset) {

				}
			});

			//setup icons
			mMarkerPolice = Util.vectorToBitmap(R.drawable.map_police_pin, ContextCompat.getColor(getApplicationContext(),
						R.color.colorMarkerFill));
			mMarkerAmbulance = Util.vectorToBitmap(R.drawable.map_ambulance1, ContextCompat.getColor(getApplicationContext(),
						R.color.colorMarkerFillDarkRed));
			mMarkerAirport = Util.vectorToBitmap(R.drawable.map_airport_pin, ContextCompat.getColor(getApplicationContext(),
						R.color.colorMarkerFill));
			mMarkerBroadcast = Util.vectorToBitmap(R.drawable.map_broadcast_pin, ContextCompat.getColor(getApplicationContext(),
						R.color.colorMarkerFill));
			mMarkerDefault = BitmapDescriptorFactory.fromResource(R.drawable.antenna_blue_48x48);

		} catch (Exception e) {
			 Timber.e("Error loading MainActivity");
		 }
		
	}

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter(PlaybackService.INTENT_FILTER));
        bindService(new Intent(this, net.kwatts.android.NetScanner.PlaybackService.class),
                mPlayerConnection, Context.BIND_AUTO_CREATE);

		//Start maps!
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
    }


	@Override
	public void onMapReady(GoogleMap map) {
		mMap = map;
		UiSettings settings = mMap.getUiSettings();
		settings.setZoomControlsEnabled(true);

		mMap.setOnMyLocationButtonClickListener(this);
		mMap.setOnMyLocationClickListener(this);
		mMap.setOnMarkerClickListener(this);


		//Permission checks and initial flyto's
		enableMyLocation();


	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		Timber.d("Marker clicked!" + marker.getTitle());
		Feed f = (Feed) marker.getTag();

		String feedCountyName = f.getCounties().get(0).name;
		String feedCountryCode = f.getCounties().get(0).countryCode;
		String feedStateName = f.getCounties().get(0).stateName;
		String feedStateCode = f.getCounties().get(0).stateCode;
		String feedStreamLocation = feedCountyName + ", " + feedStateCode + ", " + feedCountryCode;


		playerViewModel.setStreamGenre(f.genre);
		playerViewModel.setStreamDescription(f.descr);
		playerViewModel.setStreamLocation(feedStreamLocation);
		playerViewModel.setStreamURL("http://" + f.getRelays().get(0).host  + f.mount);

		mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
		mBottomSheetBehaviour.setPeekHeight(290);

		//Notifications for streams playing
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		//intent.putExtra("com.kwatts.android.NetScanner.StreamLocation", playerViewModel.getStreamLocation());
		//intent.putExtra("com.kwatts.android.NetScanner.StreamGenre", playerViewModel.getStreamGenre());
		//intent.putExtra("com.kwatts.android.NetScanner.StreamTitle", playerViewModel.getStreamDescription());
		//intent.putExtra("com.kwatts.android.NetScanner.StreamURL", playerViewModel.getStreamURL());

		//intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		mNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setSmallIcon(R.drawable.antenna_blue_48x48)
				.setContentTitle(playerViewModel.getStreamDescription())    			// expanded message title
				.setContentText(feedStreamLocation)						// expanded message text
				.setContentInfo(playerViewModel.getStreamGenre())
				.setTicker("NetScanner")
				.setWhen(System.currentTimeMillis()) 	// notification time, now
				.setOngoing(true)
				.setPriority(NotificationCompat.PRIORITY_LOW)
				.setOnlyAlertOnce(true)
				.setAutoCancel(false)
				.setContentIntent(pendingIntent);
		mNotificationManager.notify(1, mNotificationBuilder.build());

		mPlayerFragment  = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentPlayer);
		mPlayerFragment.prepare();

		return false;
	}

	private void enableAudio() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
				!= PackageManager.PERMISSION_GRANTED) {
			// Request permission without stopping activity
			PermissionUtils.requestPermission(this, AUDIO_PERMISSION_REQUEST_CODE,
					Manifest.permission.RECORD_AUDIO, false);
		} else {
			Timber.d("We already have permission for audio.");
		}
	}

	//Permission to get current location, else request default
	private void enableMyLocation() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
					!= PackageManager.PERMISSION_GRANTED) {
			// Request permission without stopping activity
			PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
					Manifest.permission.ACCESS_FINE_LOCATION, false);
		} else if (mMap != null) {
			// Location is enabled and map is ready, get the current state and fly to it.
			mMap.setMyLocationEnabled(true);
			mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			Message msg;
			if (mLastLocation != null) {
				msg = MainActivity.mHandler.obtainMessage(MainActivity.GOTOLATLON_MSG);
				msg.obj = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
			} else {
				msg = MainActivity.mHandler.obtainMessage(MainActivity.GOTOSTATE_MSG);
				msg.obj = "California";
			}

			MainActivity.mHandler.sendMessage(msg);
		}
	}

	private void requestMyLocation() {
		if (App.mSharedPref.getBoolean("has_picked_a_state", false)) {
			String defaultState = App.mSharedPref.getString("state_selected","California");
			Message msg = MainActivity.mHandler.obtainMessage(MainActivity.GOTOSTATE_MSG);
			msg.obj = defaultState;
			MainActivity.mHandler.sendMessage(msg);
		} else {
			Intent i = new Intent(getBaseContext(), SelectStateActivity.class);
			MainActivity.this.startActivity(i);
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
			return;
		}
		if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
			enableMyLocation();
		} else {
			mPermissionDenied = true;
			requestMyLocation();
		}
	}
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		if (mPermissionDenied) {
			// Permission was not granted, display error dialog.
			//showMissingPermissionError();
			mPermissionDenied = false;
		}
	}
	private void showMissingPermissionError() {
		PermissionUtils.PermissionDeniedDialog.newInstance(true).show(getSupportFragmentManager(), "dialog");
	}

	@Override
	public boolean onMyLocationButtonClick() {
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
		return false;
	}

	@Override
	public void onMyLocationClick(@NonNull Location l) {
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(l.getLatitude(), l.getLongitude()),DEFAULT_ZOOM));
	}

	// end v2


	@Override
	public void onWindowFocusChanged(boolean b) {
//		 createOverlayWithLazyLoading();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//v1 maps
		//mMyLocationOverlay.enableMyLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//v1 maps
		//mMyLocationOverlay.disableMyLocation();
	}
    
	protected void onDestroy() {
    	super.onDestroy();
        try {
			mPlaybackService.stop();
			mNotificationManager.cancel(1);
			unbindService(mPlayerConnection);
            //Intent svc = new Intent(this, PlaybackService.class);
            //stopService(svc);
        } catch (Exception e) {
        	
        }
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);

		//menu.add(0, 1, 0, "My Location").setIcon(android.R.drawable.ic_menu_mylocation);
		//menu.add(0, 2, 0, "Go to State").setIcon(android.R.drawable.ic_menu_directions);
		//menu.add(0, 3, 0, "Exit").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(Build.VERSION.SDK_INT > 11) {
			invalidateOptionsMenu();
//			if ((FirebaseAuth.getInstance().getCurrentUser() != null)) {
//				menu.findItem(R.id.user_saved_command).setVisible(true);
//				menu.findItem(R.id.user_remove_command).setVisible(true);
				menu.findItem(R.id.action_logout).setVisible(false);
				menu.findItem(R.id.action_login).setVisible(false);
			    menu.findItem(R.id.action_about).setVisible(true);
//			} else {
//				menu.findItem(R.id.action_login).setVisible(true);
//				menu.findItem(R.id.user_saved_command).setVisible(false);
//				menu.findItem(R.id.user_remove_command).setVisible(false);
//				menu.findItem(R.id.action_logout).setVisible(false);
//				menu.findItem(R.id.action_about).setVisible(true);
//			}
		}

		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_goto_state:
				Intent selectStateIntent = new Intent(getBaseContext(), SelectStateActivity.class);
				MainActivity.this.startActivity(selectStateIntent);
				return true;
			case R.id.action_police_code:
				//Intent selectStateIntent = new Intent(getBaseContext(), SelectStateActivity.class);
				Intent policeCodeIntent = new Intent(this, PoliceCodeActivity.class);
				MainActivity.this.startActivity(policeCodeIntent);
				return true;
			case R.id.action_setting:
				Intent i = new Intent(this, SettingsActivity.class);
				MainActivity.this.startActivity(i);
				return true;
			case R.id.action_about:
				com.eggheadgames.aboutbox.activity.AboutActivity.launch(this);
				return true;
			default:
				return super.onOptionsItemSelected(item);

		}
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void createNotificationChannel() {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = "notify";
			String description = "show last running command output";
			int importance = NotificationManager.IMPORTANCE_LOW;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

	public static void loadFeed(String state) {
		try {
			int stid = Constants.stateLookupMap.get(state).getID();
			Feeds feeds;

/*			if (BuildConfig.DEBUG) {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				FeedXMLHandler feedHandler = new FeedXMLHandler();
				xr.setContentHandler(feedHandler);
				InputSource is = new InputSource(App.INSTANCE.getAssets().open("feeds-ca.xml"));
				xr.parse(is);
				feeds = feedHandler.getFeeds();
			} else { */
				RetrieveFeedTask task = new RetrieveFeedTask();
				feeds = task.execute(App.BROADCASTIFY_API_URL + stid).get();
			//}

			// used to find duplicate lat/lon
			coordsList.clear();
			for (Feed f: feeds.Feeds) {
				for (County c: f.Counties) {
					if (!(c.lat.equals("") && c.lon.equals("") && f.descr.equals("") && f.mount.equals("") && f.status.equals("0"))) {
						try {
							LatLng point = new LatLng(Double.parseDouble(c.lat), Double.parseDouble(c.lon));
							Timber.d("Starting tower location: " + f.descr + " (" +  point.latitude + "," + point.longitude + ")");

							// Has this already been plotted? TODO: cluster info windows
							// ... https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering#info-window
							for (LatLng geocoords: coordsList) {
								if (geocoords.equals(point)) {
									Timber.d( "Looks like we already have a tower there, moving it +/- 5 miles.");
									double shiftLat = point.latitude + (((double) (mRandom.nextInt(3) + 1))  *.01);
									//double shiftLat = point.latitude + (((double) (4))  *.01);
									double shiftLon = point.longitude + (((double) (mRandom.nextInt(3) + 1))  *.01);
									point = new LatLng(shiftLat,shiftLon);
									break;
								}
							}

							coordsList.add(point);
							Timber.d("Final tower marker: " + f.descr + "(" +  point.latitude + "," + point.longitude + ")");

							BitmapDescriptor icon;
							if (f.genre.equals("Public Safety")) {
								icon = mMarkerPolice;
							}
							else if (f.genre.equals("Aviation")) {
								icon = mMarkerAirport;
							}
							else {
								icon = mMarkerBroadcast;
							}

							Marker m = mMap.addMarker(new MarkerOptions()
										.position(point)
										.title(f.descr)
										.icon(icon));
							m.setTag(f);

						} catch (Exception e) {
							Timber.e(e);
						}
					}
					else {
						Timber.d("Skipped " + f.descr + " due to empty fields or it was down.");
					}
				}

			} // end for loop for feeds

		} catch (Exception e) {
			Timber.e( "Exception in loading the feeds:" + e.getMessage());
		}

		//return items;
	}



	//todo: restrict to only this app
	public static String getStateUsingGoogle(double latitude, double longitude) {
		String res = DEFAULT_STATE_USA;
		try {
			com.google.maps.model.LatLng loc = new com.google.maps.model.LatLng(latitude, longitude);
			GeoApiContext geoApiContext = new GeoApiContext.Builder().apiKey(App.GOOGLE_MAPS_GEOCODE_API_KEY).build();
			GeocodingResult[] results = GeocodingApi.reverseGeocode(geoApiContext, loc).await();

			for (com.google.maps.model.AddressComponent ac : results[0].addressComponents) {
				com.google.maps.model.AddressComponentType act = ac.types[0];
				if (act.toString().equals("administrative_area_level_1")) {
					return ac.longName;
				}
			}
			//return results[0].formattedAddress + " (" + results[0].geometry.locationType + ")";
		} catch (Exception e) {
			Timber.e(e);
		}

		return res;
	}

	// Somehow less reliable then google maps
	public String getStateUsingGeonames(double lat, double lon) {
		String res = DEFAULT_STATE_USA;
		try {
			String jsonResponse = Util.fetchURL("https://secure.geonames.org/findNearestAddressJSON?lat=" + lat + "&lng=" + lon + "&username=kwatts");
			//String jsonResponse = fetchURL("http://ws.geonames.org/findNearestAddressJSON?lat=" + lat + "&lng=" + lon);
			JSONObject jsonAddressObject = new JSONObject(jsonResponse);
			if (jsonAddressObject.has("address")) {
				JSONObject a = jsonAddressObject.getJSONObject("address");
				if (a.has("adminName1")) {
					return a.getString("adminName1");
				}
			}
		} catch (Exception e) {
			Timber.e(e);
			
		}
		return res;
	}
	
	
	/*
    public static Feeds getFeedsFromUrl(String url) {
      	 try {
      			SAXParserFactory spf = SAXParserFactory.newInstance();
      			SAXParser sp = spf.newSAXParser();
      			XMLReader xr = sp.getXMLReader();
      			URL sourceUrl = new URL(url);
      			FeedXMLHandler feedHandler = new FeedXMLHandler();
      			
      	        xr.setContentHandler(feedHandler);
      			xr.parse(new InputSource(sourceUrl.openStream()));
      			return feedHandler.getFeeds();

              } catch (Exception e) {
             	 Log.e(MSG_TAG, "Got an Exception in trying to fetch the feedFile:");
             	 e.printStackTrace(); 
             	 return null;
              }             
      	
    } */

    public void checkIfFirstTime() {
    	SharedPreferences settings = getSharedPreferences(App.PREFS_NAME, 0);
    	boolean displayWelcome = settings.getBoolean("welcomeScreen", true);
    	//displayWelcome = true;
    	if (displayWelcome) {
	    	Intent welcomeIntent = new Intent(MainActivity.this, WelcomeScreenActivity.class);
	    	MainActivity.this.startActivity(welcomeIntent);
    	}
    	
    	SharedPreferences.Editor editor = settings.edit().putBoolean("welcomeScreen", false);
    	editor.commit();
    	
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Timber.d("onActivityResult called: " + requestCode + " " + resultCode);
    }

    //RetrieveFeedTask task = new RetrieveFeedTask();
	//Feeds f = task.execute(url).get();
	public static class RetrieveFeedTask extends AsyncTask<String, Void, Feeds> {

		private Exception exception;

		protected Feeds doInBackground(String... urls) {
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				URL sourceUrl = new URL(urls[0]);
				FeedXMLHandler feedHandler = new FeedXMLHandler();

				xr.setContentHandler(feedHandler);
				InputSource is = new InputSource(sourceUrl.openStream());
				xr.parse(is);

				return feedHandler.getFeeds();
			} catch (Exception e) {
				this.exception = e;
				return null;
			} finally {
				//is.close();
			}
		}

		protected void onPostExecute(Feeds feeds) {
			super.onPostExecute(feeds);
		}
	}

	/**
	 * Class for interacting with the Player Service.
	 */
	public ServiceConnection mPlayerConnection = new ServiceConnection() {
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

	public void playClickHandler(View v) {
		try {
			if (mPlaybackService.isPlaying()) {
				mPlaybackService.pause();
				mPlayerFragment.startPlayer();
			} else {
				mPlayerFragment.pausePlayer();
				mPlaybackService.play(playerViewModel.getStreamURL());
			}


		} catch (Exception e) {
			Timber.e(e);
		}

	}
	public void closeClickHandler(View v) {
		Timber.d("Closing the stream...");
		try {
			//this.setVisible(false);
			//Message msg_stop = PlayerActivity.mHandler.obtainMessage(PlayerActivity.MSG_STATUS_GREY);
			//msg_stop.obj = "Shutting down stream... ";
			//PlayerActivity.mHandler.sendMessage(msg_stop);
			mPlaybackService.stop();
			//this.finish();
			//mFragmentActivity.unbindService(mPlayerConnection);
			//finish();
		} catch (Exception e) {
			Timber.e(e);
		}
	}

	/**
	 * receive audio session id required for visualizer through
	 * broadcast receiver from service
	 * ref https://stackoverflow.com/a/27652660/5164673
	 */
	// visualizer/tree/master/sample/src/main/java/com/chibde/audiovisualizer/sample
	private BroadcastReceiver bReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int audioSessionId = intent.getIntExtra(PlaybackService.INTENT_AUDIO_SESSION_ID, -1);
			if (audioSessionId != -1) {
				Timber.d("Got the audiosessionid:" +audioSessionId);
				try {
					mLineBarVisualizer.setPlayer(audioSessionId);
				} catch (Exception e) {
					Timber.e(e);
				}
			}
		}
	};



}
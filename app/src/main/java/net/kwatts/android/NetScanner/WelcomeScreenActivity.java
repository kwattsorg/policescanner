package net.kwatts.android.NetScanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import timber.log.Timber;


public class WelcomeScreenActivity extends Activity {

	public static final String MSG_TAG = "NetScanner";

    WebView mWebView;
	    
	
	public void onCreate(Bundle savedInstanceState) {
		 try {	
			super.onCreate(savedInstanceState);

			boolean userIsOnboard = App.mSharedPref.getBoolean("user_is_onboard", false);
			if (userIsOnboard) {
				startMainActivity();
			}
			setContentView(R.layout.welcomescreenview);
			
			ImageView image = (ImageView) findViewById(R.id.welcome_netscanner_logo);		
			image.setImageResource(R.drawable.antenna_blue_48x48);
			
			mWebView = findViewById(R.id.welcome_webview);
            StringBuilder outXML = new StringBuilder();
            outXML.append("<html><head><style type=\"text/css\">");
            outXML.append("body { font-size: 1em; }</style></head><body>");
            outXML.append(App.DESCRIPTION_HTML);
            //outXML.append("Welcome to <b>NetScanner!</b><br><br><b>NetScanner</b> helps you find the scanner sources around you, asking you to enable the location permission. This application will still work without this permission, allowing you to load up scanner sources for a specific area or state. Ads can be turned off anytime in settings.");
			//outXML.append("<br><br>To get started move around the map to lock in the state. All radio sources will be listed. Simply pick one to start the stream.");
			//outXML.append("<br><br>After the stream is started, you can go to your home menu and the stream will run in the background. Also, the player menu has a police code popup that lists all the police codes, which sometimes help for the police scanner sources.");
			//outXML.append("<br><br>If you have any suggestions, need support, or anything else contact us at kwatkins@gmail.com or visit the github site. I promise this app will evolve and continue to get better! Enjoy.<br><br>");
            outXML.append("</body></html>");
	    	mWebView.loadData(outXML.toString(), "text/html", "utf-8");

	    	Button startButton = (Button) findViewById(R.id.startButton);
	    	startButton.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 App.mSharedPref.edit().putBoolean("user_is_onboard", true).commit();
					 startMainActivity();
				 }
			 });
            
			
		 } catch (Exception e) { 
			 Timber.e(e);
		 } 
		
		}
	
    protected void onResume() {
        super.onResume();
    }
    
    protected void onPause() {
        super.onPause();
    }
     
    protected void onStart() {
    	super.onStart();
    }
    protected void onDestroy() {
    	super.onDestroy();
    }

    public void startMainActivity() {
		Intent intent = new Intent(App.INSTANCE, MainActivity.class);
		startActivity(intent);
		this.finish();
	}

    public void startClickHandler(View target) {
    	try {
        	this.finish();
        } catch (Exception e) {
			Timber.e(e);
        }
    }
    
    
    
}
    

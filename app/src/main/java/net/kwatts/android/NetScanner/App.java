package net.kwatts.android.NetScanner;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.eggheadgames.aboutbox.AboutConfig;

import timber.log.Timber;

/**
 * This is a subclass of {@link Application} used to provide shared objects and superuser functionality across the full app
 */
public class App extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static App INSTANCE = null;

    public static final String PREFS_NAME = "NetScannerPrefsFile";
    public static SharedPreferences mSharedPref;
    public static boolean mDisableAds = false;

    public static final String GOOGLE_MAPS_GEOCODE_API_KEY = BuildConfig.GOOGLE_MAPS_GEOCODE_API_KEY;
    public static final String GOOGLE_MAPS_NETSCANNER_API_KEY = BuildConfig.GOOGLE_MAPS_NETSCANNER_API_KEY;
    public static final String BROADCASTIFY_API_KEY = BuildConfig.BROADCASTIFY_API_KEY;
    public static final String BROADCASTIFY_API_URL = "https://api.broadcastify.com/audio/?a=feeds&type=xml&key="
                                                        + App.BROADCASTIFY_API_KEY + "&stid=";
    public static final String ADMOB_ID="ca-app-pub-2189980367471582~1693298075";

    public static final String DESCRIPTION_HTML = "Welcome to <b>NetScanner!</b><br><br><b>NetScanner</b> helps you find the scanner sources around you, asking you to enable the location permission." +
            " This application will still work without this permission, allowing you to load up scanner sources for a specific area or state. Ads can be turned off anytime in settings.<br><br>" +
            "<b> Hints to help guide your journey:</b><br>" +
            "<ul>" +
            "<li>To get started move around the map, looking at streams available around you or in your state.</li>" +
            "<li>Select the name of the stream to start listening.</li>" +
            "<li>You can go to your home menu and the stream will continue to play in the background.</li>" +
            "<li> Ads are included to cover hosting costs, time, etc but feel free to turn them off in settings</li>" +
            "</ul>" +
            "<br>NetScanner is fully open source! <a href=\"https://github.com/kwattsorg/netscanner\">https://github.com/kwattsorg/netscanner</a>" +
            " for feedback, support, or to dust off those coding skills. I promise this app will evolve and continue to get better! Enjoy!";


    public App() {
        INSTANCE = this;
    }

    @Override
    public synchronized void onCreate() {
        super.onCreate();

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mSharedPref.registerOnSharedPreferenceChangeListener(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }
        AboutConfig aboutConfig = AboutConfig.getInstance();
        aboutConfig.appName = getString(R.string.app_name);
        aboutConfig.appIcon = R.mipmap.ic_launcher;
        aboutConfig.version = BuildConfig.VERSION_NAME;
        aboutConfig.aboutLabelTitle = "About App";
        aboutConfig.extraTitle = "Description";
        aboutConfig.extra = DESCRIPTION_HTML;
        aboutConfig.packageName = getApplicationContext().getPackageName();
        aboutConfig.buildType = AboutConfig.BuildType.GOOGLE;
        aboutConfig.facebookUserName = null;
        aboutConfig.twitterUserName = null;
        aboutConfig.webHomePage = "https://github.com/kwattsorg/netscanner";
        aboutConfig.appPublisher = null;
        aboutConfig.companyHtmlPath = null;
        aboutConfig.privacyHtmlPath = "https://github.com/kwattsorg/adbshellkit/wiki/Privacy-Policy";
        aboutConfig.acknowledgmentHtmlPath = null;
        aboutConfig.emailAddress = "kwatkins@gmail.com";
        aboutConfig.emailSubject = "Feedback for " + aboutConfig.packageName;
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Timber.i( "onSharedPreferenceChanged callback");
        switch (key) {
            case "disableAds":
                mDisableAds = sharedPreferences.getBoolean(key, false);
                break;
            default:
                try {
                    //boolean checkState = sharedPreferences.getBoolean(key, false);
                } catch (Exception e) {}

        }

    }

    public static final class ReleaseTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable throwable) {
            switch (priority) {
                case Log.VERBOSE:
                case Log.DEBUG:
                case Log.INFO:
                    break;
                case Log.WARN:
                    //logWarning(priority, tag, message);
                    break;
                case Log.ERROR:
                    //logException(throwable);
                    break;
                default:
                    break;
            }
        }

  /*      private void logWarning(int priority, String tag, String message) {
            Crashlytics.log(priority, tag, message);
        }

        private void logException(final Throwable throwable) {
            Crashlytics.logException(throwable);
        } */
    }

    public void checkIfFirstTime() {
        //Onboarding screen
        boolean displayWelcome =  App.mSharedPref.getBoolean("show_onboarding_screen", true);
        try {
            displayWelcome = true;
            if (displayWelcome) {
                Intent welcomeIntent = new Intent(this, WelcomeScreenActivity.class);
                //welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                this.startActivity(welcomeIntent);
            }
            App.mSharedPref.edit().putBoolean("show_onboarding_screen", false).commit();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
    public static final class DebugTree extends Timber.DebugTree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            // Workaround for devices that doesn't show lower priority logs
            if (Build.MANUFACTURER.equals("HUAWEI") || Build.MANUFACTURER.equals("samsung")) {
                if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO)
                    priority = Log.ERROR;
            }
            super.log(priority, tag, message, t);
        }

        @Override
        protected String createStackElementTag(StackTraceElement element) {
            return String.format("net.kwatts.android.netscanner [C:%s] [M:%s] [L:%s] ",
                    super.createStackElementTag(element),
                    element.getMethodName(),
                    element.getLineNumber());
        }
    }
}
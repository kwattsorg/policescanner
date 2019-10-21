package net.kwatts.android.NetScanner;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import timber.log.Timber;


public class SelectStateActivity extends Activity {
    public static Spinner stateSpinner;
	    
	
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 try {	
			 setContentView(R.layout.statelistview);
			 stateSpinner = (Spinner) findViewById(R.id.StateSpinner);
			 ArrayAdapter<CharSequence> myAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
			 myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			 stateSpinner.setAdapter(myAdapter);
			 

			 for (Object key: Constants.stateLookupMap.keySet()) {
				 //System.out.println(Constants.stateLookupMap.get(key));
				 myAdapter.add((String) key);
				 //arrayList.add((String) key);
			 }

			 //java.util.Arrays.sort
			 
	//		 NetScannerActivity.stateLookupMap.
	//		 ArrayAdapter aAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayState);
			
		 } catch (Exception e) { 
			 Timber.e(e);
		 } 
		
		}
	//Then add a list item click listener.
	//public void onListItemClick(ListView parent, View v, int position, long id) {
	    //Do something here.	
//	}
	
    
	
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
    
    public void goClickHandler(View target) {
        Timber.d( "Going to the state");
    	try {    
    		// get current spinner 
    		String selectedState = (String) SelectStateActivity.stateSpinner.getSelectedItem();
    		Timber.d( "State selected:" + selectedState);

		    // Save chosen state
			App.mSharedPref.edit()
					.putBoolean("has_picked_a_state", true).commit();
			App.mSharedPref.edit()
					.putString("state_selected", selectedState).commit();

			Message msg = MainActivity.mHandler.obtainMessage(MainActivity.GOTOSTATE_MSG);
			msg.obj = selectedState;
			MainActivity.mHandler.sendMessage(msg);
        	this.finish();
        } catch (Exception e) {
			Timber.e(e);
        }
    }
    
    public void cancelClickHandler(View target) {
    	try {           
        	this.finish();
        } catch (Exception e) {
			Timber.e(e);
        }
    }
    
    
    
}
	


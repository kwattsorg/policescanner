package net.kwatts.android.NetScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;



public class Constants {
	
	public static LinkedHashMap<String, StateInfo> stateLookupMap = new LinkedHashMap<String,StateInfo>();
	
//	public static TreeMap<String, StateInfo> stateLookupMap = new TreeMap<String, StateInfo>();
	
	static {
        // Load list of maps
        stateLookupMap.put("Alabama", new StateInfo(1,32.7990,-86.8073));
        stateLookupMap.put("Alaska", new StateInfo(2,61.3850,-152.2683));        
        stateLookupMap.put("Arizona", new StateInfo(4,33.7712,-111.3877));
        stateLookupMap.put("Arkansas", new StateInfo(5,34.9513,-92.3809));
        stateLookupMap.put("California", new StateInfo(6,36.1700,-119.7462));
        stateLookupMap.put("Colorado", new StateInfo(8,39.0646,-105.3272));
        stateLookupMap.put("Connecticut", new StateInfo(9,41.5834,-72.7622));
        stateLookupMap.put("Delaware", new StateInfo(10,39.3498,-75.5148));
        stateLookupMap.put("District of Columbia", new StateInfo(11,38.8964,-77.0262));
        stateLookupMap.put("Florida", new StateInfo(12,27.8333,-81.7170));
        stateLookupMap.put("Georgia", new StateInfo(13,32.9866,-83.6487));
        stateLookupMap.put("Hawaii", new StateInfo(15,21.1098,-157.5311));
        stateLookupMap.put("Idaho", new StateInfo(16,44.2394,-114.5103));
        stateLookupMap.put("Illinois", new StateInfo(17,40.3363,-89.0022));
        stateLookupMap.put("Indiana", new StateInfo(18,39.8647,-86.2604));
        stateLookupMap.put("Iowa", new StateInfo(19,42.0046,-93.2140));
        stateLookupMap.put("Kansas", new StateInfo(20,38.5111,-96.8005));
        stateLookupMap.put("Kentucky", new StateInfo(21,37.6690,-84.6514));
        stateLookupMap.put("Louisiana", new StateInfo(22,31.1801,-91.8749));
        stateLookupMap.put("Maine", new StateInfo(23,44.6074,-69.3977));
        stateLookupMap.put("Maryland", new StateInfo(24,39.0724,-76.7902));
        stateLookupMap.put("Massachusetts", new StateInfo(25,2.2373,-71.5314));
        stateLookupMap.put("Michigan", new StateInfo(26,43.3504,-84.5603));
        stateLookupMap.put("Minnesota", new StateInfo(27,45.7326,-93.9196));
        stateLookupMap.put("Mississippi", new StateInfo(28,32.7673,-89.6812));
        stateLookupMap.put("Missouri", new StateInfo(29,38.4623,-92.3020));
        stateLookupMap.put("Montana", new StateInfo(30,46.9048,-110.3261));
        stateLookupMap.put("Nebraska", new StateInfo(31,41.1289,-98.2883));
        stateLookupMap.put("Nevada", new StateInfo(32,38.4199,-117.1219));
        stateLookupMap.put("New Hampshire", new StateInfo(33,43.4108,-71.5653));
        stateLookupMap.put("New Jersey", new StateInfo(34,40.3140,-74.5089));
        stateLookupMap.put("New Mexico", new StateInfo(35,34.8375,-106.2371));
        stateLookupMap.put("New York", new StateInfo(36,42.1497,-74.9384));
        stateLookupMap.put("North Carolina", new StateInfo(37,35.6411,-79.8431));
        stateLookupMap.put("North Dakota", new StateInfo(38,47.5362,-99.7930));
        stateLookupMap.put("Ohio", new StateInfo(39,40.3736,-82.7755));
        stateLookupMap.put("Oklahoma", new StateInfo(40,35.5376,-96.9247));
        stateLookupMap.put("Oregon", new StateInfo(41,44.5672,-122.1269));
        stateLookupMap.put("Pennsylvania", new StateInfo(42,40.5773,-77.2640));
        stateLookupMap.put("Rhode Island", new StateInfo(44,41.6772,-71.5101));
        stateLookupMap.put("South Carolina", new StateInfo(45,33.8191,-80.9066));
        stateLookupMap.put("South Dakota", new StateInfo(46,44.2853,-99.4632));
        stateLookupMap.put("Tennessee", new StateInfo(47,35.7449,-86.7489));
        stateLookupMap.put("Texas", new StateInfo(48,31.1060,-97.6475));
        stateLookupMap.put("Utah", new StateInfo(49,40.1135,-111.8535));
        stateLookupMap.put("Vermont", new StateInfo(50,44.0407,-72.7093));
        stateLookupMap.put("Virginia", new StateInfo(51,37.7680,-78.2057));
        stateLookupMap.put("Washington", new StateInfo(53,47.3917,-121.5708));
        stateLookupMap.put("West Virginia", new StateInfo(54,38.4680,-80.9696));
        stateLookupMap.put("Wisconsin", new StateInfo(55,44.2563,-89.6385));
        stateLookupMap.put("Wyoming", new StateInfo(56,42.7475,-107.2085));
        stateLookupMap.put("Puerto Rico", new StateInfo(57,18.2766,-66.3350));      
        stateLookupMap.put("Alberta", new StateInfo(101,53.933271,-116.576503));
        stateLookupMap.put("British Columbia", new StateInfo(102,49.286679,-123.019409));
        stateLookupMap.put("Manitoba", new StateInfo(103,49.6467,-96.8115));
        stateLookupMap.put("Nova Scotia", new StateInfo(107,45.170678,-62.731934));
        stateLookupMap.put("Ontario", new StateInfo(109,51.253775,-85.323214));
        stateLookupMap.put("Prince Edward Island", new StateInfo(110,46.341745,-63.388367));
        stateLookupMap.put("Quebec", new StateInfo(111,52.939916,-73.549136));
        stateLookupMap.put("Saskatchewan", new StateInfo(112,52.939916,-106.450864));  
        stateLookupMap.put("England", new StateInfo(601,52.019029,-0.770427));
        stateLookupMap.put("London", new StateInfo(601,52.019029,-0.770427));
        stateLookupMap.put("Greater London", new StateInfo(601,52.019029,-0.770427));
        stateLookupMap.put("Hertfordshire", new StateInfo(601,52.019029,-0.770427));
        stateLookupMap.put("Cambridgeshire", new StateInfo(601,52.019029,-0.770427));
	}
	
	
	public static class StateInfo {
		public int id;
		double longitude;
		double latitude;

		public StateInfo(int id, double latitude, double longitude) {
			this.id = id;
			this.longitude = longitude;
			this.latitude = latitude;
		}
		
		public int getID() {
			return id;
		}
		
	}

}




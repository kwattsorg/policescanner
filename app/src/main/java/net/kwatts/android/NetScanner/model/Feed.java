package net.kwatts.android.NetScanner.model;

import java.util.ArrayList;


/* 
 * <feed id="33" status="0" listeners="0" descr="Arkansas and San Luis Valleys Sheriff, Fire and EMS" genre="Public Safety" mount="/chaffee" bitrate="0">
 * <counties><county ctid="262" name="Fremont" type="County" stid="8" stateCode="CO" stateName="Colorado" countryName="United States" countryCode="US" coid="1" countyDetails="" lat="38.475094" lon="-105.479736"/>
 * <county ctid="248" name="Chaffee" type="County" stid="8" stateCode="CO" stateName="Colorado" countryName="United States" countryCode="US" coid="1" countyDetails="" lat="38.777640" lon="-106.185608"/><county ctid="295" name="Saguache" type="County" stid="8" stateCode="CO" stateName="Colorado" countryName="United States" countryCode="US" coid="1" countyDetails="" lat="38.0833337" lon="-106.2511384"/>
 * <county ctid="254" name="Custer" type="County" stid="8" stateCode="CO" stateName="Colorado" countryName="United States" countryCode="US" coid="1" countyDetails="" lat="38.110789" lon="-105.369873"/>
 * </counties>
 * <relays>
 * <relay host="relay.radioreference.com" port="80"/>
 * </relays>
 * </feed>
 */

public class Feed {
	public String id = "";
	public String status = "";
	public String listeners = "";
	public String descr = "";
	public String genre = "";
	public String mount = "";
	public String bitrate = "";
	
    public ArrayList<County> Counties = new ArrayList<County>();
	public ArrayList<Relay> Relays = new ArrayList<Relay>();
    
	public ArrayList<County> getCounties() {
		return Counties;
	}
	public ArrayList<Relay> getRelays() {
		return Relays;
	}

	
	public void setCounty(County county) {
		this.Counties.add(county);
	}
	public void setRelay(Relay relay) {
		this.Relays.add(relay);
	}
	
	
}

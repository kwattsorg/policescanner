package net.kwatts.android.NetScanner;

import net.kwatts.android.NetScanner.model.County;
import net.kwatts.android.NetScanner.model.Feed;
import net.kwatts.android.NetScanner.model.Feeds;
import net.kwatts.android.NetScanner.model.Relay;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FeedXMLHandler extends DefaultHandler {

	Boolean currentElement = false;
	String currentValue = null;
	public Feeds feeds = new Feeds();
	
	public Feed currentFeed;
	public County currentCounty;
	public Relay currentRelay;

	public Feeds getFeeds() {
		return feeds;
	}



	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		currentElement = true;

		if (localName.equals("feed"))
		{
				currentFeed = new Feed();
                currentFeed.id = attributes.getValue("id");
                currentFeed.status = attributes.getValue("status");
                currentFeed.listeners = attributes.getValue("listeners");
                currentFeed.descr = attributes.getValue("descr");
                currentFeed.genre = attributes.getValue("genre");
                currentFeed.mount = attributes.getValue("mount");
                currentFeed.bitrate = attributes.getValue("bitrate");				
		} else if (localName.equals("county")) {
				currentCounty = new County();
				currentCounty.coid = attributes.getValue("coid");
				currentCounty.ctid = attributes.getValue("ctid");
				currentCounty.name = attributes.getValue("name");
				currentCounty.type = attributes.getValue("type");
				currentCounty.stid = attributes.getValue("stid");
				currentCounty.stateCode = attributes.getValue("stateCode");
				currentCounty.stateName = attributes.getValue("stateName");
				currentCounty.countryName = attributes.getValue("countryName");
				currentCounty.countryCode = attributes.getValue("countryCode");
				currentCounty.coid = attributes.getValue("coid");
				currentCounty.countyDetails = attributes.getValue("countyDetails");
				currentCounty.lat = attributes.getValue("lat");
				currentCounty.lon = attributes.getValue("lon");  
		} else if (localName.equals("relay")) {
				currentRelay = new Relay();
				currentRelay.host = attributes.getValue("host");
				currentRelay.port = attributes.getValue("port");				
		}

	}

	/** Called when tag closing ( ex:- <name>AndroidPeople</name>
	 * -- </name> )*/
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {



		/** set value */
		if (localName.equals("feed")) {
			feeds.setFeed(currentFeed);
		}
		if (localName.equals("county")) {
			currentFeed.setCounty(currentCounty);		
		}
		if (localName.equals("relay")) {
			currentFeed.setRelay(currentRelay);
		}

	}

	/** Called to get tag characters ( ex:- <name>AndroidPeople</name>
	 * -- to get AndroidPeople Character ) */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {


	}

}


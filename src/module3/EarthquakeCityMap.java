package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Vishal Aslot
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	public final int YELLOW = color(255,255,0);
	public final int RED = color(255,0,0);
	public final int BLUE = color(0,0,255);
	
	public final float YELLOW_RADIUS = 10.0f;
	public final float RED_RADIUS = 7.0f;
	public final float BLUE_RADIUS = 4.0f;
	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			// map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Microsoft.AerialProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    for (PointFeature f: earthquakes) {
	    		SimplePointMarker mk = new SimplePointMarker(f.getLocation(), f.getProperties());
	    		float mag = Float.parseFloat(f.getProperty("magnitude").toString());
	    		if (mag < THRESHOLD_LIGHT) {
	    			mk.setRadius(BLUE_RADIUS);
	    			mk.setColor(BLUE);
	    		}
	    		else if (mag >= THRESHOLD_LIGHT && mag <= THRESHOLD_MODERATE) {
	    			mk.setRadius(RED_RADIUS);
	    			mk.setColor(RED);
	    		}
	    		else {
	    			mk.setRadius(YELLOW_RADIUS);
	    			mk.setColor(YELLOW);
	    		}
	    		markers.add(mk);
	    }
	    map.addMarkers(markers);
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	private void addKey() 
	{	
		// Remember you can use Processing's graphics methods here
		fill(255); rect(25, 50, 150, 100);
		
		fill(YELLOW); ellipse(40, 75, YELLOW_RADIUS, YELLOW_RADIUS);
		fill(RED); ellipse(40, 105, RED_RADIUS, RED_RADIUS);
		fill(BLUE); ellipse(40, 135, BLUE_RADIUS, BLUE_RADIUS);
		
		fill(0);
		text("5.0+ Magnitude", 50, 80);
		text("4.0+ Magnitude", 50, 110);
		text("Below 4.0", 50, 140);
	
	}
}

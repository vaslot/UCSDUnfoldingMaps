package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.Microsoft;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	private static final long serialVersionUID = 1L;
	private UnfoldingMap map;
	private List<Marker> airportList;
	private List<Marker> routeList; 
	
	public void setup() {
		// setting up PAppler
		size(800,900, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 25, 25, 750, 850, new Microsoft.RoadProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		List<Location> indianAirportList = new ArrayList<Location>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		int maxAirports = 100;
		
		// create markers from features
		for(PointFeature feature : features) {
			
			if (maxAirports == 0)
				break;
			
			// Only add Indian airports
			if ("\"India\"".equals(feature.getStringProperty("country"))) {
				AirportMarker m = new AirportMarker(feature);
	
				m.setRadius(15);
				m.setId(feature.getId());
				airportList.add(m);
				indianAirportList.add(feature.getLocation());
			
				// put airport in hashmap with OpenFlights unique id for key
				airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
				maxAirports--;
			}
		}
		// parse route data and all routes within India
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			// this will only add routes connecting Indian cities
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());

		    routeList.add(sl);
		}
		map.zoomAndPanToFit(indianAirportList);
		map.addMarkers(airportList);
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	@Override
	public void mouseClicked()
	{
		for (Marker mk : airportList) {
			if (mk.isInside(map, mouseX, mouseY)) {
				System.out.println(mk.getProperty("city") + " : " + mk.getId());
				// show all routes out of this airport
				for (Marker rmk : routeList) {
					if (rmk.getProperty("source").equals(mk.getId())) {
						System.out.println(rmk.getProperties());
					    map.addMarker(rmk);
					}
				}
			}
		}
	}
}

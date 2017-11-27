package Backend.DB;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.postgresql.util.PSQLException;

public class QueryManagement {

	private Connector connector;

	private Statement statement;

	public QueryManagement() {
		try {
			connector = new Connector();
			statement = connector.getStatement();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Hlavna metoda na vratenie vsetkych parkov. 
	 * */
	public List<JSONObject> getAll() throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();

		ResultSet result1 = statement.executeQuery(
				"SELECT plg.name, ST_Area(ST_Transform(plg.way,4326)::geography) AS area, ST_AsGeoJSON(ST_Transform(plg.way, 4326)) AS result FROM planet_osm_polygon AS plg where plg.leisure in ('park','recreation_ground') GROUP BY plg.name,plg.way ");
		
		while (result1.next()) {

			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result1.getString("result")));

			JSONObject features = new JSONObject();
			features.put("title", result1.getString("name"));
			
			
			double value = Double.parseDouble(result1.getString("area").toString());
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(RoundingMode.CEILING);
			features.put("area", df.format(value));
			
			features.put("fill", "#009900");
			features.put("stroke-width", "2");
			features.put("fill-opacity", 0.6);

			json.put("properties", features);
			geoJsons.add(json);
		}


		return geoJsons;
	}
	/*
	 * Metoda zobrazi pocet historickych pamiatok v danom parku.
	 * */
	public List<JSONObject> getHistoric() throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();
		
		
		ResultSet result1 = statement.executeQuery(
				"SELECT plg.name, count(*) AS parkcount, ST_AsGeoJSON(ST_Transform(plg.way, 4326)) AS result FROM planet_osm_point AS pnt CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, pnt.way) = 't' AND plg.leisure in ('park','recreation_ground') AND (pnt.tourism='artwork' OR pnt.historic='memorial') GROUP BY plg.name, plg.way "
						+ "UNION "
						+ "SELECT pnt.name, count(*) AS historiccount, ST_AsGeoJSON(ST_Transform(pnt.way, 4326)) AS result FROM planet_osm_point AS pnt CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, pnt.way) = 't' AND plg.leisure in ('park','recreation_ground') AND (pnt.tourism='artwork' OR pnt.historic='memorial') GROUP BY pnt.name, pnt.way");

		while (result1.next()) {
			
			int r=0, g=100, b=0;
			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result1.getString("result")));

			JSONObject features = new JSONObject();
			features.put("title", result1.getString("name"));
			features.put("count", result1.getString("parkcount"));
			int poc = Integer.parseInt(result1.getString("parkcount"));
			
			for(int i=1; i<poc; i++){
				
				g *= 1.25;
				if(g >= 200) break;
			}
			
			String hex = String.format("#%02x%02x%02x", r, g, b);
			
			features.put("fill", hex);
			features.put("stroke-width", "2");
			features.put("fill-opacity", 0.6);

			features.put("marker-color", "#ffcc00");
			features.put("marker-size", "small");
			features.put("marker-symbol", "star-stroked");
			
			json.put("properties", features);
			geoJsons.add(json);
		}

		return geoJsons;
	}
	
		
	/**
	 * Metoda zobrazi vyklikané elementy podla checkboxov 
	 * @param elements 
	 */	
	//TODO: vracat rozlohu parku a pocet jednotlivych elementov
	public List<JSONObject> getSearch(List<String> elements) throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();
		
		if(elements.isEmpty()) {
			return null;
		}
		String qrStart1 ="SELECT ";
		String qrStart = "plg.name, ST_AsGeoJSON(ST_Transform(plg.way, 4326)) AS result, ST_Area(ST_Transform(plg.way,4326)::geography) AS area FROM planet_osm_point AS pnt CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, pnt.way) = 't' AND plg.leisure in ('park','recreation_ground') AND ";
		String connector = "INTERSECT ";
		String qrEnd = "GROUP BY plg.name, plg.way ";
		String qr = "";
		int counter = 0;
		for(String element : elements){
			if(counter == 0) qr = qr.concat(qrStart1);
			else{
				qr = qr.concat(connector);
				qr = qr.concat(qrStart1);
			}
			
			
			if(element.equals("fountain")){
				//qr = qr.concat("count(*) AS fountcount,");
				qr = qr.concat(qrStart);
				qr = qr.concat(" pnt.amenity = 'fountain' " + qrEnd);
				
			}
			
			if(element.equals("bench")){
				//qr = qr.concat("count(*) AS bechcount,");
				qr = qr.concat(qrStart);
				qr = qr.concat(" pnt.amenity = 'bench' " + qrEnd);
				
			}
			
			if(element.equals("playground")){
				//qr = qr.concat("count(*) AS playcount,");
				qr = qr.concat(qrStart);
				qr = qr.concat(" pnt.leisure = 'playground' " + qrEnd);
				
			}
			counter++;
		}
				
		ResultSet result1 = statement.executeQuery(qr);
		
		
		while (result1.next()) {
			//System.out.println("--------------------------------------------");
			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result1.getString("result")));

			JSONObject features = new JSONObject();
			features.put("title", result1.getString("name"));
			
		/*	try {
				System.out.println("fount " + result1.getString("fountcount"));
			} catch (Exception e) {
				
			}
			
			try {
				System.out.println("bench " + result1.getString("bechcount"));
			} catch (Exception ee) {
				
			}
			
			try {
				System.out.println("play " + result1.getString("playcount"));
			} catch (Exception eee) {
				
			}*/
			
			double value = Double.parseDouble(result1.getString("area").toString());
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(RoundingMode.CEILING);
			features.put("area", df.format(value));
			
			features.put("fill", "#009900");
			features.put("stroke-width", "2");
			features.put("fill-opacity", 0.6);

			features.put("marker-color", "#0066ff");
			features.put("marker-size", "large");
			features.put("marker-symbol", "art-gallery");
			
			json.put("properties", features);
			geoJsons.add(json);
		}
		
		
		return geoJsons;
	}
	
	/*
	 * Metoda zobrazi najblizsie parky k aktualnej pozici markeru
	 * */
	public List<JSONObject> getNear(double n, double e, int limit) throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();
		//pridat rozlohu
		String queryText = "SELECT ST_Distance(ST_GeomFromText('POINT(" + e + " " + n + ")', 4326)::geography, ST_Transform(way, 4326)::geography) AS distance, ST_AsGeoJSON(ST_Transform(way, 4326)) AS result, ST_Area(ST_Transform(way,4326)::geography) AS area, name FROM planet_osm_polygon where leisure in ('park','recreation_ground') ORDER BY distance LIMIT " + limit;
		ResultSet result = statement.executeQuery(queryText);
		while(result.next()) {
			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result.getString("result")));
			JSONObject features = new JSONObject();
			features.put("title", result.getString("name"));
			
			double value = Double.parseDouble(result.getString("area").toString());
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(RoundingMode.CEILING);
			features.put("area", df.format(value));
			
			value = Double.parseDouble(result.getString("distance").toString());
			df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.CEILING);
			features.put("distance", df.format(value));
			
			features.put("fill", "#009900");
			features.put("stroke-width", "2");
			features.put("fill-opacity", 0.6);
			
			json.put("properties", features);
			geoJsons.add(json);
		}
		
		return geoJsons;
	}
	/*
	 * Metoda zobrazi najblizsie MHD a Parkoviska
	 * */
	public List<JSONObject> getTransport(double n, double e, int limit) throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();
	
		String queryText = "SELECT ST_Distance(ST_GeomFromText('POINT(" + e + " " + n + ")', 4326)::geography, ST_Transform(way, 4326)::geography) AS distance, ST_AsGeoJSON(ST_Transform(pnt.way, 4326)) AS result, name, amenity, railway, highway FROM planet_osm_point as pnt where ((pnt.amenity='parking') OR (pnt.railway='tram_stop') OR (pnt.highway='bus_stop')) ORDER BY distance LIMIT " + limit;
		ResultSet result = statement.executeQuery(queryText);
		while(result.next()) {
			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result.getString("result")));
			JSONObject features = new JSONObject();
			features.put("title", result.getString("name"));
			features.put("distance", result.getString("distance"));
			
			
			try {
				if(result.getString("amenity").equals("parking")){
					
					features.put("marker-color", "#0066ff");
					features.put("marker-size", "medium");
					features.put("marker-symbol", "parking");
				
				}
			} catch (Exception e1) {
				
			} 
			
			try {
				if(result.getString("railway").equals("tram_stop")){
					
					features.put("marker-color", "#66ccff");
					features.put("marker-size", "medium");
					features.put("marker-symbol", "rail");
				
				}
			} catch (Exception e1) {
				
			} 
			
			try {
				if(result.getString("highway").equals("bus_stop")){
					
					features.put("marker-color", "#66ccff");
					features.put("marker-size", "medium");
					features.put("marker-symbol", "bus");
				
				}
			} catch (Exception e1) {
				
			}
			
			
			
			json.put("properties", features);
			geoJsons.add(json);
		}
		
		 queryText = "SELECT plg.name, ST_AsGeoJSON(ST_Transform(plg.way, 4326)) AS result, ST_Distance(ST_GeomFromText('POINT(" + e + " " + n + ")', 4326)::geography, ST_Transform(way, 4326)::geography) AS distance FROM planet_osm_polygon AS plg where ST_CONTAINS(ST_Transform(plg.way, 4326), ST_GeomFromText('POINT(" + e + " " + n + ")', 4326)) = 't' AND plg.leisure in ('park','recreation_ground') GROUP BY plg.name, plg.way ";
		 result = statement.executeQuery(queryText);
		
		while(result.next()) {
			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result.getString("result")));
			JSONObject features = new JSONObject();
			features.put("title", result.getString("name"));
			features.put("distance", result.getString("distance"));
			
			features.put("fill", "#00cc00");
			features.put("stroke-width", "2");
			features.put("fill-opacity", 0.6);
			
			json.put("properties", features);
			geoJsons.add(json);
		}
		
		 queryText = "SELECT ST_Distance(ST_GeomFromText('POINT(" + e + " " + n + ")', 4326)::geography, ST_Transform(way, 4326)::geography) AS distance, ST_AsGeoJSON(ST_Transform(way, 4326)) AS result, name FROM planet_osm_point as pnt where (pnt.tourism='artwork' OR pnt.historic='memorial') ORDER BY distance LIMIT " + limit;
		 result = statement.executeQuery(queryText);
		while(result.next()) {
			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result.getString("result")));
			JSONObject features = new JSONObject();
			features.put("title", result.getString("name"));
			features.put("distance", result.getString("distance"));
			
			features.put("marker-color", "#c68c53");
			features.put("marker-size", "medium");
			features.put("marker-symbol", "star");
			
			json.put("properties", features);
			geoJsons.add(json);
		}
		
		
		return geoJsons;
	}
	
	/*
	 * Metoda vrati kumulovanu dlzku chodnikov v jednotlivých parkoch
	 * */
	public List<JSONObject> getLength() throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();
		//SELECT ln.surface, plg.name as name, ST_AsGeoJSON(ST_Transform(plg.way, 4326)) AS result, ST_AsGeoJSON(ST_Transform(ln.way, 4326)) AS result1, SUM(ST_Length(ST_Intersection(ln.way,plg.way))) as length FROM planet_osm_line AS ln CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, ln.way) = 't' AND plg.leisure in ('park','recreation_ground') AND (ln.highway='footway') GROUP BY plg.way, plg.name, ln.way, ln.surface
		
		String queryText = "SELECT plg.name as name, ST_AsGeoJSON(ST_Transform(plg.way, 4326)) AS result, SUM(ST_Length(ST_Intersection(ln.way,plg.way))) as length FROM planet_osm_line AS ln CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, ln.way) = 't' AND plg.leisure in ('park','recreation_ground') AND (ln.highway='footway' OR ln.highway='path') GROUP BY plg.way, plg.name"
							+ " UNION "
							+ "SELECT ln.surface, ST_AsGeoJSON(ST_Transform(ln.way, 4326)) AS result, SUM(ST_Length(ST_Intersection(ln.way,plg.way))) as length FROM planet_osm_line AS ln CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, ln.way) = 't' AND plg.leisure in ('park','recreation_ground') AND (ln.highway='footway' OR ln.highway='path') GROUP BY  ln.way, ln.surface";
		
		
		ResultSet result = statement.executeQuery(queryText);
		while(result.next()) {
			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result.getString("result")));
			//System.out.println("cesta " + result.getString("length"));
			
			JSONObject features = new JSONObject();
			features.put("title", result.getString("name"));
			features.put("length", result.getString("length"));
			
			features.put("fill", "#009900");
			features.put("stroke-width", "2");
			features.put("fill-opacity", 0.6);
			
			features.put("stroke", "#2f33fa");
			json.put("properties", features);
			geoJsons.add(json);
			
						
		}
		
		return geoJsons;
	}
	
	
	
	
	
	public List<JSONObject> getHistoricbeta() throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();

		ResultSet result = statement.executeQuery(
				"SELECT ST_AsGeoJSON(ST_Transform(way, 4326)) AS result, name FROM planet_osm_point where tourism='artwork' OR historic='memorial'");
		while (result.next()) {

			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result.getString("result")));
			JSONObject features = new JSONObject();
			features.put("title", result.getString("name"));
			
			
			features.put("fill", "#009900");
			features.put("stroke-width", "2");
			features.put("fill-opacity", 0.6);
			json.put("properties", features);
			geoJsons.add(json);
		}

		return geoJsons;
	}
	
	
	
	
	public List<JSONObject> getAllbeta() throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();

		/*
		 * ResultSet result = statement.executeQuery(
		 * "SELECT plg.name, ST_AsGeoJSON(ST_Transform(plg.way, 4326)) AS result FROM planet_osm_point AS pnt CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, pnt.way) = 't' AND plg.leisure in ('park','recreation_ground') AND pnt.amenity='fountain' GROUP BY plg.name, plg.way "
		 * + "UNION " +
		 * "SELECT pnt.name, ST_AsGeoJSON(ST_Transform(pnt.way, 4326)) AS result FROM planet_osm_point AS pnt CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, pnt.way) = 't' AND plg.leisure in ('park','recreation_ground') AND pnt.amenity='fountain' GROUP BY pnt.name, pnt.way"
		 * );
		 */

		ResultSet result1 = statement.executeQuery(
				"SELECT plg.name, ST_AsGeoJSON(ST_Transform(plg.way, 4326)) AS result FROM planet_osm_point AS pnt CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, pnt.way) = 't' AND plg.leisure in ('park','recreation_ground') AND pnt.amenity='fountain' GROUP BY plg.name, plg.way "
						+ "UNION "
						+ "SELECT pnt.name, ST_AsGeoJSON(ST_Transform(pnt.way, 4326)) AS result FROM planet_osm_point AS pnt CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, pnt.way) = 't' AND plg.leisure in ('park','recreation_ground') AND pnt.amenity='fountain' GROUP BY pnt.name, pnt.way");

		while (result1.next()) {

			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result1.getString("result")));

			JSONObject features = new JSONObject();
			features.put("title", result1.getString("name"));

			features.put("fill", "#009900");
			features.put("stroke-width", "2");
			features.put("fill-opacity", 0.6);

			features.put("marker-color", "#0066ff");
			features.put("marker-size", "large");
			features.put("marker-symbol", "water");

			json.put("properties", features);
			geoJsons.add(json);
		}

		ResultSet result = statement.executeQuery(
				"SELECT plg.name,plg.leisure, ST_X(st_centroid(ST_Transform(plg.way, 4326))) AS x, ST_Y(st_centroid(ST_Transform(plg.way, 4326))) AS y, ST_AsGeoJSON(ST_Transform(plg.way, 4326)) AS result FROM planet_osm_polygon AS plg1 CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, plg1.way) = 't' AND plg.leisure in ('park','recreation_ground') AND plg1.amenity='fountain' GROUP BY plg.name,plg.leisure, plg.way "
						+ "UNION "
						+ "SELECT plg1.name, plg1.amenity, ST_X(st_centroid(ST_Transform(plg1.way, 4326))) AS x1, ST_Y(st_centroid(ST_Transform(plg1.way, 4326))) AS y1, ST_AsGeoJSON(ST_Transform(plg1.way, 4326)) AS result FROM planet_osm_polygon AS plg1 CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, plg1.way) = 't' AND plg.leisure in ('park','recreation_ground') AND plg1.amenity='fountain' GROUP BY plg1.name,plg1.amenity, plg1.way");

		while (result.next()) {

			JSONObject json = new JSONObject();
			json.put("type", "Feature");

			JSONObject features = new JSONObject();

			if (result.getString("leisure").equals("park")) {
				json.put("geometry", new JSONObject(result.getString("result")));

				features.put("title", result.getString("name"));
				features.put("fill", "#009900");
				features.put("stroke-width", "2");
				features.put("fill-opacity", 0.6);

			}

			if (result.getString("leisure").equals("fountain")) {

				JSONObject coord = new JSONObject();
				coord.put("type", "Point");

				ArrayList<Double> listOfCoord = new ArrayList<>();
				listOfCoord.add(Double.parseDouble(result.getString("x")));
				listOfCoord.add(Double.parseDouble(result.getString("y")));
				coord.put("coordinates", listOfCoord);
				json.put("geometry", coord);
				features.put("title", result.getString("name"));
				features.put("fill", "#0066ff");
				features.put("stroke-width", "1");
				features.put("fill-opacity", 0.8);

				features.put("marker-color", "#0066ff");
				features.put("marker-size", "large");
				features.put("marker-symbol", "water");
			}

			json.put("properties", features);
			geoJsons.add(json);
		}

		return geoJsons;
	}

	public List<JSONObject> getnadrz() throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();

		ResultSet result = statement.executeQuery(
				"SELECT ST_AsGeoJSON(ST_Transform(way,4326)) AS result, name FROM planet_osm_polygon where amenity='fountain' UNION SELECT ST_AsGeoJSON(ST_Transform(way,4326)) AS result, name FROM planet_osm_point where amenity='fountain'");
		while (result.next()) {
			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result.getString("result")));
			JSONObject features = new JSONObject();
			features.put("title", result.getString("name"));
			features.put("marker-color", "#0066ff");
			features.put("marker-size", "large");
			features.put("marker-symbol", "water");
			features.put("fill", "#0066ff");
			features.put("stroke-width", "1");
			features.put("fill-opacity", 0.6);
			json.put("properties", features);
			geoJsons.add(json);
		}

		return geoJsons;
	}

	public List<JSONObject> getStatue() throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();

		ResultSet result = statement.executeQuery(
				"SELECT ST_AsGeoJSON(ST_Transform(way, 4326)) AS result, name FROM planet_osm_point where tourism='artwork' OR historic='memorial'");
		while (result.next()) {

			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result.getString("result")));
			JSONObject features = new JSONObject();
			features.put("title", result.getString("name"));
			features.put("marker-color", "#c68c53");
			features.put("marker-size", "large");
			features.put("marker-symbol", "art-gallery");
			json.put("properties", features);
			geoJsons.add(json);
		}

		return geoJsons;
	}

	public List<JSONObject> getPlayground() throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();

		ResultSet result = statement.executeQuery(
				"SELECT plg.name, ST_AsGeoJSON(ST_Transform(plg.way, 4326)) AS result FROM planet_osm_point AS pnt CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, pnt.way) = 't' AND plg.leisure in ('park','recreation_ground') AND pnt.leisure='playground' GROUP BY plg.name, plg.way "
						+ "UNION "
						+ "SELECT pnt.name, ST_AsGeoJSON(ST_Transform(pnt.way, 4326)) AS result FROM planet_osm_point AS pnt CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, pnt.way) = 't' AND plg.leisure in ('park','recreation_ground') AND pnt.leisure='playground' GROUP BY pnt.name, pnt.way");

		
		/*ResultSet result = statement.executeQuery(
				"SELECT ST_AsGeoJSON(ST_Transform(way, 4326)) AS result, name FROM planet_osm_point where leisure='playground' UNION SELECT ST_AsGeoJSON(ST_Transform(way, 4326)) AS result, name FROM planet_osm_polygon where leisure='playground'");
		*/
		while (result.next()) {

			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result.getString("result")));
			JSONObject features = new JSONObject();
			features.put("title", result.getString("name"));
			features.put("marker-color", "#ffa64d");
			features.put("marker-size", "large");
			features.put("marker-symbol", "playground");
			features.put("fill", "#ffa64d");
			features.put("stroke-width", "1");
			features.put("fill-opacity", 0.6);
			json.put("properties", features);
			geoJsons.add(json);
		}

		return geoJsons;
	}

	public List<JSONObject> getBench() throws SQLException {
		List<JSONObject> geoJsons = new ArrayList<>();

		ResultSet result = statement.executeQuery(
				"SELECT plg.name, ST_AsGeoJSON(ST_Transform(plg.way, 4326)) AS result FROM planet_osm_point AS pnt CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, pnt.way) = 't' AND plg.leisure in ('park','recreation_ground') AND pnt.amenity='bench' GROUP BY plg.name, plg.way "
						+ "UNION "
						+ "SELECT pnt.name, ST_AsGeoJSON(ST_Transform(pnt.way, 4326)) AS result FROM planet_osm_point AS pnt CROSS JOIN planet_osm_polygon AS plg where ST_CONTAINS(plg.way, pnt.way) = 't' AND plg.leisure in ('park','recreation_ground') AND pnt.amenity='bench' GROUP BY pnt.name, pnt.way");

		/*ResultSet result1 = statement.executeQuery(
				"SELECT ST_AsGeoJSON(ST_Transform(way, 4326)) AS result, name FROM planet_osm_point where amenity='bench'");*/
		while (result.next()) {

			JSONObject json = new JSONObject();
			json.put("type", "Feature");
			json.put("geometry", new JSONObject(result.getString("result")));
			JSONObject features = new JSONObject();
			features.put("title", result.getString("name"));
			features.put("marker-color", "#ffa64d");
			features.put("marker-size", "small");
			features.put("marker-symbol", "square-stroked");
			features.put("fill", "#ffa64d");
			features.put("stroke-width", "1");
			features.put("fill-opacity", 0.6);
			json.put("properties", features);
			geoJsons.add(json);
		}

		return geoJsons;
	}

}

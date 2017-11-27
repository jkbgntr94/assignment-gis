package Backend.pdtProject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("myresource")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Hello, Heroku MAIN!";
    }
    
private Backend.DB.QueryManagement manager = new Backend.DB.QueryManagement();
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAll() {
		JSONArray array = new JSONArray();
		
		try {
			List<JSONObject> result = manager.getAll();
			for(JSONObject json : result) {
				array.put(json);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		return array.toString();
	}
	
	
	@GET
	@Path("/historic")
	@Produces(MediaType.APPLICATION_JSON)
	public String getHistoric() {
		JSONArray array = new JSONArray();
		
		try {
			List<JSONObject> result = manager.getHistoric();
			for(JSONObject json : result) {
				array.put(json);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		return array.toString();
	}
	
	
	@GET
	@Path("/search/{ben}/{fount}/{play}/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getByBrands(@PathParam("ben") boolean cb1, @PathParam("fount") boolean cb2, @PathParam("play") boolean cb3) {
		JSONArray array = new JSONArray();
		
		List<String> elements = new ArrayList<>();
		if(cb1) elements.add("bench");
		if(cb2) elements.add("fountain");
		if(cb3) elements.add("playground");
		
		try {
			List<JSONObject> result = manager.getSearch(elements);
			for(JSONObject json : result) {
				array.put(json);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		return array.toString();
	}
	
	@GET
	@Path("/near/{n}/{e}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNearest(@PathParam("n") double n, @PathParam("e") double e, @PathParam("limit") int limit) {
		JSONArray array = new JSONArray();
		
		try {
			List<JSONObject> result = manager.getNear(n, e, limit);
			for(JSONObject json : result) {
				array.put(json);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		return array.toString();
	}
	
	@GET
	@Path("/transport/{n}/{e}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTransport(@PathParam("n") double n, @PathParam("e") double e, @PathParam("limit") int limit) {
		JSONArray array = new JSONArray();
		
		try {
			List<JSONObject> result = manager.getTransport(n, e, limit);
			for(JSONObject json : result) {
				array.put(json);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		return array.toString();
	}
	
	@GET
	@Path("/pathlength")
	@Produces(MediaType.APPLICATION_JSON)
	public String getLength() {
		JSONArray array = new JSONArray();
		
		try {
			List<JSONObject> result = manager.getLength();
			for(JSONObject json : result) {
				array.put(json);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		return array.toString();
	}
	
	
	
	@GET
	@Path("/fountain")
	@Produces(MediaType.APPLICATION_JSON)
	public String getnadrz() {
		JSONArray array = new JSONArray();
		
		try {
			List<JSONObject> result = manager.getnadrz();
			for(JSONObject json : result) {
				array.put(json);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		return array.toString();
	}
	
	@GET
	@Path("/statue")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStatue() {
		JSONArray array = new JSONArray();
		
		try {
			List<JSONObject> result = manager.getStatue();
			for(JSONObject json : result) {
				array.put(json);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		return array.toString();
	}

	@GET
	@Path("/playground")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPlayground() {
		JSONArray array = new JSONArray();
		
		try {
			List<JSONObject> result = manager.getPlayground();
			for(JSONObject json : result) {
				array.put(json);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		return array.toString();
	}
	
	@GET
	@Path("/bench")
	@Produces(MediaType.APPLICATION_JSON)
	public String getBench() {
		JSONArray array = new JSONArray();
		
		try {
			List<JSONObject> result = manager.getBench();
			for(JSONObject json : result) {
				array.put(json);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		return array.toString();
	}
		
}

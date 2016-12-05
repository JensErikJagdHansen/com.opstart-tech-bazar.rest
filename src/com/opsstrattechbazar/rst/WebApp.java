package com.opsstrattechbazar.rst;

// Version 2016.12.05 14.30

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import org.json.JSONArray;
import static java.util.concurrent.TimeUnit.*;


@Path("/v1") 
public class WebApp {
	
	//Connection version 
	public static final Integer intPrint_JSON = 0 ; //Set =0 if not print JSON, =1 if print JSON

	public static final String dbURL = "jdbc:sqlserver://localhost:49170;databaseName=opstarttechbazar;user=flowline;password=123";
	//Connection version 
	public static final String strVersion = "2.0.4.16d";

	
	
	private static final JSONHelper JSONHelper = new JSONHelper(dbURL,intPrint_JSON);
	
	private static final String strSQL_insert_beacon_log =  "Insert Into beacon_log Values (?,?,?,?, ?,?,?,?, ?)";	
	
	
	@Path("/version")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_version() throws Exception {return Response.ok(strVersion).build() ;}

	
	
	
	@Path("/log")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response log_data_a(String strLogData) throws Exception { 

		
		String strDevice = "a";
		// Parse the string to json object
		JSONArray ja_LogData = new JSONArray(strLogData);
		System.out.println(ja_LogData.toString(1));
		
		JSONObject jo = new JSONObject();
/*		
		
		for (int i = 0; i <  ja_LogData.length(); i++) {
				jo=ja_LogData.getJSONObject(i);
				JSONHelper.json_db("e", strSQL_insert_beacon_log ,9,jo.opt("RSSI"), 
																	jo.opt("Major/Instance"),
																	jo.opt("TX"),
																	jo.opt("UUID/Namespace"),
																	jo.opt("Minor"),
																	jo.opt("Timestamp"),
																	jo.opt("TimeFormatted"),
																	jo.opt("Distance"),
																	strDevice); 
		}
	*/	
		return Response.ok("test").build();
	}

	
	
	
}





	
	



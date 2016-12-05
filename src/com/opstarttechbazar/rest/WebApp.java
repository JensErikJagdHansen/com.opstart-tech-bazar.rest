package com.opstarttechbazar.rest;

// Version 2016.09.21 14.30

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

	public static final String dbURL = "jdbc:sqlserver://localhost:49170;databaseName=wateralert;user=flowline;password=123";
	
	private static final JSONHelper JSONHelper = new JSONHelper(dbURL,intPrint_JSON);
		
	
	
}





	
	



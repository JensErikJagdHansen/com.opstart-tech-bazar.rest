package com.opsstrattechbazar.rest;

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
	public static final String dbURL = "jdbc:sqlserver://localhost:49170;databaseName=opsstrattechbazar;user=flowline;password=123";

	
	//Connection version 
	public static final String strVersion = "2.0.4.16d";
	private static final JSONHelper JSONHelper = new JSONHelper(dbURL,intPrint_JSON);
	

	// Updates users position
	private static final String strSQL_insert_beacon_log =  "Insert Into [110_Beacon_scan] Values (?,?,?, ?,?,?, ?, getutcdate() )";
	private static final String strSQL_delete_user_position = "delete from [210_UserPosition] where UserID = ? ";

	private static final String strSQL_insert_user_position  = "Insert into [210_UserPosition] (UUID, Major, Count_Beacons, RSSI_Sum, UserID, Building, Floor, UTCDateTime)  select ? ,? , ?, ?,?,?,?, getUTCDate()";  
	private static final String strSQL_select_position = "Select top 1 [UUID], Major, 1 as Count_Beacons, RSSI as RSSI_Sum, UserID, getUTCDate() FROM [opsstrattechbazar].[dbo].[110_Beacon_scan] where UserID = ? order by RSSI_sum desc ";  //group by UUID, Major, UserID order by RSSI_Sum desc ";
	private static final String strSQL_get_building = "select *   from [010_BeaconRegions]  where UUID= ?  and Major = ?";

	private static final String strSQL_delete_beacon_log =  "delete from [110_Beacon_scan] where UserID = ? ";

	private static final String strSQL_Regions =  "select * from [010_BeaconRegions]";

	private static final String strSQL_add_beacon = "Insert Into [020_Beacons] (UUID, Major, Minor) ?,?,?";
	
	
	// Used to schedule statistics update
	private static final String strSQL_statistics_control_set  =  "update [880_line_stats_control] set Update_Run_Flag =  ?";
	private static final String strSQL_get_status_current = "Select * from [880_line_stats_control]";
	private static final ScheduledExecutorService scheduler =  Executors.newScheduledThreadPool(1);
	private static ScheduledFuture<?> task;

	private static final String strSQL_TrackUsers = "insert into [220_UserTrack]  select UserID, UUID, Major, Building, [Floor], [RSSI_sum], [Count_Beacons], getUTCDate() as UTCDateTime  from  [210_UserPosition]"; 
	
	
	
	@Path("/version")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_version() throws Exception {return Response.ok(strVersion).build() ;}


	@Path("/BeaconRegions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_beacons() throws Exception {return Response.ok(JSONHelper.json_db("q",strSQL_Regions,0).toString(1)).build() ;}
	

	@Path("/log")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response log_data_a(String strLogData) throws Exception { 

		JSONArray ja_LogData = new JSONArray();
		String strUserID;
		String fldUUID;
		String fldMajor;
		
		
		strLogData.trim();
		if( strLogData.charAt(0) == '{')
		{	// Assume from IOS scanner
			JSONObject j = new  JSONObject(strLogData);	
			strUserID = j.optString("UserID");
			ja_LogData = j.optJSONArray("LogData");
			fldUUID = "UUID";
			fldMajor = "Major";

		}
		else
		{
			ja_LogData = new JSONArray(strLogData);
			strUserID ="Android";
			fldUUID = "UUID/Namespace";
			fldMajor = "Major/Instance";
			
		}
		

		//System.out.println(ja_LogData.toString(1));
		
		JSONObject jo = new JSONObject();
		
		for (int i = 0; i <  ja_LogData.length(); i++) {
				jo=ja_LogData.getJSONObject(i);
				
				
		 
				JSONHelper.json_db("e", strSQL_insert_beacon_log ,7, 																	
																	jo.opt(fldUUID),
																	jo.opt(fldMajor),
																	jo.opt("Minor"),
																	jo.opt("RSSI"),
																	0,
																	0,
																	strUserID); 

		}
		
		

		JSONHelper.json_db("e", strSQL_delete_user_position ,1,strUserID);
		
		JSONArray ja_position = JSONHelper.json_db("q", strSQL_select_position ,1,strUserID);
		if (ja_position.length()==0) 
			 {
				return Response.ok("test").build();
			 }

		String  UUID 			= ja_position.getJSONObject(0).getString("UUID");
		Integer Major 	   		= ja_position.getJSONObject(0).getInt("Major");
		Integer Count_Beacons 	= ja_position.getJSONObject(0).getInt("Count_Beacons");
		Double  RSSI_Sum		= ja_position.getJSONObject(0).getDouble("RSSI_Sum");
		String UserID			= ja_position.getJSONObject(0).getString("UserID");
		
		JSONArray ja_building =  JSONHelper.json_db("q", strSQL_get_building ,2,UUID, Major); 
		
		if (ja_building.length()>0) 
		 {
			String Building 		= ja_building.getJSONObject(0).getString("Building");
			String Floor 			= ja_building.getJSONObject(0).getString("Floor");
			JSONHelper.json_db("e", strSQL_insert_user_position ,7 , UUID, Major, Count_Beacons, RSSI_Sum, UserID,Building, Floor);		
		 }


		JSONHelper.json_db("e", strSQL_delete_beacon_log ,1,strUserID);

		
		return Response.ok("test").build();
	}
		
	
	@Path("/add")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response add_beacon(String strLogData) throws Exception { 

		// Parse the string to json object
		JSONArray ja_LogData = new JSONArray(strLogData);
		JSONObject jo = new JSONObject();
		
		System.out.println(ja_LogData.toString(1));
		
		for (int i = 0; i <  ja_LogData.length(); i++) {
				jo=ja_LogData.getJSONObject(i);
				JSONHelper.json_db("e", strSQL_add_beacon ,3,jo.get("UUID/Namespace"),jo.opt("Major/Instance"),	jo.opt("Minor")) ;
		}
		return Response.ok("test").build();
	}
	
	
	
	private void statistics_updater() {
		
		Integer isRunning =0;
		JSONArray ja = new JSONArray();
		
		try{
			// look up frequency for update and recalculate in to seconds
			ja = JSONHelper.json_db("q", strSQL_get_status_current, 0);
			// if update flag set to zero then stop execution
			isRunning =ja.getJSONObject(0).optInt("Update_Run_Flag");
			
			set_statistics();
			
		}
		catch (Exception e){
			e.printStackTrace();}
		finally	{
			if ( isRunning == 0) {	task.cancel(true);	}
		}
		return;	
	}
	

	
	
	
	
	
	
	
	
	
	
	
	

	@Path("/statistics/autoupdate/start")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response autopdate_start() throws Exception {

		final Runnable beeper = new Runnable() {
			public void run() { statistics_updater();	}
		};

		
		// look up running status flag, frequency for update and frequency
		JSONArray ja = JSONHelper.json_db("q", strSQL_get_status_current, 0);

		// Checks to see if the update is all ready running (to not allow multiple threads)
		Integer intRunning = ja.getJSONObject(0).optInt("Update_Run_Flag");
		if (intRunning == 1) { 	return Response.ok("Auto-update already running").build();}
		
		// Calculated the seconds between updats 
		Integer interval_seconds = (int) (60 * ja.getJSONObject(0).getDouble("Update_Frequency"));
		
		// Find current time and calculate day time into seconds
		Calendar rightNow = Calendar.getInstance();
		int SecondsInToDay =  rightNow.get(Calendar.HOUR_OF_DAY) *60*60 +  rightNow.get(Calendar.MINUTE)*60 + rightNow.get(Calendar.SECOND)*1;
		
		// Find time to start, in seconds
		int DelayFromNow_seconds = ((int) SecondsInToDay/interval_seconds)*interval_seconds + interval_seconds - SecondsInToDay;
		
		task = scheduler.scheduleAtFixedRate(beeper, DelayFromNow_seconds, interval_seconds, SECONDS);

		// Add time to start, for display in response
		rightNow.add(Calendar.SECOND,DelayFromNow_seconds );
		SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

		// Set to flag to running
		JSONHelper.json_db("e", strSQL_statistics_control_set, 1,1);
		
		return Response.ok("Auto-update started at: " + ft.format(rightNow.getTime()) + " Frequency of update is: " + (int) (interval_seconds/60) + " minutte(s) and "  +  (interval_seconds - 60 * (int) (interval_seconds/60)) + " seconds"  ).build();
		
	}
	
	
		
	@Path("/statistics/autoupdate/stop")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response autopdate_stop() throws Exception {

		// Set to stop 
		JSONHelper.json_db("e", strSQL_statistics_control_set, 1, 0);
		if ( (task == null) || (task.isCancelled())) {return Response.ok("Auto-update did not seem to be running").build();}	
		task.cancel(true);
		
		
	return Response.ok("Auto-update stopped").build();
	}
	
	

	
	
	@Path("/statistics/update")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response set_statistics() throws Exception { 
	
		JSONArray ja = new JSONArray();
		
		JSONHelper.json_db("e", strSQL_TrackUsers, 0);
		
		
		return Response.ok("Statistics opdated" ).build();
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
}





	
	



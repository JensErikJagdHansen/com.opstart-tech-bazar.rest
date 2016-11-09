package com.datacapture.rest;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public class JSONHelper {
	
	private static  String dbURL;
	private static  Integer intPrint_JSON;
	
	public JSONHelper(String URL, Integer Print_JSON) {
		dbURL = URL;
		intPrint_JSON = Print_JSON;
	}

	
	public  JSONArray json_db(String strType, String strSQL, int intWhereClause, Object  ... objClauseParamArg ) throws Exception  {
		//  strType				"q", "e"
		//	strSQL 				SQL statement, including ? in where clauses
		//  intWhereClause  	Number of ? to replace
		//  strClauseParamArg 	list of parameter values to replace in ?
		
		Connection conn = null;
		int i;
	  try {
		  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		  conn = DriverManager.getConnection(dbURL);
		  if (conn != null) {
	        	PreparedStatement statement =  conn.prepareStatement(strSQL);
	        	// SQL Injection 
	        	for (i = 1; i <=intWhereClause; i++ ) {
	        		if (objClauseParamArg[i-1] instanceof String) statement.setString(i, (String) objClauseParamArg[i-1] );
	        		if (objClauseParamArg[i-1] instanceof Integer) statement.setInt(i, (Integer) objClauseParamArg[i-1] );
	        		if (objClauseParamArg[i-1] instanceof Double) statement.setDouble(i, (double) objClauseParamArg[i-1] );
	        		if (objClauseParamArg[i-1] instanceof Date) statement.setDate(i, (Date) objClauseParamArg[i-1] );
	        		if (objClauseParamArg[i-1] instanceof Long ) statement.setLong(i, (long) objClauseParamArg[i-1] );
	        	}
	        	JSONArray ja = new JSONArray();
	        	//select query or execution
	        	if ( strType=="q" ) {
	            	ResultSet result = statement.executeQuery();
	            	ja  =  convertToJSON(result);
	        	}
	        	else {
	            	Integer result = statement.executeUpdate();
	            	JSONObject jo = new JSONObject();
	            	jo.put("records_affected", result);
	            	ja.put(jo);
	        	}
	        	if (intPrint_JSON==1) { System.out.println(ja.toString(1)); }
	        	return ja;
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	       
	    } finally {
	        try {
	            if (conn != null && !conn.isClosed()) {
	                conn.close();
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	   return null;
	}
	
	
	public static JSONArray convertToJSON(ResultSet resultSet) 
	            throws Exception {
			/**
		     * Convert a result set into a JSON Array
		     * @param resultSet
		     * @return a JSONArray
		     * @throws Exception
		     */
			JSONArray jsonArray = new JSONArray();
	        
	        while (resultSet.next()) {
	            int total_cols = resultSet.getMetaData().getColumnCount();
	            JSONObject obj = new JSONObject();
	
	            //Next line is testing purposes, JEH
	            //obj.put("Row", resultSet.getRow());
	
	            
	            for (int i = 0; i < total_cols; i++) {
	                obj.put(resultSet.getMetaData().getColumnLabel(i + 1), resultSet.getObject(i + 1) );
	            }
	            jsonArray.put(obj);
	        }
	        return jsonArray;
	    }	
	
	
	public JSONObject json_merge(JSONObject Obj1, JSONObject Obj2) throws JSONException{
	
		JSONObject merged = new JSONObject(Obj1, JSONObject.getNames(Obj1));
		for(String key : JSONObject.getNames(Obj2))
		{
		  merged.put(key, Obj2.get(key));
		}
		
	return merged;
	
	}
	
	
	
	public JSONArray json_merge_array(String key, JSONArray Source, JSONArray Target) throws JSONException{
		// Note: Type of "key" needs to be an Integer.
		
		for (int i=0;i<Target.length();i++) {
			for (int j=0;j<Source.length();j++){
				JSONObject jo = Source.getJSONObject(j);
				
				if ( Target.getJSONObject(i).optInt(key) ==  jo.optInt(key) ) {
					for(String key2 : JSONObject.getNames(jo))
					{
						Target.getJSONObject(i).put(key2, jo.get(key2));
					}
				}
			}
		}		
		
	return Target;
	
	}
	
	
	

}

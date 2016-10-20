package com.datacapture.rest;


// Version 2016.09.21 14.30


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.sql.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;


import org.json.JSONArray;
import org.json.JSONException;
import static java.util.concurrent.TimeUnit.*;


@Path("/v1") 
public class WebApp {
	
	//Connection version 
	public static final String strVersion = 		"2.0.6.1";

	// If the last last 'z' in x.y.z is different than the in app, then a forced update of app is required of the phone/tablet
	public static final String strVersion_release = "2.0.6";

	public static final Integer intPrint_JSON = 0 ; //Set =0 if not print JSON, =1 if print JSON
	
	//Connection string
	// Test environment
//	public static final String dbURL = "jdbc:sqlserver://217.157.143.212:49170;databaseName=pandoradatacapture;user=flowline;password=123";
//	public static final String dbURL = "jdbc:sqlserver://implement-dev.com:49170;databaseName=pandoradatacapture;user=flowline;password=123";
//	public static final String dbURL = "jdbc:sqlserver://localhost:49170;databaseName=pandoradatacapture;user=flowline;password=123";


	// There are problems with this (it seems to work also, JEH) //USE THIS ONE FOR SERVER
//	public static final String dbURL = "jdbc:sqlserver://THBAN1SRV197:1433;databaseName=pandoradatacapture;integratedSecurity=true";

	//USE THIS ONE FOR SERVER
//	public static final String dbURL = "jdbc:sqlserver://THBAN1SRV197:1433;databaseName=pandoradatacapture;user=flowline;password=123";
	public static final String dbURL = "jdbc:sqlserver://THBAN1SRV197:1433;databaseName=pandoradatacapture;user=DataCaptureWriter;password=Sup3rMan";
	
		
	//Error messages
	private static final String strSQL_ErrMsg =  " select  * from [910_ui_captions] where Type = 2 and CaptionID = ?";
	
	//Lists for pick list in user interface
	private static final String strSQL_lines =  "select LineID from [210_lines] order by SortID";
	private static final String strSQL_operations =  "select OperationID, OperationDescription_EN, OperationDescription_TH  from [320_operations] order by SortID";
	
	//User session
	private static final String strSQL_session_check_user =  "select UserID, UserStatus, DeviceMacAddress from [110_Users] where UserID = ? and PassWord = ? ";
	private static final String strSQL_session_add_userlog      =  "INSERT INTO [120_user_log]      ( UserID, DateTime_Start,  DateTime_End, UserStatus, LineID, WorkBenchID, OperationID, DeviceMacAddress, End_YearWeek ) SELECT  UserID, DateTime_Start, getdate(), UserStatus, LineID, WorkBenchID, OperationID,  DeviceMacAddress , Current_YearWeek FROM [110_users], [880_line_stats_control] where UserID = ?";
	private static final String strSQL_session_add_userlog_hist =  "INSERT INTO [120_user_log_hist] ( UserID, DateTime_Start,  DateTime_End, UserStatus, LineID, WorkBenchID, OperationID, DeviceMacAddress, End_YearWeek ) SELECT  UserID, DateTime_Start, getdate(), UserStatus, LineID, WorkBenchID, OperationID,  DeviceMacAddress , Current_YearWeek FROM [110_users], [880_line_stats_control] where UserID = ?";
	
	private static final String strSQL_session_update_user_1 = "UPDATE [110_users] SET DateTime_Start = getdate(), UserStatus =  1 , LineID = ?, WorkBenchID = ? , OperationID = ? , DeviceMacAddress = ? 	WHERE UserID = ? ";
	private static final String strSQL_session_update_user = "UPDATE [110_users] SET DateTime_Start = getdate(), UserStatus = ? WHERE UserID = ? ";
	private static final String strSQL_session_pause_baskets = "UPDATE [610_baskets] SET BasketStatus = 3 , Last_Update = getdate() WHERE BasketStatus = 2 AND UserID = ? ";
	private static final String strSQL_session_reopen_baskets_from_pause = "UPDATE [610_baskets] SET BasketStatus = 2 , Pause_Time = Pause_Time + Datediff(second, Last_Update, getdate() ), Last_Update = getdate() WHERE BasketStatus = 3 AND UserID = ? ";
	
	private static final String strSQL_session_userinfo = "SELECT [110_users].*, [210_lines].* FROM [110_users] LEFT JOIN [210_lines] ON [110_users].LineID = [210_lines].LineID where USERID = ?"; 

	//Basket update
	private static final String strSQL_basket_get_basket_info = "SELECT * from [610_baskets] where BasketID = ? ";
	private static final String strSQL_basket_add_basket = "INSERT INTO [610_Baskets] (BasketID, BasketStatus) values( ? ,0 ) ";
	private static final String strSQL_UserName  = "Select UserName from [110_Users] where UserID = ? "; 
	private static final String strSQL_operation_description  = "Select OperationDescription_EN, OperationDescription_TH from [320_operations] where OperationID = ? ";
	
	private static final String strSQL_status_load  = "UPDATE [610_Baskets] SET ImagUrl = ?, LineID = ?, JobNr = ?, ItemID=?, OperationNr = ?, OperationID = ?, WorkInstruction = ?, SequenceType = ?, SequenceID = ?, DefectTypeID = ?, BasketStatus = ?, UserID = ?, " 
													+ "WorkbenchID = ?, Std_ProcessTime = ?,  Std_MachineTime = ?, Good_Pcs_In = ?, Good_Pcs_Out = ?, Bad_Pcs_In = ?,  Bad_Pcs_Out = ?, Rejected_Pcs_In =? , Rejected_Pcs_Out = ?, Weight_In = ?, Weight_Out = ?, "
													+ "DateTime_Load = getdate(), DateTime_Start = null, DateTime_End=null, DateTime_Unload = null, "
													+ "Pause_Time = 0, Rework_Time = 0, Pause_Count = 0, Rework_Count = 0 , Last_Update =   getdate(),  " 
													+ "Load_YearWeek  = ?, Load_Shift = ?, OperationMultipla = ? "
													+ "where BasketID = ? AND (BasketStatus =  0 OR BasketStatus = 6) ";
	
	private static final String strSQL_status_start  = "UPDATE [610_Baskets] SET BasketStatus = 2, UserID = ? , WorkbenchID = ? , "
													+ "DateTime_Start = getdate(), Last_Update = getdate(), Good_Pcs_Out = ?, Bad_Pcs_Out = ?, Rejected_Pcs_out = ?  " 
													+ "where BasketID = ? AND BasketStatus = 1";
	  

	private static final String strSQL_status_start_after_pause  = "UPDATE [610_Baskets] SET BasketStatus = 2, UserID = ? , WorkbenchID = ? , "
													+ "Pause_Time = Pause_Time + Datediff(second, Last_Update, getdate()), Pause_Count = Pause_Count +1, Last_Update = getdate(), "
													+ "Good_Pcs_Out = ?, Bad_Pcs_Out = ? , Rejected_Pcs_out = ?   where BasketID = ?  AND BasketStatus= 3 ";

	private static final String strSQL_status_start_after_rework  = "UPDATE [610_Baskets] SET BasketStatus = 2, UserID = ? , WorkbenchID = ? , "
													+ "Rework_Time = Rework_Time + Datediff(second, Last_Update, getdate()), Rework_Count = Rework_Count + 1, Last_Update = getdate(),"
													+ "Good_Pcs_Out = ?, Bad_Pcs_Out = ?, Rejected_Pcs_out = ?   where BasketID = ?  AND BasketStatus = 4";

	private static final String strSQL_status_pause  =  "UPDATE [610_Baskets] SET BasketStatus = 3, UserID = ?, WorkbenchID = ? , Last_Update = getdate() , "
													+ "Good_Pcs_Out = ?, Bad_Pcs_Out = ? , Rejected_Pcs_out = ?  where BasketID = ? AND BasketStatus = 2 ";
	
	private static final String strSQL_status_rework  = "UPDATE [610_Baskets] SET BasketStatus = 4, UserID = ?, WorkbenchID = ? , Last_Update = getdate() ,  "
													+ "Good_Pcs_Out = ?, Bad_Pcs_Out = ? , Rejected_Pcs_out = ?  where BasketID = ? AND BasketStatus = 2 ";
													

	private static final String strSQL_status_end  = "UPDATE [610_Baskets] SET BasketStatus = 5, UserID = ?, WorkbenchID = ? ,"
													+ "DateTime_End = getdate(), Last_Update = getdate() ,"
													+ " Good_Pcs_Out = ? ,  Bad_Pcs_Out = ? , Rejected_Pcs_out = ? where BasketID = ?  and BasketStatus = 2 ";
	
	private static final String strSQL_status_unload  = "UPDATE [610_Baskets] SET BasketStatus = 6, UserID = ?, WorkbenchID = ? , "
													+ "Good_Pcs_In = ?, Good_Pcs_Out = ?, Bad_Pcs_In = ?,  Bad_Pcs_Out = ?, Rejected_Pcs_in = ?, Rejected_Pcs_out = ?,   Weight_In = ?, Weight_Out = ?, "
													+ "DateTime_Unload = getdate(), Last_Update = getdate()  where BasketID = ? AND BasketStatus = 5";

	private static final String strSQL_status_clear_basket = "UPDATE [610_Baskets] SET BasketStatus = 0, ImagUrl = Null, UserID = Null, LineID = Null, WorkbenchID = Null , ItemID =  Null, Load_YearWeek  = Null, Load_Shift = Null,  OperationNr = Null, OperationID=Null , WorkInstruction = Null, "
									+ "Std_ProcessTime = 0, Std_MachineTime=0,  Last_Update = getdate() , JobNr=Null, SequenceID = Null, SequenceType = 1 , DefectTypeID = Null , "
									+ " Good_Pcs_In = 0, Good_Pcs_Out = 0 , Bad_Pcs_in = 0, Bad_Pcs_Out = 0,  Pause_Time=0, Rework_Time=0 ,Rejected_Pcs_in = 0, Rejected_Pcs_out = 0,  " 
									+ "Weight_In = 0, Weight_Out = 0, DateTime_Load =Null , DateTime_Start =Null , DateTime_End=Null , DateTime_Unload=Null, OperationMultipla=1  where BasketID = ? ";
	
	
	private static final String strSQL_status_add_basketlog 	=  "INSERT INTO [620_basket_log]      SELECT [610_baskets].* FROM [610_baskets] WHERE BasketID = ? ";
	private static final String strSQL_status_add_basketlog_hist = "INSERT INTO [620_basket_log_hist] SELECT [610_baskets].* FROM [610_baskets] WHERE BasketID = ? ";
	
	// current year week and shift
	private static final String strSQL_Current = "Select * from  [880_line_stats_control]";
		
	//Job Nr (used for pacemaker screen)
	private static final String strSQL_jobnr_get = "SELECT Top 1 *, FamilyLane=Lane from [520_loadplan] WHERE JobNr = ? ";
	private static final String strSQL_jobnr_item = "SELECT * from [310_Products] where ItemID = ? ";
	private static final String strSQL_jobnr_sequences = "SELECT [330_standard_sequences].*, [320_operations].OperationDescription_EN, [320_operations].OperationDescription_TH "
													+" FROM [320_operations] INNER JOIN [330_standard_sequences] ON [320_operations].OperationID = [330_standard_sequences].OperationID where ItemID = ? and SequenceType=1 order by OperationNr ";
	
	private static final String strSQL_jobnr_sequences_rework =  "select * from [415_Sequences] where (ItemID= ? or ItemID is Null or ItemID='' ) and SequenceType = 2  order by DefectTypeID, ItemRelationType Desc, SortID " ;

	
	private static final String strSQL_sequence = "SELECT [330_standard_sequences].*, [320_operations].OperationDescription_EN, [320_operations].OperationDescription_TH "
			+" FROM [320_operations] INNER JOIN [330_standard_sequences] ON [320_operations].OperationID = [330_standard_sequences].OperationID where SequenceID = ? order by OperationNr ";
	
	
	private static final String strSQL_sequence_def = "Select * from [415_Sequences] where SequenceID = ?";
	
	private static final String strSQL_Sequences_MaxSortID_specific  = " Select max(sortID) as SortID from [415_sequences] where ItemID = ? and DefectTypeID = ? and ItemRelationType = 1";
	private static final String strSQL_Sequences_MaxSortID_generic  = " Select max(sortID) as SortID from [415_sequences] where (ItemID is null or ItemID='') and DefectTypeID = ? and ItemRelationType = 2";
	

	private static final String strSequence_add = "INSERT INTO [415_sequences] ( SequenceID, SequenceType, ItemRelationType, ItemID, DefectTypeID , SequenceDescription_EN, SequenceDescription_TH, SortID ) SELECT  ?,?,?,  ?,?,?, ?,? ";
	public static final String strSequence_steps_add = "Insert into [330_standard_sequences] (SequenceID ,SequenceType,ItemID, DefectTypeID, OperationNr,OperationID ,WorkInstruction, ProcessTime, MachineTime,WeightControlFlag ,OperationMultipla ) select ?,?,?,  ?,?,?,  ?,?,? ,?,? ";
	
	private static final String strSQL_jobnr_baskets = "SELECT * from [610_baskets] where  basketstatus > 0 and  basketstatus<6  and JobNr like ? order by OperationID, DateTime_Load";
	
	//Sequences
	private static final String strSQL_sequences_defecttypes = "Select * from [410_defect_types] order by sortID ";
	
	//Standard time
	private static final String strSQL_standard_time_standard = "SELECT *  FROM [330_standard_sequences] WHERE SequenceID= ? AND OperationNr = ? ";

	
	//Translations and error msg
	private static final String strSQL_translations_caption = "SELECT * from [910_ui_captions] where Type=1";
	private static final String strSQL_translations_error_msg = "SELECT * from [910_ui_captions] where Type=2";
	
	
	//JobNr, ItemID overview....
	private static final String strSQL_jobnr_loadplan = "SELECT isnull(sum(Quantity),0) AS Pcs, sum(CountBaskets) AS Baskets, 'load_plan' as Field  FROM [520_loadplan], [880_line_stats_control] where  				(Current_YearWeek - YearWeek ) * iif( ? = 'LimitToCurrent' ,1,0)>=0    AND LineID like ?   AND JobNr like ?    AND ItemID like ? ";   
	private static final String strSQL_jobnr_loaded_active = "SELECT isnull(sum(Good_Pcs_In),0) AS Pcs, Count(ItemID) AS Baskets, 'loaded_active' as Field 	FROM [610_Baskets],    [880_line_stats_control] Where 	(Current_YearWeek - cast(load_YearWeek as int) ) * iif( ? = 'LimitToCurrent' ,1,0)>=0 AND OperationNr = 1 AND LineID like ?  AND JobNr like ?  AND ItemID like ? "; 
	private static final String strSQL_jobnr_loaded_log =    "SELECT isnull(sum(Good_Pcs_In),0) AS Pcs, Count(ItemID) AS Baskets, 'loaded_log'    as Field 	FROM [620_Basket_log], [880_line_stats_control] Where 	(Current_YearWeek - cast(load_YearWeek  as int)) * iif( ? = 'LimitToCurrent' ,1,0)>=0 AND OperationNr = 1 AND LineID like ?  AND JobNr like ?  AND ItemID like ? ";
	

	private static final String strSQL_jobnr_overview = "Select  i.Opr2ID, Baskets = count(i.BasketID), Good_PCS = sum(i.Good_PCS), Bad_PCS = sum(i.Bad_PCS), OperationNr From  ( SELECT  Opr2ID=IIf([SequenceType]=2 And [OperationID]<>'PCK' And [OperationID]<>'QI','R',[OperationID]), "
													+ " BasketID, Good_PCS= iif(BasketStatus   <5, Good_Pcs_In, Good_pcs_out) ,   Bad_PCS = iif(BasketStatus <   5, Bad_Pcs_in, Bad_pcs_out) , " 
													+ "OperationNr =  iif(OperationID='QI', 9990,   iif(SequenceType=2,9991, iif(operationID='FQC', 9992, iif(operationID='PCK',9993,OperationNr)))) "
													+ "FROM [610_baskets]  where basketstatus > 0 and  basketstatus<6 and  LineID like ? and  jobnr like ? and  itemID like ?  ) as i group by i.Opr2ID, i.OperationNr order by i.OperationNr ";

	
	
	private static final String strSQL_jobnr_overview_total =  "SELECT  'Total' as Opr2ID , Count(BasketID) AS Baskets, sum(iif(BasketStatus   <5, Good_Pcs_In, Good_pcs_out)) AS Good_PCS,   sum(iif(BasketStatus <   5, Bad_Pcs_in, Bad_pcs_out)) AS Bad_PCS  FROM [610_baskets]  where basketstatus >0 and  basketstatus<6 and LineID like ?  and jobnr like ? and itemID like ?  ";
	
	
	private static final String strSQL_Line_LoadPlan = "Select * from [520_LoadPlan] where lineID = ?  and YearWeek > 0 AND (Initiated=0 or Initiated=Null) order by  LoadDateTime";
	
	private static final String	strSQL_JobNr_swap_get =  "select * from [520_LoadPlan] where (JobNr=? or JobNr=?) and (Initiated <> 1 or Initiated is null)";
	
	private static final String strSQL_JobNr_swap_set  = "update [520_LoadPlan] set LoadDateTime =  ?, YearWeek = ?,  shift = ? , WeekDay = ?, SortID = ?, PctInToWeek = ? where JobNr =  ?";
	
	private static final String strSQL_load_JobNr_initiate = "update [520_LoadPlan] set Initiated =  1, Initiated_DateTime= getdate() where JobNr =  ? AND (Initiated = 0 or Initiated is null)  ";
	
	private static final String strSQL_load_JobNr_receive = "update [520_LoadPlan] set Received =  1 - ISNULL(Received, 0 ) ,  Received_DateTime = iif( Received=1,Null,getdate()) where JobNr =  ? and (Initiated=0 or Initiated is Null)";
	private static final String strSQL_load_JobNr_force_receive = "update [520_LoadPlan] set Received =  1 - ISNULL(Received, 0 ) ,  Received_DateTime = iif( Received=1,Null,getdate()) where JobNr =  ? and (Received =0 or Received is Null)";
	
	
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Statistics update, information for dashboard - set current shift and hour
	private static final String  strSQL_set_current_shift_hour = "UPDATE  [880_line_stats_control] SET  Current_Shift  = shift, Current_Hour = DATEPART(hh,GETDATE()) from [880_line_stats_control], [890_line_stats_shifts]  where " 
			+ "DATEPART(dw,GETDATE()) + 1.0*datepart(hh,getdate())/24 +1.0*datepart(mi,getdate())/60/24  >=cal_weekday_start + start_time  AND "
			+ "DATEPART(dw,GETDATE()) + 1.0*datepart(hh,getdate())/24 +1.0*datepart(mi,getdate())/60/24  < cal_weekday_end   + end_time ";

			
	// Statistics update, information for dashboard -  Update target volume by hour and by shift
	private static final String strSQL_target_delete = "delete from [830_line_stats_status_target]";
	private static final String strSQL_target_by_hour =  "INSERT INTO [830_line_stats_status_target] ( LineID, SortID, Shift, Hour, pcs, baskets, Type ) SELECT LineID, min(datepart(dy,loaddatetime))*1000+datepart(hh,loaddatetime), [520_loadplan].Shift, datepart(hh,loaddatetime), sum(Quantity), sum(CountBaskets), 2  FROM [520_loadplan] , [880_line_stats_control] where  [520_loadplan].YearWeek = [880_line_stats_control].Current_YearWeek GROUP BY LineID, [520_loadplan].Shift, datepart(hh,loaddatetime)";
	private static final String strSQL_target_by_shift = "INSERT INTO [830_line_stats_status_target] ( LineID, Type, Shift, Hour, SortID, Pcs, Baskets ) SELECT LineID, 1, Shift, 0, min(SortID) , Sum(Pcs), Sum(Baskets) FROM [830_line_stats_status_target] GROUP BY LineID, Type, Shift HAVING Type=2";

	
	// Statistics update elapsed hours of plan
	private static final String strSQL_set_current_elapsed  =  "UPDATE  [880_line_stats_control] SET Current_ElapsedHoursOfPlan = x from (select x= Count (distinct SortID) from [830_line_stats_status_target] where Type=2 and sortid <= datepart(dy, getdate() )*1000+datepart(hh,getdate())) as InnerSQL ";

	//  Statistics update, information for dashboard - Update actual volume by hour and by shift
	private static final String strSQL_actual_delete = "delete from [832_line_stats_status_actual]";
	private static final String strSQL_actual_by_hour_live_baskets =      "INSERT INTO [832_line_stats_status_actual] (LineID, Type, Shift, [Hour], SortID, Baskets, Pcs_good, Pcs_bad, BasketStatus) SELECT  LineID, 2, Load_Shift, DatePart(hh,DateTime_Load) ,      DatePart(dy,[DateTime_Load])*1000+DatePart(hh,[DateTime_load]),  Count(BasketID),   Sum(Good_Pcs_In) , Sum(Bad_Pcs_In), BasketStatus FROM [610_baskets],     [880_line_stats_control] WHERE BasketStatus<>6 And BasketStatus<>0 AND Load_yearweek = Current_YearWeek AND OperationNr = 1 AND SequenceType =  1 GROUP BY LineID, Load_Shift, DatePart(hh,DateTime_Load), BasketStatus, DatePart(dy,[DateTime_Load])*1000+DatePart(hh,[DateTime_load]) ";
	private static final String strSQL_actual_by_hour_unloaded_baskets =  "INSERT INTO [832_line_stats_status_actual] (LineID, Type, Shift, [Hour], SortID, Baskets, Pcs_good, Pcs_bad, BasketStatus) SELECT  LineID, 2, Load_Shift, DatePart(hh,DateTime_Load) ,  min(DatePart(dy,[DateTime_Load])*1000+DatePart(hh,[DateTime_load])), Count(BasketID),   Sum(Good_Pcs_In) , Sum(Bad_Pcs_In), BasketStatus FROM [620_basket_log],  [880_line_stats_control] WHERE BasketStatus=6                      AND Load_yearweek = Current_YearWeek AND OperationNr = 1 AND SequenceType =  1 GROUP BY LineID, Load_Shift, DatePart(hh,DateTime_Load), BasketStatus "; 
	private static final String strSQL_actual_by_shift = 				 "INSERT INTO [832_line_stats_status_actual] (LineID, Type, Shift, [Hour], SortID, Baskets, Pcs_good, Pcs_bad, BasketStatus) SELECT  LineID, 1, Shift, 0, min(SortID) , Sum(Baskets), Sum(Pcs_good), Sum(Pcs_bad), BasketStatus  FROM [832_line_stats_status_actual] GROUP BY LineID, Type, Shift, BasketStatus HAVING Type=2";
	
	//  Statistics update, information for dashboard - Update WIP 
	private static final String strSQL_wip_delete = "DELETE  from [840_line_stats_wip]";
	private static final String strSQL_wip = "INSERT INTO [840_line_stats_wip] ( LineID, BasketStatus, Opr2ID, Baskets, PCS ) SELECT LineID, BasketStatus, IIf([SequenceType]=2 And [OperationID]<>'PCK' And [OperationID]<>'QI','R',[OperationID]), Count(BasketID) , Sum([Bad_Pcs_In]+[Good_Pcs_In]) FROM [610_baskets] where operationID is not null  GROUP BY LineID, BasketStatus, IIf([SequenceType]=2 And [OperationID]<>'PCK' And [OperationID]<>'QI','R',[OperationID]) HAVING (BasketStatus=1 Or BasketStatus=2 Or BasketStatus=3 Or BasketStatus=4 Or BasketStatus=5) ";



	//  Statistics update, information for dashboard - Update statistics table 
	private static final String strSQL_ow_strSQL_delete = "Delete from [810_line_stats_quantity] ";
	private static final String strSQL_ow_target_week = " INSERT INTO [810_line_stats_quantity]  ( Pcs, Baskets, Field, LineID ) SELECT Sum(Pcs),                   Sum(Baskets) ,   'target_week' , LineID  FROM [830_line_stats_status_target] WHERE Type=1 GROUP BY LineID ";
	private static final String strSQL_ow_target_now =   "INSERT INTO [810_line_stats_quantity]  ( Pcs, Baskets, Field, LineID ) SELECT Sum(Pcs) ,                  Sum(Baskets) ,   'target_now', LineID FROM [830_line_stats_status_target], [880_line_stats_control] where Type=2 AND sortid <= datepart(dy, getdate() )*1000+datepart(hh,getdate()) GROUP BY LineID ";
	private static final String strSQL_ow_loaded_now  =  "INSERT INTO [810_line_stats_quantity]  ( Pcs, Baskets, Field, LineID ) SELECT sum([Pcs_bad]+[Pcs_good]) , sum(Baskets),    'loaded_now', LineID  FROM [832_line_stats_status_actual] where type =1 GROUP BY LineID" ;
	private static final String strSQL_ow_wip_standard = "INSERT INTO [810_line_stats_quantity]  ( Pcs, Baskets, Field, LineID ) SELECT Sum(Pcs),                   Sum(Baskets),    'wip_standard' , LineID FROM [840_line_stats_wip] WHERE Opr2ID not in ('QI', 'R', 'FQC', 'PCK', 'Prepare') GROUP BY LineID";
	
	private static final String strSQL_ow_wip_rework   = "INSERT INTO [810_line_stats_quantity]  ( Pcs, Baskets, Field, LineID ) SELECT Sum(Pcs),                   Sum(Baskets),    'wip_rework' ,   LineID FROM [840_line_stats_wip] WHERE Opr2ID in ('QI', 'R', 'FQC', 'PCK')     GROUP BY LineID";
	private static final String strSQL_ow_completed    = "INSERT INTO [810_line_stats_quantity]  ( Pcs, Baskets, Field, LineID ) SELECT sum(Good_Pcs_Out),          sum(iif(SequenceType=1,1,0)), 'completed_this_week', LineID FROM [880_line_stats_control],[620_basket_log] where Current_YearWeek = Load_YearWeek AND OperationID='QI' GROUP BY LineID";

	
	//  Statistics update, information for dashboard - Utilisation/Efficiency and productivity
	private static final String strSQL_productivity_delete = "Delete from [850_line_stats_productivity]";
	private static final String strSQL_productivity_util_userlog = "INSERT INTO [850_line_stats_productivity] (Type, LineID, UserID, OperationID, AvailableTime_Lag, AvailableTime_Week )  SELECT  1, LineID, UserID,  OperationID, sum(1.0*Datediff(second, iif(DateTime_Start < getdate() - 1.0*Statistics_lag/60/24, getdate() - 1.0*Statistics_lag/60/24, DateTime_Start), iif(DateTime_End < getdate() - 1.0*Statistics_lag/60/24, getdate() - 1.0*Statistics_lag/60/24, DateTime_End))), sum(Datediff(second, DateTime_Start, DateTime_End)) FROM [120_user_log], [880_line_stats_control] WHERE (UserStatus='1' Or UserStatus='4') and End_YearWeek=Current_YearWeek and OperationID is not Null Group By LineID, UserID, OperationID ";
	private static final String strSQL_productivity_util_users =   "INSERT INTO [850_line_stats_productivity] (Type, LineID, UserID, OperationID, AvailableTime_Lag, AvailableTime_Week )  SELECT  2, LineID, UserID,  OperationID, sum(1.0*Datediff(second, iif(DateTime_Start < getdate() - 1.0*Statistics_lag/60/24, getdate() - 1.0*Statistics_lag/60/24, DateTime_Start), getdate())), sum(Datediff(second, DateTime_Start, getDate()))                                                                                                   FROM [110_users],    [880_line_stats_control] WHERE (UserStatus='1' Or UserStatus='4')                                       and OperationID is not null Group By LineID, UserID, OperationID";

	private static final String strSQL_productivity_eff_basketlog =	"Insert into [850_line_stats_productivity] (LineID, UserID, OperationID, Type, ProcessTime_lag,ProcessTime_week,StandardTime_lag,StandardTime_week,Pcs_lag,Pcs_Week ) "
											+	"Select i.LineID, i.UserID, i.opr2ID, i.Type,  sum(i.ProcessTime*i.fraction),sum(i.ProcessTime),sum(i.StandardTime*i.fraction),sum(i.StandardTime),sum(Good_Pcs_Out*i.fraction/Multipla),sum(Good_Pcs_Out/Multipla) "
											+	"From ( Select fraction = 1.0*Datediff(second, iif(DateTime_Start < getdate() - 1.0*Statistics_lag/60/24, getdate() - 1.0*Statistics_lag/60/24, DateTime_Start),iif(DateTime_End < getdate() - 1.0*Statistics_lag/60/24, getdate() - 1.0*Statistics_lag/60/24, DateTime_End))/iif(Datediff(second, DateTime_Start, DateTime_End)=0,1,Datediff(second, DateTime_Start, DateTime_End)), "
											+	"3 as Type, LineID, UserID,  OperationID, Good_Pcs_Out, Multipla=isnull(iif(OperationMultipla=0,1,OperationMultipla),1), "
											+   "ProcessTime = 1.0*Datediff(second, DateTime_Start, DateTime_End) - isnull(Pause_Time,0) - isnull(Rework_Time,0), StandardTime = isnull(1.0*Good_Pcs_in*Std_ProcessTime,0), "
											+   "Opr2ID= IIf([SequenceType]=2 And [OperationID]<>'PCK' And [OperationID]<>'QI','R',[OperationID])  "
											+	"FROM [620_Basket_Log], [880_line_stats_control] where Load_YearWeek=Current_YearWeek and  DateTime_End is not null  and DateTime_Start is not null and DateTime_End>DateTime_Start and OperationID is not null  ) AS i Group By i.Type, i.LineID, i.UserID, i.opr2ID ";
	
	private static final String strSQL_productivity_eff_basket = "Insert into [850_line_stats_productivity] ( LineID, UserID, OperationID, Type, ProcessTime_lag,ProcessTime_week,StandardTime_lag,StandardTime_week,Pcs_lag,Pcs_Week) "
											+ "Select i.LineID, i.UserID, i.opr2ID, i.Type,  sum(i.ProcessTime), sum(i.ProcessTime), sum(iif(i.ProcessTime > i.Std_ProcessTime, Std_ProcessTime, i.ProcessTime)), sum(iif(i.ProcessTime > i.Std_ProcessTime, Std_ProcessTime, i.ProcessTime)), sum(i.Good_Pcs_in * iif(i.processtime>i.std_processtime,1,iif( std_processtime>0 ,i.processtime/i.std_processtime,1) )/Multipla), sum(i.Good_Pcs_in * iif(i.processtime>i.std_processtime,1,iif( std_processtime>0 ,i.processtime/i.std_processtime,1) )/Multipla) "
											+ "From  (Select 4 as Type, LineID, UserID,  OperationID, Multipla=isnull(iif(OperationMultipla=0,1,OperationMultipla),1) , "
											+ "ProcessTime = 1.0*Datediff(second,	iif(DateTime_Start < getdate() - 1.0*Statistics_lag/60/24, getdate() - 1.0*Statistics_lag/60/24, DateTime_Start),  iif( DateTime_End is null, getdate(), iif(DateTime_End < getdate() - 1.0*Statistics_lag/60/24 , getdate() - 1.0*Statistics_lag/60/24, DateTime_End )) ) - isnull(Pause_Time,0)*0 - isnull(Rework_Time,0)*0, "
											+ "Std_ProcessTime, Good_Pcs_In, Opr2ID= IIf([SequenceType]=2 And [OperationID]<>'PCK' And [OperationID]<>'QI','R',[OperationID]) " 
											+ "FROM [610_Baskets], [880_line_stats_control]  where Load_YearWeek=Current_YearWeek and  DateTime_Start is not null and (BasketStatus=2 or BasketStatus=5) and OperationID is not null ) AS i Group By i.Type, i.LineID, i.UserID, i.opr2ID" ;
	
	private static final String strSQL_productivity_target  = "select * from  [210_lines] where LineID =  ? ";
	
	
	
	
	
	//  Statistics update, information for dashboard - manning
	private static final String strSQL_manning_delete = "Delete from [860_line_stats_manning]";
	private static final String strSQL_manning_actual = "INSERT INTO [860_line_stats_manning] (LineID, Field, OperationID, Manning) select LineID,  Field='manning_actual', operationID, count(UserID) from [110_Users] where OperationID is not Null  and (UserStatus = 1 or UserStatus = 4) group by LineID, OperationID ";
	private static final String strSQL_manning_plan =	"INSERT INTO [860_line_stats_manning] (LineID, Field, OperationID, Manning) select LineId, Field = 'manning_plan', operationID, Manning  from [230_Line_Manning]"; 
	
	
//  Statistics update, sequence familty
	private static final String strSQL_seqfamily_delete = "Delete from [895_line_stats_qty_by_seqfamily]";
	private static final String strSQL_seqfamily_plan = "insert into [895_line_stats_qty_seqfamily] (LineId, SeqFamily, Type, Lane, Quantity)  select LineId, SeqFamily, 1, Lane, sum(Quantity) from [520_loadplan], [880_line_stats_control]  where yearweek = Current_YearWeek group by lineID, SeqFamily, Lane ";
	
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Statistics, get information for dashboard
	private static final String strSQL_get_status_target = "SELECT  IIF(Type=1, shift, [hour])  AS x, Pcs as Pcs_Target, Baskets as Baskets_Target                    FROM [830_line_stats_status_target]  WHERE LineID= ? AND  Type= ?  AND IIF(Type=1, 0, shift-  ?) = 0                                              ORDER BY SortID";

	
	private static final String strSQL_get_status_actual = "SELECT  IIF(Type=1, shift, [hour])  AS x,  sum(Pcs_good) as Pcs_Actual,  sum(Baskets) as Baskets_Actual   ,sortID*(type-1) + (2-type)*shift as sorting FROM [832_line_stats_status_actual]  WHERE LineID= ?  AND  Type= ?  AND IIF(Type=1, 0, shift - ?) = 0  Group by sortID*(type-1) + (2-type)*shift , IIF(Type=1, shift, [hour])  order by sortID*(type-1) + (2-type)*shift ";
	private static final String strSQL_get_status_current = "Select * from [880_line_stats_control]";

	private static final String strSQL_get_wip = "SELECT Opr2ID, BasketStatus, Pcs, Baskets FROM [840_line_stats_wip] WHERE LineID= ?";
	private static final String strSQL_get_overview = "Select * from [810_line_stats_quantity] where LineID = ?";
	
	
	private static final String strSQL_get_productivity = "SELECT OperationID,  "
				+	" eff_week =  (select min(v) from (select v=avg(Max_Util_Eff) union select v=   COALESCE (sum(isnull(  StandardTime_week   ,0))/ NULLIF( sum(isnull( processTime_week           ,0)),0) ,0))  as i1), "  
				+	" eff_lag =   (select min(v) from (select v=avg(Max_Util_Eff) union select v=   COALESCE (sum(isnull(  StandardTime_Lag    ,0))/ NULLIF( sum(isnull( processTime_lag            ,0)),0) ,0))  as i2), "
				+	" util_week = (select min(v) from (select v=avg(Max_Util_Eff) union select v=   COALESCE (sum(isnull(  processTime_week,    0))/ NULLIF( sum(isnull( AvailableTime_week         ,0)),0) ,0))  as i3), "
				+	" util_lag  = (select min(v) from (select v=avg(Max_Util_Eff) union select v=   COALESCE (sum(isnull(   processTime_lag,    0))/ NULLIF( sum(isnull( AvailableTime_lag          ,0)),0) ,0))  as i4), "
				+	" pcs_per_hour_week= COALESCE (      sum(isnull(pcs_week,0))/ NULLIF( avg(isnull( Current_ElapsedHoursOfPlan ,0)),0) ,0), "
				+	" pcs_per_hour_lag = COALESCE(    60*sum(isnull(pcs_lag,0)) / NULLIF( avg(isnull( statistics_lag             ,0)),0) ,0)  "
				+	" FROM [850_line_stats_productivity], [880_line_stats_control] where lineID = ?  GROUP BY OperationID ";
	
	
	private static final String strSQL_get_manning =  "Select * from [860_line_stats_manning] where LineID = ? ";	
	
	// Basket test
	private static final String strSQL_basket_test =  "select basketID from [620_basket_log] where BasketID= ? ";

	// Used to schedule statistics update
	private static final String strSQL_statistics_control_set  =  "update [880_line_stats_control] set Update_Run_Flag =  ?";
			
	private static final String strSQL_stats_last_update = "INSERT INTO [885_line_stats_updates] (What, LastUpdate ) select 'Statistics Update', getdate() ";
	
	private static final String strSQL_clean_log_basket =  "delete from [620_basket_log] where datediff(dd, DateTime_Load,getdate()) > ?";
	private static final String strSQL_clean_log_users =   "delete from [120_user_log]   where datediff(dd, DateTime_Start,getdate()) > ?";
	
	
	//-update standard sequences with OperationMultipla
	private static final String strSQL_update_operation_multipla_standard = "Update [330_standard_sequences] set OperationMultipla=InnerSQL.OperationMultipla "
			+ " from (select ItemID, operationID, OperationMultipla=count(operationId) from [330_standard_sequences] group by operationID, ItemID) " 
			+ " AS InnerSQL where [330_standard_sequences].ItemID=InnerSQL.ItemID and [330_standard_sequences].OperationID=InnerSQL.OperationID "; 
	
	//-online output
	// Production per hour during shift
	private static final String strSQL_output_realised_production = "" 
			+" select load_yearweek, lineID, Date = convert(date, DateTime_End), Hour=datepart(hh, DateTime_End), Quantity = sum((Good_Pcs_out+ Bad_Pcs_out)/isnull(iif(OperationMultipla=0,1,OperationMultipla),1)), Baskets = count (basketID) , operationID, SequenceType from [620_Basket_log]" 
			+" where  BasketStatus =  6 and  lineID like ? and load_yearweek like ? and SequenceType like ?  and Load_Shift like ? "
			+" group by SequenceType, load_yearweek, lineID, convert(date, DateTime_End), datepart(hh, DateTime_End), operationID"
			+" UNION"
			+" select load_yearweek, lineID, Date = convert(date, DateTime_End), Hour=datepart(hh, DateTime_End), Quantity = sum((Good_Pcs_out+ Bad_Pcs_out)/isnull(iif(OperationMultipla=0,1,OperationMultipla),1)), Baskets = count (basketID) , operationID, SequenceType from  [610_Baskets]"
			+" where BasketStatus =  5 and  lineID like ?  and load_yearweek like ? and SequenceType like ? and Load_Shift like ? "
			+" group by SequenceType, load_yearweek, lineID, convert(date, DateTime_End), datepart(hh, DateTime_End), operationID ";

	private static final String strSQL_loaded_jobnr = "	Select  LineID, JobNr, Loaded= sum(Loadx), load_yearweek "
			+ " from (Select loadx=sum(Good_Pcs_In+ Bad_Pcs_In), JobNr, LineID, load_yearweek from [610_Baskets]     	where SequenceType=1 and lineId like ? and load_yearweek like ? and OperationNr = 1     group by LineID, JobNr, OperationID, load_yearweek " 	    
			+ " Union Select loadx=sum(Good_Pcs_In+ Bad_Pcs_In), JobNr, LineID, load_yearweek from [620_Basket_log] 	where SequenceType=1 and lineId like ? and load_yearweek like ? and OperationNr = 1  	group by LineID, JobNr, OperationID, load_yearweek ) AS innerSQL group by lineID, Jobnr, load_yearweek "; 
	
			private static final ScheduledExecutorService scheduler =  Executors.newScheduledThreadPool(1);
			private static ScheduledFuture<?> task;
	
	
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------------	
	// Version
		
		
		@Path("/version")
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Response return_version() throws Exception {return Response.ok(strVersion).build() ;}

	
	
	
//-------------------------------------------------------------------------------------------------------------------------------------------------------	
// Lines and operations
	
	
	@Path("/lines")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_lines() throws Exception {return Response.ok(JSONHelper.json_db("q",strSQL_lines,0).toString(1)).build() ;}

	
	@Path("/operations")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_operations() throws Exception {return Response.ok(JSONHelper.json_db("q",strSQL_operations,0).toString(1)).build() ;}


	
//-------------------------------------------------------------------------------------------------------------------------------------------------------
// Session login/logout/break/rejoin
		
	@Path("/session/login")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response session_login(String strUserData) throws Exception { 

		// Parse the string to json object
		JSONObject UserData = new JSONObject(strUserData);
		JSONArray Msg = new JSONArray();
		
		// Parse json object to string variables
		String strUserID = UserData.optString("UserID");
		String strPassWord = UserData.optString("PassWord");
		String strLineID = UserData.optString("LineID");
		String strWorkBenchID = UserData.optString("WorkBenchID");
		String strOperationID = UserData.optString("OperationID");
		String strMacAddress = UserData.optString("MacAddress");
		String strVersion= UserData.optString("Version");
		
	// if version is obsolete, force user to update
		if ( (! strVersion.equals(strVersion_release)) && ( ! strVersion.isEmpty() ))  {
			Msg = JSONHelper.json_db("q",strSQL_ErrMsg, 1 ,"new_version_required");
			return Response.status(409).entity(Msg.getJSONObject(0).toString(1)).build();
		}
		
		
		//Look up user in with user id and password. Look in table "110_users"
		JSONArray ja = JSONHelper.json_db("q",strSQL_session_check_user,2,strUserID, strPassWord);
		

		// User or Password error. Find error message and return. Look up in "910_ui_captions" 
		if (ja.length() == 0) {
			Msg = JSONHelper.json_db("q",strSQL_ErrMsg,1 ,"session_uid_pw_not_recognized");
			Msg.getJSONObject(0).put("UserStatus",0);
			return Response.status(401).entity(Msg.getJSONObject(0).toString(1)).build();
		}


		// Pick up json object from json array, to simplify code
		JSONObject jo = ja.getJSONObject(0);
		
		// If status has changed then create log in table "120_user_log" for the old status. 
		if ( jo.optInt("UserStatus") > 1 ) {
			JSONHelper.json_db("e",strSQL_session_add_userlog,1,strUserID);
			JSONHelper.json_db("e",strSQL_session_add_userlog_hist,1,strUserID);
			}
		
		// Update user with status 1. Update in table "110_Users"
		JSONHelper.json_db("e",strSQL_session_update_user_1, 5, strLineID, strWorkBenchID, strOperationID, strMacAddress, strUserID); 

		// Login succeeded, get message and return. Find message in table "910_ui_captions"
		Msg = JSONHelper.json_db("q",strSQL_ErrMsg,1 ,"session_login_succesfull");

		// Find user info of the user and merge into new output
		JSONArray ja_UserInfo = JSONHelper.json_db("q",strSQL_session_userinfo,1 ,strUserID);
		JSONObject ja_out= JSONHelper.json_merge(Msg.getJSONObject(0) , ja_UserInfo.getJSONObject(0));
		
		return Response.ok(ja_out.toString(1)).build();
	}

	
	@Path("/session/logout/{UserID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response session_logout(
			@PathParam("UserID") String strUserID) 
					throws Exception { 
		return Session_Helper.logout_break(strUserID,2);
	}
	
	
	@Path("/session/break/{UserID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response session_break(
			@PathParam("UserID") String strUserID) 
					throws Exception { 
	return Session_Helper.logout_break(strUserID,3);
	}
	
	
	@Path("/session/rejoin/{UserID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response session_rejoin(
			@PathParam("UserID") String strUserID) 
					throws Exception { 
		
		// Create log in table "120_user_log" for the old status. 
		JSONHelper.json_db("e",strSQL_session_add_userlog,1,strUserID);
		JSONHelper.json_db("e",strSQL_session_add_userlog_hist,1,strUserID);
		
		
		// Update user with new status. Update in table "110_Users"
		JSONHelper.json_db("e",strSQL_session_update_user, 2, "4", strUserID); 

		
		// Reopen all baskets that are in status 3=Operation Paused, 
		JSONArray ja = JSONHelper.json_db("e",strSQL_session_reopen_baskets_from_pause ,1 , strUserID);
		Integer i = ja.getJSONObject(0).optInt("records_affected");

		// Reopen succeed, get message and return. Find message in table "910_ui_captions"
		JSONArray Msg = JSONHelper.json_db("q",strSQL_ErrMsg, 1 ,"session_rejoin");
		Msg.getJSONObject(0).put("UserStatus",4);
		String str = Msg.getJSONObject(0).toString(1);
		str=str.replace("??" , i.toString());  
		return Response.ok(str).build();
	
	}

	
	
		
//-------------------------------------------------------------------------------------------------------------------------------------------------------	
// Basket info and update

	// Scan for basket and add if BasketID does not exist.
	@Path("/basket/scan_create/{BasketID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response basket_info_get_create(@PathParam("BasketID") String strBasketID) 
		throws Exception { 
		//Looks up basket with basketID, and retrieve basket information. Adds basket if not exist
		return Response.ok(Basket_Helper.return_basket_info(strBasketID,1).toString(1)).build();
	}
	

	// Scan for basket and return error message if not exist
	@Path("/basket/scan/{BasketID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response basket_info_get(@PathParam("BasketID") String strBasketID) 
		throws Exception { 

		// get basket info. But do no add new basket if it does not exist
		JSONObject jo = Basket_Helper.return_basket_info(strBasketID,0);
				
		// return basket info if JSON object is a basket record
		if (jo.has("BasketID"))  {
			return Response.ok(jo.toString(1)).build();
		}

		// return error message
		return Response.status(404).entity(jo.toString(1)).build();
		
	}
	
	
	@Path("/basket/status")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response update_basket_status(String strBasketInfo) throws Exception { 
		// Parse the string to json object
		JSONObject BasketInfo = new JSONObject(strBasketInfo);

		// Parse json object to string variables
		String strBasketID = BasketInfo.optString("BasketID");
		
		String strLineID = BasketInfo.optString("LineID");
		String strJobNr = BasketInfo.optString("JobNr");
		String strItemID = BasketInfo.optString("ItemID");
		Integer intOperationNr = BasketInfo.optInt("OperationNr");
		String strOperationID = BasketInfo.optString("OperationID");
		String strWorkInstruction = BasketInfo.optString("WorkInstruction");
		Integer intSequenceType = BasketInfo.optInt ("SequenceType");
		String strDefectTypeID = BasketInfo.optString("DefectTypeID");
		Integer intBasketStatus = BasketInfo.optInt("BasketStatus");
		String strUserID = BasketInfo.optString("UserID");
		String strWorkbenchID = BasketInfo.optString("WorkbenchID");
		
		Integer intGood_Pcs_In = BasketInfo.optInt("Good_Pcs_In");
		Integer intGood_Pcs_Out = BasketInfo.optInt("Good_Pcs_Out");
		Integer intBad_Pcs_In = BasketInfo.optInt("Bad_Pcs_In");
		Integer intBad_Pcs_Out = BasketInfo.optInt("Bad_Pcs_Out");
		Integer intRejected_Pcs_In = BasketInfo.optInt("Rejected_Pcs_In");
		Integer intRejected_Pcs_Out = BasketInfo.optInt("Rejected_Pcs_Out");
		String strSequenceID = BasketInfo.optString("SequenceID");

		
		
		double dblWeight_In = BasketInfo.optDouble("Weight_In");
		double dblWeight_Out = BasketInfo.optDouble("Weight_Out");

		String strImagUrl = BasketInfo.optString("ImagUrl");
		
		Integer intOperationMultipla =  BasketInfo.optInt("OperationMultipla", 1);
		
		if (Double.isNaN(dblWeight_In)) dblWeight_In=0;
		if (Double.isNaN(dblWeight_Out)) dblWeight_Out = 0;

		
		JSONArray ja_BtnPressed = BasketInfo.optJSONArray("BtnPressed");
		
		double dblStdProcessTime = 0;
		double dblStdMachineTime = 0;

		JSONArray ja = new JSONArray();
		String strLoad_YearWeek = "";
		Integer intLoad_Shift = 0;
		String strBtnPressed = null;
		JSONArray ja_std_time = new JSONArray();
		JSONArray ja_current = new JSONArray();

		// if more then one button pressed in one shot (functional mode) then cycle through buttons 
		for (int j=0;j<ja_BtnPressed.length();j++ ) {
			strBtnPressed = ja_BtnPressed.getString(j);
		

			// find the standard time to use for loading a new basket
			if ( strBtnPressed.equals("load") || strBtnPressed.equals("next") ){
				if (strBtnPressed.equals("next")) intOperationNr = intOperationNr + 1;
			
				// Lookup standard process time and machine time. If standard sequence (1) then look in "300_standard_sequences", else (2) look in "rework_seqences" 
				ja_std_time = JSONHelper.json_db("q",strSQL_standard_time_standard, 2, strSequenceID, intOperationNr );
	
				if (ja_std_time.length()>0 ) {
					dblStdProcessTime = ja_std_time.getJSONObject(0).optDouble("ProcessTime");
					dblStdMachineTime = ja_std_time.getJSONObject(0).optDouble("MachineTime");
					strOperationID = ja_std_time.getJSONObject(0).optString("OperationID");
					strWorkInstruction = ja_std_time.getJSONObject(0).optString("WorkInstruction");
					intOperationMultipla =  ja_std_time.getJSONObject(0).optInt("OperationMultipla", 1);
					}
			}
			
			
			// do different stuff depending on what button was pressed
			switch (strBtnPressed.toLowerCase()) {
	
			case "load":
				// read current YearWeek, Shift and Hour (updated on server with stored procedure)
				ja_current = JSONHelper.json_db("q",strSQL_Current,0);
				strLoad_YearWeek = ja_current.getJSONObject(0).optString("Current_YearWeek");
				intLoad_Shift = ja_current.getJSONObject(0).optInt("Current_Shift");
				
				if (intSequenceType==2) dblWeight_Out = dblWeight_In; 
						
				// Load the basket. Update record in "610_basket_status"			
				ja = JSONHelper.json_db("e",strSQL_status_load, 27, strImagUrl, strLineID, strJobNr, strItemID, intOperationNr, strOperationID, strWorkInstruction, intSequenceType, strSequenceID, strDefectTypeID, 1, strUserID, 
						strWorkbenchID, dblStdProcessTime, dblStdMachineTime, intGood_Pcs_In ,intGood_Pcs_In,  intBad_Pcs_In,  intBad_Pcs_In, intRejected_Pcs_In, intRejected_Pcs_In, dblWeight_In ,  dblWeight_Out , strLoad_YearWeek, intLoad_Shift, intOperationMultipla, strBasketID);
				
				JSONArray ja_init = JSONHelper.json_db("e",strSQL_load_JobNr_initiate,1,strJobNr);
				
				// if initiated then set as recieved
				Integer k = ja_init.getJSONObject(0).optInt("records_affected");
				if ( k != 0)  { JSONHelper.json_db("e",strSQL_load_JobNr_force_receive,1,strJobNr); } 
				
				break;
	
			case "start":
	
				
				ja = JSONHelper.json_db("e",strSQL_status_start, 6, strUserID, strWorkbenchID, intGood_Pcs_Out, intBad_Pcs_Out, intRejected_Pcs_Out ,strBasketID);
				break;
			
			case "start_after_pause":
				ja = JSONHelper.json_db("e",strSQL_status_start_after_pause, 6, strUserID,  strWorkbenchID, intGood_Pcs_Out, intBad_Pcs_Out, intRejected_Pcs_Out, strBasketID);
				break;
	
			case "start_after_rework":
				ja = JSONHelper.json_db("e",strSQL_status_start_after_rework, 6, strUserID, strWorkbenchID, intGood_Pcs_Out, intBad_Pcs_Out, intRejected_Pcs_Out, strBasketID);
				break;
			
			case "pause":
				ja = JSONHelper.json_db("e",strSQL_status_pause, 6, strUserID, strWorkbenchID, intGood_Pcs_Out, intBad_Pcs_Out, intRejected_Pcs_Out, strBasketID);
				break;
			
			case "rework":
				ja = JSONHelper.json_db("e",strSQL_status_rework, 6, strUserID, strWorkbenchID, intGood_Pcs_Out, intBad_Pcs_Out, intRejected_Pcs_Out, strBasketID);
				break;
				
			case "end": 
				ja = JSONHelper.json_db("e",strSQL_status_end, 6, strUserID, strWorkbenchID, intGood_Pcs_Out,  intBad_Pcs_Out, intRejected_Pcs_Out,  strBasketID);
				break;
				
			case "unload":
				//unload in "610_Baskets" and add to log "620_Basket_log"
				ja = JSONHelper.json_db("e",strSQL_status_unload, 11, strUserID,  strWorkbenchID, intGood_Pcs_In ,intGood_Pcs_Out,  intBad_Pcs_In,  intBad_Pcs_Out,  intRejected_Pcs_In, intRejected_Pcs_Out ,dblWeight_In ,  dblWeight_Out , strBasketID);
				// if basket not in status (0 records affected)		
				if (ja.getJSONObject(0).getInt("records_affected")==0 )	{  
					return Response.status(404).entity(JSONHelper.json_db("q",strSQL_ErrMsg, 1 ,"basket_status_not_right").getJSONObject(0).toString(1) ).build();
				}
				JSONHelper.json_db("e",strSQL_status_add_basketlog, 1, strBasketID);
				JSONHelper.json_db("e",strSQL_status_add_basketlog_hist, 1, strBasketID);
				JSONHelper.json_db("e",strSQL_status_clear_basket, 1, strBasketID);
				
				break;
					
			case "next":
				//unload in "610_Baskets" and add to log "620_Basket_log"
				ja = JSONHelper.json_db("e",strSQL_status_unload, 11, strUserID,  strWorkbenchID, intGood_Pcs_In ,intGood_Pcs_Out,  intBad_Pcs_In,  intBad_Pcs_Out, intRejected_Pcs_In, intRejected_Pcs_Out , dblWeight_In ,  dblWeight_Out,  strBasketID);
				// if basket not in status (0 records affected)		
				if (ja.getJSONObject(0).getInt("records_affected")==0 )	{  
					return Response.status(404).entity(JSONHelper.json_db("q",strSQL_ErrMsg, 1 ,"basket_status_not_right").getJSONObject(0).toString(1) ).build();
				}
				
				// put basket in to log and into log_hist
				JSONHelper.json_db("e",strSQL_status_add_basketlog, 1, strBasketID);
				JSONHelper.json_db("e",strSQL_status_add_basketlog_hist, 1, strBasketID);
				JSONHelper.json_db("e",strSQL_status_clear_basket, 1, strBasketID);
	
				// read current YearWeek, Shift and Hour (updated on server with stored procedure)
				ja_current = JSONHelper.json_db("q",strSQL_Current,0);
				strLoad_YearWeek = ja_current.getJSONObject(0).optString("Current_YearWeek");
				intLoad_Shift = ja_current.getJSONObject(0).optInt("Current_Shift");
				strWorkbenchID="";
				
				
				
				// Load the basket. Update record in "610_baskets"			
				ja = JSONHelper.json_db("e",strSQL_status_load, 27, strImagUrl, strLineID, strJobNr, strItemID, intOperationNr, strOperationID, strWorkInstruction, intSequenceType, strSequenceID, strDefectTypeID, 1, strUserID, 
						strWorkbenchID, dblStdProcessTime, dblStdMachineTime, intGood_Pcs_Out , intGood_Pcs_Out,  intBad_Pcs_Out,  intBad_Pcs_Out ,intRejected_Pcs_Out, intRejected_Pcs_Out,   dblWeight_Out ,  0 , strLoad_YearWeek, intLoad_Shift, intOperationMultipla, strBasketID); 
	
				break;
			}
		}
		
		
		// if basket not in status (0 records affected)		
		if (ja.getJSONObject(0).getInt("records_affected")==0 )	{  
			return Response.status(404).entity(JSONHelper.json_db("q",strSQL_ErrMsg, 1 ,"basket_status_not_right").getJSONObject(0).toString(1) ).build();
		}
			
		return  Response.ok(Basket_Helper.return_basket_info(strBasketID,1).toString(1)).build();
	}
		
	
//-------------------------------------------------------------------------------------------------------------------------------------------------------	
//	Information on jobnr and job overview (or all overview)
	
	@Path("/jobinfo/{JobNr}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_jobinfo(@PathParam("JobNr") String strJobNr) throws Exception {

		String strItemID = null;
		String strSequenceID= null;

		JSONArray ja_jobnr = new JSONArray();
		JSONArray ja_item = new JSONArray();
		JSONObject jo_out = new JSONObject();
		JSONArray ja_sequence_steps = new JSONArray();
		JSONArray ja_rework = new JSONArray();
		

		// find the item ID from the job nr. Look up in table "520_LoadPlan" 
		ja_jobnr  =  JSONHelper.json_db("q",strSQL_jobnr_get, 1, strJobNr);
		
		// Find the product information. Put into object ja_out. Look up in table "310_Product"  
		if (ja_jobnr.length()>0 ) {
			strItemID = ja_jobnr.getJSONObject(0).optString("ItemID");
			ja_item  = JSONHelper.json_db("q",strSQL_jobnr_item, 1, strItemID);
			
			// Find the sequences for the Item. Look in table 330_standard_sequences 
			if (ja_item.length()>0) {
				jo_out = ja_item.getJSONObject(0);
				jo_out.put("Jobnr", strJobNr);
				ja_sequence_steps = JSONHelper.json_db("q", strSQL_jobnr_sequences, 1, strItemID);

				// Put everything into return object
				if (ja_sequence_steps.length()>0){
					
					JSONObject jo_steps = new JSONObject();
					jo_steps.put("Steps", ja_sequence_steps);
					jo_out.put("StandardSequence",jo_steps);
					
					strSequenceID = ja_sequence_steps.getJSONObject(0).optString("SequenceID");
					jo_out.getJSONObject("StandardSequence").put("SequenceID",strSequenceID);

				}
				ja_rework  =  JSONHelper.json_db("q",strSQL_jobnr_sequences_rework, 1, strItemID);
				
				jo_out.put("ReworkSequences",ja_rework);
				
				return Response.ok(jo_out.toString(1)).build();
			}
		}

		// return error code if not found
		JSONArray Msg = JSONHelper.json_db("q",strSQL_ErrMsg, 1 ,"jobnr_not_found");		
		return Response.status(404).entity(Msg.getJSONObject(0).toString(1)).build();
		}

	//toggle job
	@Path("/job/{JobNr}/toggle_receive")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_toggle_job_received(@PathParam("JobNr") String strJobNr) throws Exception {
		
		JSONArray ja  =  JSONHelper.json_db("e",strSQL_load_JobNr_receive,1,strJobNr);
		
		Integer i = ja.getJSONObject(0).optInt("records_affected");
		if (i != 0) {
			return Response.ok(JSONHelper.json_db("q",strSQL_jobnr_get, 1, strJobNr).getJSONObject(0).toString(1)).build();
		}
		// return error code if not found
		JSONArray Msg = JSONHelper.json_db("q",strSQL_ErrMsg, 1 ,"jobnr_not_found_or_already_initiated");		
		return Response.status(404).entity(Msg.getJSONObject(0).toString(1)).build();

	}
	
	
	//display all baskets for a given job
	@Path("/baskets/{JobNr}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_baskets(@PathParam("JobNr") String strJobNr) throws Exception {
		
			String strStar = "*";
			if ( strStar.equals(strJobNr)  )   strJobNr = "%";
			return Response.ok(JSONHelper.json_db("q",strSQL_jobnr_baskets, 1, strJobNr).toString(1)).build();}

		
	@Path("/job/overview")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_overview(String strOverview) throws Exception { 
		// Parse the string to json object
		JSONObject OverviewInfo = new JSONObject(strOverview);

		// Parse json object to string variables
		String strLineID = OverviewInfo.optString("LineID");
		String strJobNr = OverviewInfo.optString("JobNr");
		String strItemID = OverviewInfo.optString("ItemID");

		
		// variable used control limitation to current week. If set to 'ShowAllForJob' then no limitation in plan
		String strLim = "x";
		String strStar = "*";
		
		// select which overview to create based on input object
		if ( strJobNr.length() ==0 || strJobNr.equals(strStar) ) {strJobNr = "%";strLim= "LimitToCurrent";};
		if ( strItemID.length() ==0 ) {strItemID = "%";};
		if ( strLineID.equals(strStar) ) {strLineID = "%";};		
	
		JSONArray ja_loadplan = JSONHelper.json_db("q",strSQL_jobnr_loadplan,4,strLim, strLineID, strJobNr, strItemID);
		
		JSONArray ja_loaded_active = JSONHelper.json_db("q",strSQL_jobnr_loaded_active,4,strLim, strLineID, strJobNr, strItemID);
		JSONArray ja_loaded_log = JSONHelper.json_db("q",strSQL_jobnr_loaded_log,4, strLim,strLineID, strJobNr, strItemID);

		JSONArray ja_operations = JSONHelper.json_db("q",strSQL_jobnr_overview, 3,strLineID, strJobNr, strItemID);
		
		JSONArray ja_operations_total = JSONHelper.json_db("q",strSQL_jobnr_overview_total, 3,strLineID, strJobNr, strItemID);
		
		JSONObject jo = new JSONObject();
		JSONArray ja_jobnr  =  JSONHelper.json_db("q",strSQL_jobnr_get, 1, strJobNr);
		if (ja_jobnr.optJSONObject(0) != null) {
			jo = ja_jobnr.optJSONObject(0);
		} 
		
		
		jo.put("load_plan",ja_loadplan.getJSONObject(0));
		jo.put("loaded_active",ja_loaded_active.getJSONObject(0));
		jo.put("loaded_log",ja_loaded_log.getJSONObject(0));
		jo.put("total",ja_operations_total.getJSONObject(0));

		
		jo.put("By_Operations", ja_operations);
		
		return Response.ok(jo.toString(1)).build();

	}

	

//-------------------------------------------------------------------------------------------------------------------------------------------------------	
//	Standard and rework sequences

	
	@Path("/sequence/defecttypes")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_sequence_defectypes() throws Exception {
		return Response.ok(
				JSONHelper.json_db("q",strSQL_sequences_defecttypes, 0).toString(1)).build();}
	

	@Path("/sequence/{SequenceID}")
	@GET
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_sequence(@PathParam("SequenceID") String strSequenceID) throws Exception {

		// current only working for rework sequences
		
		JSONObject jo_out = new JSONObject();
		
			
		// find sequence from item ID. Look up in table "rework_sequences" 
		JSONArray ja_sequence =  JSONHelper.json_db("q",strSQL_sequence, 1, strSequenceID);
		JSONArray ja_def = JSONHelper.json_db("q",  strSQL_sequence_def,1,strSequenceID);
		
		
		// Find the sequence information  
		if (ja_sequence.length()>0 && ja_def.length()>0) {
			jo_out = ja_def.getJSONObject(0);
			jo_out.put("Steps", ja_sequence);
			return Response.ok(jo_out.toString(1)).build();
		}
		
		// return error code if not found
		JSONArray Msg = JSONHelper.json_db("q",strSQL_ErrMsg, 1 ,"sequence_not_found_rework");	
		String str = Msg.getJSONObject(0).toString(1).replace("??", strSequenceID);
		return Response.status(404).entity(str).build();
		
		
	}

	

	@Path("/sequence/rework/")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response post_rework_sequence(String strInfoKey) throws Exception { 
		// current only working for rework sequences
		
		// Parse the string to json object
		JSONObject jo_InfoKey = new JSONObject(strInfoKey);
		String strItemID = jo_InfoKey.optString("ItemID");
		String strDefectTypeID = jo_InfoKey.optString("DefectTypeID");
		String strDescpription_EN  = jo_InfoKey.optString("Description_EN");
		String strDescpription_TH  = jo_InfoKey.optString("Description_TH");
		Integer intItemRelationType = 1;

		
		// if generic
		if (strItemID == null  || strItemID.length() ==0) intItemRelationType=2;
		
	
		JSONArray ja_sequence = jo_InfoKey.getJSONArray("Sequence");

		JSONArray ja_SortID = new JSONArray();
		if  (intItemRelationType== 1) {
			ja_SortID= JSONHelper.json_db("q",strSQL_Sequences_MaxSortID_specific, 2, strItemID, strDefectTypeID);
		}
		else {
			ja_SortID= JSONHelper.json_db("q",strSQL_Sequences_MaxSortID_generic, 1,  strDefectTypeID);
		} 
		
		Integer intSortID = ja_SortID.getJSONObject(0).optInt("SortID") + 1;
		String strSequenceID = "rwk-" + strItemID + "-" + strDefectTypeID + "-" + intSortID;		
		Integer intSequenceType=2;
				
		//create record in table 415_Sequences
		JSONHelper.json_db("e",strSequence_add, 8 , strSequenceID, intSequenceType, intItemRelationType, strItemID,  strDefectTypeID,strDescpription_EN, strDescpription_TH, intSortID );

		Integer intOperationNr;
		String strOperationID;
		String strWorkInstruction;
		JSONObject jo = new JSONObject() ;
		
		//Insert the steps into 330_standard_sequences
		for (int i=0;i<ja_sequence.length();i++ ) {

			jo = ja_sequence.getJSONObject(i);
			intOperationNr  = jo.optInt("OperationNr");
			strOperationID = jo.optString("OperationID");
			strWorkInstruction = jo.optString("WorkInstruction");
			
			JSONHelper.json_db("e",strSequence_steps_add, 11 ,strSequenceID, intSequenceType, strItemID, strDefectTypeID,intOperationNr,strOperationID ,strWorkInstruction, 0 , 0 , 0 ,1 );
		
		}
		
		return return_sequence(strSequenceID) ;
	}

	
	
	
	
	
	

//-------------------------------------------------------------------------------------------------------------------------------------------------------	
//	Error messages

	
	@Path("/translations")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_translations() throws Exception {
			return Response.ok(JSONHelper.json_db("q",strSQL_translations_caption,0).toString(1)).build();}

	@Path("/errormsg")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_error_msg() throws Exception {
			return Response.ok(JSONHelper.json_db("q",strSQL_translations_error_msg,0).toString(1)).build();}


	
//-------------------------------------------------------------------------------------------------------------------------------------------------------	
//	Statistics for dashboard
	
	
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
		
		ja = JSONHelper.json_db("e",strSQL_set_current_shift_hour,0) ;

		ja = JSONHelper.json_db("e",strSQL_target_delete,0) ;
		ja = JSONHelper.json_db("e",strSQL_target_by_hour,0) ;
		ja = JSONHelper.json_db("e",strSQL_target_by_shift,0) ;
		
		ja = JSONHelper.json_db("e",strSQL_set_current_elapsed,0) ;
		
		ja = JSONHelper.json_db("e",strSQL_actual_delete,0) ;
		ja = JSONHelper.json_db("e",strSQL_actual_by_hour_live_baskets,0) ;
		ja = JSONHelper.json_db("e",strSQL_actual_by_hour_unloaded_baskets,0) ;
		ja = JSONHelper.json_db("e",strSQL_actual_by_shift,0) ;

		ja = JSONHelper.json_db("e",strSQL_wip_delete,0) ;
		ja = JSONHelper.json_db("e",strSQL_wip,0) ;
	
		
		ja = JSONHelper.json_db("e",strSQL_ow_strSQL_delete,0) ;
		ja = JSONHelper.json_db("e",strSQL_ow_target_week,0) ;
		ja = JSONHelper.json_db("e",strSQL_ow_target_now,0) ;
		ja = JSONHelper.json_db("e",strSQL_ow_loaded_now,0) ;
		
		
		ja = JSONHelper.json_db("e",strSQL_ow_wip_standard,0) ;
		ja = JSONHelper.json_db("e",strSQL_ow_wip_rework,0) ;
		ja = JSONHelper.json_db("e",strSQL_ow_completed,0) ;
		
		
		ja = JSONHelper.json_db("e",strSQL_productivity_delete,0) ;
		ja = JSONHelper.json_db("e",strSQL_productivity_util_userlog,0) ;
		ja = JSONHelper.json_db("e",strSQL_productivity_util_users,0) ;
		ja = JSONHelper.json_db("e",strSQL_productivity_eff_basketlog,0) ;
		ja = JSONHelper.json_db("e",strSQL_productivity_eff_basket,0) ;
		
		
		ja = JSONHelper.json_db("e",strSQL_manning_delete,0) ;
		ja = JSONHelper.json_db("e",strSQL_manning_actual,0) ;
		ja = JSONHelper.json_db("e",strSQL_manning_plan,0) ;
		
		
		ja = JSONHelper.json_db("e", strSQL_stats_last_update, 0);
		
		
		return Response.ok("Statistics opdated" ).build();
	}
	
	

	@Path("/statistics/plan_actual")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_plan_actual(String strInfoKey) throws Exception { 

		// Parse the string to json object
		JSONObject jo_InfoKey = new JSONObject(strInfoKey);
		String strLineID = jo_InfoKey.optString("LineID");
		Integer intType = jo_InfoKey.optInt("Type");
		
		// look up current shift
		JSONArray jj = JSONHelper.json_db("q",strSQL_get_status_current,0);
		Integer intShift = jj.getJSONObject(0).optInt("Current_Shift");
		Integer intHour = jj.getJSONObject(0).optInt("Current_Hour");

		
		// Look up in the statistics. Plan and loaded.
		JSONArray ja_plan = JSONHelper.json_db("q",strSQL_get_status_target,3,strLineID, intType, intShift);
		JSONArray ja_actual = JSONHelper.json_db("q",strSQL_get_status_actual,3,strLineID, intType, intShift);
	
		Integer i_p=ja_plan.length();
		Integer i_a=ja_actual.length();
		
		
		// finds the start and the end clock
		Integer intStart = Integer.min((i_p == 0) ? 999: ja_plan.getJSONObject(0).optInt("x")                 , (i_a ==0) ? 999 : ja_actual.optJSONObject(0).getInt("x")); 
		Integer intEnd =   Integer.max((i_p == 0) ? -1 : ja_plan.getJSONObject(ja_plan.length()-1).optInt("x"), (i_a ==0) ? -1  : ja_actual.getJSONObject(ja_actual.length()-1).optInt("x"));

		
		//Calculate number of hours in shift
		Integer d = intEnd-intStart +1 ;
		if (d<0) d=d+24;
		
		
		//Build JSON Array with right x axis, but otherwise empty
		JSONArray ja = new JSONArray();
		for(int i=1; i<=d; i++){
			JSONObject jo = new JSONObject();
			int k=intStart + i -1;
			if (k>=24) k = k-24;
			jo.put("x",k);
			jo.put("index", i);
			ja.put(jo);
		}
		

		// merge the plan and actual statistics into the array
		ja = JSONHelper.json_merge_array("x",ja_plan, ja);
		ja = JSONHelper.json_merge_array("x",ja_actual, ja);

		// set replace null with zero
		Integer intEnd_Index_Actual  =-1;
		Integer intX = (intType==1) ? intShift : intHour ;
		//object and index number of the current shift or current hour  
		for (int i=0;i<ja.length();i++ ) {
			if (ja.getJSONObject(i).optInt("x") == intX ) {intEnd_Index_Actual = ja.getJSONObject(i).optInt("index");} 
		}
		
		JSONObject jo = new JSONObject();
		for (int i=0;i<ja.length();i++ ) {
			jo = ja.getJSONObject(i);
			if ( jo.opt("Pcs_Target"    ) == null) 									   { ja.getJSONObject(i).put("Pcs_Target"    , 0); }
			if ( jo.opt("Baskets_Target") == null)                                     { ja.getJSONObject(i).put("Baskets_Target", 0); }
			
			if ( jo.getInt("index") < intEnd_Index_Actual &&  jo.opt("Pcs_Actual") == null) 	   { ja.getJSONObject(i).put("Pcs_Actual", 0); }
			if ( jo.getInt("index") < intEnd_Index_Actual &&  jo.opt("Baskets_Actual") == null)  { ja.getJSONObject(i).put("Baskets_Actual", 0); }
		}
		
		return Response.ok(ja.toString(1)).build();
	}
	
	
		
	@Path("/statistics/wip/{LineID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_wip(
			@PathParam("LineID") String strLineID) 
			throws Exception { 
		return Response.ok(JSONHelper.json_db("q",strSQL_get_wip,1,strLineID).toString(1)).build();	}
	

	@Path("/statistics/productivity/{LineID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_productivity(
			@PathParam("LineID") String strLineID) 
			throws Exception {
		
		JSONArray ja_actual  = JSONHelper.json_db("q",strSQL_get_productivity,1,strLineID);
		JSONArray ja_targets = JSONHelper.json_db("q",strSQL_productivity_target,1,strLineID);
		
		JSONObject jo = new JSONObject();
		
		jo.put("actuals", ja_actual);
		jo.put("targets", ja_targets.optJSONObject(0) );
		
		return Response.ok(jo.toString(1)).build();	}
	

	@Path("/statistics/overview/{LineID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_overview(
			@PathParam("LineID") String strLineID) 
			throws Exception { 
		return Response.ok(JSONHelper.json_db("q",strSQL_get_overview,1,strLineID).toString(1)).build();	}

	
	@Path("/statistics/manning/{LineID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_manning(
			@PathParam("LineID") String strLineID) 
			throws Exception { 
		return Response.ok(JSONHelper.json_db("q",strSQL_get_manning,1,strLineID).toString(1)).build();	}


	
// ---------------------------------------------------------------------------------------------------------------------------------------------------
// Clear log tables	


	@Path("/log/delete/{days}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_manning(
			@PathParam("days") Integer intDays) 
			throws Exception { 
		
	String strS = "Deleted from Basket_log: ";
	
	strS = strS + JSONHelper.json_db("e",strSQL_clean_log_basket,1,intDays).toString();
	strS = strS +  "Deleted from User_log: ";
	strS = strS + JSONHelper.json_db("e",strSQL_clean_log_users,1,intDays).toString();
		
				
	return Response.ok(strS).build();	}

	
// ---------------------------------------------------------------------------------------------------------------------------------------------------
// off line Output

	@Path("/sequence/update_operation_multipla")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_update_operation_multipla() throws Exception {
		return Response.ok(JSONHelper.json_db("e",strSQL_update_operation_multipla_standard,0).toString(1)).build() ;}

	
	
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------------
	// Load Plan
	
	
	
	
	@Path("/loadplan/{LineID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_line_loadplan(@PathParam("LineID") String strLineID) throws Exception {
		return Response.ok(JSONHelper.json_db("q",strSQL_Line_LoadPlan,1,strLineID).toString(1)).build() ;}

	
	@Path("/loadplan/swap/{JobNr1}/{JobNr2}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_swap_loadplan(
			@PathParam("JobNr1") String strJobNr1,
			@PathParam("JobNr2") String strJobNr2
	) throws Exception {
		
		JSONArray ja = JSONHelper.json_db("q",strSQL_JobNr_swap_get, 2, strJobNr1, strJobNr2);
		
		if (ja.length() != 2)
		{
			JSONArray Msg =JSONHelper.json_db("q",strSQL_ErrMsg, 1 ,"loadplan_swap_not_possible");
			return Response.status(409).entity(Msg.getJSONObject(0).toString(1)).build();
		}
		
		
		
		JSONHelper.json_db("e",strSQL_JobNr_swap_set,7, ja.getJSONObject(0).optString("LoadDateTime"), ja.getJSONObject(0).optInt("YearWeek") ,   ja.getJSONObject(0).optInt("Shift"), ja.getJSONObject(0).optInt("Weekday"),ja.getJSONObject(0).optInt("SortID"),ja.getJSONObject(0).optDouble("PctInToWeek"), ja.getJSONObject(1).getString("JobNr"));
		JSONHelper.json_db("e",strSQL_JobNr_swap_set,7, ja.getJSONObject(1).optString("LoadDateTime"), ja.getJSONObject(1).optInt("YearWeek"),    ja.getJSONObject(1).optInt("Shift"), ja.getJSONObject(1).optInt("Weekday"),ja.getJSONObject(1).optInt("SortID"),ja.getJSONObject(1).optDouble("PctInToWeek"), ja.getJSONObject(0).getString("JobNr"));
		
		String strLineID = ja.getJSONObject(0).optString("LineID");
		
		return Response.ok(JSONHelper.json_db("q",strSQL_Line_LoadPlan,1,strLineID).toString(1)).build() ;
		}

	
// ---------------------------------------------------------------------------------------------------------------------------------------------------
// off line Output
	
	@Path("/output/realised_production/{LineID}/{Load_YearWeek}/{SequenceType}/{Shift}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_completed(
			@PathParam("LineID") String strLineID , 
			@PathParam("Load_YearWeek") String strLoad_YearWeek,
			@PathParam("SequenceType") String strSequenceType,
			@PathParam("Shift") String strShift) 
			throws Exception { 
		String strStar = "*";
		
		if ( strLineID.equals(strStar) ) {strLineID = "%";}
		if ( strLoad_YearWeek.equals(strStar) ) {strLoad_YearWeek = "%";}
		if ( strSequenceType.equals(strStar) ) {strLineID = "%";}
		if ( strShift.equals(strStar) ) {strShift = "%";}
		
		return Response.ok(JSONHelper.json_db("q",strSQL_output_realised_production ,8,strLineID, strLoad_YearWeek, strSequenceType,strShift, strLineID, strLoad_YearWeek, strSequenceType,strShift).toString(1)).build();	}
	
	

	
	@Path("/output/loaded_jobnr/{LineID}/{Load_YearWeek}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_loaded(
			@PathParam("LineID") String strLineID , 
			@PathParam("Load_YearWeek") String strLoad_YearWeek)
			throws Exception { 
		String strStar = "*";
		
		if ( strLineID.equals(strStar) ) {strLineID = "%";}
		if ( strLoad_YearWeek.equals(strStar) ) {strLoad_YearWeek = "%";}
		
		return Response.ok(JSONHelper.json_db("q",strSQL_loaded_jobnr ,4,strLineID, strLoad_YearWeek, strLineID, strLoad_YearWeek).toString(1)).build();	}

	

	
	@Path("/output/sql/{strSQL}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_sql(	
			@PathParam("strSQL") String strSQL )
			throws Exception { 
		return Response.ok(JSONHelper.json_db("q",strSQL ,0).toString(1)).build();	}
	
	
	
	
// ---------------------------------------------------------------------------------------------------------------------------------------------------
// List baskets for testing purposes	
	
	
	
	@Path("/basket_test/{BasketID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_baskettest(
		@PathParam("BasketID") String strBasketID ) 
		throws Exception { 

		try{}
		catch (Exception e){
			e.printStackTrace();
			return Response.status(200).entity("Error: ").build();
		}
		return Response.ok(JSONHelper.json_db("q",strSQL_basket_test,1,strBasketID).toString(1)).build() ;
	}



	@Path("/chart_test/{BasketID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response return_jsontest(
		@PathParam("BasketID") String strBasketID ) 
		throws Exception { 
		try{}
		catch (Exception e){
			e.printStackTrace();
			return Response.status(200).entity("Error: ").build();
		}

		
		
		return Response.ok(JSONHelper.json_db("q",strSQL_basket_test,1,strBasketID).toString(1)).build() ;
	}


	
	
	
	
	
	

//-------------------------------------------------------------------------------------------------------------------------------------------------------	
// Support functions
	
	// Help funktions

	private static class Basket_Helper {
		
		public static JSONObject return_basket_info(String strBasketID, Integer intAdd) throws Exception {
			// intAdd 	1 = Add new basket, 0 = Do not allow add of basket return error message
			
			// Find the basket information from 
			JSONArray ja = JSONHelper.json_db("q",strSQL_basket_get_basket_info,1, strBasketID);
			
			// Add basket if it does not exist. 
			if (ja.length() == 0 ) {
				
				// If not "force add" then check if BasketID is jobnr, then force "add" anyway
				if (intAdd==0 && JSONHelper.json_db("q",strSQL_jobnr_get,1, strBasketID).length()>0 ) {intAdd=1;}				
				
				if (intAdd==1) {
					// Add record in "960_basket"
					JSONHelper.json_db("e",strSQL_basket_add_basket,1, strBasketID);
					ja = JSONHelper.json_db("q",strSQL_basket_get_basket_info,1, strBasketID);
				}
				else {
					// Return with Error code
					ja = JSONHelper.json_db("q",strSQL_ErrMsg,1 ,"basket_does_not_exist");
					String str = ja.toString();
					str=str.replace("??" , strBasketID );  
					ja = new JSONArray(str);
					
					return ja.getJSONObject(0);
				}
			}
			

			// Pick the basket json object from the returned basket json array
			JSONObject ja_out = ja.getJSONObject(0);

			// find the current time and merge into output
			JSONArray ja_time = JSONHelper.json_db("q","select DateTime_Now = getdate()" , 0);
			ja_out = JSONHelper.json_merge(ja_out, ja_time.getJSONObject(0));
			
			
			// Find the user name of the user. Look in table "110_users"
			String strUserID = ja.getJSONObject(0).optString("UserID");
			if (strUserID.length()>0) {
				JSONArray ja_username = JSONHelper.json_db("q",strSQL_UserName,1, strUserID);
				// Merge username description into output object
				if (ja_username.length()>0 ) {
					ja_out = JSONHelper.json_merge(ja_out, ja_username.getJSONObject(0));}
			}
						
			// Find operation description. Look in table "320_operations"
			String strOperationID = ja.getJSONObject(0).optString("OperationID");
			if (strOperationID.length()>0) {
				JSONArray ja_operation_descr = JSONHelper.json_db("q",strSQL_operation_description, 1, strOperationID);
				// Merge operation description into output object
				if (ja_operation_descr.length()>0 ) {
					ja_out = JSONHelper.json_merge(ja_out, ja_operation_descr.getJSONObject(0));}
			}
			return ja_out;
		}
	}
	
	
	private static class Session_Helper {
	
		public static Response logout_break(String strUserID, Integer intToBeUserStatus) throws Exception {
			
			// Create log in table "120_user_log" for the old status. 
			JSONHelper.json_db("e",strSQL_session_add_userlog,1,strUserID);
			JSONHelper.json_db("e",strSQL_session_add_userlog_hist,1,strUserID);
			
			// Update user with new status. Update in table "110_Users"
			JSONHelper.json_db("e",strSQL_session_update_user, 2, intToBeUserStatus.toString(), strUserID); 
			
			// close baskets that are in status 2 =Operation Initiated, and set status to 3=Operation Paused
			JSONArray ja = JSONHelper.json_db("e",strSQL_session_pause_baskets,1 , strUserID);
			Integer i = ja.getJSONObject(0).optInt("records_affected");

			// Logout or break succeed, get message and return. Find message in table "910_ui_captions"
			String strKey =  "session_logout_break_"+ intToBeUserStatus.toString();
			JSONArray Msg = JSONHelper.json_db("q",strSQL_ErrMsg, 1 ,strKey );
			Msg.getJSONObject(0).put("UserStatus",intToBeUserStatus);
			String str = Msg.getJSONObject(0).toString(1);
			str=str.replace("??" , i.toString());  
			return Response.ok(str).build();
		}
		
	}
	
	
	private static class JSONHelper {
			
		public static JSONArray json_db(String strType, String strSQL, int intWhereClause, Object  ... objClauseParamArg ) throws Exception  {
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

		public static String SQL_build_for_update(JSONObject ja) throws JSONException {
			String strSQL = " ";
 			for(String key : JSONObject.getNames(ja))
			{
			  strSQL =  strSQL + key + " =  " + ja.get(key) + " , ";
			}
			strSQL = strSQL.substring(0,strSQL.length()-2);
				
 			return strSQL;
			
		}
		
		public static JSONObject json_merge(JSONObject Obj1, JSONObject Obj2) throws JSONException{
		
			JSONObject merged = new JSONObject(Obj1, JSONObject.getNames(Obj1));
			for(String key : JSONObject.getNames(Obj2))
			{
			  merged.put(key, Obj2.get(key));
			}
			
		return merged;
		
		}
		
		public static JSONArray json_merge_array(String key, JSONArray Source, JSONArray Target) throws JSONException{
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
	
}





	
	



/*
   Wednesday, October 5, 201616:00:59
   User: 
   Server: DESKTOP-CUOMU8T\SQLEXPRESS
   Database: pandoradatacapture
   Application: 
*/

/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
CREATE TABLE dbo.Tmp_620_basket_log_hist
	(
	BasketLogID bigint NOT NULL IDENTITY (1, 1),
	BasketID nvarchar(50) NULL,
	BasketEmptyWeight float(53) NULL,
	BasketStatus int NULL,
	JobNr nvarchar(50) NULL,
	LineID nvarchar(50) NULL,
	ItemID nvarchar(50) NULL,
	OperationNr int NULL,
	OperationID nvarchar(50) NULL,
	WorkInstruction nvarchar(255) NULL,
	SequenceType int NULL,
	DefectTypeID nvarchar(50) NULL,
	ReworkSequenceID nvarchar(50) NULL,
	UserID nvarchar(50) NULL,
	WorkbenchID nvarchar(50) NULL,
	Std_ProcessTime float(53) NULL,
	Std_MachineTime float(53) NULL,
	Good_Pcs_In int NULL,
	Good_Pcs_Out int NULL,
	Bad_Pcs_In int NULL,
	Bad_Pcs_Out int NULL,
	Rejected_Pcs_In int NULL,
	Rejected_Pcs_Out int NULL,
	Weight_In float(53) NULL,
	Weight_Out float(53) NULL,
	DateTime_Load datetime NULL,
	DateTime_Start datetime NULL,
	DateTime_End datetime NULL,
	DateTime_Unload datetime NULL,
	Pause_Time float(53) NULL,
	Rework_Time float(53) NULL,
	Pause_Count int NULL,
	Rework_Count int NULL,
	Last_Update datetime NULL,
	Load_YearWeek nvarchar(50) NULL,
	Load_Shift int NULL,
	ImagUrl nvarchar(255) NULL,
	OperationMultipla int NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.Tmp_620_basket_log_hist SET (LOCK_ESCALATION = TABLE)
GO
SET IDENTITY_INSERT dbo.Tmp_620_basket_log_hist ON
GO
IF EXISTS(SELECT * FROM dbo.[620_basket_log_hist])
	 EXEC('INSERT INTO dbo.Tmp_620_basket_log_hist (BasketLogID, BasketID, BasketEmptyWeight, BasketStatus, JobNr, LineID, ItemID, OperationNr, OperationID, WorkInstruction, SequenceType, DefectTypeID, UserID, WorkbenchID, Std_ProcessTime, Std_MachineTime, Good_Pcs_In, Good_Pcs_Out, Bad_Pcs_In, Bad_Pcs_Out, Rejected_Pcs_In, Rejected_Pcs_Out, Weight_In, Weight_Out, DateTime_Load, DateTime_Start, DateTime_End, DateTime_Unload, Pause_Time, Rework_Time, Pause_Count, Rework_Count, Last_Update, Load_YearWeek, Load_Shift, ImagUrl, OperationMultipla)
		SELECT BasketLogID, BasketID, BasketEmptyWeight, BasketStatus, JobNr, LineID, ItemID, OperationNr, OperationID, WorkInstruction, SequenceType, DefectTypeID, UserID, WorkbenchID, Std_ProcessTime, Std_MachineTime, Good_Pcs_In, Good_Pcs_Out, Bad_Pcs_In, Bad_Pcs_Out, Rejected_Pcs_In, Rejected_Pcs_Out, Weight_In, Weight_Out, DateTime_Load, DateTime_Start, DateTime_End, DateTime_Unload, Pause_Time, Rework_Time, Pause_Count, Rework_Count, Last_Update, Load_YearWeek, Load_Shift, ImagUrl, OperationMultipla FROM dbo.[620_basket_log_hist] WITH (HOLDLOCK TABLOCKX)')
GO
SET IDENTITY_INSERT dbo.Tmp_620_basket_log_hist OFF
GO
DROP TABLE dbo.[620_basket_log_hist]
GO
EXECUTE sp_rename N'dbo.Tmp_620_basket_log_hist', N'620_basket_log_hist', 'OBJECT' 
GO
ALTER TABLE dbo.[620_basket_log_hist] ADD CONSTRAINT
	PK_620_basket_log_hist PRIMARY KEY CLUSTERED 
	(
	BasketLogID
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
COMMIT
select Has_Perms_By_Name(N'dbo.[620_basket_log_hist]', 'Object', 'ALTER') as ALT_Per, Has_Perms_By_Name(N'dbo.[620_basket_log_hist]', 'Object', 'VIEW DEFINITION') as View_def_Per, Has_Perms_By_Name(N'dbo.[620_basket_log_hist]', 'Object', 'CONTROL') as Contr_Per 
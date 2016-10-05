/*
   Wednesday, October 5, 201616:02:52
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
ALTER TABLE dbo.[610_baskets]
	DROP CONSTRAINT DF_610_baskets_BasketStatus
GO
CREATE TABLE dbo.Tmp_610_baskets
	(
	BasketID nvarchar(50) NOT NULL,
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
ALTER TABLE dbo.Tmp_610_baskets SET (LOCK_ESCALATION = TABLE)
GO
ALTER TABLE dbo.Tmp_610_baskets ADD CONSTRAINT
	DF_610_baskets_BasketStatus DEFAULT ((0)) FOR BasketStatus
GO
IF EXISTS(SELECT * FROM dbo.[610_baskets])
	 EXEC('INSERT INTO dbo.Tmp_610_baskets (BasketID, BasketEmptyWeight, BasketStatus, JobNr, LineID, ItemID, OperationNr, OperationID, WorkInstruction, SequenceType, DefectTypeID, UserID, WorkbenchID, Std_ProcessTime, Std_MachineTime, Good_Pcs_In, Good_Pcs_Out, Bad_Pcs_In, Bad_Pcs_Out, Rejected_Pcs_In, Rejected_Pcs_Out, Weight_In, Weight_Out, DateTime_Load, DateTime_Start, DateTime_End, DateTime_Unload, Pause_Time, Rework_Time, Pause_Count, Rework_Count, Last_Update, Load_YearWeek, Load_Shift, ImagUrl, OperationMultipla)
		SELECT BasketID, BasketEmptyWeight, BasketStatus, JobNr, LineID, ItemID, OperationNr, OperationID, WorkInstruction, SequenceType, DefectTypeID, UserID, WorkbenchID, Std_ProcessTime, Std_MachineTime, Good_Pcs_In, Good_Pcs_Out, Bad_Pcs_In, Bad_Pcs_Out, Rejected_Pcs_In, Rejected_Pcs_Out, Weight_In, Weight_Out, DateTime_Load, DateTime_Start, DateTime_End, DateTime_Unload, Pause_Time, Rework_Time, Pause_Count, Rework_Count, Last_Update, Load_YearWeek, Load_Shift, ImagUrl, OperationMultipla FROM dbo.[610_baskets] WITH (HOLDLOCK TABLOCKX)')
GO
DROP TABLE dbo.[610_baskets]
GO
EXECUTE sp_rename N'dbo.Tmp_610_baskets', N'610_baskets', 'OBJECT' 
GO
ALTER TABLE dbo.[610_baskets] ADD CONSTRAINT
	PK_610_baskets PRIMARY KEY CLUSTERED 
	(
	BasketID
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
COMMIT
select Has_Perms_By_Name(N'dbo.[610_baskets]', 'Object', 'ALTER') as ALT_Per, Has_Perms_By_Name(N'dbo.[610_baskets]', 'Object', 'VIEW DEFINITION') as View_def_Per, Has_Perms_By_Name(N'dbo.[610_baskets]', 'Object', 'CONTROL') as Contr_Per 
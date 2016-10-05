/*
   Wednesday, October 5, 201615:59:38
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
EXECUTE sp_rename N'dbo.[420_rework_sequences].DefectTypeID', N'Tmp_ReworkSequenceID_1', 'COLUMN' 
GO
EXECUTE sp_rename N'dbo.[420_rework_sequences].Tmp_ReworkSequenceID_1', N'ReworkSequenceID', 'COLUMN' 
GO
ALTER TABLE dbo.[420_rework_sequences] SET (LOCK_ESCALATION = TABLE)
GO
COMMIT

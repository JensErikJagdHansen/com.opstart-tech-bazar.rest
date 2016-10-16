/*
   16. oktober 201608:11:44
   User: 
   Server: localhost,49170
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
ALTER TABLE dbo.[330_standard_sequences] ADD
	DefectTypeID nvarchar(50) NULL
GO
ALTER TABLE dbo.[330_standard_sequences] SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
select Has_Perms_By_Name(N'dbo.[330_standard_sequences]', 'Object', 'ALTER') as ALT_Per, Has_Perms_By_Name(N'dbo.[330_standard_sequences]', 'Object', 'VIEW DEFINITION') as View_def_Per, Has_Perms_By_Name(N'dbo.[330_standard_sequences]', 'Object', 'CONTROL') as Contr_Per 
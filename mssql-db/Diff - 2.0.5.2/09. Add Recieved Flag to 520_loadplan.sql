/*
   16. oktober 201616:32:26
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
ALTER TABLE dbo.[520_loadplan] ADD
	Received int NULL,
	Received_DateTime datetime NULL
GO
ALTER TABLE dbo.[520_loadplan] SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
select Has_Perms_By_Name(N'dbo.[520_loadplan]', 'Object', 'ALTER') as ALT_Per, Has_Perms_By_Name(N'dbo.[520_loadplan]', 'Object', 'VIEW DEFINITION') as View_def_Per, Has_Perms_By_Name(N'dbo.[520_loadplan]', 'Object', 'CONTROL') as Contr_Per 
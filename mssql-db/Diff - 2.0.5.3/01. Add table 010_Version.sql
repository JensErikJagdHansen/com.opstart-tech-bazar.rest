/*
   19. oktober 201608:07:27
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
CREATE TABLE dbo.[010_Version]
	(
	Version nvarchar(50) NULL,
	DateTime datetime NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.[010_Version] SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
select Has_Perms_By_Name(N'dbo.[010_Version]', 'Object', 'ALTER') as ALT_Per, Has_Perms_By_Name(N'dbo.[010_Version]', 'Object', 'VIEW DEFINITION') as View_def_Per, Has_Perms_By_Name(N'dbo.[010_Version]', 'Object', 'CONTROL') as Contr_Per 
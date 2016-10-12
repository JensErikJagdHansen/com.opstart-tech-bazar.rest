/*
   12. oktober 201619:50:41
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
CREATE TABLE dbo.Tmp_415_Sequences
	(
	SequenceID nvarchar(50) NULL,
	SequenceType nvarchar(50) NULL,
	ItemID nvarchar(50) NULL,
	DefectTypeID nvarchar(50) NULL,
	SequenceDescription_EN nvarchar(50) NULL,
	SequenceDescription_TH nvarchar(50) NULL,
	SortID int NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.Tmp_415_Sequences SET (LOCK_ESCALATION = TABLE)
GO
IF EXISTS(SELECT * FROM dbo.[415_Sequences])
	 EXEC('INSERT INTO dbo.Tmp_415_Sequences (SequenceID, ItemID, DefectTypeID, SequenceDescription_EN, SequenceDescription_TH, SortID)
		SELECT SequenceID, ItemID, DefectTypeID, SequenceDescription_EN, SequenceDescription_TH, SortID FROM dbo.[415_Sequences] WITH (HOLDLOCK TABLOCKX)')
GO
DROP TABLE dbo.[415_Sequences]
GO
EXECUTE sp_rename N'dbo.Tmp_415_Sequences', N'415_Sequences', 'OBJECT' 
GO
COMMIT
select Has_Perms_By_Name(N'dbo.[415_Sequences]', 'Object', 'ALTER') as ALT_Per, Has_Perms_By_Name(N'dbo.[415_Sequences]', 'Object', 'VIEW DEFINITION') as View_def_Per, Has_Perms_By_Name(N'dbo.[415_Sequences]', 'Object', 'CONTROL') as Contr_Per 
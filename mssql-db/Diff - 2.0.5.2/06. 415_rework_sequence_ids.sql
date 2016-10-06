/*
   5. oktober 201621:23:15
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
CREATE TABLE dbo.Tmp_415_rework_sequence_ids
	(
	ItemID nvarchar(50) NOT NULL,
	DefectTypeID nvarchar(50) NOT NULL,
	ReworkSequenceID nvarchar(50) NOT NULL,
	ReworkSequenceDescription_EN nvarchar(255) NULL,
	ReworkSequenceDescription_TH nvarchar(50) NULL,
	SortID int NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.Tmp_415_rework_sequence_ids SET (LOCK_ESCALATION = TABLE)
GO
IF EXISTS(SELECT * FROM dbo.[415_rework_sequence_ids])
	 EXEC('INSERT INTO dbo.Tmp_415_rework_sequence_ids (ItemID, DefectTypeID, ReworkSequenceID, ReworkSequenceDescription_EN, ReworkSequenceDescription_TH, SortID)
		SELECT ItemID, DefectTypeID, ReworkSequenceID, ReworkSequenceDescription_EN, ReworkSequenceDescription_TH, SortID FROM dbo.[415_rework_sequence_ids] WITH (HOLDLOCK TABLOCKX)')
GO
DROP TABLE dbo.[415_rework_sequence_ids]
GO
EXECUTE sp_rename N'dbo.Tmp_415_rework_sequence_ids', N'415_rework_sequence_ids', 'OBJECT' 
GO
ALTER TABLE dbo.[415_rework_sequence_ids] ADD CONSTRAINT
	PK_415_rework_sequence_ids PRIMARY KEY CLUSTERED 
	(
	ItemID,
	DefectTypeID,
	ReworkSequenceID
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
COMMIT

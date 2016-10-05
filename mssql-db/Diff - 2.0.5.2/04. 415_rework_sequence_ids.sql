/*
   Wednesday, October 5, 201616:01:39
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
CREATE TABLE dbo.[415_rework_sequence_ids]
	(
	ItemID nvarchar(50) NULL,
	DefectTypeID nvarchar(50) NULL,
	ReworkSequenceID nvarchar(50) NULL,
	ReworkSequenceDescription_EN nvarchar(255) NULL,
	ReworkSequenceDescription_TH nvarchar(50) NULL,
	SortID int NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.[415_rework_sequence_ids] SET (LOCK_ESCALATION = TABLE)
GO
COMMIT

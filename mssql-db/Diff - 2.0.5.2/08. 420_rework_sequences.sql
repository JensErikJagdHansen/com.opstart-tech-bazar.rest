/*
   12. oktober 201616:18:45
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
CREATE TABLE dbo.Tmp_420_rework_sequences
	(
	SequenceID nvarchar(50) NOT NULL,
	ItemID nvarchar(50) NOT NULL,
	DefectTypeID nvarchar(50) NOT NULL,
	OperationNr int NOT NULL,
	OperationID nvarchar(50) NULL,
	WorkInstruction nvarchar(255) NULL,
	ProcessTime float(53) NOT NULL,
	MachineTime float(53) NOT NULL,
	WeightControlFlag int NULL,
	OperationMultipla int NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.Tmp_420_rework_sequences SET (LOCK_ESCALATION = TABLE)
GO
ALTER TABLE dbo.Tmp_420_rework_sequences ADD CONSTRAINT
	DF_420_rework_sequences_SequenceID DEFAULT ((1)) FOR SequenceID
GO
IF EXISTS(SELECT * FROM dbo.[420_rework_sequences])
	 EXEC('INSERT INTO dbo.Tmp_420_rework_sequences (DefectTypeID, OperationNr, OperationID, WorkInstruction, ProcessTime, MachineTime, WeightControlFlag, OperationMultipla)
		SELECT DefectTypeID, OperationNr, OperationID, WorkInstruction, ProcessTime, MachineTime, WeightControlFlag, OperationMultipla FROM dbo.[420_rework_sequences] WITH (HOLDLOCK TABLOCKX)')
GO
DROP TABLE dbo.[420_rework_sequences]
GO
EXECUTE sp_rename N'dbo.Tmp_420_rework_sequences', N'420_rework_sequences', 'OBJECT' 
GO
ALTER TABLE dbo.[420_rework_sequences] ADD CONSTRAINT
	PK_420_rework_sequences PRIMARY KEY CLUSTERED 
	(
	SequenceID,
	ItemID,
	OperationNr
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
COMMIT
select Has_Perms_By_Name(N'dbo.[420_rework_sequences]', 'Object', 'ALTER') as ALT_Per, Has_Perms_By_Name(N'dbo.[420_rework_sequences]', 'Object', 'VIEW DEFINITION') as View_def_Per, Has_Perms_By_Name(N'dbo.[420_rework_sequences]', 'Object', 'CONTROL') as Contr_Per 
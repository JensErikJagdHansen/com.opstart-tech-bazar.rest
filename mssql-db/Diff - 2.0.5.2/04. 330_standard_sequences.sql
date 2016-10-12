/*
   12. oktober 201616:49:43
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
CREATE TABLE dbo.Tmp_330_standard_sequences
	(
	SequenceID nvarchar(50) NULL,
	ItemID nvarchar(50) NOT NULL,
	OperationNr int NOT NULL,
	OperationID nvarchar(50) NULL,
	WorkInstruction nvarchar(255) NULL,
	ProcessTime float(53) NOT NULL,
	MachineTime float(53) NOT NULL,
	WeightControlFlag int NULL,
	OperationMultipla int NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.Tmp_330_standard_sequences SET (LOCK_ESCALATION = TABLE)
GO
IF EXISTS(SELECT * FROM dbo.[330_standard_sequences])
	 EXEC('INSERT INTO dbo.Tmp_330_standard_sequences (ItemID, OperationNr, OperationID, WorkInstruction, ProcessTime, MachineTime, WeightControlFlag, OperationMultipla)
		SELECT ItemID, OperationNr, OperationID, WorkInstruction, ProcessTime, MachineTime, WeightControlFlag, OperationMultipla FROM dbo.[330_standard_sequences] WITH (HOLDLOCK TABLOCKX)')
GO
DROP TABLE dbo.[330_standard_sequences]
GO
EXECUTE sp_rename N'dbo.Tmp_330_standard_sequences', N'330_standard_sequences', 'OBJECT' 
GO
ALTER TABLE dbo.[330_standard_sequences] ADD CONSTRAINT
	PK_330_standard_seqeunces PRIMARY KEY CLUSTERED 
	(
	ItemID,
	OperationNr
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
COMMIT
select Has_Perms_By_Name(N'dbo.[330_standard_sequences]', 'Object', 'ALTER') as ALT_Per, Has_Perms_By_Name(N'dbo.[330_standard_sequences]', 'Object', 'VIEW DEFINITION') as View_def_Per, Has_Perms_By_Name(N'dbo.[330_standard_sequences]', 'Object', 'CONTROL') as Contr_Per 
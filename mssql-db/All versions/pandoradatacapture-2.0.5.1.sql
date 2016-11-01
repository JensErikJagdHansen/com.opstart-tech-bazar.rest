USE [master]
GO
/****** Object:  Database [pandoradatacapture]    Script Date: 10/5/2016 9:55:30 PM ******/
CREATE DATABASE [pandoradatacapture]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'pandoradatacapture', FILENAME = N'D:\Data\pandoradatacapture.mdf' , SIZE = 479232KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'pandoradatacapture_log', FILENAME = N'E:\Log\pandoradatacapture_log.ldf' , SIZE = 568896KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [pandoradatacapture] SET COMPATIBILITY_LEVEL = 120
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [pandoradatacapture].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [pandoradatacapture] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [pandoradatacapture] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [pandoradatacapture] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [pandoradatacapture] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [pandoradatacapture] SET ARITHABORT OFF 
GO
ALTER DATABASE [pandoradatacapture] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [pandoradatacapture] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [pandoradatacapture] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [pandoradatacapture] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [pandoradatacapture] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [pandoradatacapture] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [pandoradatacapture] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [pandoradatacapture] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [pandoradatacapture] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [pandoradatacapture] SET  DISABLE_BROKER 
GO
ALTER DATABASE [pandoradatacapture] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [pandoradatacapture] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [pandoradatacapture] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [pandoradatacapture] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [pandoradatacapture] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [pandoradatacapture] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [pandoradatacapture] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [pandoradatacapture] SET RECOVERY FULL 
GO
ALTER DATABASE [pandoradatacapture] SET  MULTI_USER 
GO
ALTER DATABASE [pandoradatacapture] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [pandoradatacapture] SET DB_CHAINING OFF 
GO
ALTER DATABASE [pandoradatacapture] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [pandoradatacapture] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
ALTER DATABASE [pandoradatacapture] SET DELAYED_DURABILITY = DISABLED 
GO
EXEC sys.sp_db_vardecimal_storage_format N'pandoradatacapture', N'ON'
GO
USE [pandoradatacapture]
GO
/****** Object:  User [global\svc-th-monitor]    Script Date: 10/5/2016 9:55:31 PM ******/
CREATE USER [global\svc-th-monitor] FOR LOGIN [GLOBAL\SVC-TH-MONITOR] WITH DEFAULT_SCHEMA=[global\svc-th-monitor]
GO
/****** Object:  User [GLOBAL\pracha.t]    Script Date: 10/5/2016 9:55:31 PM ******/
CREATE USER [GLOBAL\pracha.t] FOR LOGIN [GLOBAL\pracha.t] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  User [GLOBAL\extjens.e]    Script Date: 10/5/2016 9:55:31 PM ******/
CREATE USER [GLOBAL\extjens.e] FOR LOGIN [GLOBAL\extjens.e] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  User [flowline]    Script Date: 10/5/2016 9:55:31 PM ******/
CREATE USER [flowline] FOR LOGIN [flowline] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  User [DataCaptureWriter]    Script Date: 10/5/2016 9:55:31 PM ******/
CREATE USER [DataCaptureWriter] FOR LOGIN [DataCaptureWriter] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  User [DataCaptureReader]    Script Date: 10/5/2016 9:55:31 PM ******/
CREATE USER [DataCaptureReader] FOR LOGIN [DataCaptureReader] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_datareader] ADD MEMBER [GLOBAL\pracha.t]
GO
ALTER ROLE [db_owner] ADD MEMBER [GLOBAL\extjens.e]
GO
ALTER ROLE [db_ddladmin] ADD MEMBER [GLOBAL\extjens.e]
GO
ALTER ROLE [db_datareader] ADD MEMBER [GLOBAL\extjens.e]
GO
ALTER ROLE [db_datawriter] ADD MEMBER [GLOBAL\extjens.e]
GO
ALTER ROLE [db_owner] ADD MEMBER [flowline]
GO
ALTER ROLE [db_datareader] ADD MEMBER [flowline]
GO
ALTER ROLE [db_datawriter] ADD MEMBER [flowline]
GO
ALTER ROLE [db_datareader] ADD MEMBER [DataCaptureWriter]
GO
ALTER ROLE [db_datawriter] ADD MEMBER [DataCaptureWriter]
GO
ALTER ROLE [db_datareader] ADD MEMBER [DataCaptureReader]
GO
/****** Object:  Schema [global\svc-th-monitor]    Script Date: 10/5/2016 9:55:31 PM ******/
CREATE SCHEMA [global\svc-th-monitor]
GO
/****** Object:  Table [dbo].[001_Version]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[001_Version](
	[Version] [nvarchar](50) NULL,
	[Date] [datetime] NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[110_users]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[110_users](
	[UserID] [nvarchar](50) NOT NULL,
	[UserName] [nvarchar](50) NULL,
	[FullName] [nvarchar](255) NULL,
	[PassWord] [nvarchar](50) NULL,
	[DateTime_Start] [datetime] NULL,
	[DateTime_End] [datetime] NULL,
	[UserStatus] [int] NULL,
	[LineID] [nvarchar](50) NULL,
	[WorkBenchID] [nvarchar](50) NULL,
	[OperationID] [nvarchar](50) NULL,
	[DeviceMacAddress] [nvarchar](50) NULL,
	[UserRole] [int] NOT NULL,
	[ShowProgress] [int] NULL,
 CONSTRAINT [PK_110_Users] PRIMARY KEY CLUSTERED 
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[120_user_log]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[120_user_log](
	[UserLogID] [bigint] IDENTITY(1,1) NOT NULL,
	[UserID] [nvarchar](50) NOT NULL,
	[UserStatus] [int] NULL,
	[DateTime_Start] [datetime] NULL,
	[DateTime_End] [datetime] NULL,
	[LineID] [nvarchar](50) NULL,
	[WorkBenchID] [nvarchar](50) NULL,
	[OperationID] [nvarchar](50) NULL,
	[DeviceMacAddress] [nvarchar](50) NULL,
	[End_YearWeek] [nvarchar](50) NULL,
 CONSTRAINT [PK_120_UserStatus] PRIMARY KEY CLUSTERED 
(
	[UserLogID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[120_user_log_hist]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[120_user_log_hist](
	[UserLogID] [bigint] IDENTITY(1,1) NOT NULL,
	[UserID] [nvarchar](50) NOT NULL,
	[UserStatus] [int] NULL,
	[DateTime_Start] [datetime] NULL,
	[DateTime_End] [datetime] NULL,
	[LineID] [nvarchar](50) NULL,
	[WorkBenchID] [nvarchar](50) NULL,
	[OperationID] [nvarchar](50) NULL,
	[DeviceMacAddress] [nvarchar](50) NULL,
	[End_YearWeek] [nvarchar](50) NULL,
 CONSTRAINT [PK_120_user_log_hist] PRIMARY KEY CLUSTERED 
(
	[UserLogID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[210_lines]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[210_lines](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[LineID] [nvarchar](50) NOT NULL,
	[Location] [nvarchar](255) NULL,
	[ConWIP] [int] NULL,
	[SortID] [int] NULL,
	[SkillSetGroup] [nvarchar](50) NULL,
	[ProductivityTarget] [float] NOT NULL,
	[UtilisationTarget] [float] NULL,
	[EfficiencyTarget] [float] NULL,
	[ProgressBarOrange] [float] NULL,
	[ProgressBarRed] [float] NULL,
 CONSTRAINT [PK_210_lines_1] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[220_line_opening_hours]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[220_line_opening_hours](
	[LineID] [nvarchar](50) NOT NULL,
	[ShiftID] [nvarchar](50) NOT NULL,
	[OpeningHours] [float] NOT NULL,
 CONSTRAINT [PK_220_line_opening_hours] PRIMARY KEY CLUSTERED 
(
	[LineID] ASC,
	[ShiftID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[230_line_manning]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[230_line_manning](
	[LineID] [nvarchar](50) NOT NULL,
	[OperationID] [nvarchar](50) NOT NULL,
	[Manning] [int] NULL,
 CONSTRAINT [PK_230_line_manning] PRIMARY KEY CLUSTERED 
(
	[LineID] ASC,
	[OperationID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[310_products]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[310_products](
	[ItemID] [nvarchar](50) NOT NULL,
	[AXItemID] [nvarchar](50) NULL,
	[StdPcsInBasket] [int] NULL,
	[ImagUrl] [nvarchar](255) NULL,
	[StdPcsInBatch] [int] NULL,
 CONSTRAINT [PK_310_products] PRIMARY KEY CLUSTERED 
(
	[ItemID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[320_operations]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[320_operations](
	[OperationID] [nvarchar](50) NOT NULL,
	[OperationDescription_EN] [nvarchar](255) NULL,
	[OperationDescription_TH] [nvarchar](255) NULL,
	[SortID] [int] NULL,
 CONSTRAINT [PK_320_operations] PRIMARY KEY CLUSTERED 
(
	[OperationID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[330_standard_sequences]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[330_standard_sequences](
	[ItemID] [nvarchar](50) NOT NULL,
	[OperationNr] [int] NOT NULL,
	[OperationID] [nvarchar](50) NULL,
	[WorkInstruction] [nvarchar](255) NULL,
	[ProcessTime] [float] NOT NULL,
	[MachineTime] [float] NOT NULL,
	[WeightControlFlag] [int] NULL,
	[OperationMultipla] [int] NULL,
 CONSTRAINT [PK_330_standard_seqeunces] PRIMARY KEY CLUSTERED 
(
	[ItemID] ASC,
	[OperationNr] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[410_defect_types]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[410_defect_types](
	[DefectTypeID] [nvarchar](50) NOT NULL,
	[DefectTypeDescription_EN] [nvarchar](255) NULL,
	[DefectTypeDescription_TH] [nvarchar](255) NULL,
	[SortID] [int] NULL,
 CONSTRAINT [PK_410_defect_types] PRIMARY KEY CLUSTERED 
(
	[DefectTypeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[420_rework_sequences]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[420_rework_sequences](
	[DefectTypeID] [nvarchar](50) NOT NULL,
	[OperationNr] [int] NOT NULL,
	[OperationID] [nvarchar](50) NULL,
	[WorkInstruction] [nvarchar](255) NULL,
	[ProcessTime] [float] NOT NULL,
	[MachineTime] [float] NOT NULL,
	[WeightControlFlag] [int] NULL,
	[OperationMultipla] [int] NULL,
 CONSTRAINT [PK_420_rework_sequences] PRIMARY KEY CLUSTERED 
(
	[DefectTypeID] ASC,
	[OperationNr] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[520_loadplan]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[520_loadplan](
	[LineID] [nvarchar](50) NOT NULL,
	[YearWeek] [int] NOT NULL,
	[SortID] [int] NOT NULL,
	[Count] [int] NOT NULL,
	[ItemID] [nvarchar](50) NOT NULL,
	[JobNr] [nvarchar](50) NOT NULL,
	[Quantity] [int] NOT NULL,
	[PctInToWeek] [float] NOT NULL,
	[Weekday] [int] NULL,
	[Shift] [int] NULL,
	[LoadDateTime] [datetime] NULL,
	[SeqFamily] [nvarchar](50) NULL,
	[Lane] [int] NULL,
	[CountBaskets] [int] NULL,
	[Initiated] [int] NULL,
	[Initiated_DateTime] [datetime] NULL,
	[Completed_DateTime] [datetime] NULL,
	[ImagUrl] [nvarchar](255) NULL,
	[Priority] [int] NULL,
	[Original_YearWeek] [int] NULL,
 CONSTRAINT [PK_520_loadplan] PRIMARY KEY CLUSTERED 
(
	[LineID] ASC,
	[Count] ASC,
	[JobNr] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[610_baskets]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[610_baskets](
	[BasketID] [nvarchar](50) NOT NULL,
	[BasketEmptyWeight] [float] NULL,
	[BasketStatus] [int] NULL CONSTRAINT [DF_610_baskets_BasketStatus]  DEFAULT ((0)),
	[JobNr] [nvarchar](50) NULL,
	[LineID] [nvarchar](50) NULL,
	[ItemID] [nvarchar](50) NULL,
	[OperationNr] [int] NULL,
	[OperationID] [nvarchar](50) NULL,
	[WorkInstruction] [nvarchar](255) NULL,
	[SequenceType] [int] NULL,
	[DefectTypeID] [nvarchar](50) NULL,
	[UserID] [nvarchar](50) NULL,
	[WorkbenchID] [nvarchar](50) NULL,
	[Std_ProcessTime] [float] NULL,
	[Std_MachineTime] [float] NULL,
	[Good_Pcs_In] [int] NULL,
	[Good_Pcs_Out] [int] NULL,
	[Bad_Pcs_In] [int] NULL,
	[Bad_Pcs_Out] [int] NULL,
	[Rejected_Pcs_In] [int] NULL,
	[Rejected_Pcs_Out] [int] NULL,
	[Weight_In] [float] NULL,
	[Weight_Out] [float] NULL,
	[DateTime_Load] [datetime] NULL,
	[DateTime_Start] [datetime] NULL,
	[DateTime_End] [datetime] NULL,
	[DateTime_Unload] [datetime] NULL,
	[Pause_Time] [float] NULL,
	[Rework_Time] [float] NULL,
	[Pause_Count] [int] NULL,
	[Rework_Count] [int] NULL,
	[Last_Update] [datetime] NULL,
	[Load_YearWeek] [nvarchar](50) NULL,
	[Load_Shift] [int] NULL,
	[ImagUrl] [nvarchar](255) NULL,
	[OperationMultipla] [int] NULL,
 CONSTRAINT [PK_610_baskets] PRIMARY KEY CLUSTERED 
(
	[BasketID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[620_basket_log]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[620_basket_log](
	[BasketLogID] [bigint] IDENTITY(1,1) NOT NULL,
	[BasketID] [nvarchar](50) NULL,
	[BasketEmptyWeight] [float] NULL,
	[BasketStatus] [int] NULL,
	[JobNr] [nvarchar](50) NULL,
	[LineID] [nvarchar](50) NULL,
	[ItemID] [nvarchar](50) NULL,
	[OperationNr] [int] NULL,
	[OperationID] [nvarchar](50) NULL,
	[WorkInstruction] [nvarchar](255) NULL,
	[SequenceType] [int] NULL,
	[DefectTypeID] [nvarchar](50) NULL,
	[UserID] [nvarchar](50) NULL,
	[WorkbenchID] [nvarchar](50) NULL,
	[Std_ProcessTime] [float] NULL,
	[Std_MachineTime] [float] NULL,
	[Good_Pcs_In] [int] NULL,
	[Good_Pcs_Out] [int] NULL,
	[Bad_Pcs_In] [int] NULL,
	[Bad_Pcs_Out] [int] NULL,
	[Rejected_Pcs_In] [int] NULL,
	[Rejected_Pcs_Out] [int] NULL,
	[Weight_In] [float] NULL,
	[Weight_Out] [float] NULL,
	[DateTime_Load] [datetime] NULL,
	[DateTime_Start] [datetime] NULL,
	[DateTime_End] [datetime] NULL,
	[DateTime_Unload] [datetime] NULL,
	[Pause_Time] [float] NULL,
	[Rework_Time] [float] NULL,
	[Pause_Count] [int] NULL,
	[Rework_Count] [int] NULL,
	[Last_Update] [datetime] NULL,
	[Load_YearWeek] [nvarchar](50) NULL,
	[Load_Shift] [int] NULL,
	[ImagUrl] [nvarchar](255) NULL,
	[OperationMultipla] [int] NULL,
 CONSTRAINT [PK_620_basket_information_log] PRIMARY KEY CLUSTERED 
(
	[BasketLogID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[620_basket_log_hist]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[620_basket_log_hist](
	[BasketLogID] [bigint] IDENTITY(1,1) NOT NULL,
	[BasketID] [nvarchar](50) NULL,
	[BasketEmptyWeight] [float] NULL,
	[BasketStatus] [int] NULL,
	[JobNr] [nvarchar](50) NULL,
	[LineID] [nvarchar](50) NULL,
	[ItemID] [nvarchar](50) NULL,
	[OperationNr] [int] NULL,
	[OperationID] [nvarchar](50) NULL,
	[WorkInstruction] [nvarchar](255) NULL,
	[SequenceType] [int] NULL,
	[DefectTypeID] [nvarchar](50) NULL,
	[UserID] [nvarchar](50) NULL,
	[WorkbenchID] [nvarchar](50) NULL,
	[Std_ProcessTime] [float] NULL,
	[Std_MachineTime] [float] NULL,
	[Good_Pcs_In] [int] NULL,
	[Good_Pcs_Out] [int] NULL,
	[Bad_Pcs_In] [int] NULL,
	[Bad_Pcs_Out] [int] NULL,
	[Rejected_Pcs_In] [int] NULL,
	[Rejected_Pcs_Out] [int] NULL,
	[Weight_In] [float] NULL,
	[Weight_Out] [float] NULL,
	[DateTime_Load] [datetime] NULL,
	[DateTime_Start] [datetime] NULL,
	[DateTime_End] [datetime] NULL,
	[DateTime_Unload] [datetime] NULL,
	[Pause_Time] [float] NULL,
	[Rework_Time] [float] NULL,
	[Pause_Count] [int] NULL,
	[Rework_Count] [int] NULL,
	[Last_Update] [datetime] NULL,
	[Load_YearWeek] [nvarchar](50) NULL,
	[Load_Shift] [int] NULL,
	[ImagUrl] [nvarchar](255) NULL,
	[OperationMultipla] [int] NULL,
 CONSTRAINT [PK_620_basket_log_hist] PRIMARY KEY CLUSTERED 
(
	[BasketLogID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[810_line_stats_quantity]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[810_line_stats_quantity](
	[LineID] [nvarchar](50) NOT NULL,
	[Field] [nvarchar](50) NOT NULL,
	[Pcs] [int] NULL,
	[Baskets] [int] NULL,
 CONSTRAINT [PK_810_line_stats_quantity] PRIMARY KEY CLUSTERED 
(
	[LineID] ASC,
	[Field] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[830_line_stats_status_target]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[830_line_stats_status_target](
	[LineID] [nvarchar](50) NOT NULL,
	[Type] [int] NOT NULL,
	[Shift] [int] NOT NULL,
	[Hour] [int] NOT NULL,
	[SortID] [int] NULL,
	[Pcs] [int] NULL,
	[Baskets] [int] NULL,
 CONSTRAINT [PK_830_line_stats_plan_status] PRIMARY KEY CLUSTERED 
(
	[LineID] ASC,
	[Type] ASC,
	[Shift] ASC,
	[Hour] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[832_line_stats_status_actual]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[832_line_stats_status_actual](
	[LineID] [nvarchar](50) NOT NULL,
	[Type] [int] NOT NULL,
	[Shift] [int] NOT NULL,
	[Hour] [int] NOT NULL,
	[SortID] [int] NULL,
	[Pcs_good] [int] NULL,
	[Pcs_bad] [int] NULL,
	[Baskets] [int] NULL,
	[BasketStatus] [int] NOT NULL,
 CONSTRAINT [PK_832_line_stats_status_actual_1] PRIMARY KEY CLUSTERED 
(
	[LineID] ASC,
	[Type] ASC,
	[Shift] ASC,
	[Hour] ASC,
	[BasketStatus] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[840_line_stats_wip]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[840_line_stats_wip](
	[LineID] [nvarchar](50) NOT NULL,
	[Opr2ID] [nvarchar](50) NOT NULL,
	[BasketStatus] [int] NOT NULL,
	[Pcs] [int] NULL,
	[Baskets] [int] NULL,
 CONSTRAINT [PK_840_line_stats_wip] PRIMARY KEY CLUSTERED 
(
	[LineID] ASC,
	[Opr2ID] ASC,
	[BasketStatus] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[850_line_stats_productivity]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[850_line_stats_productivity](
	[Type] [int] NOT NULL,
	[LineID] [nvarchar](50) NOT NULL,
	[UserID] [nvarchar](50) NOT NULL,
	[OperationID] [nvarchar](50) NOT NULL,
	[AvailableTime_Week] [float] NULL,
	[AvailableTime_Lag] [float] NULL,
	[ProcessTime_Week] [float] NULL,
	[ProcessTime_Lag] [float] NULL,
	[StandardTime_Week] [float] NULL,
	[StandardTime_Lag] [float] NULL,
	[Pcs_Week] [int] NULL,
	[Pcs_Lag] [int] NULL,
 CONSTRAINT [PK_850_line_stats_productivity] PRIMARY KEY CLUSTERED 
(
	[Type] ASC,
	[LineID] ASC,
	[UserID] ASC,
	[OperationID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[860_line_stats_manning]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[860_line_stats_manning](
	[LineID] [nvarchar](50) NOT NULL,
	[Field] [nvarchar](50) NOT NULL,
	[OperationID] [nvarchar](50) NOT NULL,
	[Manning] [int] NULL,
 CONSTRAINT [PK_860_line_stats_manning] PRIMARY KEY CLUSTERED 
(
	[LineID] ASC,
	[Field] ASC,
	[OperationID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[880_line_stats_control]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[880_line_stats_control](
	[Current_YearWeek] [nvarchar](50) NOT NULL,
	[Current_Shift] [int] NULL,
	[Current_Hour] [int] NULL,
	[Statistics_lag] [int] NULL,
	[Update_Frequency] [float] NULL,
	[Update_Run_Flag] [int] NULL,
	[DeleteDelay] [int] NOT NULL CONSTRAINT [DF_880_line_stats_control_DeleteDelay]  DEFAULT ((14)),
	[Current_ElapsedHoursOfPlan] [int] NULL,
	[Max_Util_Eff] [float] NULL,
 CONSTRAINT [PK_880_line_stats_control] PRIMARY KEY CLUSTERED 
(
	[Current_YearWeek] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[885_line_stats_updates]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[885_line_stats_updates](
	[What] [nvarchar](50) NULL,
	[LastUpdate] [datetime] NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[890_line_stats_shifts]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[890_line_stats_shifts](
	[Shift] [int] NOT NULL,
	[cal_weekday_start] [int] NULL,
	[cal_weekday_end] [int] NULL,
	[start_time] [float] NULL,
	[end_time] [float] NULL,
 CONSTRAINT [PK_890_line_stats_shifts] PRIMARY KEY CLUSTERED 
(
	[Shift] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[895_line_stats_qty_seqfamily]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[895_line_stats_qty_seqfamily](
	[LineID] [nvarchar](50) NOT NULL,
	[SeqFamily] [nvarchar](50) NOT NULL,
	[Type] [int] NOT NULL,
	[Lane] [int] NULL,
	[Quantity] [int] NULL,
 CONSTRAINT [PK_895_line_stats_qty_by_seqfamily] PRIMARY KEY CLUSTERED 
(
	[LineID] ASC,
	[SeqFamily] ASC,
	[Type] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[910_ui_captions]    Script Date: 10/5/2016 9:55:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[910_ui_captions](
	[CaptionID] [nvarchar](50) NOT NULL,
	[Header_EN] [nvarchar](50) NULL,
	[Header_TH] [nvarchar](50) NULL,
	[Text_EN] [nvarchar](255) NULL,
	[Text_TH] [nvarchar](255) NULL,
	[Type] [int] NULL,
 CONSTRAINT [PK_910_UI_Captions] PRIMARY KEY CLUSTERED 
(
	[CaptionID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
USE [master]
GO
ALTER DATABASE [pandoradatacapture] SET  READ_WRITE 
GO

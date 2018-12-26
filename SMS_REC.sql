/*
Navicat SQL Server Data Transfer

Source Server         : 192.168.1.120
Source Server Version : 130000
Source Host           : 192.168.1.120:1433
Source Database       : ppms_v10
Source Schema         : dbo

Target Server Type    : SQL Server
Target Server Version : 130000
File Encoding         : 65001

Date: 2018-12-19 14:21:58
*/


-- ----------------------------
-- Table structure for SMS_REC
-- ----------------------------
DROP TABLE [dbo].[SMS_REC]
GO
CREATE TABLE [dbo].[SMS_REC] (
[gen_time] datetime NULL ,
[acc_no] varchar(12) NULL ,
[cell_number] varchar(11) NOT NULL ,
[sms_txt] varchar(512) NOT NULL ,
[send_times] int NULL ,
[first_send_time] datetime NULL ,
[last_send_time] datetime NULL ,
[flag] varchar(1) NULL ,
[id] int NOT NULL IDENTITY(1,1) ,
[start_time] varchar(5) NULL ,
[end_time] varchar(5) NULL ,
[already_send_times] int NULL 
)


GO
DBCC CHECKIDENT(N'[dbo].[SMS_REC]', RESEED, 2223)
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'SMS_REC', 
NULL, NULL)) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'?????????Sms_rec??'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_REC'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'?????????Sms_rec??'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_REC'
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'SMS_REC', 
'COLUMN', N'flag')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'0?????
   1?????'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_REC'
, @level2type = 'COLUMN', @level2name = N'flag'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'0?????
   1?????'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_REC'
, @level2type = 'COLUMN', @level2name = N'flag'
GO

-- ----------------------------
-- Indexes structure for table SMS_REC
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table SMS_REC
-- ----------------------------
ALTER TABLE [dbo].[SMS_REC] ADD PRIMARY KEY ([id])
GO

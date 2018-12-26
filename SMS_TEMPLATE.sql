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

Date: 2018-12-19 14:22:08
*/


-- ----------------------------
-- Table structure for SMS_TEMPLATE
-- ----------------------------
DROP TABLE [dbo].[SMS_TEMPLATE]
GO
CREATE TABLE [dbo].[SMS_TEMPLATE] (
[sms_type] varchar(2) NULL ,
[create_time] datetime NULL ,
[temp_cont] text NULL ,
[limit_times] int NULL ,
[send_time_flag] varchar(2) NULL ,
[start_time] varchar(6) NULL ,
[End_time] varchar(6) NULL ,
[ID] int NOT NULL IDENTITY(1,1) ,
[sms_name] varchar(128) NULL 
)


GO
DBCC CHECKIDENT(N'[dbo].[SMS_TEMPLATE]', RESEED, 53)
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'SMS_TEMPLATE', 
NULL, NULL)) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'?????????Sms_Template??'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'?????????Sms_Template??'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'SMS_TEMPLATE', 
'COLUMN', N'sms_type')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'01????????
   02??????????
   03????????
   04???????
   ????'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
, @level2type = 'COLUMN', @level2name = N'sms_type'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'01????????
   02??????????
   03????????
   04???????
   ????'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
, @level2type = 'COLUMN', @level2name = N'sms_type'
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'SMS_TEMPLATE', 
'COLUMN', N'limit_times')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'??????2'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
, @level2type = 'COLUMN', @level2name = N'limit_times'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'??????2'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
, @level2type = 'COLUMN', @level2name = N'limit_times'
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'SMS_TEMPLATE', 
'COLUMN', N'send_time_flag')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'0??δ????
   1????????????á???????????'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
, @level2type = 'COLUMN', @level2name = N'send_time_flag'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'0??δ????
   1????????????á???????????'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
, @level2type = 'COLUMN', @level2name = N'send_time_flag'
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'SMS_TEMPLATE', 
'COLUMN', N'start_time')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'?????HH24MISS'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
, @level2type = 'COLUMN', @level2name = N'start_time'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'?????HH24MISS'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
, @level2type = 'COLUMN', @level2name = N'start_time'
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'SMS_TEMPLATE', 
'COLUMN', N'End_time')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'?????HH24MISS'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
, @level2type = 'COLUMN', @level2name = N'End_time'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'?????HH24MISS'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'SMS_TEMPLATE'
, @level2type = 'COLUMN', @level2name = N'End_time'
GO

-- ----------------------------
-- Indexes structure for table SMS_TEMPLATE
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table SMS_TEMPLATE
-- ----------------------------
ALTER TABLE [dbo].[SMS_TEMPLATE] ADD PRIMARY KEY ([ID])
GO

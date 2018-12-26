USE [ppms_v10]
GO

EXEC sys.sp_dropextendedproperty @name=N'MS_DiagramPaneCount' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'BALANCE_REMOTE_REC_VIEW'
GO

EXEC sys.sp_dropextendedproperty @name=N'MS_DiagramPane1' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'BALANCE_REMOTE_REC_VIEW'
GO

/****** Object:  View [dbo].[BALANCE_REMOTE_REC_VIEW]    Script Date: 12/18/2018 4:24:56 PM ******/
DROP VIEW [dbo].[BALANCE_REMOTE_REC_VIEW]
GO

/****** Object:  View [dbo].[BALANCE_REMOTE_REC_VIEW]    Script Date: 12/18/2018 4:24:56 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE VIEW [dbo].[BALANCE_REMOTE_REC_VIEW]
AS
SELECT        dbo.A_BALANCE_REC.acc_no AS acc_no, dbo.A_BALANCE_REC.update_flag AS update_flag, dbo.A_BALANCE_REC.ctl_no AS ctl_no, dbo.REMOTE_DISCON_REC.TXN_ID AS txn_id, 
                         dbo.REMOTE_DISCON_REC.TXN_TYPE AS txn_type, dbo.REMOTE_DISCON_REC.GEN_TIME AS gen_time, dbo.REMOTE_DISCON_REC.MAC_ID AS mac_id, 
                         dbo.REMOTE_DISCON_REC.OPER_STATUS AS oper_status, dbo.REMOTE_DISCON_REC.OPER_TIME AS oper_time, dbo.REMOTE_DISCON_REC.OPER_TIMES AS oper_times, 
                         dbo.REMOTE_DISCON_REC.EXE_START_TIME AS exe_start_time, dbo.REMOTE_DISCON_REC.EXE_END_TIME AS exe_end_time, dbo.REMOTE_DISCON_REC.CTL_STATUS AS ctl_status, 
                         dbo.REMOTE_DISCON_REC.CTL_TIME AS ctl_time, dbo.REMOTE_DISCON_REC.METER_CON_STATUS AS meter_con_status, dbo.REMOTE_DISCON_REC.DEL_FLAG AS del_flag, 
                         dbo.REMOTE_DISCON_REC.METER_ID AS meter_id, dbo.REMOTE_DISCON_REC.ID AS id, 'REMOTE DISCONNECTION' AS CONTROL_TYPE
FROM            dbo.REMOTE_DISCON_REC, dbo.A_BALANCE_REC
WHERE        dbo.A_BALANCE_REC.ctl_no = dbo.REMOTE_DISCON_REC.ID 
UNION
SELECT        dbo.A_BALANCE_REC.acc_no AS acc_no, dbo.A_BALANCE_REC.update_flag AS update_flag, dbo.A_BALANCE_REC.ctl_no AS ctl_no, dbo.REMOTE_RECON_REC.TXN_ID AS txn_id, 
                         dbo.REMOTE_RECON_REC.TXN_TYPE AS txn_type, dbo.REMOTE_RECON_REC.GEN_TIME AS gen_time, dbo.REMOTE_RECON_REC.MAC_ID AS mac_id, 
                         dbo.REMOTE_RECON_REC.OPER_STATUS AS oper_status, dbo.REMOTE_RECON_REC.OPER_TIME AS oper_time, dbo.REMOTE_RECON_REC.OPER_TIMES AS oper_times, 
                         dbo.REMOTE_RECON_REC.EXE_START_TIME AS exe_start_time, dbo.REMOTE_RECON_REC.EXE_END_TIME AS exe_end_time, dbo.REMOTE_RECON_REC.CTL_STATUS AS ctl_status, 
                         dbo.REMOTE_RECON_REC.CTL_TIME AS ctl_time, dbo.REMOTE_RECON_REC.METER_CON_STATUS AS meter_con_status, dbo.REMOTE_RECON_REC.DEL_FLAG AS del_flag, 
                         dbo.REMOTE_RECON_REC.METER_ID AS meter_id, dbo.REMOTE_RECON_REC.ID AS id,  'REMOTE RECONNECTION' AS CONTROL_TYPE
FROM            dbo.REMOTE_RECON_REC, dbo.A_BALANCE_REC
WHERE        dbo.A_BALANCE_REC.ctl_no = dbo.REMOTE_RECON_REC.id 
GO

EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPane1', @value=N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[27] 4[5] 2[63] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
      Begin ColumnWidths = 9
         Width = 284
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'BALANCE_REMOTE_REC_VIEW'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPaneCount', @value=1 , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'BALANCE_REMOTE_REC_VIEW'
GO



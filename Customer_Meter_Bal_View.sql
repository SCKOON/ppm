USE [ppms_v10]
GO

EXEC sys.sp_dropextendedproperty @name=N'MS_DiagramPaneCount' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'CUSTOMER_METER_BAL_VIEW'
GO

EXEC sys.sp_dropextendedproperty @name=N'MS_DiagramPane1' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'CUSTOMER_METER_BAL_VIEW'
GO

/****** Object:  View [dbo].[CUSTOMER_METER_BAL_VIEW]    Script Date: 12/18/2018 2:22:45 PM ******/
DROP VIEW [dbo].[CUSTOMER_METER_BAL_VIEW]
GO

/****** Object:  View [dbo].[CUSTOMER_METER_BAL_VIEW]    Script Date: 12/18/2018 2:22:45 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE VIEW [dbo].[CUSTOMER_METER_BAL_VIEW]
AS
SELECT        dbo.A_CUSTOMER_INFO.acc_no, dbo.A_CUSTOMER_INFO.name, dbo.A_CUSTOMER_INFO.nric, dbo.A_CUSTOMER_INFO.installation_no, dbo.A_CUSTOMER_INFO.salutation, dbo.A_CUSTOMER_INFO.address, 
                         dbo.A_CUSTOMER_INFO.premise_type, dbo.A_CUSTOMER_INFO.block_number, dbo.A_CUSTOMER_INFO.street_name, dbo.A_CUSTOMER_INFO.unit_number, dbo.A_CUSTOMER_INFO.postal_code, 
                         dbo.A_CUSTOMER_INFO.mail_addr, dbo.A_CUSTOMER_INFO.email_addr, dbo.A_CUSTOMER_INFO.telephone_number, dbo.A_CUSTOMER_INFO.fax_number, dbo.A_CUSTOMER_INFO.mobile_number, 
                         dbo.A_CUSTOMER_INFO.tariff_code, dbo.A_CUSTOMER_INFO.gst_code, dbo.A_CUSTOMER_INFO.preset_credit, dbo.A_CUSTOMER_INFO.emergency_credit, dbo.A_CUSTOMER_INFO.emergency_state, 
                         dbo.A_CUSTOMER_INFO.low_credit_alarm, dbo.A_CUSTOMER_INFO.disconnect_alarm, dbo.A_CUSTOMER_INFO.account_status, dbo.A_CUSTOMER_INFO.open_date, 
                         dbo.A_CUSTOMER_INFO.sheduled_activation_date, dbo.A_CUSTOMER_INFO.activation_date, dbo.A_CUSTOMER_INFO.close_date, dbo.A_CUSTOMER_INFO.init_reads, dbo.A_CUSTOMER_INFO.final_reads, 
                         dbo.A_CUSTOMER_INFO.type, dbo.A_CUSTOMER_INFO.del_flag, dbo.A_CUSTOMER_INFO.arrear_pct, dbo.A_METER_INFO.meter_id, dbo.A_METER_INFO.mac_id, dbo.A_METER_INFO.meter_status, 
                         dbo.A_METER_INFO.install_date, dbo.A_METER_INFO.removal_date, dbo.A_METER_INFO.switch_status, dbo.A_ACCT_BAL.BALANCE, dbo.A_ACCT_BAL.UPDATE_TIME
FROM            dbo.A_CUSTOMER_INFO INNER JOIN
                         dbo.A_ACCT_BAL ON dbo.A_ACCT_BAL.ACC_NO = dbo.A_CUSTOMER_INFO.acc_no LEFT OUTER JOIN
                         dbo.A_METER_INFO ON dbo.A_METER_INFO.acc_no = dbo.A_CUSTOMER_INFO.acc_no
WHERE        (dbo.A_METER_INFO.meter_status = '03')
GO

EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPane1', @value=N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
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
         Begin Table = "A_CUSTOMER_INFO"
            Begin Extent = 
               Top = 6
               Left = 38
               Bottom = 136
               Right = 260
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "A_ACCT_BAL"
            Begin Extent = 
               Top = 138
               Left = 38
               Bottom = 251
               Right = 208
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "A_METER_INFO"
            Begin Extent = 
               Top = 138
               Left = 246
               Bottom = 268
               Right = 420
            End
            DisplayFlags = 280
            TopColumn = 3
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
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
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'CUSTOMER_METER_BAL_VIEW'
GO

EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPaneCount', @value=1 , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'CUSTOMER_METER_BAL_VIEW'
GO



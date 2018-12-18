ALTER VIEW dbo.FINANCE_REPORT_TO_EMA_VIEW AS SELECT
	acc_no,
	topup_amt,
	txn_status,
	(
		CASE
		WHEN txn_status IN ('07', '08') THEN
			'1'
		WHEN txn_status = '01' THEN
			'2'
		WHEN txn_status IS NULL THEN
			'4'
		END
	) txn_type,
	txn_date,
	consumption,
	data_date,
	ROW_NUMBER () OVER (
		partition BY acc_no
		ORDER BY
			data_date ASC
	) AS sequence
FROM
	(
		SELECT
			acc_no,
			topup_amt,
			txn_status,
			txn_date,
			NULL AS consumption,
			NULL AS data_date
		FROM
			SP_TOPUP_REC
		WHERE
			txn_status IN ('01', '07', '08')
		UNION ALL
			SELECT
				acc_no,
				topup_amt,
				txn_status,
				txn_date,
				NULL,
				NULL
			FROM
				CA_TOPUP_REC
			WHERE
				txn_status IN ('01', '07', '08')
			UNION ALL
				SELECT
					ACC_NO,
					NULL,
					NULL,
					NULL,
					CONSUMPTION,
					DATA_DATE
				FROM
					ST_MONTH_POWER
	) financeview
go
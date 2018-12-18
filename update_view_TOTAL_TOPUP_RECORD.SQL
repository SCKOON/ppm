alter view TOTAL_TOPUP_RECORD AS
SELECT
	d.acc_no, d.txn_date
FROM
	SP_TOPUP_REC d
WHERE
	d.txn_status IN ( '01', '07', '08')
UNION ALL
	SELECT
		e.acc_no, e.txn_date
	FROM
		CA_TOPUP_REC e
	WHERE
		e.txn_status IN ( '01', '07', '08')
go
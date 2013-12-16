

-------------------------------------------------------------------------------
--  inspektr
-------------------------------------------------------------------------------
CREATE TABLE COM_AUDIT_TRAIL (
	AUD_USER      VARCHAR(100) NOT NULL,
	AUD_CLIENT_IP VARCHAR(15)   NOT NULL,
	AUD_SERVER_IP VARCHAR(15)   NOT NULL,
	AUD_RESOURCE  VARCHAR(100) NOT NULL,
	AUD_ACTION    VARCHAR(100) NOT NULL,
	APPLIC_CD     VARCHAR(5)   NOT NULL,
	AUD_DATE      TIMESTAMP     NOT NULL
)




-------------------------------------------------------------------------------
--  audit extra
-------------------------------------------------------------------------------
CREATE TABLE AUDIT_EXTRA(
        ID BIGINT NOT NULL,
        NAME VARCHAR(200),
	VALUE VARCHAR(200),
	AUDIT_BASE_ID BIGINT,
        CONSTRAINT PK_AUDIT_EXTRA PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;


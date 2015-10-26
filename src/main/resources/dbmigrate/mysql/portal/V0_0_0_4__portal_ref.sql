

-------------------------------------------------------------------------------
--  portal ref
-------------------------------------------------------------------------------
CREATE TABLE PORTAL_REF(
        ID BIGINT AUTO_INCREMENT,
	INFO_ID BIGINT,
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_PORTAL_REF PRIMARY KEY(ID),
	CONSTRAINT FK_PORTAL_REF_INFO FOREIGN KEY(INFO_ID) REFERENCES PORTAL_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;


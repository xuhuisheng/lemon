

-------------------------------------------------------------------------------
--  portal widget
-------------------------------------------------------------------------------
CREATE TABLE PORTAL_WIDGET(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	URL VARCHAR(200),
	DATA VARCHAR(200),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_PORTAL_WIDGET PRIMARY KEY(ID)
);


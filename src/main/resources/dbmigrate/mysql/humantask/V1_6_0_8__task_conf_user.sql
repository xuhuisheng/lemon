

-------------------------------------------------------------------------------
--  task conf user
-------------------------------------------------------------------------------
CREATE TABLE TASK_CONF_USER(
	ID BIGINT NOT NULL,
	BUSINESS_KEY VARCHAR(200),
	CODE VARCHAR(200),
	VALUE VARCHAR(200),
        CONSTRAINT PK_TASK_CONF_USER PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;









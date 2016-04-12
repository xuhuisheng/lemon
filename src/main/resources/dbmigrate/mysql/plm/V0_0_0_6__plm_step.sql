

-------------------------------------------------------------------------------
--  plm step
-------------------------------------------------------------------------------
CREATE TABLE PLM_STEP(
        ID BIGINT NOT NULL,
	CODE VARCHAR(50),
	NAME VARCHAR(200),
	PRIORITY INT,
	ACTION VARCHAR(50),
	CONFIG_ID BIGINT,
	CONSTRAINT PK_PLM_STEP PRIMARY KEY(ID),
        CONSTRAINT FK_PIM_STEP_CONFIG FOREIGN KEY(CONFIG_ID) REFERENCES PLM_CONFIG(ID)
) ENGINE=INNODB CHARSET=UTF8;


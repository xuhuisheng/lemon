

-------------------------------------------------------------------------------
--  dynamic model
-------------------------------------------------------------------------------
CREATE TABLE M_DYNAMIC_MODEL(
        ID BIGINT auto_increment,
	TYPE VARCHAR(200),
	DEFINITION_ID VARCHAR(200),
	INSTANCE_ID VARCHAR(200),
	EXECUTION_ID VARCHAR(200),
	STATUS INT,
        CONSTRAINT PK_M_DYNAMIC_MODEL PRIMARY KEY(ID)
) engine=innodb;


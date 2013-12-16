

-------------------------------------------------------------------------------
--  dynamic model data
-------------------------------------------------------------------------------
CREATE TABLE M_DYNAMIC_MODEL_DATA(
        ID BIGINT auto_increment,
        LABEL VARCHAR(200),
	NAME VARCHAR(200),
	VALUE VARCHAR(200),
	MODEL_ID BIGINT,
        CONSTRAINT PK_M_DYNAMIC_MODEL_DATA PRIMARY KEY(ID),
        CONSTRAINT FK_M_DYNAMIC_MODEL FOREIGN KEY(MODEL_ID) REFERENCES M_DYNAMIC_MODEL(ID)
) engine=innodb;


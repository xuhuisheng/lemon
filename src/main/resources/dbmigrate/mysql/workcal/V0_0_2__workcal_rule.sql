

-------------------------------------------------------------------------------
--  workcal rule
-------------------------------------------------------------------------------
CREATE TABLE WORKCAL_RULE(
        ID BIGINT AUTO_INCREMENT,
	YEAR INT,
        WEEK INT,
	NAME VARCHAR(50),
	WORK_DATE TIMESTAMP,
	STATUS INT,
	TYPE_ID BIGINT,
        CONSTRAINT PK_WORKCAL_RULE PRIMARY KEY(ID),
	CONSTRAINT FK_WORKCAL_RULE_TYPE FOREIGN KEY(TYPE_ID) REFERENCES WORKCAL_TYPE(ID)
) ENGINE=INNODB CHARSET=UTF8;


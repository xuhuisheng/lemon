

-------------------------------------------------------------------------------
--  car info
-------------------------------------------------------------------------------
CREATE TABLE CAR_INFO(
        ID BIGINT auto_increment,
	NAME VARCHAR(200),
	CODE VARCHAR(200),
	STATUS INTEGER,
	WEIGHT INTEGER,
	PEOPLE INTEGER,
        CONSTRAINT PK_CAR_INFO PRIMARY KEY(ID)
) engine=innodb;


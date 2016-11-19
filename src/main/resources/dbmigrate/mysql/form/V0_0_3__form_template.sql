

-------------------------------------------------------------------------------
--  form template
-------------------------------------------------------------------------------
create table FORM_TEMPLATE(
        ID BIGINT NOT NULL,
	TYPE INT,
	NAME VARCHAR(200),
	CONTENT VARCHAR(2000),
        CONSTRAINT PK_FORM_TEMPLATE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;










-------------------------------------------------------------------------------
--  form template
-------------------------------------------------------------------------------
create table M_FORM_TEMPLATE(
	ID BIGINT auto_increment,
	NAME VARCHAR(200),
	CONTENT VARCHAR(2000),
        CONSTRAINT PK_FORM_TEMPLATE PRIMARY KEY(ID)
) engine=innodb;




-------------------------------------------------------------------------------
--  template info
-------------------------------------------------------------------------------
CREATE TABLE TEMPLATE_INFO(
        ID BIGINT auto_increment,
	NAME VARCHAR(50),
	CODE VARCHAR(50),
        CONSTRAINT PK_TEMPLATE_INFO PRIMARY KEY(ID)
) engine=innodb;


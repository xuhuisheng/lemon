

-------------------------------------------------------------------------------
--  sid
-------------------------------------------------------------------------------
CREATE TABLE ACL_SID(
        ID BIGINT auto_increment,
	NAME VARCHAR(200),
	REFERENCE VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_ACL_SID PRIMARY KEY(ID)
) engine=innodb;




-------------------------------------------------------------------------------
--  object type
-------------------------------------------------------------------------------
CREATE TABLE ACL_OBJECT_TYPE(
        ID BIGINT auto_increment,
	CODE VARCHAR(50),
        NAME VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_ACL_OBJECT_TYPE PRIMARY KEY(ID)
) engine=innodb;


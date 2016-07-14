

-------------------------------------------------------------------------------
--  disk acl
-------------------------------------------------------------------------------
CREATE TABLE DISK_ACL(
        ID BIGINT NOT NULL,
	TYPE VARCHAR(50),
	REF VARCHAR(64),

	SHARE_ID BIGINT,

        CONSTRAINT PK_DISK_ACL PRIMARY KEY(ID),
	CONSTRAINT FK_DISK_ACL_SHARE FOREIGN KEY (SHARE_ID) REFERENCES DISK_SHARE(ID)
) ENGINE=INNODB CHARSET=UTF8;




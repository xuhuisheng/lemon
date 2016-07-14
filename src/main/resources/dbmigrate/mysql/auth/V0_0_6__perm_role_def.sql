

-------------------------------------------------------------------------------
--  permission role def
-------------------------------------------------------------------------------
CREATE TABLE AUTH_PERM_ROLE_DEF(
        PERM_ID BIGINT NOT NULL,
        ROLE_DEF_ID BIGINT NOT NULL,
        CONSTRAINT PK_AUTH_PERM_ROLE_DEF PRIMARY KEY(PERM_ID,ROLE_DEF_ID),
        CONSTRAINT FK_AUTH_PERM_ROLE_DEF_PERM FOREIGN KEY(PERM_ID) REFERENCES AUTH_PERM(ID),
        CONSTRAINT FK_AUTH_PERM_ROLE_DEF_ROLE_DEF FOREIGN KEY(ROLE_DEF_ID) REFERENCES AUTH_ROLE_DEF(ID)
) ENGINE=INNODB CHARSET=UTF8;





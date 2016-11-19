

-------------------------------------------------------------------------------
--  user role
-------------------------------------------------------------------------------
CREATE TABLE AUTH_USER_ROLE(
        USER_STATUS_ID BIGINT NOT NULL,
        ROLE_ID BIGINT NOT NULL,
        CONSTRAINT PK_AUTH_USER_ROLE PRIMARY KEY(USER_STATUS_ID,ROLE_ID),
        CONSTRAINT FK_AUTH_USER_ROLE_USER FOREIGN KEY(USER_STATUS_ID) REFERENCES AUTH_USER_STATUS(ID),
        CONSTRAINT FK_AUTH_USER_ROLE_ROLE FOREIGN KEY(ROLE_ID) REFERENCES AUTH_ROLE(ID)
) ENGINE=INNODB CHARSET=UTF8;





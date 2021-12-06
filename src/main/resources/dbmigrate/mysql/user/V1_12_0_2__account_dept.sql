

-------------------------------------------------------------------------------
--  account dept
-------------------------------------------------------------------------------
CREATE TABLE ACCOUNT_DEPT(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(50),
    REF VARCHAR(100),
    SUPERIOUR VARCHAR(100),
    PRIORITY INT,
    PARENT_ID BIGINT,
    CONSTRAINT PK_ACCOUNT_DEPT PRIMARY KEY(ID),
    CONSTRAINT FK_ACCOUNT_DEPT_PARENT FOREIGN KEY(PARENT_ID) REFERENCES ACCOUNT_DEPT(ID)
) ENGINE=INNODB CHARSET=UTF8;











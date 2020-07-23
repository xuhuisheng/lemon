

-------------------------------------------------------------------------------
--  door info
-------------------------------------------------------------------------------
CREATE TABLE DOOR_INFO(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(200),
    TYPE VARCHAR(50),
    BUILDING VARCHAR(100),
    FLOOR VARCHAR(100),
    PRIORITY INT,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    CONSTRAINT PK_DOOR_INFO PRIMARY KEY(ID)
);


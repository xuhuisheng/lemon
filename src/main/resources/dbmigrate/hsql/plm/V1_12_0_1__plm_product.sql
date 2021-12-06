

-------------------------------------------------------------------------------
--  plm product
-------------------------------------------------------------------------------
CREATE TABLE PLM_PRODUCT(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(200),
    CATEGORY VARCHAR(50),
    SEVERITY VARCHAR(50),
    PRIORITY INT,
    TYPE VARCHAR(50),
    DEADLINE DATE,
    DESCRIPTION VARCHAR(200),
    TAG VARCHAR(200),
    STATUS VARCHAR(50),
    CREATE_TIME DATETIME,
    USER_ID VARCHAR(64),
    PARENT_ID BIGINT,
    ISSUE_ID BIGINT,
    VERSION_ID BIGINT,
    PROJECT_ID BIGINT,
    GROUP_NAME VARCHAR(200),
    ASSIGNEE VARCHAR(64),
    CONSTRAINT PK_PLM_PRODUCT PRIMARY KEY(ID)
);

COMMENT ON TABLE PLM_PRODUCT IS '需求';
COMMENT ON COLUMN PLM_PRODUCT.ID IS '主键';


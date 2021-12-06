

-------------------------------------------------------------------------------
--  disk rule
-------------------------------------------------------------------------------
CREATE TABLE DISK_RULE(
    ID BIGINT NOT NULL,
    CREATE_TIME TIMESTAMP,
    USER_ID VARCHAR(64),
    UPDATE_TIME TIMESTAMP,
    CONSTRAINT PK_DISK_RULE PRIMARY KEY(ID)
);

COMMENT ON TABLE DISK_RULE IS '权限规则';
COMMENT ON COLUMN DISK_RULE.ID IS 'id';
COMMENT ON COLUMN DISK_RULE.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN DISK_RULE.USER_ID IS '创建人';
COMMENT ON COLUMN DISK_RULE.UPDATE_TIME IS '更新时间';


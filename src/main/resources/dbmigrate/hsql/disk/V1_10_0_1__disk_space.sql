

-------------------------------------------------------------------------------
--  disk space
-------------------------------------------------------------------------------
CREATE TABLE DISK_SPACE(
    ID BIGINT NOT NULL,
    CATALOG VARCHAR(50),
    TYPE VARCHAR(50),
    NAME VARCHAR(200),
    DESCRIPTION VARCHAR(255),
    CREATOR VARCHAR(64),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    PRIORITY INT,
    QUOTA INT,
    CONSTRAINT PK_DISK_SPACE PRIMARY KEY(ID)
);

COMMENT ON TABLE DISK_SPACE IS '文件空间';
COMMENT ON COLUMN DISK_SPACE.ID IS '主键';
COMMENT ON COLUMN DISK_SPACE.CATALOG IS '分类';
COMMENT ON COLUMN DISK_SPACE.TYPE IS '类型';
COMMENT ON COLUMN DISK_SPACE.NAME IS '名称';
COMMENT ON COLUMN DISK_SPACE.DESCRIPTION IS '备注';
COMMENT ON COLUMN DISK_SPACE.CREATOR IS '创建人';
COMMENT ON COLUMN DISK_SPACE.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN DISK_SPACE.STATUS IS '状态';
COMMENT ON COLUMN DISK_SPACE.PRIORITY IS '优先级';
COMMENT ON COLUMN DISK_SPACE.QUOTA IS '配额';



-------------------------------------------------------------------------------
--  dict type
-------------------------------------------------------------------------------
CREATE TABLE DICT_TYPE(
        ID BIGINT NOT NULL,
        NAME VARCHAR(200),
	TYPE VARCHAR(200),
	DESCN VARCHAR(200),
        CONSTRAINT PK_DICT_TYPE PRIMARY KEY(ID)
);

COMMENT ON TABLE DICT_TYPE IS '数据字典类型';
COMMENT ON COLUMN DICT_TYPE.ID IS '唯一主键';
COMMENT ON COLUMN DICT_TYPE.TYPE IS '类型';
COMMENT ON COLUMN DICT_TYPE.NAME IS '名称';
COMMENT ON COLUMN DICT_TYPE.DESCN IS '描述';



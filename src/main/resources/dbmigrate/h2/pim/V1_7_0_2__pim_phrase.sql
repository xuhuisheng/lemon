

-------------------------------------------------------------------------------
--  pim phrase
-------------------------------------------------------------------------------
CREATE TABLE PIM_PHRASE(
        ID BIGINT NOT NULL,
	CONTENT VARCHAR(200),
	USER_ID VARCHAR(64),
        CONSTRAINT PK_PIM_PHRASE PRIMARY KEY(ID)
);

COMMENT ON TABLE PIM_PHRASE IS '常用语';
COMMENT ON COLUMN PIM_PHRASE.ID IS '主键';
COMMENT ON COLUMN PIM_PHRASE.CONTENT IS '内容';
COMMENT ON COLUMN PIM_PHRASE.USER_ID IS '用户';


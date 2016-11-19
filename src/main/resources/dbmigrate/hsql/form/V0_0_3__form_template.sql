

-------------------------------------------------------------------------------
--  form template
-------------------------------------------------------------------------------
create table FORM_TEMPLATE(
        ID BIGINT NOT NULL,
	TYPE INT,
	NAME VARCHAR(200),
	CONTENT VARCHAR(2000),
        CONSTRAINT PK_FORM_TEMPLATE PRIMARY KEY(ID)
);

COMMENT ON TABLE FORM_TEMPLATE IS '表单模板';
COMMENT ON COLUMN FORM_TEMPLATE.ID IS '主键';
COMMENT ON COLUMN FORM_TEMPLATE.TYPE IS '分类';
COMMENT ON COLUMN FORM_TEMPLATE.NAME IS '名称';
COMMENT ON COLUMN FORM_TEMPLATE.CONTENT IS '内容';


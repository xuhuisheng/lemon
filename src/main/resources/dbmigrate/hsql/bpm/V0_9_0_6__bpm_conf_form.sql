
CREATE TABLE BPM_CONF_FORM(
	ID BIGINT NOT NULL,
	VALUE VARCHAR(200),
	TYPE INT,
	ORIGIN_VALUE VARCHAR(200),
	ORIGIN_TYPE INT,
	STATUS INT,
	NODE_ID BIGINT,
        CONSTRAINT PK_BPM_CONF_FORM PRIMARY KEY(ID),
        CONSTRAINT FK_BPM_CONF_FORM_NODE FOREIGN KEY(NODE_ID) REFERENCES BPM_CONF_NODE(ID)
);

COMMENT ON TABLE BPM_CONF_FORM IS '配置表单';
COMMENT ON COLUMN BPM_CONF_FORM.ID IS '主键';
COMMENT ON COLUMN BPM_CONF_FORM.VALUE IS '值';
COMMENT ON COLUMN BPM_CONF_FORM.TYPE IS '分类';
COMMENT ON COLUMN BPM_CONF_FORM.ORIGIN_VALUE IS '原始值';
COMMENT ON COLUMN BPM_CONF_FORM.ORIGIN_TYPE IS '原始类型';
COMMENT ON COLUMN BPM_CONF_FORM.STATUS IS '状态';
COMMENT ON COLUMN BPM_CONF_FORM.NODE_ID IS '外键，配置节点';

